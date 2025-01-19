function printTicket(ticketId) {
    const printContent = document.getElementById('ticket-' + ticketId).innerHTML;
    const originalContent = document.body.innerHTML;
    document.body.innerHTML = printContent;
    window.print();
    document.body.innerHTML = originalContent;
}

function preventBackNavigation(event) {
    event.preventDefault();
    showCustomConfirmation();
}

function showCustomConfirmation() {
    const overlay = document.createElement('div');
    overlay.id = 'custom-confirm-overlay';
    overlay.style.position = 'fixed';
    overlay.style.top = 0;
    overlay.style.left = 0;
    overlay.style.width = '100%';
    overlay.style.height = '100%';
    overlay.style.backgroundColor = 'rgba(0, 0, 0, 0.5)';
    overlay.style.display = 'flex';
    overlay.style.justifyContent = 'center';
    overlay.style.alignItems = 'center';
    overlay.style.zIndex = 1000;

    const confirmBox = document.createElement('div');
    confirmBox.id = 'custom-confirm-box';
    confirmBox.style.backgroundColor = '#fff';
    confirmBox.style.padding = '20px';
    confirmBox.style.borderRadius = '10px';
    confirmBox.style.boxShadow = '0 0 10px rgba(0, 0, 0, 0.25)';
    confirmBox.style.textAlign = 'center';

    const message = document.createElement('p');
    const locale = getLocaleCookie()
    message.textContent = locale === 'ru'
        ? 'Вы уверены, что хотите покинуть эту страницу? Убедитесь, что вы сохранили информацию о приобретенных билетах'
        : 'Are you sure you want to leave this page? Make sure you save your purchased ticket information'

    const confirmButton = document.createElement('button');
    confirmButton.textContent = locale === 'ru' ? 'Да' : 'Confirm';
    confirmButton.style.margin = '10px';
    confirmButton.onclick = function() {
        document.body.removeChild(overlay);
        location.href='/view_available_events';
    };

    const cancelButton = document.createElement('button');
    cancelButton.textContent = locale === 'ru' ? 'Отмена' : 'Cancel';
    cancelButton.style.margin = '10px';
    cancelButton.onclick = function() {
        document.body.removeChild(overlay);
        window.history.pushState(null, null, window.location.href);
    };

    confirmBox.appendChild(message);
    confirmBox.appendChild(confirmButton);
    confirmBox.appendChild(cancelButton);
    overlay.appendChild(confirmBox);

    document.body.appendChild(overlay);
}

window.addEventListener('popstate', function (event) {
    history.pushState(null, "Purchased Tickets", window.location.href);
});