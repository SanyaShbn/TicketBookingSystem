<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.time.format.DateTimeFormatter, java.time.LocalDateTime" %>

<fmt:setBundle basename="messages" />

<html>
<head>
    <title>Tickets</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
    <script src="<c:url value="/js/toggleFilterFormScript.js"/>"></script>
</head>
<body>
<div>
    <h1><fmt:message key="tickets.list"/></h1>

    <div class="filter-bar">
        <button type="button" onclick="toggleFilterForm()">Настроить фильтр</button>
        <form action="${pageContext.request.contextPath}/tickets" method="get">
            <div class="form-item">
                <label for="priceSortOrder"><fmt:message key="ticket.price"/></label>
                <select id="priceSortOrder" name="priceSortOrder" class="scrollable-dropdown">
                    <option value="">-- Сортировка --</option>
                    <option value="ASC" ${param.priceSortOrder != null
                            && param.priceSortOrder == 'ASC' ? 'selected' : ''}>
                        По возрастанию
                    </option>
                    <option value="DESC" ${param.priceSortOrder != null
                            && param.priceSortOrder == 'DESC' ? 'selected' : ''}>
                        По убыванию
                    </option>
                </select>
            </div>
            <input type="hidden" name="eventId" value="${param.eventId}">
            <button type="submit"><fmt:message key="apply.filters"/></button>
        </form>
    </div>

    <button onclick="location.href='${pageContext.request.contextPath}/sport_events'">
        <fmt:message key="button.back"/>
    </button>
    <button onclick="location.href='${pageContext.request.contextPath}/create-ticket?<%= request.getQueryString() %>'">
        <fmt:message key="button.add"/>
    </button>
    <div class="arena-container">
        <c:choose>
        <c:when test="${not empty requestScope.tickets}">
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
            <div class="pagination" style="padding-top: 80px">
                <c:if test="${requestScope.page > 1}">
                    <a href="${pageContext.request.contextPath}/tickets?eventId=${param.eventId}&page=${param.page - 1}"
                       class="pagination-arrow">&laquo; <fmt:message key="page.previous"/></a>
                </c:if>
                <c:if test="${requestScope.tickets.size() eq requestScope.limit}">
                    <a href="${pageContext.request.contextPath}/tickets?eventId=${param.eventId}&page=${param.page != null
          ? param.page + 1 : 2}" class="pagination-arrow"><fmt:message key="page.next"/> &raquo;</a>
                </c:if>
            </div>
        </c:when>
            <c:otherwise>
                <div><fmt:message key="tickets.not_found"/></div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
