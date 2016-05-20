package es.udc.jcastedo.NosaTenda;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonAuthArrayRequest;
import com.android.volley.toolbox.JsonAuthObjectRequest;
import es.udc.jcastedo.NosaTenda.json.Tienda;
import es.udc.jcastedo.NosaTenda.json.utils.JSONObjectConversor;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class List_favTiendas_activity extends BaseActivity {
	
	private static final String TAG = "List_favTiendas_activity";

	private ViewGroup mContainerView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_tiendas_favoritas);
		
		auxiliaryOnCreate();
		
		mContainerView = (ViewGroup)findViewById(R.id.lista_tiendas_favoritas_container);
		
		String uri = getCompleteURL(R.string.URL_GETTIENDAS_FAVORITAS) + "?clienteId=" + idClienteSession;
		
		JsonAuthArrayRequest getFavTiendasJSONReq = new JsonAuthArrayRequest(
				Method.GET,
				uri,
				null,
				getTiendasFavJSONReqSuccessListener(),
				getTiendasFavJSONReqErrorListener());
		
		getFavTiendasJSONReq.setUsername(correoSession);
		getFavTiendasJSONReq.setPassword(passwordSession);
		
		mRequestQueue.add(getFavTiendasJSONReq);
		
		Log.i(TAG, getString(R.string.TO_SERVER_GET));
	}

	private ErrorListener getTiendasFavJSONReqErrorListener() {
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				ActivityDialogs.showErrorDialog(List_favTiendas_activity.this, error, getString(R.string.ERROR_INTERNO));
				
			}
		};
	}

	private Listener<JSONArray> getTiendasFavJSONReqSuccessListener() {
		return new Listener<JSONArray>() {

			@Override
			public void onResponse(JSONArray response) {
				
				try {
					
					Log.i(TAG, getString(R.string.FROM_SERVER));
					Log.i(TAG, response.toString(4));
					
					if (response.length() != 0) {
						findViewById(R.id.empty_tiendas_favoritas).setVisibility(View.GONE);
					}
					for(int i=0; i<response.length(); i++) {
						JSONObject jsonObject = response.getJSONObject(i);
						Tienda tienda = JSONObjectConversor.toTienda(jsonObject);
						addItem(tienda);
					}
					
				} catch (JSONException e) {
					
					ActivityDialogs.showErrorDialog(List_favTiendas_activity.this, getString(R.string.ERROR_INTERNO));
					
				}
				
			}
		};
	}
	
	private void addItem(Tienda tienda) {
		// Instantiate a new "row" view
		final ViewGroup newView = (ViewGroup)LayoutInflater.from(this).inflate(
				R.layout.elemento_lista_favtiendas, mContainerView, false);
		
		((TextView)newView.findViewById(R.id.text_tienda_favorita)).setText(tienda.getNombre());
		
		final Tienda tiendaAux = tienda;
		
		newView.findViewById(R.id.text_tienda_favorita).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				callTiendaDetail(tiendaAux);
				
			}
		});
		
		// Set a click listener for the "star" button in the row that will show the products of this shop.
		newView.findViewById(R.id.button_productos_fav).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				callEscaparate(tiendaAux);
			}
		});
		
		
		// Set a click listener for the "X" button in the row that will remove/cancel the row.
		newView.findViewById(R.id.button_borrar_fav).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				unfavTienda(tiendaAux);
				mContainerView.removeView(newView);
				
				if (mContainerView.getChildCount() == 0) {
					findViewById(R.id.empty_tiendas_favoritas).setVisibility(View.VISIBLE);
				}
			}
		});
		
		mContainerView.addView(newView, 0);
		
	} // addItem
	
	private void callTiendaDetail(Tienda tienda) {
		
		Intent i = new Intent(getApplicationContext(), Tienda_detail_activity.class);
		
		i.putExtra("idTienda", tienda.getId());
		i.putExtra("nombreTienda", tienda.getNombre());
		i.putExtra("direccionTienda", tienda.getDireccion());
		i.putExtra("localidadTienda", tienda.getLocalidad());
		i.putExtra("correoTienda", tienda.getCorreo());
		i.putExtra("phoneTienda", tienda.getPhone1());
		i.putExtra("webTienda", tienda.getWeb());
		i.putExtra("url_imagen_tienda", tienda.getUrl_imagen());
		i.putExtra("latTienda", tienda.getLat());
		i.putExtra("lonTienda", tienda.getLon());
		
		startActivity(i);
	}
	
	private void callEscaparate(Tienda tienda) {
		
		Intent i = new Intent(getApplicationContext(), EscaparateJSON.class);
		
		i.putExtra("tiendaId", tienda.getId());
		
		startActivity(i);
	}
	
	private void unfavTienda(Tienda tienda) {
		
		String uri = getCompleteURL(R.string.URL_UNFAV_TIENDA);
		
		JSONObject jsonUnfavBody = new JSONObject();
		try {
			
			jsonUnfavBody.put("clienteId", idClienteSession);
			jsonUnfavBody.put("tiendaId", tienda.getId());
			
		} catch (JSONException e) {
			
			// Test
			//ActivityUtils.showErrorDialog(List_favTiendas_activity.this, e.getMessage());
			// Producción
			ActivityDialogs.showErrorDialog(List_favTiendas_activity.this, getString(R.string.ERROR_INTERNO));
			
		}

		JsonAuthObjectRequest unfavTiendaReq = new JsonAuthObjectRequest(
				Method.POST,
				uri,
				jsonUnfavBody,
				null,
				null);
		
		unfavTiendaReq.setUsername(correoSession);
		unfavTiendaReq.setPassword(passwordSession);
		
		mRequestQueue.add(unfavTiendaReq);
		
	}

}
