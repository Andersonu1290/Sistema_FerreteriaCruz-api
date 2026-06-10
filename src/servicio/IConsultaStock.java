/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ferreteriacruz.servicio;

/**
 *
 * @author Anderson
 */

import java.util.List;
import ferreteriacruz.modelo.Producto;

public interface IConsultaStock {
    List<Producto> obtenerInventarioActivo();
}