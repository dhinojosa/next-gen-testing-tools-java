package com.evolutionnext.testcontainers.dao;

import com.evolutionnext.testcontainers.models.Student;
import com.evolutionnext.testcontainers.models.StudentId;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class PostgreSQLStudentDAO implements StudentDAO<Long> {

    private final Supplier<Connection> connectionSupplier;

    public PostgreSQLStudentDAO(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    public List<Student> findByFirstName(String firstName) {
        try (Connection connection =
                 getConnectionSupplier().get()) {
            PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT * from REGISTRATION where" +
                    " firstName = ?");
            preparedStatement.setString(1, firstName);
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Student> studentArrayList = new ArrayList<>();
            while (resultSet.next()) {
                studentArrayList.add(resultSetToStudent(resultSet));
            }
            return studentArrayList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Student> findAll()  {
        ArrayList<Student> result;
        try (Connection connection = connectionSupplier.get()) {
            PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT * from REGISTRATION;");
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Student> studentArrayList = new ArrayList<>();
            while (resultSet.next()) {
                studentArrayList.add(resultSetToStudent(resultSet));
            }
            result = studentArrayList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void deleteAll() throws SQLException {
        try (PreparedStatement preparedStatement =
                 getConnectionSupplier().get().prepareStatement(
                     "DELETE FROM REGISTRATION;")) {
            preparedStatement.executeUpdate();
        }
    }

    public void createTable() {
        var createTableSQL = """
            CREATE TABLE REGISTRATION (
              ID        SERIAL      PRIMARY KEY,
              FIRSTNAME TEXT        NOT NULL,
              LASTNAME  TEXT        NOT NULL,
              STUDENTID VARCHAR(20) NOT NULL
            );
            """;

        try (Connection connection =
                 connectionSupplier.get()) {
            PreparedStatement preparedStatement =
                connection.prepareStatement(createTableSQL);
            boolean execute = preparedStatement.execute();
            System.out.println("Table created?" + execute);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Student> findByStudentId(StudentId studentId) {
        try (Connection connection = connectionSupplier.get()) {
            PreparedStatement findByStudentIDStatement =
                connection.prepareStatement("SELECT * from REGISTRATION where" +
                    " STUDENTID = ?");
            findByStudentIDStatement.setString(1, studentId.id());
            ResultSet resultSet = findByStudentIDStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSetToStudent(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Student resultSetToStudent(ResultSet resultSet) throws SQLException {
        return new Student(
            resultSet.getLong("ID"),
            new StudentId(resultSet.getString("STUDENTID")),
            resultSet.getString("FIRSTNAME"),
            resultSet.getString("LASTNAME"));
    }


    @Override
    public Long persist(Student student) {
        try (PreparedStatement insertStatement =
                 connectionSupplier.get().prepareStatement("""
                     INSERT INTO REGISTRATION (FIRSTNAME, LASTNAME, STUDENTID) values
                     (?, ?, ?);
                     """, Statement.RETURN_GENERATED_KEYS)) {

            insertStatement.setString(1, student.firstName());
            insertStatement.setString(2, student.lastName());
            insertStatement.setString(3, student.studentId().id());
            insertStatement.executeUpdate();

            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
            generatedKeys.next();
            return generatedKeys.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Student> findByLastName(String lastName) {
        return null;
    }


    @Override
    public Optional<Student> findById(Long id) {
        return Optional.empty();
    }

    public Supplier<Connection> getConnectionSupplier() {
        return connectionSupplier;
    }
}
