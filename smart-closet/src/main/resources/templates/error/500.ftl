<#import "/layout/base.ftl" as layout>
<@layout.page title="Ошибка сервера">
    <h1>500</h1>
    <h2>Внутренняя ошибка сервера</h2>
    <p>${message!"Что-то пошло не так на стороне приложения."}</p>
    <p><a href="/">Вернуться на главную</a></p>
</@layout.page>