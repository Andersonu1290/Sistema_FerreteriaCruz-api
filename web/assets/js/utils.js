function inicializarBuscador(inputId, tableId) {
    const input = document.getElementById(inputId);
    const tabla = document.getElementById(tableId);

    if (!input || !tabla) return;

    input.addEventListener('keyup', function() {
        let filtro = this.value.toLowerCase();
        let filas = tabla.querySelectorAll('tbody tr');
        
        filas.forEach(function(fila) {
            // Se verifica que la fila tenga celdas para no filtrar el encabezado o mensajes de 'vacio'
            if(fila.cells.length > 1) {
                let textoFila = fila.textContent.toLowerCase();
                fila.style.display = textoFila.includes(filtro) ? '' : 'none';
            }
        });
    });
}