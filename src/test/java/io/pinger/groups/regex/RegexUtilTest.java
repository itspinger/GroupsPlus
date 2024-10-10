package io.pinger.groups.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class RegexUtilTest {

    @Test
    void testExtractMatches_singleMatch() {
        final String input = "This is a test string with one test.";
        final Pattern pattern = Pattern.compile("test");

        final List<String> matches = RegexUtil.extractMatches(input, pattern);

        assertEquals(2, matches.size());
        assertTrue(matches.contains("test"));
    }

    @Test
    void testExtractMatches_multipleMatches() {
        final String input = "abc123def456ghi789";
        final Pattern pattern = Pattern.compile("\\d+");

        final List<String> matches = RegexUtil.extractMatches(input, pattern);

        assertEquals(3, matches.size());
        assertEquals("123", matches.get(0));
        assertEquals("456", matches.get(1));
        assertEquals("789", matches.get(2));
    }

    @Test
    void testExtractMatches_noMatch() {
        final String input = "There is no digit here!";
        final Pattern pattern = Pattern.compile("\\d+");

        final List<String> matches = RegexUtil.extractMatches(input, pattern);

        assertTrue(matches.isEmpty());
    }

    @Test
    void testExtractMatches_specialCharacters() {
        final String input = "Match this special character: $!";
        final Pattern pattern = Pattern.compile("\\$!");

        final List<String> matches = RegexUtil.extractMatches(input, pattern);

        assertEquals(1, matches.size());
        assertEquals("$!", matches.get(0));
    }

    @Test
    void testExtractMatches_emptyInput() {
        final String input = "";
        final Pattern pattern = Pattern.compile("test");

        final List<String> matches = RegexUtil.extractMatches(input, pattern);

        assertTrue(matches.isEmpty());
    }

    @Test
    void testExtractMatches_fullMatch() {
        final String input = "The entire input string";
        final Pattern pattern = Pattern.compile(".+");

        final List<String> matches = RegexUtil.extractMatches(input, pattern);

        assertEquals(1, matches.size());
        assertEquals("The entire input string", matches.get(0));
    }
}