package com.evolutionnext.jqwik;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.AlphaChars;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NewClassNamingConvention")
public class CaesarShiftProperty {
    @Property()
    void verifyThatCaesarShiftWorksForAllAlpha(
        @ForAll @AlphaChars String string,
        @ForAll int shift) {
        System.out.printf("string: %s, shift: %d%n", string, shift);
        assertThat(
            CaesarShift.decode(CaesarShift.encode(string, shift), shift))
            .isEqualTo(string);
    }
}
