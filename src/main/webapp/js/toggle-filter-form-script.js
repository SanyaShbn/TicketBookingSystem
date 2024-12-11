function toggleFilterForm() {
    var form = document.querySelector('.filter-bar form');
    var button = document.querySelector('.filter-bar button');
    if (form.style.display === 'none' || form.style.display === '') {
        form.style.display = 'flex';
        button.innerText = 'Скрыть фильтр';
    } else {
        form.style.display = 'none';
        button.innerText = 'Настроить фильтр';
    }
}