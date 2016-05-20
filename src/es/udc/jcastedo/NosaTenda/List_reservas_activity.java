package es.udc.jcastedo.NosaTenda;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonAuthArrayRequest;
import com.android.volley.toolbox.JsonAuthObjectRequest;
import es.udc.jcastedo.NosaTenda.json.Reserva;
import es.udc.jcastedo.NosaTenda.json.utils.JSONObjectConversor;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class List_reservas_activity extends BaseActivity {
	
	private ViewGroup mContainerView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_reservas);
		
		auxiliaryOnCreate();
		
		mContainerView = (ViewGroup)findViewById(R.id.lista_reservas_container);
		
		String uri = getCompleteURL(R.string.URL_CLIENT_RESERVES) + "?clienteId=" + idClienteSession;
		
		JsonAuthArrayRequest getReservasJSONReq = new JsonAuthArrayRequest(
				Request.Method.GET,
				uri,
				null,
				getReservasJSONReqSuccessListener(),
				getReservasJSONReqErrorListener());
		
		getReservasJSONReq.setUsername(correoSession);
		getReservasJSONReq.setPassword(passwordSession);
		
		mRequestQueue.add(getReservasJSONReq);
	}

	private ErrorListener getReservasJSONReqErrorListener() {
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				ActivityDialogs.showErrorDialog(List_reservas_activity.this, error, getString(R.string.ERROR_INTERNO));
				
			}
		};
	}

	private Listener<JSONArray> getReservasJSONReqSuccessListener() {
		return new Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				
				try {
					
					if (response.length() != 0) {
						findViewById(R.id.empty_reserves).setVisibility(View.GONE);
					}
					for(int i=0; i<response.length(); i++) {
						JSONObject jsonObject = response.getJSONObject(i);
						Reserva reserva = JSONObjectConversor.toReserva(jsonObject);
						addItem(reserva);
					}
					
				} catch (JSONException e) {
					
					ActivityDialogs.showErrorDialog(List_reservas_activity.this, getString(R.string.ERROR_INTERNO));
					
				}
			}
		};
	}
	
	private void addItem(Reserva reserva) {
		// Instantiate a new "row" view
		final ViewGroup newView = (ViewGroup)LayoutInflater.from(this).inflate(
				R.layout.elemento_lista_reservas, mContainerView, false);
		
		((TextView)newView.findViewById(R.id.text_producto_reservado)).setText(reserva.getProducto().getNombre());
		((TextView)newView.findViewById(R.id.text_estado_reservado)).setText(reserva.getEstadoString());
		((TextView)newView.findViewById(R.id.text_fecha_reservado)).setText(ActivityUtils.dateToString(reserva.getFecha(), "dd-MM-yyyy"));
		
		final Reserva reservaAux = reserva;
		
		newView.findViewById(R.id.text_producto_reservado).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				callReservaDetail(reservaAux);
			}
		});
		
		newView.findViewById(R.id.text_estado_reservado).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				callReservaDetail(reservaAux);
				
			}
		});
		
		newView.findViewById(R.id.text_fecha_reservado).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				callReservaDetail(reservaAux);
			}
		});
		
		// Set a click listener for the "X" button in the row that will remove/cancel the row.
		newView.findViewById(R.id.button_cancel_reserva).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// checkeo del estado de la reserva
				if (reservaAux.getEstadoString().equals(getString(R.string.RESERVA_STATE_CANCELADA))) {
					ActivityDialogs.showErrorDialog(List_reservas_activity.this, getString(R.string.ERROR_RESERVA_CANCELADA));
					return;
				}
				
				if (reservaAux.getEstadoString().equals(getString(R.string.RESERVA_STATE_ENTREGADA))) {
					ActivityDialogs.showErrorDialog(List_reservas_activity.this, getString(R.string.ERROR_RESERVA_ENTREGADA));
					return;
				}
				
				AlertDialog.Builder b = new AlertDialog.Builder(List_reservas_activity.this);
				b.setTitle(getString(R.string.dialog_cancel_reserva));
				b.setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						JSONObject jsonCancelBody = new JSONObject();
						
						try {
							jsonCancelBody.put(getString(R.string.reservaId), reservaAux.getId());
							
							JsonAuthObjectRequest jsonCancelRequest = new JsonAuthObjectRequest(
									Method.POST,
									getCompleteURL(R.string.URL_CANCEL_RESERVE),
									jsonCancelBody,
									cancelJSONReqSuccessListener(),
									cancelJSONReqErrorListener());
							
							jsonCancelRequest.setUsername(correoSession);
							jsonCancelRequest.setPassword(passwordSession);
							
							mRequestQueue.add(jsonCancelRequest);
						} catch (JSONException e) {
							// Test
							//ActivityUtils.showErrorDialog(List_reservas_activity.this, e.getMessage());
							// Producción
							ActivityDialogs.showErrorDialog(List_reservas_activity.this, getString(R.string.ERROR_INTERNO));
						}
					}
				});
				b.setNegativeButton(getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// we do nothing
						
					}
				});
				b.show();
				
			}

			private Listener<JSONObject> cancelJSONReqSuccessListener() {
				return new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						
						try {
							
							Long reservaIdResponse = response.getLong(getString(R.string.reservaId));
							
							ActivityDialogs.showSuccessDialog(List_reservas_activity.this, getString(R.string.SUCCESS_CANCELAR_RESERVA) + " " + reservaIdResponse);
							
							((TextView)newView.findViewById(R.id.text_estado_reservado)).setText(getString(R.string.RESERVA_STATE_CANCELADA));
							
							// Como queremos mostrar también reservas canceladas, no borramos las filas
							
							// Remove the row from its parent (the container view).
			                // Because mContainerView has android:animateLayoutChanges set to true,
			                // this removal is automatically animated.
							//mContainerView.removeView(newView);
							
							// If there are no rows remaining, show the empty view.
							if (mContainerView.getChildCount() == 0) {
								findViewById(R.id.empty_reserves).setVisibility(View.VISIBLE);
							}
							
						} catch (JSONException e) {
							
							// Test
							//ActivityUtils.showErrorDialog(List_reservas_activity.this, error.getMessage());
							// Producción
							ActivityDialogs.showErrorDialog(List_reservas_activity.this, getString(R.string.ERROR_INTERNO_CANCELAR_RESERVA));
							
						}
						
					}
				};
			} // cancelJSONReqSuccessListener
			
			private ErrorListener cancelJSONReqErrorListener() {
				return new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						
						// Test
						//ActivityUtils.showErrorDialog(List_reservas_activity.this, error.getMessage());
						// Producción
						ActivityDialogs.showErrorDialog(List_reservas_activity.this, error, getString(R.string.ERROR_INTERNO_CANCELAR_RESERVA));
						
					}
				};
			} // cancelJSONReqErrorListener
		}); // setOnClickListener
		
		mContainerView.addView(newView, 0);
	} // addItem
	
	private void callReservaDetail(Reserva reserva) {
		
		Intent i = new Intent(getApplicationContext(), Reserva_detail_activity.class);
		i.putExtra("nombre", reserva.getProducto().getNombre());
		i.putExtra("fecha", reserva.getFecha());
		i.putExtra("unidades", reserva.getUnidades());
		i.putExtra("subtotal", reserva.getPrecio());
		i.putExtra("total", reserva.getTotal());
		i.putExtra("url_imagen", reserva.getProducto().getImagen());
		
		i.putExtra("idTienda", reserva.getProducto().getTienda().getId());
		i.putExtra("nombreTienda", reserva.getProducto().getTienda().getNombre());
		i.putExtra("direccionTienda", reserva.getProducto().getTienda().getDireccion());
		i.putExtra("localidadTienda", reserva.getProducto().getTienda().getLocalidad());
		i.putExtra("correoTienda", reserva.getProducto().getTienda().getCorreo());
		i.putExtra("phoneTienda", reserva.getProducto().getTienda().getPhone1());
		i.putExtra("webTienda", reserva.getProducto().getTienda().getWeb());
		i.putExtra("url_imagen_tienda", reserva.getProducto().getTienda().getUrl_imagen());
		i.putExtra("latTienda", reserva.getProducto().getTienda().getLat());
		i.putExtra("lonTienda", reserva.getProducto().getTienda().getLon());
		
		startActivity(i);
	}
}
