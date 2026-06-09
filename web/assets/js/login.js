document.addEventListener("DOMContentLoaded", function() {
    const frmLogin = document.getElementById("frmLogin");
    if (frmLogin) {
        frmLogin.addEventListener("submit", function() {
            const btn = document.getElementById("btnIngresar");
            const icon = document.getElementById("btnIcon");
            const text = document.getElementById("btnText");
            const msgError = document.getElementById("msgError");

            if(msgError) msgError.style.display = 'none';

            btn.style.backgroundColor = "#475569";
            btn.style.cursor = "not-allowed";
            text.innerHTML = "Autenticando...";
            
            icon.innerHTML = '<path d="M21 12a9 9 0 1 1-6.219-8.56"></path>';
            icon.style.animation = "spin 1s linear infinite";
        });
    }
});
