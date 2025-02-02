<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="messages" />

<html>
<head>
    <title>Ticket Booking System</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<header>
    <h1><fmt:message key="welcome.message"/></h1>
    <nav>
        <ul>
            <li><a href="<c:url value="/arenas"/>"><fmt:message key="arenas.nav.link"/></a></li>
            <li><a href="<c:url value="/sport_events"/>"><fmt:message key="events.nav.link"/></a></li>
            <li><a href="<c:url value="/view_available_events"/>"><fmt:message key="tickets.nav.link"/></a></li>
        </ul>
    </nav>
</header>
</body>
</html>