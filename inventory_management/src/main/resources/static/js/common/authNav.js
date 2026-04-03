/**
 * Navbar auth state controller
 */

document.addEventListener('DOMContentLoaded', function () {
    const loginItem = document.getElementById('navLoginItem');
    const logoutItem = document.getElementById('navLogoutItem');
    const userItem = document.getElementById('navUserItem');
    const userText = document.getElementById('navUserText');
    const logoutBtn = document.getElementById('logoutBtn');
    const adminItemsLink = document.getElementById('navItemsAdmin');
    const adminCategoriesLink = document.getElementById('navCategoriesAdmin');
    const requestItem = document.getElementById('navRequestItem');
    const myRequestsItem = document.getElementById('navMyRequestsItem');

    if (!loginItem || !logoutItem || !userItem || !userText) {
        return;
    }

    const authUser = typeof getAuthUser === 'function' ? getAuthUser() : null;

    if (authUser) {
        loginItem.style.display = 'none';
        logoutItem.style.display = '';
        userItem.style.display = '';
        if (requestItem) {
            requestItem.style.display = authUser.role === 'ADMIN' ? 'none' : '';
        }
        if (myRequestsItem) {
            myRequestsItem.style.display = authUser.role === 'ADMIN' ? 'none' : '';
        }
        if (adminItemsLink) {
            adminItemsLink.style.display = authUser.role === 'ADMIN' ? '' : 'none';
        }
        if (adminCategoriesLink) {
            adminCategoriesLink.style.display = authUser.role === 'ADMIN' ? '' : 'none';
        }
        const emailText = authUser.email || 'Signed in';
        const roleText = authUser.role ? ` (${authUser.role})` : '';
        userText.textContent = `${emailText}${roleText}`;
    } else {
        loginItem.style.display = '';
        logoutItem.style.display = 'none';
        userItem.style.display = 'none';
        if (requestItem) {
            requestItem.style.display = '';
        }
        if (myRequestsItem) {
            myRequestsItem.style.display = '';
        }
        if (adminItemsLink) {
            adminItemsLink.style.display = 'none';
        }
        if (adminCategoriesLink) {
            adminCategoriesLink.style.display = 'none';
        }
    }

    if (logoutBtn && typeof logout === 'function') {
        logoutBtn.addEventListener('click', function () {
            logout();
        });
    }
});
