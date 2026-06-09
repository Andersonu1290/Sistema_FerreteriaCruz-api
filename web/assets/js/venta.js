document.addEventListener("DOMContentLoaded", function() {
    const cboProducto = document.getElementById("cboProducto");
    const cboSerie = document.getElementById("txtNroSerie");

    if (cboProducto) {
        cboProducto.addEventListener("change", function() {
            const selectedOption = this.options[this.selectedIndex];
            const seriesString = selectedOption.getAttribute("data-series");

            cboSerie.innerHTML = '<option value="" disabled selected>-- Seleccione el Número de Serie --</option>';
            
            if (seriesString) {
                const seriesArray = seriesString.split(",");
                seriesArray.forEach(serie => {
                    if(serie.trim() !== "") {
                        const opt = document.createElement("option");
                        opt.value = serie;
                        opt.innerHTML = serie;
                        cboSerie.appendChild(opt);
                    }
                });
            } else {
                cboSerie.innerHTML = '<option value="" disabled selected>-- Sin series disponibles en BD --</option>';
            }
        });
    }
});

