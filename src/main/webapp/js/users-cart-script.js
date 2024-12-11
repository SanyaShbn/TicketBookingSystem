document.addEventListener('DOMContentLoaded', function () {
    const cartItems = JSON.parse(localStorage.getItem('cartItems'));
    const seatElements = document.querySelectorAll('.arena-seat.available');
    const cartList = document.getElementById('cartItems');
    const totalPriceElement = document.getElementById('totalPrice');
    const cartCountElement = document.getElementById('cartCount');
    const checkoutButton = document.getElementById('checkoutButton');
    const clearCartButton = document.getElementById('clearCartButton');
    let totalPrice = parseFloat(localStorage.getItem('totalPrice')) || 0;

    function updateTotalPrice() {
        let totalPrice = 0;
        cartItems.forEach(seatId => {
            const seatElement = document.querySelector(`.arena-seat[data-seat-id="${seatId}"]`);
            if (seatElement) {
                totalPrice += parseFloat(seatElement.getAttribute('data-seat-price'));
            } else {
                console.error(`Seat element with id ${seatId} not found`);
            }
        });
        totalPriceElement.textContent = totalPrice.toFixed(2);
        localStorage.setItem('totalPrice', totalPrice.toFixed(2));
    }

    function updateCartMessage() {
        const emptyCartMessage = document.getElementById('emptyCartMessage');
        if (cartItems.length === 0) {
            if (!emptyCartMessage) {
                const message = document.createElement('li');
                message.id = 'emptyCartMessage';
                message.textContent = 'Корзина пуста';
                cartList.appendChild(message);
            }
            checkoutButton.style.display = 'none';
            clearCartButton.style.display = 'none';
        } else {
            if (emptyCartMessage) {
                emptyCartMessage.remove();
            }
            checkoutButton.style.display = 'block';
            clearCartButton.style.display = 'block';
        }
        cartCountElement.textContent = cartItems.length;
    }

    function loadCartItems() {
        cartItems.forEach(seatId => {
            const seatElement = document.querySelector(`.arena-seat[data-seat-id="${seatId}"]`);
            if (seatElement) {
                const sector = seatElement.getAttribute('data-sector-name');
                const row = parseFloat(seatElement.getAttribute('data-row-numb'));
                const seatNumb = seatElement.getAttribute('data-seat-numb');
                const seatPrice = parseFloat(seatElement.getAttribute('data-seat-price'));
                const ticketId = seatElement.getAttribute('data-ticket-id');

                const listItem = document.createElement('li');
                listItem.innerHTML = `Сектор: ${sector}, Ряд: ${row}, Место: ${seatNumb}, Цена: ${seatPrice} руб. <button type="button" class="removeItem" data-ticket-id ="${ticketId}" data-seat-id="${seatId}">Удалить</button>`;
                listItem.setAttribute('data-seat-id', seatId);
                listItem.setAttribute('data-ticket-id', ticketId);
                cartList.appendChild(listItem);

                seatElement.classList.remove('available');
                seatElement.classList.add('selected');

                buttonListener(document.querySelectorAll('.removeItem'));
            }
        });
    }

    function buttonListener(buttons) {
        buttons.forEach(button => {
            button.removeEventListener('click', removeItem);
            button.addEventListener('click', removeItem);
        });
    }

    function removeItem() {
        const seatIdToRemove = this.getAttribute('data-seat-id');
        const matchingTicketId = this.getAttribute('data-ticket-id');
        const seatElement = document.querySelector(`.arena-seat[data-seat-id="${seatIdToRemove}"]`);
        if (seatElement) {
            const seatPriceToRemove = parseFloat(seatElement.getAttribute('data-seat-price'));

            fetch('/user_cart', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    ticketId: matchingTicketId,
                    action: 'remove'
                })
            }).then(response => response.json())
                .then(data => {
                    if (data.success) {
                        const index = cartItems.indexOf(seatIdToRemove);
                        if (index > -1) {
                            cartItems.splice(index, 1);
                            localStorage.setItem('cartItems', JSON.stringify(cartItems));
                        }
                        seatElement.classList.remove('selected');
                        seatElement.classList.add('available');

                        this.parentElement.remove();
                        totalPrice -= seatPriceToRemove;
                        totalPriceElement.textContent = totalPrice.toFixed(2);
                        cartCountElement.textContent = cartItems.length;
                        updateCartMessage();
                    } else {
                        console.error('Failed to remove item from cart.');
                    }
                }).catch(error => console.error('Error:', error));
        } else {
            console.error(`Seat element with id ${seatIdToRemove} not found for removal`);
        }
    }

    function addItem() {
        const seatIdToAdd = this.getAttribute('data-seat-id');
        const matchingTicketId = this.getAttribute('data-ticket-id');
        const seatElement = document.querySelector(`.arena-seat[data-seat-id="${seatIdToAdd}"]`);
        if (seatElement) {
            const seatPriceToAdd = parseFloat(seatElement.getAttribute('data-seat-price'));

            fetch('/user_cart', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    ticketId: matchingTicketId,
                    action: 'add'
                })
            }).then(response => response.json())
                .then(data => {
                    if (data.success) {
                        cartItems.push(seatIdToAdd);
                        localStorage.setItem('cartItems', JSON.stringify(cartItems));

                        const sector = seatElement.getAttribute('data-sector-name');
                        const row = parseFloat(seatElement.getAttribute('data-row-numb'));
                        const seatNumb = seatElement.getAttribute('data-seat-numb');
                        const listItem = document.createElement('li');
                        listItem.innerHTML = `Сектор: ${sector}, Ряд: ${row}, Место: ${seatNumb}, Цена: ${seatPriceToAdd} руб. <button type="button" class="removeItem" data-ticket-id ="${matchingTicketId}" data-seat-id="${seatIdToAdd}">Удалить</button>`;
                        cartList.appendChild(listItem);

                        seatElement.classList.remove('available');
                        seatElement.classList.add('selected');

                        totalPrice += seatPriceToAdd;
                        totalPriceElement.textContent = totalPrice.toFixed(2);
                        updateCartMessage();
                        buttonListener(document.querySelectorAll('.removeItem'));
                    }
                    else {
                        console.error('Failed to add item to cart.');
                    }
                }).catch(error => console.error('Error:', error));
        }
    }

    seatElements.forEach(seat => {
        seat.style.cursor = 'pointer';
        seat.addEventListener('click', addItem);
    });

    clearCartButton.addEventListener('click', function () {
        fetch('/user_cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                action: 'clear'
            })
        }).then(response => response.json())
            .then(data => {
                if (data.success) {
                    cartItems.forEach(seatId => {
                        const seatElement = document.querySelector(`.arena-seat[data-seat-id="${seatId}"]`);
                        if (seatElement) {
                            seatElement.classList.remove('selected');
                            seatElement.classList.add('available');
                        }
                    });
                    cartItems.length = 0;
                    cartList.innerHTML = '';
                    totalPrice = 0;
                    totalPriceElement.textContent = totalPrice.toFixed(2);
                    cartCountElement.textContent = cartItems.length;
                    updateCartMessage();
                    localStorage.setItem('cartItems', JSON.stringify(cartItems));
                    localStorage.setItem('totalPrice', totalPrice.toFixed(2));
                } else {
                    console.error('Failed to clear cart.');
                }
            }).catch(error => console.error('Error:', error));
    });

    loadCartItems();
    updateCartMessage();
    updateTotalPrice();
});