// ============================================
// HealthifyMe Complete Frontend JavaScript
// With Authentication, Charts, Animations
// Compatible with Python 3.13.9 backend
// ============================================

// === API CONFIGURATION ===
const API_CONFIG = {
    SPRING_BOOT: 'http://localhost:8081/api',
    FLASK_CHATBOT: 'http://localhost:5000'
};

// === AUTHENTICATION FUNCTIONS ===

function handleLogout() {
    if (confirm('Are you sure you want to logout?')) {
        localStorage.removeItem('healthifyMeSession');
        showToast('Logged out successfully!', 'success');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 1000);
    }
}

function checkAuthentication() {
    const session = localStorage.getItem('healthifyMeSession');
    if (!session) {
        window.location.href = 'login.html';
        return null;
    }
    return JSON.parse(session);
}

// === SIDEBAR FUNCTIONS ===

function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
        sidebar.classList.toggle('active');
        sidebar.classList.toggle('collapsed');
    }
}

// === THEME TOGGLE ===

function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    
    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('healthifyMeTheme', newTheme);
    
    const icon = document.getElementById('themeIcon');
    if (icon) {
        icon.textContent = newTheme === 'dark' ? '☀️' : '🌙';
    }
    
    showToast(`${newTheme === 'dark' ? 'Dark' : 'Light'} mode activated`, 'success');
}

