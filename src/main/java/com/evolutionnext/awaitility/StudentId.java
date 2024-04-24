package com.evolutionnext.awaitility;

public record StudentId(String id) {
    public StudentId {
        if (id == null || !id.matches("\\d\\d-\\d\\d\\d\\d")) {
            throw new IllegalArgumentException(
                "Invalid StudentId format: " + id + ". It should be like: nn-nnnn, where n is a digit.");
        }
    }
}
