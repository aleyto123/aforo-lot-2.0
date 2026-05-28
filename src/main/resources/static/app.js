// API Base URL (Relative since it's served by the same Spring Boot server)
const API_BASE = '';

// State management
let currentUser = null;
let pollingInterval = null;

// DOM Elements
const loginView = document.getElementById('login-view');
const loginForm = document.getElementById('login-form');
const loginError = document.getElementById('login-error');
const dashboardView = document.getElementById('dashboard-view');
const dashboardTitle = document.getElementById('dashboard-title');
const welcomeMsg = document.getElementById('welcome-msg');
const userBadge = document.getElementById('user-badge');
const btnLogout = document.getElementById('btn-logout');

// Admin Panel Elements
const adminPanel = document.getElementById('admin-panel');
const createBusinessForm = document.getElementById('create-business-form');
const businessesTable = document.querySelector('#businesses-table tbody');
const createUserForm = document.getElementById('create-user-form');
const newRoleSelect = document.getElementById('new-role');
const optRoleAdmin = document.getElementById('opt-role-admin');
const newUserBusinessSelect = document.getElementById('new-user-business');
const userCreationError = document.getElementById('user-creation-error');
const userCreationSuccess = document.getElementById('user-creation-success');

// User Panel Elements
const userPanel = document.getElementById('user-panel');
const bizDisplayName = document.getElementById('business-display-name');
const currentCountDisplay = document.getElementById('current-count-display');
const progressPercentage = document.getElementById('progress-percentage');
const progressFill = document.getElementById('progress-fill');
const aforoCircle = document.querySelector('.aforo-circle-outer');
const metricMax = document.getElementById('metric-max');
const metricMin = document.getElementById('metric-min');
const metricEntries = document.getElementById('metric-entries');
const metricExits = document.getElementById('metric-exits');
const badgeProfitability = document.getElementById('badge-profitability');
const badgeCapacity = document.getElementById('badge-capacity');
const btnSimEnter = document.getElementById('btn-sim-enter');
const btnSimExit = document.getElementById('btn-sim-exit');
const btnEndOfDay = document.getElementById('btn-end-of-day');
const btnResetCounts = document.getElementById('btn-reset-counts');
const alertsContainer = document.getElementById('alerts-container');

// Modal Elements
const editBusinessModal = document.getElementById('edit-business-modal');
const editBusinessForm = document.getElementById('edit-business-form');
const modalBizTitle = document.getElementById('modal-biz-title');
const editBizId = document.getElementById('edit-biz-id');
const editBizMax = document.getElementById('edit-biz-max');
const editBizMin = document.getElementById('edit-biz-min');
const btnCloseModal = document.getElementById('btn-close-modal');

/* ==========================================================================
   INITIALIZATION & AUTO-LOGIN
   ========================================================================== */
document.addEventListener('DOMContentLoaded', () => {
    // Check if user is already logged in
    const savedUser = localStorage.getItem('aforo_user');
    if (savedUser) {
        currentUser = JSON.parse(savedUser);
        showDashboard();
    } else {
        showLogin();
    }
});