// Load saved theme
window.addEventListener('load', () => {
    const savedTheme = localStorage.getItem('healthifyMeTheme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);
    
    const icon = document.getElementById('themeIcon');
    if (icon) {
        icon.textContent = savedTheme === 'dark' ? '☀️' : '🌙';
    }
});

// === TOAST NOTIFICATION ===

function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    if (!toast) return;
    
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// === USER DATA MANAGEMENT ===

function getUserData(userId) {
    const users = JSON.parse(localStorage.getItem('healthifyMeUsers') || '[]');
    return users.find(u => u.id === userId) || { meals: [], waterLogs: [], progressLogs: [] };
}

function updateUserData(userId, data) {
    const users = JSON.parse(localStorage.getItem('healthifyMeUsers') || '[]');
    const userIndex = users.findIndex(u => u.id === userId);
    
    if (userIndex !== -1) {
        users[userIndex] = { ...users[userIndex], ...data };
        localStorage.setItem('healthifyMeUsers', JSON.stringify(users));
        return true;
    }
    return false;
}

// === DASHBOARD DATA LOADING ===

function loadUserData() {
    const session = checkAuthentication();
    if (!session) return;
    
    const userData = getUserData(session.userId);
    
    // Update weight display
    const weightElement = document.getElementById('currentWeight');
    if (weightElement) {
        weightElement.textContent = userData.weight ? `${userData.weight}kg` : '--';
    }
}

function loadDashboardData() {
    const session = checkAuthentication();
    if (!session) return;
    
    const userData = getUserData(session.userId);
    const today = new Date().toISOString().split('T')[0];
    
    // Calculate today's totals
    const todayMeals = (userData.meals || []).filter(m => m.date === today);
    const todayWater = (userData.waterLogs || []).find(w => w.date === today);
    
    const totalCalories = todayMeals.reduce((sum, m) => sum + m.calories, 0);
    const totalProtein = todayMeals.reduce((sum, m) => sum + (m.protein || 0), 0);
    const waterIntake = todayWater ? todayWater.liters : 0;
    
    // Update displays
    updateElement('todayCalories', totalCalories);
    updateElement('todayProtein', totalProtein.toFixed(1) + 'g');
    updateElement('todayWater', waterIntake.toFixed(1) + 'L');
    
    // Update progress bars
    updateProgressBar('calorieProgress', totalCalories, 2000);
    updateProgressBar('proteinProgress', totalProtein, 150);
    updateProgressBar('waterProgress', waterIntake, 3);
    
    // Update streak
    updateStreak(userData);
    
    // Update recent meals
    if (document.getElementById('recentMeals')) {
        displayRecentMeals(todayMeals);
    }
}

function updateElement(id, value) {
    const element = document.getElementById(id);
    if (element) {
        element.textContent = value;
    }
}

function updateProgressBar(id, current, goal) {
    const element = document.getElementById(id);
    if (element) {
        const percentage = Math.min((current / goal) * 100, 100);
        element.style.width = percentage + '%';
    }
}

function updateStreak(userData) {
    const streakElement = document.getElementById('streakDays');
    if (!streakElement) return;
    
    // Calculate streak (simplified - counts consecutive days with meals)
    const dates = [...new Set((userData.meals || []).map(m => m.date))].sort().reverse();
    let streak = 0;
    let currentDate = new Date().toISOString().split('T')[0];
    
    for (let date of dates) {
        if (date === currentDate) {
            streak++;
            const dateObj = new Date(currentDate);
            dateObj.setDate(dateObj.getDate() - 1);
            currentDate = dateObj.toISOString().split('T')[0];
        } else {
            break;
        }
    }
    
    streakElement.textContent = `${streak} Day${streak !== 1 ? 's' : ''}`;
}

function displayRecentMeals(meals) {
    const container = document.getElementById('recentMeals');
    if (!container) return;
    
    if (meals.length === 0) {
        container.innerHTML = '<p class="empty-state">No meals logged today. Start tracking!</p>';
        return;
    }
    
    container.innerHTML = meals.slice(-5).reverse().map(meal => `
        <div class="meal-item">
            <span class="meal-icon">${getMealIcon(meal.mealType)}</span>
            <div class="meal-info">
                <strong>${meal.foodName}</strong>
                <small>${meal.mealType} • ${meal.calories} cal</small>
            </div>
        </div>
    `).join('');
}

function getMealIcon(type) {
    const icons = {
        breakfast: '🌅',
        lunch: '🌞',
        dinner: '🌙',
        snack: '🍪'
    };
    return icons[type] || '🍽️';
}

// === GREETING & MOTIVATION ===

function updateGreeting() {
    const greetingElement = document.getElementById('greetingText');
    if (!greetingElement) return;
    
    const session = checkAuthentication();
    if (!session) return;
    
    const username = session.username.split(' ')[0];
    const hour = new Date().getHours();
    
    let greeting;
    if (hour < 12) greeting = 'Good Morning';
    else if (hour < 18) greeting = 'Good Afternoon';
    else greeting = 'Good Evening';
    
    greetingElement.textContent = `${greeting}, ${username}!`;
}

function updateMotivationalQuote() {
    const quoteElement = document.getElementById('motivationalQuote');
    if (!quoteElement) return;
    
    const quotes = [
        "Every meal is a chance to fuel your goals!",
        "Progress, not perfection!",
        "You're one workout away from a good mood!",
        "Stay committed to your journey!",
        "Small steps lead to big changes!",
        "Your only limit is you!",
        "Success is the sum of small efforts repeated daily!"
    ];
    
    const randomQuote = quotes[Math.floor(Math.random() * quotes.length)];
    quoteElement.textContent = randomQuote;
}

// === CHART INITIALIZATION ===

function initializeCharts() {
    if (typeof Chart === 'undefined') return;
    
    const session = checkAuthentication();
    if (!session) return;
    
    const userData = getUserData(session.userId);
    
    // Calorie Chart
    initCalorieChart(userData);
    
    // Macro Chart
    initMacroChart(userData);
    
    // Water Chart
    initWaterChart(userData);
    
    // Weight Chart
    initWeightChart(userData);
}

function initCalorieChart(userData) {
    const ctx = document.getElementById('calorieChart');
    if (!ctx) return;
    
    const last7Days = getLast7Days();
    const calorieData = last7Days.map(date => {
        const dayMeals = (userData.meals || []).filter(m => m.date === date);
        return dayMeals.reduce((sum, m) => sum + m.calories, 0);
    });
    
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: last7Days.map(d => new Date(d).toLocaleDateString('en-US', { weekday: 'short' })),
            datasets: [{
                label: 'Calories',
                data: calorieData,
                borderColor: '#4CAF50',
                backgroundColor: 'rgba(76, 175, 80, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } }
        }
    });
}

