document.addEventListener('DOMContentLoaded', function() {
    function clearLocalStorage() {
        const csrfToken = document.getElementById('csrfToken').value;
        localStorage.setItem('cartItems', JSON.stringify([]));
        localStorage.setItem('totalPrice', '0.00');
        fetch('/user_cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-CSRF-TOKEN': csrfToken
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
