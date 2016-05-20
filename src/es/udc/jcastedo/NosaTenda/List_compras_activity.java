package es.udc.jcastedo.NosaTenda;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonAuthArrayRequest;
import es.udc.jcastedo.NosaTenda.json.Compra;
import es.udc.jcastedo.NosaTenda.json.utils.JSONObjectConversor;

public class List_compras_activity extends BaseActivity {
	
	private ViewGroup mContainerView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_compras);
		
		auxiliaryOnCreate();
		
		mContainerView = (ViewGroup)findViewById(R.id.lista_compras_container);
		
		// petición al servicio de las compras del cliente
		String URL = getCompleteURL(R.string.URL_CLIENT_COMPRAS) + "?clienteId=" + idClienteSession;
		
		JsonAuthArrayRequest getComprasJSONReq = new JsonAuthArrayRequest(
				Request.Method.GET,
				URL,
				null,
				getComprasJSONReqSuccessListener(),
				getComprasJSONReqErrorListener());
		
		getComprasJSONReq.setUsername(correoSession);
		getComprasJSONReq.setPassword(passwordSession);
		
		mRequestQueue.add(getComprasJSONReq);
	}

	private ErrorListener getComprasJSONReqErrorListener() {
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				ActivityDialogs.showErrorDialog(List_compras_activity.this, error, getString(R.string.ERROR_INTERNO));
				
			}
		};
	}

	private Listener<JSONArray> getComprasJSONReqSuccessListener() {
		return new Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				
				try {
					
					if (response.length() != 0) {
						findViewById(R.id.empty_compras).setVisibility(View.GONE);
					}
					for(int i=0; i<response.length(); i++) {
						JSONObject jsonObject = response.getJSONObject(i);
						Compra compra = JSONObjectConversor.toCompra(jsonObject);
						addItem(compra);
					}
					
				} catch (JSONException e) {
					
					ActivityDialogs.showErrorDialog(List_compras_activity.this, getString(R.string.ERROR_INTERNO));
					
				}
			}
		};
	}
	
	public void addItem(Compra compra) {
		// Instantiate a new "row" view
		final ViewGroup newView = (ViewGroup)LayoutInflater.from(this).inflate(
				R.layout.elemento_lista_compras, mContainerView, false);
		
		((TextView)newView.findViewById(R.id.text_producto_comprado)).setText(compra.getProducto().getNombre());
		((TextView)newView.findViewById(R.id.text_estado_comprado)).setText(compra.getEstadoRecogidaString());
		((TextView)newView.findViewById(R.id.text_fecha_comprado)).setText(ActivityUtils.dateToString(compra.getFecha(), "dd-MM-yyyy"));
		
		final Compra compraAux = compra;
		
		newView.findViewById(R.id.text_producto_comprado).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				callCompraDetail(compraAux);
				
			}
		});
		
		newView.findViewById(R.id.text_estado_comprado).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				callCompraDetail(compraAux);
				
			}
		});
		
		newView.findViewById(R.id.text_fecha_comprado).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				callCompraDetail(compraAux);
				
			}
		});
		
		// Set a click listener for the "X" button in the row that will remove/cancel the row.
		newView.findViewById(R.id.button_cancel_compra).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO
				
			}
		});
		
		mContainerView.addView(newView, 0);
		
	} // addItem
	
	private void callCompraDetail(Compra compra) {
		
		Intent i = new Intent(getApplicationContext(), Compra_detail_activity.class);
		i.putExtra("nombre", compra.getProducto().getNombre());
		i.putExtra("unidades", compra.getUnidades());
		i.putExtra("fecha", compra.getFecha());
		i.putExtra("subtotal", compra.getPrecio());
		i.putExtra("total", compra.getTotal());
		i.putExtra("formaPago", compra.getFormaPago());
		i.putExtra("estadoRecogida", compra.getEstadoRecogidaString());
		i.putExtra("url_imagen", compra.getProducto().getImagen());
		i.putExtra("formaPago", compra.getFormaPago());
		
		i.putExtra("idTienda", compra.getProducto().getTienda().getId());
		i.putExtra("nombreTienda", compra.getProducto().getTienda().getNombre());
		i.putExtra("direccionTienda", compra.getProducto().getTienda().getDireccion());
		i.putExtra("localidadTienda", compra.getProducto().getTienda().getLocalidad());
		i.putExtra("correoTienda", compra.getProducto().getTienda().getCorreo());
		i.putExtra("phoneTienda", compra.getProducto().getTienda().getPhone1());
		i.putExtra("webTienda", compra.getProducto().getTienda().getWeb());
		i.putExtra("url_imagen_tienda", compra.getProducto().getTienda().getUrl_imagen());
		i.putExtra("latTienda", compra.getProducto().getTienda().getLat());
		i.putExtra("lonTienda", compra.getProducto().getTienda().getLon());
		i.putExtra("estadoRecogida", compra.getEstadoRecogidaString());
		
		startActivity(i);
	}
}
