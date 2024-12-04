<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.time.format.DateTimeFormatter, java.time.LocalDateTime" %>

<fmt:setBundle basename="messages" />

<html>
<head>
    <title>Tickets</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<div>
    <h1><fmt:message key="tickets.list"/></h1>
    <button onclick="location.href='${pageContext.request.contextPath}/sport_events'">
        <fmt:message key="button.back"/>
    </button>
    <button onclick="location.href='${pageContext.request.contextPath}/create-ticket?<%= request.getQueryString() %>'">
        <fmt:message key="button.add"/>
    </button>
    <div class="arena-container">
        <c:if test="${not empty requestScope.tickets}">
            <c:forEach var="ticket" items="${requestScope.tickets}">
                <div class="arena-card">

                    <div><fmt:message key="ticket.price"/>: ${ticket.price}</div>
                    <div><fmt:message key="ticket.sector"/>: ${ticket.seat.row.sector.sectorName}</div>
                    <div><fmt:message key="ticket.row"/>: ${ticket.seat.row.rowNumber}</div>
                    <div><fmt:message key="ticket.seat.numb"/>: ${ticket.seat.seatNumber}</div>

                    <form action="${pageContext.request.contextPath}/update-ticket" method="get" style="display:inline;">
                        <input type="hidden" name="id" value="${ticket.id}"/>
                        <input type="hidden" name="eventId" value="${ticket.sportEvent.id}"/>
                        <button type="submit"><fmt:message key="button.update"/></button>
                    </form>
                    <form action="${pageContext.request.contextPath}/delete-ticket?<%= request.getQueryString() %>"
                          method="post" style="display:inline;">
                        <input type="hidden" name="id" value="${ticket.id}"/>
                        <button type="submit"><fmt:message key="button.delete"/></button>
                    </form>
                </div>
            </c:forEach>
        </c:if>
    </div>
</div>
</body>
</html>
