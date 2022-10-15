package com.evolutionnext.testcontainers.dao;

import com.evolutionnext.testcontainers.models.Student;
import com.evolutionnext.testcontainers.models.StudentId;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentDAOIntegrationTest {

    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        String property = System.getProperty("postgres.version");
        System.out.println(">>>" + property);
        connection = DriverManager
            .getConnection("jdbc:tc:postgresql:9.6.8:///school");
        var createTableSQL = """
            CREATE TABLE REGISTRATION (
              ID        SERIAL      PRIMARY KEY,
              FIRSTNAME TEXT        NOT NULL,
              LASTNAME  TEXT        NOT NULL,
              STUDENTID VARCHAR(20) NOT NULL
            );
            """;
        PreparedStatement preparedStatement =
            connection.prepareStatement(createTableSQL);
        boolean execute = preparedStatement.execute();
        System.out.println(execute);
    }

    @Test
    void insertAndFind() throws SQLException {
        String firstName = "Rogelio";
        String lastName = "Vargas";
        StudentId studentID = new StudentId("001");

        PreparedStatement insertStatement =
            connection.prepareStatement("""
                INSERT INTO REGISTRATION (FIRSTNAME, LASTNAME, STUDENTID) values
                (?, ?, ?);
                """, Statement.RETURN_GENERATED_KEYS);

        insertStatement.setString(1, firstName);
        insertStatement.setString(2, lastName);
        insertStatement.setString(3, studentID.id());
        insertStatement.executeUpdate();

        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
        generatedKeys.next();
        Long key = generatedKeys.getLong(1);

        assertThat(key).isNotNull();
        System.out.println(key);

        PreparedStatement findByStudentIDStatement = connection.prepareStatement(
            "SELECT * from REGISTRATION where STUDENTID = ?");
        findByStudentIDStatement.setString(1, studentID.id());
        ResultSet resultSet = findByStudentIDStatement.executeQuery();
        resultSet.next();

        Student student = new Student(
            resultSet.getLong("ID"),
            new StudentId(resultSet.getString("STUDENTID")),
            resultSet.getString("FIRSTNAME"),
            resultSet.getString("LASTNAME"));

        assertThat(student.id()).isEqualTo(key);
        assertThat(student.firstName()).isEqualTo("Rogelio");
        assertThat(student.lastName()).isEqualTo("Vargas");
        assertThat(student.studentId()).isEqualTo(studentID);

        System.out.println(student);
    }

    @AfterEach
    void tearDown() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
            "DELETE FROM REGISTRATION;");
        preparedStatement.executeUpdate();
    }
}
