<div style="position: absolute; top: 20px; right: 20px;">
    <form action="${pageContext.request.contextPath}/logout" method="post" style="display: inline;">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <button id="logoutButton" type="submit" style="background-color: #ff0000; color: white; border: none; padding: 10px 20px;
                 border-radius: 5px; cursor: pointer;"><fmt:message key="logout"/></button>
    </form>
</div>