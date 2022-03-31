/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Prácticas de SINT (paquete común a todas las prácticas)
 *
 *    Autor: Alberto Gil Solla
 ****************************************************************/

// ErrorHandler para las prácticas de SINT y para los checkers, siempre igual, independiente del lenguaje

package docencia.sint.Common;

import java.util.ArrayList;

import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ErrorHandlerSINT extends DefaultHandler  {

	ArrayList<String> warnings;
	ArrayList<String> errors;
	ArrayList<String> fatalerrors;

	public ErrorHandlerSINT ()  {
		warnings = new ArrayList<String>();
		errors = new ArrayList<String>();
		fatalerrors = new ArrayList<String>();
	}

	public void warning(SAXParseException spe)  {
		warnings.add(spe.toString());
	}

	public void error(SAXParseException spe)  {
		errors.add(spe.toString());
	}

	public void fatalerror(SAXParseException spe) {
		fatalerrors.add(spe.toString());
	}


	public boolean hasWarnings() {
		if (warnings.size() > 0) return true;
		else return false;
	}

	public boolean hasErrors() {
		if (errors.size() > 0) return true;
		else return false;
	}

	public boolean hasFatalerrors() {
		if (fatalerrors.size() > 0) return true;
		else return false;
	}


	public ArrayList<String> getWarnings() {
		ArrayList<String> w = new ArrayList<String>(warnings);
		return w;
	}

	public ArrayList<String> getErrors() {
		ArrayList<String> e = new ArrayList<String>(errors);
		return e;
	}

	public ArrayList<String> getFatalerrors() {
		ArrayList<String> f = new ArrayList<String>(fatalerrors);
		return f;
	}


	public void clear () {
		warnings.clear();
		errors.clear();
		fatalerrors.clear();
	}
}
