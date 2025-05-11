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
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@WebServlet("/RootUserServlet")
public class RootUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IOException("Unable to find " + fileName);
            }
            properties.load(input);
        }
        return properties;
    }

    private Connection connectToDatabase() throws IOException, SQLException, ClassNotFoundException {
        Properties dbProperties = loadProperties("commonDB.properties");
        Properties userProperties = loadProperties("root.properties");

        String dbDriverClass = dbProperties.getProperty("MYSQL_DB_DRIVER_CLASS");
        String dbUrl = dbProperties.getProperty("MYSQL_DB_URL");
        String dbUsername = userProperties.getProperty("MYSQL_DB_USERNAME");
        String dbPassword = userProperties.getProperty("MYSQL_DB_PASSWORD");

        Class.forName(dbDriverClass);
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sqlCommand = request.getParameter("sqlCommand");
        request.setAttribute("sqlCommand", sqlCommand);

        Connection connection = null;
        Statement statement = null;
        PreparedStatement businessLogicStatement = null;
        ResultSet resultSet = null;
        List<String> columnNames = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();
        String errorMessage = null;
        String executionResult = null;
        boolean businessLogicTriggered = false;
        List<String> businessLogicMessages = new ArrayList<>();

        try {
            connection = connectToDatabase();
            statement = connection.createStatement();

            boolean isResultSet = statement.execute(sqlCommand);
            if (isResultSet) {
                resultSet = statement.getResultSet();
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
            } else {
                int updateCount = statement.getUpdateCount();
                executionResult = "The statement executed successfully. " + updateCount + " row(s) affected.";
                request.setAttribute("executionResult", executionResult);

                if (sqlCommand.trim().toLowerCase().startsWith("insert into shipments")) {
                    // Extract the inserted snum and quantity values
                    String[] values = sqlCommand.split("\\(")[1].replace(")", "").split(",");
                    String snum = values[0].replace("'", "").trim();
                    int quantity = Integer.parseInt(values[3].replace(";", "").trim());

                    if (quantity >= 100) {
                        businessLogicStatement = connection.prepareStatement("UPDATE suppliers SET status = status + 5 WHERE snum = ?");
                        businessLogicStatement.setString(1, snum);
                        int affectedRows = businessLogicStatement.executeUpdate();
                        if (affectedRows > 0) {
                            businessLogicTriggered = true;
                            businessLogicMessages.add("Business Logic Detected - Updating Supplier Status for snum: " + snum);
                        }
                    }
                } else if (sqlCommand.trim().toLowerCase().startsWith("update shipments")) {
                    // Retrieve snum and new quantity for the affected rows
                    resultSet = statement.executeQuery("SELECT snum, quantity FROM shipments WHERE quantity >= 100");
                    while (resultSet.next()) {
                        String snum = resultSet.getString("snum");
                        int quantity = resultSet.getInt("quantity");

                        businessLogicStatement = connection.prepareStatement("UPDATE suppliers SET status = status + 5 WHERE snum = ?");
                        businessLogicStatement.setString(1, snum);
                        int affectedRows = businessLogicStatement.executeUpdate();
                        if (affectedRows > 0) {
                            businessLogicTriggered = true;
                            businessLogicMessages.add("Business Logic Detected - Updating Supplier Status for snum: " + snum);
                        }
                    }
                }
            }

            if (businessLogicTriggered) {
                StringBuilder businessLogicResult = new StringBuilder("<br>");
                for (String message : businessLogicMessages) {
                    businessLogicResult.append(message).append("<br>");
                }
                request.setAttribute("executionResult", executionResult + businessLogicResult.toString());
            }

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
                if (businessLogicStatement != null) businessLogicStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        request.getRequestDispatcher("rootHome.jsp").forward(request, response);
    }
}
