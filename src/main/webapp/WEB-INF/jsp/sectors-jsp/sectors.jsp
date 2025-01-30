<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../localization/localization.jsp" %>

<html>
<head>
    <title><fmt:message key="sectors.list"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
    <script src="<c:url value="/js/cookieUtils.js"/>"></script>
    <script src="<c:url value="/js/toggle-filter-form-script.js"/>"></script>
</head>
<body>
<%@ include file="../localization/language-switcher.jsp" %>
<div>
    <h1><fmt:message key="sectors.list"/></h1>

    <div class="filter-bar">
        <button type="button" onclick="toggleFilterForm()"><fmt:message key="setup.filter.button" /></button>
        <form action="${pageContext.request.contextPath}/admin/sectors" method="get">
            <div class="form-item">
                <label for="nameSortOrder"><fmt:message key="sector.sectorName"/></label>
                <select id="nameSortOrder" name="nameSortOrder" class="scrollable-dropdown">
                    <option value="">-- <fmt:message key="sorting" /> --</option>
                    <option value="ASC" ${requestScope.filter.nameSortOrder != null
                            && requestScope.filter.nameSortOrder == 'ASC' ? 'selected' : ''}>
                        <fmt:message key="sorting.asc" />
                    </option>
                    <option value="DESC" ${requestScope.filter.nameSortOrder != null
                            && requestScope.filter.nameSortOrder == 'DESC' ? 'selected' : ''}>
                        <fmt:message key="sorting.desc" />
                    </option>
                </select>
            </div>
            <div class="form-item">
                <label for="maxRowsNumbSortOrder"><fmt:message key="sector.maxRowsNumb"/></label>
                <select id="maxRowsNumbSortOrder" name="maxRowsNumbSortOrder" class="scrollable-dropdown">
                    <option value="">-- <fmt:message key="sorting" /> --</option>
                    <option value="ASC" ${requestScope.filter.maxRowsNumbSortOrder != null
                            && requestScope.filter.maxRowsNumbSortOrder == 'ASC' ? 'selected' : ''}>
                        <fmt:message key="sorting.asc" />
                    </option>
                    <option value="DESC" ${requestScope.filter.maxRowsNumbSortOrder != null
                            && requestScope.filter.maxRowsNumbSortOrder == 'DESC' ? 'selected' : ''}>
                        <fmt:message key="sorting.desc" />
                    </option>
                </select>
            </div>
            <div class="form-item">
                <label for="maxSeatsNumbSortOrder"><fmt:message key="sector.maxSeatsNumb"/></label>
                <select id="maxSeatsNumbSortOrder" name="maxSeatsNumbSortOrder" class="scrollable-dropdown">
                    <option value="">-- <fmt:message key="sorting" /> --</option>
                    <option value="ASC" ${requestScope.filter.maxSeatsNumbSortOrder != null
                            && requestScope.filter.maxSeatsNumbSortOrder == 'ASC' ? 'selected' : ''}>
                        <fmt:message key="sorting.asc" />
                    </option>
                    <option value="DESC" ${requestScope.filter.maxSeatsNumbSortOrder != null
                            && requestScope.filter.maxSeatsNumbSortOrder == 'DESC' ? 'selected' : ''}>
                        <fmt:message key="sorting.desc" />
                    </option>
                </select>
            </div>
            <div>
                <label for="displayPage"><fmt:message key="page.current"/>:
                    <input id="displayPage" type="number" value="${requestScope.sectors.metadata.page + 1}">
                    <input id="page" type="hidden" name="page" value="${requestScope.sectors.metadata.page}">
                </label>
                <label for="size"><fmt:message key="page.content.size"/>:
                    <input id="size" type="number" name="size" value="${requestScope.sectors.metadata.size}">
                </label>
            </div>
            <input type="hidden" name="arenaId" value="${param.arenaId}">
            <button type="submit"><fmt:message key="apply.filters"/></button>
        </form>
    </div>

    <button onclick="location.href='${pageContext.request.contextPath}/admin/arenas'">
        <fmt:message key="button.back"/>
    </button>
    <button onclick="location.href='${pageContext.request.contextPath}/admin/sectors/create?<%= request.getQueryString() %>'">
        <fmt:message key="button.add"/>
    </button>
    <div class="arena-container">
        <c:choose>
        <c:when test="${not empty requestScope.sectors.content}">
            <c:forEach var="sector" items="${requestScope.sectors.content}">
                <div class="arena-card">
                    <a href="${pageContext.request.contextPath}/admin/rows?arenaId=<%= request.getParameter("arenaId") %>&sectorId=${sector.id}">
                        <fmt:message key="sector.sectorName"/>: ${sector.sectorName}
                    </a>
                    <div><fmt:message key="sector.maxRowsNumb"/>: ${sector.maxRowsNumb}</div>
                    <div><fmt:message key="sector.availableRowsNumb"/>: ${sector.availableRowsNumb}</div>
                    <div><fmt:message key="sector.maxSeatsNumb"/>: ${sector.maxSeatsNumb}</div>
                    <div><fmt:message key="sector.availableSeatsNumb"/>: ${sector.availableSeatsNumb}</div>
                    <form action="${pageContext.request.contextPath}/admin/sectors/${sector.id}/update" method="get" style="display:inline;">
                        <input type="hidden" name="arenaId" value="${sector.arena.id}"/>
                        <button type="submit"><fmt:message key="button.update"/></button>
                    </form>
                    <form action="${pageContext.request.contextPath}/admin/sectors/${sector.id}/delete?<%= request.getQueryString() %>"
                          method="post" style="display:inline;">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <input type="hidden" name="id" value="${sector.id}"/>
                        <button type="submit"><fmt:message key="button.delete"/></button>
                    </form>
                </div>
            </c:forEach>
        </c:when>
            <c:otherwise>
                <div><fmt:message key="sectors.not_found"/></div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
