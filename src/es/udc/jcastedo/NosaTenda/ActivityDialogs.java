package es.udc.jcastedo.NosaTenda;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;

import android.app.Activity;
import android.app.AlertDialog;

public class ActivityDialogs {

	public ActivityDialogs() {}
	
	/**
	 * Auxiliary method to show an error dialog with a custom error message
	 * @param activity Activity where the error has generated and where the
	 * error dialog while be shown
	 * @param error Custom error String, that will be append to the generic
	 * error message
	 */
	public static void showErrorDialog(Activity activity, String error) {
		
		AlertDialog.Builder b = new AlertDialog.Builder(activity);
		b.setMessage(activity.getString(R.string.ERROR_OCCURRED) + " " + error);
		b.show();
	}
	
	/**
	 * Auxiliary method to show an error dialog with a custom error message, though it can be
	 * overridden depending on the content of the VolleyError.
	 * @param activity Activity where the error has generated and where the
	 * error dialog while be shown
	 * @param error VolleyError with the content of the response of the server (or lack of it)
	 * which will be processed and depending on the specific error, may override the errorMessage
	 * specified
	 * @param errorMessage Custom error String, that will be append to the generic
	 * error message
	 */
	public static void showErrorDialog(Activity activity, VolleyError error, String errorMessage) {
		
		AlertDialog.Builder b = new AlertDialog.Builder(activity);
		
		String json = null;
		NetworkResponse response = error.networkResponse;
		
		if (response != null && response.data != null) {
			
			json = new String(response.data);
			json = trimMessage(json, "type");
			if (json==null) {
				b.setMessage(activity.getString(R.string.ERROR_OCCURRED) + " " + activity.getString(R.string.ERROR_INTERNO));
			} else {
			
				switch (response.statusCode) {
				case 500:
					b.setMessage(activity.getString(R.string.ERROR_OCCURRED) + " " + errorMessage);
					if (json.equals("InsufficientStockException")) {
						b.setMessage(activity.getString(R.string.ERROR_OCCURRED) + " " + activity.getString(R.string.ERROR_INSUFFICIENT_STOCK_NOW));
					}
					break;
				case 400:
					b.setMessage(activity.getString(R.string.ERROR_OCCURRED) + " " + activity.getString(R.string.ERROR_INTERNO));
					if (json.equals("IncorrectPasswordException")) {
						b.setMessage(activity.getString(R.string.ERROR_OCCURRED) + " " + activity.getString(R.string.ERROR_PASSWORD_WRONG));
					}
					if (json.equals("DuplicateInstanceException")) {
						b.setMessage(activity.getString(R.string.ERROR_OCCURRED) + " " + activity.getString(R.string.ERROR_CORREO_DUPLICATE));
					}
					break;
	
				default:
					b.setMessage(activity.getString(R.string.ERROR_OCCURRED) + " " + activity.getString(R.string.ERROR_INTERNO));
					break;
				}
			}
		} else {
			if (error instanceof NoConnectionError) {
				b.setMessage(activity.getString(R.string.ERROR_OCCURRED) + " " + activity.getString(R.string.ERROR_CONNECTION_FAILED));
			}
		}
		
		b.show();
	}

	public static void showSuccessDialog(Activity activity) {
		
		AlertDialog.Builder b = new AlertDialog.Builder(activity);
		b.setMessage(activity.getString(R.string.SUCCESS));
		b.show();
	}
	
	public static void showSuccessDialog(Activity activity, String message) {
		
		AlertDialog.Builder b = new AlertDialog.Builder(activity);
		b.setMessage(activity.getString(R.string.SUCCESS) + " " + message);
		b.show();
	}
	
	public static AlertDialog.Builder showSuccessDialogWithReturn(Activity activity, String message) {
		
		AlertDialog.Builder b = new AlertDialog.Builder(activity);
		b.setMessage(activity.getString(R.string.SUCCESS) + " " + message);
		
		return b;
	}
	
	public static String trimMessage(String json, String key){
	    String trimmedString = null;

	    try{
	    	
	        JSONObject obj = new JSONObject(json);
	        trimmedString = obj.getString(key);
	        
	    } catch(JSONException e){
			
	        e.printStackTrace();
	        return null;
	        
	    }

	    return trimmedString;
	}

}
