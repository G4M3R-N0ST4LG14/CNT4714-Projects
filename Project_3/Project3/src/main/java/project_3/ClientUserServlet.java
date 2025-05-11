/* Name: Arturo Lara
Course: CNT 4714 – Summer 2024 – Project Three
Assignment title: A Three-Tier Distributed Web-Based Application
Date: August 1, 2024
*/

package project_3;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@WebServlet("/ClientUserServlet")
public class ClientUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sqlCommand = request.getParameter("sqlCommand");
        request.setAttribute("sqlCommand", sqlCommand);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<String> columnNames = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();
        String errorMessage = null;

        try {
            Properties dbProperties = new Properties();
            dbProperties.load(getClass().getClassLoader().getResourceAsStream("commonDB.properties"));

            Properties userProperties = new Properties();
            userProperties.load(getClass().getClassLoader().getResourceAsStream("client.properties"));

            String dbDriverClass = dbProperties.getProperty("MYSQL_DB_DRIVER_CLASS");
            String dbUrl = dbProperties.getProperty("MYSQL_DB_URL");
            String dbUsername = userProperties.getProperty("MYSQL_DB_USERNAME");
            String dbPassword = userProperties.getProperty("MYSQL_DB_PASSWORD");

            Class.forName(dbDriverClass);
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlCommand);

            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(rsmd.getColumnName(i));
            }

            while (resultSet.next()) {
                List<String> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getString(i));
                }
                rows.add(row);
            }

            request.setAttribute("columnNames", columnNames);
            request.setAttribute("rows", rows);
        } catch (SQLException e) {
            errorMessage = "Error executing the SQL statement: " + e.getMessage();
            request.setAttribute("errorMessage", errorMessage);
        } catch (ClassNotFoundException e) {
            errorMessage = "Database connection error: " + e.getMessage();
            request.setAttribute("errorMessage", errorMessage);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        request.getRequestDispatcher("clientHome.jsp").forward(request, response);
    }
}
