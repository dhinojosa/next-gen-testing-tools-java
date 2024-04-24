package com.evolutionnext.awaitility;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyService {

    ExecutorService executorService = Executors.newFixedThreadPool(3);
    private ConcurrentHashMap<StudentId, Student> students = new ConcurrentHashMap<>();

    public void addStudent(Student student){
       executorService.submit(() -> {
           try {
               Thread.sleep(4000);
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
           students.put(student.studentId(), student);
       });
    }

    public Optional<Student> getStudent(StudentId studentId) {
        System.out.printf("Called at %s%n", LocalDateTime.now());
        return Optional.ofNullable(students.get(studentId));
    }
}
