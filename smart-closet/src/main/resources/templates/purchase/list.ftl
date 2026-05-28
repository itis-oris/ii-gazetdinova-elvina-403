<#import "/layout/base.ftl" as layout>
<@layout.page title="Мои покупки">
    <h1>Мои покупки</h1>
    <a href="/purchases/new" class="btn">Добавить покупку</a>

    <table>
        <tr>
            <th>Вещь</th>
            <th>Дата покупки</th>
            <th>Цена</th>
            <th>Заметка</th>
        </tr>
        <#list purchases as purchase>
            <tr>
                <td>${purchase.item.name!""}</td>
                <td>${purchase.purchaseDate!""}</td>
                <td>${purchase.price!""}</td>
                <td>${purchase.note!""}</td>
            </tr>
        </#list>
    </table>
</@layout.page>