function initMacroChart(userData) {
    const ctx = document.getElementById('macroChart');
    if (!ctx) return;
    
    const today = new Date().toISOString().split('T')[0];
    const todayMeals = (userData.meals || []).filter(m => m.date === today);
    
    const totalProtein = todayMeals.reduce((sum, m) => sum + (m.protein || 0), 0);
    const totalCarbs = todayMeals.reduce((sum, m) => sum + (m.carbs || 0), 0);
    const totalFats = todayMeals.reduce((sum, m) => sum + (m.fats || 0), 0);
    
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Protein', 'Carbs', 'Fats'],
            datasets: [{
                data: [totalProtein || 30, totalCarbs || 40, totalFats || 30],
                backgroundColor: ['#4CAF50', '#2196F3', '#FF9800']
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false
        }
    });
}

function initWaterChart(userData) {
    const ctx = document.getElementById('waterChart');
    if (!ctx) return;
    
    const last7Days = getLast7Days();
    const waterData = last7Days.map(date => {
        const log = (userData.waterLogs || []).find(w => w.date === date);
        return log ? log.liters : 0;
    });
    
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: last7Days.map(d => new Date(d).toLocaleDateString('en-US', { weekday: 'short' })),
            datasets: [{
                label: 'Liters',
                data: waterData,
                backgroundColor: '#2196F3'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true, max: 4 } }
        }
    });
}

function initWeightChart(userData) {
    const ctx = document.getElementById('weightChart');
    if (!ctx) return;
    
    const progressLogs = userData.progressLogs || [];
    const labels = progressLogs.map(p => new Date(p.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }));
    const weights = progressLogs.map(p => p.weight);
    
    // If no data, use sample data
    if (weights.length === 0) {
        weights.push(userData.weight || 70, userData.weight || 70);
        labels.push('Start', 'Now');
    }
    
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Weight (kg)',
                data: weights,
                borderColor: '#FF9800',
                backgroundColor: 'rgba(255, 152, 0, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } }
        }
    });
}

function getLast7Days() {
    const days = [];
    for (let i = 6; i >= 0; i--) {
        const date = new Date();
        date.setDate(date.getDate() - i);
        days.push(date.toISOString().split('T')[0]);
    }
    return days;
}

// === FOOD SEARCH ===

async function searchFood() {
    const query = document.getElementById('foodSearch')?.value.trim();
    const resultDiv = document.getElementById('foodResults');
    
    if (!query) {
        showToast('Please enter a food name!', 'error');
        return;
    }
    
    if (resultDiv) {
        resultDiv.innerHTML = '<p style="text-align: center;">🔍 Searching...</p>';
    }
    
    try {
        const response = await fetch(`${API_CONFIG.SPRING_BOOT}/foods/search?name=${encodeURIComponent(query)}`);
        
        if (!response.ok) throw new Error('Search failed');
        
        const foods = await response.json();
        
        if (foods && foods.length > 0 && resultDiv) {
            let html = '<div style="display: grid; gap: 1rem;">';
            foods.forEach(food => {
                html += `
                    <div style="background: var(--bg-light); padding: 1.5rem; border-radius: 12px;">
                        <h4 style="color: var(--primary); margin-bottom: 0.5rem;">${food.food_name}</h4>
                        <p><strong>Calories:</strong> ${food.calories} kcal</p>
                        <p><strong>Protein:</strong> ${food.protein}g | <strong>Carbs:</strong> ${food.carbs}g | <strong>Fats:</strong> ${food.fats}g</p>
                        <p><small>${food.cuisine} • ${food.diet_category} • ${food.type}</small></p>
                    </div>
                `;
            });
            html += '</div>';
            resultDiv.innerHTML = html;
        } else if (resultDiv) {
            resultDiv.innerHTML = `<p style="text-align: center; color: var(--text-secondary);">No results found for "${query}"</p>`;
        }
    } catch (error) {
        console.error('Search error:', error);
        if (resultDiv) {
            resultDiv.innerHTML = '<p style="text-align: center; color: var(--danger);">❌ Search failed. Make sure backend is running.</p>';
        }
    }
}

