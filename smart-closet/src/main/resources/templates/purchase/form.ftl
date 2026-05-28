<#import "/layout/base.ftl" as layout>
<@layout.page title="Добавить покупку">
    <h1>Добавить покупку</h1>

    <form action="/purchases" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

        <div>
            <label for="itemId">Вещь:</label>
            <select name="itemId" id="itemId" required>
                <option value="">Выберите вещь</option>
                <#list items as item>
                    <option value="${item.id}">${item.name}</option>
                </#list>
            </select>
        </div>

        <div>
            <label for="purchaseDate">Дата покупки:</label>
            <input type="date" id="purchaseDate" name="purchaseDate" required>
        </div>

        <div>
            <label for="price">Цена:</label>
            <input type="number" id="price" name="price" step="0.01">
        </div>

        <div>
            <label for="note">Заметка:</label>
            <textarea id="note" name="note" rows="3"></textarea>
        </div>

        <button type="submit" class="btn">Сохранить</button>
    </form>
</@layout.page>