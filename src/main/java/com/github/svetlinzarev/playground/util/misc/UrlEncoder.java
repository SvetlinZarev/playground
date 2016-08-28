package com.github.svetlinzarev.playground.util.misc;/*
 * (C) Copyright 2016 Svetlin Zarev.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     ...
 */

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.BitSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 */
public final class UrlEncoder {
    /**
     * This constant determines how many characters
     * the encoder can process at once
     */
    private static final int CHAR_BUFFER_SIZE = 8;

    /**
     * On one hand,the output buffer should be large
     * enough to avoid most overflow conditions when
     * possible. On the other hand too large buffer
     * is a waste of space
     */
    private static final int BYTE_BUFFER_SIZE = CHAR_BUFFER_SIZE * 2;

    /**
     * How many encoding suites should be cached
     */
    private static final int CACHE_SIZE = 64;

    /**
     * Do not cache StringBuilders with larger capacity
     */
    private static final int MAX_STRING_BUILDER_CAPACITY = 1024;

    /**
     * The initial capacity of the newly allocated StringBuilders
     */
    private static final int INITIAL_STRING_BUILDER_CAPACITY = 64;

    /**
     * Mask for extracting the lower four bits of a byte
     */
    private static final int MASK_LOW = 0x0f;

    /**
     * Mask for extracting the higher four bits of a byte
     */
    private static final int MASK_HIGH = 0xf0;

    /**
     * Each byte should be prefixed with the percent sign
     */
    private static final char PERCENT = '%';

    /**
     * The hexadecimal alphabet
     */
    private static final char[] HEXADECIMAL = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * This class is to hold together all objects
     * needed for an encoding operation, in order
     * to use a single pool instead of several
     */
    private static class PoolEntry {
        CharsetEncoder charsetEncoder;
        CharBuffer charBuffer;
        ByteBuffer byteBuffer;
        StringBuilder stringBuilder;
    }

    /**
     * Efficiently remember which characters should not be encoded
     */
    private final BitSet safeCharacters = new BitSet(256);

    /**
     * Cache the already created encoders and buffers
     * in order to avoid excessive memory allocation
     */
    private final BlockingQueue<PoolEntry> pool;

    /**
     * Create a new encoder instance with the
     * ASCII alphanumeric characters added to the
     * safe characters list.
     */
    public UrlEncoder() {
        pool = new ArrayBlockingQueue<PoolEntry>(CACHE_SIZE);

        for (char i = 'a'; i <= 'z'; i++) {
            addSafeCharacter(i);
        }
        for (char i = 'A'; i <= 'Z'; i++) {
            addSafeCharacter(i);
        }
        for (char i = '0'; i <= '9'; i++) {
            addSafeCharacter(i);
        }
    }

    /**
     * Add a character to the safe list. Those characters
     * will not be encoded
     *
     * @param c The character to add
     */
    public void addSafeCharacter(char c) {
        safeCharacters.set(c);
    }

    public String encode(String string, Charset charset) {
        final PoolEntry poolEntry = retrievePoolEntryForCharset(charset);
        final CharsetEncoder encoder = poolEntry.charsetEncoder;
        final CharBuffer inputBuffer = poolEntry.charBuffer;
        final ByteBuffer outputBuffer = poolEntry.byteBuffer;
        final StringBuilder result = poolEntry.stringBuilder;

        for (int nextCharacterIndex = 0; nextCharacterIndex < string.length(); ) {
            final char character = string.charAt(nextCharacterIndex);
            if (safeCharacters.get(character)) {
                result.append(character);
                nextCharacterIndex++;
            } else {
                /*
                 * Recycle the char buffer, because the last fillInputBuffer()
                 * call may have flipped an empty buffer, hence making the
                 * position and limit equal to 0. No need to recycle the
                 * byte buffer, as flushOutputBuffer() always recycles it
                 */
                recycle(inputBuffer);

                int charsRead;
                while ((charsRead = fillInputBuffer(string, inputBuffer, nextCharacterIndex)) != 0) {
                    nextCharacterIndex += charsRead;

                    CoderResult encodingResult;
                    do {
                        encodingResult = encoder.encode(inputBuffer, outputBuffer, isEndOfInput(string, nextCharacterIndex));
                        requireCorrectEncodingResult(encodingResult, string, charsRead, nextCharacterIndex);
                        flushOutputBuffer(outputBuffer, result);
                    } while (encodingResult.isOverflow());
                    prepareInputBuffer(inputBuffer);
                }

                //Make sure that everything has been encoded and written to the output buffer
                CoderResult flushResult;
                do {
                    flushResult = encoder.flush(outputBuffer);
                    requireCorrectEncodingResult(flushResult, string, charsRead, nextCharacterIndex);
                    flushOutputBuffer(outputBuffer, result);
                } while (flushResult.isOverflow());

                //Prepare the encoder for the next set of encode() operations if any
                encoder.reset();
            }
        }

        final String encodedString = result.toString();
        returnToPool(poolEntry);
        return encodedString;
    }

    /**
     * Fill in the character buffer with the characters to be encoded.
     * This method flips the char buffer and prepares it for reading.
     *
     * @param source   The original string that is to be encoded
     * @param dest     The CharBuffer to fill with unsafe characters
     * @param readFrom The index of the first unsafe character
     * @return The number of read consecutive, unsafe characters.
     */
    private int fillInputBuffer(String source, CharBuffer dest, int readFrom) {
        int charsRead = 0;
        if (source.length() > readFrom) {
            for (int i = readFrom; dest.hasRemaining() && i < source.length(); i++) {
                final char c = source.charAt(i);
                if (safeCharacters.get(c)) {
                    break;
                }

                dest.put(c);
                charsRead++;
            }
        }

        dest.flip();
        return charsRead;
    }

