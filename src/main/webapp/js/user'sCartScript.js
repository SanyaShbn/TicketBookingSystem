document.addEventListener('DOMContentLoaded', function () {
    const cartItems = JSON.parse(localStorage.getItem('cartItems')) || [];
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

                const listItem = document.createElement('li');
                listItem.innerHTML = `Сектор: ${sector}, Ряд: ${row}, Место: ${seatNumb}, Цена: ${seatPrice} руб. <button type="button" class="removeItem" data-seat-id="${seatId}">Удалить</button>`;
                listItem.setAttribute('data-seat-id', seatId);
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
        const seatElement = document.querySelector(`.arena-seat[data-seat-id="${seatIdToRemove}"]`);
        if (seatElement) {
            const seatPriceToRemove = parseFloat(seatElement.getAttribute('data-seat-price'));

            cartItems.splice(cartItems.indexOf(seatIdToRemove), 1);
            seatElement.classList.remove('selected');
            seatElement.classList.add('available');

            this.parentElement.remove();
            totalPrice -= seatPriceToRemove;
            totalPriceElement.textContent = totalPrice.toFixed(2);
            cartCountElement.textContent = cartItems.length;
            updateCartMessage();
            localStorage.setItem('cartItems', JSON.stringify(cartItems));
            localStorage.setItem('totalPrice', totalPrice.toFixed(2));
        } else {
            console.error(`Seat element with id ${seatIdToRemove} not found for removal`);
        }
    }

    seatElements.forEach(seat => {
        seat.style.cursor = 'pointer';
        seat.addEventListener('click', function () {
            const seatId = seat.getAttribute('data-seat-id');
            const sector = seat.getAttribute('data-sector-name');
            const row = parseFloat(seat.getAttribute('data-row-numb'));
            const seatNumb = seat.getAttribute('data-seat-numb');
            const seatPrice = parseFloat(seat.getAttribute('data-seat-price'));

            if (!cartItems.includes(seatId)) {
                cartItems.push(seatId);
                const listItem = document.createElement('li');
                listItem.innerHTML = `Сектор: ${sector}, Ряд: ${row}, Место: ${seatNumb}, Цена: ${seatPrice} руб. <button type="button" class="removeItem" data-seat-id="${seatId}">Удалить</button>`;
                listItem.setAttribute('data-seat-id', seatId);
                cartList.appendChild(listItem);
                cartCountElement.textContent = cartItems.length;

                seat.classList.remove('available');
                seat.classList.add('selected');

                totalPrice += seatPrice;
                totalPriceElement.textContent = totalPrice.toFixed(2);
                updateCartMessage();
                localStorage.setItem('cartItems', JSON.stringify(cartItems));
                localStorage.setItem('totalPrice', totalPrice.toFixed(2));

                buttonListener(document.querySelectorAll('.removeItem'));
            }
        });
    });

    clearCartButton.addEventListener('click', function () {
        cartItems.forEach(seatId => {
            const seatElement = document.querySelector(`.arena-seat[data-seat-id="${seatId}"]`);
            if (seatElement) {
                seatElement.classList.remove('selected');
                seatElement.classList.add('available');
            } else {
                console.error(`Seat element with id ${seatId} not found for clearing`);
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
    });

    loadCartItems();
    updateCartMessage();
    updateTotalPrice();
});
