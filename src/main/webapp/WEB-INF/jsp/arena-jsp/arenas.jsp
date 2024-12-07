<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="messages" />

<html>
<head>
    <title>Arenas</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
    <script src="<c:url value="/js/toggleFilterFormScript.js"/>"></script>
</head>
<body>
<div>
    <h1><fmt:message key="arenas.list"/></h1>

    <div class="filter-bar">
        <button type="button" onclick="toggleFilterForm()">Настроить фильтр</button>
        <form action="${pageContext.request.contextPath}/arenas" method="get">
            <div class="form-item">
                <label for="city"><fmt:message key="arena.city" />:</label>
                <select id="city" name="city" class="scrollable-dropdown">
                    <option value="">-- Выберите город --</option>
                    <c:forEach var="city" items="${cities}">
                        <option value="${city}" ${param.city != null && param.city == city
                        ? 'selected' : ''}>
                                ${city}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-item">
                <label for="capacitySortOrder"><fmt:message key="arena.capacitySortOrder"/></label>
                <select id="capacitySortOrder" name="capacitySortOrder" class="scrollable-dropdown">
                    <option value="">-- Сортировка --</option>
                    <option value="ASC" ${param.capacitySortOrder != null
                    && param.capacitySortOrder == 'ASC' ? 'selected' : ''}>
                        По возрастанию
                    </option>
                    <option value="DESC" ${param.capacitySortOrder != null
                    && param.capacitySortOrder == 'DESC' ? 'selected' : ''}>
                        По убыванию
                    </option>
                </select>
            </div>
            <div class="form-item">
                <label for="seatsNumbSortOrder"><fmt:message key="arena.seatsNumbSortOrder"/></label>
                <select id="seatsNumbSortOrder" name="seatsNumbSortOrder" class="scrollable-dropdown">
                    <option value="">-- Сортировка --</option>
                    <option value="ASC" ${param.seatsNumbSortOrder != null
                            && param.seatsNumbSortOrder == 'ASC' ? 'selected' : ''}>
                        По возрастанию
                    </option>
                    <option value="DESC" ${param.seatsNumbSortOrder != null
                            && param.seatsNumbSortOrder == 'DESC' ? 'selected' : ''}>
                        По убыванию
                    </option>
                </select>
            </div>

            <button type="submit"><fmt:message key="apply.filters"/></button>
        </form>
    </div>

    <button onclick="location.href='${pageContext.request.contextPath}/'">
        <fmt:message key="button.back"/>
    </button>
    <button onclick="location.href='${pageContext.request.contextPath}/create-arena'">
        <fmt:message key="button.create"/>
    </button>
    <div class="arena-container">
        <c:choose>
        <c:when test="${not empty requestScope.arenas}">
            <c:forEach var="arena" items="${requestScope.arenas}">
                <div class="arena-card">
                    <a href="${pageContext.request.contextPath}/sectors?arenaId=${arena.id}">
                        <fmt:message key="arena.name"/>: ${arena.name}
                    </a>
                    <div><fmt:message key="arena.city"/>: ${arena.city}</div>
                    <div><fmt:message key="arena.capacity"/>: ${arena.capacity}</div>
                    <div><fmt:message key="arena.generalSeatsNumb"/>: ${arena.generalSeatsNumb}</div>
                    <button onclick="location.href='${pageContext.request.contextPath}/arena-sectors?id=${arena.id}'">
                        Перейти с меню настройки информации о арене
                    </button>
<%--                    <form action="${pageContext.request.contextPath}/upload-arena-photo" method="post"--%>
<%--                          enctype="multipart/form-data" style="display:inline;">--%>
<%--                        <input type="hidden" name="id" value="${arena.id}"/>--%>
<%--                        <input type="file" name="arenaPhoto"/>--%>
<%--                        <button type="submit">Добавить фото арены</button>--%>
<%--                    </form>--%>
<%--                    <c:if test="${sessionScope.user.role == 'ADMIN'}">--%>
                        <form action="${pageContext.request.contextPath}/update-arena" method="get" style="display:inline;">
                            <input type="hidden" name="id" value="${arena.id}"/>
                            <button type="submit"><fmt:message key="button.update"/></button>
                        </form>
                        <form action="${pageContext.request.contextPath}/delete-arena" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${arena.id}"/>
                            <button type="submit"><fmt:message key="button.delete"/></button>
                        </form>
<%--                    </c:if>--%>
                </div>
            </c:forEach>
            <div class="pagination" style="padding-top: 145px">
                <c:if test="${requestScope.page > 1}">
                    <a href="${pageContext.request.contextPath}/arenas?page=${param.page - 1}"
                       class="pagination-arrow">&laquo; <fmt:message key="page.previous"/></a>
                </c:if>
                <c:if test="${requestScope.arenas.size() eq requestScope.limit}">
                    <a href="${pageContext.request.contextPath}/arenas?page=${param.page != null
          ? param.page + 1 : 2}" class="pagination-arrow"><fmt:message key="page.next"/> &raquo;</a>
                </c:if>
            </div>
        </c:when>
        <c:otherwise>
            <div><fmt:message key="arenas.not_found"/></div>
        </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
