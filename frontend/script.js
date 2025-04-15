// ====== Configuration ======
const API_BASE = 'http://localhost:8080/api';

// ====== Auth Utilities ======
function saveToken(token) {
    localStorage.setItem('jwt', token);
}
function getToken() {
    return localStorage.getItem('jwt');
}
function clearToken() {
    localStorage.removeItem('jwt');
}
function isLoggedIn() {
    return !!getToken();
}
function authHeader() {
    const token = getToken();
    return token ? { 'Authorization': 'Bearer ' + token } : {};
}

// ====== DOM Elements ======
const loginForm = document.getElementById('login-form');
const signupForm = document.getElementById('signup-form');
const loginBtn = document.getElementById('login-btn');
const signupBtn = document.getElementById('signup-btn');
const showSignup = document.getElementById('show-signup');
const showLogin = document.getElementById('show-login');
const loginMsg = document.getElementById('login-message');
const signupMsg = document.getElementById('signup-message');
const authSection = document.getElementById('auth-section');
const userSection = document.getElementById('user-section');
const welcomeUser = document.getElementById('welcome-user');
const logoutBtn = document.getElementById('logout-btn');
const tabBtns = document.querySelectorAll('.tab-btn');
const productsTab = document.getElementById('products-tab');
const cartTab = document.getElementById('cart-tab');
const ordersTab = document.getElementById('orders-tab');

// ====== Auth Handlers ======
showSignup.onclick = (e) => {
    e.preventDefault();
    loginForm.style.display = 'none';
    signupForm.style.display = 'flex';
    loginMsg.textContent = '';
    signupMsg.textContent = '';
};
showLogin.onclick = (e) => {
    e.preventDefault();
    signupForm.style.display = 'none';
    loginForm.style.display = 'flex';
    loginMsg.textContent = '';
    signupMsg.textContent = '';
};

loginBtn.onclick = async () => {
    loginMsg.textContent = '';
    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value;
    if (!username || !password) {
        loginMsg.textContent = 'Please enter username and password.';
        return;
    }
    try {
        const res = await fetch(API_BASE + '/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: username, password })
        });
        const data = await res.json();
        if (res.ok && data.token) {
            saveToken(data.token);
            showUserSection(username);
        } else {
            loginMsg.textContent = data.message || 'Login failed.';
        }
    } catch (e) {
        loginMsg.textContent = 'Network error.';
    }
};

