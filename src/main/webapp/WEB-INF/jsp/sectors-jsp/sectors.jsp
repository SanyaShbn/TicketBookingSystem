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
    <h1><fmt:message key="sectors.list"/></h1>
    <button onclick="location.href='${pageContext.request.contextPath}/create-sector?<%= request.getQueryString() %>'">
        <fmt:message key="button.add"/>
    </button>
    <div class="arena-container">
        <c:if test="${not empty requestScope.sectors}">
            <c:forEach var="sector" items="${requestScope.sectors}">
                <div class="arena-card">
                    <div><fmt:message key="sector.sectorName"/>: ${sector.sectorName}</div>
                    <form action="${pageContext.request.contextPath}/update-sector" method="get" style="display:inline;">
                        <input type="hidden" name="id" value="${sector.id}"/>
                        <input type="hidden" name="arenaId" value="${sector.arena.id}"/>
                        <button type="submit"><fmt:message key="button.update"/></button>
                    </form>
                    <form action="${pageContext.request.contextPath}/delete-sector?<%= request.getQueryString() %>" method="post" style="display:inline;">
                        <input type="hidden" name="id" value="${sector.id}"/>
                        <button type="submit"><fmt:message key="button.delete"/></button>
                    </form>
                </div>
            </c:forEach>
        </c:if>
    </div>
</div>
</body>
</html>
