/**
 * Login/Register page logic
 */

document.addEventListener('DOMContentLoaded', function () {
    if (isAuthenticated()) {
        window.location.href = '/dashboard';
        return;
    }

    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');

    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
});

async function handleLogin(event) {
    event.preventDefault();
    clearMessages();

    const submitBtn = document.getElementById('loginSubmitBtn');
    submitBtn.disabled = true;

    try {
        const email = document.getElementById('loginEmail').value.trim();
        const password = document.getElementById('loginPassword').value;

        await loginUser({ email, password });
        showSuccess('Login successful. Redirecting to dashboard...');
        window.location.href = '/dashboard';
    } catch (error) {
        showError(error.message || 'Login failed');
    } finally {
        submitBtn.disabled = false;
    }
}

async function handleRegister(event) {
    event.preventDefault();
    clearMessages();

    const submitBtn = document.getElementById('registerSubmitBtn');
    submitBtn.disabled = true;

    try {
        const payload = {
            fullName: document.getElementById('registerFullName').value.trim(),
            email: document.getElementById('registerEmail').value.trim(),
            password: document.getElementById('registerPassword').value,
        };

        await registerUser(payload);
        showSuccess('Account created and logged in. Redirecting to dashboard...');
        window.location.href = '/dashboard';
    } catch (error) {
        showError(error.message || 'Registration failed');
    } finally {
        submitBtn.disabled = false;
    }
}

function showError(message) {
    const errorBox = document.getElementById('authError');
    errorBox.textContent = message;
    errorBox.classList.remove('d-none');
}

function showSuccess(message) {
    const successBox = document.getElementById('authSuccess');
    successBox.textContent = message;
    successBox.classList.remove('d-none');
}

function clearMessages() {
    const errorBox = document.getElementById('authError');
    const successBox = document.getElementById('authSuccess');

    if (errorBox) {
        errorBox.classList.add('d-none');
        errorBox.textContent = '';
    }

    if (successBox) {
        successBox.classList.add('d-none');
        successBox.textContent = '';
    }
}
