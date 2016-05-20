package es.udc.jcastedo.NosaTenda.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import es.udc.jcastedo.NosaTenda.Escaparate;

public class EscaparateTest extends ActivityInstrumentationTestCase2<Escaparate> {

	private Escaparate miEscaparate;
	private GridView miGridview;
	private ListAdapter miGridAdapter;
	
	public static final int ADAPTER_COUNT = 10;
	public static final int INITIAL_POSITION = 0;
	public static final int TEST_POSITION = 4;
	public static final int TEST_MOVES = 2;
	
	private int mPos;
	private String mSelection;
	
	public EscaparateTest() {
		super(Escaparate.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		setActivityInitialTouchMode(false);
		
		
		miEscaparate = getActivity();
		
		miGridview =
				(GridView) miEscaparate.findViewById(
					es.udc.jcastedo.NosaTenda.R.id.gridview
				);
		
		miGridAdapter = miGridview.getAdapter();
	}
	
	public void testPreConditions() {
		
		// TODO Ya hay listener, rehacer este test
		//assertTrue(miGridview.getOnItemSelectedListener() != null); //aun no tenemos listener
		assertTrue(miGridAdapter != null);
		assertEquals(miGridAdapter.getCount(), ADAPTER_COUNT);
	}
	
	public void testGridViewUI() {
		
		miEscaparate.runOnUiThread(
			new Runnable() {
				public void run() {
					miGridview.requestFocus();
					miGridview.setSelection(INITIAL_POSITION);
						
				}
			}
		);
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		for (int i = 1; i <= TEST_MOVES; i++) {
			this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		}
		
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		mPos = miGridview.getSelectedItemPosition();

		/*mSelection = (String)miGridview.getItemAtPosition(TEST_POSITION);
		
		TextView resultView =
				(TextView) miEscaparate.findViewById(
					es.udc.jcastedo.NosaTenda.R.id.text
				);
		
		
		String resultText = (String) resultView.getText();
		
		assertEquals(resultText, "Kappa");*/
		
		assertEquals(mPos, TEST_POSITION);
	}

}
