<%--
  Created by IntelliJ IDEA.
  User: Qin Liang
  Date: 2018/7/3
  Time: 22:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>成功</title>
</head>
<body>
<p>
    啊啊啊~~，成功了.我是web-inf下面的哦
    <%=request.getAttribute("test") %> <br>

</p>
</body>
</html>
