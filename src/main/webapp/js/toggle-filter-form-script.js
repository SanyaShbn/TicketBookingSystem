document.addEventListener('DOMContentLoaded', (event) => {
    const form = document.querySelector('.filter-bar form');
    const displayPage = document.getElementById('displayPage');
    const hiddenPage = document.getElementById('page');

    displayPage.addEventListener('input', (event) => {
        if (displayPage.value < 1) displayPage.value = 1;
        hiddenPage.value = displayPage.value - 1;
    });

    form.addEventListener('submit', (event) => {
        hiddenPage.value = displayPage.value - 1;
    });
});

function toggleFilterForm() {
    const form = document.querySelector('.filter-bar form');
    const button = document.querySelector('.filter-bar button');
    if (form.style.display === 'none' || form.style.display === '') {
        form.style.display = 'flex';
        button.innerText = getLocaleCookie() === 'ru' ? 'Скрыть фильтр' : 'Hide filter bar';
    } else {
        form.style.display = 'none';
        button.innerText = getLocaleCookie() === 'ru' ? 'Настроить фильтр' : 'Setup filter';
    }
}