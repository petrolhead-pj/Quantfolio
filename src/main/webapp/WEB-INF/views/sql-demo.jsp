<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="currentPage" value="sql-demo" scope="request"/>
<%@ include file="header.jsp" %>

<div class="page-header">
    <div>
        <h2>SQL Demo Console</h2>
        <p class="muted">Live database connection, table counts, trigger audit rows, and recent trades.</p>
    </div>
    <a href="${pageContext.request.contextPath}/sql-demo" class="btn btn-primary">Refresh</a>
</div>

<div class="cards-grid">
    <c:forEach var="stat" items="${tableStats}">
        <div class="card summary-card">
            <div class="card-label">${stat.table_name}</div>
            <div class="card-value">${stat.row_count}</div>
        </div>
    </c:forEach>
</div>

<div class="sql-grid">
    <div class="card">
        <h3>Connection Status</h3>
        <div class="kv-list">
            <c:forEach var="entry" items="${connectionStatus}">
                <div class="kv-row">
                    <span>${entry.key}</span>
                    <strong>${entry.value}</strong>
                </div>
            </c:forEach>
        </div>
    </div>

    <div class="card">
        <h3>Queries Used</h3>
        <pre class="sql-code">SELECT COUNT(*) FROM users;
SELECT * FROM audit_log ORDER BY logged_at DESC LIMIT 15;
INSERT INTO transactions (...) VALUES (...);
-- MySQL triggers update portfolio_holdings and audit_log</pre>
    </div>
</div>

<div class="card">
    <div class="card-header-row">
        <h3>Audit Log</h3>
        <a href="${pageContext.request.contextPath}/transactions" class="btn btn-sm">Record Trade</a>
    </div>
    <div class="table-wrap">
        <table class="data-table">
            <thead>
                <tr><th>ID</th><th>User</th><th>Action</th><th>Detail</th><th>IP</th><th>Time</th></tr>
            </thead>
            <tbody>
            <c:forEach var="row" items="${auditRows}">
                <tr>
                    <td>${row.log_id}</td>
                    <td>${row.name} <span class="muted">#${row.user_id}</span></td>
                    <td><span class="badge badge-green">${row.action}</span></td>
                    <td>${row.detail}</td>
                    <td class="muted">${row.ip_address}</td>
                    <td><fmt:formatDate value="${row.logged_at}" pattern="dd MMM yyyy HH:mm:ss"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<div class="card">
    <h3>Latest Transactions</h3>
    <div class="table-wrap">
        <table class="data-table">
            <thead>
                <tr><th>ID</th><th>User</th><th>Stock</th><th>Type</th><th>Qty</th><th>Price</th><th>Total</th><th>Date</th></tr>
            </thead>
            <tbody>
            <c:forEach var="t" items="${latestTransactions}">
                <tr>
                    <td>${t.transaction_id}</td>
                    <td>${t.user_name}</td>
                    <td><strong>${t.symbol}</strong></td>
                    <td><span class="badge ${t.type == 'BUY' ? 'badge-green' : 'badge-red'}">${t.type}</span></td>
                    <td>${t.quantity}</td>
                    <td>Rs. <fmt:formatNumber value="${t.price}" pattern="#,##0.00"/></td>
                    <td>Rs. <fmt:formatNumber value="${t.total_value}" pattern="#,##0.00"/></td>
                    <td><fmt:formatDate value="${t.transaction_date}" pattern="dd MMM yyyy HH:mm"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="footer.jsp" %>
