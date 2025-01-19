function getLocaleCookie() {
    let matches = document.cookie.match(/(?:^|; )locale=([^;]*)/);
    return matches ? decodeURIComponent(matches[1]) : undefined;
}
