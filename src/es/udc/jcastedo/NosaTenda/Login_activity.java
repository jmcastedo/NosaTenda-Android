package es.udc.jcastedo.NosaTenda;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class Login_activity extends BaseActivity {
	
	private static final String TAG = "Login_activity";

	private ProgressBar progress_login;
	
	private String correo_parametro;
	private String password_parametro;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		auxiliaryOnCreate();
		
		Button btnConfirmarLogin = (Button)findViewById(R.id.btn_confirmar_login);
		btnConfirmarLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				progress_login = (ProgressBar)findViewById(R.id.loading_login);
				progress_login.setVisibility(View.VISIBLE);
				
				EditText correo = (EditText)findViewById(R.id.editText_correo_login);
				EditText password = (EditText)findViewById(R.id.editText_password_login);
				
				correo_parametro = correo.getEditableText().toString();
				password_parametro = password.getEditableText().toString();
				
				// checkeo del campo correo
				if (!(ActivityUtils.checkTextFieldNotEmpty(correo_parametro))) {
					ActivityDialogs.showErrorDialog(Login_activity.this, getString(R.string.ERROR_CORREO));
					progress_login.setVisibility(View.GONE);
					return;
				}
				
				// checkeo del campo contraseña
				if (!(ActivityUtils.checkTextFieldNotEmpty(password_parametro))) {
					ActivityDialogs.showErrorDialog(Login_activity.this, getString(R.string.ERROR_PASSWORD));
					progress_login.setVisibility(View.GONE);
					return;
				}
				if (!(ActivityUtils.checkPasswordField(password_parametro, getResources().getInteger(R.integer.MAX_CHARACTERS)))) {
					ActivityDialogs.showErrorDialog(Login_activity.this, String.format(getString(R.string.ERROR_PASSWORD_TOO_LONG), getResources().getInteger(R.integer.MAX_CHARACTERS)));
					progress_login.setVisibility(View.GONE);
					return;
				}
				
				// Petición de login al servicio
				JSONObject jsonLoginBody = new JSONObject();
				try {
					
					jsonLoginBody.put("correo", correo_parametro);
					jsonLoginBody.put("password", password_parametro);
					
					String uri = getCompleteURL(R.string.URL_LOGIN) + "?correo=" + correo_parametro + "&password=" + password_parametro;
					
					JsonObjectRequest jsonLoginRequest = new JsonObjectRequest(
							Method.GET,
							uri,
							null,
							createLoginJSONReqSuccessListener(),
							createLoginJSONReqErrorListener());
					
					mRequestQueue.add(jsonLoginRequest);
					
					Log.i(TAG, getString(R.string.TO_SERVER_GET));
					
				} catch (JSONException e) {
					
					// Test
					//ActivityUtils.showErrorDialog(Reserva_activity.this, e.getMessage());
					// Producción
					ActivityDialogs.showErrorDialog(Login_activity.this, getString(R.string.ERROR_INTERNO));
					progress_login.setVisibility(View.GONE);
					
				}
				
			}

		});
		
	}
	
	private Listener<JSONObject> createLoginJSONReqSuccessListener() {
		return new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				
				try {
					
					Long clienteIdResponse = response.getLong(getString(R.string.clienteId));
					
					// asignamos el id de la respuesta a la aplicación
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putLong(getString(R.string.clienteId), clienteIdResponse);
					editor.putString(getString(R.string.correoTag), correo_parametro);
					editor.putString(getString(R.string.passwordTag), password_parametro);
					editor.commit();
					
					ActivityDialogs.showSuccessDialogWithReturn(Login_activity.this, "")
						.setOnCancelListener(new OnCancelListener() {
							
							@Override
							public void onCancel(DialogInterface dialog) {
								Intent i = new Intent(getApplicationContext(), EscaparateJSON.class);
								startActivity(i);
							}
						})
						.show();
					
					progress_login.setVisibility(View.GONE);
					
				} catch (JSONException e) {
					
					// Test
					//ActivityUtils.showErrorDialog(Reserva_activity.this, error.getMessage());
					// Producción
					ActivityDialogs.showErrorDialog(Login_activity.this, getString(R.string.ERROR_INTERNO_LOGIN));
					progress_login.setVisibility(View.GONE);
					
				}
				
			}
		};
	}

	private ErrorListener createLoginJSONReqErrorListener() {
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				// Test
				//ActivityUtils.showErrorDialog(Reserva_activity.this, error.getMessage());
				// Producción
				//ActivityDialogs.showErrorDialog(Login_activity.this, getString(R.string.ERROR_INTERNO_LOGIN));
				progress_login.setVisibility(View.GONE);
				
				ActivityDialogs.showErrorDialog(Login_activity.this, error, getString(R.string.ERROR_INTERNO_LOGIN));
				
			}
		};
	}
}
