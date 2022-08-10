package com.evolutionnext.jqwik;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;

public class CaesarShiftTest {

    /**
     * Input: word(String)
     * Input: shift (int)
     * Output: word(String)
     * "", 0 -> ""
     */
    @Test
    public void testEncodeWithEmptyStringAndZero() {
        String result = CaesarShift.encode("", 0);
        assertThat(result).isEqualTo("");
    }

    @Test
    public void testEncodeWithSmallAAndZero() {
        String result = CaesarShift.encode("a", 0);
        assertThat(result).isEqualTo("a");
    }

    @Test
    public void testEncodeWithSmallAAndShiftOf1() {
        String result = CaesarShift.encode("a", 1);
        assertThat(result).isEqualTo("b");
    }

    //GreenBar
    @Test
    public void testMoreThanOneCharacter() {
        String result = CaesarShift.encode("ab", 0);
        assertThat(result).isEqualTo("ab");
    }

    //GreenBar
    @Test
    public void testShiftLetterGWithShiftOf2() {
        String result = CaesarShift.encode("g", 2);
        assertThat(result).isEqualTo("i");
    }

    //GreenBar
    @Test
    public void testShiftWordLowerCaseGoCartWithShiftOf2() {
        String result = CaesarShift.encode("gocart", 2);
        assertThat(result).isEqualTo("iqectv");
    }

    @Test
    public void testStringIsNull() {
        try {
            CaesarShift.encode(null, 0);
            fail("This line should not be invoked");
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("Word cannot be null");
        }
    }

    @Test
    public void testStringIsNullWithLambda() {
        assertThatThrownBy(() -> CaesarShift.encode(null, 0))
            .hasMessage("Word cannot be null")
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testLowerZAndShiftOf1() {
        String result = CaesarShift.encode("z", 1);
        assertThat(result).isEqualTo("a");
    }

    //GreenBar
    @Test
    public void testShift26times2() {
        String result = CaesarShift.encode("c", 26 * 2);
        assertThat(result).isEqualTo("c");
    }

    //GreenBar
    @Test
    public void testShift26times2plus1() {
        String result = CaesarShift.encode("c", (26 * 2) + 1);
        assertThat(result).isEqualTo("d");
    }

    @Test
    public void testNumbersAndSpecialCharacter() {
        int randomNumberBecauseItDoesntMatter = 40002;
        String result = CaesarShift.encode("!", randomNumberBecauseItDoesntMatter);
        assertThat(result).isEqualTo("!");
    }

    //GreenBar
    @Test
    public void testSpaces() {
        String result = CaesarShift.encode(" ", 40002);
        assertThat(result).isEqualTo(" ");
    }

    @Test
    public void testUpperCaseAandShift1() {
        String result = CaesarShift.encode("A", 1);
        assertThat(result).isEqualTo("B");
    }

    //GreenBar
    @Test
    public void testNegativeShiftWithSmallB() {
        String result = CaesarShift.encode("b", -1);
        assertThat(result).isEqualTo("a");
    }

    @Test
    public void testNegativeShiftWithSmallA() {
        String result = CaesarShift.encode("a", -1);
        assertThat(result).isEqualTo("z");
    }

    @Test
    public void testNegativeShiftWithCapital() {
        String result = CaesarShift.encode("A", -1);
        assertThat(result).isEqualTo("Z");
    }

    @Test
    public void testLessThanNegative26() {
        String result = CaesarShift.encode("A", -28);
        assertThat(result).isEqualTo("Y");
    }

    @Test
    public void testDecodeWithSmallAAndShiftOf1() {
        String result = CaesarShift.decode("a", 1);
        assertThat(result).isEqualTo("z");
    }
}
