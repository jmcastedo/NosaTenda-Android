package es.udc.jcastedo.NosaTenda.json.utils;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import es.udc.jcastedo.NosaTenda.json.Categoria;
import es.udc.jcastedo.NosaTenda.json.Compra;
import es.udc.jcastedo.NosaTenda.json.Localidad;
import es.udc.jcastedo.NosaTenda.json.Compra.CompraEstado;
import es.udc.jcastedo.NosaTenda.json.Producto;
import es.udc.jcastedo.NosaTenda.json.Reserva;
import es.udc.jcastedo.NosaTenda.json.Reserva.ReservaEstado;
import es.udc.jcastedo.NosaTenda.json.Tienda;

public class JSONObjectConversor {
	
	public JSONObjectConversor() {}


	public static Tienda toTienda(JSONObject jsonObject) {
		
		Tienda tienda = new Tienda();
		
		try {
			
			tienda.setId(jsonObject.getLong("id"));
			tienda.setNombre(jsonObject.getString("nombre"));
			tienda.setDireccion(jsonObject.getString("direccion"));
			tienda.setLocalidad(jsonObject.getString("localidad"));
			tienda.setCorreo(jsonObject.getString("correo"));
			tienda.setPhone1(jsonObject.getString("phone1"));
			tienda.setWeb(jsonObject.getString("web"));
			tienda.setUrl_imagen(jsonObject.getString("imagen"));
			tienda.setLat(jsonObject.getDouble("lat"));
			tienda.setLon(jsonObject.getDouble("lon"));
			
		} catch (JSONException e) {
			// TODO modificar el e.printStackTrace() segun cómo decidamos gestionar las excepciones
			Log.d("JSONObjectConvesor",
					"Error parseando el JSON de tienda recibido, algo no concuerda",
					e);
			e.printStackTrace();
		}
		
		
		return tienda;
	}
	
	public static Producto toProducto(JSONObject jsonObject) {
		
		Producto producto = new Producto();
		
		try {
			
			producto.setId(jsonObject.getLong("id"));
			producto.setNombre(jsonObject.getString("nombre"));
			producto.setDescripcion(jsonObject.getString("descripcion"));
			producto.setImagen(jsonObject.getString("imagen"));
			producto.setPrecio(jsonObject.getDouble("precio"));
			producto.setStock(getLongNonMandatory(jsonObject, "stock"));
			// TODO arreglar redondeo hacia abajo de la fecha
			// TODO pasar a mandatory? ahora solo deberiamos recibir productos en venta
			producto.setFecha_puesta_venta(getDateNonMandatory(jsonObject, "fecha_puesta_venta"));
			producto.setFecha_retirada(getDateNonMandatory(jsonObject, "fecha_retirada"));
			
			producto.setTienda(toTienda(jsonObject.getJSONObject("tienda")));
			
		} catch (JSONException e) {
			// TODO modificar el e.printStackTrace() segun cómo decidamos gestionar las excepciones
			Log.d("JSONObjectConvesor",
					"Error parseando el JSON de producto recibido, algo no concuerda",
					e);
			e.printStackTrace();
		}
		
		
		return producto;
	}

	public static Reserva toReserva(JSONObject jsonObject) {
		
		Reserva reserva = new Reserva();
		
		try {
			
			reserva.setId(jsonObject.getLong("id"));
			reserva.setUnidades(jsonObject.getLong("unidades"));
			reserva.setEstado(toReservaEstado(jsonObject.getString("estado")));
			// TODO arreglar redondeo hacia abajo de la fecha
			reserva.setFecha(getDateNonMandatory(jsonObject, "fecha"));
			reserva.setFecha_limite(getDateNonMandatory(jsonObject, "fecha_limite"));
			reserva.setPrecio_noiva(getDoubleNonMandatory(jsonObject, "precio_noiva"));
			reserva.setPrecio(jsonObject.getDouble("precio"));
			reserva.setTotal(jsonObject.getDouble("total"));
			reserva.setTax_amount(getDoubleNonMandatory(jsonObject, "tax_amount"));
			reserva.setTax_percentage(getDoubleNonMandatory(jsonObject, "tax_percentage"));
			reserva.setProducto(toProducto(jsonObject.getJSONObject("producto")));
			
		} catch (JSONException e) {
			// TODO modificar el e.printStackTrace() segun cómo decidamos gestionar las excepciones
			Log.d("JSONObjectConvesor",
					"Error parseando el JSON de reserva recibido, algo no concuerda",
					e);
			e.printStackTrace();
		}
		
		return reserva;
	}
	
