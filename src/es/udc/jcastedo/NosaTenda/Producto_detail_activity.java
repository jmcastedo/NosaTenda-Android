package es.udc.jcastedo.NosaTenda;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import es.udc.jcastedo.NosaTenda.json.Tienda;
import es.udc.jcastedo.NosaTenda.volley.MySingletonVolley;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Producto_detail_activity extends BaseActivity {
	
	private static final String TAG = "Producto_detail_activity";

	private ImageLoader imageLoader;
	
	private String url_imagen;
	
	private NetworkImageView zoom;
	
	private LinearLayout no_zoom;
	
	private Bundle extras;
	
	private TextView nombre_producto;
	
	private Long productoId = null;
	
	private Tienda tienda;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_producto_detail);
		
		auxiliaryOnCreate();
		
		NetworkImageView imagen_producto = (NetworkImageView)findViewById(R.id.imageView_detail_producto);
		
		zoom = (NetworkImageView)findViewById(R.id.imageView_detail_producto_zoom);
		no_zoom = (LinearLayout)findViewById(R.id.container);
		
		nombre_producto = (TextView)findViewById(R.id.textView_detail_nombre_producto);
		TextView descripcion_producto = (TextView)findViewById(R.id.textView_detail_descripcion_producto);
		TextView precio_producto = (TextView)findViewById(R.id.textView_detail_precio_producto);
		TextView nombre_tienda = (TextView)findViewById(R.id.textView_detail_nombre_tienda);
		
		extras = getIntent().getExtras();
		if (extras != null) {
			
			nombre_producto.setText(extras.getCharSequence("nombre"));
			productoId = extras.getLong("productoId");
			descripcion_producto.setText(extras.getCharSequence("descripcion"));
			descripcion_producto.setMovementMethod(new ScrollingMovementMethod());
			Double precio = extras.getDouble("precio");
			precio_producto.setText(ActivityUtils.fmtNoZeros(precio) + " €");
			nombre_tienda.setText(extras.getCharSequence("tienda"));
			
			url_imagen = (String) extras.getCharSequence("url_imagen");
			
			imageLoader = MySingletonVolley.getInstance(getApplicationContext()).getImageLoader();
			imagen_producto.setDefaultImageResId(R.drawable.no_image);
			imagen_producto.setImageUrl(url_imagen, imageLoader);
			
			tienda = new Tienda();
			tienda.setId(extras.getLong("idTienda"));
			tienda.setNombre(extras.getString("tienda"));
			tienda.setDireccion(extras.getString("direccionTienda"));
			tienda.setLocalidad(extras.getString("localidadTienda"));
			tienda.setCorreo(extras.getString("correoTienda"));
			tienda.setPhone1(extras.getString("phoneTienda"));
			tienda.setWeb(extras.getString("webTienda"));
			tienda.setUrl_imagen(extras.getString("url_imagen_tienda"));
			tienda.setLat(extras.getDouble("latTienda"));
			tienda.setLon(extras.getDouble("lonTienda"));
		}
		
		// checkeo del stock (necesitamos pedir el stock antes de que el usuario presione el botón de comprar)
		String URL_stock = getCompleteURL(R.string.URL_CHECK_STOCK) + "?productoId=" + productoId;
		
		JsonObjectRequest jsonStockRequest = new JsonObjectRequest(
				Method.GET,
				URL_stock,
				null,
				stockJSONReqSuccessListener(),
				stockJSONReqErrorListener());
		
		mRequestQueue.add(jsonStockRequest);
		
		Log.i(TAG, getString(R.string.TO_SERVER_GET));
		
		imagen_producto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				
				no_zoom.setVisibility(View.GONE);
				zoom.setVisibility(View.VISIBLE);
				
				zoom.setImageUrl(url_imagen, imageLoader);
			}
		});
		
		zoom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				zoom.setVisibility(View.GONE);
				no_zoom.setVisibility(View.VISIBLE);
				
			}
		});
		
		Button btnReservar = (Button)findViewById(R.id.btn_reserva);
		btnReservar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(), Reserva_activity.class);
				intent.putExtra("productoId", extras.getLong("productoId"));
				intent.putExtra("nombre", extras.getCharSequence("nombre"));
				intent.putExtra("precio", extras.getDouble("precio"));
				startActivity(intent);
				
			}
		});
		
		Button btnComprar = (Button)findViewById(R.id.btn_compra);
		btnComprar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getApplicationContext(), Compra_activity.class);
				intent.putExtra("productoId", extras.getLong("productoId"));
				intent.putExtra("nombre", extras.getCharSequence("nombre"));
				intent.putExtra("precio", extras.getDouble("precio"));
				startActivity(intent);
			}
		});
		
		final Tienda tiendaAux = tienda;
		
		nombre_tienda.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				callTiendaDetail(tiendaAux);
				
			}
		});
		
		TextView nombre_tienda_data = (TextView)findViewById(R.id.textView_tienda);
		nombre_tienda_data.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				callTiendaDetail(tiendaAux);
				
			}
		});
	}
	
	private Listener<JSONObject> stockJSONReqSuccessListener() {
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				
				try {
					
					Log.i(TAG, getString(R.string.FROM_SERVER));
					Log.i(TAG, response.toString(4));
					
					Long stockResponse = response.getLong("stock");
					
					Log.i(TAG, "Petición getStock exitosa");
					
					if (stockResponse <= getResources().getInteger(R.integer.MIN_STOCK) ) {
						if (stockResponse == 0) {
							nombre_producto.setText(nombre_producto.getText() + " "
									+ getString(R.string.nothing));
						} else if (stockResponse == 1) {
							nombre_producto.setText(nombre_producto.getText() + " "
									+ getString(R.string.only_singular) + " " + stockResponse + " " +  getString(R.string.unidad) + ")");
						} else {
							nombre_producto.setText(nombre_producto.getText() + " "
								+ getString(R.string.only) + " " + stockResponse + " " +  getString(R.string.unidades) + ")");
						}
					}
					
				} catch (JSONException e) {
					
					// Test
	    			//ActivityUtils.showErrorDialog(Compra_activity.this, error.getMessage());
	    			// Producción
					ActivityDialogs.showErrorDialog(Producto_detail_activity.this, getString(R.string.ERROR_INTERNO));
					
				}
				
			}
		};
	}
	
	private ErrorListener stockJSONReqErrorListener() {
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				// Test
				//ActivityUtils.showErrorDialog(Compra_activity.this, error.getMessage());
				// Producción
				//ActivityDialogs.showErrorDialog(Producto_detail_activity.this, getString(R.string.ERROR_INTERNO));
				
				ActivityDialogs.showErrorDialog(Producto_detail_activity.this, error, getString(R.string.ERROR_INTERNO));
				
			}
		};
	}
	
	private void callTiendaDetail(Tienda tienda) {
		
		Intent i = new Intent(getApplicationContext(), Tienda_detail_activity.class);
		i.putExtra("idTienda", tienda.getId());
		i.putExtra("nombreTienda", tienda.getNombre());
		i.putExtra("direccionTienda", tienda.getDireccion() +
							" " + tienda.getLocalidad());
		i.putExtra("phoneTienda", tienda.getPhone1());
		i.putExtra("correoTienda", tienda.getCorreo());
		i.putExtra("webTienda", tienda.getWeb());
		i.putExtra("url_imagen_tienda", tienda.getUrl_imagen());
		i.putExtra("latTienda", tienda.getLat());
		i.putExtra("lonTienda", tienda.getLon());
		
		startActivity(i);
	}
	
}
