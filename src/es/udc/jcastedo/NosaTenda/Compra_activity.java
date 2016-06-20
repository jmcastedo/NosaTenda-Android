package es.udc.jcastedo.NosaTenda;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonAuthObjectRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import es.udc.jcastedo.NosaTenda.json.Cliente;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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

public class Compra_activity extends BaseActivity {

	private static final String TAG = "Compra_activity";
	
	private Bundle extras;
	
	private EditText unidades_compra;
	private TextView anombrede_compra;
	private EditText nombre_compra;
	private TextView textView_correo;
	private EditText editText_correo;
	private TextView textView_pass_compra;
	private EditText edit_password_compra;
	private TextView textView_confirm_pass_compra;
	private EditText editText_confirm_pass_compra;
	private LinearLayout buttons;
	private Button btnConfirmarCompra;
	private ProgressBar progress_compra;
	
	private Double precio = null;
	private Long clienteId = null;
	private Long productoId = null;
	private String nombreProducto = "";
	private Long stock = null;
	
	private String nombreCliente;
	private String correoCliente;
	private String passwordCliente;
	
	private int opcion; // VALUES: {1,2,3}
	private final int COMPRA_SIN_LOGIN = 1;
	private final int COMPRA_SIN_CUENTA = 2;
	private final int COMPRA_CON_LOGIN = 3;
	
