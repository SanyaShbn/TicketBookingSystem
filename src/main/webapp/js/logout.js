document.addEventListener('DOMContentLoaded', function () {
    function clearLocalStorage() {
        localStorage.setItem('cartItems', JSON.stringify([]));
        localStorage.setItem('totalPrice', '0.00');
        fetch('/user_cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                action: 'clear'
            })
        }).then(response => response.json())
    }

    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', clearLocalStorage);
    }
});
