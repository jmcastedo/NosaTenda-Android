package es.udc.jcastedo.NosaTenda;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityUtils {

	public ActivityUtils() {}
	
	/**
	 * Método auxiliar para comprobar campos numéricos, si el valor no es
	 * nulo y está entre los límites min y max indicados, devuelve true.
	 * En otro caso devuelve false.
	 * @param unidades Valor en String de la unidad a comparar
	 * @param min Valor mínimo que puede adoptar el valor, inclusive
	 * @param max Valor máximo que puede adoptar el valor, inclusive
	 * @return Si el valor existe y está entre min y max, devuelve true. En otro caso false.
	 */
	public static Boolean checkUnidades(String unidades, int min, int max) {
		
		if (unidades.isEmpty() || Integer.parseInt(unidades)<min || Integer.parseInt(unidades)>max) {
			return false;
		} else {
			return true;
		}	
		
	}
	
	/**
	 * Método auxiliar para comprobar que un campo de texto no está vacío
	 * @param text String con el texto introducido en el campo
	 * @return false si no se ha introducido texto, true en otro caso
	 */
	public static Boolean checkTextFieldNotEmpty(String text) {
		if (text.trim().isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Método auxiliar para comprobar que la contraseña introducida no excede
	 * la longitud máxima admitida
	 * @param password String con la contraseña introducida en el campo
	 * @param max Máximo número de caracteres que puede tener una contraseña
	 * @return false si la contraseña es demasiado larga, true en otro caso
	 */
	public static Boolean checkPasswordField(String password, int max) {
		
		if (password.length() > max) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Método auxiliar para comprobar que comprobar que dos strings introducidos
	 * son idénticos, por ejemplo al confirmar una contraseña o un correo.
	 * @param string1 Primer string a comparar
	 * @param string2 Segundo string a comparar
	 */
	public static Boolean checkStringsEguals(String string1, String string2) {
		
		return string1.equals(string2);
	}
	
	/**
	 * Método auxiliar para formatear un número de manera que siempre tenga dos
	 * números decimales exactamente, como exige el API de PayPal
	 * @param d Double a formatear
	 */
	public static String fmtTwoZeros(Double d) {

		DecimalFormat myFormatter = new DecimalFormat("0.00");
		
		return myFormatter.format(d);
	}
	
	/**
	 * Método auxiliar para formatear un número de manera que no aparezcan
	 * ceros innecesarios por la derecha en los decimales
	 * @param d Double a formatear
	 */
	@SuppressLint("DefaultLocale")
	public static String fmtNoZeros(Double d) {
		
		double dd = d.doubleValue();
		
		if (dd == (long) dd)
			return String.format("%d", (long) dd);
		else
			return String.format("%s", dd);
	}
	
	/**
	 * Método auxiliar para pasar una fecha en formato Date a un String
	 * con el formato de fecha que queramos
	 * @param date Fecha que queremos pasar a String
	 * @param pattern Patrón para formatear la fecha, por ejemplo dd-MM-yyyy
	 * @return Fecha en String con el formato de fecha indicado
	 */
	@SuppressLint("SimpleDateFormat")
	public static String dateToString(Date date, String pattern) {
		
		SimpleDateFormat date_format = new SimpleDateFormat(pattern);
		
		String dateString = new String(date_format.format(date));
		
		return dateString;
	}
	
}
