
<#import "/layout/base.ftl" as layout>
<@layout.page title="Вишлист">
    <h1>Вишлист</h1>

    <form id="wishlist-form" class="auth-form">
        <h3>Добавить желание</h3>

        <div class="form-group">
            <label for="wish-name" class="form-label">Название:</label>
            <input id="wish-name" name="name" type="text" class="form-input"
                   placeholder="Например, чёрные кроссовки" required maxlength="100">
        </div>

        <div class="form-group">
            <label for="wish-price" class="form-label">Примерная цена:</label>
            <input id="wish-price" name="price" type="number" step="0.01" min="0"
                   class="form-input" placeholder="3990.00">
        </div>

        <div class="form-group">
            <label for="wish-url" class="form-label">Ссылка на товар:</label>
            <input id="wish-url" name="url" type="url" class="form-input"
                   placeholder="https://..." maxlength="500">
        </div>

        <div class="form-group">
            <label for="wish-note" class="form-label">Заметка:</label>
            <input id="wish-note" name="note" type="text" class="form-input"
                   maxlength="500">
        </div>

        <button type="submit" class="btn">Добавить</button>
    </form>

    <h2>Список желаний</h2>
    <table class="data-table">
        <thead>
            <tr>
                <th>Название</th>
                <th>Цена</th>
                <th>Ссылка</th>
                <th>Заметка</th>
                <th>Действия</th>
            </tr>
        </thead>
        <tbody id="wishlist-body">
            <tr><td colspan="5">Загрузка...</td></tr>
        </tbody>
    </table>
</@layout.page>
