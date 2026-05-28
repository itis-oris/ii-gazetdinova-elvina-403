<#import "/layout/base.ftl" as layout>
<@layout.page title="Вещь">
    <h1><#if form.id??>Редактировать<#else>Добавить</#if> вещь</h1>

    <form action="/items<#if form.id??>/${form.id}</#if>" method="post"
          enctype="multipart/form-data" class="auth-form">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <#if form.id??>
            <input type="hidden" name="id" value="${form.id}">
        </#if>

        <div class="form-group">
            <label for="name" class="form-label">Название:</label>
            <input
                    id="name"
                    type="text"
                    name="name"
                    class="form-input"
                    value="${(form.name)!''}"
                    required>
            <#if errors?? && errors.hasFieldErrors("name")>
                <p class="form-error">
                    ${errors.getFieldError("name").defaultMessage}
                </p>
            </#if>
        </div>

        <div class="form-group">
            <label for="size" class="form-label">Размер:</label>
            <input
                    id="size"
                    type="text"
                    name="size"
                    class="form-input"
                    value="${(form.size)!''}">
        </div>

        <div class="form-group">
            <label for="season" class="form-label">Сезон:</label>
            <input
                    id="season"
                    type="text"
                    name="season"
                    class="form-input"
                    value="${(form.season)!''}">
        </div>

        <div class="form-group">
            <label for="price" class="form-label">Цена:</label>
            <input
                    id="price"
                    type="number"
                    step="0.01"
                    name="price"
                    class="form-input"
                    value="${(form.price)!''}">
            <#if errors?? && errors.hasFieldErrors("price")>
                <p class="form-error">
                    ${errors.getFieldError("price").defaultMessage}
                </p>
            </#if>
        </div>

        <div class="form-group">
            <label for="category" class="form-label">Категория:</label>
            <select id="category" name="categoryId" class="form-input">
                <option value="">-- выбрать --</option>
                <#list categories as cat>
                    <option value="${cat.id}"<#if form.categoryId?? && form.categoryId == cat.id> selected</#if>>
                        ${cat.name}
                    </option>
                </#list>
            </select>
            <#if errors?? && errors.hasFieldErrors("categoryId")>
                <p class="form-error">
                    ${errors.getFieldError("categoryId").defaultMessage}
                </p>
            </#if>
        </div>

        <div class="form-group">
            <label for="brand" class="form-label">Бренд:</label>
            <select id="brand" name="brandId" class="form-input">
                <option value="">-- выбрать --</option>
                <#list brands as b>
                    <option value="${b.id}"<#if form.brandId?? && form.brandId == b.id> selected</#if>>
                        ${b.name}
                    </option>
                </#list>
            </select>
        </div>

        <div class="form-group">
            <label for="colorPicker" class="form-label">Цвет:</label>
            <div style="display: flex; gap: 10px; align-items: center;">
                <input
                        id="colorPicker"
                        type="color"
                        name="color"
                        class="form-input form-color"
                        value="${(form.color)!'#FFFFFF'}"
                        style="flex: 1;">
                <input
                        id="colorHex"
                        type="text"
                        class="form-input"
                        placeholder="#FF0000"
                        value="${(form.color)!'#FFFFFF'}"
                        maxlength="7"
                        style="width: 120px;">
            </div>
            <p class="color-info">
                Название цвета: <span id="colorName">—</span>
            </p>
            <p id="complementInfo" class="color-info"></p>
        </div>

        <div class="form-group">
            <label for="photo" class="form-label">Фото вещи:</label>
            <#if item?? && item.imageUrl??>
                <div style="margin-bottom: 8px;">
                    <img src="${item.imageUrl}" alt="Текущее фото"
                         style="max-width: 120px; max-height: 120px; border-radius: 8px; object-fit: cover; border: 1px solid #ddd;">
                    <p style="font-size: 0.85em; color: #666; margin-top: 4px;">Текущее фото. Загрузите новое, чтобы заменить.</p>
                </div>
            </#if>
            <input
                    id="photo"
                    type="file"
                    name="photo"
                    accept="image/jpeg,image/png,image/webp,image/gif"
                    class="form-input"
                    style="padding: 6px;">
            <p style="font-size: 0.8em; color: #888; margin-top: 4px;">JPG, PNG, WEBP — до 5 МБ. Необязательно.</p>
            <#if photoError??>
                <p class="form-error">${photoError}</p>
            </#if>
        </div>

        <button type="submit" class="btn">Сохранить</button>
    </form>

    <p class="auth-link">
        <a href="/items">Назад к списку</a>
    </p>
</@layout.page>
