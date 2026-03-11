<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="currentPage" value="dashboard" scope="request"/>
<%@ include file="header.jsp" %>

<div class="page-header">
    <h2>📊 Dashboard</h2>
    <p>Welcome back, <strong>${sessionScope.loggedInUser.name}</strong></p>
</div>

<!-- Summary Cards -->
<div class="cards-grid">
    <div class="card summary-card">
        <div class="card-label">Portfolio Value</div>
        <div class="card-value">
            ₹<fmt:formatNumber value="${summary.portfolioValue}" pattern="#,##0.00"/>
        </div>
    </div>
    <div class="card summary-card">
        <div class="card-label">Total Invested</div>
        <div class="card-value">
            ₹<fmt:formatNumber value="${summary.totalInvested}" pattern="#,##0.00"/>
        </div>
    </div>
    <div class="card summary-card ${summary.unrealizedPnl >= 0 ? 'positive' : 'negative'}">
        <div class="card-label">Unrealized P&L</div>
        <div class="card-value">
            ₹<fmt:formatNumber value="${summary.unrealizedPnl}" pattern="#,##0.00"/>
            <span class="badge">${summary.pnlPct}%</span>
        </div>
    </div>
    <div class="card summary-card">
        <div class="card-label">Total Stocks</div>
        <div class="card-value">${summary.totalStocks}</div>
    </div>
</div>

<!-- Charts Row -->
<div class="charts-grid">
    <div class="card chart-card">
        <h3>Sector Allocation</h3>
        <canvas id="sectorChart"></canvas>
    </div>
    <div class="card chart-card">
        <h3>Top Performers</h3>
        <canvas id="performersChart"></canvas>
    </div>
</div>

<div class="card chart-card-wide">
    <h3>Monthly Activity (Last 12 Months)</h3>
    <canvas id="monthlyChart"></canvas>
</div>

<!-- Recent Transactions -->
<div class="card">
    <div class="card-header-row">
        <h3>Recent Transactions</h3>
        <a href="${pageContext.request.contextPath}/transactions" class="btn btn-sm">View All</a>
    </div>
    <div class="table-wrap">
        <table class="data-table">
            <thead>
                <tr><th>Date</th><th>Stock</th><th>Type</th><th>Qty</th><th>Price</th><th>Total</th></tr>
            </thead>
            <tbody>
                <c:forEach var="t" items="${recentTxns}">
                <tr>
                    <td><fmt:formatDate value="${t.transactionDate}" pattern="dd MMM yyyy"/></td>
                    <td><strong>${t.symbol}</strong> <span class="muted">${t.companyName}</span></td>
                    <td><span class="badge ${t.type == 'BUY' ? 'badge-green' : 'badge-red'}">${t.type}</span></td>
                    <td>${t.quantity}</td>
                    <td>₹<fmt:formatNumber value="${t.price}" pattern="#,##0.00"/></td>
                    <td>₹<fmt:formatNumber value="${t.totalValue}" pattern="#,##0.00"/></td>
                </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<script>
const SECTORS    = ${sectorsJson};
const PERFORMERS = ${performersJson};
const MONTHLY    = ${monthlyJson};

// Sector Doughnut
new Chart(document.getElementById('sectorChart'), {
    type: 'doughnut',
    data: {
        labels: SECTORS.map(s => s.sector_name),
        datasets: [{
            data: SECTORS.map(s => parseFloat(s.allocation_pct)),
            backgroundColor: ['#6366f1','#22d3ee','#f59e0b','#10b981','#f43f5e','#8b5cf6','#fb923c','#34d399'],
            borderWidth: 2, borderColor: '#1e293b'
        }]
    },
    options: {
        responsive: true,
        plugins: { legend: { position: 'right', labels: { color: '#94a3b8', font: { size: 12 } } } }
    }
});

// Top Performers Bar
new Chart(document.getElementById('performersChart'), {
    type: 'bar',
    data: {
        labels: PERFORMERS.map(p => p.symbol),
        datasets: [{
            label: 'Return %',
            data: PERFORMERS.map(p => parseFloat(p.return_pct)),
            backgroundColor: PERFORMERS.map(p => p.return_pct >= 0 ? '#10b981' : '#f43f5e'),
            borderRadius: 6
        }]
    },
    options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: {
            y: { ticks: { color: '#94a3b8', callback: v => v + '%' }, grid: { color: '#334155' } },
            x: { ticks: { color: '#94a3b8' }, grid: { display: false } }
        }
    }
});

// Monthly Activity
new Chart(document.getElementById('monthlyChart'), {
    type: 'bar',
    data: {
        labels: MONTHLY.map(m => m.month_label),
        datasets: [
            { label: 'Buy',  data: MONTHLY.map(m => parseFloat(m.buy_value  || 0)), backgroundColor: '#10b981', borderRadius: 4 },
            { label: 'Sell', data: MONTHLY.map(m => parseFloat(m.sell_value || 0)), backgroundColor: '#f43f5e', borderRadius: 4 }
        ]
    },
    options: {
        responsive: true,
        plugins: { legend: { labels: { color: '#94a3b8' } } },
        scales: {
            y: { ticks: { color: '#94a3b8', callback: v => '₹' + v.toLocaleString() }, grid: { color: '#334155' } },
            x: { ticks: { color: '#94a3b8' }, grid: { display: false } }
        }
    }
});
</script>

<%@ include file="footer.jsp" %>
