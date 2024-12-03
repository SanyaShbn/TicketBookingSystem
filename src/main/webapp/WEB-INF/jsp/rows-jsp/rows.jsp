<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="messages" />

<html>
<head>
    <title>Sectors</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<div>
    <h1><fmt:message key="rows.list"/></h1>
    <button onclick="location.href='${pageContext.request.contextPath}/sectors?arenaId=<%= request.getParameter("arenaId") %>'">
        <fmt:message key="button.back"/>
    </button>
    <button onclick="location.href='${pageContext.request.contextPath}/create-row?<%= request.getQueryString() %>'">
        <fmt:message key="button.add"/>
    </button>
    <div class="arena-container">
        <c:if test="${not empty requestScope.rows}">
            <c:forEach var="row" items="${requestScope.rows}">
                <div class="arena-card">
                    <div><fmt:message key="row.rowNumber"/>: ${row.rowNumber}</div>
                    <div><fmt:message key="row.seatsNumb"/>: ${row.seatsNumb}</div>
                    <form action="${pageContext.request.contextPath}/update-row" method="get" style="display:inline;">
                        <input type="hidden" name="id" value="${row.id}"/>
                        <input type="hidden" name="sectorId" value="${row.sector.id}"/>
                        <input type="hidden" name="arenaId" value="${row.sector.arena.id}"/>
                        <button type="submit"><fmt:message key="button.update"/></button>
                    </form>
                    <form action="${pageContext.request.contextPath}/delete-row?<%= request.getQueryString() %>"
                          method="post" style="display:inline;">
                        <input type="hidden" name="id" value="${row.id}"/>
                        <button type="submit"><fmt:message key="button.delete"/></button>
                    </form>
                </div>
            </c:forEach>
        </c:if>
    </div>
</div>
</body>
</html>