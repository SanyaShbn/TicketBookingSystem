function showErrorMessage(message) {
    console.log("showErrorMessage called with message:", message);
    const overlay = document.createElement('div');
    overlay.id = 'error-overlay';
    overlay.style.position = 'fixed';
    overlay.style.top = '0';
    overlay.style.left = '0';
    overlay.style.width = '100%';
    overlay.style.height = '100%';
    overlay.style.backgroundColor = 'rgba(0, 0, 0, 0.5)';
    overlay.style.display = 'flex';
    overlay.style.justifyContent = 'center';
    overlay.style.alignItems = 'center';
    overlay.style.zIndex = '1000';

    const errorBox = document.createElement('div');
    errorBox.id = 'error-box';
    errorBox.style.backgroundColor = '#fff';
    errorBox.style.padding = '20px';
    errorBox.style.borderRadius = '10px';
    errorBox.style.boxShadow = '0 0 10px rgba(0, 0, 0, 0.25)';
    errorBox.style.textAlign = 'center';

    const messageElement = document.createElement('p');
    messageElement.textContent = message;
    messageElement.style.marginBottom = '20px';

    const closeButton = document.createElement('button');
    const locale = getLocaleCookie();
    closeButton.textContent = locale === 'ru' ? 'Закрыть' : 'Close';
    closeButton.style.marginTop = '10px';
    closeButton.onclick = function() {
        document.body.removeChild(overlay);
    };

    errorBox.appendChild(messageElement);
    errorBox.appendChild(closeButton);
    overlay.appendChild(errorBox);

    document.body.appendChild(overlay);

    setTimeout(function() {
        if (overlay.parentNode) {
            document.body.removeChild(overlay);
        }
    }, 5000);
}
