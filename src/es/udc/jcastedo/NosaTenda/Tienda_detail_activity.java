package es.udc.jcastedo.NosaTenda;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonAuthObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import es.udc.jcastedo.NosaTenda.volley.MySingletonVolley;

public class Tienda_detail_activity extends BaseActivity {
	
	private static final String TAG = "Tienda_detail_activity";

	private ImageLoader imageLoader;
	
	private Long idTienda;
	private String nombreStringTienda;
	private String url_imagen;
	private String url_web;
	private String correo;
	private String phone;
	private Double lat;
	private Double lon;
	
	private Bundle extras;
	
	private ImageView btnFav;
	private Boolean favTienda;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tienda_detail);
		
		auxiliaryOnCreate();
		
		NetworkImageView imagen_producto = (NetworkImageView)findViewById(R.id.imageView_detail_tienda);
		
		TextView nombre_tienda = (TextView)findViewById(R.id.textView_detail_nombre_tienda_detail);
		TextView direccion_tienda = (TextView)findViewById(R.id.textView_detail_direccion_tienda_data);
		TextView telefono_tienda = (TextView)findViewById(R.id.textView_detail_phone_tienda_data);
		TextView correo_tienda = (TextView)findViewById(R.id.textView_detail_correo_tienda_data);
		TextView web_tienda = (TextView)findViewById(R.id.textView_detail_web_tienda_data);
		
		extras = getIntent().getExtras();
		if (extras != null) {
			url_imagen = (String)extras.getCharSequence("url_imagen_tienda");
			
			imageLoader = MySingletonVolley.getInstance(getApplicationContext()).getImageLoader();
			imagen_producto.setDefaultImageResId(R.drawable.no_image);
			imagen_producto.setImageUrl(url_imagen, imageLoader);
			
			nombre_tienda.setText(extras.getCharSequence("nombreTienda"));
			direccion_tienda.setText(extras.getCharSequence("direccionTienda"));
			telefono_tienda.setText(extras.getCharSequence("phoneTienda"));
			correo_tienda.setText(extras.getCharSequence("correoTienda"));
			web_tienda.setText(extras.getCharSequence("webTienda"));
			
			idTienda = extras.getLong("idTienda");
			nombreStringTienda = extras.getString("nombreTienda");
			url_web = extras.getString("webTienda");
			correo = extras.getString("correoTienda");
			phone = extras.getString("phoneTienda");
			lat = extras.getDouble("latTienda");
			lon = extras.getDouble("lonTienda");
		}
		
		direccion_tienda.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				launchMap();
				
			}
		});
		
		telefono_tienda.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:"+phone));
				startActivity(callIntent);
				
			}
		});
		
		correo_tienda.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent mailIntent = new Intent(Intent.ACTION_SEND);
				mailIntent.setType("text/plain"); 
				mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{correo});
				startActivity(Intent.createChooser(mailIntent, getString(R.string.sendmail)));
				
			}
		});
		
		web_tienda.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_web));
				startActivity(browserIntent);
				
			}
		});
		
		Button btnMap = (Button)findViewById(R.id.btn_locate_tienda);
		btnMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				launchMap();
			}
		});
		
		if (isLogged()) {
			btnFav = (ImageView)findViewById(R.id.imageView_fav_tienda_detail);
			btnFav.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (favTienda) {
						btnFav.setImageResource(R.drawable.star_big_off);
						favTienda(favTienda);
						favTienda = false;
					} else {
						btnFav.setImageResource(R.drawable.star_big_on);
						favTienda(favTienda);
						favTienda = true;
					}
					
				}
			});
			
			String uri = getCompleteURL(R.string.URL_ES_FAVORITA_TIENDA) + "?clienteId=" + idClienteSession + "&tiendaId=" + idTienda;
			
			JsonAuthObjectRequest esFavRequest = new JsonAuthObjectRequest(
					Method.GET,
					uri,
					null,
					esFavRequestJSONReqSuccessListener(),
					esFavRequestJSONReqErrorListener());
			
			esFavRequest.setUsername(correoSession);
			esFavRequest.setPassword(passwordSession);
			
			mRequestQueue.add(esFavRequest);
		}

	} // onCreate
	
	private ErrorListener esFavRequestJSONReqErrorListener() {
		return new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				
				// Test
				//ActivityUtils.showErrorDialog(Tienda_detail_activity.this, error.getMessage());
				// Producción
				//ActivityDialogs.showErrorDialog(Tienda_detail_activity.this, getString(R.string.ERROR_INTERNO));
				
				ActivityDialogs.showErrorDialog(Tienda_detail_activity.this, error, getString(R.string.ERROR_INTERNO));
				
			}
		};
	}

	private Listener<JSONObject> esFavRequestJSONReqSuccessListener() {
		return new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				
				try {
					
					Log.i(TAG, getString(R.string.FROM_SERVER));
					Log.i(TAG, response.toString(4));
					
					favTienda = response.getBoolean("response");
					
					if (favTienda) {
						btnFav.setImageResource(R.drawable.star_big_on);
					} else {
						btnFav.setImageResource(R.drawable.star_big_off);
					}
					
					btnFav.setVisibility(View.VISIBLE);
					
				} catch (JSONException e) {
					
					// Test
					//ActivityUtils.showErrorDialog(Tienda_detail_activity.this, error.getMessage());
					// Producción
					ActivityDialogs.showErrorDialog(Tienda_detail_activity.this, getString(R.string.ERROR_INTERNO));
					
				}
				
			}
		};
	}

	private void launchMap() {
		
		Intent mapIntent = new Intent(getApplicationContext(), Map_activity.class);
		
		mapIntent.putExtra("nombreTienda", nombreStringTienda);
		mapIntent.putExtra("lat", lat);
		mapIntent.putExtra("lon", lon);
		
		startActivity(mapIntent);
	}
	
	private void favTienda(Boolean fav) {
		
		String uri;
		
		if (fav) {
			uri = getCompleteURL(R.string.URL_UNFAV_TIENDA);
		} else {
			uri = getCompleteURL(R.string.URL_FAV_TIENDA);
		}
		
		JSONObject jsonFavBody = new JSONObject();
		try {
			jsonFavBody.put("clienteId", idClienteSession);
			jsonFavBody.put("tiendaId", idTienda);
		} catch (JSONException e) {
			// Test
			//ActivityUtils.showErrorDialog(Tienda_detail_activity.this, e.getMessage());
			// Producción
			ActivityDialogs.showErrorDialog(Tienda_detail_activity.this, getString(R.string.ERROR_INTERNO));
		}

		JsonAuthObjectRequest favOrUnfavTiendaReq = new JsonAuthObjectRequest(
				Method.POST,
				uri,
				jsonFavBody,
				null,
				null);
		
		favOrUnfavTiendaReq.setUsername(correoSession);
		favOrUnfavTiendaReq.setPassword(passwordSession);
		
		mRequestQueue.add(favOrUnfavTiendaReq);
	}
}
