package es.udc.jcastedo.NosaTenda.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;



/**
 * Helper class that is used to provide references to initialized RequestQueue(s) and ImageLoader(s)
 * 
 * 
 */
public class MySingletonVolley {

	private static MySingletonVolley mInstance;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private static Context mCtx;
	
	
	private MySingletonVolley(Context context) {
	
		mCtx = context;
		mRequestQueue = getRequestQueue();
		
		mImageLoader = new ImageLoader(mRequestQueue,
				new ImageLoader.ImageCache() {
			
					private final LruCache<String, Bitmap>
							cache = new LruCache<String, Bitmap>(20);
					
					@Override
					public void putBitmap(String url, Bitmap bitmap) {
						cache.put(url, bitmap);
						
					}
					
					@Override
					public Bitmap getBitmap(String url) {
						return cache.get(url);
					}
				});
	}
	
	// synchronized evita que se cree más de una instancia si el método se ejecuta
	// simultáneamente en dos o más threads, puede perjudicar el rendimiento
	public static synchronized MySingletonVolley getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingletonVolley(context);
        }
        return mInstance;
    }
	
	public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }
	
	public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
	
}
