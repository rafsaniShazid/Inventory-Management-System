/**
 * Dashboard Page Logic
 * Loads and displays dashboard widgets with real-time data
 */

document.addEventListener('DOMContentLoaded', function () {
    if (!requireAuth()) {
        return;
    }
    loadDashboard();
});

async function loadDashboard() {
    // Load all dashboard widgets in parallel
    await Promise.all([
        loadStats(),
        loadPendingRequests(),
        loadLowStockItems(),
        loadApprovedRequests(),
    ]);
}

// ==================== Stats Widget ====================
async function loadStats() {
    const container = document.getElementById('statsContainer');
    const loader = document.getElementById('statsLoader');
    const error = document.getElementById('statsError');

    try {
        showLoading('statsLoader');
        const stats = await getDashboardStats();

        hideLoading('statsLoader');
        clearError('statsError');

        const statsHTML = `
            <div class="col-md-4">
                <div class="stat-card">
                    <h6>Total Items</h6>
                    <div class="stat-value">${stats.totalItems || 0}</div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stat-card success">
                    <h6>Total Stock</h6>
                    <div class="stat-value">${stats.totalStockCount || 0}</div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stat-card danger">
                    <h6>Low Stock Alert</h6>
                    <div class="stat-value">${stats.lowStockCount || 0}</div>
                </div>
            </div>
        `;

        container.innerHTML = statsHTML;
    } catch (err) {
        hideLoading('statsLoader');
        showError('Failed to load statistics', 'statsError');
        container.innerHTML = `
            <div class="col-12">
                <div class="empty-state">
                    <div class="empty-state-icon">📊</div>
                    <p>Unable to load dashboard statistics. Please try again later.</p>
                </div>
            </div>
        `;
    }
}

