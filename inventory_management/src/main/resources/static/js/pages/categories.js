/**
 * Admin category management page
 */

let categoriesCache = [];

document.addEventListener('DOMContentLoaded', async function () {
    if (!requireAuth()) {
        return;
    }

    const authUser = getAuthUser();
    if (!authUser || authUser.role !== 'ADMIN') {
        window.location.href = '/dashboard';
        return;
    }

    setupCategoryEventHandlers();
    await loadCategoriesTable();
});

function setupCategoryEventHandlers() {
    const form = document.getElementById('categoryForm');
    const refreshBtn = document.getElementById('refreshCategoriesBtn');
    const cancelBtn = document.getElementById('cancelCategoryEditBtn');

    form.addEventListener('submit', handleSaveCategory);
    refreshBtn.addEventListener('click', loadCategoriesTable);
    cancelBtn.addEventListener('click', resetCategoryForm);
}

async function loadCategoriesTable() {
    showLoading('categoriesLoader');
    try {
        const categories = await getAllCategories();
        categoriesCache = Array.isArray(categories) ? categories : [];
        renderCategoriesTable(categoriesCache);
    } catch (error) {
        showCategoriesError(error.message || 'Failed to load categories');
        renderCategoriesTable([]);
    } finally {
        hideLoading('categoriesLoader');
    }
}

function renderCategoriesTable(categories) {
    const container = document.getElementById('categoriesTableContainer');

    if (!Array.isArray(categories) || categories.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">🗂️</div>
                <p>No categories found</p>
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
                        <th>Description</th>
                        <th>Items</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${categories.map(category => `
                        <tr>
                            <td><small>${category.categoryId}</small></td>
                            <td><strong>${escapeHtml(category.categoryName || 'N/A')}</strong></td>
                            <td>${escapeHtml(category.description || '-')}</td>
                            <td>${category.itemCount ?? 0}</td>
                            <td>
                                <button class="btn btn-sm btn-outline-primary me-2" onclick="startCategoryEdit(${category.categoryId})">Edit</button>
                                <button class="btn btn-sm btn-outline-danger" onclick="confirmCategoryDelete(${category.categoryId})">Delete</button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
    `;
}

async function handleSaveCategory(event) {
    event.preventDefault();
    clearCategoryMessages();

    const categoryIdValue = document.getElementById('categoryId').value;
    const payload = {
        categoryName: document.getElementById('categoryName').value.trim(),
        description: document.getElementById('categoryDescription').value.trim(),
    };

    if (!payload.categoryName) {
        showCategoriesError('Category name is required.');
        return;
    }

    try {
        if (categoryIdValue) {
            await updateCategory(parseInt(categoryIdValue, 10), payload);
            showCategoriesSuccess('Category updated successfully.');
        } else {
            await createCategory(payload);
            showCategoriesSuccess('Category created successfully.');
        }

        resetCategoryForm();
        await loadCategoriesTable();
    } catch (error) {
        showCategoriesError(error.message || 'Failed to save category.');
    }
}

function startCategoryEdit(categoryId) {
    const category = categoriesCache.find(x => x.categoryId === categoryId);
    if (!category) {
        showCategoriesError('Category not found for editing.');
        return;
    }

    document.getElementById('categoryId').value = category.categoryId;
    document.getElementById('categoryName').value = category.categoryName || '';
    document.getElementById('categoryDescription').value = category.description || '';

    document.getElementById('categoryFormTitle').textContent = `Edit Category #${category.categoryId}`;
    document.getElementById('saveCategoryBtn').textContent = 'Update Category';
    document.getElementById('cancelCategoryEditBtn').style.display = '';
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function confirmCategoryDelete(categoryId) {
    clearCategoryMessages();

    const confirmed = window.confirm(`Delete category #${categoryId}? If items exist in this category, deletion will fail.`);
    if (!confirmed) {
        return;
    }

    try {
        await deleteCategory(categoryId);
        showCategoriesSuccess(`Category #${categoryId} deleted successfully.`);
        if (document.getElementById('categoryId').value === String(categoryId)) {
            resetCategoryForm();
        }
        await loadCategoriesTable();
    } catch (error) {
        showCategoriesError(error.message || 'Failed to delete category.');
    }
}

function resetCategoryForm() {
    document.getElementById('categoryForm').reset();
    document.getElementById('categoryId').value = '';
    document.getElementById('categoryFormTitle').textContent = 'Add New Category';
    document.getElementById('saveCategoryBtn').textContent = 'Save Category';
    document.getElementById('cancelCategoryEditBtn').style.display = 'none';
}

function showCategoriesError(message) {
    const el = document.getElementById('categoriesError');
    const success = document.getElementById('categoriesSuccess');
    success.classList.add('d-none');
    success.textContent = '';
    el.textContent = message;
    el.classList.remove('d-none');
}

function showCategoriesSuccess(message) {
    const el = document.getElementById('categoriesSuccess');
    const error = document.getElementById('categoriesError');
    error.classList.add('d-none');
    error.textContent = '';
    el.textContent = message;
    el.classList.remove('d-none');
}

function clearCategoryMessages() {
    document.getElementById('categoriesError').classList.add('d-none');
    document.getElementById('categoriesError').textContent = '';
    document.getElementById('categoriesSuccess').classList.add('d-none');
    document.getElementById('categoriesSuccess').textContent = '';
}

function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}