// === INITIALIZATION ===

document.addEventListener('DOMContentLoaded', function() {
    console.log('✅ HealthifyMe Frontend Loaded');
    
    // Check authentication on protected pages
    const publicPages = ['login.html', 'signup.html'];
    const currentPage = window.location.pathname.split('/').pop();
    
    if (!publicPages.includes(currentPage)) {
        checkAuthentication();
    }
});

// Auto-update dashboard every 30 seconds
setInterval(() => {
    if (document.getElementById('todayCalories')) {
        loadDashboardData();
    }
}, 30000);

console.log('🏋️ HealthifyMe - Ready!');

// === BMI CALCULATOR ===
function calculateBMI() {
    const weight = parseFloat(document.getElementById('bmiWeight').value);
    const height = parseFloat(document.getElementById('bmiHeight').value);
    const resultDiv = document.getElementById('bmiResult');
    
    // Validation
    if (!weight || !height || weight <= 0 || height <= 0) {
        showResult(resultDiv, 'Please enter valid weight and height!', 'error');
        return;
    }
    
    // Convert height from cm to meters
    const heightInMeters = height / 100;
    
    // Calculate BMI
    const bmi = (weight / (heightInMeters * heightInMeters)).toFixed(1);
    
    // Determine category
    let category, color;
    if (bmi < 18.5) {
        category = 'Underweight';
        color = '#2196F3';
    } else if (bmi >= 18.5 && bmi < 25) {
        category = 'Normal';
        color = '#4CAF50';
    } else if (bmi >= 25 && bmi < 30) {
        category = 'Overweight';
        color = '#FF9800';
    } else {
        category = 'Obese';
        color = '#f44336';
    }
    
    // Display result
    const html = `
        <h4 style="color: ${color}">Your BMI: ${bmi}</h4>
        <p><strong>Category:</strong> ${category}</p>
        <p><small>BMI = Weight(kg) / Height(m)²</small></p>
    `;
    
    showResult(resultDiv, html, 'success');
}

// === BMR CALCULATOR ===
function calculateBMR() {
    const weight = parseFloat(document.getElementById('bmrWeight').value);
    const height = parseFloat(document.getElementById('bmrHeight').value);
    const age = parseInt(document.getElementById('bmrAge').value);
    const gender = document.getElementById('bmrGender').value;
    const resultDiv = document.getElementById('bmrResult');
    
    // Validation
    if (!weight || !height || !age || weight <= 0 || height <= 0 || age <= 0) {
        showResult(resultDiv, 'Please enter valid values!', 'error');
        return;
    }
    
    // Mifflin-St Jeor Equation
    let bmr;
    if (gender === 'male') {
        bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
    } else {
        bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
    }
    
    bmr = Math.round(bmr);
    
    // Calculate daily calorie needs based on activity level
    const activityLevels = {
        sedentary: bmr * 1.2,
        light: bmr * 1.375,
        moderate: bmr * 1.55,
        active: bmr * 1.725,
        veryActive: bmr * 1.9
    };
    
    // Display result
    const html = `
        <h4 style="color: #4CAF50">Your BMR: ${bmr} calories/day</h4>
        <p><strong>Daily Calorie Needs:</strong></p>
        <ul style="list-style: none; padding-left: 0;">
            <li>🛋️ Sedentary: ${Math.round(activityLevels.sedentary)} cal</li>
            <li>🚶 Light Activity: ${Math.round(activityLevels.light)} cal</li>
            <li>🏃 Moderate: ${Math.round(activityLevels.moderate)} cal</li>
            <li>💪 Active: ${Math.round(activityLevels.active)} cal</li>
            <li>🏋️ Very Active: ${Math.round(activityLevels.veryActive)} cal</li>
        </ul>
        <p><small>BMR = calories burned at rest</small></p>
    `;
    
    showResult(resultDiv, html, 'success');
}

