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

// cosas comunes para varias clases

package docencia.sint.IML;

import java.io.PrintWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import docencia.sint.Common.CommonSINT;

import java.util.ArrayList;
import java.util.Collection;

// import docencia.sint.Common.CommonSINT;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonIML {

	public static final String CREATED = "2018";
	public static final String CURSO = "2018-2019";
	public static final String LANGUAGE = "IML"; 
    static final String MSGTITLE = "Servicio Songs";
    static final String MSGINICIAL = "Servicio de consulta de información musical";

    static HashMap<String,Document> mapDocs = new HashMap<String,Document>();   // el hashmap de documentos
    
    static XPathFactory xpathFactory = XPathFactory.newInstance();
    static XPath xpath = xpathFactory.newXPath();
    
    static int real=1; // para indicar si los resultados son reales (real=1) o inventados (real=0)
    
	private static Logger logger = null;  // el objeto Logger
	
	// para inicializar el objeto Logger
	
	public static void initLoggerIML (Class c) {
		logger = LogManager.getLogger(c);
	}
	
	 // para imprimir con el Logger en el sintprof.log
	 
	 public static void logIML (String msg) {
		logger.info("## IML ## "+msg);
	 }
	 
	 
    
    // para imprimir la cabecera de cada respuesta HTML
    
	public static void printHead(PrintWriter out) {
		out.println("<head><meta charset='utf-8'/>");
		out.println("<title>"+CommonIML.MSGTITLE+"</title>");
		out.println("<link rel='stylesheet'  type='text/css' href='css/iml.css'/></head>");
	}
   

	// para encontrar una canción a partir de su IDC
	
	public static Element findSong(String panio, String pidd, String pidc) {
		Document doc = CommonIML.mapDocs.get(panio);
	   	
		if (doc == null) {
			CommonIML.logIML("No hay doc para el año "+panio);
			return null;  // no existe ese año
		}
	
		String xpathTarget= "/Songs/Pais/Disco/Cancion[@idc='"+pidc+"']";  
		
		NodeList nlCancion;
		
		try {  // obtenemos la cancion seleccionada
		    	nlCancion = (NodeList)CommonIML.xpath.evaluate(xpathTarget, doc, XPathConstants.NODESET);
		}
		catch (XPathExpressionException ex) {CommonIML.logIML("Excepción: "+ex.toString()+" con la expresión: "+xpathTarget); return null;}
		
		if (nlCancion.getLength() == 0) {
			CommonIML.logIML("No hay canción "+pidc+"para el año "+panio+"y el disco:"+pidd);
			return null; 
		}
			
		Element elemCancion = (Element)nlCancion.item(0);
		
		return elemCancion;
	}
	
	
	// para encontrar todas las canciones de un intérprete
	
	
	public static ArrayList<Cancion> findSongsInterprete (String interprete) {	
		Document doc;
		String xpathTarget;
		NodeList nlCancion;
		Element elemCancion;
		String idc, titulo, sduracion, descripcion, genero, premios="";
		int duracion;
		NodeList nlPremios, nlPremio;
		Element elemDisco, elemPremios, elemPremio;
			
		ArrayList<Cancion> listaCanciones = new ArrayList<Cancion>();
			
		// iteramos sobre todos los docs
			
		Collection<Document> collectionDocs = CommonIML.mapDocs.values();
		Iterator<Document> iter = collectionDocs.iterator();
	
		while (iter.hasNext()) {   // se busca en todos los años
	
			doc = iter.next();
			
			xpathTarget = "/Songs/Pais/Disco[Interprete = '"+interprete+"']/Cancion";  
		    
			try {  // obtenemos las canciones de ese intérprete
	  	    		nlCancion = (NodeList)CommonIML.xpath.evaluate(xpathTarget, doc, XPathConstants.NODESET);
			}
			catch (XPathExpressionException ex) {
				CommonIML.logIML("Excepción: "+ex.toString()+" con la expresión: "+xpathTarget); 
				continue;  // hubo un problema desconocido con este año, pasamos al siguiente
			}

			
			// procesamos todas las canciones de ese intérprete de ese año
			
			for (int t=0; t < nlCancion.getLength(); t++) {
	
				elemCancion = (Element)nlCancion.item(t);  // estudiamos una canción
				
				idc = elemCancion.getAttribute("idc");
				titulo = CommonSINT.getTextContentOfChild(elemCancion, "Titulo");   // averiguamos el título de la canción
				sduracion = CommonSINT.getTextContentOfChild(elemCancion, "Duracion");   //obtenemos la duración de la canción seleccionada
				duracion = Integer.parseInt(sduracion);
				descripcion = CommonSINT.getTextContent(elemCancion);		    						    				
				genero = CommonSINT.getTextContentOfChild(elemCancion, "Genero");   // averiguamos el genero de la canción
				
				elemDisco = (Element)elemCancion.getParentNode();
    			
    			nlPremios = elemDisco.getElementsByTagName("Premios");  
    			
    			premios="";
    			if (nlPremios.getLength() > 0) {
	    			elemPremios = (Element)nlPremios.item(0);// obtenemos el elemento Premios del disco
	    			
	    			nlPremio = elemPremios.getElementsByTagName("Premio");  // obtenemos los elementos Premio del disco
	    			
    				for (int z=0; z < nlPremio.getLength(); z++) {
		    			elemPremio = (Element)nlPremio.item(z);   	    			
		    			
		    			if (premios.equals("")) premios = elemPremio.getTextContent().trim();
		    			else premios = premios+" "+elemPremio.getTextContent().trim();
    				}
    			}
				
				listaCanciones.add(new Cancion(idc, titulo, duracion, descripcion, genero, premios));
			}
		}
		
		return listaCanciones;
	}
	
	
	// para averiguar el género de una canción
	
	public static String findGeneroOfCancion (String idc) {	
	
	    Document doc;
	    NodeList nlCancion;
	    Element elCancion;
	    String genero="";
	    
	    String target = "/Songs/Pais/Disco/Cancion[@idc='"+idc+"']";     // canción con idc 
			
		Collection<Document> collectionDocs = CommonIML.mapDocs.values();
		Iterator<Document> iter = collectionDocs.iterator();
		
		while (iter.hasNext()) {   // iteramos sobre todos los años
	
			doc = iter.next();
	
	    	try {  // obtenemos las canciones con ese idc, debería haber sólo una, en un único fichero
	    		nlCancion = (NodeList)CommonIML.xpath.evaluate(target, doc, XPathConstants.NODESET);
	    		}
			catch (XPathExpressionException ex) {CommonIML.logIML(ex.toString()); return null;}
			catch (Exception ex) {CommonIML.logIML(ex.toString()); return null;}
	    		    	
	    	if (nlCancion.getLength() == 0) continue;  // en este fichero no hay, vamos al siguiente fichero
	    	
	    	if (nlCancion.getLength() > 1) break;  // hay más de una, imposible, salimos del bucle
	    	
	    	elCancion = (Element)nlCancion.item(0);
	    	
	    	genero = CommonSINT.getTextContentOfChild(elCancion, "Genero"); 
	    	break;
		}
		
		return genero;
	
	}
	
	
}



