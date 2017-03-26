package com.github.svetlinzarev.playground.util.misc;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.BitSet;

public class UrlEncoder {
    private static final class DefaultEncoderPool extends UrlEncoder {
        private final UrlEncoder[] encoders;

        public DefaultEncoderPool(int poolSize) {
            super(null);
            encoders = new UrlEncoder[poolSize];
            for (int i = 0; i < encoders.length; i++) {
                encoders[i] = newDefaultInstance();
            }
        }

        private static UrlEncoder newDefaultInstance() {
            final UrlEncoder encoder = new UrlEncoder();
            encoder.addSafeCharacter('~');
            encoder.addSafeCharacter('-');
            encoder.addSafeCharacter('_');
            encoder.addSafeCharacter('.');
            encoder.addSafeCharacter('*');
            encoder.addSafeCharacter('/');
            return encoder;
        }

        @Override
        public void addSafeCharacter(char c) {
            throw new UnsupportedOperationException("Modification of the default encoder is not allowed");
        }

        @Override
        public String encode(String string, String encoding) {
            final long threadId = Thread.currentThread().getId();
            final int index = (int) (threadId % encoders.length);
            return encoders[index].encode(string, encoding);
        }
    }

    /**
     * This constant determines how many unsafe characters
     * the encoder can process at once
     */
    private static final int CHAR_BUFFER_SIZE = 16;

    /**
     * The output buffer should be large enough to avoid
     * most overflow conditions when possible. Yet, too
     * large buffer is a waste of space
     */
    private static final int BYTE_BUFFER_SIZE = CHAR_BUFFER_SIZE * 4;

    /**
     * The initial capacity of the newly allocated StringBuilders.
     */
    private static final int INITIAL_STRING_BUILDER_CAPACITY = 32;

    /**
     * Do not cache StringBuilders with larger capacity because it's a
     * waste of heap space.
     */
    private static final int MAX_CACHEABLE_STRING_BUILDER_CAPACITY = 256;

    /**
     * Upper limit on how many default encoder instances to pre-create and cache.
     */
    private static final int MAX_ENCODER_POOL_SIZE = 16;

    /**
     * Mask for extracting the lower four bits of a byte
     */
    private static final int MASK_LOW = 0x0f;

    /**
     * Mask for extracting the higher four bits of a byte
     */
    private static final int MASK_HIGH = 0xf0;

    /**
     * Each encoded byte must be prefixed with the percent sign
     */
    private static final char PERCENT = '%';

