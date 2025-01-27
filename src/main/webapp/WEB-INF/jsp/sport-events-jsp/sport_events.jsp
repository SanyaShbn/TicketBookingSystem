<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.time.format.DateTimeFormatter, java.time.LocalDateTime" %>
<%@ include file="../localization/localization.jsp" %>

<html>
<head>
  <title><fmt:message key="sport_events.list"/></title>
  <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
  <script src="<c:url value="/js/cookieUtils.js"/>"></script>
  <script src="<c:url value="/js/toggle-filter-form-script.js"/>"></script>
</head>
<body>
<%@ include file="../localization/language-switcher.jsp" %>
<div>
  <h1><fmt:message key="sport_events.list"/></h1>

  <!-- Filter Bar -->
  <div class="filter-bar">
    <button type="button" onclick="toggleFilterForm()"><fmt:message key="setup.filter.button" /></button>
    <form action="${pageContext.request.contextPath}/admin/sport_events" method="get">
      <div class="form-item">
        <label for="startDate"><fmt:message key="sport.event.startDate"/></label>
        <input type="datetime-local" step="60" id="startDate" name="startDate" value="${requestScope.filter.startDate()}">
      </div>
      <div class="form-item">
        <label for="endDate"><fmt:message key="sport.event.endDate"/></label>
        <input type="datetime-local" step="60" id="endDate" name="endDate" value="${requestScope.filter.endDate()}">
      </div>

      <div class="form-item">
      <label for="arenaId"><fmt:message key="sport_event.arena" />:</label>
      <select id="arenaId" name="arenaId" class="scrollable-dropdown">
        <option value="">-- <fmt:message key="choose.arena"/> --</option>
        <c:forEach var="arena" items="${arenas}">
          <option value="${arena.id}" ${requestScope.filter.arenaId() != null
                  && requestScope.filter.arenaId() == arena.id ? 'selected' : ''}>
              ${arena.name}. ${arena.city}.
          </option>
        </c:forEach>
      </select>
      </div>

      <div class="form-item">
        <label for="sortOrder"><fmt:message key="sport.event.sortOrder"/></label>
        <select id="sortOrder" name="sortOrder" class="scrollable-dropdown">
          <option value="">-- <fmt:message key="sorting" /> --</option>
          <option value="ASC" ${requestScope.filter.sortOrder != null
                  && requestScope.filter.sortOrder == 'ASC' ? 'selected' : ''}>
            <fmt:message key="sorting.asc" />
          </option>
          <option value="DESC" ${requestScope.filter.sortOrder != null
                  && requestScope.filter.sortOrder == 'DESC' ? 'selected' : ''}>
            <fmt:message key="sorting.desc" />
          </option>
        </select>
      </div>
      <div>
        <label for="displayPage"><fmt:message key="page.current"/>:
          <input id="displayPage" type="number" value="${requestScope.sport_events.metadata.page + 1}">
          <input id="page" type="hidden" name="page" value="${requestScope.sport_events.metadata.page}">
        </label>
        <label for="size"><fmt:message key="page.content.size"/>:
          <input id="size" type="number" name="size" value="${requestScope.sport_events.metadata.size}">
        </label>
      </div>
      <button type="submit"><fmt:message key="apply.filters"/></button>
    </form>
  </div>

  <button onclick="location.href='${pageContext.request.contextPath}/admin'">
    <fmt:message key="button.back"/>
  </button>
  <button onclick="location.href='${pageContext.request.contextPath}/admin/sport_events/create'">
    <fmt:message key="button.add"/>
  </button>
  <div class="arena-container">
    <c:choose>
    <c:when test="${not empty requestScope.sport_events.content}">
      <c:forEach var="sport_event" items="${requestScope.sport_events.content}">
        <div class="arena-card">
          <a href="${pageContext.request.contextPath}/admin/tickets?eventId=${sport_event.id}">
            <fmt:message key="sport_event.eventName"/>: ${sport_event.eventName}
          </a>

          <c:set var="eventDateTime" value="${sport_event.eventDateTime}" />
          <c:choose>
            <c:when test="${not empty eventDateTime}">
              <%
                LocalDateTime eventDateTime = (LocalDateTime) pageContext.findAttribute("eventDateTime");
                String formattedDate = eventDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                String formattedTime = eventDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
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
          <form action="${pageContext.request.contextPath}/admin/sport_events/${sport_event.id}/update" method="get" style="display:inline;">
            <input type="hidden" name="id" value="${sport_event.id}"/>
            <button type="submit"><fmt:message key="button.update"/></button>
          </form>
          <form action="${pageContext.request.contextPath}/admin/sport_events/${sport_event.id}/delete?<%= request.getQueryString() %>"
                method="post" style="display:inline;">
            <input type="hidden" name="id" value="${sport_event.id}"/>
            <button type="submit"><fmt:message key="button.delete"/></button>
          </form>
        </div>
      </c:forEach>
    </c:when>
      <c:otherwise>
        <div><fmt:message key="sport_events.not_found"/></div>
      </c:otherwise>
    </c:choose>
  </div>
</div>
</body>
</html>