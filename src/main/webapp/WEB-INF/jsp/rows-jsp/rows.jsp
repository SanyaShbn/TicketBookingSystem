<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="messages" />

<html>
<head>
    <title>Rows</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
    <script src="<c:url value="/js/toggle-filter-form-script.js"/>"></script>
</head>
<body>
<div>
    <h1><fmt:message key="rows.list"/></h1>

    <div class="filter-bar">
        <button type="button" onclick="toggleFilterForm()">Настроить фильтр</button>
        <form action="${pageContext.request.contextPath}/admin/rows" method="get">
            <div class="form-item">
                <label for="rowNumberOrder"><fmt:message key="row.rowNumber"/></label>
                <select id="rowNumberOrder" name="rowNumberOrder" class="scrollable-dropdown">
                    <option value="">-- Сортировка --</option>
                    <option value="ASC" ${param.rowNumberOrder != null
                            && param.rowNumberOrder == 'ASC' ? 'selected' : ''}>
                        По возрастанию
                    </option>
                    <option value="DESC" ${param.nameSortOrder != null
                            && param.rowNumberOrder == 'DESC' ? 'selected' : ''}>
                        По убыванию
                    </option>
                </select>
            </div>
            <div class="form-item">
                <label for="seatsNumbOrder"><fmt:message key="row.seatsNumb"/></label>
                <select id="seatsNumbOrder" name="seatsNumbOrder" class="scrollable-dropdown">
                    <option value="">-- Сортировка --</option>
                    <option value="ASC" ${param.seatsNumbOrder != null
                            && param.seatsNumbOrder == 'ASC' ? 'selected' : ''}>
                        По возрастанию
                    </option>
                    <option value="DESC" ${param.seatsNumbOrder != null
                            && param.seatsNumbOrder == 'DESC' ? 'selected' : ''}>
                        По убыванию
                    </option>
                </select>
            </div>
            <input type="hidden" name="sectorId" value="${param.sectorId}">
            <input type="hidden" name="arenaId" value="${param.arenaId}">
            <button type="submit"><fmt:message key="apply.filters"/></button>
        </form>
    </div>

    <button onclick="location.href='${pageContext.request.contextPath}/admin/sectors?arenaId=<%= request.getParameter("arenaId") %>'">
        <fmt:message key="button.back"/>
    </button>
    <button onclick="location.href='${pageContext.request.contextPath}/admin/create-row?<%= request.getQueryString() %>'">
        <fmt:message key="button.add"/>
    </button>
    <div class="arena-container">
        <c:choose>
        <c:when test="${not empty requestScope.rows}">
            <c:forEach var="row" items="${requestScope.rows}">
                <div class="arena-card">
                    <div><fmt:message key="row.rowNumber"/>: ${row.rowNumber}</div>
                    <div><fmt:message key="row.seatsNumb"/>: ${row.seatsNumb}</div>
                    <form action="${pageContext.request.contextPath}/admin/update-row" method="get" style="display:inline;">
                        <input type="hidden" name="id" value="${row.id}"/>
                        <input type="hidden" name="sectorId" value="${row.sector.id}"/>
                        <input type="hidden" name="arenaId" value="${row.sector.arena.id}"/>
                        <button type="submit"><fmt:message key="button.update"/></button>
                    </form>
                    <form action="${pageContext.request.contextPath}/admin/delete-row?<%= request.getQueryString() %>"
                          method="post" style="display:inline;">
                        <input type="hidden" name="id" value="${row.id}"/>
                        <button type="submit"><fmt:message key="button.delete"/></button>
                    </form>
                </div>
            </c:forEach>
            <div class="pagination" style="padding-top: 45px">
                <c:if test="${requestScope.page > 1}">
                    <a href="${pageContext.request.contextPath}/admin/rows?arenaId=${param.arenaId}&sectorId=${param.sectorId}&page=${param.page - 1}"
                       class="pagination-arrow">&laquo; <fmt:message key="page.previous"/></a>
                </c:if>
                <c:if test="${requestScope.rows.size() eq requestScope.limit}">
                    <a href="${pageContext.request.contextPath}/admin/rows?arenaId=${param.arenaId}&sectorId=${param.sectorId}&page=${param.page != null
          ? param.page + 1 : 2}" class="pagination-arrow"><fmt:message key="page.next"/> &raquo;</a>
                </c:if>
            </div>
        </c:when>
            <c:otherwise>
                <div><fmt:message key="rows.not_found"/></div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>