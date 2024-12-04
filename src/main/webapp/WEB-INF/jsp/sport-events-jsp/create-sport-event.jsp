<%--@elvariable id="arena" type="com.sun.org.apache.xml.internal.security.signature.Manifest"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="messages" />

<html>
<head>
    <title><fmt:message key="create.sport.event.title" /></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<div class="form-container">
    <h1><fmt:message key="create.sport.event.title" /></h1>
    <form action="${pageContext.request.contextPath}/create-sport-event?<%= request.getQueryString() %>" method="post">
        <label for="eventName"><fmt:message key="sport_event.eventName" />:</label>
        <input type="text" id="eventName" name="eventName" required>
        <label for="eventDateTime"><fmt:message key="sport_event.eventDateTime" />:</label>
        <input type="datetime-local" id="eventDateTime" name="eventDateTime" required>
        <div class="button-group">
            <button type="button"
                    onclick="location.href='${pageContext.request.contextPath}/sport_events';">
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