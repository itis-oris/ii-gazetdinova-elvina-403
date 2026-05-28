<#import "/layout/base.ftl" as layout>
<@layout.page title="Вход">
    <h1>Вход</h1>
    <form action="/login" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <div>
            <label>Имя пользователя:</label>
            <input type="text" name="username" required>
        </div>
        <div>
            <label>Пароль:</label>
            <input type="password" name="password" required>
        </div>
        <button type="submit">Войти</button>
    </form>
    <p><a href="/register">Нет аккаунта? Зарегистрироваться</a></p>
</@layout.page>