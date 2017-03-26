package com.github.svetlinzarev.playground.util.misc;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class UrlEncoderTest {
    private UrlEncoder encoder;

    @Before
    public void beforeEach() throws Exception {
        encoder = new UrlEncoder();
    }

    @Test
    public void testUtf8Cyrillic() throws Exception {
        final String alphabet = "абвгдежзийклпмнопрстуфхцчшщъьюяэ";
        final String expected = "%D0%B0%D0%B1%D0%B2%D0%B3%D0%B4%D0%B5%D0%B6%D0%B7%D0%B8%D0%B9%D0%BA%D0%BB%D0" +
          "%BF%D0%BC%D0%BD%D0%BE%D0%BF%D1%80%D1%81%D1%82%D1%83%D1%84%D1%85%D1%86%D1%87%D1%88%D1%89%D1%8A" +
          "%D1%8C%D1%8E%D1%8F%D1%8D";
        final String actual = encoder.encode(alphabet, "UTF-8");
        assertEquals(alphabet, expected, actual);
    }

    @Test
    public void testUtf8SpecialCharacters() throws Exception {
        final String alphabet = "<>~!@#$%^&*()+=/{}[]:;'\"\\,| ._";
        final String expected = "%3C%3E%7E%21%40%23%24%25%5E%26%2A%28%29%2B%3D%2F%7B%7D%5B%5D%3A%3B%27%22%5C%2C%7C%20%2E%5F";
        final String actual = encoder.encode(alphabet, "UTF-8");
        assertEquals(alphabet, expected, actual);
    }

    @Test
    public void testUtf8Arabic() throws Exception {
        final String alphabet = "شغظذخثتسرقضفعصنملكيطحزوهدجبا";
        final String expected = "%D8%B4%D8%BA%D8%B8%D8%B0%D8%AE%D8%AB%D8%AA%D8%B3%D8%B1%D9%82%D8%B6%D9%81%D8%B9%D8%B5" +
          "%D9%86%D9%85%D9%84%D9%83%D9%8A%D8%B7%D8%AD%D8%B2%D9%88%D9%87%D8%AF%D8%AC%D8%A8%D8%A7";
        final String actual = encoder.encode(alphabet, "UTF-8");
        assertEquals(alphabet, expected, actual);
    }

    @Test
    public void testUtf8ExoticCharacters_1() throws Exception {
        final String characters = "ąćęłńśóżź";
        final String expected = "%C4%85%C4%87%C4%99%C5%82%C5%84%C5%9B%C3%B3%C5%BC%C5%BA";
        final String actual = encoder.encode(characters, "UTF-8");
        assertEquals(characters, expected, actual);
    }

    @Test
    public void testUtf8ExoticCharacters_2() throws Exception {
        final String characters = "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃ";
        final String expected = "%EF%BD%B1%EF%BD%B2%EF%BD%B3%EF%BD%B4%EF%BD%B5%EF%BD%B6%EF%BD%B7%EF%BD%B8%EF%BD%B9%EF" +
          "%BD%BA%EF%BD%BB%EF%BD%BC%EF%BD%BD%EF%BD%BE%EF%BD%BF%EF%BE%80%EF%BE%81%EF%BE%82%EF%BE%83";
        final String actual = encoder.encode(characters, "UTF-8");
        assertEquals(characters, expected, actual);
    }


    @Test
    public void testSafeCharacters() throws Exception {
        encoder.addSafeCharacter('@');
        encoder.addSafeCharacter('+');
        encoder.addSafeCharacter('=');
        encoder.addSafeCharacter('!');
        final String alphabet = "@!+=";
        final String expected = "@!+=";
        final String actual = encoder.encode(alphabet, "UTF-8");
        assertEquals(alphabet, expected, actual);
    }

    @Test
    public void testMultipleInvocations() throws Exception {
        final String text = "тестTest";
        final String expected = "%D1%82%D0%B5%D1%81%D1%82Test";
        for (int i = 0; i < 10; i++) {
            final String result = encoder.encode(text, "UTF-8");
            assertEquals(expected, result);
        }
    }

}
