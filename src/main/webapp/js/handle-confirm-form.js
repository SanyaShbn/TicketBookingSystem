function confirmNavigation(event) {
    const cartItems = JSON.parse(localStorage.getItem('cartItems')) || [];
    if (cartItems.length > 0) {
        event.preventDefault();
        showCustomConfirmation();
    }
    else{
        location.href='/view_available_events';
    }
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
    message.textContent = 'Вы уверены, что хотите покинуть эту страницу?' +
        ' Данные вашей корзины не сохранятся, если вы не завершите покупку';

    const confirmButton = document.createElement('button');
    confirmButton.textContent = 'Да';
    confirmButton.style.margin = '10px';
    confirmButton.onclick = function() {
        document.body.removeChild(overlay);
        location.href='/view_available_events';
    };

    const cancelButton = document.createElement('button');
    cancelButton.textContent = 'Отмена';
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
