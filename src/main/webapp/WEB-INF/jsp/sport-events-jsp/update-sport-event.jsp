<%--@elvariable id="arena" type="com.sun.org.apache.xml.internal.security.signature.Manifest"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../localization/localization.jsp" %>

<html>
<head>
    <title><fmt:message key="update.sport.event.title" /></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<%@ include file="../localization/language-switcher.jsp" %>
<div class="form-container">
    <h1><fmt:message key="update.sport.event.title" /></h1>
    <form action="${pageContext.request.contextPath}/admin/sport_events/${sport_event.id}/update" method="post">
        <input type="hidden" name="id" value="${sport_event.id}">
        <label for="eventName"><fmt:message key="sport_event.eventName" />:</label>
        <input type="text" id="eventName" name="eventName" value="${sport_event.eventName}" required>
        <label for="eventDateTime"><fmt:message key="sport_event.eventDateTime" />:</label>
        <input type="datetime-local" step="60" id="eventDateTime" name="eventDateTime"
               value="${sport_event.eventDateTime}" required>

        <label for="arenaId"><fmt:message key="sport_event.arena" />:</label>
        <select id="arenaId" name="arenaId" class="scrollable-dropdown" required>
            <c:forEach var="arena" items="${arenas}">
                <option value="${arena.id}" ${arena.id == sport_event.arena.id ? 'selected' : ''}>
                        ${arena.name}. ${arena.city}. Вместимость: ${arena.capacity} чел.
                </option>
            </c:forEach>
        </select>

        <div class="button-group">
            <button type="button"
                    onclick="location.href='${pageContext.request.contextPath}/admin/sport_events';">
                <fmt:message key="button.back" />
            </button>
            <button type="submit"><fmt:message key="button.save" /></button>
        </div>
    </form>

    <c:if test="${not empty requestScope.errors}">
        <div class="error">
            <c:forEach var="error" items="${requestScope.errors}">
                <span>${error.defaultMessage}</span>
                <br/>
            </c:forEach>
        </div>
    </c:if>
</div>
</body>
</html>