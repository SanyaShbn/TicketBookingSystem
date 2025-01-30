<%--@elvariable id="arena" type="com.sun.org.apache.xml.internal.security.signature.Manifest"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../localization/localization.jsp" %>

<html>
<head>
    <title><fmt:message key="create.sector.title" /></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<%@ include file="../localization/language-switcher.jsp" %>
<div class="form-container">
    <h1><fmt:message key="create.sector.title" /></h1>
    <form action="${pageContext.request.contextPath}/admin/sectors/create" method="post">

        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

        <input type="hidden" name="arenaId" value="${arenaId}">

        <label for="sectorName"><fmt:message key="sector.sectorName" />:</label>
        <input type="text" id="sectorName" name="sectorName" required>
        <label for="maxRowsNumb"><fmt:message key="sector.maxRowsNumb" />:</label>
        <input type="text" id="maxRowsNumb" name="maxRowsNumb" required>
        <label for="maxSeatsNumb"><fmt:message key="sector.maxSeatsNumb" />:</label>
        <input type="text" id="maxSeatsNumb" name="maxSeatsNumb" required>
        <div class="button-group">
            <button type="button"
                    onclick="location.href='${pageContext.request.contextPath}/admin/sectors?<%= request.getQueryString() %>';">
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