package es.udc.jcastedo.NosaTenda.json;

import java.util.Date;

public class Compra {

	public enum CompraEstado {RECOGIDA, NO_RECOGIDA};
	
	private Long id;
	private Long unidades;
	private CompraEstado estadoRecogida;
	private Date fecha;
	private Double precio_noiva;
	private Double precio;
	private Double total;
	private String formaPago;
	private String currency;
	private Double tax_amount;
	private Double tax_percentage;
	private Producto producto;
	//private Cliente cliente;
	
	public String getEstadoRecogidaString() {
		
		if (estadoRecogida.toString().equals("RECOGIDA")) {
			return "RECOGIDA";
		} else {
			if (estadoRecogida.toString().equals("NO_RECOGIDA")) {
				return "NO RECOGIDA";
			} else {
				return null; // TODO lanzar excepcion
			}
		}
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUnidades() {
		return unidades;
	}
	public void setUnidades(Long unidades) {
		this.unidades = unidades;
	}
	public CompraEstado getEstadoRecogida() {
		return estadoRecogida;
	}
	public void setEstadoRecogida(CompraEstado estadoRecogida) {
		this.estadoRecogida = estadoRecogida;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Double getPrecio_noiva() {
		return precio_noiva;
	}
	public void setPrecio_noiva(Double precio_noiva) {
		this.precio_noiva = precio_noiva;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	public String getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Double getTax_amount() {
		return tax_amount;
	}
	public void setTax_amount(Double tax_amount) {
		this.tax_amount = tax_amount;
	}
	public Double getTax_percentage() {
		return tax_percentage;
	}
	public void setTax_percentage(Double tax_percentage) {
		this.tax_percentage = tax_percentage;
	}
	public Producto getProducto() {
		return producto;
	}
	public void setProducto(Producto producto) {
		this.producto = producto;
	}
	/*public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}*/
	
	
}
