<#import "/layout/base.ftl" as layout>
<@layout.page title="Регистрация">
    <h1>Регистрация</h1>

    <form action="/register" method="post" class="auth-form">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

        <div class="form-group">
            <label for="username" class="form-label">Имя пользователя:</label>
            <input
                    id="username"
                    type="text"
                    name="username"
                    class="form-input"
                    value="${(form.username)!''}"
                    required>
            <#if errors?? && errors.hasFieldErrors("username")>
                <p class="form-error">
                    ${errors.getFieldError("username").defaultMessage}
                </p>
            </#if>
        </div>

        <div class="form-group">
            <label for="email" class="form-label">Email:</label>
            <input
                    id="email"
                    type="email"
                    name="email"
                    class="form-input"
                    value="${(form.email)!''}"
                    required>
            <#if errors?? && errors.hasFieldErrors("email")>
                <p class="form-error">
                    ${errors.getFieldError("email").defaultMessage}
                </p>
            </#if>
        </div>

        <div class="form-group">
            <label for="password" class="form-label">Пароль:</label>
            <input
                    id="password"
                    type="password"
                    name="password"
                    class="form-input"
                    required>
            <#if errors?? && errors.hasFieldErrors("password")>
                <p class="form-error">
                    ${errors.getFieldError("password").defaultMessage}
                </p>
            </#if>
        </div>

        <button type="submit" class="btn btn-primary">Зарегистрироваться</button>
    </form>

    <p class="auth-link">
        <a href="/login">Уже есть аккаунт? Войти</a>
    </p>
</@layout.page>