<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.time.format.DateTimeFormatter, java.time.LocalDateTime" %>
<%@ include file="../localization/localization.jsp" %>

<html>
<head>
    <title><fmt:message key="tickets.list"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
    <script src="<c:url value="/js/cookieUtils.js"/>"></script>
    <script src="<c:url value="/js/toggle-filter-form-script.js"/>"></script>
</head>
<body>
<%@ include file="../localization/language-switcher.jsp" %>
<div>
    <h1><fmt:message key="tickets.list"/></h1>
    <div class="filter-bar">
        <button type="button" onclick="toggleFilterForm()"><fmt:message key="setup.filter.button"/></button>
        <form action="${pageContext.request.contextPath}/admin/tickets" method="get">
            <div class="form-item">
                <label for="priceSortOrder"><fmt:message key="ticket.price"/></label>
                <select id="priceSortOrder" name="priceSortOrder" class="scrollable-dropdown">
                    <option value="">-- <fmt:message key="sorting"/> --</option>
                    <option value="ASC" ${requestScope.filter.priceSortOrder != null
                            && requestScope.filter.priceSortOrder == 'ASC' ? 'selected' : ''}>
                        <fmt:message key="sorting.asc"/>
                    </option>
                    <option value="DESC" ${requestScope.filter.priceSortOrder != null
                            && requestScope.filter.priceSortOrder == 'DESC' ? 'selected' : ''}>
                        <fmt:message key="sorting.desc"/>
                    </option>
                </select>
            </div>
            <div>
                <label for="displayPage"><fmt:message key="page.current"/>:
                    <input id="displayPage" type="number" value="${requestScope.tickets.metadata.page + 1}">
                    <input id="page" type="hidden" name="page" value="${requestScope.tickets.metadata.page}">
                </label>
                <label for="size"><fmt:message key="page.content.size"/>:
                    <input id="size" type="number" name="size" value="${requestScope.tickets.metadata.size}">
                </label>
            </div>
            <input type="hidden" name="eventId" value="${param.eventId}">
            <button type="submit"><fmt:message key="apply.filters"/></button>
        </form>
    </div>

    <button onclick="location.href='${pageContext.request.contextPath}/admin/sport_events'">
        <fmt:message key="button.back"/>
    </button>
    <button onclick="location.href='${pageContext.request.contextPath}/admin/tickets/create?<%= request.getQueryString() %>'">
        <fmt:message key="button.add"/>
    </button>
    <div class="arena-container">
        <c:choose>
        <c:when test="${not empty requestScope.tickets.content}">
            <c:forEach var="ticket" items="${requestScope.tickets.content}">
                <div class="arena-card">

                    <div><fmt:message key="ticket.price"/>: ${ticket.price}</div>
                    <div><fmt:message key="ticket.sector"/>: ${ticket.seat.row.sector.sectorName}</div>
                    <div><fmt:message key="ticket.row"/>: ${ticket.seat.row.rowNumber}</div>
                    <div><fmt:message key="ticket.seat.numb"/>: ${ticket.seat.seatNumber}</div>

                    <form action="${pageContext.request.contextPath}/admin/tickets/${ticket.id}/update" method="get" style="display:inline;">
                        <input type="hidden" name="id" value="${ticket.id}"/>
                        <input type="hidden" name="eventId" value="${ticket.sportEvent.id}"/>
                        <input type="hidden" name="seatId" value="${ticket.seat.id}"/>
                        <button type="submit"><fmt:message key="button.update"/></button>
                    </form>
                    <form action="${pageContext.request.contextPath}/admin/tickets/${ticket.id}/delete?<%= request.getQueryString() %>"
                          method="post" style="display:inline;">
                        <input type="hidden" name="id" value="${ticket.id}"/>
                        <button type="submit"><fmt:message key="button.delete"/></button>
                    </form>
                </div>
            </c:forEach>
        </c:when>
            <c:otherwise>
                <div><fmt:message key="tickets.not_found"/></div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
