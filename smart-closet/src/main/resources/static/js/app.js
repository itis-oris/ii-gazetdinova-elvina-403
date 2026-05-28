
document.addEventListener("DOMContentLoaded", function () {
    if (document.getElementById("wishlist-body")) {
        loadWishlist();
        bindWishlistForm();
    }

    initColorPicker();
});

function getCookie(name) {
    const match = document.cookie.match(new RegExp("(?:^|; )" + name + "=([^;]*)"));
    return match ? decodeURIComponent(match[1]) : null;
}

function csrfHeaders() {
    const token = getCookie("XSRF-TOKEN");
    const headers = { "Content-Type": "application/json" };
    if (token) {
        headers["X-XSRF-TOKEN"] = token;
    }
    return headers;
}

function loadWishlist() {
    fetch("/api/wishlist", {
        headers: { "Accept": "application/json" },
        credentials: "same-origin"
    })
        .then(handleJsonResponse)
        .then(renderWishlist)
        .catch(err => console.error("Ошибка загрузки вишлиста:", err));
}

function renderWishlist(items) {
    const tbody = document.getElementById("wishlist-body");
    while (tbody.firstChild) tbody.removeChild(tbody.firstChild);

    if (!Array.isArray(items) || items.length === 0) {
        const row = document.createElement("tr");
        const cell = document.createElement("td");
        cell.colSpan = 5;
        cell.textContent = "Пока пусто. Добавьте первое желание.";
        row.appendChild(cell);
        tbody.appendChild(row);
        return;
    }

    items.forEach(item => tbody.appendChild(buildWishlistRow(item)));
}

function buildWishlistRow(item) {
    const row = document.createElement("tr");

    row.appendChild(td(item.name));
    row.appendChild(td(item.price != null ? String(item.price) : ""));

    const urlCell = document.createElement("td");
    if (item.url) {
        const a = document.createElement("a");
        a.href = item.url;
        a.target = "_blank";
        a.rel = "noopener noreferrer";
        a.textContent = "Ссылка";
        urlCell.appendChild(a);
    }
    row.appendChild(urlCell);

    row.appendChild(td(item.note || ""));

    const actionCell = document.createElement("td");
    const btn = document.createElement("button");
    btn.className = "btn btn-danger";
    btn.type = "button";
    btn.textContent = "Удалить";
    btn.addEventListener("click", () => deleteWish(item.id));
    actionCell.appendChild(btn);
    row.appendChild(actionCell);

    return row;
}

function td(text) {
    const cell = document.createElement("td");
    cell.textContent = text == null ? "" : String(text);
    return cell;
}

function bindWishlistForm() {
    const form = document.getElementById("wishlist-form");
    if (!form) return;
    form.addEventListener("submit", function (e) {
        e.preventDefault();
        addWish();
    });
}

function addWish() {
    const nameEl = document.getElementById("wish-name");
    const priceEl = document.getElementById("wish-price");
    const urlEl = document.getElementById("wish-url");
    const noteEl = document.getElementById("wish-note");

    const wish = {
        name: nameEl.value,
        price: priceEl.value ? Number(priceEl.value) : null,
        url: urlEl.value,
        note: noteEl.value
    };

    fetch("/api/wishlist", {
        method: "POST",
        headers: csrfHeaders(),
        credentials: "same-origin",
        body: JSON.stringify(wish)
    })
        .then(res => {
            if (!res.ok) return res.json().then(body => { throw body; });
            return res.json();
        })
        .then(() => {
            nameEl.value = "";
            priceEl.value = "";
            urlEl.value = "";
            noteEl.value = "";
            clearValidationErrors();
            loadWishlist();
        })
        .catch(err => {
            if (err && err.fieldErrors) {
                showValidationErrors(err.fieldErrors);
            } else if (err && err.message) {
                alert(err.message);
            } else {
                console.error("Ошибка добавления:", err);
            }
        });
}

function deleteWish(id) {
    if (!confirm("Удалить запись из вишлиста?")) return;
    fetch("/api/wishlist/" + encodeURIComponent(id), {
        method: "DELETE",
        headers: csrfHeaders(),
        credentials: "same-origin"
    })
        .then(res => {
            if (!res.ok && res.status !== 204) throw new Error("HTTP " + res.status);
            loadWishlist();
        })
        .catch(err => console.error("Ошибка удаления:", err));
}

function initColorPicker() {
    const picker = document.getElementById("colorPicker");
    const hexInput = document.getElementById("colorHex");
    if (!picker) return;

    if (picker.value) loadColorInfo(picker.value);

    let debounceTimer = null;
    const trigger = function (value) {
        if (debounceTimer) clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => loadColorInfo(value), 150);
    };

    picker.addEventListener("change", () => {
        if (hexInput) hexInput.value = picker.value;
        trigger(picker.value);
    });
    picker.addEventListener("input", () => {
        if (hexInput) hexInput.value = picker.value;
        trigger(picker.value);
    });

    if (hexInput) {
        hexInput.addEventListener("input", () => {
            let v = hexInput.value.trim();
            if (!v.startsWith("#")) v = "#" + v;
            if (/^#[0-9A-Fa-f]{6}$/.test(v)) {
                picker.value = v.toUpperCase();
                trigger(picker.value);
            }
        });
    }

    const form = picker.closest("form");
    if (form) {
        form.addEventListener("submit", function () {
            if (debounceTimer) clearTimeout(debounceTimer);
        });
    }
}

function loadColorInfo(hexWithHash) {
    const hex = (hexWithHash || "").replace("#", "");
    if (!hex) return;

    fetch("/api/colors/info?hex=" + encodeURIComponent(hex), {
        headers: { "Accept": "application/json" },
        credentials: "same-origin"
    })
        .then(handleJsonResponse)
        .then(data => {
            const nameEl = document.getElementById("colorName");
            const complEl = document.getElementById("complementInfo");
            if (nameEl) nameEl.textContent = data && data.name ? data.name : "—";
            if (complEl) {
                complEl.textContent = data && data.complement
                    ? "Дополняющий цвет: #" + data.complement
                    : "";
            }
        })
        .catch(err => console.warn("color-service недоступен:", err));
}

function handleJsonResponse(res) {
    if (res.status === 401 || res.status === 403) {
        window.location.href = "/login";
        throw new Error("Не авторизован");
    }
    if (!res.ok) {
        return res.json().then(body => { throw body; });
    }
    return res.json();
}

function showValidationErrors(fieldErrors) {
    clearValidationErrors();
    Object.keys(fieldErrors).forEach(field => {
        const input = document.getElementById("wish-" + field);
        if (!input) return;
        const msg = document.createElement("p");
        msg.className = "form-error js-form-error";
        msg.textContent = fieldErrors[field];
        input.parentNode.insertBefore(msg, input.nextSibling);
    });
}

function clearValidationErrors() {
    document.querySelectorAll(".js-form-error").forEach(el => el.remove());
}
