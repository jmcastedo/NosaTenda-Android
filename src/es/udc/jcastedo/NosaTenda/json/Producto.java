package es.udc.jcastedo.NosaTenda.json;

import java.util.Date;

public class Producto {

	private Long id;
	private String nombre;
	private String descripcion;
	private String imagen;
	private Double precio;
	private Long stock;
	private Date fecha_puesta_venta;
	private Date fecha_retirada;
	private Tienda tienda;
	
	// para los sticky headers
	private int section;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getImagen() {
		return imagen;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public Long getStock() {
		return stock;
	}
	public void setStock(Long stock) {
		this.stock = stock;
	}
	public Date getFecha_puesta_venta() {
		return fecha_puesta_venta;
	}
	public void setFecha_puesta_venta(Date fecha_puesta_venta) {
		this.fecha_puesta_venta = fecha_puesta_venta;
	}
	public Date getFecha_retirada() {
		return fecha_retirada;
	}
	public void setFecha_retirada(Date fecha_retirada) {
		this.fecha_retirada = fecha_retirada;
	}
	public Tienda getTienda() {
		return tienda;
	}
	public void setTienda(Tienda tienda) {
		this.tienda = tienda;
	}
	
	// para los sticky headers
	public int getSection() {
		return section;
	}
	public void setSection(int section) {
		this.section = section;
	}
}