signupBtn.onclick = async () => {
    signupMsg.textContent = '';
    const username = document.getElementById('signup-username').value.trim();
    const password = document.getElementById('signup-password').value;
    if (!username || !password) {
        signupMsg.textContent = 'Please enter username and password.';
        return;
    }
    try {
        const res = await fetch(API_BASE + '/auth/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const data = await res.json();
        if (res.ok && data.token) {
            saveToken(data.token);
            showUserSection(username);
        } else {
            signupMsg.textContent = data.message || 'Signup failed.';
        }
    } catch (e) {
        signupMsg.textContent = 'Network error.';
    }
};

logoutBtn.onclick = () => {
    clearToken();
    showAuthSection();
};

// ====== UI Switching ======
function showUserSection(username) {
    authSection.style.display = 'none';
    userSection.style.display = 'block';
    welcomeUser.textContent = 'Welcome, ' + (username || getUsernameFromToken() || 'User');
    switchTab('products');
    loadProducts();
}
function showAuthSection() {
    authSection.style.display = 'block';
    userSection.style.display = 'none';
    loginForm.style.display = 'flex';
    signupForm.style.display = 'none';
    loginMsg.textContent = '';
    signupMsg.textContent = '';
    document.getElementById('login-username').value = '';
    document.getElementById('login-password').value = '';
    document.getElementById('signup-username').value = '';
    document.getElementById('signup-password').value = '';
}

// ====== Tab Handling ======
tabBtns.forEach(btn => {
    btn.onclick = () => {
        switchTab(btn.dataset.tab);
    };
});
function switchTab(tab) {
    tabBtns.forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-panel').forEach(panel => panel.style.display = 'none');
    document.querySelector('.tab-btn[data-tab="' + tab + '"]').classList.add('active');
    document.getElementById(tab + '-tab').style.display = 'block';
    if (tab === 'products') loadProducts();
    if (tab === 'cart') loadCart();
    if (tab === 'orders') loadOrders();
}

// ====== Product Listing ======
async function loadProducts() {
    productsTab.innerHTML = '<div>Loading products...</div>';
    try {
        const res = await fetch(API_BASE + '/products');
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Failed to load products.');
        if (!Array.isArray(data)) {
            productsTab.innerHTML = '<div>No products found.</div>';
            return;
        }
        productsTab.innerHTML = '<ul class="product-list">' +
            data.map(product => `
                <li class="product-item">
                    <div class="product-title">${escapeHtml(product.name)}</div>
                    <div>${escapeHtml(product.description || '')}</div>
                    <div>Price: $${product.price != null ? product.price : 'N/A'}</div>
                    <div class="product-actions">
                        <button class="small" onclick="showProductDetails(${product.id})">Details</button>
                        <button class="small" onclick="addToCart(${product.id})">Add to Cart</button>
                    </div>
                </li>
            `).join('') +
            '</ul>';
    } catch (e) {
        productsTab.innerHTML = '<div class="form-message">' + e.message + '</div>';
    }
}

window.showProductDetails = async function(productId) {
    productsTab.innerHTML = '<div>Loading product details...</div>';
    try {
        const res = await fetch(API_BASE + '/products/' + productId);
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Failed to load product.');
        productsTab.innerHTML = `
            <div>
                <button class="small" onclick="loadProducts()">Back to Products</button>
                <h3>${escapeHtml(data.name)}</h3>
                <div>${escapeHtml(data.description || '')}</div>
                <div>Price: $${data.price != null ? data.price : 'N/A'}</div>
                <div>
                    <input type="number" id="detail-qty" value="1" min="1" style="width:60px;">
                    <button class="small" onclick="addToCart(${data.id}, true)">Add to Cart</button>
                </div>
                <div id="detail-message" class="form-message"></div>
            </div>
        `;
    } catch (e) {
        productsTab.innerHTML = '<div class="form-message">' + e.message + '</div>';
    }
};

window.addToCart = async function(productId, fromDetail) {
    if (!isLoggedIn()) {
        alert('Please login to add items to cart.');
        return;
    }
    let quantity = 1;
    if (fromDetail) {
        quantity = parseInt(document.getElementById('detail-qty').value, 10) || 1;
    }
    try {
        const res = await fetch(API_BASE + '/cart/items', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                ...authHeader()
            },
            body: JSON.stringify({ productId, quantity })
        });
        const data = await res.json();
        if (res.ok) {
            if (fromDetail) {
                document.getElementById('detail-message').textContent = 'Added to cart!';
            }
            else {
                loadProducts();
                alert('Added to cart!');
            }
        } else {
            if (fromDetail) {
                document.getElementById('detail-message').textContent = data.message || 'Failed to add to cart.';
            } else {
                alert(data.message || 'Failed to add to cart.');
            }
        }
    } catch (e) {
        if (fromDetail) {
            document.getElementById('detail-message').textContent = 'Network error.';
        } else {
            alert('Network error.');
        }
    }
};

