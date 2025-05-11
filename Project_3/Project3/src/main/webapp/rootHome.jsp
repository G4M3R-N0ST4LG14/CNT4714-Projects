<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Root User Home</title>
    <style>
        body {
            background-color: black;
            color: white;
            text-align: center;
            font-family: Arial, sans-serif;
        }
        .container {
            margin: 0 auto;
            width: 80%;
        }
        textarea {
            width: 100%;
            height: 200px;
            background-color: blue;
            color: white;
            font-size: 16px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 8px;
            border: 1px solid white;
        }
        th {
            background-color: red;
        }
        input[type="submit"], input[type="reset"], button {
            padding: 10px 20px;
            font-size: 16px;
            margin: 5px;
            border: none;
            cursor: pointer;
        }
        input[type="submit"] {
            background-color: green;
        }
        input[type="reset"] {
            background-color: red;
        }
        button {
            background-color: yellow;
        }
    </style>
</head>
<body>
    <h1>Welcome to the Summer 2024 Project 3 Enterprise System</h1>
    <h2>A Servlet/JSP-based Multi-tiered Enterprise Application Using A Tomcat Container</h2>
    <div class="container">
        <h3>You are connected to the Project 3 Enterprise System database as a root-level user.<br>
        Please enter any SQL query or update command in the box below.</h3>
        <form action="RootUserServlet" method="post">
            <textarea name="sqlCommand" id="sqlCommand"><%= request.getAttribute("sqlCommand") != null ? request.getAttribute("sqlCommand") : "" %></textarea><br>
            <input type="submit" value="Execute Command">
            <input type="reset" value="Reset Form">
            <button type="button" onclick="clearResults()">Clear Results</button>
        </form>
        <div id="executionResults">
            <h3>Execution Results:</h3>
            <%
                List<String> columnNames = (List<String>) request.getAttribute("columnNames");
                List<List<String>> rows = (List<List<String>>) request.getAttribute("rows");
                String errorMessage = (String) request.getAttribute("errorMessage");
                String executionResult = (String) request.getAttribute("executionResult");

                if (errorMessage != null) {
                    out.println("<p style='color: red;'>" + errorMessage + "</p>");
                } else if (executionResult != null) {
                    out.println("<p style='color: green;'>" + executionResult + "</p>");
                } else if (columnNames != null && rows != null) {
                    out.println("<table border='1'><tr>");
                    for (String columnName : columnNames) {
                        out.println("<th>" + columnName + "</th>");
                    }
                    out.println("</tr>");
                    for (List<String> row : rows) {
                        out.println("<tr>");
                        for (String cell : row) {
                            out.println("<td>" + cell + "</td>");
                        }
                        out.println("</tr>");
                    }
                    out.println("</table>");
                }
            %>
        </div>
    </div>
    <script>
        function clearResults() {
            document.getElementById('executionResults').innerHTML = '<h3>Execution Results:</h3>';
        }
    </script>
</body>
</html>

