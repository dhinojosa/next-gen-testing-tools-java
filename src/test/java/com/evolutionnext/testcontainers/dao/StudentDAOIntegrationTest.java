package com.evolutionnext.testcontainers.dao;

import com.evolutionnext.testcontainers.models.Student;
import com.evolutionnext.testcontainers.models.StudentId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class StudentDAOIntegrationTest {

    public static PostgreSQLStudentDAO postgreSQLStudentDAO;
    private static PostgreSQLContainer<?> postgreSQLContainer;

    @BeforeAll
    static void setUp() {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.5");
        postgreSQLContainer.withDatabaseName("school");
        postgreSQLContainer.start();

        org.postgresql.ds.PGConnectionPoolDataSource source =
            new org.postgresql.ds.PGConnectionPoolDataSource();
        source.setURL(postgreSQLContainer.getJdbcUrl());
        source.setUser(postgreSQLContainer.getUsername());
        source.setPassword(postgreSQLContainer.getPassword());
        source.setDatabaseName("school");

        postgreSQLStudentDAO = new PostgreSQLStudentDAO(() -> {
            try {
                return source.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        postgreSQLStudentDAO.createTable();
    }


    @Test
    void testInsertAndFind() {
        String firstName = "Rogelio";
        String lastName = "Vargas";
        StudentId studentID = new StudentId("001");

        Long key = postgreSQLStudentDAO.persist(new Student(0L, studentID,
            firstName, lastName));
        assertThat(key).isNotNull();

        Optional<Student> student =
            postgreSQLStudentDAO.findByStudentId(studentID);
        student.ifPresentOrElse(s -> {
            assertThat(s.id()).isEqualTo(key);
            assertThat(s.firstName()).isEqualTo(firstName);
            assertThat(s.lastName()).isEqualTo(lastName);
            assertThat(s.studentId()).isEqualTo(studentID);
        }, () -> fail("Did not find student"));

        System.out.println(student);
    }

    @Test
    void testFindAll() throws SQLException {
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("001"),
            "Rogelio", "Vargas"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("002"),
            "Barbara", "Conner"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("003"),
            "Hector", "Josey"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("004"),
            "Ludvig", "Iva"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("005"),
            "Benito", "Amadeo"));

        List<Student> result = postgreSQLStudentDAO.findAll();

        assertThat(result).size().isEqualTo(5);
        assertThat(result
            .stream()
            .map(Student::firstName)
            .collect(Collectors.joining(", ")))
            .isEqualTo("Rogelio, Barbara, Hector, Ludvig, Benito");
    }

    @Test
    void testFindByFirstNameWithTwoCandidates() {
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("001"),
            "Rogelio", "Vargas"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("002"),
            "Barbara", "Conner"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("003"),
            "Hector", "Josey"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("004"),
            "Ludvig", "Iva"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("005"),
            "Benito", "Amadeo"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("006"),
            "Hector", "Michaels"));

        List<Student> result = postgreSQLStudentDAO.findByFirstName("Hector");

        assertThat(result).size().isEqualTo(2);
        assertThat(result).map(Student::firstName).allMatch(s -> s.equals(
            "Hector"));
    }

    @Test
    void testFindByFirstNameWithNoCandidates() {
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("001"),
            "Rogelio", "Vargas"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("002"),
            "Barbara", "Conner"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("003"),
            "Hector", "Josey"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("004"),
            "Ludvig", "Iva"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("005"),
            "Benito", "Amadeo"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("006"),
            "Hector", "Michaels"));

        List<Student> result = postgreSQLStudentDAO.findByFirstName("Ester");

        assertThat(result).size().isEqualTo(0);
    }

    @Test
    void testFindByFirstNameWithOneCandidates() {
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("001"),
            "Rogelio", "Vargas"));
        Long barbarasId = postgreSQLStudentDAO.persist(new Student(0L,
            new StudentId("002"),
            "Barbara", "Conner"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("003"),
            "Hector", "Josey"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("004"),
            "Ludvig", "Iva"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("005"),
            "Benito", "Amadeo"));
        postgreSQLStudentDAO.persist(new Student(0L, new StudentId("006"),
            "Hector", "Michaels"));

        List<Student> result = postgreSQLStudentDAO.findByFirstName("Barbara");

        assertThat(result).size().isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(new Student(barbarasId, new StudentId("002"),
            "Barbara", "Conner"));
    }


    @AfterEach
    void tearDown() throws SQLException {
        postgreSQLStudentDAO.deleteAll();
    }

    @AfterAll
    static void afterAll() {
        postgreSQLContainer.close();
    }
}
