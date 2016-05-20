package es.udc.jcastedo.NosaTenda.json;

import java.util.Date;

public class Reserva {
	
	public enum ReservaEstado {PENDIENTE, ENTREGADA, CANCELADA};

	private Long id;
	private Long unidades;
	private ReservaEstado estado;
	private Date fecha;
	private Date fecha_limite;
	private Double precio_noiva;
	private Double precio;
	private Double total;
	private Double tax_amount;
	private Double tax_percentage;
	private Producto producto;
	
	public String getEstadoString() {
		
		if (estado.toString().equals("PENDIENTE")) {
			return "PENDIENTE";
		} else {
			if (estado.toString().equals("ENTREGADA")) {
				return "ENTREGADA";
			} else {
				if (estado.toString().equals("CANCELADA")) {
					return "CANCELADA";
				}
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
	public ReservaEstado getEstado() {
		return estado;
	}
	public void setEstado(ReservaEstado estado) {
		this.estado = estado;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Date getFecha_limite() {
		return fecha_limite;
	}
	public void setFecha_limite(Date fecha_limite) {
		this.fecha_limite = fecha_limite;
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
	
}
