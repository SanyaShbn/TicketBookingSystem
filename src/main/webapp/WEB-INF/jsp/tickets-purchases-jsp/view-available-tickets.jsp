<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.ticketbookingsystem.entity.Row" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.ticketbookingsystem.entity.TicketStatus" %>
<%@ page import="com.example.ticketbookingsystem.dto.seat_dto.SeatReadDto" %>
<%@ page import="com.example.ticketbookingsystem.dto.ticket_dto.TicketReadDto" %>
<%@ include file="../localization/localization.jsp" %>

<%
    Set<String> sectors = new HashSet<>();
    Set<Row> rows = new HashSet<>();
    List<Long> ticketSeatsIds = new ArrayList<>();
    List<SeatReadDto> seats = (List<SeatReadDto>) request.getAttribute("seats");
    List<TicketReadDto> tickets = (List<TicketReadDto>) request.getAttribute("tickets");

    for (TicketReadDto ticket : tickets) {
        if(ticket.getStatus() != TicketStatus.SOLD) {
            ticketSeatsIds.add(ticket.getSeat().getId());
        }
    }

    for (SeatReadDto seat : seats) {
        sectors.add(seat.getRow().getSector().getSectorName());
        rows.add(seat.getRow());
    }
    List<Row> sortedRows = rows.stream()
            .sorted((r1, r2) -> Integer.compare(r1.getRowNumber(), r2.getRowNumber())).toList();
    List<SeatReadDto> sortedSeats = seats.stream()
            .sorted((r1, r2) -> Integer.compare(r1.getSeatNumber(), r2.getSeatNumber())).toList();

    request.setAttribute("sectors", sectors);
    request.setAttribute("rows", sortedRows);
    request.setAttribute("seats", sortedSeats);
    request.setAttribute("ticketSeatsIds", ticketSeatsIds);
%>

<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="view.available.tickets.page.title"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
    <script src="<c:url value="/js/cookieUtils.js"/>"></script>
    <script src="<c:url value="/js/error-notification.js"/>"></script>
    <script src="<c:url value="/js/users-cart-script.js"/>"></script>
    <script src="<c:url value="/js/handle-confirm-form.js"/>"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/language-switcher.css"/>">
</head>
<body>
<div class="language-switcher" style="right: 400px; top: 20px">
    <select onchange="window.location.href=this.value;">
        <option value="<c:url value='${pageContext.request.contextPath}?lang=en'/>" <c:if test="${locale == 'en'}">selected</c:if>>English</option>
        <option value="<c:url value='${pageContext.request.contextPath}?lang=ru'/>" <c:if test="${locale == 'ru'}">selected</c:if>>Русский</option>
    </select>
</div>
<h1><fmt:message key="choose.seat.title"/></h1>
<button style="margin-bottom: 10px" onclick="confirmNavigation(event)">
    <fmt:message key="button.back"/>
</button>
<div class="view-arena-seats-container">
    <div class="arena-map">
        <c:forEach var="sector" items="${sectors}">
            <div class="arena-section">
                <h2><fmt:message key="ticket.sector"/>: ${sector}</h2>
                <c:forEach var="row" items="${rows}">
                    <c:if test="${sector == row.sector.sectorName}">
                        <div class="arena-row">
                            <c:forEach var="seat" items="${seats}">
                                <c:if test="${seat.row.id == row.id}">
                                    <c:set var="matchingTicket" value="${null}" />
                                    <c:forEach var="ticket" items="${tickets}">
                                        <c:if test="${ticket.seat.id == seat.id}">
                                            <c:set var="matchingTicket" value="${ticket}" />
                                        </c:if>
                                    </c:forEach>
                                    <fmt:message key="ticket.price.not.set" var="missingTicketPrice"/>
                                    <fmt:message key="ticket.not.available" var="ticketNotAvailable"/>
                                    <div class="arena-seat ${ticketSeatsIds.contains(seat.id) ? 'available' : 'unavailable'}"
                                         data-seat-id="${seat.id}"
                                         data-ticket-id="${matchingTicket.id}"
                                         data-sector-name="${seat.row.sector.sectorName}"
                                         data-row-numb="${seat.row.rowNumber}"
                                         data-seat-numb="${seat.seatNumber}"
                                         data-seat-price="${matchingTicket!=null ? matchingTicket.price : missingTicketPrice}"
                                         title="<fmt:message key="ticket.sector"/>: ${seat.row.sector.sectorName}, <fmt:message key="ticket.row"/>: ${seat.row.rowNumber}, <fmt:message key="ticket.seat.numb"/>: ${seat.seatNumber}, <fmt:message key="ticket.price"/>: ${matchingTicket!=null ? matchingTicket.price : ticketNotAvailable}">
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
        <h2><fmt:message key="user.cart"/> (<fmt:message key="tickets.in.user.cart"/>:<span id="cartCount">0</span>)</h2>

        <input type="hidden" id="csrfToken" name="${_csrf.parameterName}" value="${_csrf.token}" />

        <form id="cartForm" action="${pageContext.request.contextPath}/purchase" method="get">
            <ul id="cartItems">
                <li id="emptyCartMessage"><fmt:message key="user.cart.empty"/></li>
            </ul>
            <p><fmt:message key="general.price"/>: <span id="totalPrice">0</span> <fmt:message key="currency"/></p>
            <button id="clearCartButton" type="button" style="display: none;">
                <fmt:message key="delete.all.from.user.cart"/>
            </button>
            <button id="checkoutButton" type="submit" style="display: none;">
                <fmt:message key="submit.purchase"/>
            </button>
        </form>
    </div>

    <c:if test="${not empty requestScope.errors}">
        <div class="error">
            <c:forEach var="error" items="${requestScope.errors}">
                <span>${error.defaultMessage}</span>
                <br/>
            </c:forEach>
        </div>
    </c:if>
</div>
</body>
</html>
