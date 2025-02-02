<%--@elvariable id="arena" type="com.sun.org.apache.xml.internal.security.signature.Manifest"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../localization/localization.jsp" %>

<html>
<head>
    <title><fmt:message key="update.sector.title" /></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<%@ include file="../localization/language-switcher.jsp" %>
<div class="form-container">
    <h1><fmt:message key="update.sector.title" /></h1>
    <form action="${pageContext.request.contextPath}/admin/sectors/${sector.id}/update" method="post">

        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

        <input type="hidden" name="arenaId" value="${arenaId}">

        <input type="hidden" name="id" value="${sector.id}">
        <label for="sectorName"><fmt:message key="sector.sectorName" />:</label>
        <input type="text" id="sectorName" name="sectorName" value="${sector.sectorName}" required>
        <label for="maxRowsNumb"><fmt:message key="sector.maxRowsNumb" />:</label>
        <input type="text" id="maxRowsNumb" name="maxRowsNumb" value="${sector.maxRowsNumb}" required>
        <label for="maxSeatsNumb"><fmt:message key="sector.maxSeatsNumb" />:</label>
        <input type="text" id="maxSeatsNumb" name="maxSeatsNumb" value="${sector.maxSeatsNumb}" required>
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
                <span>${error.defaultMessage}</span>
                <br/>
            </c:forEach>
        </div>
    </c:if>
</div>
</body>
</html>