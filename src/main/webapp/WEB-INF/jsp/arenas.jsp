<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="messages" />

<html>
<head>
    <title>Arenas</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<div>
    <h1><fmt:message key="arenas.list"/></h1>
    <button onclick="location.href='${pageContext.request.contextPath}/create-arena'">Создать</button>
    <div class="arena-container">
        <c:if test="${not empty requestScope.arenas}">
            <c:forEach var="arena" items="${requestScope.arenas}">
                <div class="arena-card">
                    <div><fmt:message key="arena.name"/>: ${arena.name}</div>
                    <div><fmt:message key="arena.city"/>: ${arena.city}</div>
                    <div><fmt:message key="arena.capacity"/>: ${arena.capacity}</div>
<%--                    <c:if test="${sessionScope.user.role == 'ADMIN'}">--%>
                        <form action="${pageContext.request.contextPath}/update-arena" method="get" style="display:inline;">
                            <input type="hidden" name="id" value="${arena.id}"/>
                            <button type="submit"><fmt:message key="button.update"/></button>
                        </form>
                        <form action="${pageContext.request.contextPath}/delete-arena" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${arena.id}"/>
                            <button type="submit"><fmt:message key="button.delete"/></button>
                        </form>
<%--                    </c:if>--%>
                </div>
            </c:forEach>
        </c:if>
    </div>
</div>
</body>
</html>
