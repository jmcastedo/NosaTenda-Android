package es.udc.jcastedo.NosaTenda.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Entry {

	@Element(name="id")
	private int id;
	
	@Element(name="nombre")
	private String nombre;
	
	@Element(name="descripcion")
	private String descripcion;
	
	@Element(name="imagen")
	private String imagen;
	
	@Element(name="precio")
	private int precio;
	
	@Element(required=false, name="nombreTienda")
	private String nombreTienda;
	
	@Element(required=false, name="direccionTienda")
	private String direccionTienda;

	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getImagen() {
		return imagen;
	}

	public int getPrecio() {
		return precio;
	}

	public String getNombreTienda() {
		return nombreTienda;
	}

	public String getDireccionTienda() {
		return direccionTienda;
	}
	
	
	
}
