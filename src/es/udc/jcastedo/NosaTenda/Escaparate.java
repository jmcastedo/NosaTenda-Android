package es.udc.jcastedo.NosaTenda;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;

import es.udc.jcastedo.NosaTenda.volley.MySingletonVolley;
import es.udc.jcastedo.NosaTenda.xml.Entry;
import es.udc.jcastedo.NosaTenda.xml.Feed;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
//DEPRECATED CLASS!! USE EscaparteJSON!!
@Deprecated
public class Escaparate extends Activity {
	
	private List<Entry> mEntries;
	private MySingletonVolley mySingletonVolley;
	private RequestQueue mRequestQueue;
	private GridAdapter mGAdapter;
	// DEPRECATED CLASS!! USE EscaparteJSON!!
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_escaparate);
		
		mySingletonVolley = MySingletonVolley.getInstance(this);
		mRequestQueue = mySingletonVolley.getRequestQueue();
		
		SimpleXmlRequest<Feed> simpleRequest = new SimpleXmlRequest<Feed>(
				Request.Method.GET, getString(R.string.URL_GETPRODUCTOS), Feed.class,
				createMyReqSuccessListener(),
				createMyReqErrorListener());
		
		mRequestQueue.add(simpleRequest);
		
		GridView gridView = (GridView)findViewById(R.id.gridview);
		mEntries = new ArrayList<Entry>();
		mGAdapter = new GridAdapter(this,mEntries);
	    gridView.setAdapter(mGAdapter);
	    
	    // TODO crear una clase EndlessScrollListener como en ejemplo de Bankov sobre
	    // NetworkListView para ampliar la lista a medida que se hace scroll
	    //gridView.setOnScrollListener(null);
	    
	    gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(Escaparate.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
	// DEPRECATED CLASS!! USE EscaparteJSON!!
	private ErrorListener createMyReqErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				showErrorDialog();
			}
		};
	}
	// DEPRECATED CLASS!! USE EscaparteJSON!!
	private void showErrorDialog() {
		
		AlertDialog.Builder b = new AlertDialog.Builder(Escaparate.this);
		b.setMessage(getString(R.string.ERROR));
		b.show();
	}
	// DEPRECATED CLASS!! USE EscaparteJSON!!
	private Listener<Feed> createMyReqSuccessListener() {
		
		return new Response.Listener<Feed>() {
			@Override
			public void onResponse(Feed response) {
				
				//mEntries = response.getList();
				
				for (Entry e : response.getList()) {
					mEntries.add(e);
				}
				
				mGAdapter.notifyDataSetChanged();
				
				if (mEntries != null) {
					Log.i("Escaparate", "Escaparate onResponse: mEntries not null"+mEntries.size());
				} else {
					Log.i("Escaparate", "Escaparate onResponse: mEntries null");
				}
				
			}
		};
	}
	// DEPRECATED CLASS!! USE EscaparteJSON!!
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.escaparate, menu);
		return true;
	}
	
	
}
