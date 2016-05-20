package es.udc.jcastedo.NosaTenda;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import es.udc.jcastedo.NosaTenda.json.Producto;
import es.udc.jcastedo.NosaTenda.volley.MySingletonVolley;

public class ProductoStickyGridAdapter extends BaseAdapter implements
		StickyGridHeadersSimpleAdapter {

	private LayoutInflater inflater = null;
	private Activity mContext;
	private List<Producto> mProductos = new ArrayList<Producto>();
	private ImageLoader imageLoader;
	
	boolean isScrollStop = true;
	private GridView mGridView;
	
	public ProductoStickyGridAdapter(Activity context,
			List<Producto> productos, GridView gridView) {
		
		mContext = context;
		mProductos = productos;
		inflater = (LayoutInflater) mContext.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mGridView = gridView;
		
		mGridView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.v("onScrollStateChanged", "onScrollStateChanged");
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					isScrollStop = true;
					notifyDataSetChanged();
				} else {
					isScrollStop = false;
				}
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				Log.v("onScroll", "onScroll");
				
			}
		});
	}
	
	public ProductoStickyGridAdapter(Activity context,
			List<Producto> productos) {
		
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
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		NetworkImageView networkPicture;
		TextView name;
		TextView price;
		TextView opt_agotado;
		
		if (view == null) {
			
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
	
	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		TextView header;
		
		if (view == null) {
			
			view = inflater.inflate(R.layout.header_grid, parent, false);
			view.setTag(R.id.header, view.findViewById(R.id.header));
			
		}
		
		header = (TextView)view.getTag(R.id.header);
		
		header.setText("Hasta el " + ActivityUtils.dateToString(mProductos.get(position).getFecha_retirada(), "dd-MM-yyyy"));
		
		return view;
		
	}
	
	@Override
	public long getHeaderId(int position) {
		return mProductos.get(position).getSection();
	}
}
