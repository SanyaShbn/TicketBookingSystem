<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:setBundle basename="messages" />

<html>
<head>
    <title><fmt:message key="purchased.tickets"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/view-purchased-tickets.css' />">
    <script src="<c:url value="/js/view-purchased-tickets.js"/>"></script>
</head>
<body>
<h1><fmt:message key="purchased.tickets"/></h1>
<button type="button"
        style="margin-bottom: 20px"
        onclick="preventBackNavigation(event)">
    <fmt:message key="back.to.view.available.events" />
</button>
<c:forEach var="ticket" items="${purchasedTickets}">
    <div id="ticket-${ticket.ticketId}" class="ticket-info">
        <p><fmt:message key="sport_event.eventName" />: ${ticket.eventName}</p>
        <c:set var="eventDateTime" value="${ticket.eventDateTime}" />
        <c:set var="formattedDate" value="${fn:substring(eventDateTime, 0, 10)}" />
        <c:set var="formattedTime" value="${fn:substring(eventDateTime, 11, 16)}" />
        <p><fmt:message key="sport_event.eventDate"/>: ${formattedDate}</p>
        <p><fmt:message key="sport_event.eventTime"/>: ${formattedTime}</p>
        <p><fmt:message key="arena" />: ${ticket.arenaName}, ${ticket.arenaCity}</p>
        <p><fmt:message key="sector" />: ${ticket.sectorName}</p>
        <p><fmt:message key="row" />: ${ticket.rowNumber}</p>
        <p><fmt:message key="seat" />: ${ticket.seatNumber}</p>
        <p><fmt:message key="ticket.price" />: ${ticket.price} руб.</p>
        <button class="no-print" onclick="printTicket(${ticket.ticketId})"><fmt:message key="print.ticket" /></button>
    </div>
</c:forEach>

<c:if test="${not empty requestScope.errors}">
    <div class="error">
        <c:forEach var="error" items="${requestScope.errors}">
            <span>${error.message}</span>
            <br/>
        </c:forEach>
    </div>
</c:if>
</body>
</html>
