<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="../localization/localization.jsp" %>

<html>
<head>
    <title><fmt:message key="rows.list"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
    <script src="<c:url value="/js/cookieUtils.js"/>"></script>
    <script src="<c:url value="/js/toggle-filter-form-script.js"/>"></script>
</head>
<body>
<%@ include file="../localization/language-switcher.jsp" %>
<div>
    <h1><fmt:message key="rows.list"/></h1>

    <div class="filter-bar">
        <button type="button" onclick="toggleFilterForm()"><fmt:message key="setup.filter.button" /></button>
        <form action="${pageContext.request.contextPath}/admin/rows" method="get">
            <div class="form-item">
                <label for="rowNumberOrder"><fmt:message key="row.rowNumber"/></label>
                <select id="rowNumberOrder" name="rowNumberOrder" class="scrollable-dropdown">
                    <option value="">-- <fmt:message key="sorting" /> --</option>
                    <option value="ASC" ${requestScope.filter.rowNumberOrder != null
                            && requestScope.filter.rowNumberOrder == 'ASC' ? 'selected' : ''}>
                        <fmt:message key="sorting.asc" />
                    </option>
                    <option value="DESC" ${requestScope.filter.rowNumberOrder != null
                            && requestScope.filter.rowNumberOrder == 'DESC' ? 'selected' : ''}>
                        <fmt:message key="sorting.desc" />
                    </option>
                </select>
            </div>
            <div class="form-item">
                <label for="seatsNumbOrder"><fmt:message key="row.seatsNumb"/></label>
                <select id="seatsNumbOrder" name="seatsNumbOrder" class="scrollable-dropdown">
                    <option value="">-- <fmt:message key="sorting" /> --</option>
                    <option value="ASC" ${requestScope.filter.seatsNumbOrder != null
                            && requestScope.filter.seatsNumbOrder == 'ASC' ? 'selected' : ''}>
                        <fmt:message key="sorting.asc" />
                    </option>
                    <option value="DESC" ${requestScope.filter.seatsNumbOrder != null
                            && requestScope.filter.seatsNumbOrder == 'DESC' ? 'selected' : ''}>
                        <fmt:message key="sorting.desc" />
                    </option>
                </select>
            </div>
            <div>
                <label for="displayPage"><fmt:message key="page.current"/>:
                    <input id="displayPage" type="number" value="${requestScope.rows.metadata.page + 1}">
                    <input id="page" type="hidden" name="page" value="${requestScope.rows.metadata.page}">
                </label>
                <label for="size"><fmt:message key="page.content.size"/>:
                    <input id="size" type="number" name="size" value="${requestScope.rows.metadata.size}">
                </label>
            </div>
            <input type="hidden" name="sectorId" value="${param.sectorId}">
            <input type="hidden" name="arenaId" value="${param.arenaId}">
            <button type="submit"><fmt:message key="apply.filters"/></button>
        </form>
    </div>

    <button onclick="location.href='${pageContext.request.contextPath}/admin/sectors?arenaId=<%= request.getParameter("arenaId") %>'">
        <fmt:message key="button.back"/>
    </button>
    <button onclick="location.href='${pageContext.request.contextPath}/admin/rows/create?<%= request.getQueryString() %>'">
        <fmt:message key="button.add"/>
    </button>
    <div class="arena-container">
        <c:choose>
        <c:when test="${not empty requestScope.rows.content}">
            <c:forEach var="row" items="${requestScope.rows.content}">
                <div class="arena-card">
                    <div><fmt:message key="row.rowNumber"/>: ${row.rowNumber}</div>
                    <div><fmt:message key="row.seatsNumb"/>: ${row.seatsNumb}</div>
                    <form action="${pageContext.request.contextPath}/admin/rows/${row.id}/update" method="get" style="display:inline;">
                        <input type="hidden" name="sectorId" value="${row.sector.id}"/>
                        <input type="hidden" name="arenaId" value="${param.arenaId}"/>
                        <button type="submit"><fmt:message key="button.update"/></button>
                    </form>
                    <form action="${pageContext.request.contextPath}/admin/rows/${row.id}/delete?<%= request.getQueryString() %>"
                          method="post" style="display:inline;">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <input type="hidden" name="id" value="${row.id}"/>
                        <button type="submit"><fmt:message key="button.delete"/></button>
                    </form>
                </div>
            </c:forEach>
        </c:when>
            <c:otherwise>
                <div><fmt:message key="rows.not_found"/></div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>