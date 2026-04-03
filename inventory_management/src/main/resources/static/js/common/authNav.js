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
    const adminManageRequestsLink = document.getElementById('navManageRequestsAdmin');
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
        if (adminManageRequestsLink) {
            adminManageRequestsLink.style.display = authUser.role === 'ADMIN' ? '' : 'none';
        }
        const displayName = authUser.fullName || deriveNameFromEmail(authUser.email) || 'Signed in';
        const roleText = authUser.role ? ` (${authUser.role})` : '';
        userText.textContent = `${displayName}${roleText}`;
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
        if (adminManageRequestsLink) {
            adminManageRequestsLink.style.display = 'none';
        }
    }

    if (logoutBtn && typeof logout === 'function') {
        logoutBtn.addEventListener('click', function () {
            logout();
        });
    }
});

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
