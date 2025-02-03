<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../localization/localization.jsp" %>

<html>
<head>
    <title><fmt:message key="purchase.commitment"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/styles.css' />">
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/card-details.css' />">
</head>
<body>
<%@ include file="../localization/language-switcher.jsp" %>
<h1><fmt:message key="purchase.commitment"/></h1>
<form action="${pageContext.request.contextPath}/purchase" method="post">

    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

    <div class="form-item">
        <label for="cardNumber"><fmt:message key="card.numb"/>:</label>
        <input type="text" id="cardNumber" name="cardNumber" maxlength="16" placeholder="1234 5678 9012 3456" required>
    </div>
    <div class="form-item">
        <label for="cardExpiry"><fmt:message key="card.validity.period"/>:</label>
        <input type="text" id="cardExpiry" name="cardExpiry" maxlength="5" placeholder="MM/YY" required>
    </div>
    <div class="form-item">
        <label for="cardCVC"><fmt:message key="card.cvc"/>:</label>
        <input type="password" id="cardCVC" name="cardCVC" maxlength="3" placeholder="123" required>
    </div>
    <button type="submit"><fmt:message key="confirm.purchase"/></button>
</form>
<c:if test="${not empty requestScope.errors}">
    <div class="error">
        <c:forEach var="error" items="${requestScope.errors}">
            <span>${error.defaultMessage}</span>
            <br/>
        </c:forEach>
    </div>
</c:if>
</body>
</html>
