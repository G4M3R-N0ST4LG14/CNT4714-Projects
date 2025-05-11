<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Data Entry Application</title>
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
    <h2>Data Entry Application</h2>
    <div class="container">
        <h3>You are connected to the Project 3 Enterprise System database as a data-entry-level user.<br>
        Enter the data values in a form below to add a new record to the corresponding database table.</h3>

        <!-- Suppliers Record Insert -->
        <div>
            <h3>Suppliers Record Insert</h3>
            <form action="SuppliersRecordInsertServlet" method="post">
                <table>
                    <tr>
                        <td>snum</td>
                        <td><input type="text" name="snum" required></td>
                        <td>sname</td>
                        <td><input type="text" name="sname" required></td>
                        <td>status</td>
                        <td><input type="text" name="status" required></td>
                        <td>city</td>
                        <td><input type="text" name="city" required></td>
                    </tr>
                </table>
                <input type="submit" value="Enter Supplier Record Into Database">
                <input type="reset" value="Clear Data and Results">
            </form>
        </div>

        <!-- Parts Record Insert -->
        <div>
            <h3>Parts Record Insert</h3>
            <form action="PartsRecordInsertServlet" method="post">
                <table>
                    <tr>
                        <td>pnum</td>
                        <td><input type="text" name="pnum" required></td>
                        <td>pname</td>
                        <td><input type="text" name="pname" required></td>
                        <td>color</td>
                        <td><input type="text" name="color" required></td>
                        <td>weight</td>
                        <td><input type="text" name="weight" required></td>
                        <td>city</td>
                        <td><input type="text" name="city" required></td>
                    </tr>
                </table>
                <input type="submit" value="Enter Part Record Into Database">
                <input type="reset" value="Clear Data and Results">
            </form>
        </div>

        <!-- Jobs Record Insert -->
        <div>
            <h3>Jobs Record Insert</h3>
            <form action="JobsRecordInsertServlet" method="post">
                <table>
                    <tr>
                        <td>jnum</td>
                        <td><input type="text" name="jnum" required></td>
                        <td>jname</td>
                        <td><input type="text" name="jname" required></td>
                        <td>numworkers</td>
                        <td><input type="text" name="numworkers" required></td>
                        <td>city</td>
                        <td><input type="text" name="city" required></td>
                    </tr>
                </table>
                <input type="submit" value="Enter Job Record Into Database">
                <input type="reset" value="Clear Data and Results">
            </form>
        </div>

        <!-- Shipments Record Insert -->
        <div>
            <h3>Shipments Record Insert</h3>
            <form action="ShipmentsRecordInsertServlet" method="post">
                <table>
                    <tr>
                        <td>snum</td>
                        <td><input type="text" name="snum" required></td>
                        <td>pnum</td>
                        <td><input type="text" name="pnum" required></td>
                        <td>jnum</td>
                        <td><input type="text" name="jnum" required></td>
                        <td>quantity</td>
                        <td><input type="text" name="quantity" required></td>
                    </tr>
                </table>
                <input type="submit" value="Enter Shipment Record Into Database">
                <input type="reset" value="Clear Data and Results">
            </form>
        </div>

        <div id="executionResults">
            <h3>Execution Results:</h3>
            <%
                String errorMessage = (String) request.getAttribute("errorMessage");
                String executionResult = (String) request.getAttribute("executionResult");

                if (errorMessage != null) {
                    out.println("<p style='color: red;'>" + errorMessage + "</p>");
                } else if (executionResult != null) {
                    out.println("<p style='color: green;'>" + executionResult + "</p>");
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
