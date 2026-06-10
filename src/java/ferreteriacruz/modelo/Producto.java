/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.modelo;

/**
 *
 * @author Anderson
 */

public class Producto {
    private int idProducto;
    private String codigoSKU;
    private String nombre;
    private int idCategoria;
    private int stockActual;
    private int stockMinimo;
    private double precio;
    private byte[] imagen;
    private String base64Imagen;
    public Producto() {}

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getCodigoSKU() { return codigoSKU; }
    public void setCodigoSKU(String codigoSKU) { this.codigoSKU = codigoSKU; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    
    public byte[] getImagen() {
        return imagen;
    }
    
    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
        if (imagen != null && imagen.length > 0) {
            this.base64Imagen = java.util.Base64.getEncoder().encodeToString(imagen);
        }
    }

    public String getBase64Imagen() {
        return base64Imagen;
    }
}