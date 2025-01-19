function toggleFilterForm() {
    var form = document.querySelector('.filter-bar form');
    var button = document.querySelector('.filter-bar button');
    if (form.style.display === 'none' || form.style.display === '') {
        form.style.display = 'flex';
        button.innerText = getLocaleCookie() === 'ru' ? 'Скрыть фильтр' : 'Hide filter bar';
    } else {
        form.style.display = 'none';
        button.innerText = getLocaleCookie() === 'ru' ? 'Настроить фильтр' : 'Setup filter';
    }
}