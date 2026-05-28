<#import "/layout/base.ftl" as layout>
<@layout.page title="Мои вещи">
    <h1>Мои вещи</h1>
    <a href="/items/new" class="btn">Добавить вещь</a>

    <form method="get" action="/items" class="filter-form">
        <label for="season">Сезон:</label>
        <select name="season" id="season">
            <option value="" <#if (selectedSeason!"") == "">selected</#if>>Все</option>
            <option value="Лето" <#if (selectedSeason!"") == "Лето">selected</#if>>Лето</option>
            <option value="Зима" <#if (selectedSeason!"") == "Зима">selected</#if>>Зима</option>
            <option value="Весна" <#if (selectedSeason!"") == "Весна">selected</#if>>Весна</option>
            <option value="Осень" <#if (selectedSeason!"") == "Осень">selected</#if>>Осень</option>
        </select>

        <label for="name">Название:</label>
        <input type="text" id="name" name="name" placeholder="Поиск по подстроке" value="${selectedName!''}">

        <label for="categoryId">Категория:</label>
        <select name="categoryId" id="categoryId">
            <option value="">Все</option>
            <#if categories??>
                <#list categories as cat>
                    <option value="${cat.id}"
                            <#if selectedCategoryId?? && selectedCategoryId == cat.id>selected</#if>>
                        ${cat.name!""}
                    </option>
                </#list>
            </#if>
        </select>

        <button type="submit" class="btn">Применить</button>
        <a href="/items" class="btn btn-secondary">Сбросить</a>
    </form>

    <table class="data-table">
        <thead>
        <tr>
            <th>Фото</th>
            <th>Название</th>
            <th>Цвет</th>
            <th>Размер</th>
            <th>Сезон</th>
            <th>Цена</th>
            <th>Категория</th>
            <th>Бренд</th>
            <th>Действия</th>
            <th>Рекомендация</th>
        </tr>
        </thead>
        <tbody>
        <#if items??>
        <#list items as item>
            <tr>
                <td>
                    <#if item.imageUrl??>
                        <img src="${item.imageUrl}" alt="${item.name!""}"
                             style="width:56px; height:56px; object-fit:cover; border-radius:6px; border:1px solid #ddd;">
                    <#else>
                        <div style="width:56px;height:56px;background:#f0f0f0;border-radius:6px;
                                    display:flex;align-items:center;justify-content:center;
                                    font-size:1.4em;color:#bbb;">👗</div>
                    </#if>
                </td>
                <td>
                    ${item.name!""}
                    <#if expensiveIds?? && expensiveIds?seq_contains(item.id)>
                        <span class="badge-expensive">💰 выше среднего</span>
                    </#if>
                </td>
                <td>
                    <#if item.color??>
                        <span style="display:inline-block;width:16px;height:16px;
                                     background:${item.color};border-radius:50%;
                                     border:1px solid #ccc;vertical-align:middle;margin-right:4px;"></span>
                    </#if>
                    ${(item.colorName)!(item.color)!"—"}
                </td>
                <td>${item.size!""}</td>
                <td>${item.season!""}</td>
                <td>${item.price!""}</td>
                <td>${(item.category.name)!""}</td>
                <td>${(item.brand.name)!""}</td>
                <td>
                    <a href="/items/${item.id}/edit" class="btn">Редактировать</a>
                    <form action="/items/${item.id}/delete" method="post" class="inline-form">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        <button type="submit" class="btn btn-danger">Удалить</button>
                    </form>
                </td>
                <td>
                    <#assign matches = (matchingItems[item.id?c])![]>
                    <#if matches?size gt 0>
                        Подходит к:
                        <#list matches as match>
                            ${match.name!""}<#if match_has_next>, </#if>
                        </#list>
                    <#else>
                        —
                    </#if>
                </td>
            </tr>
        </#list>
        </#if>
        </tbody>
    </table>
</@layout.page>
