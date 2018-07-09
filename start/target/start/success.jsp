<%--
  Created by IntelliJ IDEA.
  User: Qin Liang
  Date: 2018/7/3
  Time: 22:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title>成功</title>
</head>
<body>
<p>
    小样，成功了<br>
    ${param.test} <br>
    <%=request.getAttribute("test") %> <br>


</p>
</body>
</html>
