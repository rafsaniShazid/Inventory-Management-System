/**
 * Request Page Logic
 * Handles item selection, form submission, and validation
 */

let itemsData = [];

document.addEventListener('DOMContentLoaded', function () {
    if (!requireAuth()) {
        return;
    }
    loadItems();
    setupFormHandlers();
    prefillRequesterInfo();
});

// ==================== Initialize Page ====================
async function loadItems() {
    try {
        itemsData = await getAllItems();

        // Populate item dropdown
        const itemSelect = document.getElementById('itemId');
        itemSelect.innerHTML = '<option value="">-- Select an item --</option>';

        if (Array.isArray(itemsData)) {
            itemsData.forEach(item => {
                const option = document.createElement('option');
                option.value = item.itemId;
                option.textContent = `${item.itemName} (Stock: ${item.stockQuantity})`;
                option.dataset.item = JSON.stringify(item);
                itemSelect.appendChild(option);
            });

            preselectItemFromQuery(itemSelect);
        }
    } catch (error) {
        console.error('Failed to load items:', error);
        alert('Failed to load items. Please refresh the page.');
    }
}

// ==================== Form Handlers ====================
function setupFormHandlers() {
    const form = document.getElementById('requestForm');
    const itemSelect = document.getElementById('itemId');

    // Item selection changes
    itemSelect.addEventListener('change', function () {
        const selectedOption = this.options[this.selectedIndex];
        if (selectedOption.value) {
            const item = JSON.parse(selectedOption.dataset.item);
            populateItemDetails(item);
        } else {
            clearItemDetails();
        }
    });

    // Form submission
    form.addEventListener('submit', async function (e) {
        e.preventDefault();

        // Clear previous messages
        document.getElementById('successMessage').classList.add('d-none');
        document.getElementById('errorMessage').classList.add('d-none');
        document.getElementById('errorMessage').textContent = '';

        // Validate form using browser constraints first.
        if (!form.checkValidity()) {
            e.stopPropagation();
            form.classList.add('was-validated');
            return;
        }

        form.classList.add('was-validated');

        await submitRequestForm();
    });
}

function prefillRequesterInfo() {
    const authUser = typeof getAuthUser === 'function' ? getAuthUser() : null;
    if (!authUser) {
        return;
    }

    const nameInput = document.getElementById('requesterName');
    const emailInput = document.getElementById('requesterEmail');

    const fallbackName = deriveNameFromEmail(authUser.email);
    if (nameInput) {
        nameInput.value = (authUser.fullName || fallbackName || '').trim();
        nameInput.readOnly = true;
    }

    if (emailInput && authUser.email) {
        emailInput.value = authUser.email;
        emailInput.readOnly = true;
    }
}

function preselectItemFromQuery(itemSelect) {
    const params = new URLSearchParams(window.location.search);
    const itemId = params.get('itemId');

    if (!itemId) {
        return;
    }

    const hasOption = Array.from(itemSelect.options).some(option => option.value === itemId);
    if (!hasOption) {
        return;
    }

    itemSelect.value = itemId;
    const selectedOption = itemSelect.options[itemSelect.selectedIndex];
    if (selectedOption && selectedOption.dataset.item) {
        const item = JSON.parse(selectedOption.dataset.item);
        populateItemDetails(item);
    }
}

function deriveNameFromEmail(email) {
    if (!email || !email.includes('@')) {
        return '';
    }

    const localPart = email.split('@')[0];
    return localPart
        .replace(/[._-]+/g, ' ')
        .split(' ')
        .filter(Boolean)
        .map(part => part.charAt(0).toUpperCase() + part.slice(1).toLowerCase())
        .join(' ');
}

// ==================== Item Details ====================
function populateItemDetails(item) {
    document.getElementById('categoryName').value = item.categoryName || 'N/A';
    document.getElementById('stockQuantity').value = `${item.stockQuantity} units`;
    document.getElementById('itemDescription').value = item.description || 'No description';
}

function clearItemDetails() {
    document.getElementById('categoryName').value = '';
    document.getElementById('stockQuantity').value = '';
    document.getElementById('itemDescription').value = '';
}

// ==================== Form Submission ====================
async function submitRequestForm() {
    const form = document.getElementById('requestForm');
    const submitBtn = document.getElementById('submitBtn');
    const submitBtnText = document.getElementById('submitBtnText');
    const submitSpinner = document.getElementById('submitSpinner');

    // Disable button and show loading
    submitBtn.disabled = true;
    submitSpinner.classList.remove('d-none');
    submitBtnText.textContent = 'Submitting...';

    try {
        // Prepare request data
        const requestData = {
            itemId: parseInt(document.getElementById('itemId').value),
            requestedQuantity: parseInt(document.getElementById('requestedQuantity').value),
            requesterName: document.getElementById('requesterName').value.trim(),
            requesterEmail: document.getElementById('requesterEmail').value.trim(),
        };

        // Validate data
        if (!requestData.itemId || !requestData.requestedQuantity || !requestData.requesterName || !requestData.requesterEmail) {
            throw new Error('Please fill in all required fields');
        }

        // Submit to API using the API client function
        const response = await submitRequest(requestData);

        // Clear form
        form.reset();
        form.classList.remove('was-validated');
        clearItemDetails();

        // Show success message
        const successMsg = document.getElementById('successMessage');
        successMsg.textContent = `✓ Request #${response.requestId} submitted successfully! Status: ${response.status}. You can track it in My Requests.`;
        successMsg.classList.remove('d-none');

        // Scroll to success message
        successMsg.scrollIntoView({ behavior: 'smooth' });

        // Reset button
        submitBtn.disabled = false;
        submitSpinner.classList.add('d-none');
        submitBtnText.textContent = 'Submit Request';

        // Auto-clear form after 3 seconds
        setTimeout(() => {
            loadItems();
        }, 2000);
    } catch (error) {
        console.error('Submission error:', error);

        // Extract and display validation errors
        let errorMessage = error.message || 'Failed to submit request';

        if (error.status === 400 && error.data) {
            // Backend validation errors
            const validationErrors = getValidationErrors(error);
            const errorLines = Object.entries(validationErrors).map(
                ([field, message]) => `• ${field}: ${message}`
            );
            if (errorLines.length > 0) {
                errorMessage = 'Validation errors:\n' + errorLines.join('\n');
            }
        }

        // Display error in UI
        const errorDiv = document.getElementById('errorMessage');
        errorDiv.textContent = errorMessage;
        errorDiv.classList.remove('d-none');
        errorDiv.scrollIntoView({ behavior: 'smooth' });

        // Re-enable button
        submitBtn.disabled = false;
        submitSpinner.classList.add('d-none');
        submitBtnText.textContent = 'Submit Request';
    }
}


