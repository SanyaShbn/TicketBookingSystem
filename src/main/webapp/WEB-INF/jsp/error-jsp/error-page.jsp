<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../localization/localization.jsp" %>

<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="error.page.title"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<div>
<button onclick="history.back()">
    <fmt:message key="button.back"/>
</button>
<div class="error-container">
    <div class="error-message">
        <c:if test="${not empty requestScope.errors}">
            <div class="error">
                <c:forEach var="error" items="${requestScope.errors}">
                    <span>${error.message}</span>
                    <br/>
                </c:forEach>
            </div>
        </c:if>
    </div>
</div>
</div>
</body>
</html>
