<div style="position: absolute; top: 20px; right: 20px;">
    <c:if test="${not empty sessionScope.user}">
        <form action="${pageContext.request.contextPath}/logout" method="post" style="display: inline;">
            <button id="logoutButton" type="submit" style="background-color: #ff0000; color: white; border: none; padding: 10px 20px;
                 border-radius: 5px; cursor: pointer;"><fmt:message key="logout"/></button>
        </form>
    </c:if>
</div>
<script src="<c:url value='/js/logout.js'/>"></script>