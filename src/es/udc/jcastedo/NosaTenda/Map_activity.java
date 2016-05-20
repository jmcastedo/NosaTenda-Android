package es.udc.jcastedo.NosaTenda;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Map_activity extends BaseActivity implements OnMapReadyCallback {

	private Bundle extras;
	
	private GoogleMap mMap;
	
	private String nombreTienda;
	private double lat;
	private double lon;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		auxiliaryOnCreate();
		
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		
		extras = getIntent().getExtras();
		if (extras != null) {
			nombreTienda = extras.getString("nombreTienda");
			lat = extras.getDouble("lat");
			lon = extras.getDouble("lon");
		}
		
		Button btnSatellite = (Button)findViewById(R.id.btn_satellite_map);
		btnSatellite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				
				
			}
		});
		
		Button btnNormal = (Button)findViewById(R.id.btn_normal_map);
		btnNormal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				
			}
		});
	}

	@Override
	public void onMapReady(GoogleMap map) {
		
		mMap = map;
		
		LatLng position = new LatLng(lat, lon);
		
		mMap.setMyLocationEnabled(true);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
		
		mMap.addMarker(new MarkerOptions()
				.title(nombreTienda)
				.position(position));
		
	}

}
