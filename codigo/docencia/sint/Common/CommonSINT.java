/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Prácticas de SINT  (paquete común a todas las prácticas)
 *
 *    Autor: Alberto Gil Solla
 ****************************************************************/


// cosas comunes para todas las prácticas, independientemente del lenguaje de cada año

package docencia.sint.Common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class CommonSINT {

	  public static final String PFASE = "pphase";  // nombre del parámetro que lleva la fase en todas las prácticas
    public static String PPWD = "sp";   // la passwd de sintprof

	// a devolver en caso de algún error en la solicitud

    public static void doBadRequest (String reason, ServletRequest request, ServletResponse response)
    		throws IOException
    {
	    	response.setCharacterEncoding("utf-8");
	    	PrintWriter out = response.getWriter();

	    	String auto = request.getParameter("auto");

	    	if (auto == null) {
	    		out.println("<html><head><meta charset='utf-8'/><title>Página de error</title>");
	    		out.println("</head><body>");
	    		out.println("<h3>Wrong Request: "+reason+"</h3>");
	    		out.println("</body></html>");
	    	}
	    	else {
	    		out.println("<?xml version='1.0' encoding='utf-8'?>");
	    		out.println("<wrongRequest>"+reason+"</wrongRequest>");
	    	}
    }



    // devuelve el texto intermedio de un elemento tipo mixed

	 public static String getTextContent (Element elem) {
	 	NodeList nlHijos = elem.getChildNodes();

	 	String texto = "";

	 	for (int x=0; x < nlHijos.getLength(); x++) {
	 		Node hijo = nlHijos.item(x);

	 		if (hijo.getNodeType() == org.w3c.dom.Node.TEXT_NODE)
	 			texto = texto+hijo.getNodeValue();
	 	}

	 	texto = texto.trim();

	 	return texto;  // "" si no hay texto
	 }



	 // devuelve el contenido (que es sólo texto) de un elemento llamado como el segundo parámetro, hijo del que se le pasa como primero

	 public static String getTextContentOfChild (Element elem, String nombre) {
			NodeList nl = elem.getElementsByTagName(nombre);
			if (nl.getLength() == 0)  return null;

			Element elemHijo = (Element)nl.item(0);
			String contenido = elemHijo.getTextContent().trim();
		    return contenido;
	 }



    // para pedir el contenido de una URL

    public static String getURLContents (String stringURL) throws ExcepcionChecker
    {
            String answer="", inputLine="";
            URL url = null;

            try {
               url = new URL(stringURL);
            }
            catch (MalformedURLException e) {
							  CheckerFailure cf = new CheckerFailure(stringURL, "", "getURLContents: Problema generando la URL");
                throw new ExcepcionChecker(cf);
            }

            BufferedReader in = null;

            try {
                in = new BufferedReader(new InputStreamReader(url.openStream()));
            }
            catch (Exception e) {
							CheckerFailure cf = new CheckerFailure(stringURL, "", "getURLContents: Problema abriendo la URL");
							throw new ExcepcionChecker(cf);
            }

            try {
               while ((inputLine = in.readLine()) != null) {
                   answer = answer + inputLine;
               }
            }
            catch (IOException ioe){
							CheckerFailure cf = new CheckerFailure(stringURL, "", "getURLContents: Problema leyendo el contenido de la URL");
							throw new ExcepcionChecker(cf);
            }

            return answer;
	}



    public static void printFoot(PrintWriter out, String created)
    {
        out.println("<div id='foot'><hr>&copy; Alberto Gil Solla ("+created+")</div>");
    }


    // para terminar de imprimir una página de un checker con los botones de Inicio y Atrás
	// para el botón de Atrás, recibe el número de pantalla al cual debe volverse si se pulsa ese botón

	public static void printEndPageChecker (PrintWriter out, String previousPhase, int esProfesor, String curso, String lang)
	{
		out.println("<form>");
		out.println("<p><input type='hidden' name='screenP' value='0'>");
		if (esProfesor == 1)
			out.println("<p><input type='hidden' name='p' value='si'>");
		out.println("<input class='back' type='submit' value='"+MsCP.getMsg(MsCP.CPC02,lang)+"' onClick='document.forms[0].screenP.value=\""+previousPhase+"\"'>"); //CB02=Atrás
		out.println("<input class='home'  type='submit' value='"+MsCP.getMsg(MsCP.CPC01,lang)+"' onClick='document.forms[0].screenP.value=\"0\"'>"); //CB01=Inicio
		out.println("</form>");

		CommonSINT.printFoot(out, curso);

		out.println("</body></html>");
	}



}