    /**
     * Peek one character ahead to check if there is
     * more input waiting to be encoded
     *
     * @param string    The string argument passed to teh encode() method
     * @param nextIndex The index of the first unprocessed character
     * @return true if there is an unsafe character at nextIndex
     */
    private boolean isEndOfInput(String string, int nextIndex) {
        if (nextIndex >= string.length()) {
            return true;
        }

        return safeCharacters.get(string.charAt(nextIndex));
    }

    /**
     * Prepares the buffer for the next encoding operation.
     * <p>
     * If the buffer has any remaining unprocessed characters
     * they must be preserved for the next encoding operation.
     *
     * @param charBuffer The buffer to prepare
     */
    private void prepareInputBuffer(CharBuffer charBuffer) {
        if (charBuffer.hasRemaining()) {
            charBuffer.compact();
        } else {
            recycle(charBuffer);
        }
    }

    /**
     * Write the content of the ByteBuffer to
     * the StringBuilder as percent encoded
     * hexadecimal string representation.
     * <p>
     * This method flips the ByteBuffer to prepares it for reading
     * and recycles it in order to prepare it for writing
     *
     * @param source The buffer containing the data to be written
     * @param dest   The buffer that will store the result of the encoding operation
     */
    private void flushOutputBuffer(ByteBuffer source, StringBuilder dest) {
        source.flip();
        while (source.hasRemaining()) {
            final byte toEncode = source.get();
            final int low = toEncode & MASK_LOW;
            final int high = (toEncode & MASK_HIGH) >> 4;
            dest.append(PERCENT);
            dest.append(HEXADECIMAL[high]);
            dest.append(HEXADECIMAL[low]);
        }
        recycle(source);
    }

    private void requireCorrectEncodingResult(CoderResult result, String source, int charsRead, int nextCharIndex) {
        if (!result.isUnderflow() && !result.isOverflow()) {
            final String errorDetails = "\tSource: '" + source + "'" +
                    "\tNext character index: '" + nextCharIndex + "'" +
                    "\tChars read: '" + charsRead + "'" +
                    "\tCoder result: '" + result + "'";

            if (result.isUnmappable()) {
                throw new IllegalArgumentException("Non mappable input. " + errorDetails);
            } else if (result.isMalformed()) {
                throw new IllegalArgumentException("Malformed input:." + errorDetails);
            } else {
                throw new IllegalStateException("Unknown coder state. " + errorDetails);
            }
        }
    }

    /**
     * Recycle the buffer. Set the limit to the capacity,
     * discard the mark and set the position to zero
     *
     * @param buffer The buffer to be recycled
     * @return The given buffer
     */
    private <T extends Buffer> T recycle(T buffer) {
        buffer.rewind();
        buffer.limit(buffer.capacity());
        return buffer;
    }

    /**
     * Either take from the pool or create a new pool entry.
     *
     * @param charset The charset of teh encoder
     * @return The retrieved PoolEntry
     */
    private PoolEntry retrievePoolEntryForCharset(Charset charset) {
        PoolEntry poolEntry = pool.poll();
        if (null == poolEntry) {
            poolEntry = createPoolEntryForCharset(charset);
        } else {
            poolEntry = recyclePoolEntryForCharset(poolEntry, charset);
        }

        /**
         * Preserver the behaviour of the old URLEncoder
         */
        poolEntry.charsetEncoder
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);
        return poolEntry;
    }

    private PoolEntry createPoolEntryForCharset(Charset charset) {
        PoolEntry poolEntry = new PoolEntry();
        poolEntry.charsetEncoder = charset.newEncoder();
        poolEntry.charBuffer = CharBuffer.allocate(CHAR_BUFFER_SIZE);
        poolEntry.byteBuffer = ByteBuffer.allocate(BYTE_BUFFER_SIZE);
        poolEntry.stringBuilder = new StringBuilder(INITIAL_STRING_BUILDER_CAPACITY);
        return poolEntry;
    }

    private PoolEntry recyclePoolEntryForCharset(PoolEntry poolEntry, Charset charset) {
        if (!poolEntry.charsetEncoder.charset().equals(charset)) {
            poolEntry.charsetEncoder = charset.newEncoder();
        } else {
            poolEntry.charsetEncoder.reset();
        }

        if (null == poolEntry.stringBuilder) {
            poolEntry.stringBuilder = new StringBuilder(INITIAL_STRING_BUILDER_CAPACITY);
        } else {
            poolEntry.stringBuilder.setLength(0);
        }

        recycle(poolEntry.charBuffer);
        recycle(poolEntry.byteBuffer);

        return poolEntry;
    }

    private void returnToPool(PoolEntry poolEntry) {
        if (poolEntry.stringBuilder.capacity() > MAX_STRING_BUILDER_CAPACITY) {
            poolEntry.stringBuilder = null;
        }
        pool.offer(poolEntry);
    }


    public static void main(String[] args) {
        UrlEncoder encoder = new UrlEncoder();
        System.out.println(encoder.encode("Can you encode me ? Можеш ли да ме кодираш ?", StandardCharsets.UTF_8));
    }
}
