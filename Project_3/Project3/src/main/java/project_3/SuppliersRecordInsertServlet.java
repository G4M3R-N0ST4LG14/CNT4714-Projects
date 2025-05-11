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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

@WebServlet("/SuppliersRecordInsertServlet")
public class SuppliersRecordInsertServlet extends HttpServlet {
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
        Properties userProperties = loadProperties("dataentryuser.properties");

        String dbDriverClass = dbProperties.getProperty("MYSQL_DB_DRIVER_CLASS");
        Class.forName(dbDriverClass);

        String dbUrl = dbProperties.getProperty("MYSQL_DB_URL");
        String dbUsername = userProperties.getProperty("MYSQL_DB_USERNAME");
        String dbPassword = userProperties.getProperty("MYSQL_DB_PASSWORD");

        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String snum = request.getParameter("snum");
        String sname = request.getParameter("sname");
        int status = Integer.parseInt(request.getParameter("status"));
        String city = request.getParameter("city");

        response.setContentType("text/html");

        try (Connection con = connectToDatabase()) {
            String sql = "INSERT INTO suppliers (snum, sname, status, city) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, snum);
                ps.setString(2, sname);
                ps.setInt(3, status);
                ps.setString(4, city);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    request.setAttribute("executionResult", "New suppliers record: (" + snum + ", " + sname + ", " + status + ", " + city + ") - successfully entered into database.");
                } else {
                    request.setAttribute("executionResult", "Failed to insert the new suppliers record.");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error executing the SQL statement: " + e.getMessage());
        }

        request.getRequestDispatcher("dataEntryHome.jsp").forward(request, response);
    }
}
