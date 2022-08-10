package com.xyzcorp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class WalletTest {

    private Wallet wallet;

    @BeforeEach
    private void setUp() {
        wallet = new Wallet();
    }

    @Test
    @DisplayName("The wallet should initially be empty")
    void testWalletIsEmpty() {
        assertThat(wallet.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("The wallet should be non-empty when adding funds")
    void testAddFundsNonEmptyWallet() {
        wallet.addFunds(1);
        assertThat(wallet.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("""
       The wallet should be empty when
       adding the funds and removing the same amount""")
    void testAddFundsWallet() {
        wallet.addFunds(1);
        assertThat(wallet.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("""
       Adding 10 funds to wallet and removing 3 should
       be non-empty""")
    void testSpendFundsWallet() {
        wallet.addFunds(10);
        wallet.spendFunds(3);
        assertThat(wallet.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("""
       Adding 10 funds to wallet and removing 10 should
       be empty""")
    void testAddAndSpendTheSameAmountOfFunds() {
        wallet.addFunds(10);
        wallet.spendFunds(10);
        assertThat(wallet.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("""
       All Wallets should have a UUID""")
    void testAllWalletInstancesShouldHaveAUUID() {
        UUID uuidOne = wallet.getUUID();
        UUID uuidTwo = new Wallet().getUUID();
        assertThat(uuidOne).isNotEqualTo(uuidTwo);
    }
}
