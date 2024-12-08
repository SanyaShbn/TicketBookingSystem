<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.example.ticketbookingsystem.entity.Seat" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.ticketbookingsystem.entity.Row" %>
<%@ page import="com.example.ticketbookingsystem.entity.Ticket" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.ticketbookingsystem.entity.TicketStatus" %>

<%
    Set<String> sectors = new HashSet<>();
    Set<Row> rows = new HashSet<>();
    List<Long> ticketSeatsIds = new ArrayList<>();
    List<Seat> seats = (List<Seat>) request.getAttribute("seats");
    List<Ticket> tickets = (List<Ticket>) request.getAttribute("tickets");

    for (Ticket ticket : tickets) {
        if(ticket.getStatus()== TicketStatus.AVAILABLE) {
            ticketSeatsIds.add(ticket.getSeat().getId());
        }
    }

    for (Seat seat : seats) {
        sectors.add(seat.getRow().getSector().getSectorName());
        rows.add(seat.getRow());
    }
    List<Row> sortedRows = rows.stream()
            .sorted((r1, r2) -> Integer.compare(r1.getRowNumber(), r2.getRowNumber())).toList();
    List<Seat> sortedSeats = seats.stream()
            .sorted((r1, r2) -> Integer.compare(r1.getSeatNumber(), r2.getSeatNumber())).toList();

    request.setAttribute("sectors", sectors);
    request.setAttribute("rows", sortedRows);
    request.setAttribute("seats", sortedSeats);
    request.setAttribute("ticketSeatsIds", ticketSeatsIds);
%>

<!DOCTYPE html>
<html>
<head>
    <title>Доступные билеты</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<h1>Выберите место</h1>
<div class="view-arena-seats-container">
    <div class="arena-map">

        <c:forEach var="sector" items="${sectors}">
            <div class="arena-section">
                <h2>Сектор: ${sector}</h2>
                <c:forEach var="row" items="${rows}">
                    <c:if test="${sector == row.sector.sectorName}">
                        <div class="arena-row">
                            <c:forEach var="seat" items="${seats}">
                                <c:if test="${seat.row.id == row.id}">
                                    <div class="arena-seat ${ticketSeatsIds.contains(seat.id) ? 'available' : 'unavailable'}"
                                         data-seat-id="${seat.id}"
                                         title="Ряд: ${seat.row.rowNumber}, Место: ${seat.seatNumber}">
                                            ${seat.seatNumber}
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
        </c:forEach>
    </div>
    <div class="cart">
        <h2>Корзина</h2>
        <form id="cartForm" action="${pageContext.request.contextPath}/checkout" method="post">
            <ul id="cartItems"></ul>
            <p>Общая сумма: <span id="totalPrice">0</span> руб.</p>
            <button type="submit">Оформить заказ</button>
        </form>
    </div>
</div>

<script>
    const cartItems = [];
    const seatElements = document.querySelectorAll('.arena-seat.available');
    const cartList = document.getElementById('cartItems');
    const totalPriceElement = document.getElementById('totalPrice');

    seatElements.forEach(seat => {
        seat.style.cursor = 'pointer';
        seat.addEventListener('click', function () {
            const seatId = this.getAttribute('data-seat-id');
            const seatPrice = parseFloat(this.getAttribute('data-seat-price'));

            if (!cartItems.includes(seatId)) {
                cartItems.push(seatId);
                const listItem = document.createElement('li');
                listItem.textContent = `Место: ${seat.seatNumber}, Цена: ${seatPrice} руб.`;
                listItem.setAttribute('data-seat-id', seatId);
                cartList.appendChild(listItem);

                updateTotalPrice();
            }
        });
    });

    function updateTotalPrice() {
        let totalPrice = 0;
        cartItems.forEach(seatId => {
            const seatElement = document.querySelector(`.arena-seat[data-seat-id="${seatId}"]`);
            totalPrice += parseFloat(seatElement.getAttribute('data-seat-price'));
        });
        totalPriceElement.textContent = totalPrice.toFixed(2);
    }
</script>
</body>
</html>