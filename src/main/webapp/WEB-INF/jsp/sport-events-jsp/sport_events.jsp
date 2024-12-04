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
  <h1><fmt:message key="sport_events.list"/></h1>
  <button onclick="location.href='${pageContext.request.contextPath}/'">
    <fmt:message key="button.back"/>
  </button>
  <button onclick="location.href='${pageContext.request.contextPath}/create-sport-event?<%= request.getQueryString() %>'">
    <fmt:message key="button.add"/>
  </button>
  <div class="arena-container">
    <c:if test="${not empty requestScope.sport_events}">
      <c:forEach var="sport_event" items="${requestScope.sport_events}">
        <div class="arena-card">
          <div><fmt:message key="sport_event.eventName"/>: ${sport_event.eventName}</div>
          <div><fmt:message key="sport_event.eventDateTime"/>: ${sport_event.eventDateTime}</div>
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
