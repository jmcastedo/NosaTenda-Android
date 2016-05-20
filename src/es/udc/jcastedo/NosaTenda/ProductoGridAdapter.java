package es.udc.jcastedo.NosaTenda;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import es.udc.jcastedo.NosaTenda.json.Producto;
import es.udc.jcastedo.NosaTenda.volley.MySingletonVolley;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProductoGridAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	private Activity mContext;
	private List<Producto> mProductos = new ArrayList<Producto>();
	private ImageLoader imageLoader;
	
	public ProductoGridAdapter(Activity context, List<Producto> productos) {
		
		mContext = context;
		mProductos = productos;
		inflater = (LayoutInflater) mContext.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return mProductos.size();
	}

	@Override
	public Object getItem(int position) {
		return mProductos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO El ejemplo mete un drawable, no sé muy bien que meter yo
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		NetworkImageView networkPicture;
		TextView name;
		TextView price;
		TextView opt_agotado;
		
		if(view==null) {
			view = inflater.inflate(R.layout.elemento_grid, parent, false);
			view.setTag(R.id.picture, view.findViewById(R.id.picture));
			view.setTag(R.id.text, view.findViewById(R.id.text));
			view.setTag(R.id.price, view.findViewById(R.id.price));
			view.setTag(R.id.opt_agotado, view.findViewById(R.id.opt_agotado));
		}
		
		networkPicture = (NetworkImageView)view.getTag(R.id.picture);
		name = (TextView)view.getTag(R.id.text);
		price = (TextView)view.getTag(R.id.price);
		opt_agotado = (TextView)view.getTag(R.id.opt_agotado);
		
		Producto producto = (Producto)getItem(position);
		
		imageLoader = MySingletonVolley.getInstance(mContext).getImageLoader();
		
		networkPicture.setDefaultImageResId(R.drawable.no_image);
		
		networkPicture.setImageUrl(producto.getImagen(), imageLoader);
		
		name.setText(producto.getNombre());
		price.setText(ActivityUtils.fmtNoZeros(producto.getPrecio()) + " €");
		
		opt_agotado.setVisibility(View.GONE);
		if (producto.getStock() == 0) {

			opt_agotado.setVisibility(View.VISIBLE);
			opt_agotado.setRotation(-45);
			opt_agotado.setText(R.string.opt_agotado);
			
		} else if (producto.getStock() <= 5) {
			
			opt_agotado.setVisibility(View.VISIBLE);
			opt_agotado.setRotation(-45);
			opt_agotado.setText(R.string.opt_ultimasunidades);
			
		}
		
		return view;
	}

	
}
