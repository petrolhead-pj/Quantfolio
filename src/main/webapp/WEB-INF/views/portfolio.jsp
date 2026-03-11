<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="currentPage" value="portfolio" scope="request"/>
<%@ include file="header.jsp" %>

<div class="page-header">
    <h2>💼 My Portfolio</h2>
</div>

<!-- Summary -->
<div class="cards-grid">
    <div class="card summary-card">
        <div class="card-label">Portfolio Value</div>
        <div class="card-value">₹<fmt:formatNumber value="${summary.portfolioValue}" pattern="#,##0.00"/></div>
    </div>
    <div class="card summary-card">
        <div class="card-label">Total Invested</div>
        <div class="card-value">₹<fmt:formatNumber value="${summary.totalInvested}" pattern="#,##0.00"/></div>
    </div>
    <div class="card summary-card ${summary.unrealizedPnl >= 0 ? 'positive' : 'negative'}">
        <div class="card-label">Unrealized P&L</div>
        <div class="card-value">
            ₹<fmt:formatNumber value="${summary.unrealizedPnl}" pattern="#,##0.00"/>
            <span class="badge">${summary.pnlPct}%</span>
        </div>
    </div>
    <div class="card summary-card">
        <div class="card-label">Holdings</div>
        <div class="card-value">${summary.totalStocks} stocks</div>
    </div>
</div>

<!-- Holdings Table -->
<div class="card">
    <h3>Current Holdings</h3>
    <div class="table-wrap">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Symbol</th><th>Company</th><th>Sector</th>
                    <th>Qty</th><th>Avg Buy</th><th>Current</th>
                    <th>Invested</th><th>Current Value</th><th>P&L</th><th>Return</th>
                </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${empty holdings}">
                    <tr><td colspan="10" class="empty-state">No holdings yet. <a href="${pageContext.request.contextPath}/transactions">Add a transaction</a> to get started.</td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="h" items="${holdings}">
                    <tr>
                        <td><strong>${h.symbol}</strong></td>
                        <td>${h.companyName}</td>
                        <td><span class="sector-tag">${h.sectorName}</span></td>
                        <td>${h.quantity}</td>
                        <td>₹<fmt:formatNumber value="${h.avgBuyPrice}" pattern="#,##0.00"/></td>
                        <td>₹<fmt:formatNumber value="${h.currentPrice}" pattern="#,##0.00"/></td>
                        <td>₹<fmt:formatNumber value="${h.investedValue}" pattern="#,##0.00"/></td>
                        <td>₹<fmt:formatNumber value="${h.currentValue}" pattern="#,##0.00"/></td>
                        <td class="${h.unrealizedPnl >= 0 ? 'positive' : 'negative'}">
                            ₹<fmt:formatNumber value="${h.unrealizedPnl}" pattern="#,##0.00"/>
                        </td>
                        <td class="${h.returnPct >= 0 ? 'positive' : 'negative'}">
                            ${h.returnPct}%
                        </td>
                    </tr>
                    </c:otherwise>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="footer.jsp" %>
