<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quantfolio - ${param.pageTitle != null ? param.pageTitle : 'Dashboard'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
</head>
<body>
<nav class="navbar">
    <a class="brand" href="${pageContext.request.contextPath}/dashboard">
        <span class="brand-icon">Q</span> Quantfolio
    </a>
    <ul class="nav-links">
        <li><a href="${pageContext.request.contextPath}/dashboard" class="${currentPage=='dashboard' ? 'active' : ''}">Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/portfolio" class="${currentPage=='portfolio' ? 'active' : ''}">Portfolio</a></li>
        <li><a href="${pageContext.request.contextPath}/transactions" class="${currentPage=='transactions' ? 'active' : ''}">Transactions</a></li>
        <li><a href="${pageContext.request.contextPath}/stocks" class="${currentPage=='stocks' ? 'active' : ''}">Stocks</a></li>
        <li><a href="${pageContext.request.contextPath}/sql-demo" class="${currentPage=='sql-demo' ? 'active' : ''}">SQL Demo</a></li>
    </ul>
    <div class="nav-user">
        <span>${sessionScope.loggedInUser.name}</span>
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
    </div>
</nav>
<main class="container">
