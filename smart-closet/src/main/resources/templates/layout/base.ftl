<#macro page title="Smart Closet">
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${title}</title>
        <link rel="stylesheet" href="/css/style.css">
    </head>
    <body>
    <nav class="navbar">
        <a href="/" class="logo">Smart Closet</a>
        <div class="nav-links">
            <a href="/items">Мои вещи</a>
            <a href="/wishlist">Вишлист</a>
            <a href="/purchases">Покупки</a>
            <#if isAdmin?? && isAdmin>
                <a href="/admin">Админ</a>
            </#if>
            <#if _csrf??>
                <form action="/logout" method="post" class="logout-form">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <button type="submit" class="nav-link-btn">Выйти</button>
                </form>
            </#if>
        </div>
    </nav>

    <main class="container">
        <#nested>
    </main>

    <footer class="footer">
        <p>Smart Closet &copy; 2026</p>
    </footer>

    <script src="/js/app.js"></script>
    </body>
    </html>
</#macro>