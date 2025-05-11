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

@WebServlet("/AccountantUserServlet")
public class AccountantUserServlet extends HttpServlet {
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
        Properties userProperties = loadProperties("accountant.properties");

        String dbDriverClass = dbProperties.getProperty("MYSQL_DB_DRIVER_CLASS");
        String dbUrl = dbProperties.getProperty("MYSQL_DB_URL");
        String dbUsername = userProperties.getProperty("MYSQL_DB_USERNAME");
        String dbPassword = userProperties.getProperty("MYSQL_DB_PASSWORD");

        Class.forName(dbDriverClass);
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String command = request.getParameter("command");

        Connection connection = null;
        CallableStatement callableStatement = null;
        ResultSet resultSet = null;
        List<String> columnNames = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();
        String errorMessage = null;
        String executionResult = null;

        try {
            connection = connectToDatabase();
            switch (command) {
                case "maxStatus":
                    callableStatement = connection.prepareCall("{CALL Get_The_Maximum_Status_Of_All_Suppliers()}");
                    break;
                case "totalWeight":
                    callableStatement = connection.prepareCall("{CALL Get_The_Sum_Of_All_Parts_Weights()}");
                    break;
                case "totalShipments":
                    callableStatement = connection.prepareCall("{CALL Get_The_Total_Number_Of_Shipments()}");
                    break;
                case "jobWithMostWorkers":
                    callableStatement = connection.prepareCall("{CALL Get_The_Name_Of_The_Job_With_The_Most_Workers()}");
                    break;
                case "listSuppliers":
                    callableStatement = connection.prepareCall("{CALL List_The_Name_And_Status_Of_All_Suppliers()}");
                    break;
                default:
                    throw new IllegalArgumentException("Invalid command: " + command);
            }

            boolean isResultSet = callableStatement.execute();
            if (isResultSet) {
                resultSet = callableStatement.getResultSet();
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
                int updateCount = callableStatement.getUpdateCount();
                executionResult = "The statement executed successfully. " + updateCount + " row(s) affected.";
                request.setAttribute("executionResult", executionResult);
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
                if (callableStatement != null) callableStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        request.getRequestDispatcher("accountantHome.jsp").forward(request, response);
    }
}
