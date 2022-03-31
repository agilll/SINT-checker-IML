/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Práctica IML
 *
 *    Autor: Alberto Gil Solla
 *    Curso : 2018-2019
 ****************************************************************/

// puede trabajar con datos reales o inventados (parámetro 'real=no' en web.xml)
// se puede especificar en el web.xml el directorio base  de los documentos (parámetro dirBase), el directorio base de los Schemas (dirRulesBase), y el fichero inicial (urlInicial)

package docencia.sint.IML;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import docencia.sint.Common.CommonSINT;
import docencia.sint.Common.ErrorHandlerSINT;
import docencia.sint.Common.WrongFile;

import javax.xml.xpath.XPathConstants;



public class P2IM extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
	
	// todas estas son variables de clase, compartidas por todos los usuarios
    
    final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    final String IML_SCHEMA = "_rules/iml.xsd";
	    
    // los valores por defecto, si no hay parámetros de configuración
    String dirBaseDefault = "http://localhost:7000/sintprof/ficheros_prueba/18-19_IML/";
    String urlInicialDefault = "iml2001.xml";
    // String urlInicialDefault = "T1.xml";
    
    // los parámetros de configuración o los valores por defecto
    String dirBase;
    String urlInicial;
    
    ArrayList<String> listaFicherosProcesados = new ArrayList<String>();
    
    ArrayList<WrongFile> listWarnings = new ArrayList<WrongFile>();
    ArrayList<WrongFile> listErrores= new ArrayList<WrongFile>();
    ArrayList<WrongFile> listErroresfatales = new ArrayList<WrongFile>();
	
 
    // El init se ejecuta al cargar el servlet la primera vez
    
    public void init (ServletConfig servletConfig) throws ServletException {
	
        CommonIML.initLoggerIML(P2IM.class);
        CommonIML.logIML("\nInit...");
        
        // CommonSINT.sintLog(this,"P2IM", "Init...");
        
		/*  para el examen
		 
		     DocumentBuilderFactory dbfe;    // los terminados en e son para el examen
	    DocumentBuilder dbe;
	 
	    
		dbfe = DocumentBuilderFactory.newInstance();
		
	    	try {
	    		dbe = dbfe.newDocumentBuilder();
	    	}
	    	catch  (ParserConfigurationException e) {
	    			throw new UnavailableException("Error creando el builder para el examen: "+e);
	    	}
		
		Document doce;
	
	    	try {
	    		doce = dbe.parse("http://gssi.det.uvigo.es/users/agil/public_html/ex1.xml");
	    	}
	    	catch (Exception e) {
	    		return;
	    	}
	  
	
		Element examen = doce.getDocumentElement();
		
		// esto será distinto para cada examen
		
		NodeList nlcalle = examen.getElementsByTagName("calle");
		Element calle = (Element)nlcalle.item(0);
		excalle = calle.getTextContent().trim();
	
		exnum = calle.getAttribute("numeros");
		
		NodeList nlhijos = examen.getChildNodes();
		
		for (int j=0; j < nlhijos.getLength(); j++) {
			Node e = (Node)nlhijos.item(j);
	    		if (e.getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
	    			extit = e.getNodeValue().trim();
				if (!extit.equals(""))
					break;
			}
	
		*/
			
    	
    	
    	// si hay un parámetro "real=no" se trabajará con datos inventados (real=0)
    	// de lo contrario se leerán los ficheros reales

    	String datosReales = servletConfig.getInitParameter("real");

    	if (datosReales != null)
    		if (datosReales.equals("no")) CommonIML.real=0;

    	// si hay un parámetro "dirBase", se tomará como directorio base de los ficheros
    	// de lo contrario se cogerá el especificado por defecto

    	dirBase = servletConfig.getInitParameter("dirBase");
    	if (dirBase == null) dirBase = dirBaseDefault;

    	// si hay un parámetro "urlInicial", se tomará como fichero inicial
    	// de lo contrario se cogerá el especificado por defecto

    	urlInicial = servletConfig.getInitParameter("urlInicial");
    	if (urlInicial == null)  urlInicial = urlInicialDefault;
    	
        CommonIML.logIML("Leyendo ficheros...");
       	if (CommonIML.real==1) this.buscarFicheros(dirBase, urlInicial, servletConfig);
       	
       	Collections.sort(listWarnings);
       	Collections.sort(listErrores);
       	Collections.sort(listErroresfatales);
       	
    }
  
  
    	
	
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    		throws IOException, ServletException
    {
        // previamente se ha comprobado la passwd con un filtro
        
	    String pfase = request.getParameter("pfase");
	    if (pfase == null) pfase = "01";

	    CommonIML.logIML("Solicitud fase "+pfase);
	       
	    switch (pfase) {
		case "01":
			this.doGetHome(request,response);
			break;
		case "02":
			this.doGetErrors(request,response);
			break;
	
			// consulta 1, canciones de un interprete que duran menos que una dada

		case "11": // se pide el listado de años
			P2IMC1.doGetF11Anios(request, response);
			break;
		case "12": // se pide los discos de un año	
			P2IMC1.doGetF12Discos(request, response); 
			break;
		case "13": // se pide las canciones de un disco de un año
			P2IMC1.doGetF13Canciones(request, response); 
			break;
		case "14":  // se pide las canciones de un interprete que duran menos que una dada
			P2IMC1.doGetF14Result(request, response); 
			break;
	

			// consulta 2, discos de un intérprete en un determinado idioma
	
		case "21":  // se pide el listado de idiomas
			P2IMC2.doGetF21Langs(request, response);
			break;
		case "22":  // se pide las canciones en ese idioma
			P2IMC2.doGetF22Canciones(request, response);
			break;
		case "23":  // se piden los intérpretes de canciones en un idioma y de un género
			P2IMC2.doGetF23Interpretes(request, response);
			break;
		case "24":   // se piden los discos de un intérprete
			P2IMC2.doGetF24Discos(request, response);
			break;
	      
			
		default:
			CommonSINT.doBadRequest("el parámetro 'pfase' tiene un valor incorrecto ("+pfase+")", request, response); 
			break;
	}
}


 




    // la pantalla inicial 

    public void doGetHome (HttpServletRequest request, HttpServletResponse response)
    		throws IOException
    {
    	response.setCharacterEncoding("utf-8");
    	PrintWriter out = response.getWriter();

    	String auto = request.getParameter("auto");
	    
    	if (auto == null) {
    	    CommonIML.logIML("HOME auto=no");
    	    
    		out.println("<html>");
    		CommonIML.printHead(out);
    		out.println("<body>");
   
    		out.println("<h2>"+CommonIML.MSGINICIAL+"</h2>");
    		out.println("<h2>Bienvenido a este servicio</h2>");

    		out.println("<a href='?pfase=02&p="+CommonSINT.PASSWD+"'>Pulsa aquí para ver los ficheros erróneos</a>");

    		out.println("<h3>Selecciona una consulta:</h3>");

    		out.println("<form>");
    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
    		out.println("<input type='radio' name='pfase' value='11' checked>Consulta 1: Canciones de un interprete  <br>");
    		out.println("<input type='radio' name='pfase' value='21' checked>Consulta 2: Discos de un intérprete<br>");

    		out.println("<p><input class='enviar' type='submit' value='Enviar'>");
    		out.println("</form>");

    		CommonSINT.printFoot(out, CommonIML.CURSO);
    		out.println("</body></html>");
    	}
    	else {
    	    CommonIML.logIML("HOME auto=si");
    	    
    		out.println("<?xml version='1.0' encoding='utf-8'?>");
    		out.println("<service>");
    		out.println("<status>OK</status>");
    		out.println("</service>");
    	}
    }


    // método que imprime o devuelve la lista de errores

    public void doGetErrors (HttpServletRequest request, HttpServletResponse response)
    		throws IOException
    {
    	response.setCharacterEncoding("utf-8");
    	PrintWriter out = response.getWriter();

    	String auto = request.getParameter("auto");

    	if (auto == null) {
    	    CommonIML.logIML("ERRORES auto=no");
    	    
       	    out.println("<html>");
    		CommonIML.printHead(out);
    		out.println("<body>");
 
    		out.println("<h2>"+CommonIML.MSGINICIAL+"</h2>");

    		out.println("<h3>Se han encontrado "+listWarnings.size()+" ficheros con warnings:</h3>");
    		if (listWarnings.size() > 0) {
    			out.println("<ul>");

    			for (int x=0; x < listWarnings.size(); x++) {
      				WrongFile wf = listWarnings.get(x);
    				out.println("<li> "+wf.getFile()+":<BR>");
    				out.println("<ul>");
    	  			
    				ArrayList<String> warningsL = wf.getCausas();

    	  			for (int y=0; y < warningsL.size(); y++) {
    	  				out.println("<li> "+warningsL.get(y)+"<BR>");
    	  			}
    	  			
        			out.println("</ul>");
    			}

    			out.println("</ul>");
    		}
    		    		
       		out.println("<h3>Se han encontrado "+listErrores.size()+" ficheros con errores:</h3>");
    		if (listErrores.size() > 0) {
    			out.println("<ul>");

    			for (int x=0; x < listErrores.size(); x++) {
      				WrongFile wf = listErrores.get(x);
    				out.println("<li> "+wf.getFile()+":<BR>");
    	  			out.println("<ul>");
    	  			
    				ArrayList<String> erroresL = wf.getCausas();

    	  			for (int y=0; y < erroresL.size(); y++) {
    	  				out.println("<li> "+erroresL.get(y)+"<BR>");
    	  			}
    	  			
        			out.println("</ul>");
    			}

    			out.println("</ul>");
    		}
    		    		
      		out.println("<h3>Se han encontrado "+listErroresfatales.size()+" ficheros con errores fatales:</h3>");
    		if (listErroresfatales.size() > 0) {
    			out.println("<ul>");

    			for (int x=0; x < listErroresfatales.size(); x++) {
    				WrongFile wf = listErroresfatales.get(x);
    				out.println("<li> "+wf.getFile()+":<BR>");
   	  			    out.println("<ul>");
	  			
    				ArrayList<String> fatalerroresL = wf.getCausas();

    	  			for (int y=0; y < fatalerroresL.size(); y++) {
    	  				out.println("<li> "+fatalerroresL.get(y)+"<BR>");
    	  			}
    	  			
        			out.println("</ul>");
    			}

    			out.println("</ul>");
    		}

    		out.println("<form>");
    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
    		out.println("<input class='back' type='submit' value='Atrás'>");
    		out.println("</form>");
    		
    		CommonSINT.printFoot(out, CommonIML.CURSO);               		
    		out.println("</body></html>");
    	}
    	else {
    	    CommonIML.logIML("ERRORES auto=si");
    	    
    		out.println("<?xml version='1.0' encoding='utf-8'?>");
    		out.println("<errores>");

    		out.println("<warnings>");
    		for (int x=0; x < listWarnings.size(); x++) {
 				WrongFile wf = listWarnings.get(x);
    			out.println("<warning>");
    			out.println("<file>"+wf.getFile()+"</file>");
    			out.println("<cause>");
    			ArrayList<String> warningsL = wf.getCausas();

	  			for (int y=0; y < warningsL.size(); y++) {
	  				out.println(warningsL.get(y));
	  			}
    		  	out.println("</cause>");
    			out.println("</warning>");
    		}
    		out.println("</warnings>");

    		out.println("<errors> ");	
    		for (int x=0; x < listErrores.size(); x++) {
				WrongFile wf = listErrores.get(x);
    			out.println("<error> ");
    			out.println("<file>"+wf.getFile()+"</file>");
    			out.println("<cause>");
    			ArrayList<String> errorsL = wf.getCausas();

	  			for (int y=0; y < errorsL.size(); y++) {
	  				out.println(errorsL.get(y));
	  			}
    	
    			out.println("</cause>");
    			out.println("</error>");
    		}
    		out.println("</errors>");

    		out.println("<fatalerrors>");	
    		for (int x=0; x < listErroresfatales.size(); x++) {
				WrongFile wf = listErroresfatales.get(x);
    			out.println("<fatalerror>");
    			out.println("<file>"+wf.getFile()+"</file>");
    			out.println("<cause>");
    			ArrayList<String> fatalerroresL = wf.getCausas();

	  			for (int y=0; y < fatalerroresL.size(); y++) {
	  				out.println(fatalerroresL.get(y));
	  			}
    			out.println("</cause>");
    			out.println("</fatalerror>");
    		}
    		out.println("</fatalerrors>");

    		out.println("</errores>");
    	}
    }








    // MÉTODOS AUXILIARES


    // Zona de búsqueda de ficheros, llamado la primera vez que se invoca el doGet

    public void buscarFicheros (String urlBase, String fich, ServletConfig conf)
	throws UnavailableException  {

		DocumentBuilderFactory dbf;
		DocumentBuilder db;
		ErrorHandlerSINT errorHandler;
	
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(true);
		dbf.setNamespaceAware(true);
		dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		
		ServletContext servCont = conf.getServletContext();
		String pathSchema = servCont.getRealPath(IML_SCHEMA);
		File fileSchema = new File(pathSchema);
		dbf.setAttribute(JAXP_SCHEMA_SOURCE, fileSchema);
	
	/* otra forma
	 * 
	 FICHERO_SCHEMA = "/eaml.xsd";
SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
StreamSource streamSource = new StreamSource(this.getServletContext().getResourceAsStream(FICHERO_SCHEMA));
Schema schema = sf.newSchema(streamSource);
dbf.setSchema(schema);
	 
	 */

	
		try {
			db = dbf.newDocumentBuilder();
		}
		catch  (ParserConfigurationException e) {
			throw new UnavailableException("Error creando el analizador de ficheros "+CommonIML.LANGUAGE+": "+e);
		}
	
		errorHandler = new ErrorHandlerSINT();
		db.setErrorHandler(errorHandler);
			
    	Document doc;
    	String url = urlBase+fich;
    	
    	listaFicherosProcesados.add(url);  // damos este fichero por procesado

    	// parsear el fichero solicitado

    	errorHandler.clear();  // resetear el ErrorHandler para borrar lo anterior
    	
    	try {
    		doc = db.parse(url);
    	}
    	catch (SAXException ex) {
    		listErroresfatales.add(new WrongFile(url, ex.toString()));    		
    		return;
    	}
    	catch (IOException ex) {    	
    		listErroresfatales.add(new WrongFile(url, ex.toString()));
    		return;
    	}

    	// ver si saltó el ErrorHandler

    	if (errorHandler.hasWarnings()) {  	
      		listWarnings.add(new WrongFile(url, errorHandler.getWarnings()));
    	}

    	if (errorHandler.hasErrors()) {    
    		listErrores.add(new WrongFile(url, errorHandler.getErrors()));
    		return;  // si hubo un error se termina
    	}

    	if (errorHandler.hasFatalerrors()) {    	   		
       		listErroresfatales.add(new WrongFile(url, errorHandler.getFatalerrors()));
    		return;  // si hubo un fatalerror se termina
    	}
    	

    	// Vamos a procesar este año para ver si contiene enlaces a otros ficheros

    	String anio; 

    	// averiguar el año del fichero que acabamos de leer
    	// la excepción no debería producirse, pero...
    	try {
    		NodeList nlAnios = (NodeList)CommonIML.xpath.evaluate("/Songs/Anio", doc, XPathConstants.NODESET);
    		Element elemAnio = (Element)nlAnios.item(0);
    		anio = elemAnio.getTextContent().trim();
    		if (anio.equals("")) throw new Exception("Anio vacío");
    	}
    	catch (Exception ex) {    	
       		listErrores.add(new WrongFile(url, "Problema leyendo 'Anio' ("+ex+")"));
    		return;  // si se produce cualquier tipo de excepción, hay un error y se termina
    	}

    	CommonIML.mapDocs.put(anio,doc);  // almacenar el Document del año leído


    	// buscar recursivamente los nuevos ficheros que hay en el que acabamos de leer

    	// conseguir la lista de Version

    	NodeList nlVersion = doc.getElementsByTagName("Version");

    	for (int x=0; x < nlVersion.getLength(); x++) {

    		// procesar cada uno de los encontrados
    		
    		Element elemVersion = (Element)nlVersion.item(x);
    		
    		// averiguar el año al que corresponde    		
    		String nuevoAnio = elemVersion.getAttribute("anio");
    		if (nuevoAnio.equals("")) continue;

    		// averiguar el url de ese nuevo año
			String nuevaUrl = CommonSINT.getTextContentOfChild(elemVersion, "IML");
			if (nuevaUrl.equals("")) continue;
			
			String laBase, elFichero;
			
			if (nuevaUrl.startsWith("http://"))   {  // si es absoluta la dividimos entre la base y el fichero
				laBase = nuevaUrl.substring(0,nuevaUrl.lastIndexOf('/')+1);
				elFichero = nuevaUrl.substring(nuevaUrl.lastIndexOf('/')+1);
			}
			else {
				laBase = urlBase;
				elFichero = nuevaUrl;
			}
			
			// si ya hemos leído este fichero en el pasado lo saltamos
			if (listaFicherosProcesados.contains(laBase+elFichero)) continue;
			
    		// mirar si este año ya lo tenemos, lo normal es que no
    		
    		Document doc2 = CommonIML.mapDocs.get(nuevoAnio);

    		// si no lo tenemos, aplicamos este método recursivamente sobre su fichero

    		if (doc2 == null) this.buscarFicheros(laBase, elFichero, conf);
    
    	}
    }
}