<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="${locale}" />
<fmt:setBundle basename="messages" />
<style>
    .language-switcher {
        position: fixed;
        top: 10px;
        right: 10px;
        z-index: 1000;
    }

    .language-switcher select {
        padding: 5px 10px;
        font-size: 16px;
        font-weight: bold;
        color: #0073e6;
        border: none;
        background: #f5f5f5;
        cursor: pointer;
        border-radius: 5px;
    }

    .language-switcher select:hover {
        background: #e0e0e0;
    }
</style>
<div class="language-switcher">
    <select onchange="window.location.href=this.value;">
        <option value="<c:url value='${pageContext.request.contextPath}?lang=en'/>" <c:if test="${locale == 'en'}">selected</c:if>>English</option>
        <option value="<c:url value='${pageContext.request.contextPath}?lang=ru'/>" <c:if test="${locale == 'ru'}">selected</c:if>>Русский</option>
    </select>
</div>
