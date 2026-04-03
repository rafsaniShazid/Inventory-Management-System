/**
 * Navbar auth state controller
 */

document.addEventListener('DOMContentLoaded', function () {
    const loginItem = document.getElementById('navLoginItem');
    const logoutItem = document.getElementById('navLogoutItem');
    const userItem = document.getElementById('navUserItem');
    const userText = document.getElementById('navUserText');
    const logoutBtn = document.getElementById('logoutBtn');

    if (!loginItem || !logoutItem || !userItem || !userText) {
        return;
    }

    const authUser = typeof getAuthUser === 'function' ? getAuthUser() : null;

    if (authUser) {
        loginItem.style.display = 'none';
        logoutItem.style.display = '';
        userItem.style.display = '';
        const emailText = authUser.email || 'Signed in';
        const roleText = authUser.role ? ` (${authUser.role})` : '';
        userText.textContent = `${emailText}${roleText}`;
    } else {
        loginItem.style.display = '';
        logoutItem.style.display = 'none';
        userItem.style.display = 'none';
    }

    if (logoutBtn && typeof logout === 'function') {
        logoutBtn.addEventListener('click', function () {
            logout();
        });
    }
});
