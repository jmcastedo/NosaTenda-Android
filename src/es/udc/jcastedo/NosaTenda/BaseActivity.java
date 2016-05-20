package es.udc.jcastedo.NosaTenda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;

import es.udc.jcastedo.NosaTenda.json.Categoria;
import es.udc.jcastedo.NosaTenda.json.Localidad;
import es.udc.jcastedo.NosaTenda.json.utils.JSONObjectConversor;
import es.udc.jcastedo.NosaTenda.volley.MySingletonVolley;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BaseActivity extends Activity {

	private static final String TAG = "BaseActivity";
	
	private static final int baseURL = R.string.baseURL;
	
	protected RequestQueue mRequestQueue;
	protected DrawerLayout mDrawerLayout;
	protected ListView mDrawerList;
    protected ActionBarDrawerToggle mDrawerToggle;

    protected CharSequence mDrawerTitle;
    protected CharSequence mTitle;
    protected String[] mNavOptiones;
    
    // estas tres variables Session mantienen la session del usuario hasta que se desloguea
    protected Long idClienteSession;
    protected String correoSession;
    protected String passwordSession;
    protected SharedPreferences sharedPref;
    
    List<Integer> mSelectedItems;
    List<String> mSelectedNames;
    
    /**
     * This method configures the user session and the navigation drawer
     */
    public void auxiliaryOnCreate() {
    	
    	mTitle = getTitle();
    	
    	mDrawerTitle = getString(R.string.app_name);
    	
    	mRequestQueue = MySingletonVolley.getInstance(this).getRequestQueue();
    	
    	// comprobamos si hay sesión abierta
    	sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.idSession), Context.MODE_PRIVATE);
    	idClienteSession = sharedPref.getLong(getString(R.string.clienteId), 0);
    	// TODO movimos el login a action bar, ya no hay logged/nologged array, modificar cuando sepamos la disposicion definitiva
        if (isLogged()) {
        	correoSession = sharedPref.getString(getString(R.string.correoTag), "");
        	passwordSession = sharedPref.getString(getString(R.string.passwordTag), "");
        	mNavOptiones = getResources().getStringArray(R.array.logged_array);
        } else {
        	mNavOptiones = getResources().getStringArray(R.array.nologged_array);
        }
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavOptiones));
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {
        	@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		
        		String menuItem = mNavOptiones[position];
        		menuSelection(menuItem);
            }
		});

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    
    private void menuSelection(String selection) {
    	
    	if (selection.equals(getString(R.string.menu_escaparate))) {
			Intent i = new Intent(getApplicationContext(), EscaparateJSON.class);
			mDrawerLayout.closeDrawers();
			startActivity(i);
		} else if (selection.equals(getString(R.string.menu_reservas))) {
			Intent i = new Intent(getApplicationContext(), List_reservas_activity.class);
			mDrawerLayout.closeDrawers();
			startActivity(i);
		} else if (selection.equals(getString(R.string.menu_compras))) {
			Intent i = new Intent(getApplicationContext(), List_compras_activity.class);
			mDrawerLayout.closeDrawers();
			startActivity(i);
		} else if (selection.equals(getString(R.string.menu_fProductosCiudad))) {
			mDrawerLayout.closeDrawers();
			JsonArrayRequest jsonLocalidadesReq = new JsonArrayRequest(
					Method.GET,
					getCompleteURL(R.string.URL_GETLOCALIDADES),
					null,
					getLocalidadesJSONReqSuccessListener(),
					getLocalidadesJSONReqErrorListener());
			
			mRequestQueue.add(jsonLocalidadesReq);
		} else if (selection.equals(getString(R.string.menu_fProductosCategoria))) {
			mDrawerLayout.closeDrawers();
			JsonArrayRequest jsonCategoriasReq = new JsonArrayRequest(
					Method.GET,
					getCompleteURL(R.string.URL_GETCATEGORIAS),
					null,
					getCategoriasJSONReqSuccessListener(),
					getCategoriasJSONReqErrorListener());
			
			mRequestQueue.add(jsonCategoriasReq);
		} else if (selection.equals(getString(R.string.menu_fProductosCategoriaTiendas))) {
			mDrawerLayout.closeDrawers();
			JsonArrayRequest jsonCategoriasReq = new JsonArrayRequest(
					Method.GET,
					getCompleteURL(R.string.URL_GETCATEGORIAS),
					null,
					getCategoriasTiendaJSONReqSuccessListener(),
					getCategoriasTiendaJSONReqErrorListener());
			
			mRequestQueue.add(jsonCategoriasReq);
		} else if (selection.equals(getString(R.string.menu_fProductosTiendasFavoritas))) {
			Intent i = new Intent(getApplicationContext(), EscaparateJSON.class);
			i.putExtra("fav", true);
			mDrawerLayout.closeDrawers();
			startActivity(i);
		} else if (selection.equals(getString(R.string.menu_tiendasCercanas))) {
			mDrawerLayout.closeDrawers();
			
			AlertDialog.Builder b = new AlertDialog.Builder(BaseActivity.this);
			b.setItems(R.array.distance_array, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					String choice = getResources().getStringArray(R.array.distance_array)[which];
					
					Intent i = new Intent(getApplicationContext(), Tiendas_cercanas_activity.class);
					
					if (choice.equals(getString(R.string.distance_200))) {
						i.putExtra("distancia", 200.0);
					} else if (choice.equals(getString(R.string.distance_500))) {
						i.putExtra("distancia", 500.0);
					} else if (choice.equals(getString(R.string.distance_1000))) {
						i.putExtra("distancia", 1000.0);
					} else if (choice.equals(getString(R.string.distance_2000))) {
						i.putExtra("distancia", 2000.0);
					} else if (choice.equals(getString(R.string.distance_5000))) {
						i.putExtra("distancia", 5000.0);
					}
					dialog.dismiss();
					startActivity(i);
					
				}
			})
			.setTitle(getString(R.string.distance_title))
			.show();
		} else if (selection.equals(getString(R.string.menu_tiendasFavoritas))) {
			Intent i = new Intent(getApplicationContext(), List_favTiendas_activity.class);
			mDrawerLayout.closeDrawers();
			startActivity(i);
		}
    }
    
    protected ErrorListener getCategoriasTiendaJSONReqErrorListener() {

    	return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				ActivityDialogs.showErrorDialog(BaseActivity.this, error, getString(R.string.ERROR_INTERNO));
				
			}
		};
	}

	protected Listener<JSONArray> getCategoriasTiendaJSONReqSuccessListener() {
		
		return new Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				
				try {
					
					List<Categoria> mCategorias = new ArrayList<Categoria>();
					List<String> mNombresCategorias = new ArrayList<String>();
					
					for(int i = 0; i < response.length(); i++) {
						JSONObject jsonObject = response.getJSONObject(i);
						Categoria categoria = JSONObjectConversor.toCategoria(jsonObject);
						
						mCategorias.add(categoria);
						mNombresCategorias.add(categoria.getNombre());
					}
					
					AlertDialog.Builder b = new AlertDialog.Builder(BaseActivity.this);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getApplicationContext(), R.layout.elemento_lista_localidades, mNombresCategorias);
					
					final List<Categoria> mCategoriasAux = mCategorias;
					final List<String> mNombresCategoriasAux = mNombresCategorias;
					
					b.setSingleChoiceItems(adapter, -1, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							Intent i = new Intent(getApplicationContext(), EscaparateJSON.class);
							Long categoriaId = Long.valueOf(-1);
							
							for(Categoria categoria: mCategoriasAux) {
								if (mNombresCategoriasAux.get(which).equals(categoria.getNombre()))
									categoriaId = categoria.getId();
							}
							i.putExtra("categoriaTiendaId", categoriaId);
							dialog.dismiss();
							startActivity(i);
						}
					})
					.setTitle(getString(R.string.dialog_categoria_tienda))
					.show();
					
				} catch (JSONException e) {
					
					ActivityDialogs.showErrorDialog(BaseActivity.this, getString(R.string.ERROR_INTERNO));
					
				}
				
			}
		};
	}



	private ErrorListener getCategoriasJSONReqErrorListener() {
    	
    	return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				ActivityDialogs.showErrorDialog(BaseActivity.this, error, getString(R.string.ERROR_INTERNO));
				
			}
		};
	}



	private Listener<JSONArray> getCategoriasJSONReqSuccessListener() {
		
		return new Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				
				try {
					
					List<Categoria> mCategorias = new ArrayList<Categoria>();
					List<String> mNombresCategorias = new ArrayList<String>();
					
					for(int i = 0; i < response.length(); i++) {
						JSONObject jsonObject = response.getJSONObject(i);
						Categoria categoria = JSONObjectConversor.toCategoria(jsonObject);
						mCategorias.add(categoria);
						
						mNombresCategorias.add(categoria.getNombre());
					}
					
					final List<Categoria> mCategoriasAux = mCategorias;
					final List<String> mNombresCategoriasAux = mNombresCategorias;
					Collections.sort(mNombresCategoriasAux);
					final CharSequence[] nombres = mNombresCategoriasAux.toArray(new CharSequence[mNombresCategoriasAux.size()]);
					mSelectedItems = new ArrayList<Integer>();
					mSelectedNames = new ArrayList<String>();
					
					
					AlertDialog.Builder b = new AlertDialog.Builder(BaseActivity.this);
					b.setMultiChoiceItems(nombres, null, new OnMultiChoiceClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {

							if (isChecked) {
								mSelectedItems.add(which);
								mSelectedNames.add(nombres[which].toString());
								Log.i(TAG, "selected: " + nombres[which].toString());
							} else if (mSelectedItems.contains(which)) {
								mSelectedItems.remove(Integer.valueOf(which));
								mSelectedNames.remove(nombres[which].toString());
								Log.i(TAG, "deselected: " + nombres[which].toString());
							}
							
						}
					})
					.setPositiveButton(R.string.dialog_positive, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							Intent i = new Intent(getApplicationContext(), EscaparateJSON.class);
							
							int contador = 0;
							for (String categoriaNombre: mSelectedNames) {
								for (Categoria categoria: mCategoriasAux) {
									if (categoria.getNombre().equals(categoriaNombre)) {
										contador = contador + 1;
										i.putExtra("categoriaId" + contador, categoria.getId());
										Log.i(TAG, "categoria" + contador + " = " + categoriaNombre);
									}
										
								}
								
							}
							i.putExtra("categorias", contador);
							Log.i(TAG, "contador = " + contador);
							dialog.dismiss();
							startActivity(i);
							
						}
					})
					.setNegativeButton(R.string.dialog_negative, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							dialog.dismiss();
							
						}
					})
					.setTitle(getString(R.string.dialog_categoria))
					.show();
					
				} catch (JSONException e) {
					
					ActivityDialogs.showErrorDialog(BaseActivity.this, getString(R.string.ERROR_INTERNO));
					
				}
			}
		};
	}

	private ErrorListener getLocalidadesJSONReqErrorListener() {
		
    	return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
				ActivityDialogs.showErrorDialog(BaseActivity.this, error, getString(R.string.ERROR_INTERNO));
				
			}
		};
	}

	private Listener<JSONArray> getLocalidadesJSONReqSuccessListener() {
		
		return new Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {

				try {
					
					List<Localidad> mLocalidades = new ArrayList<Localidad>();
					List<String> mNombresLocalidades = new ArrayList<String>();
					
					for(int i = 0; i < response.length(); i++) {
						JSONObject jsonObject = response.getJSONObject(i);
						Localidad localidad = JSONObjectConversor.toLocalidad(jsonObject);
						mLocalidades.add(localidad);
						
						mNombresLocalidades.add(localidad.getNombre());
					}
					
					AlertDialog.Builder b = new AlertDialog.Builder(BaseActivity.this);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getApplicationContext(), R.layout.elemento_lista_localidades, mNombresLocalidades);
					
					final List<Localidad> mLocalidadesAux = mLocalidades;
					final List<String> mNombresLocalidadesAux = mNombresLocalidades;
					
					b.setSingleChoiceItems(adapter, -1, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							Intent i = new Intent(getApplicationContext(), EscaparateJSON.class);
							Long localidadId = Long.valueOf(-1);
							for(Localidad localidad: mLocalidadesAux) {
								if (mNombresLocalidadesAux.get(which).equals(localidad.getNombre()))
										localidadId = localidad.getId();
							}
							i.putExtra("localidadId", localidadId);
							dialog.dismiss();
							startActivity(i);
							
						}
					})
					.setTitle(getString(R.string.dialog_ciudad))
					.show();
					
				} catch (JSONException e) {
					
					ActivityDialogs.showErrorDialog(BaseActivity.this, getString(R.string.ERROR_INTERNO));
					
				}
			}
		};
	}

	// añade botones a la derecha de la action bar o en el menu overflow
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        
        if (isLogged()) {
        	inflater.inflate(R.menu.main_logged, menu);
        } else {
        	inflater.inflate(R.menu.main_unlogged, menu);
        }
        //inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    /* Called whenever we call invalidateOptionsMenu() */
    // TODO replace websearch with menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    
    // TODO replace websearch with menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
//        case R.id.action_websearch:
//            // create intent to perform web search for this planet
//            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
//            // catch event that there's no activity to handle intent
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(intent);
//            } else {
//                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//            }
//            return true;
        case R.id.action_account:
        	return true;
        case R.id.action_logout:
        	// en principio mandamos al usuario a la Activity de inicio de la App, deslogueado
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.remove(getString(R.string.clienteId));
			editor.remove(getString(R.string.correoTag));
			editor.remove(getString(R.string.passwordTag));
			editor.commit();
			Intent logout_intent = new Intent(getApplicationContext(), EscaparateJSON.class);
			startActivity(logout_intent);
        	return true;
        case R.id.action_create_account:
        	return true;
        case R.id.action_login:
        	Intent login_intent = new Intent(getApplicationContext(), Login_activity.class);
        	startActivity(login_intent);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
    
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    public String getCompleteURL(int resId) {
    	
    	return getString(baseURL) + getString(resId);
    }
    
    public Boolean isLogged() {
    	// si idCliente no es 0 significa que el usuario está logueado
    	if (idClienteSession != 0) {
        	return true;
        } else {
        	return false;
        }
    }
}
