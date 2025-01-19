<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/language-switcher.css"/>">
</head>
<body>
<div class="language-switcher">
    <select onchange="window.location.href=this.value;">
        <option value="<c:url value='${pageContext.request.contextPath}?lang=en'/>" <c:if test="${locale == 'en'}">selected</c:if>>English</option>
        <option value="<c:url value='${pageContext.request.contextPath}?lang=ru'/>" <c:if test="${locale == 'ru'}">selected</c:if>>Русский</option>
    </select>
</div>
</body>
</html>
