<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="localization/localization.jsp" %>

<html>
<head>
    <title><fmt:message key="admin.panel.page.title"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<%@ include file="localization/language-switcher.jsp" %>
<header>
    <h1><fmt:message key="welcome.message"/></h1>
    <nav>
        <ul>
            <li><a href="<c:url value="/admin/arenas"/>"><fmt:message key="arenas.nav.link"/></a></li>
            <li><a href="<c:url value="/admin/sport_events"/>"><fmt:message key="events.nav.link"/></a></li>
        </ul>
    </nav>
    <div style="position: absolute; top: 60px; right: 20px;">
        <c:if test="${not empty sessionScope.user}">
            <form action="${pageContext.request.contextPath}/logout" method="post" style="display: inline;">
                <button id="logoutButton" type="submit" style="background-color: #ff0000; color: white; border: none; padding: 10px 20px;
                 border-radius: 5px; cursor: pointer;"><fmt:message key="logout"/></button>
            </form>
        </c:if>
    </div>
</header>
</body>
</html>