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

// consulta 1
// canciones de un interprete que duren menos que una dada de ese interprete (año+disco+canción)

package docencia.sint.IML;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import docencia.sint.Common.CommonSINT;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;


   // MÉTODOS PARA LA PRIMERA CONSULTA
   
public class P2IMC1 {

    // F11: método que imprime o devuelve la lista de años

    public static void doGetF11Anios (HttpServletRequest request, HttpServletResponse response) throws IOException {

	    	ArrayList<String> anios = P2IMC1.getAniosF11();   // se pide la lista de anios
	
	    	if (anios.size() == 0) {
	    		CommonSINT.doBadRequest("no hay años", request, response);
	    		return;
	    	}
	
	    	response.setCharacterEncoding("utf-8");
	    	PrintWriter out = response.getWriter();
	
	    	String auto = request.getParameter("auto");
	
	    	if (auto == null) {
	    		out.println("<html>");
	    		CommonIML.printHead(out);
	    		out.println("<body>");
	    		out.println("<h2>"+CommonIML.MSGINICIAL+"</h2>");
		    	out.println("<h3>Consulta 1</h3>");
	    		out.println("<h3>Selecciona un año:</h3>");
	
	    		out.println("<form>");
	    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
	    		out.println("<input type='hidden' name='pfase' value='12'>");  // de aquí se pasa a la fase 12
	
	    		for (int x=0; x < anios.size(); x++) 
	    			out.println("<input type='radio' name='panio' value='"+anios.get(x)+"' checked> "+(x+1)+".- "+anios.get(x)+"<BR>"); 
			
	    		out.println("<p><input class='enviar'  type='submit' value='Enviar'>");
	    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"01\"'>");  // Atrás vuelve al inicio
	    		out.println("</form>");
	    		
	    		CommonSINT.printFoot(out, CommonIML.CURSO);
	    		out.println("</body></html>");
	    	}
	    	else {
	    		out.println("<?xml version='1.0' encoding='utf-8'?>");
	    		out.println("<anios>");
	
	    		for (int x=0; x < anios.size(); x++) 
	    			out.println("<anio>"+anios.get(x).trim()+"</anio>");
	
	    		out.println("</anios>");
	    	}
    }


    // método auxiliar del anterior, que calcula la lista de años

    private static ArrayList<String> getAniosF11 () {	
	    	if (CommonIML.real == 0)
	    		return new ArrayList<String>(Arrays.asList("2011", "2012","2013","2014"));
	    	else {
	    		ArrayList<String> listaAnios = new ArrayList<String>();
	    		
	    		// convertimos las claves del hashmap en una lista
	    		
	    		Set<String> setAnios = CommonIML.mapDocs.keySet();
	    		listaAnios.addAll(setAnios);
	    		
	    		Collections.sort(listaAnios);  // se ordenan alfabéticamente, que es equivalente a cronológicamente
	    		return listaAnios;
	    	}
    }






    // F12: método que imprime o devuelve la lista de discos de un año

    public static void doGetF12Discos (HttpServletRequest request, HttpServletResponse response) throws IOException {

		Disco disco;
	    ArrayList<Disco> discos;
	
	    String panio = request.getParameter("panio");
	    if (panio == null) {
	    	CommonSINT.doBadRequest("no param:panio", request, response);
	    	return;
	    }
	
	    discos = P2IMC1.getDiscosF12(panio);  // se pide la lista de discos del año seleccionado
	
	    if (discos == null) {
	    		CommonSINT.doBadRequest("el año "+panio+" no existe", request, response);
	    		return;
	    }	    
	
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	
	    String auto = request.getParameter("auto");
	
	    if (auto == null) {
	    		out.println("<html>");
	    		CommonIML.printHead(out);
	    		out.println("<body>");
	 
	    		out.println("<h2>"+CommonIML.MSGINICIAL+"</h2>");
	    		out.println("<h3>Consulta 1:");
	    		out.println("Año="+panio+"</h3>");
	
	    		out.println("<h3>Selecciona un disco:</h3>");
	
	    		out.println("<form>");
	    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
	    		out.println("<input type='hidden' name='panio' value='"+panio+"'>");
	    		out.println("<input type='hidden' name='pfase' value='13'>");  // de aquí se pasa a la fase 13
	    		
	    		if (discos.size() == 0)
	    			out.println("No hay discos en el año "+panio);
	    		else
		    		for (int x=0; x < discos.size(); x++) {
		    			disco = discos.get(x);
		    		
		    			out.println("<input type='radio' name='pidd' value='"+disco.getIDD()+"' checked> "+(x+1)+".- <b>Título</b> = '"+
		    					     disco.getTitulo()+"'  ---  <b>IDD</b> = '"+disco.getIDD()+"'  ---  <b>Interprete</b> = '"+disco.getInterprete()+"'  ---  <b>Idiomas</b> = '"+disco.getIdiomas()+"'<BR>"); 
		    		}
	
	    		out.println("<p><input class='enviar' type='submit' value='Enviar'>");
	    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"11\"'>");  // Atrás vuelve a la fase 11
	    		out.println("<input class='home' type='submit' value='Inicio' onClick='document.forms[0].pfase.value=\"01\"'>");
	    		out.println("</form>");
	
	    		CommonSINT.printFoot(out, CommonIML.CURSO);
	    		out.println("</body></html>");
	    }
	    else {
	    		out.println("<?xml version='1.0' encoding='utf-8'?>");
	
	    		out.println("<discos>");
	
	    		for (int x=0; x < discos.size(); x++) {
	    			disco = discos.get(x);
	    		
	    			out.println("<disco idd='"+disco.getIDD()+"' interprete='"+disco.getInterprete()+"' langs='"+disco.getIdiomas()+"'>"+disco.getTitulo()+"</disco>");
	    		}
	
	    		out.println("</discos>");
	    }
    }


    // método auxiliar del anterior, que calcula la lista de discos de un año dado

    private static ArrayList<Disco> getDiscosF12 (String panio) {
	    if (CommonIML.real == 0)
	    	return new ArrayList<Disco>(Arrays.asList(new Disco("IDD1","D1", "I1","lang1", "pr1"), new Disco("IDD2","D2", "I2","lang2", "pr2"), new Disco ("IDD3","D3", "I3","lang3", "pr3")));
	    else {	    
	    		Document doc = CommonIML.mapDocs.get(panio);
	    		if (doc == null) return null;  // no existe ese año
	
	    		ArrayList<Disco> listaDiscos = new ArrayList<Disco>();
	
	    		NodeList nlDiscos = doc.getElementsByTagName("Disco");  // pedimos el NodeList con todas los discos de ese año
	
	    		Element elemDisco, elemPais, elemPremios, elemPremio;
	    		String langPais, titulo, idd, interprete, langsDisco, langs, premios;
	    		NodeList nlPremios, nlPremio;
	    		
	    		// vamos a recopilar la información de todos los discos
	    		
	    		for (int y=0; y < nlDiscos.getLength(); y++) {
	    			elemDisco = (Element)nlDiscos.item(y);  // estudiamos un disco
	    			
	    			interprete = CommonSINT.getTextContentOfChild(elemDisco, "Interprete");  // obtenemos el intérprete del disco
	    			titulo = CommonSINT.getTextContentOfChild(elemDisco, "Titulo");  // obtenemos el título del disco
	    			
	    			elemPais = (Element)elemDisco.getParentNode();  // pedimos el País al que pertenece
	    			langPais = elemPais.getAttribute("lang");   // leemos el idioma por defecto del país
	    			
	    			idd = elemDisco.getAttribute("idd");	    			
	    			langsDisco = elemDisco.getAttribute("langs");
	    			
	    			if (langsDisco.equals("")) langs = langPais;
	    			else langs = langsDisco;
	    			
	    			// vamos a leer los premios del disco 
	    			
	    			nlPremios = elemDisco.getElementsByTagName("Premios");  
	    			
	    			if (nlPremios.getLength() > 0) {
	    				premios="";
    	    			elemPremios = (Element)nlPremios.item(0);// obtenemos el elemento Premios del disco
    	    			
		    			nlPremio = elemPremios.getElementsByTagName("Premio");  // obtenemos los elementos Premio del disco
		    			
	    				for (int z=0; z < nlPremio.getLength(); z++) {
			    			elemPremio = (Element)nlPremio.item(z);   	    			
			    			
			    			if (premios.equals("")) premios = elemPremio.getTextContent().trim();
			    			else premios = premios+", "+elemPremio.getTextContent().trim();
	    				}
	    			}
	    			else premios="";
	    			
	    			listaDiscos.add(new Disco(idd, titulo, interprete, langs, premios));  // creamos y añadimos el disco
	    		}
	
	    		Collections.sort(listaDiscos);  // Agrupados por intérprete (dispuestos estos en orden alfabético). Para cada intérprete, por orden alfabético de título de canción
	
	    		return listaDiscos;
	    }
    }







    // F13: método que imprime o devuelve la lista de canciones de un disco de un año

    public static void doGetF13Canciones (HttpServletRequest request, HttpServletResponse response) throws IOException {

    		Cancion cancion;
    		ArrayList<Cancion> canciones;
    		
	    	String panio = request.getParameter("panio");
	
	    	if (panio == null) {
	    		CommonSINT.doBadRequest("no param:panio", request, response);
	    		return;
	    	}
	
	    	String pidd = request.getParameter("pidd");
	
	    	if (pidd == null) {
	    		CommonSINT.doBadRequest("no param:pidd", request, response);
	    		return;
	    	}
	
	    	
	    	canciones = P2IMC1.getCancionesF13(panio, pidd);   // pedimos la lista de canciones de un disco (ipd) de un año
	
	    	if (canciones == null) {
	    		CommonSINT.doBadRequest("el 'año' ("+panio+") o el 'disco' ("+pidd+") no existen", request, response);
	    		return;
	    	}
	
	    	response.setCharacterEncoding("utf-8");
	    	PrintWriter out = response.getWriter();
	
	    	String auto = request.getParameter("auto");
	
	    	if (auto == null) {
	    		out.println("<html>");
	    		CommonIML.printHead(out);
	    		out.println("<body>");
	   
	    		out.println("<h2>"+CommonIML.MSGINICIAL+"</h2>");
	    		out.println("<h3>Consulta 1: ");
	    		out.println("Año="+panio+", Disco="+pidd+"</h3>");
	
	    		out.println("<h3>Selecciona una canción:</h3>");
	
	    		out.println("<form>");
	    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
	    		out.println("<input type='hidden' name='pidd' value='"+pidd+"'>");
	    		out.println("<input type='hidden' name='panio' value='"+panio+"'>");
	    		out.println("<input type='hidden' name='pfase' value='14'>");  // de aquí se pasa a la fase 14
	
	    		if (canciones.size() == 0)
	    			out.println("No hay canciones en el año "+panio+" y disco "+pidd);
	    		else
		    		for (int x=0; x < canciones.size(); x++) {
		    			cancion = canciones.get(x);
		    			
		    			out.println("<input type='radio' name='pidc' value='"+cancion.getIDC()+"' checked> "+(x+1)+
		    					".- <b>Título</b> = '"+cancion.getTitulo()+"'  ---  <b>IDC</b> = '"+cancion.getIDC()+
		    					"'  --- <b>Género</b> = '"+cancion.getGenero()+"'  --- <b>Duración</b> = '"+cancion.getDuracion()+" seg.' <BR>"); 
		    		}
	
	    		out.println("<p><input class='enviar' type='submit' value='Enviar'>");
	    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"12\"'>");  // Atrás vuelve a la fase 12
	    		out.println("<input class='home' type='submit' value='Inicio' onClick='document.forms[0].pfase.value=\"01\"'>");
	    		out.println("</form>");
	    		
	    		CommonSINT.printFoot(out, CommonIML.CURSO);
	    		out.println("</body></html>");
	    	}
	    	else {
	    		out.println("<?xml version='1.0' encoding='utf-8'?>");
	    		out.println("<canciones>");
	
	    		for (int x=0; x < canciones.size(); x++) {
	    			cancion = canciones.get(x);
	    			out.println("<cancion idc ='"+cancion.getIDC()+"' genero='"+cancion.getGenero()+"' duracion='"+cancion.getDuracion()+"'>"+cancion.getTitulo()+"</cancion>");
	    		}
	
	    		out.println("</canciones>");
	    	}
    }



    // método auxiliar del anterior, que calcula la lista de canciones de un disco de un año
    // si devuelve null es que no se encontró el disco o el año

    private static ArrayList<Cancion> getCancionesF13 (String panio, String pidd) {
	    	if (CommonIML.real == 0)
	    		return new ArrayList<Cancion>(Arrays.asList(new Cancion("ID1","T1", 200, "D1", "ge1", "pr1"), new Cancion("ID2","T2", 200, "D2", "ge2", "pr2"),
	    				new Cancion("ID3","T3", 200, "D3", "ge3", "pr3")));
	    	else {
	    		Document doc = CommonIML.mapDocs.get(panio);
	    		if (doc == null) {
	    			CommonIML.logIML("No hay doc para el año "+panio);
	    			return null;  // no existe ese año
	    		}
	
	    		ArrayList<Cancion> listaCanciones = new ArrayList<Cancion>();  // lista de canciones a devolver
	    		 
	        	String xpathTarget =   "/Songs/Pais/Disco[@idd='"+pidd+"']/Cancion";    // canción perteneciente al disco con atributo idd igual al parámetro
	        	NodeList nlCancion=null;
	    		
	    		try {  // obtenemos las canciones del disco con el identificador seleccionado
	    			nlCancion = (NodeList)CommonIML.xpath.evaluate(xpathTarget, doc, XPathConstants.NODESET);
	    		}
	    		catch (XPathExpressionException e) {CommonIML.logIML(e.toString()); return null;}
	    		
	    		if (nlCancion.getLength() == 0) {
	    			CommonIML.logIML("No hay canciones para el disco "+pidd+" y el año "+panio);
	    			return null;  // no hay canciones para ese disco y ese año???
	    		}
	    		
	    		Element elemCancion;
	    		String idc, titulo, sduracion, descripcion, genero;
	    		int duracion;

	
	    		for (int t=0; t < nlCancion.getLength(); t++) {
					elemCancion = (Element)nlCancion.item(t);  // procesamos cada canción
					
					idc = elemCancion.getAttribute("idc");  // obtenemos su idc
					
	    			titulo = CommonSINT.getTextContentOfChild(elemCancion, "Titulo");  // averiguamos el título de la canción
					
					sduracion = CommonSINT.getTextContentOfChild(elemCancion, "Duracion");   // averiguamos la duración de la canción		
				    duracion = Integer.parseInt(sduracion);
					
					descripcion = CommonSINT.getTextContent(elemCancion);										
					genero = CommonSINT.getTextContentOfChild(elemCancion, "Genero");   //  averiguamos el genero de la canción
		
					listaCanciones.add(new Cancion(idc, titulo, duracion, descripcion, genero, ""));
	    		}
	    		
	    		// si la lista de canciones sigue vacía es que no hemos encontrado el disco
	
	    		if (listaCanciones.size() == 0) return null; 
	
	    		Collections.sort(listaCanciones, Cancion.DURACION);  // ordenamos la lista por la duración de las canciones, en orden ascendente
	    		return listaCanciones;
	    	}
    }









    // F14: método que imprime el resultado, la lista de canciones de un intérprete que duren menos que una dada  

    public static void doGetF14Result (HttpServletRequest request, HttpServletResponse response) throws IOException {
		
    		Cancion cancion;
    		ArrayList<Cancion> resultado;
	    	String panio = request.getParameter("panio");
	
	    	if (panio == null) {
	    		CommonSINT.doBadRequest("no param:panio", request, response);
	    		return;
	    	}
	
	    	String pidd = request.getParameter("pidd");
	
	    	if (pidd == null) {
	    		CommonSINT.doBadRequest("no param:pidd", request, response);
	    		return;
	    	}
	
	    	String pidc = request.getParameter("pidc");
	
	    	if (pidc == null) {
	    		CommonSINT.doBadRequest("no param:pidc", request, response);
	    		return;
	    	}
	
	    	resultado = P2IMC1.getResult14(panio, pidd, pidc);
	
	    	if (resultado == null) {
	    		CommonSINT.doBadRequest("el 'anio' ("+panio+"), o el 'disco' ("+pidd+"), o la 'cancion' ("+pidc+") no existen", request, response);
	    		return;
	    	}

		
	    	response.setCharacterEncoding("utf-8");
	    	PrintWriter out = response.getWriter();
	
	    	String auto = request.getParameter("auto");
	
	    	if (auto == null) {
	    		out.println("<html>");
	    		CommonIML.printHead(out);
	    		out.println("<body>");
	  
	    		out.println("<h2>"+CommonIML.MSGINICIAL+"</h2>");
	    		out.println("<h3>Consulta 1: ");
	    		out.println("Año="+panio+", Disco="+pidd+", Canción="+pidc+"</h3>");
	    		
	    		out.println("<h3>Este es el resultado:</h3>");
	
	    		out.println("<ul>");
	
	    		if (resultado.size() == 0)
	    			out.println("No hay canciones de duración menor a la correspondiente al año "+panio+", disco "+pidd+", y canción "+pidc);
	    		else
		    		for (int x=0; x < resultado.size(); x++) {
		    			cancion = resultado.get(x);
		    			out.println(" <li>"+(x+1)+".- <b>Titulo</b> = '"+cancion.getTitulo()+"'  ---  <b>Descripción</b> = '"+cancion.getDescripcion()+"'  ---  <b>Premios</b> = '"+cancion.getPremios()+"'<BR>"); 
		    		}
	
	    		out.println("</ul>");
	
	    		out.println("<form>");
	    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
	    		out.println("<input type='hidden' name='pidd' value='"+pidd+"'>");
	    		out.println("<input type='hidden' name='panio' value='"+panio+"'>");
	    		out.println("<input type='hidden' name='pfase' value='0'>");
	
	    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"13\"'>");  // Atrás vuelve a la fase 13
	    		out.println("<input class='home' type='submit' value='Inicio' onClick='document.forms[0].pfase.value=\"01\"'>");
			
			   		// out.println("<input type='submit' value='Next' onClick='document.forms[0].pfase.value=\"15\"'>");
					
					
	    		out.println("</form>");
	    		
	    		CommonSINT.printFoot(out, CommonIML.CURSO);
	    		out.println("</body></html>");
	    	}
	    	else {
	    		out.println("<?xml version='1.0' encoding='utf-8'?>");
	    		out.println("<songs>");
	
	    		for (int x=0; x < resultado.size(); x++) {
	    			cancion = resultado.get(x);
	    			out.println("<song descripcion='"+cancion.getDescripcion()+"'  premios='"+cancion.getPremios()+"'>"+cancion.getTitulo()+"</song>");
	    		}
	
	    		out.println("</songs>"); 
	    	}
    }


    		
    // método auxiliar del anterior, que calcula la lista de canciones de duración menor a la indicada

    private static ArrayList<Cancion> getResult14 (String panio, String pidd, String pidc) {
	    	if (CommonIML.real == 0)
	    		return new ArrayList<Cancion>(Arrays.asList(new Cancion("ID1","T1", 200, "D1", "ge1", "pr1"), new Cancion("ID2","T2", 200, "D2", "ge2", "pr2"),
	    				new Cancion("ID3","T3", 200, "D3", "ge3", "pr3")));
	    	else {
	    		
	    		// primero conseguimos la canción seleccionada
	       		
	    		Element elemCancion = CommonIML.findSong(panio, pidd, pidc);
	    		
				String sduracion = CommonSINT.getTextContentOfChild(elemCancion, "Duracion");   //obtenemos la duración de la canción seleccionada
				int laDuracion = Integer.parseInt(sduracion);
				
				Element parentDisco = (Element)elemCancion.getParentNode();			
				String elInterprete = CommonSINT.getTextContentOfChild(parentDisco, "Interprete");   //obtenemos el intérprete de la canción seleccionada
				
				ArrayList<Cancion> cancionesInterprete = CommonIML.findSongsInterprete (elInterprete);
				
	    		// y ahora vamos con la búsqueda del resultado
	    			
	    		ArrayList<Cancion> listaCanciones = new ArrayList<Cancion>();
	       		
	    		Cancion canc;
	    		
	    		for (int x=0; x < cancionesInterprete.size(); x++) {
	    			canc = cancionesInterprete.get(x);
	    			if (canc.getDuracion() < laDuracion) listaCanciones.add(canc);
	    		}
    		
	    	
    		Collections.sort(listaCanciones, Collections.reverseOrder());  // ordenamos la lista alfabéticamente
    		return listaCanciones;
    	}
    }
    
    
    /* para el examen
     * 
     public static void doGetF15 (HttpServletRequest request, HttpServletResponse response) throws IOException {
	
	 		
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
	
		out.println("<html><head><meta charset='utf-8'/><title>"+Common.MSGTITLE+"</title></head><body>");
    		out.println("<h2>"+Common.MSGINICIAL+"</h2>");
    		
    		out.println("<h3>Los datos son:</h3>");



    		out.println("<form>");
    		out.println("<input type='hidden' name='p' value='"+Common.PASSWD+"'>");  

    		out.println("<input type='hidden' name='pfase' value='0'><br>");

    		out.println("<input class='home' type='submit' value='Inicio' onClick='document.forms[0].pfase.value=\"0\"'>");
    		out.println("</form>");
    		
    		CommonIML.printFoot(out);
    		out.println("</body></html>");
		
		
     }
    	*/	
  
}