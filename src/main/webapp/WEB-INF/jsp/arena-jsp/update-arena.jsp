<%--@elvariable id="arena" type="com.sun.org.apache.xml.internal.security.signature.Manifest"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../localization/localization.jsp" %>

<html>
<head>
    <title><fmt:message key="update.arena.title" /></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<%@ include file="../localization/language-switcher.jsp" %>
<div class="form-container">
    <h1><fmt:message key="update.arena.title" /></h1>
    <form action="${pageContext.request.contextPath}/admin/arenas/${arena.id}/update" method="post">
        <input type="hidden" name="id" value="${arena.id}">
        <label for="name"><fmt:message key="arena.name" />:</label>
        <input type="text" id="name" name="name" value="${arena.name}" required>
        <label for="city"><fmt:message key="arena.city" />:</label>
        <input type="text" id="city" name="city" value="${arena.city}" required>
        <label for="capacity"><fmt:message key="arena.capacity" />:</label>
        <input type="text" id="capacity" name="capacity" value="${arena.capacity}" required>
        <div class="button-group">
            <button type="button" onclick="location.href='${pageContext.request.contextPath}/admin/arenas';">
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