	private static ReservaEstado toReservaEstado(String estado) {

		if (estado.equals("PENDIENTE")) {
			return ReservaEstado.PENDIENTE;
		} else {
			if (estado.equals("ENTREGADA")) {
				return ReservaEstado.ENTREGADA;
			} else {
				if (estado.equals("CANCELADA")) {
					return ReservaEstado.CANCELADA;
				}
				return null; // TODO lanzar excepcion
			}
		}
	}
	
	public static Compra toCompra(JSONObject jsonObject) {
		
		Compra compra = new Compra();
		
		try {
			compra.setId(jsonObject.getLong("id"));
			compra.setUnidades(jsonObject.getLong("unidades"));
			compra.setEstadoRecogida(toCompraEstado(jsonObject.getString("estadoRecogida")));
			// TODO arreglar redondeo hacia abajo de la fecha
			compra.setFecha(getDateNonMandatory(jsonObject, "fecha"));
			compra.setPrecio_noiva(getDoubleNonMandatory(jsonObject, "precio_noiva"));
			compra.setPrecio(jsonObject.getDouble("precio"));
			compra.setTotal(jsonObject.getDouble("total"));
			compra.setFormaPago(jsonObject.getString("formaPago"));
			compra.setCurrency(jsonObject.getString("currency"));
			compra.setTax_amount(getDoubleNonMandatory(jsonObject, "tax_amount"));
			compra.setTax_percentage(getDoubleNonMandatory(jsonObject, "tax_percentage"));
			compra.setProducto(toProducto(jsonObject.getJSONObject("producto")));
			//compra.setCliente(toCliente(jsonObject.getJSONObject("cliente")));
		} catch (JSONException e) {
			// TODO modificar el e.printStackTrace() segun cómo decidamos gestionar las excepciones
			Log.d("JSONObjectConvesor",
					"Error parseando el JSON de compra recibido, algo no concuerda",
					e);
			e.printStackTrace();
		}
		
		return compra;
	}
	
	private static CompraEstado toCompraEstado(String estado) {
		
		if (estado.equals("RECOGIDA")) {
			return CompraEstado.RECOGIDA;
		} else if (estado.equals("NO_RECOGIDA")) {
			return CompraEstado.NO_RECOGIDA;
		} else {
			return null; // TODO lanzar excepcion
		}
	}
	
	public static Localidad toLocalidad(JSONObject jsonObject) {
		
		Localidad localidad = new Localidad();
		
		try {
			
			localidad.setId(jsonObject.getLong("id"));
			localidad.setNombre(jsonObject.getString("nombre"));
			
		} catch (JSONException e) {
			// TODO modificar el e.printStackTrace() segun cómo decidamos gestionar las excepciones
			Log.d("JSONObjectConvesor",
					"Error parseando el JSON de localidad recibido, algo no concuerda",
					e);
			e.printStackTrace();
		}
		
		return localidad;
	}
	
	public static Categoria toCategoria(JSONObject jsonObject) {
		
		Categoria categoria = new Categoria();
		
		try {
			
			categoria.setId(jsonObject.getLong("id"));
			categoria.setNombre(jsonObject.getString("nombre"));
			
		} catch (JSONException e) {
			// TODO modificar el e.printStackTrace() segun cómo decidamos gestionar las excepciones
			Log.d("JSONObjectConvesor",
					"Error parseando el JSON de localidad recibido, algo no concuerda",
					e);
			e.printStackTrace();
		}
		
		return categoria;
	}
	
	//public static Cliente toCliente(JSONObject jsonObject) {}
	
	private static Double getDoubleNonMandatory(JSONObject jsonObject, String field) {
		
		try {
			Double result = jsonObject.getDouble(field);
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

	private static Date getDateNonMandatory(JSONObject jsonObject, String field) {
		
		try {
			Long time = Long.parseLong(jsonObject.getString(field));
			Date date = new Date(time);
			return date;
		} catch (NumberFormatException e) {
			return null;
		} catch (JSONException e) {
			return null;
		}
	}

	private static Long getLongNonMandatory(JSONObject jsonObject, String field) {
		
		try {
			Long result = jsonObject.getLong(field);
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

}
