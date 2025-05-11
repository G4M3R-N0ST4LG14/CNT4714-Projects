<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Accountant Home</title>
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
        .command-options {
            background-color: gray;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
        }
        .command-options label {
            display: block;
            padding: 10px;
            cursor: pointer;
        }
        .command-options input[type="radio"] {
            display: none;
        }
        .command-options input[type="radio"]:checked + label {
            background-color: #4CAF50;
            color: white;
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
        input[type="submit"], button {
            padding: 10px 20px;
            font-size: 16px;
            margin: 5px;
            border: none;
            cursor: pointer;
        }
        input[type="submit"] {
            background-color: green;
        }
        button {
            background-color: yellow;
        }
    </style>
</head>
<body>
    <h1>Welcome to the Summer 2024 Project 3 Enterprise System</h1>
    <h2>Data Entry Application</h2>
    <div class="container">
        <h3>You are connected to the Project 3 Enterprise System database as an accountant-level user.<br>
        Please select the operation you would like to perform from the list below.</h3>
        <form action="AccountantUserServlet" method="post">
            <div class="command-options">
                <input type="radio" id="maxStatus" name="command" value="maxStatus">
                <label for="maxStatus">Get The Maximum Status Value Of All Suppliers (Returns a maximum value)</label>
                <input type="radio" id="totalWeight" name="command" value="totalWeight">
                <label for="totalWeight">Get The Total Weight Of All Parts (Returns a sum)</label>
                <input type="radio" id="totalShipments" name="command" value="totalShipments">
                <label for="totalShipments">Get The Total Number Of Shipments (Returns the current number of shipments in total)</label>
                <input type="radio" id="jobWithMostWorkers" name="command" value="jobWithMostWorkers">
                <label for="jobWithMostWorkers">Get The Name And Number Of Workers Of The Job With The Most Workers (Returns two values)</label>
                <input type="radio" id="listSuppliers" name="command" value="listSuppliers">
                <label for="listSuppliers">List The Name And Status Of Every Supplier (Returns a list of supplier names with status)</label>
            </div>
            <input type="submit" value="Execute Command">
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
