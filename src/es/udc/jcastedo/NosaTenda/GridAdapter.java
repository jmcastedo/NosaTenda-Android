package es.udc.jcastedo.NosaTenda;

import java.util.ArrayList;
import java.util.List;

import es.udc.jcastedo.NosaTenda.xml.Entry;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {
	
    private List<Item> items = new ArrayList<Item>();
    private LayoutInflater inflater = null;
    private List<Entry> entries;
    private Activity mContext; 

    public GridAdapter(Context context) {
        inflater = LayoutInflater.from(context);

        items.add(new Item("La avecilla",       R.drawable.avecilla));
        items.add(new Item("La canción roleando",   R.drawable.cancion_rolando));
        items.add(new Item("La Divina Comedia", R.drawable.divinacomedia));
        items.add(new Item("La Iliada",      R.drawable.iliada));
        items.add(new Item("Kappa",     R.drawable.kappa));
        items.add(new Item("Perrito",      R.drawable.sample_0));
        items.add(new Item("Chucho",      R.drawable.sample_1));
        items.add(new Item("Pulgas",      R.drawable.sample_2));
        items.add(new Item("Canciño",      R.drawable.sample_3));
        items.add(new Item("Bulldozers",      R.drawable.sample_4));
    }

    public GridAdapter(Activity context, List<Entry> mEntries) {
    	mContext = context;
    	entries = mEntries;
    	inflater = (LayoutInflater) mContext.getSystemService(
    			Context.LAYOUT_INFLATER_SERVICE);
    			
    			//LayoutInflater.from(context);
    	
    	if (entries == null) {
    		Log.i("GridAdapter", "GridAdapter new constructor: mEntries null");
    	}
    	
    	items.add(new Item("La avecilla",       R.drawable.avecilla));
        items.add(new Item("La canción roleando",   R.drawable.cancion_rolando));
        items.add(new Item("La Divina Comedia", R.drawable.divinacomedia));
        items.add(new Item("La Iliada",      R.drawable.iliada));
        items.add(new Item("Kappa",     R.drawable.kappa));
        items.add(new Item("Perrito",      R.drawable.sample_0));
        items.add(new Item("Chucho",      R.drawable.sample_1));
        items.add(new Item("Pulgas",      R.drawable.sample_2));
        items.add(new Item("Canciño",      R.drawable.sample_3));
        items.add(new Item("Bulldozers",      R.drawable.sample_4));
    }
    
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
    	Log.i("GridAdapter", "Entra en getItemId");
        return items.get(i).drawableId;
        
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;

        if(v == null) {
            v = inflater.inflate(R.layout.elemento_grid, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView)v.getTag(R.id.picture);
        name = (TextView)v.getTag(R.id.text);

        Item item = (Item)getItem(i);

        picture.setImageResource(item.drawableId);
        name.setText(item.name);
        
        if (entries == null) {
        	Log.i("GridAdapter", "GridAdapter getView: entries null");
        } else {
        	Log.i("GridAdapter", "GridAdapter getView: entries not null, success!!"+entries.size());
        	//if (i == 1) { name.setText(entries.get(1).getNombre()); }
        	if (entries.size() == 2) { Log.i("GridAdapter", "entrie 1: "+entries.get(0).getNombre()); }
        }

        return v;
    }

}