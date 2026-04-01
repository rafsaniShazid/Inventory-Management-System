/**
 * Shared API Client for Inventory Management System
 * Handles all REST API calls with error handling and response mapping
 */

const API_BASE_URL = '/api';

/**
 * Generic fetch wrapper with error handling
 */
async function apiCall(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const config = { ...defaultOptions, ...options };

    try {
        const response = await fetch(url, config);
        
        // Handle non-JSON responses
        const contentType = response.headers.get('content-type');
        let data;
        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            data = await response.text();
        }

        if (!response.ok) {
            throw {
                status: response.status,
                message: data.message || data || 'Unknown error',
                data: data.data || data,
            };
        }

        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// ==================== Dashboard APIs ====================

async function getDashboardStats() {
    return apiCall('/items/stats');
}

async function getPendingRequests() {
    return apiCall('/requests/status/PENDING');
}

async function getApprovedRequests() {
    return apiCall('/requests/status/APPROVED');
}

async function getLowStockItems(threshold = 10) {
    return apiCall(`/items/low-stock?threshold=${threshold}`);
}

// ==================== Request APIs ====================

async function getAllItems() {
    return apiCall('/items');
}

async function submitRequest(requestData) {
    return apiCall('/requests', {
        method: 'POST',
        body: JSON.stringify(requestData),
    });
}

async function reviewRequest(requestId, reviewData) {
    return apiCall(`/requests/${requestId}/review`, {
        method: 'PUT',
        body: JSON.stringify(reviewData),
    });
}

// ==================== My Requests APIs ====================

async function getRequestsByEmail(email) {
    return apiCall(`/requests/email/${encodeURIComponent(email)}`);
}

async function getRequestById(requestId) {
    return apiCall(`/requests/${requestId}`);
}

// ==================== Utility Functions ====================

/**
 * Format date string to readable format
 */
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
    });
}

/**
 * Get badge class based on request status
 */
function getStatusBadgeClass(status) {
    switch (status) {
        case 'PENDING':
            return 'badge-pending';
        case 'APPROVED':
            return 'badge-approved';
        case 'REJECTED':
            return 'badge-rejected';
        default:
            return 'bg-secondary';
    }
}

/**
 * Get status label with human-readable format
 */
function getStatusLabel(status) {
    return status ? status.charAt(0).toUpperCase() + status.slice(1).toLowerCase() : 'Unknown';
}

/**
 * Display error message in UI
 */
function showError(message, elementId = null) {
    console.error('Error:', message);
    if (elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.classList.add('active');
            element.textContent = message;
        }
    }
}

/**
 * Clear error message from UI
 */
function clearError(elementId) {
    if (elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.classList.remove('active');
            element.textContent = '';
        }
    }
}

/**
 * Extract validation errors from API response
 */
function getValidationErrors(errorData) {
    if (typeof errorData === 'object' && errorData.data) {
        return errorData.data;
    }
    return {};
}

/**
 * Show loading state
 */
function showLoading(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.classList.add('active');
    }
}

/**
 * Hide loading state
 */
function hideLoading(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.classList.remove('active');
    }
}

