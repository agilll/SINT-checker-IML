
/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Prácticas de SINT  (paquete común a todas las prácticas)
 *
 *    Autor: Alberto Gil Solla
 ****************************************************************/

// Objeto para almacenar la información sobre un fallo al corregir una práctica. Almacena
// el URL que provocó el fallo
// un código que representa el tipo de error
// una lista de frases que van describiendo la traza del error, añadidas por todas las funciones por las que pasa el error, desde la que la genera hasta la que la presenta

package docencia.sint.Common;

import java.util.ArrayList;
import java.util.Collections;

public class CheckerFailure
{
	private String url;
	private String codigo;
	private ArrayList<String> traza;


	// el constructor espera recibir el URL que provocó el error, el código de clasificación, y el motivo (una frase explicativa) para añadir a la lista
	public CheckerFailure (String u, String c, String motivo)
	{
        url = u;
        codigo = c;
        traza = new ArrayList<String>(new ArrayList<String>());
        traza.add(motivo);
    }

	public void setUrl (String u) {
		url = u;
	}

	public String getUrl() {
		return url;
	}

	public void setCodigo (String c) {
		codigo = c;
	}

	public String getCodigo() {
		return codigo;
	}

	public void addMotivo(String motivo) {
		traza.add(motivo);
	}

	public ArrayList<String> getTraza() {
		return traza;
	}

	public String toString () {
		String result="";

    Collections.reverse(traza);

		if (!url.equals("")) result = "* URL = "+url+"\n";

		for (String s : traza)
			result += "* "+s+"\n";

		return result;
	}

	public String toHTMLString () {
		String result="";

		if (!url.equals("")) result = "URL = "+url+"<br><br>";

    Collections.reverse(traza);
		
		for (String s : traza)
			result += "* "+s+"<br>";

		return result;
	}

}
