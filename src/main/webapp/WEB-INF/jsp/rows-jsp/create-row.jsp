<%--@elvariable id="arena" type="com.sun.org.apache.xml.internal.security.signature.Manifest"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../localization/localization.jsp" %>

<html>
<head>
    <title><fmt:message key="create.row.title" /></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<%@ include file="../localization/language-switcher.jsp" %>
<div class="form-container">
    <h1><fmt:message key="create.row.title" /></h1>
    <form action="${pageContext.request.contextPath}/admin/rows/create" method="post">
        <input type="hidden" name="arenaId" value="${arenaId}">
        <input type="hidden" name="sectorId" value="${sectorId}">

        <label for="rowNumber"><fmt:message key="row.rowNumber" />:</label>
        <input type="text" id="rowNumber" name="rowNumber" required>
        <label for="seatsNumb"><fmt:message key="row.seatsNumb" />:</label>
        <input type="text" id="seatsNumb" name="seatsNumb" required>
        <div class="button-group">
            <button type="button"
                    onclick="location.href='${pageContext.request.contextPath}/admin/rows?<%= request.getQueryString() %>';">
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