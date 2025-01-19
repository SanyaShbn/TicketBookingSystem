<%--@elvariable id="arena" type="com.sun.org.apache.xml.internal.security.signature.Manifest"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../localization/localization.jsp" %>

<html>
<head>
    <title><fmt:message key="update.ticket.title" /></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<%@ include file="../localization/language-switcher.jsp" %>
<div class="form-container">
    <h1><fmt:message key="update.ticket.title" /></h1>
    <form action="${pageContext.request.contextPath}/admin/update-ticket?<%= request.getQueryString() %>" method="post">
        <input type="hidden" name="id" value="${ticket.id}">
        <label for="price"><fmt:message key="ticket.price" />:</label>
        <input type="text" id="price" name="price" value="${ticket.price}" required>

        <label for="seat"><fmt:message key="ticket.seat.numb" />:</label>
        <select id="seat" name="seat" class="scrollable-dropdown" required>
            <c:forEach var="seat" items="${seats}">
                <option value="${seat.id}" ${seat.id == ticket.seat.id ? 'selected' : ''}>
                    Сектор ${seat.row.sector.sectorName}. Ряд ${seat.row.rowNumber}.
                    Место ${seat.seatNumber}
                </option>
            </c:forEach>
        </select>

        <div class="button-group">
            <button type="button"
                    onclick="location.href='${pageContext.request.contextPath}/admin/tickets?<%= request.getQueryString() %>';">
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