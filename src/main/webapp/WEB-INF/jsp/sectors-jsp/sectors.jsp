<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="messages" />

<html>
<head>
    <title>Sectors</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
    <script src="<c:url value="/js/toggleFilterFormScript.js"/>"></script>
</head>
<body>
<div>
    <h1><fmt:message key="sectors.list"/></h1>

    <div class="filter-bar">
        <button type="button" onclick="toggleFilterForm()">Настроить фильтр</button>
        <form action="${pageContext.request.contextPath}/sectors" method="get">
            <div class="form-item">
                <label for="nameSortOrder"><fmt:message key="sector.sectorName"/></label>
                <select id="nameSortOrder" name="nameSortOrder" class="scrollable-dropdown">
                    <option value="">-- Сортировка --</option>
                    <option value="ASC" ${param.nameSortOrder != null
                            && param.nameSortOrder == 'ASC' ? 'selected' : ''}>
                        По возрастанию
                    </option>
                    <option value="DESC" ${param.nameSortOrder != null
                            && param.nameSortOrder == 'DESC' ? 'selected' : ''}>
                        По убыванию
                    </option>
                </select>
            </div>
            <div class="form-item">
                <label for="maxRowsNumbSortOrder"><fmt:message key="sector.maxRowsNumb"/></label>
                <select id="maxRowsNumbSortOrder" name="maxRowsNumbSortOrder" class="scrollable-dropdown">
                    <option value="">-- Сортировка --</option>
                    <option value="ASC" ${param.maxRowsNumbSortOrder != null
                            && param.maxRowsNumbSortOrder == 'ASC' ? 'selected' : ''}>
                        По возрастанию
                    </option>
                    <option value="DESC" ${param.maxRowsNumbSortOrder != null
                            && param.maxRowsNumbSortOrder == 'DESC' ? 'selected' : ''}>
                        По убыванию
                    </option>
                </select>
            </div>
            <div class="form-item">
                <label for="maxSeatsNumbSortOrder"><fmt:message key="sector.maxSeatsNumb"/></label>
                <select id="maxSeatsNumbSortOrder" name="maxSeatsNumbSortOrder" class="scrollable-dropdown">
                    <option value="">-- Сортировка --</option>
                    <option value="ASC" ${param.maxSeatsNumbSortOrder != null
                            && param.maxSeatsNumbSortOrder == 'ASC' ? 'selected' : ''}>
                        По возрастанию
                    </option>
                    <option value="DESC" ${param.maxSeatsNumbSortOrder != null
                            && param.maxSeatsNumbSortOrder == 'DESC' ? 'selected' : ''}>
                        По убыванию
                    </option>
                </select>
            </div>
            <input type="hidden" name="arenaId" value="${param.arenaId}">
            <button type="submit"><fmt:message key="apply.filters"/></button>
        </form>
    </div>

    <button onclick="location.href='${pageContext.request.contextPath}/arenas'">
        <fmt:message key="button.back"/>
    </button>
    <button onclick="location.href='${pageContext.request.contextPath}/create-sector?<%= request.getQueryString() %>'">
        <fmt:message key="button.add"/>
    </button>
    <div class="arena-container">
        <c:choose>
        <c:when test="${not empty requestScope.sectors}">
            <c:forEach var="sector" items="${requestScope.sectors}">
                <div class="arena-card">
                    <a href="${pageContext.request.contextPath}/rows?arenaId=<%= request.getParameter("arenaId") %>&sectorId=${sector.id}">
                        <fmt:message key="sector.sectorName"/>: ${sector.sectorName}
                    </a>
                    <div><fmt:message key="sector.maxRowsNumb"/>: ${sector.maxRowsNumb}</div>
                    <div><fmt:message key="sector.availableRowsNumb"/>: ${sector.availableRowsNumb}</div>
                    <div><fmt:message key="sector.maxSeatsNumb"/>: ${sector.maxSeatsNumb}</div>
                    <div><fmt:message key="sector.availableSeatsNumb"/>: ${sector.availableSeatsNumb}</div>
                    <form action="${pageContext.request.contextPath}/update-sector" method="get" style="display:inline;">
                        <input type="hidden" name="id" value="${sector.id}"/>
                        <input type="hidden" name="arenaId" value="${sector.arena.id}"/>
                        <button type="submit"><fmt:message key="button.update"/></button>
                    </form>
                    <form action="${pageContext.request.contextPath}/delete-sector?<%= request.getQueryString() %>"
                          method="post" style="display:inline;">
                        <input type="hidden" name="id" value="${sector.id}"/>
                        <button type="submit"><fmt:message key="button.delete"/></button>
                    </form>
                </div>
            </c:forEach>
            <div class="pagination" style="padding-top: 100px">
                <c:if test="${requestScope.page > 1}">
                    <a href="${pageContext.request.contextPath}/sectors?arenaId=${param.arenaId}&page=${param.page - 1}"
                       class="pagination-arrow">&laquo; <fmt:message key="page.previous"/></a>
                </c:if>
                <c:if test="${requestScope.sectors.size() eq requestScope.limit}">
                    <a href="${pageContext.request.contextPath}/sectors?arenaId=${param.arenaId}&page=${param.page != null
          ? param.page + 1 : 2}" class="pagination-arrow"><fmt:message key="page.next"/> &raquo;</a>
                </c:if>
            </div>
        </c:when>
            <c:otherwise>
                <div><fmt:message key="sectors.not_found"/></div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
