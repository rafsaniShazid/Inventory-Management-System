/**
 * Admin item management page
 */

let itemsCache = [];

document.addEventListener('DOMContentLoaded', async function () {
    if (!requireAuth()) {
        return;
    }

    const authUser = getAuthUser();
    if (!authUser || authUser.role !== 'ADMIN') {
        window.location.href = '/dashboard';
        return;
    }

    setupEventHandlers();
    await Promise.all([loadCategories(), loadItems()]);
});

function setupEventHandlers() {
    const form = document.getElementById('itemForm');
    const refreshBtn = document.getElementById('refreshItemsBtn');
    const cancelBtn = document.getElementById('cancelEditBtn');

    form.addEventListener('submit', handleSaveItem);
    refreshBtn.addEventListener('click', loadItems);
    cancelBtn.addEventListener('click', resetForm);
}

async function loadCategories() {
    const categorySelect = document.getElementById('categoryId');
    categorySelect.innerHTML = '<option value="">Select category</option>';

    try {
        const categories = await getAllCategories();
        if (!Array.isArray(categories)) {
            return;
        }

        categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category.categoryId;
            option.textContent = category.categoryName;
            categorySelect.appendChild(option);
        });
    } catch (error) {
        showPageError(error.message || 'Failed to load categories');
    }
}

async function loadItems() {
    showLoading('itemsLoader');
    try {
        const items = await getAllItems();
        itemsCache = Array.isArray(items) ? items : [];
        renderItemsTable(itemsCache);
    } catch (error) {
        showPageError(error.message || 'Failed to load items');
        renderItemsTable([]);
    } finally {
        hideLoading('itemsLoader');
    }
}

function renderItemsTable(items) {
    const container = document.getElementById('itemsTableContainer');

    if (!Array.isArray(items) || items.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📦</div>
                <p>No items found</p>
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
                        <th>Name</th>
                        <th>Category</th>
                        <th>Stock</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${items.map(item => `
                        <tr>
                            <td><small>${item.itemId}</small></td>
                            <td>
                                <strong>${escapeHtml(item.itemName || 'N/A')}</strong>
                                <div class="text-muted small">${escapeHtml(item.description || '')}</div>
                            </td>
                            <td>${escapeHtml(item.categoryName || 'N/A')}</td>
                            <td>${item.stockQuantity ?? 0}</td>
                            <td>
                                <button class="btn btn-sm btn-outline-primary me-2" onclick="startEditItem(${item.itemId})">Edit</button>
                                <button class="btn btn-sm btn-outline-danger" onclick="confirmDeleteItem(${item.itemId})">Delete</button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
    `;
}

async function handleSaveItem(event) {
    event.preventDefault();
    clearPageMessages();

    const itemIdValue = document.getElementById('itemId').value;
    const payload = {
        itemName: document.getElementById('itemName').value.trim(),
        description: document.getElementById('description').value.trim(),
        stockQuantity: parseInt(document.getElementById('stockQuantity').value, 10),
        categoryId: parseInt(document.getElementById('categoryId').value, 10),
    };

    if (!payload.itemName || Number.isNaN(payload.stockQuantity) || Number.isNaN(payload.categoryId)) {
        showPageError('Please provide valid item name, stock quantity, and category.');
        return;
    }

    try {
        if (itemIdValue) {
            await updateItem(parseInt(itemIdValue, 10), payload);
            showPageSuccess('Item updated successfully.');
        } else {
            await createItem(payload);
            showPageSuccess('Item created successfully.');
        }

        resetForm();
        await loadItems();
    } catch (error) {
        showPageError(error.message || 'Failed to save item.');
    }
}

function startEditItem(itemId) {
    const item = itemsCache.find(x => x.itemId === itemId);
    if (!item) {
        showPageError('Item not found for editing.');
        return;
    }

    document.getElementById('itemId').value = item.itemId;
    document.getElementById('itemName').value = item.itemName || '';
    document.getElementById('description').value = item.description || '';
    document.getElementById('stockQuantity').value = item.stockQuantity ?? 0;
    document.getElementById('categoryId').value = item.categoryId ?? '';

    document.getElementById('itemFormTitle').textContent = `Edit Item #${item.itemId}`;
    document.getElementById('saveItemBtn').textContent = 'Update Item';
    document.getElementById('cancelEditBtn').style.display = '';
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function confirmDeleteItem(itemId) {
    clearPageMessages();

    const confirmed = window.confirm(`Delete item #${itemId}? This cannot be undone.`);
    if (!confirmed) {
        return;
    }

    try {
        await deleteItem(itemId);
        showPageSuccess(`Item #${itemId} deleted successfully.`);
        if (document.getElementById('itemId').value === String(itemId)) {
            resetForm();
        }
        await loadItems();
    } catch (error) {
        showPageError(error.message || 'Failed to delete item.');
    }
}

function resetForm() {
    document.getElementById('itemForm').reset();
    document.getElementById('itemId').value = '';
    document.getElementById('itemFormTitle').textContent = 'Add New Item';
    document.getElementById('saveItemBtn').textContent = 'Save Item';
    document.getElementById('cancelEditBtn').style.display = 'none';
}

function showPageError(message) {
    const el = document.getElementById('itemsError');
    const success = document.getElementById('itemsSuccess');
    success.classList.add('d-none');
    success.textContent = '';
    el.textContent = message;
    el.classList.remove('d-none');
}

function showPageSuccess(message) {
    const el = document.getElementById('itemsSuccess');
    const error = document.getElementById('itemsError');
    error.classList.add('d-none');
    error.textContent = '';
    el.textContent = message;
    el.classList.remove('d-none');
}

function clearPageMessages() {
    document.getElementById('itemsError').classList.add('d-none');
    document.getElementById('itemsError').textContent = '';
    document.getElementById('itemsSuccess').classList.add('d-none');
    document.getElementById('itemsSuccess').textContent = '';
}

function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}
