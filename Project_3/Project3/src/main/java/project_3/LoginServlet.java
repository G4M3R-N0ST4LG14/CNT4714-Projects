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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

    private Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IOException("Unable to find " + fileName);
            }
            properties.load(input);
            LOGGER.log(Level.INFO, "Loaded properties from {0}", fileName);
        }
        return properties;
    }

    private Connection connectToDatabase() throws IOException, SQLException, ClassNotFoundException {
        Properties dbProperties = loadProperties("db.properties");
        Properties userProperties = loadProperties("user.properties");

        String dbDriverClass = dbProperties.getProperty("MYSQL_DB_DRIVER_CLASS");
        LOGGER.log(Level.INFO, "Driver class: {0}", dbDriverClass);
        Class.forName(dbDriverClass);

        String dbUrl = dbProperties.getProperty("MYSQL_DB_URL");
        String dbUsername = userProperties.getProperty("MYSQL_DB_USERNAME");
        String dbPassword = userProperties.getProperty("MYSQL_DB_PASSWORD");

        LOGGER.log(Level.INFO, "Attempting to connect to database...");
        LOGGER.log(Level.INFO, "DB URL: {0}", dbUrl);
        LOGGER.log(Level.INFO, "DB Username: {0}", dbUsername);

        Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        LOGGER.log(Level.INFO, "Successfully connected to the database");
        return connection;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection con = connectToDatabase()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM usercredentials WHERE login_username=? AND login_password=?");
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                switch (username) {
                    case "root":
                        response.sendRedirect("rootHome.jsp");
                        break;
                    case "client":
                        response.sendRedirect("clientHome.jsp");
                        break;
                    case "dataentryuser":
                        response.sendRedirect("dataEntryHome.jsp");
                        break;
                    case "theaccountant":
                        response.sendRedirect("accountantHome.jsp");
                        break;
                    default:
                        out.println("Invalid user role!");
                }
            } else {
                response.sendRedirect("errorpage.html");
            }

            rs.close();
            ps.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Database connection error", e);
            out.println("Database connection error: " + e.getMessage());
        }

        out.close();
    }
}
