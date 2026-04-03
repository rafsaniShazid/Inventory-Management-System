/**
 * My Requests Page Logic
 * Handles request search by email and status filtering
 */

let allRequests = [];
let currentFilter = 'ALL';

document.addEventListener('DOMContentLoaded', function () {
    if (!requireAuth()) {
        return;
    }
    setupEventHandlers();
});

// ==================== Event Handlers ====================
function setupEventHandlers() {
    const emailInput = document.getElementById('requesterEmail');

    const authUser = typeof getAuthUser === 'function' ? getAuthUser() : null;
    if (authUser && authUser.email) {
        emailInput.value = authUser.email;
    }

    // Allow Enter key to search
    emailInput.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            searchRequests();
        }
    });

    // Remember last email in sessionStorage
    const savedEmail = sessionStorage.getItem('lastSearchEmail');
    if (savedEmail) {
        emailInput.value = savedEmail;
        searchRequests();
    } else if (authUser && authUser.email) {
        searchRequests();
    }
}

// ==================== Search & Load ====================
async function searchRequests() {
    const emailInput = document.getElementById('requesterEmail');
    const email = emailInput.value.trim();

    // Validate email
    if (!email) {
        showError('Please enter an email address');
        document.getElementById('requestsContainer').innerHTML = '';
        return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showError('Please enter a valid email address');
        return;
    }

    // Save email to sessionStorage
    sessionStorage.setItem('lastSearchEmail', email);

    // Load requests
    await loadRequestsByEmail(email);
}

async function loadRequestsByEmail(email) {
    const container = document.getElementById('requestsContainer');
    const spinner = document.getElementById('loadingSpinner');

    try {
        showLoading('loadingSpinner');
        clearError();

        // Fetch requests by email
        allRequests = await getRequestsByEmail(email);

        hideLoading('loadingSpinner');

        if (!Array.isArray(allRequests)) {
            allRequests = [];
        }

        // Display requests
        displayRequests(allRequests);
    } catch (error) {
        hideLoading('loadingSpinner');
        
        // Handle 404 or other errors
        let errorMsg = 'Failed to load requests';
        if (error.status === 404) {
            errorMsg = 'No requests found for this email address';
            allRequests = [];
            displayRequests([]);
        } else if (error.message) {
            errorMsg = error.message;
        }

        showError(errorMsg);
    }
}

// ==================== Display & Filter ====================
function displayRequests(requests) {
    const container = document.getElementById('requestsContainer');

    if (!Array.isArray(requests) || requests.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📋</div>
                <p>No requests found</p>
                <small class="text-muted">Submit a request to see it here</small>
            </div>
        `;
        return;
    }

    // Filter by current status
    const filtered = currentFilter === 'ALL' 
        ? requests 
        : requests.filter(req => req.status === currentFilter);

    if (filtered.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">✓</div>
                <p>No ${currentFilter.toLowerCase()} requests</p>
            </div>
        `;
        return;
    }

    // Build table
    const table = `
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th>Request ID</th>
                        <th>Item</th>
                        <th>Quantity</th>
                        <th>Status</th>
                        <th>Requested</th>
                        <th>Reviewed</th>
                        <th>Remarks</th>
                    </tr>
                </thead>
                <tbody>
                    ${filtered.map(req => `
                        <tr>
                            <td><small>${req.requestId}</small></td>
                            <td>${req.itemName || 'N/A'}</td>
                            <td>${req.requestedQuantity}</td>
                            <td>
                                <span class="badge ${getStatusBadgeClass(req.status)}">
                                    ${getStatusLabel(req.status)}
                                </span>
                            </td>
                            <td><small>${formatDate(req.requestedAt)}</small></td>
                            <td><small>${req.reviewedAt ? formatDate(req.reviewedAt) : '-'}</small></td>
                            <td><small>${req.reviewRemarks || '-'}</small></td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
        
        <div class="row mt-3 text-muted small">
            <div class="col-6">
                Total: <strong>${filtered.length}</strong>
            </div>
            <div class="col-6 text-end">
                Last updated: <strong>${new Date().toLocaleTimeString()}</strong>
            </div>
        </div>
    `;

    container.innerHTML = table;
}

// ==================== Status Filter ====================
function filterByStatus(status) {
    currentFilter = status;
    displayRequests(allRequests);
}

// ==================== Error Handling ====================
function showError(message) {
    const errorDiv = document.getElementById('errorMessage');
    errorDiv.textContent = message;
    errorDiv.classList.remove('d-none');
    errorDiv.scrollIntoView({ behavior: 'smooth' });
}

function clearError() {
    const errorDiv = document.getElementById('errorMessage');
    errorDiv.classList.add('d-none');
    errorDiv.textContent = '';
}