    /**
     * The hexadecimal alphabet
     */
    private static final char[] HEXADECIMAL = {
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * Default instance
     */
    public static final UrlEncoder DEFAULT;

    static {
        final int poolSize = Math.min(Runtime.getRuntime().availableProcessors() + 1, MAX_ENCODER_POOL_SIZE);
        DEFAULT = new DefaultEncoderPool(poolSize);
    }

    private final BitSet safeCharacters;
    private final ByteBuffer byteBuffer;
    private final CharBuffer charBuffer;
    private final StringBuilder encoded;

    private CharsetEncoder charsetEncoder;
    private boolean endOfUnsafeCharactersSequence;
    private int currentPosition;

    public UrlEncoder() {
        safeCharacters = new BitSet(256);
        addAsciiAlphanumericSymbols();

        byteBuffer = ByteBuffer.allocate(BYTE_BUFFER_SIZE);
        charBuffer = CharBuffer.allocate(CHAR_BUFFER_SIZE);

        assert INITIAL_STRING_BUILDER_CAPACITY <= MAX_CACHEABLE_STRING_BUILDER_CAPACITY;
        encoded = new StringBuilder(INITIAL_STRING_BUILDER_CAPACITY);
    }

    /**
     * Should be used only by the <code>DefaultEncoderPool</code>
     */
    private UrlEncoder(Void notUsed) {
        this.byteBuffer = null;
        this.charBuffer = null;
        this.safeCharacters = null;
        this.encoded = null;
    }

    private void addAsciiAlphanumericSymbols() {
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
     * Add character to the set of characters that must not be encoded.
     *
     * @param c the character to add
     */
    public synchronized void addSafeCharacter(char c) {
        safeCharacters.set(c);
    }

    public synchronized String encode(String string, String encoding) {
        resetInternalState();
        initializeCharsetEncoderFor(encoding);

        while (currentPosition < string.length()) {
            final char character = string.charAt(currentPosition);
            if (safeCharacters.get(character)) {
                encodeSafeCharacter(character);
            } else {
                encodeSequenceOfUnsafeCharacters(string);
            }
        }

        return getEncodedString();
    }

    private void resetInternalState() {
        currentPosition = 0;
        encoded.setLength(0);
        recycle(charBuffer);
    }

    private void initializeCharsetEncoderFor(String encoding) {
        if (null == charsetEncoder || !charsetEncoder.charset().name().equals(encoding)) {
            final Charset charset = Charset.forName(encoding);
            charsetEncoder = charset.newEncoder();

            /*
             * Preserve the behaviour of the old URLEncoder
             */
            charsetEncoder.onMalformedInput(CodingErrorAction.REPLACE);
            charsetEncoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        }
    }

    private void encodeSafeCharacter(char character) {
        encoded.append(character);
        currentPosition += 1;
    }

    /**
     * Encode a sequence of unsafe characters starting at
     * index value of <code>currentPosition</code>
     *
     * @param string The string to encode
     */
    private void encodeSequenceOfUnsafeCharacters(String string) {
        charsetEncoder.reset();
        for (int charsRead; (charsRead = readChunkOfUnsafeCharactersFrom(string)) > 0; ) {
            charBuffer.flip();

            CoderResult encodingResult;
            do {
                recycle(byteBuffer);

                encodingResult = charsetEncoder.encode(charBuffer, byteBuffer, endOfUnsafeCharactersSequence);
                validateEncodingResult(encodingResult, string, charsRead);

                byteBuffer.flip();
                appendToEncoded(byteBuffer);
            } while (encodingResult.isOverflow());

            /*
             * If the buffer has any remaining unprocessed characters
             * they must be preserved for the next encoding operation.
             */
            charBuffer.compact();
        }

        /*
         * All characters from the source buffer must have been processed
         */
        assert charBuffer.position() == 0;

        flushEncoder();
    }

    /**
     * Read from the source string starting from the current position
     * until either the buffer is filled, end of input or a safe
     * character is reached
     *
     * @param source The string to read from
     * @return number of characters read
     */
    private int readChunkOfUnsafeCharactersFrom(String source) {
        endOfUnsafeCharactersSequence = true;

        final int initialPosition = currentPosition;
        while (currentPosition < source.length()) {
            final char character = source.charAt(currentPosition);
            if (safeCharacters.get(character)) {
                break;
            }

            if (!charBuffer.hasRemaining()) {
                endOfUnsafeCharactersSequence = false;
                break;
            }

            charBuffer.put(character);
            currentPosition += 1;
        }

        return currentPosition - initialPosition;
    }

    /**
     * Prepare debug information in case the encoding failed.
     * Should never happen.
     *
     * @param result    The result of the encoding operation
     * @param source    The string with the original text
     * @param charsRead How many new characters were inside the input buffer
     */
    private void validateEncodingResult(CoderResult result, String source, int charsRead) {
        if (result.isError()) {
            final String errorDetails = "\tSource: '" + source + "'" +
              "\tNext character index: '" + currentPosition + "'" +
              "\tChars read: '" + charsRead + "'" +
              "\tEnd of input: '" + endOfUnsafeCharactersSequence + "' " +
              "\tCoder result: '" + result + "'";

            if (result.isUnmappable()) {
                throw new IllegalArgumentException("Non mappable input: " + errorDetails);
            } else if (result.isMalformed()) {
                throw new IllegalArgumentException("Malformed input: " + errorDetails);
            } else {
                throw new IllegalStateException("Unknown coder state: " + errorDetails);
            }
        }
    }

    /**
     * Make sure that everything has been encoded and written to the output buffer
     */
    private void flushEncoder() {
        CoderResult flushResult;
        do {
            recycle(byteBuffer);
            /*
             * According to the javadoc, flush cannot return an error,
             * so no need to call validateEncodingResult()
             */
            flushResult = charsetEncoder.flush(byteBuffer);
            byteBuffer.flip();
            appendToEncoded(byteBuffer);
        } while (flushResult.isOverflow());
    }

    /**
     * Append the encoded chunk to the result.
     * This method does not flip/recycle the buffer
     *
     * @param dataToAppend Buffer containing the encoded data
     */
    private void appendToEncoded(ByteBuffer dataToAppend) {
        while (dataToAppend.hasRemaining()) {
            encoded.append(PERCENT);
            final byte toEncode = dataToAppend.get();

            final int high = (toEncode & MASK_HIGH) >> 4;
            encoded.append(HEXADECIMAL[high]);

            final int low = toEncode & MASK_LOW;
            encoded.append(HEXADECIMAL[low]);
        }
    }

    /**
     * Recycle the provided buffer - set the limit to the
     * capacity and the position to zero.
     *
     * @param buffer The buffer to recycle
     */
    private void recycle(Buffer buffer) {
        buffer.limit(buffer.capacity());
        buffer.rewind();
    }

    /**
     * Convert the result of the encoding operation to java.lang.String,
     * and downsize the intermediate (cached) StringBuilder if necessary
     */
    private String getEncodedString() {
        final String encodedAsString = encoded.toString();

        if (encoded.capacity() > MAX_CACHEABLE_STRING_BUILDER_CAPACITY) {
            encoded.setLength(INITIAL_STRING_BUILDER_CAPACITY);
            encoded.trimToSize();
        }

        return encodedAsString;
    }
}