// === FOOD SEARCH ===
async function searchFood() {
    const searchTerm = document.getElementById('foodSearch').value.trim();
    const resultDiv = document.getElementById('foodResults');
    
    if (!searchTerm) {
        showResult(resultDiv, 'Please enter a food name to search!', 'error');
        return;
    }
    
    showResult(resultDiv, '🔍 Searching...', 'info');
    
    try {
        // Call Spring Boot endpoint (which queries database)
        const response = await fetch(`${API_CONFIG.SPRING_BOOT}/foods/search?name=${encodeURIComponent(searchTerm)}`);
        
        if (!response.ok) {
            throw new Error('Search failed');
        }
        
        const foods = await response.json();
        
        if (foods && foods.length > 0) {
            let html = '<div class="food-results">';
            foods.forEach(food => {
                html += `
                    <div style="padding: 1rem; margin: 0.5rem 0; background: #f5f5f5; border-radius: 8px;">
                        <h4 style="color: #4CAF50;">${food.food_name}</h4>
                        <p><strong>Calories:</strong> ${food.calories} kcal</p>
                        <p><strong>Protein:</strong> ${food.protein}g | <strong>Carbs:</strong> ${food.carbs}g | <strong>Fats:</strong> ${food.fats}g</p>
                        <p><small>${food.cuisine} • ${food.diet_category} • ${food.type}</small></p>
                    </div>
                `;
            });
            html += '</div>';
            showResult(resultDiv, html, 'success');
        } else {
            showResult(resultDiv, `No results found for "${searchTerm}". Try: banana, paneer, chicken, rice`, 'info');
        }
    } catch (error) {
        console.error('Search error:', error);
        showResult(resultDiv, '❌ Search failed. Make sure Spring Boot backend is running on port 8081.', 'error');
    }
}

// === LOG MEAL ===
async function logMeal() {
    const foodName = document.getElementById('mealFood').value.trim();
    const calories = parseInt(document.getElementById('mealCalories').value);
    const mealType = document.getElementById('mealType').value;
    const resultDiv = document.getElementById('mealResult');
    
    if (!foodName || !calories || calories <= 0) {
        showResult(resultDiv, 'Please enter valid food name and calories!', 'error');
        return;
    }
    
    showResult(resultDiv, '📝 Logging meal...', 'info');
    
    try {
        const response = await fetch(`${API_CONFIG.SPRING_BOOT}/meals`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                userId: 1, // Demo user ID
                foodName: foodName,
                calories: calories,
                mealType: mealType,
                date: new Date().toISOString().split('T')[0]
            })
        });
        
        if (response.ok) {
            showResult(resultDiv, `✅ Logged: ${foodName} (${calories} cal) for ${mealType}`, 'success');
            
            // Update today's calories display
            updateTodayCalories(calories);
            
            // Clear form
            document.getElementById('mealFood').value = '';
            document.getElementById('mealCalories').value = '';
        } else {
            throw new Error('Failed to log meal');
        }
    } catch (error) {
        console.error('Log meal error:', error);
        showResult(resultDiv, '❌ Failed to log meal. Backend may be offline.', 'error');
    }
}

// === LOG WATER ===
function logWater() {
    const glasses = parseInt(document.getElementById('waterGlasses').value);
    const resultDiv = document.getElementById('waterResult');
    
    if (!glasses || glasses <= 0) {
        showResult(resultDiv, 'Please enter number of glasses!', 'error');
        return;
    }
    
    const liters = (glasses * 0.25).toFixed(2); // 250ml per glass
    
    // Update display
    document.getElementById('todayWater').textContent = liters;
    
    showResult(resultDiv, `✅ Logged ${glasses} glasses (${liters}L) of water!`, 'success');
    
    // Clear input
    document.getElementById('waterGlasses').value = '';
}

// === CHATBOT FUNCTIONS ===

