<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../localization/localization.jsp" %>

<html>
<head>
    <title><fmt:message key="arenas.list"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
    <script src="<c:url value="/js/cookieUtils.js"/>"></script>
    <script src="<c:url value="/js/toggle-filter-form-script.js"/>"></script>
</head>
<body>
<%@ include file="../localization/language-switcher.jsp" %>
<div>
    <h1><fmt:message key="arenas.list"/></h1>

    <div class="filter-bar">
        <button type="button" onclick="toggleFilterForm()"><fmt:message key="setup.filter.button" /></button>
        <form action="${pageContext.request.contextPath}/admin/arenas" method="get">
            <div class="form-item">
                <label for="city"><fmt:message key="arena.city" />:</label>
                <input type="text" id="city" name="city" value="${requestScope.filter.city()}">
            </div>

            <div class="form-item">
                <label for="capacitySortOrder"><fmt:message key="arena.capacitySortOrder"/></label>
                <select id="capacitySortOrder" name="capacitySortOrder" class="scrollable-dropdown">
                    <option value="">-- <fmt:message key="sorting" /> --</option>
                    <option value="ASC" ${requestScope.filter.capacitySortOrder != null
                    && requestScope.filter.capacitySortOrder.equals('ASC') ? 'selected' : ''}>
                        <fmt:message key="sorting.asc" />
                    </option>
                    <option value="DESC" ${requestScope.filter.capacitySortOrder != null
                    && requestScope.filter.capacitySortOrder.equals('DESC') ? 'selected' : ''}>
                        <fmt:message key="sorting.desc" />
                    </option>
                </select>
            </div>
            <div class="form-item">
                <label for="seatsNumbSortOrder"><fmt:message key="arena.seatsNumbSortOrder"/></label>
                <select id="seatsNumbSortOrder" name="seatsNumbSortOrder" class="scrollable-dropdown">
                    <option value="">-- <fmt:message key="sorting" /> --</option>
                    <option value="ASC" ${requestScope.filter.seatsNumbSortOrder != null
                            && requestScope.filter.seatsNumbSortOrder.equals('ASC') ? 'selected' : ''}>
                        <fmt:message key="sorting.asc" />
                    </option>
                    <option value="DESC" ${requestScope.filter.seatsNumbSortOrder != null
                            && requestScope.filter.seatsNumbSortOrder.equals('DESC') ? 'selected' : ''}>
                        <fmt:message key="sorting.desc" />
                    </option>
                </select>
            </div>
            <div>
                <label for="page"><fmt:message key="page.current"/>:
                    <input id="displayPage" type="number" value="${requestScope.arenas.metadata.page + 1}">
                    <input id="page" type="hidden" name="page" value="${requestScope.arenas.metadata.page}">
                </label>
                <label for="size"><fmt:message key="page.content.size"/>:
                    <input id="size" type="number" name="size" value="${requestScope.arenas.metadata.size}">
                </label>
            </div>

            <button type="submit"><fmt:message key="apply.filters"/></button>
        </form>
    </div>

    <button onclick="location.href='${pageContext.request.contextPath}/admin'">
        <fmt:message key="button.back"/>
    </button>
    <button onclick="location.href='${pageContext.request.contextPath}/admin/arenas/create'">
        <fmt:message key="button.create"/>
    </button>
    <div class="arena-container">
        <c:choose>
        <c:when test="${not empty requestScope.arenas.content}">
            <c:forEach var="arena" items="${requestScope.arenas.content}">
                <div class="arena-card">
                    <a href="${pageContext.request.contextPath}/admin/sectors?arenaId=${arena.id}">
                        <fmt:message key="arena.name"/>: ${arena.name}
                    </a>
                    <div><fmt:message key="arena.city"/>: ${arena.city}</div>
                    <div><fmt:message key="arena.capacity"/>: ${arena.capacity}</div>
                    <div><fmt:message key="arena.generalSeatsNumb"/>: ${arena.generalSeatsNumb}</div>
                        <form action="${pageContext.request.contextPath}/admin/arenas/${arena.id}/update" method="get" style="display:inline;">
                            <button type="submit"><fmt:message key="button.update"/></button>
                        </form>
                        <form action="${pageContext.request.contextPath}/admin/arenas/${arena.id}/delete" method="post" style="display:inline;">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <input type="hidden" name="id" value="${arena.id}"/>
                            <button type="submit"><fmt:message key="button.delete"/></button>
                        </form>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <div><fmt:message key="arenas.not_found"/></div>
        </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