/* ==========================================================================
   AUTHENTICATION
   ========================================================================== */
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    loginError.classList.add('hidden');
    
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    
    try {
        const response = await fetch(`${API_BASE}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        
        if (response.ok) {
            currentUser = await response.json();
            localStorage.setItem('aforo_user', JSON.stringify(currentUser));
            showDashboard();
        } else {
            const errorText = await response.text();
            showLoginError(errorText || 'Credenciales incorrectas.');
        }
    } catch (err) {
        showLoginError('Error de conexión con el servidor.');
        console.error(err);
    }
});

btnLogout.addEventListener('click', () => {
    localStorage.removeItem('aforo_user');
    currentUser = null;
    if (pollingInterval) {
        clearInterval(pollingInterval);
        pollingInterval = null;
    }
    showLogin();
});

function showLogin() {
    loginView.classList.remove('hidden');
    dashboardView.classList.add('hidden');
    loginForm.reset();
}

function showLoginError(msg) {
    loginError.textContent = msg;
    loginError.classList.remove('hidden');
}

/* ==========================================================================
   NAVIGATION & ROLE ROUTING
   ========================================================================== */
function showDashboard() {
    loginView.classList.add('hidden');
    dashboardView.classList.remove('hidden');
    
    // Set UI messages
    welcomeMsg.textContent = `Hola, ${currentUser.username}`;
    userBadge.textContent = currentUser.role.replace('_', ' ');
    
    // Hide panels by default
    adminPanel.classList.add('hidden');
    userPanel.classList.add('hidden');
    optRoleAdmin.classList.add('hidden');
    
    if (currentUser.role === 'MASTER_ADMIN' || currentUser.role === 'ADMIN') {
        adminPanel.classList.remove('hidden');
        dashboardTitle.textContent = currentUser.role === 'MASTER_ADMIN' ? 'Panel de Control - Master Admin' : 'Panel de Control - Administrador';
        
        // Show create admin option only to MASTER_ADMIN
        if (currentUser.role === 'MASTER_ADMIN') {
            optRoleAdmin.classList.remove('hidden');
        }
        
        // Load admin data
        loadAdminDashboard();
    } else if (currentUser.role === 'USER') {
        userPanel.classList.remove('hidden');
        dashboardTitle.textContent = 'Monitoreo de Aforo de mi Negocio';
        
        // Load user business data & start real-time updates
        loadUserBusinessDashboard();
        startRealTimeUpdates();
    }
}

/* ==========================================================================
   ADMINISTRATOR DASHBOARD LOGIC
   ========================================================================== */
async function loadAdminDashboard() {
    await loadBusinesses();
}

// Fetch and render businesses list
async function loadBusinesses() {
    try {
        const response = await fetch(`${API_BASE}/api/admin/businesses`);
        if (response.ok) {
            const list = await response.json();
            renderBusinessesTable(list);
            populateBusinessSelects(list);
        }
    } catch (err) {
        console.error('Error cargando negocios:', err);
    }
}

function renderBusinessesTable(businesses) {
    businessesTable.innerHTML = '';
    if (businesses.length === 0) {
        businessesTable.innerHTML = `<tr><td colspan="5" style="text-align: center; color: var(--text-muted);">No hay negocios registrados.</td></tr>`;
        return;
    }
    
    businesses.forEach(biz => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${biz.id}</td>
            <td><strong>${biz.name}</strong></td>
            <td>${biz.maxCapacity}</td>
            <td>${biz.minCapacityProfit}</td>
            <td>
                <span class="action-link" onclick="openEditModal(${biz.id}, '${biz.name}', ${biz.maxCapacity}, ${biz.minCapacityProfit})">Configurar</span>
            </td>
        `;
        businessesTable.appendChild(tr);
    });
}

function populateBusinessSelects(businesses) {
    // Populate dropdown in create user form
    newUserBusinessSelect.innerHTML = '<option value="">-- Seleccionar Negocio --</option>';
    businesses.forEach(biz => {
        const opt = document.createElement('option');
        opt.value = biz.id;
        opt.textContent = `${biz.name} (ID: ${biz.id})`;
        newUserBusinessSelect.appendChild(opt);
    });
}

// Create new business
createBusinessForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('biz-name').value.trim();
    const maxCapacity = parseInt(document.getElementById('biz-max').value);
    const minCapacityProfit = parseInt(document.getElementById('biz-min').value);
    
    try {
        const response = await fetch(`${API_BASE}/api/admin/businesses`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, maxCapacity, minCapacityProfit })
        });
        
        if (response.ok) {
            createBusinessForm.reset();
            loadBusinesses();
        } else {
            alert('Error al registrar negocio.');
        }
    } catch (err) {
        console.error(err);
    }
});

