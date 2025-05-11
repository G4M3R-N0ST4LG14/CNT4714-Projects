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
import java.util.Properties;

@WebServlet("/ShipmentsRecordInsertServlet")
public class ShipmentsRecordInsertServlet extends HttpServlet {
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

    private boolean recordExists(Connection con, String tableName, String columnName, String value) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String snum = request.getParameter("snum");
        String pnum = request.getParameter("pnum");
        String jnum = request.getParameter("jnum");
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        response.setContentType("text/html");

        try (Connection con = connectToDatabase()) {
            if (!recordExists(con, "suppliers", "snum", snum)) {
                request.setAttribute("errorMessage", "Supplier with snum " + snum + " does not exist.");
            } else if (!recordExists(con, "parts", "pnum", pnum)) {
                request.setAttribute("errorMessage", "Part with pnum " + pnum + " does not exist.");
            } else if (!recordExists(con, "jobs", "jnum", jnum)) {
                request.setAttribute("errorMessage", "Job with jnum " + jnum + " does not exist.");
            } else {
                String sql = "INSERT INTO shipments (snum, pnum, jnum, quantity) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, snum);
                    ps.setString(2, pnum);
                    ps.setString(3, jnum);
                    ps.setInt(4, quantity);

                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected > 0) {
                        request.setAttribute("executionResult", "New shipments record: (" + snum + ", " + pnum + ", " + jnum + ", " + quantity + ") - successfully entered into database.");

                        if (quantity >= 100) {
                            try (PreparedStatement updateStatus = con.prepareStatement("UPDATE suppliers SET status = status + 5 WHERE snum = ?")) {
                                updateStatus.setString(1, snum);
                                int affectedRows = updateStatus.executeUpdate();
                                if (affectedRows > 0) {
                                    request.setAttribute("executionResult", request.getAttribute("executionResult") + "<br>Business Logic Detected - Updating Supplier Status for snum: " + snum);
                                }
                            }
                        }
                    } else {
                        request.setAttribute("executionResult", "Failed to insert the new shipments record.");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error executing the SQL statement: " + e.getMessage());
        }

        request.getRequestDispatcher("dataEntryHome.jsp").forward(request, response);
    }
}
