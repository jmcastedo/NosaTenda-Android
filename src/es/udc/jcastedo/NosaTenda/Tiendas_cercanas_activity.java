package es.udc.jcastedo.NosaTenda;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import es.udc.jcastedo.NosaTenda.json.Tienda;
import es.udc.jcastedo.NosaTenda.json.utils.JSONObjectConversor;

public class Tiendas_cercanas_activity extends BaseActivity
			implements ConnectionCallbacks, OnConnectionFailedListener, OnMapReadyCallback {

	protected static final String TAG = "Tiendas_cercanas_activity";
	
	/**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    
    private GoogleMap mMap;
    
    private Bundle extras;
    private double distancia;
    
    private List<Tienda> mTiendasCercanas;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_tiendas_cercanas);
    	
    	auxiliaryOnCreate();
    	
    	buildGoogleApiClient();
    	
    	mTiendasCercanas = new ArrayList<Tienda>();
    	
    	MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map_tiendas_cercanas);
		mapFragment.getMapAsync(this);
    	
    	extras = getIntent().getExtras();
    	if (extras != null) {
    		distancia = extras.getDouble("distancia");
    	}
    	
    	Button btnSatellite = (Button)findViewById(R.id.btn_satellite_map_tiendas_cercanas);
		btnSatellite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				
				
			}
		});
		
		Button btnNormal = (Button)findViewById(R.id.btn_normal_map_tiendas_cercanas);
		btnNormal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				
			}
		});
    }
    
    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        // Test
		//ActivityUtils.showErrorDialog(Tiendas_cercanas_activity.this, result.getErrorCode());
		// Producción
     	ActivityDialogs.showErrorDialog(Tiendas_cercanas_activity.this, getString(R.string.ERROR_NO_LOCATION_DETECTED));
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
        	Log.i(TAG, "lat = " + mLastLocation.getLatitude());
        	Log.i(TAG, "lon = " + mLastLocation.getLongitude());
        	
        	String uri = getCompleteURL(R.string.URL_GETTIENDAS_CERCANAS) + "?lat=" + mLastLocation.getLatitude() + "&lon=" + mLastLocation.getLongitude() + "&dist=" + distancia;
        	JsonArrayRequest jsonTiendasCercanasReq = new JsonArrayRequest(
        			Method.GET,
        			uri,
        			null,
        			getTiendasCercanasJSONReqSuccesslistener(),
        			getTiendasCercanasJSONReqErrorlistener());
        	
        	mRequestQueue.add(jsonTiendasCercanasReq);
        	
        	Log.i(TAG, getString(R.string.TO_SERVER_GET));
        } else {
        	ActivityDialogs.showErrorDialog(Tiendas_cercanas_activity.this, getString(R.string.ERROR_NO_LOCATION_DETECTED));
        }
	}

	private ErrorListener getTiendasCercanasJSONReqErrorlistener() {
		
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				// Test
				//ActivityUtils.showErrorDialog(Tiendas_cercanas_activity.this, error.getMessage());
				// Producción
				//ActivityDialogs.showErrorDialog(Tiendas_cercanas_activity.this, getString(R.string.ERROR_INTERNO));
				
				ActivityDialogs.showErrorDialog(Tiendas_cercanas_activity.this, error, getString(R.string.ERROR_INTERNO));
				
			}
		};
	}

	private Listener<JSONArray> getTiendasCercanasJSONReqSuccesslistener() {
		
		// En la respuesta de esta llamada lanzaremos el mapa con un marker por tienda
		return new Listener<JSONArray>() {

			@Override
			public void onResponse(JSONArray response) {
				
				try {
					
					Log.i(TAG, getString(R.string.FROM_SERVER));
					Log.i(TAG, response.toString(4));
					
					for (int i=0; i < response.length(); i++) {
						JSONObject jsonObject = response.getJSONObject(i);
						Tienda tienda = JSONObjectConversor.toTienda(jsonObject);
						mTiendasCercanas.add(tienda);
					}
					
					addMarkersToMap();
					
				} catch (JSONException e) {
					
					// Test
					//ActivityUtils.showErrorDialog(Tiendas_cercanas_activity.this, e.getMessage());
					// Producción
					ActivityDialogs.showErrorDialog(Tiendas_cercanas_activity.this, getString(R.string.ERROR_INTERNO));
					
				}
			}
		};
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
	}

	@Override
	public void onMapReady(GoogleMap map) {
		
		mMap = map;
		
		mMap.setMyLocationEnabled(true);
		
	}
	
	private void addMarkersToMap() {
		
		if (mLastLocation != null) {
			LatLng position = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
			
			float zoom = getZoom(distancia);
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
		}
		for (Tienda tienda: mTiendasCercanas) {
			
			LatLng positionTienda = new LatLng(tienda.getLat(), tienda.getLon());
			
			mMap.addMarker(new MarkerOptions()
			.title(tienda.getNombre())
			.position(positionTienda));
		}
		
	}
	
	private float getZoom(double dist) {
		
		if (dist < 500) {
			return 16.5f;
		} else if ((dist >= 500) && (dist < 1000)) {
			return 15;
		} else if ((dist >= 1000) && (dist < 2000)) {
			return 14;
		}else if ((dist >= 2000) && (dist < 3000)) {
			return 13;
		} else {
			return 12;
		}
	}


}
