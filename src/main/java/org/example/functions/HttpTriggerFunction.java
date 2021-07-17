package org.example.functions;

import com.google.gson.Gson;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.sqlserver.jdbc.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class HttpTriggerFunction {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        /*String dbConnString = System.getenv("SQLCONNSTR_myDBConnString");
        System.out.println("dbConnString: " + dbConnString);*/

        List<Course> courses = new ArrayList<>();

        String dbConnString = "jdbc:sqlserver://mydbserveruno.database.windows.net:1433;database=mydbnameuno;user=raycartalla@mydbserveruno;password=c36560RL;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
//        String dbConnString = System.getenv("SQLCONNSTR_MyDBConnString");
        try {
            Connection connection = DriverManager.getConnection(dbConnString);
            context.getLogger().info("Database connection test: " + connection.getCatalog());

            if (request.getBody().isPresent()) {
                Gson gson = new Gson();
                Course course = gson.fromJson(request.getBody().get(), Course.class);
                insertData(course, connection, context);
                courses = readData(null, connection, context);
            }

            String courseid = request.getQueryParameters().get("courseid");
            courses = readData(courseid, connection, context);

            //Course course = new Course(2L, "AZ-900 Fundamentals", 4.5D);
            //updateData(course, connection, context);
            //deleteData(course, connection, context);
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        String coursesJson = new Gson().toJson(courses);
        return request.createResponseBuilder(HttpStatus.OK).body(coursesJson).build();

    }

    private void deleteData(Course todo, Connection connection, ExecutionContext context) throws SQLException {
        context.getLogger().info("Delete data");
        PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Course WHERE courseid = ?;");
        deleteStatement.setLong(1, todo.getCourseID());
        deleteStatement.executeUpdate();
    }

    private void updateData(Course course, Connection connection, ExecutionContext context) throws SQLException {
        context.getLogger().info("Update data");
        PreparedStatement updateStatement = connection
                .prepareStatement("UPDATE Course SET coursename = ?, rating = ? WHERE courseid = ?;");

        updateStatement.setString(1, course.getCourseName());
        updateStatement.setDouble(2, course.getRating());
        updateStatement.setLong(3, course.getCourseID());
        updateStatement.executeUpdate();
    }

    private void insertData(Course course, Connection connection, ExecutionContext context) throws SQLException {
        context.getLogger().info("Insert data");
        PreparedStatement insertStatement = connection
                .prepareStatement("INSERT INTO Course (courseid, coursename, rating) VALUES (?, ?, ?);");

        insertStatement.setLong(1, course.getCourseID());
        insertStatement.setString(2, course.getCourseName());
        insertStatement.setDouble(3, course.getRating());
        insertStatement.executeUpdate();
    }

    private List<Course> readData(String courseId, Connection connection, ExecutionContext context) throws SQLException {
        List<Course> courses = new ArrayList<>();

        PreparedStatement readStatement;
        if (!StringUtils.isEmpty(courseId)) {
            readStatement = connection.prepareStatement("SELECT * FROM course where courseid = ?;");
            readStatement.setLong(1, new Long(courseId));
        } else {
            readStatement = connection.prepareStatement("SELECT * FROM course;");
        }

        ResultSet resultSet = readStatement.executeQuery();
        while (resultSet.next()) {
            Course course = new Course();
            course.setCourseID(resultSet.getLong("courseid"));
            course.setCourseName(resultSet.getString("coursename"));
            course.setRating(resultSet.getDouble("rating"));
            context.getLogger().info("Data read from the database.. ");
            courses.add(course);
        }
        return courses;
    }
}
