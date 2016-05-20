package es.udc.jcastedo.NosaTenda;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonAuthArrayRequest;

import es.udc.jcastedo.NosaTenda.json.Producto;
import es.udc.jcastedo.NosaTenda.json.utils.JSONObjectConversor;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class EscaparateJSON extends BaseActivity {
	
	private static final String TAG = "EscaparateJSON";
	
	private List<Producto> mProductos;
	private ProductoGridAdapter mPGAdapter;
	private Bundle extras;
	
	// para los stickyHeaders
	private ProductoStickyGridAdapter mPSGAdapter;
	private static int section = 1;
	private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
	
	private final Long NON_EXISTENT_ID = Long.valueOf(-1);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_escaparate);
		
		auxiliaryOnCreate();
		
		mTitle = getString(R.string.escaparate_activity);
		
		extras = getIntent().getExtras();
		String uri = "";
		Boolean auth = false;
		if (extras != null) {
			Long localidadId = extras.getLong("localidadId", NON_EXISTENT_ID);
			if (!localidadId.equals(NON_EXISTENT_ID)) {
				uri = getCompleteURL(R.string.URL_GETPRODUCTOS_LOCALIDAD) + "?localidadId=" + localidadId;
			} else {
				int categorias = extras.getInt("categorias", 0);
				if (categorias > 0) {
					uri = getCompleteURL(R.string.URL_GETPRODUCTOS_CATEGORIA) + "?param=" + categorias;
					for (int i = 1; i <= categorias; i++) {
						uri = uri + "&cat" + i + "=" + extras.getLong("categoriaId" + i);
					}
				} else {
					Long categoriaTiendaId = extras.getLong("categoriaTiendaId", NON_EXISTENT_ID);
					if (!categoriaTiendaId.equals(NON_EXISTENT_ID)) {
						uri = getCompleteURL(R.string.URL_GETPRODUCTOS_CATEGORIATIENDA) + "?categoriaId=" + categoriaTiendaId;
					} else {
						Long tiendaId = extras.getLong("tiendaId", NON_EXISTENT_ID);
						if (!tiendaId.equals(NON_EXISTENT_ID)) {
							uri = getCompleteURL(R.string.URL_GETPRODUCTOS_TIENDA) + "?tiendaId=" + tiendaId;
						} else {
							Boolean fav = extras.getBoolean("fav");
							if (fav) {
								uri = getCompleteURL(R.string.URL_GETPRODUCTOS_TIENDASFAV) + "?clienteId=" + idClienteSession;
								auth = true; // la operacion requiere autenticacion
							} else {
								// si categorias = 0, no hay categoria ni id de tienda, y tampoco favoritos, devolvemos todos los productos
								uri = getCompleteURL(R.string.URL_GETPRODUCTOSJSON);
							}
							
						}
						
					}
				}
			}
		} else {
			//uri = getCompleteURL(R.string.URL_GETPRODUCTOSJSON);
			uri = getCompleteURL(R.string.URL_GETPRODUCTOSENVENTAJSON);
		}
		
		Log.i(TAG, "uri = " + uri);
		
		if (auth) {
			JsonAuthArrayRequest myJSONReq = new JsonAuthArrayRequest(
					Method.GET,
					uri,
					null,
					getProductosJSONReqSuccessListener(),
					getProductosJSONReqErrorListener());
			
			myJSONReq.setUsername(correoSession);
			myJSONReq.setPassword(passwordSession);
			
			mRequestQueue.add(myJSONReq);
			
		} else {
			JsonArrayRequest myJSONReq = new JsonArrayRequest(
					Method.GET,
					uri,
					null,
					getProductosJSONReqSuccessListener(),
					getProductosJSONReqErrorListener());
			
			mRequestQueue.add(myJSONReq);
		}
		
		GridView gridView = (GridView)findViewById(R.id.gridview);
		
		mProductos = new ArrayList<Producto>();
		
