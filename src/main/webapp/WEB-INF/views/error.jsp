<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Quantfolio — Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-page">
<div class="auth-card" style="text-align:center">
    <div class="brand-icon-lg">⚠️</div>
    <h2>Something went wrong</h2>
    <p class="muted">An unexpected error occurred. Please try again.</p>
    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary" style="margin-top:1rem">Go to Dashboard</a>
</div>
</body>
</html>
