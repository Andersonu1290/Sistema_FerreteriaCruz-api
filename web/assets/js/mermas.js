function ejecutarMerma(serie) {
    let motivo = prompt("ESTÁ A PUNTO DE DAR DE BAJA EL EQUIPO S/N: " + serie + "\n\nPor favor, ingrese el motivo del defecto o daño:");

    if (motivo !== null && motivo.trim() !== "") {
        document.getElementById('hdnNroSerie').value = serie;
        document.getElementById('hdnMotivo').value = motivo;
        document.getElementById('frmProcesarMerma').submit();
    } else if (motivo !== null) {
        alert("Operación cancelada: El motivo es obligatorio para registrar la merma en el Kardex.");
    }
}

document.addEventListener("DOMContentLoaded", function() {
    if (document.getElementById('buscadorDisp')) {
        inicializarBuscador('buscadorDisp', 'tablaDisp');
        inicializarBuscador('buscadorMerma', 'tablaMerma');
    }
});