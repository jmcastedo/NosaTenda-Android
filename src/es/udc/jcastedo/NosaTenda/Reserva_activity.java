package es.udc.jcastedo.NosaTenda;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonAuthObjectRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import es.udc.jcastedo.NosaTenda.json.Cliente;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Reserva_activity extends BaseActivity {
	
	private static final String TAG = "Reserva_activity";
	
	private Bundle extras;
	
	private EditText unidades_reserva;
	private TextView anombrede_reserva;
	private EditText nombre_reserva;
	private TextView textView_correo;
	private EditText editText_correo;
	private TextView textView_pass_reserva;
	private EditText edit_password_reserva;
	private TextView textView_confirm_pass_reserva;
	private EditText editText_confirm_pass_reserva;
	private LinearLayout buttons;
	private Button btnConfirmarReserva;
	private ProgressBar progress_reserva;
	
	private Long productoId = null;
	private Double precio = null;
	private Long clienteId = null;
	private Long stock = null;
	
	private String correoCliente;
	private String passwordCliente;
	
	private int opcion; // VALUES: {1,2,3}
	private final int RESERVA_SIN_LOGIN = 1;
	private final int RESERVA_SIN_CUENTA = 2;
	private final int RESERVA_CON_LOGIN = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reserva);
		
		auxiliaryOnCreate();
		
		TextView nombre_producto = (TextView)findViewById(R.id.textView_reserva_nombre_producto);
		progress_reserva = (ProgressBar)findViewById(R.id.loading_reserva);
		
		unidades_reserva = (EditText)findViewById(R.id.editText_unidades_reserva);
		anombrede_reserva = (TextView)findViewById(R.id.textView_reserva_anombre);
		nombre_reserva = (EditText)findViewById(R.id.editText_nombre_reserva);
		textView_correo = (TextView)findViewById(R.id.textView_reserva_correo);
		editText_correo = (EditText)findViewById(R.id.editText_correo_reserva);
		textView_pass_reserva = (TextView)findViewById(R.id.textView_reserva_password);
		edit_password_reserva = (EditText)findViewById(R.id.editText_password_reserva);
		textView_confirm_pass_reserva = (TextView)findViewById(R.id.textView_reserva_confirm_password);
		editText_confirm_pass_reserva = (EditText)findViewById(R.id.editText_confirm_password_reserva);
		
		extras = getIntent().getExtras();
		if (extras != null) {
			productoId = extras.getLong("productoId");
			nombre_producto.setText(extras.getCharSequence("nombre"));
			precio = extras.getDouble("precio");
			clienteId = idClienteSession;
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
		
		buttons = (LinearLayout)findViewById(R.id.layout_buttons_reserva);
		btnConfirmarReserva = (Button)findViewById(R.id.btn_confirmar_reserva);
		
		if (clienteId == 0) {
			
			Button btnLoginReserva = (Button)findViewById(R.id.btn_login_reserva);
			btnLoginReserva.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					anombrede_reserva.setVisibility(View.GONE);
					textView_correo.setVisibility(View.VISIBLE);
					editText_correo.setVisibility(View.VISIBLE);
					textView_pass_reserva.setVisibility(View.VISIBLE);
					edit_password_reserva.setVisibility(View.VISIBLE);
					buttons.setVisibility(View.GONE);
					btnConfirmarReserva.setVisibility(View.VISIBLE);
					opcion = RESERVA_SIN_LOGIN;
					
				}
			});
			
			Button btnNuevaCuentaReserva = (Button)findViewById(R.id.btn_nueva_cuenta_reserva);
			btnNuevaCuentaReserva.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					nombre_reserva.setVisibility(View.VISIBLE);
					textView_correo.setVisibility(View.VISIBLE);
					editText_correo.setVisibility(View.VISIBLE);
					textView_pass_reserva.setVisibility(View.VISIBLE);
					edit_password_reserva.setVisibility(View.VISIBLE);
					textView_confirm_pass_reserva.setVisibility(View.VISIBLE);
					editText_confirm_pass_reserva.setVisibility(View.VISIBLE);
					buttons.setVisibility(View.GONE);
					btnConfirmarReserva.setVisibility(View.VISIBLE);
					opcion = RESERVA_SIN_CUENTA;
					
				}
			});
			
		} else {
			
			String URL = getCompleteURL(R.string.URL_GETCLIENTE) + "?clienteID=" + idClienteSession;
			
			JsonAuthObjectRequest jsonRequest = new JsonAuthObjectRequest(
						Method.GET,
						URL,
						null,
						getClienteJSONReqSuccessListener(),
						getClienteJSONReqErrorListener());
			
			jsonRequest.setUsername(correoSession);
			jsonRequest.setPassword(passwordSession);
			
			mRequestQueue.add(jsonRequest);
			
			Log.i(TAG, getString(R.string.TO_SERVER_GET));
			
		}
		
		
		btnConfirmarReserva.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
				
					progress_reserva.setVisibility(View.VISIBLE);
					
					final String unidadesParam = unidades_reserva.getEditableText().toString();
					
					String nombreReservaString = nombre_reserva.getEditableText().toString();
					String correoReservaString = editText_correo.getEditableText().toString();
					String passwordReservaString = edit_password_reserva.getEditableText().toString();
					String confirmPasswordReserva = editText_confirm_pass_reserva.getEditableText().toString();

					// checkeo de unidades
					if (!(ActivityUtils.checkUnidades(unidadesParam, getResources().getInteger(R.integer.MIN_UNITS), getResources().getInteger(R.integer.MAX_UNITS)))) {
						
						ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_UNIDADES));
						progress_reserva.setVisibility(View.GONE);
						return;
						
					}
					
					// checkeo de stock
					if (stock == null) {
						ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_UNAVAILABLE_STOCK));
						progress_reserva.setVisibility(View.GONE);
						return;
					} else if (!(ActivityUtils.checkUnidades(unidadesParam, 0, stock.intValue()))) {
						ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_INSUFFICIENT_STOCK));
						progress_reserva.setVisibility(View.GONE);
						return;
					}
					
					// checkeo de nombre de cliente
					if (opcion == RESERVA_SIN_CUENTA) {
						if (!(ActivityUtils.checkTextFieldNotEmpty(nombreReservaString))) {
							ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_NOMBRE));
							progress_reserva.setVisibility(View.GONE);
							return;
						}
					}
					
					// checkear campo correo
					if (opcion != RESERVA_CON_LOGIN) {
						if (!(ActivityUtils.checkTextFieldNotEmpty(correoReservaString))) {
							ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_CORREO));
							progress_reserva.setVisibility(View.GONE);
							return;
						}
					}
					
					// checkear campo contraseña
					if (!(ActivityUtils.checkTextFieldNotEmpty(passwordReservaString))) {
						ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_PASSWORD));
						progress_reserva.setVisibility(View.GONE);
						return;
					}
					if (!(ActivityUtils.checkPasswordField(passwordReservaString, getResources().getInteger(R.integer.MAX_CHARACTERS)))) {
						ActivityDialogs.showErrorDialog(Reserva_activity.this, String.format(getString(R.string.ERROR_PASSWORD_TOO_LONG), getResources().getInteger(R.integer.MAX_CHARACTERS)));
						progress_reserva.setVisibility(View.GONE);
						return;
					}
					
					// Checkear campo confirmar contraseña
					// TODO Aunque sólo sea un equals, pasar también a ActivityUtils
					if (opcion == RESERVA_SIN_CUENTA) {
						if (!ActivityUtils.checkStringsEguals(confirmPasswordReserva, passwordReservaString)) {
							ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_PASSWORDS_NOT_EQUAL));
							progress_reserva.setVisibility(View.GONE);
							return;
						}
					}
					
					// Petición al servicio para crear un nuevo cliente
					if (opcion == RESERVA_SIN_CUENTA) {
						JSONObject jsonNewClientBody = new JSONObject();
						jsonNewClientBody.put("nombre", nombreReservaString);
						jsonNewClientBody.put("correo", correoReservaString);
						jsonNewClientBody.put("password", passwordReservaString);
						
						correoCliente = correoReservaString;
						passwordCliente = passwordReservaString;
						
						JsonObjectRequest jsonNewClientRequest = new JsonObjectRequest(
								Method.POST,
								getCompleteURL(R.string.URL_NEWCLIENT),
								jsonNewClientBody,
								createNewClientJSONReqSuccessListener(),
								createNewClientJSONReqErrorListener());
						
						mRequestQueue.add(jsonNewClientRequest);
						
						Log.i(TAG, getString(R.string.TO_SERVER));
						Log.i(TAG, jsonNewClientBody.toString(4));
					}
					
					// Petición al servicio para loguearse/confirmar contraseña
					if (opcion == RESERVA_SIN_LOGIN ||
							opcion == RESERVA_CON_LOGIN) {
						JSONObject jsonLoginBody = new JSONObject();
						jsonLoginBody.put("correo", correoReservaString);
						jsonLoginBody.put("password", passwordReservaString);
						
						correoCliente = correoReservaString;
						passwordCliente = passwordReservaString;
						
						String uri = getCompleteURL(R.string.URL_LOGIN) + "?correo=" + correoReservaString + "&password=" + passwordReservaString;
						
						JsonObjectRequest jsonLoginRequest = new JsonObjectRequest(
								Method.GET,
								uri,
								null,
								createLoginJSONReqSuccessListener(),
								createLoginJSONReqErrorListener());
						
						mRequestQueue.add(jsonLoginRequest);
						
						Log.i(TAG, getString(R.string.TO_SERVER_GET));
					}
					
				} catch (JSONException e) {
					
					// Test
					//ActivityUtils.showErrorDialog(Reserva_activity.this, e.getMessage());
					// Producción
					ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_INTERNO));
					progress_reserva.setVisibility(View.GONE);
					
				}

			} // onClick		
		}); // setOnClickListener
		
	} // onCreate
	
	private ErrorListener getClienteJSONReqErrorListener() {
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				// Test
				//ActivityUtils.showErrorDialog(Reserva_activity.this, error.getMessage());
				// Producción
				//ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_INTERNO));
				progress_reserva.setVisibility(View.GONE);
				
				ActivityDialogs.showErrorDialog(Reserva_activity.this, error, getString(R.string.ERROR_INTERNO));
				
			}
		};
	}

	private Listener<JSONObject> getClienteJSONReqSuccessListener() {
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				
				try {
					
					Cliente cliente = new Cliente();
				
					Log.i(TAG, getString(R.string.FROM_SERVER));
					Log.i(TAG, response.toString(4));
					
					cliente.setNombre(response.getString(getString(R.string.nombreTag)));
					cliente.setCorreo(response.getString(getString(R.string.correoTag)));
					
					Log.i(TAG, "Petición getCliente exitosa");
					
					nombre_reserva.setText(cliente.getNombre());
					nombre_reserva.setKeyListener(null);
					nombre_reserva.setVisibility(View.VISIBLE);
					textView_correo.setVisibility(View.VISIBLE);
					editText_correo.setText(cliente.getCorreo());
					editText_correo.setKeyListener(null);
					editText_correo.setVisibility(View.VISIBLE);
					textView_pass_reserva.setVisibility(View.VISIBLE);
					edit_password_reserva.setVisibility(View.VISIBLE);
					buttons.setVisibility(View.GONE);
					btnConfirmarReserva.setVisibility(View.VISIBLE);
					opcion = RESERVA_CON_LOGIN;
					
				} catch (JSONException e) {
					
					// Test
					//ActivityUtils.showErrorDialog(Reserva_activity.this, error.getMessage());
					// Producción
					ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_INTERNO));
					progress_reserva.setVisibility(View.GONE);
					
				}
				
			}
		};
	}
	protected ErrorListener createLoginJSONReqErrorListener() {
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				// Test
				//ActivityUtils.showErrorDialog(Reserva_activity.this, error.getMessage());
				// Producción
				//ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_INTERNO_LOGIN));
				progress_reserva.setVisibility(View.GONE);
				
				ActivityDialogs.showErrorDialog(Reserva_activity.this, error, getString(R.string.ERROR_INTERNO_LOGIN));
				
			}
		};
	}

	protected Listener<JSONObject> createLoginJSONReqSuccessListener() {
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				
				try {
					
					Log.i(TAG, getString(R.string.FROM_SERVER));
					Log.i(TAG, response.toString(4));
					
					Long clienteIdResponse = response.getLong(getString(R.string.clienteId));
					
					Log.i(TAG, "Petición createLogin existosa");
					
					// asignamos el id de la respuesta a la variable global clienteId de la Activity
					clienteId = clienteIdResponse;
					
					// asignamos el id de la respuesta a la aplicación
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putLong(getString(R.string.clienteId), clienteIdResponse);
					editor.putString(getString(R.string.correoTag), correoCliente);
					editor.putString(getString(R.string.passwordTag), passwordCliente);
					editor.commit();
					
					// hacemos la peticion de reservas
					reserve();
					
				} catch (JSONException e) {
					
					// Test
					//ActivityUtils.showErrorDialog(Reserva_activity.this, error.getMessage());
					// Producción
					ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_INTERNO_LOGIN));
					progress_reserva.setVisibility(View.GONE);
					
				}
				
			}
		};
	}

	private ErrorListener createMyJSONReqErrorListener() {
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				// Test
				//ActivityUtils.showErrorDialog(Reserva_activity.this, error.getMessage());
				// Producción
				ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_INTERNO_RESERVA));
				progress_reserva.setVisibility(View.GONE);
				
				ActivityDialogs.showErrorDialog(Reserva_activity.this, error, getString(R.string.ERROR_INTERNO_RESERVA));
				
			}
		};
	}

	private Listener<JSONObject> createMyJSONReqSuccessListener() {
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				
				try {
					
					Log.i(TAG, getString(R.string.FROM_SERVER));
					Log.i(TAG, response.toString(4));
					
					Long reservaId = response.getLong(getString(R.string.reservaId));
					
					Log.i(TAG, "Petición createNewClient exitosa");
					
					// TODO probar que funciona
					ActivityDialogs.showSuccessDialogWithReturn(Reserva_activity.this, getString(R.string.SUCCESS_RESERVA) + " " + reservaId)
						.setOnCancelListener(new OnCancelListener() {
							
							@Override
							public void onCancel(DialogInterface dialog) {
								finish();
								
							}
						})
						.show();
					progress_reserva.setVisibility(View.GONE);
					
				} catch (JSONException e) {
					
					// Test
					//ActivityUtils.showErrorDialog(Reserva_activity.this, error.getMessage());
					// Producción
					ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_INTERNO_RESERVA));
					progress_reserva.setVisibility(View.GONE);
					
				}
				
			}
		};
	}
	
	private ErrorListener createNewClientJSONReqErrorListener() {
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				// Test
				//ActivityUtils.showErrorDialog(Reserva_activity.this, error.getMessage());
				// Producción
				//ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_INTERNO_NEWCLIENTE));
				progress_reserva.setVisibility(View.GONE);
				
				ActivityDialogs.showErrorDialog(Reserva_activity.this, error, getString(R.string.ERROR_INTERNO_NEWCLIENTE));
				
			}
		};
	}

	private Listener<JSONObject> createNewClientJSONReqSuccessListener() {
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				
				try {
					
					Log.i(TAG, getString(R.string.FROM_SERVER));
					Log.i(TAG, response.toString(4));
					
					Long clienteIdResponse = response.getLong(getString(R.string.clienteId));
					
					Log.i(TAG, "Petición createNewClient exitosa");
					
					// asignamos el id de la respuesta a la variable global clienteId de la Activity
					clienteId = clienteIdResponse;
					
					// asignamos el id de la respuesta a la aplicación
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putLong(getString(R.string.clienteId), clienteIdResponse);
					editor.putString(getString(R.string.correoTag), correoCliente);
					editor.putString(getString(R.string.passwordTag), passwordCliente);
					editor.commit();
					
					// hacemos la peticion de reserva
					reserve();
					
				} catch (JSONException e) {
					
					// Test
					//ActivityUtils.showErrorDialog(Reserva_activity.this, error.getMessage());
					// Producción
					ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_INTERNO_NEWCLIENTE));
					progress_reserva.setVisibility(View.GONE);
					
				}
				
			}
		};
	}
	
	private void reserve() {
		
		final String unidadesParam = unidades_reserva.getEditableText().toString();
		final String precioParam = String.valueOf(precio);
		final String productoIdParam = String.valueOf(productoId);
		
		try {
			
			// Petición al servicio para hacer la reserva
			JSONObject jsonBody = new JSONObject();
			
			jsonBody.put("unidades", unidadesParam);
			jsonBody.put("precio", precioParam);
			jsonBody.put("productoId", productoIdParam);
			final String clienteIdParam = String.valueOf(clienteId);
			jsonBody.put("clienteId", clienteIdParam);
		
			JsonAuthObjectRequest jsonRequest = new JsonAuthObjectRequest(
					Method.POST,
					getCompleteURL(R.string.URL_RESERVE),
					jsonBody,
					createMyJSONReqSuccessListener(),
					createMyJSONReqErrorListener());
			
			jsonRequest.setUsername(correoCliente);
			jsonRequest.setPassword(passwordCliente);
			
			mRequestQueue.add(jsonRequest);
			
			Log.i(TAG, getString(R.string.TO_SERVER));
	        Log.i(TAG, jsonBody.toString(4));
	        
		} catch (JSONException e) {
			
			// Test
			//ActivityUtils.showErrorDialog(Reserva_activity.this, error.getMessage());
			// Producción
			ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_INTERNO_RESERVA));
			progress_reserva.setVisibility(View.GONE);
			
		}
		
		
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
					
					stock = stockResponse;
					
				} catch (JSONException e) {
					
					// Test
	    			//ActivityUtils.showErrorDialog(Compra_activity.this, error.getMessage());
	    			// Producción
					ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_INTERNO));
	    			progress_reserva.setVisibility(View.GONE);
	    			
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
				//ActivityDialogs.showErrorDialog(Reserva_activity.this, getString(R.string.ERROR_INTERNO));
				progress_reserva.setVisibility(View.GONE);
				
				ActivityDialogs.showErrorDialog(Reserva_activity.this, error, getString(R.string.ERROR_INTERNO));
				
			}
		};
	}

}
