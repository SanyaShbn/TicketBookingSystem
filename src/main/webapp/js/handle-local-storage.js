document.addEventListener('DOMContentLoaded', function() {
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
        }).then(response => response.json());
    }

    function handlePageShow(event) {
        if (event.persisted || (localStorage.getItem('cartItems') && localStorage.getItem('totalPrice'))) {
            clearLocalStorage();
        } else {
            localStorage.setItem('cartItems', JSON.stringify([]));
            localStorage.setItem('totalPrice', '0.00');
        }
    }

    window.addEventListener('pageshow', handlePageShow);

    handlePageShow({ persisted: false });
});
