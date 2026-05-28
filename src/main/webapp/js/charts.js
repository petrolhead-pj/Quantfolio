/**
 * Quantfolio — Chart.js Dashboard Charts
 */
Chart.defaults.color = '#8892a4';
Chart.defaults.borderColor = '#2e3250';
Chart.defaults.font.family = "'Inter', sans-serif";

const PALETTE = [
    '#6366f1','#10b981','#f59e0b','#ef4444','#a855f7',
    '#14b8a6','#3b82f6','#f97316','#ec4899','#84cc16'
];

function initDashboardCharts(sectorsData, monthlyData, top5Data) {
    renderSectorChart(sectorsData);
    renderMonthlyChart(monthlyData);
    renderPerformerChart(top5Data);
}

function renderSectorChart(sectors) {
    if (!sectors || sectors.length === 0) return;
    const ctx = document.getElementById('sectorChart');
    if (!ctx) return;
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: sectors.map(s => s.sectorName),
            datasets: [{
                data:            sectors.map(s => parseFloat(s.allocationPct)),
                backgroundColor: PALETTE.slice(0, sectors.length),
                borderColor: '#1a1d2e', borderWidth: 3, hoverOffset: 8
            }]
        },
        options: {
            responsive: true, maintainAspectRatio: false, cutout: '65%',
            plugins: {
                legend: { position: 'right', labels: { boxWidth: 12, padding: 16, font: { size: 12 } } },
                tooltip: { callbacks: { label: ctx => ` ${ctx.label}: ${ctx.parsed}%` } }
            }
        }
    });
}

function renderMonthlyChart(monthly) {
    if (!monthly || monthly.length === 0) return;
    const ctx = document.getElementById('monthlyChart');
    if (!ctx) return;
    const MONTHS = ['','Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: monthly.map(m => MONTHS[m[1]] + ' ' + m[0]),
            datasets: [
                { label: 'Bought', data: monthly.map(m => parseFloat(m[2])||0),
                  backgroundColor: 'rgba(99,102,241,0.7)', borderRadius: 6 },
                { label: 'Sold',   data: monthly.map(m => parseFloat(m[3])||0),
                  backgroundColor: 'rgba(239,68,68,0.7)',  borderRadius: 6 }
            ]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            plugins: {
                legend: { position: 'top', labels: { font: { size: 12 }, boxWidth: 12 } },
                tooltip: { callbacks: { label: ctx => ' \u20B9' + ctx.parsed.y.toLocaleString('en-IN') } }
            },
            scales: {
                x: { grid: { display: false } },
                y: { ticks: { callback: v => '\u20B9' + (v/1000).toFixed(0) + 'k' } }
            }
        }
    });
}

function renderPerformerChart(top5) {
    if (!top5 || top5.length === 0) return;
    const ctx = document.getElementById('performerChart');
    if (!ctx) return;
    const returns = top5.map(h => parseFloat(h.returnPct));
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: top5.map(h => h.symbol),
            datasets: [{
                label: 'Return %',
                data: returns,
                backgroundColor: returns.map(v => v >= 0 ? 'rgba(16,185,129,0.75)' : 'rgba(239,68,68,0.75)'),
                borderRadius: 6
            }]
        },
        options: {
            indexAxis: 'y', responsive: true, maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: { callbacks: { label: ctx => ` ${ctx.parsed.x >= 0 ? '+' : ''}${ctx.parsed.x.toFixed(2)}%` } }
            },
            scales: {
                x: { ticks: { callback: v => v + '%' }, grid: { color: '#2e3250' } },
                y: { grid: { display: false } }
            }
        }
    });
}
