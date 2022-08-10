package com.xyzcorp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Wallet {
    private final UUID uuid;
    Logger logger = LoggerFactory.getLogger(Wallet.class);
    private int balance = 0;

    public Wallet() {
        this.uuid = UUID.randomUUID();
    }

    public boolean isEmpty() {
        logger.debug("Querying isEmpty in wallet {}, which is {}", uuid, balance);
        return balance == 0;
    }

    public void addFunds(int amount) {
        logger.debug("Adding {} to wallet {}", amount, uuid);
        balance += amount;
    }

    public void spendFunds(int amount) {
        logger.debug("Removing amount {} from balance {} in wallet {}", amount, balance, uuid);
        balance -= amount;
    }

    public UUID getUUID() {
        return uuid;
    }
}
