<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.time.format.DateTimeFormatter, java.time.LocalDateTime" %>

<fmt:setBundle basename="messages" />

<html>
<head>
  <title>SportEvents</title>
  <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<div>
  <h1><fmt:message key="sport_events.list"/></h1>
  <button onclick="location.href='${pageContext.request.contextPath}/'">
    <fmt:message key="button.back"/>
  </button>
  <button onclick="location.href='${pageContext.request.contextPath}/create-sport-event'">
    <fmt:message key="button.add"/>
  </button>
  <div class="arena-container">
    <c:if test="${not empty requestScope.sport_events}">
      <c:forEach var="sport_event" items="${requestScope.sport_events}">
        <div class="arena-card">
          <div><fmt:message key="sport_event.eventName"/>: ${sport_event.eventName}</div>

          <c:set var="eventDateTime" value="${sport_event.eventDateTime}" />
          <c:choose>
            <c:when test="${not empty eventDateTime}">
              <%
                LocalDateTime eventDateTime = (LocalDateTime) pageContext.findAttribute("eventDateTime");
                String formattedDate = eventDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                String formattedTime = eventDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
              %>
              <c:set var="formattedDate" value="<%= formattedDate %>" />
              <c:set var="formattedTime" value="<%= formattedTime %>" />
              <div><fmt:message key="sport_event.eventDate"/>: ${formattedDate}</div>
              <div><fmt:message key="sport_event.eventTime"/>: ${formattedTime}</div>
            </c:when>
            <c:otherwise>
              <div><fmt:message key="sport_event.eventDate"/>: N/A</div>
              <div><fmt:message key="sport_event.eventTime"/>: N/A</div>
            </c:otherwise>
          </c:choose>

          <div><fmt:message key="sport_event.arena"/>: ${sport_event.arena.name}</div>
          <div><fmt:message key="sport_event.city"/>: ${sport_event.arena.city}</div>
          <form action="${pageContext.request.contextPath}/update-sport-event" method="get" style="display:inline;">
            <input type="hidden" name="id" value="${sport_event.id}"/>
            <button type="submit"><fmt:message key="button.update"/></button>
          </form>
          <form action="${pageContext.request.contextPath}/delete-sport-event?<%= request.getQueryString() %>"
                method="post" style="display:inline;">
            <input type="hidden" name="id" value="${sport_event.id}"/>
            <button type="submit"><fmt:message key="button.delete"/></button>
          </form>
        </div>
      </c:forEach>
    </c:if>
  </div>
</div>
</body>
</html>
