<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../localization/localization.jsp" %>

<html>
<head>
    <title><fmt:message key="create.ticket.title" /></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<%@ include file="../localization/language-switcher.jsp" %>
<div class="form-container">
    <h1><fmt:message key="create.ticket.title" /></h1>
    <form action="${pageContext.request.contextPath}/admin/tickets/create" method="post">

        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

        <input type="hidden" name="eventId" value="${eventId}">

        <label for="price"><fmt:message key="ticket.price" />:</label>
        <input type="text" id="price" name="price" required>

        <label for="seatId"><fmt:message key="ticket.seat.numb" />:</label>
        <select id="seatId" name="seatId" class="scrollable-dropdown" required>
            <c:forEach var="seat" items="${seats}">
                <option value="${seat.id}">
                        Сектор ${seat.row.sector.sectorName}. Ряд ${seat.row.rowNumber}.
                        Место ${seat.seatNumber}
                </option>
            </c:forEach>
        </select>

        <div class="button-group">
            <button type="button" onclick="location.href='${pageContext.request.contextPath}/admin/tickets?<%= request.getQueryString() %>';">
                <fmt:message key="button.back" />
            </button>
            <button type="submit"><fmt:message key="button.save" /></button>
        </div>
    </form>

    <c:if test="${not empty requestScope.errors}">
        <div class="error">
            <c:forEach var="error" items="${requestScope.errors}">
                <span>${error.message}</span>
                <br/>
            </c:forEach>
        </div>
    </c:if>
</div>
</body>
</html>
