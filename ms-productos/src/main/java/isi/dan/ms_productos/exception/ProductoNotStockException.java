package isi.dan.ms_productos.exception;

public class ProductoNotStockException extends Exception{
    public ProductoNotStockException(Long id){
        super("Producto "+id+" no tiene stock suficiente");
    }
}
