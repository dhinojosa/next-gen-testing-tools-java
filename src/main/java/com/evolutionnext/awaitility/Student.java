package com.evolutionnext.awaitility;

import java.util.Objects;

public record Student(StudentId studentId, String firstName, String lastName) {

    public Student {
        Objects.requireNonNull(studentId, "studentId cannot be null");
        Objects.requireNonNull(firstName, "firstName cannot be null");
        Objects.requireNonNull(lastName, "lastName cannot be null");
    }
}
