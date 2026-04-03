/**
 * Admin request management page
 */

let currentStatus = 'PENDING';
let currentRequests = [];

document.addEventListener('DOMContentLoaded', async function () {
    if (!requireAuth()) {
        return;
    }

    const authUser = getAuthUser();
    if (!authUser || authUser.role !== 'ADMIN') {
        window.location.href = '/dashboard';
        return;
    }

    setupManageRequestsHandlers();
    await loadRequestsByCurrentStatus();
});

function setupManageRequestsHandlers() {
    const tabs = document.querySelectorAll('#requestStatusTabs button[data-status]');
    const refreshBtn = document.getElementById('refreshRequestsBtn');

    tabs.forEach(tab => {
        tab.addEventListener('click', async function () {
            tabs.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            currentStatus = this.getAttribute('data-status') || 'PENDING';
            await loadRequestsByCurrentStatus();
        });
    });

    refreshBtn.addEventListener('click', loadRequestsByCurrentStatus);
}

async function loadRequestsByCurrentStatus() {
    showLoading('manageRequestsLoader');
    clearManageRequestsMessages();

    try {
        currentRequests = await getRequestsByStatus(currentStatus);
        if (!Array.isArray(currentRequests)) {
            currentRequests = [];
        }
        renderRequestsTable(currentRequests);
    } catch (error) {
        currentRequests = [];
        renderRequestsTable([]);
        showManageRequestsError(error.message || 'Failed to load requests.');
    } finally {
        hideLoading('manageRequestsLoader');
    }
}

function renderRequestsTable(requests) {
    const container = document.getElementById('manageRequestsTableContainer');

    if (!Array.isArray(requests) || requests.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📋</div>
                <p>No ${currentStatus.toLowerCase()} requests found</p>
            </div>
        `;
        return;
    }

    container.innerHTML = `
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Item</th>
                        <th>Requester</th>
                        <th>Quantity</th>
                        <th>Requested</th>
                        <th>Reviewed</th>
                        <th>Remarks</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${requests.map(req => `
                        <tr>
                            <td><small>${req.requestId}</small></td>
                            <td>${escapeHtml(req.itemName || 'N/A')}</td>
                            <td>
                                <small>${escapeHtml(req.requesterName || 'N/A')}</small>
                                <br>
                                <small class="text-muted">${escapeHtml(req.requesterEmail || '-')}</small>
                            </td>
                            <td>${req.requestedQuantity ?? 0}</td>
                            <td><small>${formatDate(req.requestedAt)}</small></td>
                            <td><small>${req.reviewedAt ? formatDate(req.reviewedAt) : '-'}</small></td>
                            <td><small>${escapeHtml(req.reviewRemarks || '-')}</small></td>
                            <td>
                                ${currentStatus === 'PENDING' ? `
                                    <div class="request-actions">
                                        <button class="btn btn-success btn-sm" onclick="reviewRequestFromPage(${req.requestId}, 'APPROVED')">Approve</button>
                                        <button class="btn btn-danger btn-sm" onclick="reviewRequestFromPage(${req.requestId}, 'REJECTED')">Reject</button>
                                    </div>
                                ` : '<span class="text-muted small">No actions</span>'}
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
    `;
}

async function reviewRequestFromPage(requestId, status) {
    clearManageRequestsMessages();

    const defaultRemarks = status === 'APPROVED' ? 'Approved' : 'Rejected';
    const remarks = window.prompt(`Enter ${status.toLowerCase()} remarks:`, defaultRemarks);
    if (remarks === null) {
        return;
    }

    try {
        await reviewRequest(requestId, {
            status,
            reviewRemarks: remarks,
        });

        showManageRequestsSuccess(`Request #${requestId} ${status.toLowerCase()} successfully.`);
        await loadRequestsByCurrentStatus();
    } catch (error) {
        showManageRequestsError(error.message || `Failed to ${status.toLowerCase()} request #${requestId}.`);
    }
}

function showManageRequestsError(message) {
    const errorEl = document.getElementById('manageRequestsError');
    const successEl = document.getElementById('manageRequestsSuccess');
    successEl.classList.add('d-none');
    successEl.textContent = '';
    errorEl.textContent = message;
    errorEl.classList.remove('d-none');
}

function showManageRequestsSuccess(message) {
    const errorEl = document.getElementById('manageRequestsError');
    const successEl = document.getElementById('manageRequestsSuccess');
    errorEl.classList.add('d-none');
    errorEl.textContent = '';
    successEl.textContent = message;
    successEl.classList.remove('d-none');
}

function clearManageRequestsMessages() {
    const errorEl = document.getElementById('manageRequestsError');
    const successEl = document.getElementById('manageRequestsSuccess');

    errorEl.classList.add('d-none');
    errorEl.textContent = '';
    successEl.classList.add('d-none');
    successEl.textContent = '';
}

function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}
