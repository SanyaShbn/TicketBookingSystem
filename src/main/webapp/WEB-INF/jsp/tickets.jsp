<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>Визуализация арены</title>
    <style>
        .seat {
            width: 20px;
            height: 20px;
            border-radius: 50%;
            display: inline-block;
            margin: 5px;
        }
        .available { background-color: green; }
        .reserved { background-color: gray; }
    </style>
    <script>
        function showDetails(ticketId) {
            // Показывать детали билета
            alert('Ticket ID: ' + ticketId);
        }
    </script>
</head>
<body>
<h1>Визуализация арены</h1>
<div id="arena">
    <div class="sector">
        <h2>Сектор A</h2>
        <div class="row">
            <div class="seat available" onclick="showDetails(1)"></div>
            <div class="seat reserved" onclick="showDetails(2)"></div>
            <div class="seat available" onclick="showDetails(3)"></div>
        </div>
        <div class="row">
            <div class="seat reserved" onclick="showDetails(4)"></div>
            <div class="seat available" onclick="showDetails(5)"></div>
            <div class="seat reserved" onclick="showDetails(6)"></div>
        </div>
    </div>
    <div class="sector">
        <h2>Сектор B</h2>
        <div class="row">
            <div class="seat available" onclick="showDetails(7)"></div>
            <div class="seat reserved" onclick="showDetails(8)"></div>
            <div class="seat available" onclick="showDetails(9)"></div>
        </div>
        <div class="row">
            <div class="seat reserved" onclick="showDetails(10)"></div>
            <div class="seat available" onclick="showDetails(11)"></div>
            <div class="seat reserved" onclick="showDetails(12)"></div>
        </div>
    </div>
</div>
</body>
</html>