	private static final int REQUEST_CODE_PAYMENT = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compra);
		
		auxiliaryOnCreate();
		
		TextView nombre_producto = (TextView)findViewById(R.id.textView_compra_nombre_producto);
		progress_compra = (ProgressBar)findViewById(R.id.loading_compra);
		
		unidades_compra = (EditText)findViewById(R.id.editText_unidades_compra);
		anombrede_compra = (TextView)findViewById(R.id.textView_compra_anombre);
		nombre_compra = (EditText)findViewById(R.id.editText_nombre_compra);
		textView_correo = (TextView)findViewById(R.id.textView_compra_correo);
		editText_correo = (EditText)findViewById(R.id.editText_correo_compra);
		textView_pass_compra = (TextView)findViewById(R.id.textView_compra_password);
		edit_password_compra = (EditText)findViewById(R.id.editText_password_compra);
		textView_confirm_pass_compra = (TextView)findViewById(R.id.textView_compra_confirm_password);
		editText_confirm_pass_compra = (EditText)findViewById(R.id.editText_confirm_password_compra);
		
		extras = getIntent().getExtras();
		if (extras != null) {
			nombre_producto.setText(extras.getCharSequence("nombre"));
			nombreProducto = (String) extras.getCharSequence("nombre");
			precio = extras.getDouble("precio");
			clienteId = idClienteSession;
			productoId = extras.getLong("productoId");
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
		
		buttons = (LinearLayout)findViewById(R.id.layout_buttons_compra);
		btnConfirmarCompra = (Button)findViewById(R.id.btn_confirmar_compra);
		
		if (clienteId == 0) {
			
			Button btnLoginCompra = (Button)findViewById(R.id.btn_login_compra);
			btnLoginCompra.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					anombrede_compra.setVisibility(View.GONE);
					textView_correo.setVisibility(View.VISIBLE);
					editText_correo.setVisibility(View.VISIBLE);
					textView_pass_compra.setVisibility(View.VISIBLE);
					edit_password_compra.setVisibility(View.VISIBLE);
					buttons.setVisibility(View.GONE);
					btnConfirmarCompra.setVisibility(View.VISIBLE);
					opcion = COMPRA_SIN_LOGIN;
					
				}
			});
			
			Button btnNuevaCuentaCompra = (Button)findViewById(R.id.btn_nueva_cuenta_compra);
			btnNuevaCuentaCompra.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					nombre_compra.setVisibility(View.VISIBLE);
					textView_correo.setVisibility(View.VISIBLE);
					editText_correo.setVisibility(View.VISIBLE);
					textView_pass_compra.setVisibility(View.VISIBLE);
					edit_password_compra.setVisibility(View.VISIBLE);
					textView_confirm_pass_compra.setVisibility(View.VISIBLE);
					editText_confirm_pass_compra.setVisibility(View.VISIBLE);
					buttons.setVisibility(View.GONE);
					btnConfirmarCompra.setVisibility(View.VISIBLE);
					opcion = COMPRA_SIN_CUENTA;
					
				}
			});
		} else {
			
			// compra con login
			String URL = getCompleteURL(R.string.URL_GETCLIENTE) + "?clienteID=" + idClienteSession;
			
			JsonAuthObjectRequest jsonRequest = new JsonAuthObjectRequest(
						Method.GET,
						URL,
						null,
						getClienteDataJSONReqSuccessListener(),
						getClienteDataJSONReqErrorListener());
			
			jsonRequest.setUsername(correoSession);
			jsonRequest.setPassword(passwordSession);
			
			mRequestQueue.add(jsonRequest);
			
			Log.i(TAG, getString(R.string.TO_SERVER_GET));
			
		}
		
		btnConfirmarCompra.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
					
					progress_compra.setVisibility(View.VISIBLE);
					
					final String unidadesParam = unidades_compra.getEditableText().toString();
					
					String nombreCompraString = nombre_compra.getEditableText().toString();
					String correoCompraString = editText_correo.getEditableText().toString();
					String passwordCompraString = edit_password_compra.getEditableText().toString();
					String confirmPasswordCompra = editText_confirm_pass_compra.getEditableText().toString();
					
					// checkeo de unidades
					if (!(ActivityUtils.checkUnidades(unidadesParam, getResources().getInteger(R.integer.MIN_UNITS), getResources().getInteger(R.integer.MAX_UNITS)))) {
						ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_UNIDADES));
						progress_compra.setVisibility(View.GONE);
						return;
					}
					
					// checkeo de stock
					if (stock == null) {
						ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_UNAVAILABLE_STOCK));
						progress_compra.setVisibility(View.GONE);
						return;
					} else if (!(ActivityUtils.checkUnidades(unidadesParam, 0, stock.intValue()))) {
						ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INSUFFICIENT_STOCK));
						progress_compra.setVisibility(View.GONE);
						return;
					}
					
					// checkeo de nombre de cliente
					if (opcion == COMPRA_SIN_CUENTA) {
						if (!(ActivityUtils.checkTextFieldNotEmpty(nombreCompraString))) {
							ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_NOMBRE));
							progress_compra.setVisibility(View.GONE);
							return;
						}
					}
					
					// checkear campo correo
					if (opcion != COMPRA_CON_LOGIN) {
						if (!(ActivityUtils.checkTextFieldNotEmpty(correoCompraString))) {
							ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_CORREO));
							progress_compra.setVisibility(View.GONE);
							return;
						}
					}
					
					// checkear campo contraseña
					if (!(ActivityUtils.checkTextFieldNotEmpty(passwordCompraString))) {
						ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_PASSWORD));
						progress_compra.setVisibility(View.GONE);
						return;
					}
					if (!(ActivityUtils.checkPasswordField(passwordCompraString, getResources().getInteger(R.integer.MAX_CHARACTERS)))) {
						ActivityDialogs.showErrorDialog(Compra_activity.this, String.format(getString(R.string.ERROR_PASSWORD_TOO_LONG), getResources().getInteger(R.integer.MAX_CHARACTERS)));
						progress_compra.setVisibility(View.GONE);
						return;
					}
					
					// Checkear campo confirmar contraseña
					// TODO Aunque sólo sea un equals, pasar también a ActivityUtils
					if (opcion == COMPRA_SIN_CUENTA) {
						if (!(confirmPasswordCompra.equals(passwordCompraString))) {
							ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_PASSWORDS_NOT_EQUAL));
							progress_compra.setVisibility(View.GONE);
							return;
						}
					}
					
					// Petición al servicio para crear un nuevo cliente
					if (opcion == COMPRA_SIN_CUENTA) {
						JSONObject jsonNewClientBody = new JSONObject();
						jsonNewClientBody.put("nombre", nombreCompraString);
						jsonNewClientBody.put("correo", correoCompraString);
						jsonNewClientBody.put("password", passwordCompraString);
						
						correoCliente = correoCompraString;
						passwordCliente = passwordCompraString;
						
						JsonObjectRequest jsonNewClientRequest = new JsonObjectRequest(
								Method.POST,
								getCompleteURL(R.string.URL_NEWCLIENT),
								jsonNewClientBody,
								newClientJSONReqSuccessListener(),
								newClientJSONReqErrorListener());
						
						mRequestQueue.add(jsonNewClientRequest);
						
						Log.i(TAG, getString(R.string.TO_SERVER));
						Log.i(TAG, jsonNewClientBody.toString(4));
						
					}
					
					// Petición al servicio para loguearse/confirmar contraseña
					if (opcion == COMPRA_SIN_LOGIN ||
							opcion == COMPRA_CON_LOGIN) {
						JSONObject jsonLoginBody = new JSONObject();
						jsonLoginBody.put("correo", correoCompraString);
						jsonLoginBody.put("password", passwordCompraString);
						
						correoCliente = correoCompraString;
						passwordCliente = passwordCompraString;
						
						String uri = getCompleteURL(R.string.URL_LOGIN) + "?correo=" + correoCompraString + "&password=" + passwordCompraString;
						
						JsonObjectRequest jsonLoginRequest = new JsonObjectRequest(
								Method.GET,
								uri,
								null,
								loginJSONReqSuccessListener(),
								loginJSONReqErrorListener());
						
						mRequestQueue.add(jsonLoginRequest);
						
						Log.i(TAG, getString(R.string.TO_SERVER_GET));
						
					}
					
				} catch (JSONException e) {
					// Test
					//ActivityUtils.showErrorDialog(Compra_activity.this, e.getMessage());
					// Producción
					ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INTERNO));
					progress_compra.setVisibility(View.GONE);
				}
			} // onClick
		}); // setOnClickListener
		
		
	} // onCreate
	
	private ErrorListener getClienteDataJSONReqErrorListener() {
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				// Test
				//ActivityUtils.showErrorDialog(Compra_activity.this, error.getMessage());
				// Producción
				ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INTERNO));
				progress_compra.setVisibility(View.GONE);
				
				ActivityDialogs.showErrorDialog(Compra_activity.this, error, getString(R.string.ERROR_INTERNO));
				
			}
		};
	}

	private Listener<JSONObject> getClienteDataJSONReqSuccessListener() {
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
					
					nombreCliente = cliente.getNombre();
					correoCliente = cliente.getCorreo();
					
					nombre_compra.setText(nombreCliente);
					nombre_compra.setKeyListener(null);
					nombre_compra.setVisibility(View.VISIBLE);
					textView_correo.setVisibility(View.VISIBLE);
					editText_correo.setText(correoCliente);
					editText_correo.setKeyListener(null);
					editText_correo.setVisibility(View.VISIBLE);
					textView_pass_compra.setVisibility(View.VISIBLE);
					edit_password_compra.setVisibility(View.VISIBLE);
					buttons.setVisibility(View.GONE);
					btnConfirmarCompra.setVisibility(View.VISIBLE);
					opcion = COMPRA_CON_LOGIN;
					
				} catch (JSONException e) {
					
					// Test
					//ActivityUtils.showErrorDialog(Compra_activity.this, error.getMessage());
					// Producción
					ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INTERNO));
					progress_compra.setVisibility(View.GONE);
					
				}
				
			}
		};
	}

	private ErrorListener loginJSONReqErrorListener() {
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				// Test
				//ActivityUtils.showErrorDialog(Compra_activity.this, error.getMessage());
				// Producción
				//ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INTERNO_LOGIN));
				progress_compra.setVisibility(View.GONE);
				
				ActivityDialogs.showErrorDialog(Compra_activity.this, error, getString(R.string.ERROR_INTERNO_LOGIN));
				
			}
		};
	}
	
	private Listener<JSONObject> loginJSONReqSuccessListener() {
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
					
					// hacemos la peticion de compra
					buy();
					
				} catch (JSONException e) {
					
					// Test
					//ActivityUtils.showErrorDialog(Compra_activity.this, error.getMessage());
					// Producción
					ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INTERNO_LOGIN));
					progress_compra.setVisibility(View.GONE);
					
				}
				
			}
		};
	}
	
	private ErrorListener newClientJSONReqErrorListener() {
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				// Test
				//ActivityUtils.showErrorDialog(Compra_activity.this, error.getMessage());
				// Producción
				//ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INTERNO_NEWCLIENTE));
				progress_compra.setVisibility(View.GONE);
				
				ActivityDialogs.showErrorDialog(Compra_activity.this, error, getString(R.string.ERROR_INTERNO_NEWCLIENTE));
				
			}
		};
	}
	
	private Listener<JSONObject> newClientJSONReqSuccessListener() {
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
					
					// hacemos la peticion de compra
					buy();
					
				} catch (JSONException e) {
					
					// Test
					//ActivityUtils.showErrorDialog(Compra_activity.this, error.getMessage());
					// Producción
					ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INTERNO_NEWCLIENTE));
					progress_compra.setVisibility(View.GONE);
					
				}
				
			}
		};
	}
	
	private void buy() {
		
		final String unidadesParam = unidades_compra.getEditableText().toString();
		int unidadesAsInt = Integer.parseInt(unidadesParam);
		String precio_fmt = ActivityUtils.fmtTwoZeros(precio);
		BigDecimal precioAsBig = new BigDecimal(precio_fmt);
		
		Log.i(TAG, "Buy: unidades = " + unidadesAsInt);
		Log.i(TAG, "Buy: preciosAsBig = " + precioAsBig);
		
		// Paypal Configuration
		
		/**
	     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
	     * 
	     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
	     * from https://developer.paypal.com
	     * 
	     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
	     * without communicating to PayPal's servers.
	     */
		PayPalConfiguration config = new PayPalConfiguration()
				.environment(getString(R.string.ENVIRONMENT_SANDBOX))
				.defaultUserEmail(correoCliente)
				.acceptCreditCards(true)
	            .clientId(getString(R.string.CONFIG_CLIENT_ID));
		
		Intent intentService = new Intent(this, PayPalService.class);
		intentService.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
		startService(intentService);
		
		PayPalItem[] items =
            {
                    new PayPalItem(
                    		nombreProducto,
                    		unidadesAsInt,
                    		precioAsBig,
                    		getString(R.string.currency),
                            "N/A")
            };
		
        BigDecimal subtotal = PayPalItem.getItemTotal(items);
        BigDecimal shipping = new BigDecimal(getString(R.string.recogida_tienda));
        BigDecimal tax = new BigDecimal(getString(R.string.tax));
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        
        Log.i(TAG, "Buy: subtotal = " + subtotal);
        Log.i(TAG, "Buy: shipping = " + shipping);
        Log.i(TAG, "Buy: tax = " + tax);
        Log.i(TAG, "Buy: amount = " + amount);
        
		PayPalPayment payment = new PayPalPayment(
				amount,
				getString(R.string.currency),
				nombreProducto,
				PayPalPayment.PAYMENT_INTENT_SALE);
		
        payment.items(items).paymentDetails(paymentDetails);

        //--- set other optional fields like invoice_number, custom field, and soft_descriptor
        payment.custom("This is text that will be associated with the payment that the app can use.");
		
        // send the same configuration for restart resiliency
        Intent intentPayment = new Intent(Compra_activity.this, PaymentActivity.class);
        intentPayment.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intentPayment.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        
        startActivityForResult(intentPayment, REQUEST_CODE_PAYMENT);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == REQUEST_CODE_PAYMENT) {
			if (resultCode == Activity.RESULT_OK) {
				PaymentConfirmation confirm =
						data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
				if (confirm != null) {
					try {
						Log.i(TAG, getString(R.string.FROM_PAYPAL));
						Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        
                        // Test
                        /*Toast.makeText(
                                getApplicationContext(),
                                "PaymentConfirmation info received from PayPal", Toast.LENGTH_LONG)
                                .show();*/
                        
                        // confirm with the server
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("paymentConfirmation", confirm.toJSONObject());
                        jsonObject.put("paypalPayment", confirm.getPayment().toJSONObject());
                        jsonObject.put("productoId", productoId);
                        jsonObject.put("clienteId", clienteId);
                        
                        JsonAuthObjectRequest jsonRequest = new JsonAuthObjectRequest(
                        		Method.POST,
                        		getCompleteURL(R.string.URL_CONFIRM_PAYMENT),
                        		jsonObject,
                        		jsonReqSuccessListener(),
                        		jsonReqErrorListener());
                        
                        jsonRequest.setUsername(correoCliente);
                        jsonRequest.setPassword(passwordCliente);
                        
                        Log.i(TAG, getString(R.string.TO_SERVER));
                        Log.i(TAG, jsonObject.toString(4));
                        
                        mRequestQueue.add(jsonRequest);
                        
					} catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                        ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INTERNO_COMPRA));
                        progress_compra.setVisibility(View.GONE);
                    }
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
                progress_compra.setVisibility(View.GONE);
			} else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                // Test
    			//ActivityUtils.showErrorDialog(Compra_activity.this, error.getMessage());
    			// Producción
                ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INTERNO_COMPRA));
    			progress_compra.setVisibility(View.GONE);
            }
		}
	}
	
	private ErrorListener jsonReqErrorListener() {
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				// Test
    			//ActivityUtils.showErrorDialog(Compra_activity.this, error.getMessage());
    			// Producción
				//ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INTERNO_COMPRA));
    			progress_compra.setVisibility(View.GONE);
    			
    			ActivityDialogs.showErrorDialog(Compra_activity.this, error, getString(R.string.ERROR_INTERNO_COMPRA));
    			
			}
		};
	}

	private Listener<JSONObject> jsonReqSuccessListener() {
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				
				try {
					
					String stateResponse = "";
				
					Log.i(TAG, getString(R.string.FROM_SERVER));
					Log.i(TAG, response.toString(4));
					
					stateResponse = response.getString(getString(R.string.state_response));
					
					if (stateResponse.equals(getString(R.string.state_confirm))) {
						// TODO probar si este mensaje sale bien
						ActivityDialogs.showSuccessDialogWithReturn(Compra_activity.this, getString(R.string.SUCCESS_COMPRA))
							.setOnCancelListener(new OnCancelListener() {
								
								@Override
								public void onCancel(DialogInterface dialog) {
									finish();
									
								}
							})
							.show();
					} else if (stateResponse.equals(getString(R.string.state_denay))) {
						// Test
		    			//ActivityUtils.showErrorDialog(Compra_activity.this, error.getMessage());
		    			// Producción
						ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_COMPRA_NO_CONFIRMADA));
					} else {
						// Test
		    			//ActivityUtils.showErrorDialog(Compra_activity.this, error.getMessage());
		    			// Producción
						ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INTERNO_COMPRA));
					}
					
					progress_compra.setVisibility(View.GONE);
					
				} catch (JSONException e) {
					
					// Test
	    			//ActivityUtils.showErrorDialog(Compra_activity.this, error.getMessage());
	    			// Producción
					ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INTERNO_COMPRA));
	    			progress_compra.setVisibility(View.GONE);
	    			
				}
				
				
			}
		};
				
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
					ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INTERNO));
	    			progress_compra.setVisibility(View.GONE);
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
				//ActivityDialogs.showErrorDialog(Compra_activity.this, getString(R.string.ERROR_INTERNO));
				progress_compra.setVisibility(View.GONE);
				
				ActivityDialogs.showErrorDialog(Compra_activity.this, error, getString(R.string.ERROR_INTERNO));
				
			}
		};
	}
	
	@Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
	
	
}
