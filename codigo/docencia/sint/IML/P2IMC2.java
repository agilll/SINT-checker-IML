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

// consulta 2
// discos en un idioma (S1), de un actor (S3), a partir de un género (S2)

package docencia.sint.IML;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import docencia.sint.Common.CommonSINT;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;


   // MÉTODOS PARA LA CONSULTA 2
   
public class P2IMC2 {

    // F21: método que imprime o devuelve la lista de idiomas
	 
    public static void doGetF21Langs (HttpServletRequest request, HttpServletResponse response) throws IOException {

	    ArrayList<String> langs = P2IMC2.getLangsF21();   // se pide la lista de idiomas
	
	    if (langs == null) {
	    		CommonSINT.doBadRequest("problema obteniendo los idiomas", request, response);
	    		return;
	    	}
	      	
	    	if (langs.size() == 0) {
	    		CommonSINT.doBadRequest("no hay idiomas", request, response);
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
                out.println("<h3>Consulta 2. Fase 21: lista de idiomas</h3>");
	
	    		out.println("<form>");
			    out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
	    		out.println("<input type='hidden' name='pfase' value='22'>");  // de aquí se pasa a la fase 22
	
	    		for (int x=0; x < langs.size(); x++) 
	    			out.println("<input type='radio' name='plang' value='"+langs.get(x)+"' checked> "+(x+1)+".- "+langs.get(x)+"<BR>"); 
			
	    		out.println("<h3>Seleccione un idioma</h3>");
	    		
	    		out.println("<p><input class='enviar' type='submit' value='Enviar'>");
	    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"01\"'>");  // Atrás vuelve al inicio
	    		out.println("</form>");

	    		
	    		CommonSINT.printFoot(out, CommonIML.CURSO);
	    		out.println("</body></html>");
	    	}
	    	else {
	    		out.println("<?xml version='1.0' encoding='utf-8'?>");
	    		out.println("<langs>");
	
	    		for (int x=0; x < langs.size(); x++) 
	    			out.println("<lang>"+langs.get(x).trim()+"</lang>");
	
	    		out.println("</langs>");
	    	}
    }


    // método auxiliar del anterior, que devuelve la lista de idiomas

    private static ArrayList<String> getLangsF21 () {	
	    if (CommonIML.real == 0)
	    		return new ArrayList<String>(Arrays.asList("us", "uk","es","de"));
	    else {
	    	ArrayList<String> listaLangs = new ArrayList<String>();
	    		
	        String targetLang = "/Songs/Pais/@lang";            // langs por defecto
	        String targetLangs = "/Songs/Pais/Disco/@langs";    // langs en los atributos langs de los discos
			Document doc;
			NodeList nlLangs=null;  	
			Attr attrLang, attrLangs;
			String idioma, listaIdiomas;
	       
			Collection<Document> collectionDocs = CommonIML.mapDocs.values();
			Iterator<Document> iter = collectionDocs.iterator();
			
			while (iter.hasNext()) {   // iteramos sobre todos los años
		
				doc = iter.next();
		
		    	try {  // obtenemos los atributos lang
		    			nlLangs = (NodeList)CommonIML.xpath.evaluate(targetLang, doc, XPathConstants.NODESET);
		    		}
				catch (XPathExpressionException ex) {CommonIML.logIML(ex.toString()); return null;}
		    		
				for (int z=0; z < nlLangs.getLength(); z++) {
					attrLang = (Attr)nlLangs.item(z);   // estudiamos cada lang
					idioma = attrLang.getValue();
					if (!listaLangs.contains(idioma)) listaLangs.add(idioma);
				}
				
				
		    	try {  // obtenemos los atributos langs
		    			nlLangs = (NodeList)CommonIML.xpath.evaluate(targetLangs, doc, XPathConstants.NODESET);
		    		}
				catch (XPathExpressionException ex) {CommonIML.logIML(ex.toString()); return null;}
		    		
				for (int z=0; z < nlLangs.getLength(); z++) {
					attrLangs = (Attr)nlLangs.item(z);   // estudiamos cada lang
					listaIdiomas = attrLangs.getValue();
					String[] idiomas = listaIdiomas.split(" "); 
					
					for (int i = 0; i < idiomas.length; i++){
						if (!listaLangs.contains(idiomas[i])) listaLangs.add(idiomas[i]);
					}
				}
								
			}
	    		
	    	Collections.sort(listaLangs);  // alfabéticamente 
	    	return listaLangs;
	    }
    }


    




    // F22: método que imprime o devuelve la lista de canciones en un determinado idioma (parámetro lang)

    public static void doGetF22Canciones (HttpServletRequest request, HttpServletResponse response) throws IOException {

		Cancion cancion;
		ArrayList<Cancion> canciones;

    	String plang = request.getParameter("plang");
    	if (plang == null) {
    		CommonSINT.doBadRequest("no param:plang", request, response);
    		return;
    	}

    	canciones = P2IMC2.getCancionesF22(plang);  // se pide la lista de canciones en el idioma seleccionado

    	if (canciones == null) {
    		CommonSINT.doBadRequest("problema leyendo las canciones en el idioma "+plang, request, response);
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
            out.println("<h3>Consulta 2. Fase 22: canciones en el idioma '"+plang+"'</h3>");

    		out.println("<form>");
    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
    		out.println("<input type='hidden' name='plang' value='"+plang+"'>");
    		out.println("<input type='hidden' name='pfase' value='23'>");  // de aquí se pasa a la fase 23


    		if (canciones.size() == 0)
    			out.println("No hay canciones en el idioma "+plang);
    		else
	    		for (int x=0; x < canciones.size(); x++) {
	    			cancion = canciones.get(x);
	    			
	    			out.println("<input type='radio' name='pgen' value='"+cancion.getGenero()+"' checked> "+(x+1)+
	    					".- <b>Título</b> = '"+cancion.getTitulo()+"'  --- <b>Género</b> = '"+cancion.getGenero()+"'  --- <b>Descripción</b> = '"+cancion.getDescripcion()+"' <BR>"); 
	    		}
    		out.println("<h3>Selecciona una canción:</h3>");
    		
    		out.println("<p><input class='enviar' type='submit' value='Enviar'>");
    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"21\"'>");  // Atrás vuelve a la fase 21
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
    		
    			out.println("<cancion idc ='"+cancion.getIDC()+"' genero='"+cancion.getGenero()+"' descripcion='"+cancion.getDescripcion()+"'>"+cancion.getTitulo()+"</cancion>");
    		}
    		
    		out.println("</canciones>");
    	}
    }

    

    // método auxiliar del anterior, que calcula la lista de canciones en un determinado idioma

    public static ArrayList<Cancion> getCancionesF22 (String lang) {
    	if (CommonIML.real == 0)
        	return new ArrayList<Cancion>(Arrays.asList(new Cancion("ID1","T1", 200, "D1", "ge1", "pr1"), new Cancion("ID2","T2", 200, "D2", "ge2", "pr2"),
	    			new Cancion("ID3","T3", 200, "D3", "ge3", "pr3")));
    	else {	
    		Document doc;
            NodeList nlCancion;
        	Element elCancion;
	    	String idc, titulo, sduracion, descripcion, genero;
	    	int duracion;
	    		
	    	ArrayList<Cancion> listaCanciones = new ArrayList<Cancion>();  // lista de canciones a devolver
        		
        	String target1 = "/Songs/Pais[@lang='"+lang+"']/Disco[not(@langs)]/Cancion"; // canciones en discos sin @langs de un país con lang por defecto 
        	String target2 = "/Songs/Pais/Disco[contains(@langs,'"+lang+"')]/Cancion";   // canciones en discos con lang entre sus langs
        		
			Collection<Document> collectionDocs = CommonIML.mapDocs.values();
			Iterator<Document> iter = collectionDocs.iterator();
			
			while (iter.hasNext()) {   // iteramos sobre todos los años
		
				doc = iter.next();
		
		    	try {  // obtenemos las canciones que nos interesan
		    		nlCancion = (NodeList)CommonIML.xpath.evaluate(target1+" | "+target2, doc, XPathConstants.NODESET);
		    		}
				catch (XPathExpressionException ex) {CommonIML.logIML(ex.toString()); return null;}
				catch (Exception ex) {CommonIML.logIML(ex.toString()); return null;}
		    		
				for (int z=0; z < nlCancion.getLength(); z++) {
					elCancion  = (Element)nlCancion.item(z);   // estudiamos cada canción
					
					idc = elCancion.getAttribute("idc");  // obtenemos su idc
					
	    			titulo = CommonSINT.getTextContentOfChild(elCancion, "Titulo");  // averiguamos el título de la canción
					
					sduracion = CommonSINT.getTextContentOfChild(elCancion, "Duracion");   // averiguamos la duración de la canción		
				    duracion = Integer.parseInt(sduracion);
					
					descripcion = CommonSINT.getTextContent(elCancion);										
					genero = CommonSINT.getTextContentOfChild(elCancion, "Genero");   //  averiguamos el genero de la canción
		
					listaCanciones.add(new Cancion(idc, titulo, duracion, descripcion, genero, ""));
				}
			}
			
    		Collections.sort(listaCanciones, Cancion.GENERO);  // alfabéticamente en orden inverso
	   	    return listaCanciones;
    	}		
    }







    // F23: método que imprime o devuelve la lista de intérpretes de canciones en un idioma y de un género

    public static void doGetF23Interpretes (HttpServletRequest request, HttpServletResponse response) throws IOException {
    		
	    	String plang = request.getParameter("plang");	
	    	if (plang == null) {
	    		CommonSINT.doBadRequest("no param:plang", request, response);
	    		return;
	    	}
	
	    	String pgen = request.getParameter("pgen");	
	    	if (pgen == null) {
	    		CommonSINT.doBadRequest("no param:pgen", request, response);
	    		return;
	    	}
	    	
	    	ArrayList<String> interpretes = P2IMC2.getInterpretesF23(plang, pgen);   // pedimos la lista de intérpretes de canciones en un idioma y de un género
	
	    	if (interpretes == null) {
	    		CommonSINT.doBadRequest("problema obteniendo los intérpretes con el idioma ("+plang+") y el género ("+pgen+")", request, response);
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
                out.println("<h3>Consulta 2. Fase 23: intérpretes de canciones en '"+plang+"' y del género '"+pgen+"'</h3>");
	
	    		out.println("<form>");
	    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
	    		out.println("<input type='hidden' name='plang' value='"+plang+"'>");
	    		out.println("<input type='hidden' name='pgen' value='"+pgen+"'>");
	    		out.println("<input type='hidden' name='pfase' value='24'>");  // de aquí se pasa a la fase 24
	
	    		for (int x=0; x < interpretes.size(); x++) {
	    			out.println("<input type='radio' name='pint' value='"+interpretes.get(x)+"' checked> "+(x+1)+".- "+
	    					interpretes.get(x)+"<BR>"); 
	    		}
	
	    		out.println("<h3>Selecciona un intérprete:</h3>");
	    		
	    		out.println("<p><input class='enviar' type='submit' value='Enviar'>");
	    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"22\"'>");  // Atrás vuelve a la fase 22
	    		out.println("<input class='home' type='submit' value='Inicio' onClick='document.forms[0].pfase.value=\"01\"'>");
	    		out.println("</form>");
	    		
	       		CommonSINT.printFoot(out, CommonIML.CURSO);
	    		out.println("</body></html>");
	    	}
	    	else {
	    		out.println("<?xml version='1.0' encoding='utf-8'?>");
	    		out.println("<interpretes>");
	
	    		for (int x=0; x < interpretes.size(); x++) 
	    			out.println("<interprete>"+interpretes.get(x)+"</interprete>");
	
	    		out.println("</interpretes>");
	    	}
    }



    // método auxiliar del anterior, que obtiene la lista de países de un idioma+actor

    public static ArrayList<String> getInterpretesF23 (String lang, String gen) {
	    if (CommonIML.real == 0)
	    		return new ArrayList<String>(Arrays.asList("Bruce Springsteen","Joaquín Sabina", "Mina"));
	    else {
	    	Document doc;
    		NodeList nlInterprete=null;
    		Element elInterprete;
    		String nombreInterprete;
    		
        	ArrayList<String> listaInterpretes = new ArrayList<String>();
        		
            String target1 = "/Songs/Pais[@lang='"+lang+"']/Disco[not(@langs)]/Interprete[../Cancion/Genero='"+gen+"']"; // intérprete en discos sin @langs de un país con lang por defecto 
            String target2 = "/Songs/Pais/Disco[contains(@langs,'"+lang+"')]/Interprete[../Cancion/Genero='"+gen+"']";   // intérprete en discos con lang entre sus langs
        		
			Collection<Document> collectionDocs = CommonIML.mapDocs.values();
			Iterator<Document> iter = collectionDocs.iterator();
			
			while (iter.hasNext()) {   // iteramos sobre todos los años
		
				doc = iter.next();
		
		    	try {  // obtenemos los intérpretes que nos interesan
		    			nlInterprete = (NodeList)CommonIML.xpath.evaluate(target1+" | "+target2, doc, XPathConstants.NODESET);
		    		}
				catch (XPathExpressionException ex) {CommonIML.logIML(ex.toString()); return null;}
				catch (Exception ex) {CommonIML.logIML(ex.toString()); return null;}
		    		
				for (int z=0; z < nlInterprete.getLength(); z++) {
					elInterprete  = (Element)nlInterprete.item(z);   // estudiamos cada intérprete
			
					nombreInterprete = CommonSINT.getTextContent(elInterprete);	
							
					if (!listaInterpretes.contains(nombreInterprete)) 
						listaInterpretes.add(nombreInterprete);
				}
			}
			
    		Collections.sort(listaInterpretes, Collections.reverseOrder());  // alfabéticamente en orden inverso
	   	    return listaInterpretes;
	    		
	    }
    }








    // F24: método que imprime los discos de un intérprete

    public static void doGetF24Discos (HttpServletRequest request, HttpServletResponse response) throws IOException {
		
    	Disco disco;
    	
    	String plang = request.getParameter("plang");
    	if (plang == null) {
    		CommonSINT.doBadRequest("no param:plang", request, response);
    		return;
    	}

    	String pgen = request.getParameter("pgen");	
    	if (pgen == null) {
    		CommonSINT.doBadRequest("no param:pgen", request, response);
    		return;
    	}

    	String pint = request.getParameter("pint");	
    	if (pint == null) {
    		CommonSINT.doBadRequest("no param:pint", request, response);
    		return;
    	}

    	ArrayList<Disco> discos = P2IMC2.getDiscosF24(plang, pgen, pint);

    	if (discos == null) {
    		CommonSINT.doBadRequest("problema obteniendo los discos con el idioma ("+plang+"), el género ("+pgen+"), y el intérprete ("+pint+")", request, response);
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
            out.println("<h3>Consulta 2. Fase 24: discos de canciones en '"+plang+"', del género '"+pgen+"' y con intérprete '"+pint+"'</h3>");
    		
    		out.println("<h3>Estos son los discos:</h3>");

    		out.println("<ul>");

     		if (discos.size() == 0)
    			out.println("No hay discos");
    		else
	    		for (int x=0; x < discos.size(); x++) {
	    			disco = discos.get(x);
	    		
	    			out.println("<li> <b>Título</b> = '"+disco.getTitulo()+"'  ---  <b>IDD</b> = '"+disco.getIDD()+"'  ---  <b>Idiomas</b> = '"+disco.getIdiomas()+"'<BR>"); 
	    		}
    		

    		out.println("</ul>");

    		out.println("<form>");
    		out.println("<input type='hidden' name='p' value='"+CommonSINT.PASSWD+"'>");  
    		out.println("<input type='hidden' name='plang' value='"+plang+"'>");
    		out.println("<input type='hidden' name='pgen' value='"+pgen+"'>");
    		out.println("<input type='hidden' name='pfase' value='01'>");

    		out.println("<input class='back' type='submit' value='Atrás' onClick='document.forms[0].pfase.value=\"23\"'>");  // Atrás vuelve a la fase 23
    		out.println("<input class='home' type='submit' value='Inicio' onClick='document.forms[0].pfase.value=\"01\"'>");
		
		    // out.println("<input type='submit' value='Next' onClick='document.forms[0].pfase.value=\"25\"'>");
				
    		out.println("</form>");
    		
       		CommonSINT.printFoot(out, CommonIML.CURSO);
    		out.println("</body></html>");
    	}
    	else {
    		out.println("<?xml version='1.0' encoding='utf-8'?>");
    		out.println("<discos>");

    		for (int x=0; x < discos.size(); x++) 
    			out.println("<disco idd='"+discos.get(x).getIDD()+"'  langs='"+discos.get(x).getIdiomas()+"'  >"+discos.get(x).getTitulo()+"</disco>");

    		out.println("</discos>"); 
    	}
    }


    		
    // método auxiliar del anterior, que calcula los discos de un intérprete, en un idioma y con canciones de un género

    public static  ArrayList<Disco> getDiscosF24 (String lang, String genero, String inter) {
	    if (CommonIML.real == 0)
	    		return new ArrayList<Disco>(Arrays.asList(new Disco("IDD1","D1", "I1","lang1", "pr1"), new Disco("IDD2","D2", "I2","lang2", "pr2"), new Disco ("IDD3","D3", "I3","lang3", "pr3")));
	    else {   		
	       	Document doc;
			NodeList nlDiscos=null, nlPremios, nlPremio;
			Element elDisco, elPais, elPremios, elPremio;
			String interprete, titulo, langPais, idd, langsDisco, langs, premios;
			
    		ArrayList<Disco> listaDiscos = new ArrayList<Disco>();
    		
            String target1 = "/Songs/Pais[@lang='"+lang+"']/Disco[(not(@langs)) and (Interprete='"+inter+"') and (Cancion/Genero='"+genero+"')]"; // intérprete en discos sin @langs de un país con lang por defecto 
            String target2 = "/Songs/Pais/Disco[(contains(@langs,'"+lang+"')) and (Interprete='"+inter+"') and (Cancion/Genero='"+genero+"')]";   // intérprete en discos con lang entre sus langs
        		
			Collection<Document> collectionDocs = CommonIML.mapDocs.values();
			Iterator<Document> iter = collectionDocs.iterator();
			
			while (iter.hasNext()) {   // iteramos sobre todos los años
		
				doc = iter.next();
		
		    	try {  // obtenemos los discos que nos interesan
		    			nlDiscos = (NodeList)CommonIML.xpath.evaluate(target1+" | "+target2, doc, XPathConstants.NODESET);
		    		}
				catch (XPathExpressionException ex) {CommonIML.logIML(ex.toString());}
				catch (Exception ex) {CommonIML.logIML(ex.toString());}
		    		
				for (int x=0; x < nlDiscos.getLength(); x++) {
					elDisco = (Element)nlDiscos.item(x);  // estudiamos un disco
	    			
	    			interprete = CommonSINT.getTextContentOfChild(elDisco, "Interprete");  // obtenemos el intérprete del disco
	    			titulo = CommonSINT.getTextContentOfChild(elDisco, "Titulo");  // obtenemos el título del disco
	    			
	    			elPais = (Element)elDisco.getParentNode();  // pedimos el País al que pertenece
	    			langPais = elPais.getAttribute("lang");   // leemos el idioma por defecto del país
	    			
	    			idd = elDisco.getAttribute("idd");	    			
	    			langsDisco = elDisco.getAttribute("langs");
	    			
	    			if (langsDisco.equals("")) langs = langPais;
	    			else langs = langsDisco;
	    			
	    			// vamos a leer los premios del disco 
	    			
	    			nlPremios = elDisco.getElementsByTagName("Premios");  
	    			
	    			if (nlPremios.getLength() > 0) {
	    				premios="";
    	    			elPremios = (Element)nlPremios.item(0);// obtenemos el elemento Premios del disco
    	    			
		    			nlPremio = elPremios.getElementsByTagName("Premio");  // obtenemos los elementos Premio del disco
		    			
	    				for (int z=0; z < nlPremio.getLength(); z++) {
			    			elPremio = (Element)nlPremio.item(z);   	    			
			    			
			    			if (premios.equals("")) premios = elPremio.getTextContent().trim();
			    			else premios = premios+", "+elPremio.getTextContent().trim();
	    				}
	    			}
	    			else premios="";
	    			
	    			listaDiscos.add(new Disco(idd, titulo, interprete, langs, premios));  // creamos y añadimos el disco
				}
			}
			
  			Collections.sort(listaDiscos);  // alfabéticamente en orden inverso
	   	    return listaDiscos;
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

    		out.println("<input type='hidden' name='fase' value='0'><br>");

    		out.println("<input  class='home' type='submit' value='Inicio' onClick='document.forms[0].fase.value=\"0\"'>");
    		out.println("</form>");
    		
    		CommonIML.printFoot(out);
    		out.println("</body></html>");
		
		
     }
    	*/	
  
}