// Create new system user
createUserForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    userCreationError.classList.add('hidden');
    userCreationSuccess.classList.add('hidden');
    
    const username = document.getElementById('new-username').value.trim();
    const password = document.getElementById('new-password').value;
    const role = newRoleSelect.value;
    const businessId = newUserBusinessSelect.value;
    
    // Validation: businessId required if USER
    if (role === 'USER' && !businessId) {
        showUserCreationError('Debe asociar la cuenta a un negocio para el rol de Dueño de Negocio.');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/api/admin/users`, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'X-Creator-Id': currentUser.id
            },
            body: JSON.stringify({ username, password, role, businessId })
        });
        
        if (response.ok) {
            createUserForm.reset();
            userCreationSuccess.classList.remove('hidden');
            setTimeout(() => userCreationSuccess.classList.add('hidden'), 4000);
        } else {
            const errText = await response.text();
            showUserCreationError(errText || 'Error al crear cuenta.');
        }
    } catch (err) {
        showUserCreationError('Error al conectar con el servidor.');
        console.error(err);
    }
});

function showUserCreationError(msg) {
    userCreationError.textContent = msg;
    userCreationError.classList.remove('hidden');
}

// Business Config Modal Logic
window.openEditModal = (id, name, max, min) => {
    modalBizTitle.textContent = name;
    editBizId.value = id;
    editBizMax.value = max;
    editBizMin.value = min;
    editBusinessModal.classList.remove('hidden');
};

btnCloseModal.addEventListener('click', () => {
    editBusinessModal.classList.add('hidden');
});

editBusinessForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = editBizId.value;
    const maxCapacity = parseInt(editBizMax.value);
    const minCapacityProfit = parseInt(editBizMin.value);
    
    try {
        const response = await fetch(`${API_BASE}/api/admin/businesses/${id}/config`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ maxCapacity, minCapacityProfit })
        });
        
        if (response.ok) {
            editBusinessModal.classList.add('hidden');
            loadBusinesses();
        } else {
            alert('Error al guardar configuración.');
        }
    } catch (err) {
        console.error(err);
    }
});

/* ==========================================================================
   BUSINESS OWNER (USER) DASHBOARD LOGIC
   ========================================================================== */
async function loadUserBusinessDashboard() {
    if (!currentUser.businessId) return;
    
    try {
        // 1. Fetch current status
        const statusRes = await fetch(`${API_BASE}/api/business/${currentUser.businessId}/status`);
        if (statusRes.ok) {
            const status = await statusRes.json();
            renderUserDashboard(status);
        }
        
        // 2. Fetch alerts
        const alertsRes = await fetch(`${API_BASE}/api/business/${currentUser.businessId}/alerts`);
        if (alertsRes.ok) {
            const alerts = await alertsRes.json();
            renderAlertsList(alerts);
        }
    } catch (err) {
        console.error('Error cargando panel de negocio:', err);
    }
}

function renderUserDashboard(status) {
    bizDisplayName.textContent = status.name;
    currentCountDisplay.textContent = status.currentCount;
    metricMax.textContent = status.maxCapacity;
    metricMin.textContent = status.minCapacityProfit;
    metricEntries.textContent = status.totalEntriesToday;
    metricExits.textContent = status.totalExitsToday;
    
    // Calculate percentage
    const max = status.maxCapacity || 1;
    const count = status.currentCount;
    const percent = Math.min(Math.round((count / max) * 100), 200); // Allow over 100%
    
    progressPercentage.textContent = `${percent}%`;
    progressFill.style.width = `${Math.min(percent, 100)}%`;
    
    // Dynamic color coding based on threshold
    progressFill.className = 'progress-fill';
    aforoCircle.className = 'aforo-circle-outer';
    
    if (percent >= 100) {
        progressFill.classList.add('danger');
        aforoCircle.classList.add('capacity-danger');
    } else if (percent >= 70) {
        progressFill.classList.add('warning');
        aforoCircle.classList.add('capacity-warning');
    } else {
        progressFill.classList.add('normal');
        aforoCircle.classList.add('capacity-normal');
    }
    
    // Profitability Badge
    if (status.meetsProfitability) {
        badgeProfitability.textContent = 'Rentabilidad: CUMPLIDA';
        badgeProfitability.className = 'status-badge alert-success';
    } else {
        badgeProfitability.textContent = `Rentabilidad: NO CUMPLIDA (Mín. ${status.minCapacityProfit})`;
        badgeProfitability.className = 'status-badge alert-warning';
    }
    
    // Capacity Badge
    if (status.isFull) {
        badgeCapacity.textContent = 'Aforo: LLENO / EXCEDIDO';
        badgeCapacity.classList.remove('hidden');
    } else {
        badgeCapacity.classList.add('hidden');
    }
}

function renderAlertsList(alerts) {
    alertsContainer.innerHTML = '';
    
    if (alerts.length === 0) {
        alertsContainer.innerHTML = `<div class="no-alerts">No hay alertas activas en este momento.</div>`;
        return;
    }
    
    alerts.forEach(alert => {
        const item = document.createElement('div');
        item.className = 'alert-item alert-danger';
        
        // Formatear fecha
        const date = new Date(alert.timestamp);
        const timeStr = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
        
        item.innerHTML = `
            <div class="alert-content">
                <span>${alert.message}</span>
                <span class="alert-time">${timeStr}</span>
            </div>
            <button class="btn-resolve" onclick="resolveAlert(${alert.id})">Resolver</button>
        `;
        alertsContainer.appendChild(item);
    });
}

// Simulators (Arduino trigger endpoints)
btnSimEnter.addEventListener('click', async () => {
    if (!currentUser.businessId) return;
    try {
        const response = await fetch(`${API_BASE}/api/iot/business/${currentUser.businessId}/enter`, { method: 'POST' });
        if (response.ok) {
            const updatedBiz = await response.json();
            loadUserBusinessDashboard(); // Reload data immediately
        }
    } catch (err) {
        console.error(err);
    }
});

btnSimExit.addEventListener('click', async () => {
    if (!currentUser.businessId) return;
    try {
        const response = await fetch(`${API_BASE}/api/iot/business/${currentUser.businessId}/exit`, { method: 'POST' });
        if (response.ok) {
            const updatedBiz = await response.json();
            loadUserBusinessDashboard(); // Reload data immediately
        }
    } catch (err) {
        console.error(err);
    }
});

// Operations actions
btnEndOfDay.addEventListener('click', async () => {
    if (!currentUser.businessId) return;
    if (!confirm('¿Está seguro de que desea realizar el Cierre de Fin de Día? Esto comparará las entradas y salidas de hoy.')) return;
    
    try {
        const response = await fetch(`${API_BASE}/api/business/${currentUser.businessId}/end-of-day`, { method: 'POST' });
        if (response.ok) {
            const result = await response.json();
            alert(result.message);
            loadUserBusinessDashboard();
        }
    } catch (err) {
        console.error(err);
    }
});

btnResetCounts.addEventListener('click', async () => {
    if (!currentUser.businessId) return;
    if (!confirm('¿Está seguro de que desea reiniciar a cero todos los contadores de hoy?')) return;
    
    try {
        const response = await fetch(`${API_BASE}/api/business/${currentUser.businessId}/reset`, { method: 'POST' });
        if (response.ok) {
            loadUserBusinessDashboard();
        }
    } catch (err) {
        console.error(err);
    }
});

// Resolve alert
window.resolveAlert = async (alertId) => {
    try {
        const response = await fetch(`${API_BASE}/api/business/${currentUser.businessId}/alerts/${alertId}/resolve`, {
            method: 'PUT'
        });
        if (response.ok) {
            loadUserBusinessDashboard();
        } else {
            alert('No se pudo resolver la alerta.');
        }
    } catch (err) {
        console.error(err);
    }
};

/* ==========================================================================
   REAL-TIME UPDATES (POLLING)
   ========================================================================== */
function startRealTimeUpdates() {
    if (pollingInterval) {
        clearInterval(pollingInterval);
    }
    // Poll business status & alerts every 2.5 seconds
    pollingInterval = setInterval(() => {
        loadUserBusinessDashboard();
    }, 2500);
}
