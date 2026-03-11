<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="currentPage" value="transactions" scope="request"/>
<%@ include file="header.jsp" %>

<div class="page-header">
    <h2>🔄 Transactions</h2>
    <button class="btn btn-primary" onclick="document.getElementById('txnModal').style.display='flex'">+ Add Transaction</button>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-error">${error}</div>
</c:if>
<c:if test="${param.success == 'true'}">
    <div class="alert alert-success">Transaction recorded successfully!</div>
</c:if>

<!-- Transaction History -->
<div class="card">
    <h3>Transaction History</h3>
    <div class="table-wrap">
        <table class="data-table">
            <thead>
                <tr><th>Date</th><th>Stock</th><th>Type</th><th>Qty</th><th>Price</th><th>Total</th><th>Notes</th></tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${empty transactions}">
                    <tr><td colspan="7" class="empty-state">No transactions yet. Click <strong>+ Add Transaction</strong> to record your first trade.</td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="t" items="${transactions}">
                    <tr>
                        <td><fmt:formatDate value="${t.transactionDate}" pattern="dd MMM yyyy HH:mm"/></td>
                        <td><strong>${t.symbol}</strong> — ${t.companyName}</td>
                        <td><span class="badge ${t.type == 'BUY' ? 'badge-green' : 'badge-red'}">${t.type}</span></td>
                        <td>${t.quantity}</td>
                        <td>₹<fmt:formatNumber value="${t.price}" pattern="#,##0.00"/></td>
                        <td>₹<fmt:formatNumber value="${t.totalValue}" pattern="#,##0.00"/></td>
                        <td class="muted">${t.notes}</td>
                    </tr>
                    </c:otherwise>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</div>

<!-- Add Transaction Modal -->
<div id="txnModal" class="modal-overlay" style="display:none">
    <div class="modal">
        <div class="modal-header">
            <h3>Record Transaction</h3>
            <button class="modal-close" onclick="document.getElementById('txnModal').style.display='none'">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/transactions" method="post">
            <div class="form-group">
                <label>Stock</label>
                <select name="stockId" required>
                    <option value="">-- Select Stock --</option>
                    <c:forEach var="s" items="${stocks}">
                        <option value="${s.stockId}">${s.symbol} — ${s.companyName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Type</label>
                    <select name="type" required>
                        <option value="BUY">BUY</option>
                        <option value="SELL">SELL</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Quantity</label>
                    <input type="number" name="quantity" min="1" required placeholder="10">
                </div>
            </div>
            <div class="form-group">
                <label>Price per Share (₹)</label>
                <input type="number" name="price" min="0.01" step="0.01" required placeholder="150.00">
            </div>
            <div class="form-group">
                <label>Notes (optional)</label>
                <input type="text" name="notes" placeholder="e.g. Long-term hold">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn" onclick="document.getElementById('txnModal').style.display='none'">Cancel</button>
                <button type="submit" class="btn btn-primary">Record Transaction</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="footer.jsp" %>
