<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="currentPage" value="stocks" scope="request"/>
<%@ include file="header.jsp" %>

<div class="page-header">
    <h2>📋 Stock Universe</h2>
    <input type="text" id="stockSearch" placeholder="🔍 Search stocks..." class="search-input" onkeyup="filterStocks()">
</div>

<div class="card">
    <div class="table-wrap">
        <table class="data-table" id="stockTable">
            <thead>
                <tr><th>Symbol</th><th>Company</th><th>Sector</th><th>Current Price</th><th>Action</th></tr>
            </thead>
            <tbody>
            <c:forEach var="s" items="${stocks}">
                <tr>
                    <td><strong>${s.symbol}</strong></td>
                    <td>${s.companyName}</td>
                    <td><span class="sector-tag">${s.sectorName}</span></td>
                    <td>₹<fmt:formatNumber value="${s.currentPrice}" pattern="#,##0.00"/></td>
                    <td><a href="${pageContext.request.contextPath}/transactions" class="btn btn-sm">Trade</a></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<script>
function filterStocks() {
    const q = document.getElementById('stockSearch').value.toLowerCase();
    document.querySelectorAll('#stockTable tbody tr').forEach(row => {
        row.style.display = row.textContent.toLowerCase().includes(q) ? '' : 'none';
    });
}
</script>
<%@ include file="footer.jsp" %>