// Check backend connection
async function checkConnection() {
    const statusDiv = document.getElementById('connectionStatus');
    if (!statusDiv) return;
    
    const statusDot = statusDiv.querySelector('.status-dot');
    const statusText = statusDiv.querySelector('.status-text');
    
    try {
        const response = await fetch(`${API_CONFIG.SPRING_BOOT}/test`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        
        if (response.ok) {
            statusDot.classList.add('online');
            statusText.textContent = 'Connected ✓';
        } else {
            throw new Error('Backend not responding');
        }
    } catch (error) {
        statusDot.classList.add('offline');
        statusText.textContent = 'Offline - Start Spring Boot on port 8081';
    }
}

// Send message to chatbot
async function sendMessage() {
    const input = document.getElementById('userInput');
    const message = input.value.trim();
    
    if (!message) return;
    
    // Display user message
    addMessage(message, 'user');
    
    // Clear input
    input.value = '';
    
    // Show typing indicator
    showTypingIndicator();
    
    try {
        // Send to Spring Boot (which forwards to Flask)
        const response = await fetch(`${API_CONFIG.SPRING_BOOT}/chat`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ message: message })
        });
        
        hideTypingIndicator();
        
        if (!response.ok) {
            throw new Error('Chat request failed');
        }
        
        const data = await response.json();
        
        // Display bot response
        addMessage(data.reply || 'Sorry, I encountered an error.', 'bot');
        
    } catch (error) {
        hideTypingIndicator();
        console.error('Chat error:', error);
        addMessage('❌ Sorry, I cannot connect to the chatbot service. Please ensure:\n1. Spring Boot is running on port 8081\n2. Flask chatbot is running on port 5000', 'bot');
    }
}

// Send quick message
function sendQuickMessage(message) {
    document.getElementById('userInput').value = message;
    sendMessage();
}

// Add message to chat
function addMessage(text, sender) {
    const messagesDiv = document.getElementById('chatMessages');
    
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${sender}-message`;
    
    const avatar = sender === 'bot' ? '🤖' : '👤';
    
    // Convert markdown-like formatting to HTML
    const formattedText = text
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/\n/g, '<br>');
    
    messageDiv.innerHTML = `
        <div class="message-avatar">${avatar}</div>
        <div class="message-content">${formattedText}</div>
    `;
    
    messagesDiv.appendChild(messageDiv);
    
    // Scroll to bottom
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

// Show/hide typing indicator
function showTypingIndicator() {
    const indicator = document.getElementById('typingIndicator');
    if (indicator) indicator.style.display = 'flex';
}

function hideTypingIndicator() {
    const indicator = document.getElementById('typingIndicator');
    if (indicator) indicator.style.display = 'none';
}

// Handle Enter key press in chat input
function handleKeyPress(event) {
    if (event.key === 'Enter') {
        sendMessage();
    }
}

// === UTILITY FUNCTIONS ===

// Show result message
function showResult(element, message, type = 'info') {
    element.innerHTML = message;
    element.className = `result-box show ${type}`;
}

// Update today's calories
function updateTodayCalories(additionalCalories) {
    const caloriesElement = document.getElementById('todayCalories');
    if (caloriesElement) {
        const current = parseInt(caloriesElement.textContent) || 0;
        caloriesElement.textContent = current + additionalCalories;
    }
}

// Logout function
function logout() {
    if (confirm('Are you sure you want to logout?')) {
        // In a real app, clear session/token
        alert('Logged out successfully!');
        // Redirect to login page (if you create one)
        // window.location.href = 'login.html';
    }
}

// === INITIALIZE ON PAGE LOAD ===
document.addEventListener('DOMContentLoaded', function() {
    console.log('✅ HealthifyMe Frontend Loaded');
    console.log('Backend URLs:', API_CONFIG);
    
    // Load initial data if on dashboard
    if (document.getElementById('todayCalories')) {
        loadDashboardData();
    }
});

// Load dashboard data
async function loadDashboardData() {
    // This would fetch actual user data from backend
    // For now, using demo data
    console.log('📊 Loading dashboard data...');
    
    // You can implement actual API calls here
}