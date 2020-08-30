<%@ page isELIgnored="false" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
    <form method="post" action="/resume/add">
        name: <input type="text" name="name"/>
        address: <input type="text" name="address"/>
        phone : <input type="text" name="phone"/>
        <input type="submit" value="添加"/>
    </form>
    <table class="table table-bordered">
        <thead>
            <tr>
                <th>id</th>
                <th>name</th>
                <th>address</th>
                <th>phone</th>
                <th>操作</th>
                <th>操作</th>
            </tr>
        </thead>

        <c:forEach items="${resumeList}" var="resume">
        <tr>
            <form method="post" action="/resume/update">
                <td><input type="text" name="id" value="${resume.id}" readOnly="true"/></td>
                <td><input type="text" name="name" value="${resume.name}"/></td>
                <td><input type="text" name="address" value="${resume.address}"/></td>
                <td><input type="text" name="phone" value="${resume.phone}"/></td>
                <td><input type="submit" value="编辑"/></td>
            </form>
            <form method="post" action="/resume/delete">
                <input type="hidden" name="id" value="${resume.id}"/>
                <td><input type="submit" value="删除"/></td>
            </form>
        <tr>
        </c:forEach>
    </table>
</body>
</html>