package com.evolutionnext.jqwik;

import java.util.Objects;

public class CaesarShift {
    public static final char SMALL_A = 'a';
    public static final char LARGE_A = 'A';
    public static final int ALPHA_SIZE = 26;

    public static String encode(String word, int shift) {
        Objects.requireNonNull(word, "Word cannot be null");
        if (word.isEmpty() || shift == 0) return word;
        StringBuilder result = new StringBuilder();
        for (char c : word.toCharArray()) {
            result.append(shiftChar(shift, c));
        }
        return result.toString();
    }

    private static char shiftChar(int shift, char c) {
        if (!Character.isAlphabetic(c)) return c;
        char preferredA = Character.isUpperCase(c) ? LARGE_A : SMALL_A;
        return (char) ((c - preferredA + ((shift % ALPHA_SIZE) + ALPHA_SIZE)) % ALPHA_SIZE + preferredA);
    }

    public static String decode(String word, int shift) {
        return encode(word, -shift);
    }
}
