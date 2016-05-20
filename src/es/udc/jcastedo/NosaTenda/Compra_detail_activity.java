package es.udc.jcastedo.NosaTenda;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import es.udc.jcastedo.NosaTenda.json.Tienda;
import es.udc.jcastedo.NosaTenda.volley.MySingletonVolley;

public class Compra_detail_activity extends BaseActivity {

	private ImageLoader imageLoader;
	
	private String url_imagen;
	
	private Bundle extras;
	
	private Tienda tienda;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compra_detail);
		
		auxiliaryOnCreate();
		
		NetworkImageView imagen_producto = (NetworkImageView)findViewById(R.id.imageView_detail_compra);
		
		TextView nombre_producto = (TextView)findViewById(R.id.textView_detail_nombre_producto_compra);
		TextView fecha_compra = (TextView)findViewById(R.id.textView_detail_fecha_compra_data);
		TextView n_unidades = (TextView)findViewById(R.id.textView_detail_unidades_compra_data);
		TextView p_unidad = (TextView)findViewById(R.id.textView_detail_punidad_compra_data);
		TextView p_total = (TextView)findViewById(R.id.textView_detail_ptotal_compra_data);
		TextView metodo = (TextView)findViewById(R.id.textView_detail_metodo_compra_data);
		TextView nombre_tienda = (TextView)findViewById(R.id.textView_detail_nombre_tienda_compra_data);
		TextView estado = (TextView)findViewById(R.id.textView_detail_nombre_estado_compra_data);
		
		extras = getIntent().getExtras();
		if (extras != null) {
			url_imagen = (String)extras.getCharSequence("url_imagen");
			
			imageLoader = MySingletonVolley.getInstance(getApplicationContext()).getImageLoader();
			imagen_producto.setDefaultImageResId(R.drawable.no_image);
			imagen_producto.setImageUrl(url_imagen, imageLoader);
			
			nombre_producto.setText(extras.getCharSequence("nombre"));
			Date date = (Date)extras.get("fecha");
			SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy");
			fecha_compra.setText(date_format.format(date));
			Long unidades = extras.getLong("unidades");
			n_unidades.setText(Long.toString(unidades));
			Double subtotal = extras.getDouble("subtotal");
			p_unidad.setText(ActivityUtils.fmtNoZeros(subtotal) + " €");
			Double total = extras.getDouble("total");
			p_total.setText(ActivityUtils.fmtNoZeros(total) + " €");
			metodo.setText(extras.getCharSequence("formaPago"));
			nombre_tienda.setText(extras.getCharSequence("nombreTienda"));
			estado.setText(extras.getCharSequence("estadoRecogida"));
			
			tienda = new Tienda();
			tienda.setId(extras.getLong("idTienda"));
			tienda.setNombre(extras.getString("nombreTienda"));
			tienda.setDireccion(extras.getString("direccionTienda"));
			tienda.setLocalidad(extras.getString("localidadTienda"));
			tienda.setCorreo(extras.getString("correoTienda"));
			tienda.setPhone1(extras.getString("phoneTienda"));
			tienda.setWeb(extras.getString("webTienda"));
			tienda.setUrl_imagen(extras.getString("url_imagen_tienda"));
			tienda.setLat(extras.getDouble("latTienda"));
			tienda.setLon(extras.getDouble("lonTienda"));
			
		}
		
		final Tienda tiendaAux = tienda;
		
		nombre_tienda.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				callTiendaDetail(tiendaAux);
				
			}
		});
		
		TextView tienda = (TextView)findViewById(R.id.textView_detail_tienda_compra);
		tienda.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				callTiendaDetail(tiendaAux);
				
			}
		});
	}
	
	private void callTiendaDetail(Tienda tienda) {
		
		Intent i = new Intent(getApplicationContext(), Tienda_detail_activity.class);
		i.putExtra("idTienda", tienda.getId());
		i.putExtra("nombreTienda", tienda.getNombre());
		i.putExtra("direccionTienda", tienda.getDireccion() +
							" " + tienda.getLocalidad());
		i.putExtra("phoneTienda", tienda.getPhone1());
		i.putExtra("correoTienda", tienda.getCorreo());
		i.putExtra("webTienda", tienda.getWeb());
		i.putExtra("url_imagen_tienda", tienda.getUrl_imagen());
		i.putExtra("latTienda", tienda.getLat());
		i.putExtra("lonTienda", tienda.getLon());
		
		startActivity(i);
	}
}