// ==================== Pending Requests Widget ====================
async function loadPendingRequests() {
    const container = document.getElementById('pendingContainer');
    const loader = document.getElementById('pendingLoader');
    const error = document.getElementById('pendingError');

    try {
        showLoading('pendingLoader');
        const requests = await getPendingRequests();

        hideLoading('pendingLoader');
        clearError('pendingError');

        if (!Array.isArray(requests) || requests.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">✓</div>
                    <p>No pending requests at the moment</p>
                </div>
            `;
            return;
        }

        const table = `
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead class="table-light">
                        <tr>
                            <th>Request ID</th>
                            <th>Item</th>
                            <th>Requester</th>
                            <th>Quantity</th>
                            <th>Date</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${requests.map(req => `
                            <tr>
                                <td><small>${req.requestId}</small></td>
                                <td>${req.itemName || 'N/A'}</td>
                                <td>
                                    <small>${req.requesterName}</small>
                                    <br>
                                    <small class="text-muted">${req.requesterEmail}</small>
                                </td>
                                <td>${req.requestedQuantity}</td>
                                <td><small>${formatDate(req.requestedAt)}</small></td>
                                <td>
                                    <div class="request-actions">
                                        <button class="btn btn-success btn-sm" onclick="approveRequest(${req.requestId}, this)">
                                            Approve
                                        </button>
                                        <button class="btn btn-danger btn-sm" onclick="rejectRequest(${req.requestId}, this)">
                                            Reject
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;

        container.innerHTML = table;
    } catch (err) {
        hideLoading('pendingLoader');
        showError('Failed to load pending requests', 'pendingError');
        container.innerHTML = `
            <div class="empty-state">
                <p>Unable to load requests. Please try again later.</p>
            </div>
        `;
    }
}

// ==================== Low Stock Items Widget ====================
async function loadLowStockItems() {
    const container = document.getElementById('lowStockContainer');
    const loader = document.getElementById('lowStockLoader');
    const error = document.getElementById('lowStockError');

    try {
        showLoading('lowStockLoader');
        const items = await getLowStockItems(10);

        hideLoading('lowStockLoader');
        clearError('lowStockError');

        if (!Array.isArray(items) || items.length === 0) {
            container.innerHTML = `
                <div class="empty-state" style="padding: 1.5rem;">
                    <div class="empty-state-icon" style="font-size: 2rem;">✓</div>
                    <p>All items have sufficient stock</p>
                </div>
            `;
            return;
        }

        const list = `
            <ul class="list-group list-group-flush">
                ${items.map(item => `
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        <div>
                            <strong>${item.itemName}</strong>
                            <br>
                            <small class="text-muted">Category: ${item.categoryName || 'N/A'}</small>
                        </div>
                        <span class="badge bg-danger">${item.stockQuantity} units</span>
                    </li>
                `).join('')}
            </ul>
        `;

        container.innerHTML = list;
    } catch (err) {
        hideLoading('lowStockLoader');
        showError('Failed to load low stock items', 'lowStockError');
        container.innerHTML = `
            <div class="empty-state" style="padding: 1.5rem;">
                <p class="text-muted">Unable to load items</p>
            </div>
        `;
    }
}

// ==================== Approved Requests Widget ====================
async function loadApprovedRequests() {
    const container = document.getElementById('approvedContainer');
    const loader = document.getElementById('approvedLoader');
    const error = document.getElementById('approvedError');

    try {
        showLoading('approvedLoader');
        const requests = await getApprovedRequests();

        hideLoading('approvedLoader');
        clearError('approvedError');

        if (!Array.isArray(requests) || requests.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">📋</div>
                    <p>No approved requests yet</p>
                </div>
            `;
            return;
        }

        const Limited = requests.slice(0, 10); // Show only last 10
        const table = `
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead class="table-light">
                        <tr>
                            <th>Request ID</th>
                            <th>Item</th>
                            <th>Requester</th>
                            <th>Quantity</th>
                            <th>Requested</th>
                            <th>Approved</th>
                            <th>Remarks</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${Limited.map(req => `
                            <tr>
                                <td><small>${req.requestId}</small></td>
                                <td>${req.itemName || 'N/A'}</td>
                                <td>
                                    <small>${req.requesterName}</small>
                                    <br>
                                    <small class="text-muted">${req.requesterEmail}</small>
                                </td>
                                <td>${req.requestedQuantity}</td>
                                <td><small>${formatDate(req.requestedAt)}</small></td>
                                <td><small>${formatDate(req.reviewedAt)}</small></td>
                                <td><small>${req.reviewRemarks || '-'}</small></td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;

        container.innerHTML = table;
    } catch (err) {
        hideLoading('approvedLoader');
        showError('Failed to load approved requests', 'approvedError');
        container.innerHTML = `
            <div class="empty-state">
                <p>Unable to load approved requests. Please try again later.</p>
            </div>
        `;
    }
}

// ==================== Action Handlers ====================
async function approveRequest(requestId, button) {
    button.disabled = true;
    const originalText = button.textContent;
    button.textContent = 'Approving...';

    try {
        const remarks = prompt('Enter approval remarks (optional):', 'Approved');
        if (remarks === null) {
            button.disabled = false;
            button.textContent = originalText;
            return;
        }

        const response = await reviewRequest(requestId, {
            status: 'APPROVED',
            reviewRemarks: remarks,
        });

        alert('Request approved successfully! Stock has been reduced.');
        loadDashboard(); // Refresh dashboard
    } catch (error) {
        alert('Failed to approve request: ' + (error.message || 'Unknown error'));
        button.disabled = false;
        button.textContent = originalText;
    }
}

async function rejectRequest(requestId, button) {
    button.disabled = true;
    const originalText = button.textContent;
    button.textContent = 'Rejecting...';

    try {
        const remarks = prompt('Enter rejection reason:', 'Rejected');
        if (remarks === null) {
            button.disabled = false;
            button.textContent = originalText;
            return;
        }

        const response = await reviewRequest(requestId, {
            status: 'REJECTED',
            reviewRemarks: remarks,
        });

        alert('Request rejected successfully.');
        loadDashboard(); // Refresh dashboard
    } catch (error) {
        alert('Failed to reject request: ' + (error.message || 'Unknown error'));
        button.disabled = false;
        button.textContent = originalText;
    }
}