//		mPGAdapter = new ProductoGridAdapter(this, mProductos);
//		gridView.setAdapter(mPGAdapter);
		
		mPSGAdapter = new ProductoStickyGridAdapter(this, mProductos);
		gridView.setAdapter(mPSGAdapter);
		
	    // TODO crear una clase EndlessScrollListener como en ejemplo de Bankov sobre
	    // NetworkListView para ampliar la lista a medida que se hace scroll
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            
	            Intent i = new Intent(getApplicationContext(), Producto_detail_activity.class);
	            i.putExtra("productoId", mProductos.get(position).getId());
	            i.putExtra("nombre", mProductos.get(position).getNombre());
	            i.putExtra("descripcion", mProductos.get(position).getDescripcion());
	            i.putExtra("precio", mProductos.get(position).getPrecio());
	            i.putExtra("tienda", mProductos.get(position).getTienda().getNombre());
	            i.putExtra("url_imagen", mProductos.get(position).getImagen());
	            
	            i.putExtra("idTienda", mProductos.get(position).getTienda().getId());
	            i.putExtra("direccionTienda", mProductos.get(position).getTienda().getDireccion());
	            i.putExtra("localidadTienda", mProductos.get(position).getTienda().getLocalidad());
	            i.putExtra("correoTienda", mProductos.get(position).getTienda().getCorreo());
	            i.putExtra("phoneTienda", mProductos.get(position).getTienda().getPhone1());
	            i.putExtra("webTienda", mProductos.get(position).getTienda().getWeb());
	            i.putExtra("url_imagen_tienda", mProductos.get(position).getTienda().getUrl_imagen());
	            i.putExtra("latTienda", mProductos.get(position).getTienda().getLat());
	            i.putExtra("lonTienda", mProductos.get(position).getTienda().getLon());
	            
	            startActivity(i);
	        }
	    });
	}
	
	private ErrorListener getProductosJSONReqErrorListener() {
		
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				//ActivityDialogs.showErrorDialog(EscaparateJSON.this, getString(R.string.ERROR_INTERNO));
				//error.printStackTrace();
				
				ActivityDialogs.showErrorDialog(EscaparateJSON.this, error, getString(R.string.ERROR_INTERNO));
				
			}
		};
	}
	
	private Listener<JSONArray> getProductosJSONReqSuccessListener() {
		
		return new Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				
				try {
					
					Log.i(TAG, getString(R.string.FROM_SERVER));
					Log.i(TAG, response.toString(4));

					for(int i=0; i< response.length(); i++) {
						JSONObject jsonObject = response.getJSONObject(i);
						Producto producto = JSONObjectConversor.toProducto(jsonObject);
						mProductos.add(producto);
					}
					
					// para los sticky headers
					
					for(Iterator<Producto> iterator = mProductos.iterator(); iterator.hasNext();) {
						
						Producto product = iterator.next();
						
						String ym = ActivityUtils.dateToString(product.getFecha_retirada(), "yyyy-MM-dd");
						if (!sectionMap.containsKey(ym)) {
							product.setSection(section);
							sectionMap.put(ym, section);
							section++;
						} else {
							product.setSection(sectionMap.get(ym));
						}
					}
					
					//////////////////////////
					
//					List<Producto> aux = new ArrayList<Producto>();
					
					// usamos un iterador para borrar en un bucle los productos con stock cero
//					for(Iterator<Producto> iterator = mProductos.iterator(); iterator.hasNext();) {
//						
//						Producto product = iterator.next();
//						
//						if (product.getStock() == 0) {
//							
//							// añadimos el producto a una lista auxiliar
//							aux.add(product);
//							// borramos el producto sin stock de la lista
//							iterator.remove();
//							
//						}
//					}
					
					// añadimos los elementos de la lista auxiliar al final de la lista original
//					mProductos.addAll(aux);
					
					// mPGAdapter.notifyDataSetChanged();
					mPSGAdapter.notifyDataSetChanged();
					
				} catch (JSONException e) {
					
					ActivityDialogs.showErrorDialog(EscaparateJSON.this, getString(R.string.ERROR_INTERNO));
					
				}
			}
		};
	}

}
