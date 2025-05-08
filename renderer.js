const { ipcRenderer } = require('electron');

const webview = document.getElementById('webview');
const urlBar = document.getElementById('url-bar');
const backButton = document.getElementById('back-button');
const forwardButton = document.getElementById('forward-button');
const refreshButton = document.getElementById('refresh-button');
const tabsContainer = document.getElementById('tabs-container');

// Navigation controls
backButton.addEventListener('click', () => {
    if (webview.canGoBack()) {
        webview.goBack();
    }
});

forwardButton.addEventListener('click', () => {
    if (webview.canGoForward()) {
        webview.goForward();
    }
});

refreshButton.addEventListener('click', () => {
    webview.reload();
});

// URL bar handling
urlBar.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        let url = urlBar.value;
        if (!url.startsWith('http://') && !url.startsWith('https://')) {
            if (url.includes('.')) {
                url = 'http://' + url;
            } else {
                url = 'https://www.google.com/search?q=' + encodeURIComponent(url);
            }
        }
        webview.loadURL(url);
    }
});

// Webview events
webview.addEventListener('did-start-loading', () => {
    urlBar.classList.add('loading');
});

webview.addEventListener('did-stop-loading', () => {
    urlBar.classList.remove('loading');
    urlBar.value = webview.getURL();
    updateNavigationButtons();
});

webview.addEventListener('did-navigate', () => {
    urlBar.value = webview.getURL();
    updateNavigationButtons();
});

function updateNavigationButtons() {
    backButton.disabled = !webview.canGoBack();
    forwardButton.disabled = !webview.canGoForward();
}

// Tab management
function createNewTab(url = 'about:blank') {
    const tab = document.createElement('div');
    tab.className = 'tab';
    tab.textContent = 'New Tab';
    tabsContainer.appendChild(tab);
    
    tab.addEventListener('click', () => {
        document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        // TODO: Implement tab switching logic
    });
}

// Initialize
createNewTab(); 