// ====== Cart Management ======
async function loadCart() {
    if (!isLoggedIn()) {
        cartTab.innerHTML = '<div class="form-message">Please login to view your cart.</div>';
        return;
    }
    cartTab.innerHTML = '<div>Loading cart...</div>';
    try {
        const res = await fetch(API_BASE + '/cart', {
            headers: authHeader()
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Failed to load cart.');
        if (!data.items || !data.items.length) {
            cartTab.innerHTML = '<div>Your cart is empty.</div>';
            return;
        }
        cartTab.innerHTML = `
            <ul class="cart-list">
                ${data.items.map(item => `
                    <li class="cart-item">
                        <div>${escapeHtml(item.productName)} (x${item.quantity}) - $${item.price != null ? item.price : 'N/A'}</div>
                        <div class="cart-actions">
                            <input type="number" min="1" value="${item.quantity}" id="qty-${item.id}" style="width:60px;">
                            <button class="small" onclick="updateCartItem(${item.id})">Update</button>
                            <button class="small" onclick="removeCartItem(${item.id})">Remove</button>
                        </div>
                    </li>
                `).join('')}
            </ul>
            <div>Total: $${data.total != null ? data.total : 'N/A'}</div>
            <button onclick="placeOrder()" style="margin-top:10px;">Place Order</button>
            <div id="cart-message" class="form-message"></div>
        `;
    } catch (e) {
        cartTab.innerHTML = '<div class="form-message">' + e.message + '</div>';
    }
}

window.updateCartItem = async function(itemId) {
    const qty = parseInt(document.getElementById('qty-' + itemId).value, 10) || 1;
    try {
        const res = await fetch(API_BASE + '/cart/items/' + itemId, {
            method: 'PUT',
            headers: { 
                'Content-Type': 'application/json',
                ...authHeader()
            },
            body: JSON.stringify({ quantity: qty })
        });
        const data = await res.json();
        if (res.ok) {
            loadCart();
        } else {
            document.getElementById('cart-message').textContent = data.message || 'Failed to update item.';
        }
    } catch (e) {
        document.getElementById('cart-message').textContent = 'Network error.';
    }
};

window.removeCartItem = async function(itemId) {
    try {
        const res = await fetch(API_BASE + '/cart/items/' + itemId, {
            method: 'DELETE',
            headers: authHeader()
        });
        if (res.ok) {
            loadCart();
        } else {
            const data = await res.json();
            document.getElementById('cart-message').textContent = data.message || 'Failed to remove item.';
        }
    } catch (e) {
        document.getElementById('cart-message').textContent = 'Network error.';
    }
};

window.placeOrder = async function() {
    try {
        const res = await fetch(API_BASE + '/orders', {
            method: 'POST',
            headers: authHeader()
        });
        const data = await res.json();
        if (res.ok) {
            loadCart();
            switchTab('orders');
        } else {
            document.getElementById('cart-message').textContent = data.message || 'Failed to place order.';
        }
    } catch (e) {
        document.getElementById('cart-message').textContent = 'Network error.';
    }
};

// ====== Order History ======
async function loadOrders() {
    if (!isLoggedIn()) {
        ordersTab.innerHTML = '<div class="form-message">Please login to view your orders.</div>';
        return;
    }
    ordersTab.innerHTML = '<div>Loading orders...</div>';
    try {
        const res = await fetch(API_BASE + '/orders', {
            headers: authHeader()
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Failed to load orders.');
        if (!Array.isArray(data) || !data.length) {
            ordersTab.innerHTML = '<div>No orders found.</div>';
            return;
        }
        ordersTab.innerHTML = `
            <ul class="order-list">
                ${data.map(order => `
                    <li class="order-item">
                        <div>Order #${order.id} - ${escapeHtml(order.status || '')} - $${order.total != null ? order.total : 'N/A'}</div>
                        <button class="small" onclick="showOrderDetails(${order.id})">Details</button>
                    </li>
                `).join('')}
            </ul>
            <div id="order-details"></div>
        `;
    } catch (e) {
        ordersTab.innerHTML = '<div class="form-message">' + e.message + '</div>';
    }
}

window.showOrderDetails = async function(orderId) {
    const detailsDiv = document.getElementById('order-details');
    detailsDiv.innerHTML = 'Loading order details...';
    try {
        const res = await fetch(API_BASE + '/orders/' + orderId, {
            headers: authHeader()
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Failed to load order.');
        detailsDiv.innerHTML = `
            <div>
                <h4>Order #${data.id}</h4>
                <div>Status: ${escapeHtml(data.status || '')}</div>
                <div>Total: $${data.total != null ? data.total : 'N/A'}</div>
                <div>Items:</div>
                <ul>
                    ${(data.items || []).map(item => `
                        <li>${escapeHtml(item.productName)} (x${item.quantity}) - $${item.price != null ? item.price : 'N/A'}</li>
                    `).join('')}
                </ul>
            </div>
        `;
    } catch (e) {
        detailsDiv.innerHTML = '<div class="form-message">' + e.message + '</div>';
    }
};

// ====== Helper Functions ======
function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// Optionally extract username from JWT payload (if present)
function getUsernameFromToken() {
    const token = getToken();
    if (!token) return null;
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.sub || payload.username || null;
    } catch {
        return null;
    }
}

// ====== Initial Load ======
(function init() {
    if (isLoggedIn()) {
        showUserSection(getUsernameFromToken());
    } else {
        showAuthSection();
    }
})();