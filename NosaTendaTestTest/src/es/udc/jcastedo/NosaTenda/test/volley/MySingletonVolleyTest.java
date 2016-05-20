package es.udc.jcastedo.NosaTenda.test.volley;

import es.udc.jcastedo.NosaTenda.volley.MySingletonVolley;
import junit.framework.Assert;
import junit.framework.TestCase;


public class MySingletonVolleyTest extends TestCase {
	
	private MySingletonVolley sone = null, stwo = null;
	
	public MySingletonVolleyTest(String name) {
		
		super(name);
	}
	
	public void setUp() {
		// TODO el contexto que se pasa es null, hay que encontrar la forma de acceder
		// al contexto de la aplicación
		sone = MySingletonVolley.getInstance(null);
		// TODO el contexto que se pasa es null, hay que encontrar la forma de acceder
				// al contexto de la aplicación
		stwo = MySingletonVolley.getInstance(null);
	}
	
	/**
	 * Comprueba que efectivamente {@link MySingletonVolley} es un singleton y sólo se crea
	 * una instancia.
	 *
	 */
	public void testUnique() {
		
		Assert.assertEquals(true, sone == stwo);
	}
	
}
