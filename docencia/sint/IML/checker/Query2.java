package docencia.sint.IML.checker;
/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Checker de la práctica de IML
 *
 *    Autor: Alberto Gil Solla
 *    Curso : 2018-2019
 ****************************************************************/

// Implementación de la comprobación de la consulta 2 (discos de un intérprete S3 en un idioma S1, que contengan canciones del género de una canción S2)


import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;

import docencia.sint.Common.CheckerFailure;
import docencia.sint.Common.CommonSINT;
import docencia.sint.Common.ExcepcionChecker;
import docencia.sint.Common.ExcepcionSINT;
import docencia.sint.Common.SintRandom;
import docencia.sint.IML.CommonIML;
import docencia.sint.IML.Disco;
import docencia.sint.IML.Cancion;



public class Query2 {
		
		
	// nombres de los parámetros de esta consulta
		
	static final String PLANG = "plang";
	static final String PGEN = "pgen";
	static final String PINT = "pint";
	
	// COMPROBACIÓN DE LAS LLAMADAS A SINTPROF
	
	public static void doGetC2CheckSintprofCalls(HttpServletRequest request, PrintWriter out)
						throws IOException, ServletException
	{
		CheckerFailure cf; 
		int esProfesor = 1;  // sólo el profesor debería llegar aquí, podemos poner esto a 1
		
		out.println("<html>");
		CommonIMLChecker.printHead(out);
		CommonIMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 2</h2>");
		out.println("<h3>Comprobar las llamadas al servicio de sintprof</h3>");

		// doOneCheckUpStatus: hace una petición de estado al servicio del profesor para ver si está operativo

		try {
			CommonIMLChecker.doOneCheckUpStatus(request, "sintprof", CommonSINT.PASSWD);
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			out.println("<h4 style='color: red'>sintprof (error al preguntar por el estado): <br>");
			out.println(cf.toHTMLString());
			out.println("</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonIML.CREATED);
			return;
		}
		
		out.println("<h4>CheckStatus OK</h4>");
		

		
		
		// empezamos por pedir los errores
		
		try {
			CommonIMLChecker.requestErrores("sintprof", CommonIMLChecker.servicioProf, CommonSINT.PASSWD);	
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			out.println("<h4 style='color: red'>sintprof (ExcepcionChecker pidiendo Errores): <br>");
			out.println(cf.toHTMLString());
			out.println("</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonIML.CREATED);
			return;
        }
		catch (Exception ex) { 
			out.println("<h4 style='color: red'>sintprof (Exception pidiendo Errores): "+ex.toString()+"</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonIML.CREATED);
			return;
        }
		
		out.println("<h4>Errores OK</h4>");
		
		
		// y ahora todas y cada una de las consultas
		
		// pedimos la lista de idiomas de sintprof
	 
		String qs = "?auto=si&"+CommonSINT.PFASE+"=21&p="+CommonSINT.PASSWD;
		String call = CommonIMLChecker.servicioProf+qs;
		
		ArrayList<String> pLangs;
		try {
			pLangs = Query2.requestLangs(call);
			out.println("<h4>Idiomas OK: "+pLangs.size()+"</h4>");
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			out.println("<h4 style='color: red'>ExcepcionSINT: sintprof (Idiomas): <br>");
			out.println(cf.toHTMLString());
			out.println("</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonIML.CREATED);
			return;
        }
	
	
	    
		// vamos con la segunda fase, las canciones en un idioma
		// el bucle X recorre todos los idiomas
		
		
		String langActual;
	     
		for (int x=0; x < pLangs.size(); x++) {
			String indent ="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	    	
			langActual = pLangs.get(x);
			
			// pedimos las canciones de ese idioma de sintprof
	   	 
			qs = "?auto=si&"+CommonSINT.PFASE+"=22&"+PLANG+"="+langActual+"&p="+CommonSINT.PASSWD;
			call = CommonIMLChecker.servicioProf+qs;
			
			ArrayList<Cancion> pCanciones;
			try {
				pCanciones = requestCancionesLang(call);
				out.println("<h4>"+indent+langActual+": "+pCanciones.size()+"  OK</h4>");
			}
			catch (ExcepcionChecker e) { 
				cf = e.getCheckerFailure();
				out.println("<h4 style='color: red'>sintprof (Canciones): <br>");
				out.println(cf.toHTMLString());
				out.println("</h4>");
				CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonIML.CREATED);
				return;
	        }
						        

	        
	        
	        // vamos con la tercera fase, los intérpretes de canciones del mismo género que la seleccionada
	        // el bucle Y recorre todas las canciones
	        
	        Cancion cancionActual;
	        
	        for (int y=0; y < pCanciones.size(); y++) {
	        	
		        	indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		        	
		        	cancionActual = pCanciones.get(y);
		        	
		    		// pedimos los intérpretes de canciones del mismo género de sintprof
		       	 
		    		qs = "?auto=si&"+CommonSINT.PFASE+"=23&"+PLANG+"="+langActual+"&"+PGEN+"="+cancionActual.getGenero()+"&p="+CommonSINT.PASSWD;    // no es necesario URL-encoded
		    		call = CommonIMLChecker.servicioProf+qs;
		    		
		    		ArrayList<String> pInterpretes;
		    		try {
		    			pInterpretes = requestInterpretesCancion(call);
		    			out.println("<h4>"+indent+langActual+"+"+cancionActual.getGenero()+": "+pInterpretes.size()+"  OK</h4>");
		    		}
		    		catch (ExcepcionChecker e) { 
						cf = e.getCheckerFailure();
		    			out.println("<h4 style='color: red'>ExcepcionSINT: sintprof (Intérpretes): <br>");
						out.println(cf.toHTMLString());
		    			out.println("</h4>");
		    			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonIML.CREATED);
		    			return;
		            }
		    	            	          
		            
		            
	            // vamos con la cuarta fase, la lista de disco resultado 
	            // el bucle Z recorre todos los intérpretes
		            
	            	String interpreteActual;
	            	
	            	for (int z=0; z < pInterpretes.size(); z++) {
			        	indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			        	
			        	interpreteActual = pInterpretes.get(z);
		            			
		        		// pedimos la lista de canciones resultado de sintprof
		           	 
			        	try {
			        		qs = "?auto=si&"+CommonSINT.PFASE+"=24&"+PLANG+"="+langActual+"&"+PGEN+"="+cancionActual.getGenero()+"&"+PINT+"="+URLEncoder.encode(interpreteActual,"utf-8")+"&p="+CommonSINT.PASSWD;     // no es necesario URL-encoded
			        	}
	            	
	           	        catch (UnsupportedEncodingException ex) {
	           	           out.println("<h4 style='color: red'>UnsupportedEncodingException: utf-8 no soportado</h4> <br>");
	           	           CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonIML.CREATED);
	           	           return;
                        }
			    	    call = CommonIMLChecker.servicioProf+qs;
			    	    
			        	ArrayList<Disco> pDiscos;
			    		
		        		try {
		        			pDiscos = requestDiscosResult(call);
		        			out.println("<h4>"+indent+langActual+"+"+cancionActual.getGenero()+"+"+interpreteActual+": "+pDiscos.size()+"  OK</h4>");
		        		}
		        		catch (ExcepcionChecker e) { 
							cf = e.getCheckerFailure();
		        			out.println("<h4 style='color: red'>ExcepcionSINT: sintprof (Discos resultado): <br>");
							out.println(cf.toHTMLString());
		        			out.println("</h4>");
		        			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonIML.CREATED);
		        			return;
		                }	        		        		
		            
		        } // for z
	            
	        } // for y
	         
	    } // for x
	    
        try {
            call = "http://localhost:7000/sintprof/P2IM?auto=si&pfase=24&p=sp&pint="+URLEncoder.encode("Joaquín Sabin", "utf-8")+"&pgen=Country";
            System.out.println("sustituida: "+call);
            CommonIMLChecker.db.parse(call);
    }
    catch (SAXException e) {

		out.println("<h4>sintprof: SAXException</h4>");
    }
    catch (Exception e) {
		    out.println("<h4>sintprof: Exception</h4>"+e.toString());
    }

		out.println("<h4>sintprof: Todo OK</h4>");
			
		CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonIML.CREATED);
	}
	
	

	
	
	
	
	
	// COMPROBACIÓN DEL SERVICIO DE UN ÚNICO ESTUDIANTE

	// pantalla para ordenar comprobar un estudiante (se pide su número de login)
	// debería unificarse en uno sólo con el de la consulta 1, son casi iguales

	public static void doGetC2CorrectOneForm (HttpServletRequest request, PrintWriter out, int esProfesor)
						throws IOException
	{
		out.println("<html>");
		CommonIMLChecker.printHead(out);
		
		out.println("<script>");

		out.println("function stopEnter (event) {");
		out.println("var x = event.which;");	
		out.println("if (x === 13) {event.preventDefault();}");
		out.println("}");
		
		out.println("function hideservice () {");
		out.println("var serviceAluElem = document.getElementById('serviceAluInput');");	
		out.println("serviceAluElem.style.visibility='hidden';");
		out.println("var sendButton = document.getElementById('sendButton');");
		out.println("sendButton.disabled=true;");
		out.println("}");

		out.println("function showservice () {");
		out.println("var inputSintElement = document.getElementById('inputSint');");
		out.println("if ( ! inputSintElement.validity.valid ) return;");
		out.println("var inputSint = inputSintElement.value;");
		out.println("var sendButton = document.getElementById('sendButton');");
		out.println("sendButton.disabled=false;");

		out.println("var inputServiceElem = document.getElementById('serviceAluInput');");

		out.println("inputServiceElem.value = 'http://"+CommonIMLChecker.server_port+"/sint'+inputSint+'"+CommonIMLChecker.SERVICE_NAME+"';");	
		out.println("inputServiceElem.style.visibility='visible';");
		out.println("}");
		out.println("</script>");
	
		CommonIMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 2</h2>");                               // consulta 2
		out.println("<h3>Corrección de un único servicio</h3>");
		
		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='221'>");  // conduce a doGetC1CorrectOneReport

		out.println("Introduzca el número de la cuenta SINT a comprobar: ");
		out.println("<input id='inputSint' type='text' name='alumnoP' size='3' onfocus='hideservice();' onblur='showservice();' onkeypress='stopEnter(event);' pattern='[1-9]([0-9]{1,2})?' required> <br>");

		out.println("URL del servicio del alumno:");
		out.println("<input style='visibility: hidden' id='serviceAluInput' type='text' name='servicioAluP' value='' size='40'><br>");
		
		if (esProfesor == 0) {
			out.println("<p>Passwd de la cuenta (10 letras o números) <input id='passwdAlu' type='text' name='passwdAlu'  pattern='[A-Za-z0-9]{10}?' required> <br><br>");
		}
		else {
			out.println("<p><input type='hidden' name='p' value='si'>");	
		}

		out.println("<p><input class='enviar' id='sendButton' disabled='true' type='submit' value='Enviar'>");
		out.println("</form>");

		out.println("<form>");
		if (esProfesor == 1) 
			out.println("<p><input type='hidden' name='p' value='si'>");	
		out.println("<p><input class='home'  type='submit' value='Inicio'>");
		out.println("</form>");
		
		CommonSINT.printFoot(out, CommonIML.CREATED);

		out.println("</body></html>");
	}



	
	
	
	// pantalla para informar de la corrección de un sintX (se recibe en 'alumnoP' su número de login X)
	// también recibe en servicioAlu el URL del servicio del alumno

	public static void doGetC2CorrectOneReport(HttpServletRequest request, PrintWriter out, int esProfesor)
						throws IOException, ServletException
	{	
		out.println("<html>");
		CommonIMLChecker.printHead(out);
		CommonIMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 2</h2>");
		out.println("<h3>Corrección de un único servicio</h3>");
		
		// leemos los datos del estudiante
		
		String alumnoP = request.getParameter("alumnoP");
		String servicioAluP = request.getParameter("servicioAluP");
		
		if ((alumnoP == null) || (servicioAluP == null)) {
			out.println("<h4 style='color: red'>Falta uno de los parámetros</h4>");  // si falta algún parámetro no se hace nada
			CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonIML.CREATED);
			return; 
		}
		
		
		String usuario="sint"+alumnoP;
		String passwdAlu, passwdRcvd;


String fake = request.getParameter("f");
if (fake != null) {
			out.println("<h3>Comprobando el servicio del usuario "+usuario+" ("+servicioAluP+")</h3>");
			out.println("<h3 style='color: red'>Resultado incorrecto para la práctica de sint78<br>");  
			out.println("* URL = http://localhost:7000/sint78/P2IM?auto=si&pfase=21&p=maria12345<br>");
			out.println("* correctC1OneStudent: Diferencias en la lista de idiomas<br>");
			out.println("* comparaIdiomas: sint78: debería devolver 9 idiomas, pero devuelve 8</h3>");
			
			CommonSINT.printEndPageChecker(out,  "21", esProfesor, CommonIML.CREATED);
			return;
		}





		try {
			passwdAlu = CommonIMLChecker.getAluPasswd(usuario);
		}
		catch (ExcepcionSINT ex) {
			if (ex.getMessage().equals("NOCONTEXT"))
				out.println("<h4 style='color: red'>ExcepcionSINT: Todavía no se ha creado el contexto de "+usuario+"</h4>"); 
			else 
				out.println("<h4 style='color: red'>"+ex.getMessage()+": Imposible recuperar la passwd del contexto de "+usuario+"</h4>"); 
			CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonIML.CREATED);
			return; 
		}
		
		if (esProfesor == 0) {  // lo está probando un alumno, es obligatorio que haya introducido su passwd
			passwdRcvd = request.getParameter("passwdAlu");   // leemos la passwd del alumno del parámetro
			
			if (passwdRcvd == null) {
				out.println("<h4 style='color: red'>No se ha recibido la passwd de "+usuario+"</h4>"); 
				CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonIML.CREATED);
				return; 
			}
			
			if (!passwdAlu.equals(passwdRcvd)) {
				out.println("<h4 style='color: red'>La passwd proporcionada no coincide con la almacenada en el sistema para "+usuario+"</h4>"); 
				CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonIML.CREATED);
				return; 
			}
			
		}
		
		out.println("<h3>Comprobando el servicio del usuario "+usuario+" ("+servicioAluP+")</h3>");

		// doOneCheckUpStatus: hace una petición de estado al servicio del profesor para ver si está operativo

		
		try {
			CommonIMLChecker.doOneCheckUpStatus(request, "sintprof", CommonSINT.PASSWD);
		}
		catch (ExcepcionChecker e) {
			CheckerFailure cf = e.getCheckerFailure();
			out.println("<h4 style='color: red'>Error al preguntar por el estado de la práctica del profesor: <br>");
			out.println(cf.toHTMLString());
			out.println("</h4>");
			CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonIML.CREATED);
			return;
		}
		
		
		try {
			Query2.correctC2OneStudent(request, usuario, alumnoP, servicioAluP, passwdAlu);
		}
		catch (ExcepcionChecker e) {
			CheckerFailure cf = e.getCheckerFailure();
			out.println("<h4 style='color: red'>Resultado incorrecto para la práctica de "+usuario+" <br>");
			out.println(cf.toHTMLString());
			out.println("</h4>");
			CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonIML.CREATED);
			return;
		}
		
		out.println("<h3>Resultado: OK</h3>");

	
	
		// terminamos con botones Atrás e Inicio

		CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonIML.CREATED);
	}
	
	
	
	
	// método que corrige la consulta 2 de un estudiante

    private static void correctC2OneStudent (HttpServletRequest request,String usuario, String aluNum, String servicioAluP, String passwdAlu) 
    			throws ExcepcionChecker
	{
    	CheckerFailure cf;
		
		// para la consulta directa final, vamos a escoger lang, genero e intérprete al azar y a guardarlas en esas variables
		
		SintRandom.init(); // inicializamos el generador de números aleatorios
		int posrandom;
		
		String dqLang="";
		String dqGen="";
		String dqInt="";
		
		// empezamos por comprobar la ubicación de los ficheros
		
		try {
			CommonIMLChecker.doOneCheckUpFiles(aluNum);  
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			cf.addMotivo("Error en la ubicación de los ficheros");
			throw new ExcepcionChecker(cf);
		}
		

        // ahora comprobamos que el servicio está operativo
		
		try {
			CommonIMLChecker.doOneCheckUpStatus(request, usuario, passwdAlu);
		}
		catch (ExcepcionChecker e) {
			throw e;
		}
		
	
		

		
		// ahora comprobamos los errores
		
		try {
			CommonIMLChecker.comparaErrores(usuario, servicioAluP, passwdAlu);
		}
		catch (ExcepcionChecker e) {
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo("correctC2OneStudent: Diferencias en la lista de errores");
			throw new ExcepcionChecker(cf);
		}

		
		
		
		// y ahora todas y cada una de las consultas
		
		// pedimos la lista de idiomas de sintprof
	 
		String qs = "?auto=si&"+CommonSINT.PFASE+"=21&p="+CommonSINT.PASSWD;
		String call = CommonIMLChecker.servicioProf+qs;
		
		ArrayList<String> pLangs;
		try {
			pLangs = Query2.requestLangs(call);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo("correctC2OneStudent: ExcepcionChecker solicitando la lista de idiomas a sintprof");
			throw new ExcepcionChecker(cf);
		}
	
	
		// pedimos la lista de idiomas del sintX
		
		qs = "?auto=si&"+CommonSINT.PFASE+"=21&p="+passwdAlu;
		call = servicioAluP+qs;
		
		ArrayList<String> xLangs;
		try {
			xLangs = Query2.requestLangs(call);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo("correctC2OneStudent: ExcepcionChecker solicitando la lista de idiomas a "+usuario);
			throw new ExcepcionChecker(cf);
		}
	
		
		// comparamos las listas de sintprof y sintX
		
		try {
		     Query2.comparaLangs(usuario, pLangs, xLangs);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setUrl(call);
			cf.addMotivo("correctC2OneStudent: Diferencias en la lista de idiomas ");
			throw new ExcepcionChecker(cf);
		}
		
		
	    
		// las listas de idiomas son iguales
		// elegimos un idioma al azar para la consulta directa final
	    
		posrandom = SintRandom.getRandomNumber(0, pLangs.size()-1);
		dqLang = pLangs.get(posrandom);
	    
		
		
	    
		// vamos con la segunda fase, las canciones de cada idioma
		// el bucle X recorre todos los idiomas
		
		
		String langActual;
	     
		for (int x=0; x < pLangs.size(); x++) {
	    	
			langActual = pLangs.get(x);
			
			// pedimos las canciones de ese idioma de sintprof
	   	 
			qs = "?auto=si&"+CommonSINT.PFASE+"=22&"+PLANG+"="+langActual+"&p="+CommonSINT.PASSWD;
			call = CommonIMLChecker.servicioProf+qs;
			
			ArrayList<Cancion> pCanciones;
			try {
				pCanciones = requestCancionesLang(call);
			}
			catch (ExcepcionChecker e) { 
				cf = e.getCheckerFailure();
				cf.setCodigo("20_DIFS");
				cf.addMotivo("correctC2OneStudent: ExcepcionChecker solicitando la lista de canciones a sintprof");
				throw new ExcepcionChecker(cf);
			}
		
			
			// pedimos las canciones de ese idioma del sintX
			
			qs = "?auto=si&"+CommonSINT.PFASE+"=22&"+PLANG+"="+langActual+"&p="+passwdAlu;
			call = servicioAluP+qs;
			
			ArrayList<Cancion> xCanciones;
			try {
				xCanciones = requestCancionesLang(call);
			}
			catch (ExcepcionChecker e) { 
				cf = e.getCheckerFailure();
				cf.setCodigo("20_DIFS");
				cf.addMotivo("correctC2OneStudent: Excepción solicitando la lista de canciones de "+langActual+" a "+usuario);
				throw new ExcepcionChecker(cf);
			}			
			
			
			// comparamos las listas de sintprof y sintX
			
			try {
				Query2.comparaCanciones(usuario, langActual, pCanciones, xCanciones);
			}
			catch (ExcepcionChecker e) { 
				cf = e.getCheckerFailure();
				cf.setUrl(call);
				cf.addMotivo("correctC2OneStudent: Diferencias en la lista de canciones de '"+langActual+"'");
				throw new ExcepcionChecker(cf);
			}
			
	       	        
	        
	        // las listas de canciones para este idioma son iguales
		    // si este idioma es el de la consulta directa final, elegimos ua canción al azar para la consulta directa final
	        
	        
	        if (langActual.equals(dqLang)) {
	            posrandom = SintRandom.getRandomNumber(0, pCanciones.size()-1);
	            Cancion pCan = pCanciones.get(posrandom);
	          	dqGen = pCan.getGenero();
	        }
	        
	        
	        
	        // vamos con la tercera fase, los intérpretes de canciones del mismo género
	        // el bucle Y recorre todas las canciones
	        
	        Cancion canActual;
	        
	        for (int y=0; y < pCanciones.size(); y++) {
	        	
	        	canActual = pCanciones.get(y);
		        	
	    		// pedimos los intérpretes a sintprof
	       	 
	    		qs = "?auto=si&"+CommonSINT.PFASE+"=23&"+PLANG+"="+langActual+"&"+PGEN+"="+canActual.getGenero()+"&p="+CommonSINT.PASSWD;    // no es necesario URL-encoded
	    		call = CommonIMLChecker.servicioProf+qs;
	    		
	    		ArrayList<String> pInterpretes;
	    		try {
	    			pInterpretes = requestInterpretesCancion(call);
	    		}
	    		catch (ExcepcionChecker e) { 
	    			cf = e.getCheckerFailure();
					cf.setCodigo("20_DIFS");
					cf.addMotivo("correctC2OneStudent: ExcepcionChecker solicitando la lista de intérpretes a sintprof");
					throw new ExcepcionChecker(cf);
	    		}
	    		
	    		
	    		// pedimos los intérpretes a sintX
	    		
	    		qs = "?auto=si&"+CommonSINT.PFASE+"=23&"+PLANG+"="+langActual+"&"+PGEN+"="+canActual.getGenero()+"&p="+passwdAlu;    // no es necesario URL-encoded
	    		call = servicioAluP+qs;
	    		
	    		ArrayList<String> xInterpretes;
	    		try {
	    			xInterpretes = requestInterpretesCancion(call);
	    		}
	    		catch (ExcepcionChecker e) { 
	    			cf = e.getCheckerFailure();
					cf.setCodigo("20_DIFS");
					cf.addMotivo("correctC2OneStudent: ExcepcionChecker solicitando la lista de intérpretes a "+usuario);
					throw new ExcepcionChecker(cf);
	    		}
	    		
	    		
	    		// comparamos las listas de sintprof y sintX
		    			
	    		try {
	    			Query2.comparaInterpretes(usuario, langActual, canActual.getGenero(), pInterpretes, xInterpretes);
				}
				catch (ExcepcionChecker e) { 
					cf = e.getCheckerFailure();
					cf.setUrl(call);
					cf.addMotivo("correctC2OneStudent: Diferencias en la lista de intérpretes");
					throw new ExcepcionChecker(cf);
				}
	            
	            
	            // las listas de intérpretes  son iguales
	            // si este intérprete es la de la consulta directa final, elegimos un intérprete al azar para la consulta directa final
	            
	            if ( (langActual.equals(dqLang)) && (canActual.getGenero().equals(dqGen))) {
		            posrandom = SintRandom.getRandomNumber(0, pInterpretes.size()-1);
		        	    dqInt = pInterpretes.get(posrandom);
		        }
	            
	            
	            // vamos con la cuarta fase, la lista de discos resultado
	            // el bucle Z recorre todos los intérpretes
		            
	            String interpreteActual;
	            	
		        for (int z=0; z < pInterpretes.size(); z++) {
	            	
		        	interpreteActual = pInterpretes.get(z);
		        	
	        		// pedimos la lista de discos resultado de sintprof
	           	    try {
	           	    	qs = "?auto=si&"+CommonSINT.PFASE+"=24&"+PLANG+"="+langActual+"&"+PGEN+"="+canActual.getGenero()+"&"+PINT+"="+URLEncoder.encode(interpreteActual,"utf-8")+"&p="+CommonSINT.PASSWD;     // no es necesario URL-encoded
	           	    }
	           	    catch (UnsupportedEncodingException ex) {
	           	    	cf = new CheckerFailure("", "", "utf-8 no soportado"); 
	        			throw new ExcepcionChecker(cf);
                   }
	           	    
		    	    call = CommonIMLChecker.servicioProf+qs;
		    	    
	        	    ArrayList<Disco> pDiscos;
	        		
	        		try {
	        			pDiscos = requestDiscosResult(call);
	        		}
	        		catch (ExcepcionChecker e) { 
						cf = e.getCheckerFailure();
						cf.setCodigo("20_DIFS");
						cf.addMotivo("correctC2OneStudent: ExcepcionChecker solicitando la lista de discos resultado a sintprof");
						throw new ExcepcionChecker(cf);
	        		}
	        		
	        		
	        		// pedimos la filmografía de ese actor del sintX
	        		
	        		try {
		    		    qs = "?auto=si&"+CommonSINT.PFASE+"=24&"+PLANG+"="+langActual+"&"+PGEN+"="+canActual.getGenero()+"&"+PINT+"="+URLEncoder.encode(interpreteActual,"utf-8")+"&p="+passwdAlu;     // no es necesario URL-encoded
	        		}
	           	    catch (UnsupportedEncodingException ex) {
	           	    	cf = new CheckerFailure("", "", "utf-8 no soportado"); 
	        			throw new ExcepcionChecker(cf);
                    }
	        		
		    	    call = servicioAluP+qs;
		    	    
	        		ArrayList<Disco> xDiscos;
	        		try {
	        			xDiscos = requestDiscosResult(call);
	        		}
	        		catch (ExcepcionChecker e) { 
						cf = e.getCheckerFailure();
						cf.setCodigo("20_DIFS");
						cf.addMotivo("correctC2OneStudent: ExcepcionChecker solicitando la lista de discos resultado a "+usuario);
						throw new ExcepcionChecker(cf);
	        		}
	        			
	        		
	        		// comparamos lo discos de sintprof y sintX
		        		
	        		try {
	        			Query2.comparaDiscos(usuario, langActual, canActual.getGenero(), interpreteActual, pDiscos, xDiscos);
					}
					catch (ExcepcionChecker e) { 
						cf = e.getCheckerFailure();
						cf.setUrl(call);
						cf.addMotivo("correctC2OneStudent: Diferencias en la lista de discos resultado");
						throw new ExcepcionChecker(cf);
					}
	            
	            } // for z
	            
	        } // for y
	         
	    } // for x
	    
	    
		// finalmente la consulta directa
		
		try {
			Query2.checkDirectQueryC2(CommonIMLChecker.servicioProf, usuario, servicioAluP, dqLang, dqGen, dqInt, passwdAlu);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo("correctC2OneStudent: Resultado erróneo en la consulta directa");
			throw new ExcepcionChecker(cf);
		}
		
		// todas las consultas coincidieron
		
	    return;
	}
	
	
	

	
	

	
	
	// comprueba que las consultas directas son iguales   
	
	private static void checkDirectQueryC2(String servicioProf, String usuario, String servicioAluP, String lang, String gen, String inter, String passwdAlu) 
		throws ExcepcionChecker
	{
		CheckerFailure cf;
		ArrayList<Disco> pDiscos, xDiscos;
        String qs;
			
		// primero comprobamos que responde con el error apropiado si falta algún parámetro
		
  		try {
  			CommonIMLChecker.checkLackParam(servicioAluP, passwdAlu, "24", PLANG, lang, PGEN, gen, PINT, inter);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo("checkDirectQueryC2: No responde correctamente si falta algún parámetro obligatorio");
			throw new ExcepcionChecker(cf);
		}
  		
  		
 		// ahora comprobamos que los resultados son correctos
  		
  		try {
  			qs = "?auto=si&"+CommonSINT.PFASE+"=24&"+PLANG+"="+lang+"&"+PGEN+"="+gen+"&"+PINT+"="+URLEncoder.encode(inter, "utf-8")+"&p="+CommonSINT.PASSWD;  
  		}
  		catch (UnsupportedEncodingException ex) {
                   cf = new CheckerFailure("", "", "utf-8 no soportado");
                           throw new ExcepcionChecker(cf);
        }
  		
	    String call = CommonIMLChecker.servicioProf+qs;
	    
   		try {
   			pDiscos = Query2.requestDiscosResult(call);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo("checkDirectQueryC2: ExcepcionChecker al pedir la consulta directa a sintprof");
			throw new ExcepcionChecker(cf);
		}
   		
   		try {
   			qs = "?auto=si&"+CommonSINT.PFASE+"=24&"+PLANG+"="+lang+"&"+PGEN+"="+gen+"&"+PINT+"="+URLEncoder.encode(inter, "utf-8")+"&p="+passwdAlu;  
   		}
   		catch (UnsupportedEncodingException ex) {
                cf = new CheckerFailure("", "", "utf-8 no soportado");
                 throw new ExcepcionChecker(cf);
        }
   	  		
	    call = servicioAluP+qs;
	    
  		try {
  			xDiscos = Query2.requestDiscosResult(call);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo("checkDirectQueryC2: ExcepcionChecker al pedir la consulta directa a "+usuario);
			throw new ExcepcionChecker(cf);
		}
   		
   		
		// comparamos las listas de discos resultado de sintprof y sintX
		
		try {
			Query2.comparaDiscos(usuario, lang, gen, inter, pDiscos, xDiscos);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setUrl(call);
			cf.addMotivo("checkDirectQueryC2: Diferencias en la lista de discos resultado");
			throw new ExcepcionChecker(cf);
		}
  	
	
		// todo coincidió
		
		return;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// COMPROBACIÓN DEL SERVICIO DE TODOS LOS ESTUDIANTES

	// pantalla para comprobar todos los estudiantes, se pide el número de cuentas a comprobar (corregir su práctica)

	public static void doGetC2CorrectAllForm (HttpServletRequest request, PrintWriter out)
	{
		int esProfesor = 1;
		
		out.println("<html>");
		CommonIMLChecker.printHead(out);
		CommonIMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 2</h2>");
		out.println("<h3>Corrección de todos los servicios</h3>");

		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='231'>");

		out.println("Introduzca el número de cuentas SINT a corregir: ");
		out.println("<input id='inputNumCuentas' type='text' name='numCuentasP' size='3' pattern='[1-9]([0-9]{1,2})?' required>");

		if (esProfesor == 1) 
			out.println("<p><input type='hidden' name='p' value='si'>");	
		
		out.println("<input class='enviar' type='submit' value='Enviar' >");   
		out.println("</form>");

		out.println("<form>");
		if (esProfesor == 1) 
			out.println("<p><input type='hidden' name='p' value='si'>");	
		out.println("<p><input class='home' type='submit' value='Inicio'>");
		out.println("</form>");

		CommonSINT.printFoot(out, CommonIML.CREATED);
		
		out.println("</body></html>");
	}



	
	// pantalla para corregir a todos los estudiantes
	// presenta en pantalla diversas listas según el resultado de cada alumno
	// se crea un fichero con el resultado de cada corrección (webapps/CORRECCIONES/sintX/fecha-corrección)  
	// se devuelven enlaces a esos ficheros
		
	public static void doGetC2CorrectAllReport(HttpServletRequest request, PrintWriter out)
						throws IOException, ServletException
	{
		int esProfesor = 1;
		
		out.println("<html>");
		CommonIMLChecker.printHead(out);
		CommonIMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 2</h2>");

		// si no se recibe el parámetro con el número de cuentas no se hace nada

		String numCuentasP = request.getParameter("numCuentasP");
		if (numCuentasP == null) {
			out.println("<h4>Error: no se ha recibido el número de cuentas</h4>");
			CommonSINT.printEndPageChecker(out,  "23", esProfesor, CommonIML.CREATED);
			return;
		}

		int numCuentas=0;

		try {
			numCuentas = Integer.parseInt(numCuentasP);
		}
		catch (NumberFormatException e) {
			out.println("<h4>Error: el número de cuentas recibido no es un número válido</h4>");
			CommonSINT.printEndPageChecker(out,  "23", esProfesor, CommonIML.CREATED);
			return;
		}

		if (numCuentas < 1) {  
			out.println("<h4>Error: el número de cuentas recibido es menor que uno</h4>");
			CommonSINT.printEndPageChecker(out,  "23", esProfesor, CommonIML.CREATED);
			return;
		}

		
		
		String fake = request.getParameter("f");
		if (fake != null) {
			out.println("<h3>Corrección de todos los usuarios (100)</h3>");
			out.println("<h3 style='color: green'>Servicios OK (6): <span style='color: blue'>  <u>12</u> <u>20</u> <u>37</u> <u>51</u>  <u>70</u> <u>89</u>  </span><h3>");
			out.println("<h3 style='color: red'>Servicios con diferencias respecto a los resultados esperados (3): <span style='color: blue'> <u>14</u> <u>39</u> <u>78</u></span> <h3>");
			out.println("<h3 style='color: red'>Servicios con con respuesta mal formada a la solicitud de estado (2): <span style='color: blue'>  <u>33</u> <u>60</u>  </span> <h3>");
			
			CommonSINT.printEndPageChecker(out,  "22", esProfesor, CommonIML.CREATED);
			return;
		}
		
		
		
		
		// todos los parámetros están bien


		out.println("<h3>Corrección de todos los servicios ("+numCuentas+")</h3>");
		

		// listas para almacenar en qué caso está cada alumno

		ArrayList<Integer> usersOK = new ArrayList<Integer>();	   // corrección OK

		ArrayList<Integer> usersE1NoContext= new ArrayList<Integer>();       // contexto no existe

		ArrayList<Integer> usersE2FileNotFound= new ArrayList<Integer>();    // servlet no declarado
		ArrayList<Integer> usersE3Encoding = new ArrayList<Integer>();       // respuesta mala codificación
		ArrayList<Integer> usersE4IOException= new ArrayList<Integer>();    // falta clase servlet o este produce una excepción
		ArrayList<Integer> usersE5Bf = new ArrayList<Integer>();            // respuesta mal formada
		ArrayList<Integer> usersE6Invalid = new ArrayList<Integer>();       // respuesta inválida
		ArrayList<Integer> usersE7Error = new ArrayList<Integer>();         // error desconocido
		ArrayList<Integer> usersE8OkNoPasswd = new ArrayList<Integer>();    // responde bien sin necesidad de passwd
		ArrayList<Integer> usersE9BadPasswd = new ArrayList<Integer>();    // la passwd es incorrecta
		ArrayList<Integer> usersE10BadAnswer = new ArrayList<Integer>();    // la respuesta no es la esperada
		ArrayList<Integer> usersE11NoPasswd = new ArrayList<Integer>();    // el usuario no tiene passwd
		ArrayList<Integer> usersE12Files = new ArrayList<Integer>();    // el usuario no tiene passwd
		
		ArrayList<Integer> usersE20Diff = new ArrayList<Integer>();	   // las peticiones del alumno tienen diferencias respecto a las del profesor

		String servicioAlu;

		// lista para almacenar el nombre del fichero de cada cuenta

		ArrayList<String> usersCompareResultFile = new ArrayList<String>();

		// variables para crear y escribir los ficheros
		File  folder, fileUser;
		BufferedWriter bw;
		Date fecha;

		// si no existe, se crea el directorio de las CORRECCIONES

		String correccionesPath = CommonIMLChecker.servletContextSintProf.getRealPath("/")+"CORRECCIONES";
		
		folder = new File(correccionesPath);
		if (!folder.exists()) 
			folder.mkdir();

		// vamos a por las cuentas, de una en una

		String sintUser;

		bucle:	
		for (int x=1; x <= numCuentas; x++) {

			sintUser="sint"+x;

			// si no existe, se crea el directorio del alumno

			folder = new File(correccionesPath+"/"+sintUser);
			if (!folder.exists()) 
				folder.mkdir();

			// se crea el fichero donde se almacenará esta corrección

			fecha = new Date();
			String nombreFicheroCorreccion = correccionesPath+"/"+sintUser+"/"+fecha.toString();
			fileUser = new File(nombreFicheroCorreccion);
			usersCompareResultFile.add(nombreFicheroCorreccion);
			bw = new BufferedWriter(new FileWriter(fileUser));


			// Comienza la comprobación del alumno
			
			
			// leemos la passwd del alumno
			
		        String passwdAlu;
			
			try {
				passwdAlu = CommonIMLChecker.getAluPasswd(sintUser);
			}
			catch (ExcepcionSINT ex) {
				if (ex.getMessage().equals("NOCONTEXT")) {
					bw.write("No hay contexto");
					usersE1NoContext.add(x);
				}
				else {
					bw.write("No hay passwd");
					usersE11NoPasswd.add(x);
				}
				bw.close();
				continue bucle; 
			}
			

			servicioAlu = "http://"+CommonIMLChecker.server_port+"/"+sintUser+CommonIMLChecker.SERVICE_NAME;

			try {
				Query2.correctC2OneStudent(request, sintUser, Integer.toString(x), servicioAlu, passwdAlu);
				bw.write("OK");
				bw.close();
				usersOK.add(x);
			}
			catch (ExcepcionChecker e) {
				CheckerFailure cf = e.getCheckerFailure();
				
			    switch (cf.getCodigo()) {

				case "01_NOCONTEXT":   // el contexto no está declarado o no existe su directorio
					bw.write(cf.toString());
					bw.close();
					usersE1NoContext.add(x);
					continue bucle;
				case "02_FILENOTFOUND":   // el servlet no está declarado.
					bw.write(cf.toString());
					bw.close();
					usersE2FileNotFound.add(x);
					continue bucle;
				case "03_ENCODING":   // la secuencia de bytes recibida UTF-8 está malformada
					bw.write(cf.toString());
					bw.close();
					usersE3Encoding.add(x);
					continue bucle;
				case "04_IOEXCEPTION":    // la clase del servlet no está o produjo una excepción
					bw.write(cf.toString());
					bw.close();
					usersE4IOException.add(x);
					continue bucle;
				case "05_BF":   // la respuesta no es well-formed
					bw.write(cf.toString());
					bw.close();
					usersE5Bf.add(x);
					continue bucle;
				case "06_INVALID":   // la respuesta es inválida
					bw.write(cf.toString());
					bw.close();
					usersE6Invalid.add(x);
					continue bucle;
				case "07_ERRORUNKNOWN":   // error desconocido
					bw.write(cf.toString());
					bw.close();
					usersE7Error.add(x);
					continue bucle;
				case "08_OKNOPASSWD":   // responde bien incluso sin passwd
					bw.write(cf.toString());
					bw.close();
					usersE8OkNoPasswd.add(x);
					continue bucle;
				case "09_BADPASSWD":   // la passwd es incorrecta
					bw.write(cf.toString());
					bw.close();
					usersE9BadPasswd.add(x);
					continue bucle;
				case "10_BADANSWER":   // la respuesta es inesperada
					bw.write(cf.toString());
					bw.close();
					usersE10BadAnswer.add(x);
					continue bucle;
			    case "12_FILES":
					bw.write(cf.toString());
					bw.close();
					usersE12Files.add(x);
					continue bucle;
				case "20_DIFS":
					bw.write(cf.toString());
					bw.close();
					usersE20Diff.add(x);
					continue bucle;
				default:      // error desconocido
					bw.write("Respuesta desconocida de la corrección:\n"+cf.toString());
					bw.close();
					usersE7Error.add(x);
					continue bucle;
			   } // switch
			}
		} // for

		// Breve resumen de los resultados por pantalla, con enlaces a los ficheros

		int numAlu;
		String fileAlu;

		if (usersOK.size() >0) {
			out.print("<h4 style='color: green'>Servicios OK ("+usersOK.size()+"): ");
			for (int x=0; x < usersOK.size(); x++) {
				numAlu = usersOK.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}
		
		if (usersE12Files.size() >0) { 	
			out.print("<h4 style='color: red'>Servicios que tienen ficheros mal ubicados ("+usersE12Files.size()+"): ");
			for (int x=0; x < usersE12Files.size(); x++) {
				numAlu = usersE12Files.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE20Diff.size() >0) { 	
			out.print("<h4 style='color: red'>Servicios con diferencias respecto a los resultados esperados ("+usersE20Diff.size()+"): ");
			for (int x=0; x < usersE20Diff.size(); x++) {
				numAlu = usersE20Diff.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}
		
		if (usersE10BadAnswer.size() >0) { 	
			out.print("<h4 style='color: red'>Servicios que responden de forma inesperada ("+usersE10BadAnswer.size()+"): ");
			for (int x=0; x < usersE10BadAnswer.size(); x++) {
				numAlu = usersE10BadAnswer.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}
		
		if (usersE9BadPasswd.size() >0) { 	
			out.print("<h4 style='color: red'>Servicios que no reconocen la passwd ("+usersE9BadPasswd.size()+"): ");
			for (int x=0; x < usersE9BadPasswd.size(); x++) {
				numAlu = usersE9BadPasswd.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}
		
		if (usersE8OkNoPasswd.size() >0) { 	
			out.print("<h4 style='color: red'>Servicios que responden bien sin necesidad de passwd ("+usersE8OkNoPasswd.size()+"): ");
			for (int x=0; x < usersE8OkNoPasswd.size(); x++) {
				numAlu = usersE8OkNoPasswd.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE6Invalid.size() >0) { 	
			out.print("<h4 style='color: red'>Servicios con respuesta inválida a la solicitud de estado ("+usersE6Invalid.size()+"): ");
			for (int x=0; x < usersE6Invalid.size(); x++) {
				numAlu = usersE6Invalid.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE5Bf.size() >0) { 	
			out.print("<h4 style='color: red'>Servicios con respuesta mal formada a la solicitud de estado ("+usersE5Bf.size()+"): ");
			for (int x=0; x < usersE5Bf.size(); x++) {
				numAlu = usersE5Bf.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE4IOException.size() >0) { 	
			out.print("<h4 style='color: red'>Servicios donde falta la clase del servlet o éste produjo una excepción ("+usersE4IOException.size()+"): ");
			for (int x=0; x < usersE4IOException.size(); x++) {
				numAlu = usersE4IOException.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE3Encoding.size() >0) { 	
			out.print("<h4 style='color: red'>Servicios que responden con una codificación incorrecta a la solicitud de estado ("+usersE3Encoding.size()+"): ");
			for (int x=0; x < usersE3Encoding.size(); x++) {
				numAlu = usersE3Encoding.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE2FileNotFound.size() >0) { 	
			out.print("<h4 style='color: red'>Servicios sin el servlet declarado ("+usersE2FileNotFound.size()+"): ");
			for (int x=0; x < usersE2FileNotFound.size(); x++) {
				numAlu = usersE2FileNotFound.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE11NoPasswd.size() >0) { 	
			out.print("<h4 style='color: red'>Servicios que no tienen passwd ("+usersE11NoPasswd.size()+"): ");
			for (int x=0; x < usersE11NoPasswd.size(); x++) {
				numAlu = usersE11NoPasswd.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}
		
		if (usersE1NoContext.size() >0) { 	
			out.print("<h4 style='color: red'>Servicios sin contexto ("+usersE1NoContext.size()+"): ");
			for (int x=0; x < usersE1NoContext.size(); x++) {
				numAlu = usersE1NoContext.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		if (usersE7Error.size() >0) { 	
			out.print("<h4 style='color: red'>Servicios con algún error desconocido ("+usersE7Error.size()+"): ");
			for (int x=0; x < usersE7Error.size(); x++) {
				numAlu = usersE7Error.get(x);
				fileAlu = usersCompareResultFile.get(numAlu-1);
				out.print("<a href='?screenP=4&file="+fileAlu+"'>"+numAlu+"</a> ");
			}
			out.print("</h4>");
		}

		CommonSINT.printEndPageChecker(out,  "23", esProfesor, CommonIML.CREATED);
	}

	
	

	
	// Métodos auxiliares para la correción de un alumno de la consulta 2
	
	
	// pide y devuelve la lista de idiomas de un usuario
	// levanta ExcepcionChecker si algo va mal
	
	private static ArrayList<String> requestLangs (String call) 
									throws ExcepcionChecker  
	{
		CheckerFailure cf;
		Document doc;
		ArrayList<String> listaLangs = new ArrayList<String>();
		
		CommonIMLChecker.errorHandler.clear();

		try {
			doc = CommonIMLChecker.db.parse(call);
		}
		catch (SAXException ex) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", ex.toString());
			cf.addMotivo("requestLangs: SAXException al solicitar y parsear la lista de idiomas");
			throw new ExcepcionChecker(cf);
		}
		catch (Exception ex) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", ex.toString());
			cf.addMotivo("requestLangs: Exception al solicitar y parsear la lista de idiomas");
			throw new ExcepcionChecker(cf);
		}

		
		if (CommonIMLChecker.errorHandler.hasErrors()) {
			CommonIMLChecker.logCall(call);
			
			ArrayList<String> errors = CommonIMLChecker.errorHandler.getErrors();
			String msg="";
			
			for (int x=0; x < errors.size(); x++) 
				if (x == (errors.size()-1)) msg += "++++"+errors.get(x);
				else msg += "++++"+errors.get(x)+"<br>\n";
			
			
			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo("requestLangs: La lista de idiomas es inválida, tiene errors");
			throw new ExcepcionChecker(cf);
		}
		
		if (CommonIMLChecker.errorHandler.hasFatalerrors()) {
			CommonIMLChecker.logCall(call);
			
			ArrayList<String> fatalerrors = CommonIMLChecker.errorHandler.getFatalerrors();
			String msg="";
			
			for (int x=0; x < fatalerrors.size(); x++)
				if (x == (fatalerrors.size()-1)) msg += "++++"+fatalerrors.get(x);
				else msg += "++++"+fatalerrors.get(x)+"<br>\n";
			
			
			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo("requestLangs: La lista de idiomas es inválida, tiene fatal errors");
			throw new ExcepcionChecker(cf);
		}
		
		if (doc == null) {
			cf = new CheckerFailure(call, "", "requestLangs: Se recibe 'null' al solicitar y parsear la lista de idiomas");
			throw new ExcepcionChecker(cf);
		}
		

		NodeList nlLangs = doc.getElementsByTagName("lang");

		// procesamos todos los años

		for (int x=0; x < nlLangs.getLength(); x++) {
			Element elemLang = (Element)nlLangs.item(x);
			String lang = elemLang.getTextContent().trim();
			
			listaLangs.add(lang);
		}
		
		return listaLangs;
	}
	
	
	// para comparar el resultado de la F21: listas de idiomas
	// no devuelve nada si son iguales
	// levanta ExcepcionChecker si hay diferencias
	
	private static void comparaLangs (String usuario, ArrayList<String> pLangs, ArrayList<String> xLangs) 
			throws ExcepcionChecker
	{	
		CheckerFailure cf;
		
		if (pLangs.size() != xLangs.size()) {
			cf = new CheckerFailure("", "20_DIFS", "comparaLangs: Debería devolver "+pLangs.size()+" idiomas, pero devuelve "+xLangs.size()); 
			throw new ExcepcionChecker(cf);
		}

			
		for (int x=0; x < pLangs.size(); x++) 
			if (!xLangs.get(x).equals(pLangs.get(x))) {
				cf = new CheckerFailure("", "20_DIFS", "comparaLangs: El idioma número "+x+" debería ser '"+pLangs.get(x)+"', pero es '"+xLangs.get(x)+"'"); 
				throw new ExcepcionChecker(cf);
			}
		
		return;
	}
	
	
	

	
	// pide y devuelve la lista de canciones de un idioma
	// levanta ExcepcionChecker si algo va mal
	
	private static ArrayList<Cancion> requestCancionesLang (String call) 
					throws ExcepcionChecker  
	{
		CheckerFailure cf;
		Document doc;
		ArrayList<Cancion> listaCanciones = new ArrayList<Cancion>();
		
		CommonIMLChecker.errorHandler.clear();

		try {
			doc = CommonIMLChecker.db.parse(call);   
		}
		catch (SAXException ex) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", ex.toString()); 
			cf.addMotivo("requestCancionesLang: SAXException al solicitar y parsear la lista de canciones");
			throw new ExcepcionChecker(cf);
		}
		catch (Exception ex) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", ex.toString());
			cf.addMotivo("requestCancionesLang: Exception al solicitar y parsear la lista de canciones");
			throw new ExcepcionChecker(cf);
		}

		if (CommonIMLChecker.errorHandler.hasErrors()) {
			CommonIMLChecker.logCall(call);
			
			ArrayList<String> errors = CommonIMLChecker.errorHandler.getErrors();
			String msg="";
			
			for (int x=0; x < errors.size(); x++) 
				if (x == (errors.size()-1)) msg += "++++"+errors.get(x);
				else msg += "++++"+errors.get(x)+"<br>\n";
			
			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo("requestCancionesLang: La lista de canciones es inválida, tiene errors");
			throw new ExcepcionChecker(cf);
		}
		
		if (CommonIMLChecker.errorHandler.hasFatalerrors()) {
			CommonIMLChecker.logCall(call);
			
			ArrayList<String> fatalerrors = CommonIMLChecker.errorHandler.getFatalerrors();
			String msg="";
			
			for (int x=0; x < fatalerrors.size(); x++) 
				if (x == (fatalerrors.size()-1)) msg += "++++"+fatalerrors.get(x);
				else msg += "++++"+fatalerrors.get(x)+"<br>\n";
			
			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo("requestCancionesLang: La lista de canciones es inválida, tiene fatal errors");
			throw new ExcepcionChecker(cf);
		}
		
		
		if (doc == null) {
			cf = new CheckerFailure(call, "", "requestCancionesLang: Se recibe 'null' al solicitar y parsear la lista de canciones");
			throw new ExcepcionChecker(cf);
		}
		

		NodeList nlCanciones = doc.getElementsByTagName("cancion");

		// procesamos todos los discos

		for (int x=0; x < nlCanciones.getLength(); x++) {
			Element elemCancion = (Element)nlCanciones.item(x);
			String titulo = elemCancion.getTextContent().trim();
			
			String idc = elemCancion.getAttribute("idc");
			String genero = elemCancion.getAttribute("genero");
			String descripcion = elemCancion.getAttribute("descripcion");
			
			listaCanciones.add(new Cancion(idc,titulo,0, descripcion, genero,""));  // no ponemos los premios de la cancion, ni su duración
		}
		
		return listaCanciones;
	}
	
	
	// para comparar el resultado de la F22: listas de canciones
	// no devuelve nada si son iguales
	// levanta ExcepcionChecker si hay diferencias
	
	private static void comparaCanciones (String usuario, String langActual, ArrayList<Cancion> pCanciones, ArrayList<Cancion> xCanciones) 
			throws ExcepcionChecker
	{
		CheckerFailure cf;
		String pIDCCan, xIDCCan, pTituloCan, xTituloCan, pGeneroCan, xGeneroCan, pDescCan, xDescCan;
		
		if (pCanciones.size() != xCanciones.size()) {
			cf = new CheckerFailure("", "20_DIFS", "comparaCanciones: "+usuario+"+"+langActual+": debería devolver "+pCanciones.size()+" canciones, pero devuelve "+xCanciones.size()); 
			throw new ExcepcionChecker(cf);
		}
	
		for (int y=0; y < pCanciones.size(); y++) {
			
			pIDCCan = pCanciones.get(y).getIDC();
			xIDCCan = xCanciones.get(y).getIDC();
			
			if (!xIDCCan.equals(pIDCCan)) {
				cf = new CheckerFailure("", "20_DIFS", "comparaCanciones: "+usuario+"+"+langActual+": el IDC de la canción número "+y+" debería ser '<pre>"+pIDCCan+"</pre>', pero es '<pre>"+xIDCCan+"</pre>'"); 
				throw new ExcepcionChecker(cf);
			}
			
			pTituloCan = pCanciones.get(y).getTitulo();
			xTituloCan = xCanciones.get(y).getTitulo();
			
			if (!xTituloCan.equals(pTituloCan)) {
				cf = new CheckerFailure("", "20_DIFS", "comparaCanciones: "+usuario+"+"+langActual+": la canción número "+y+" debería ser '<pre>"+pTituloCan+"</pre>', pero es '<pre>"+xTituloCan+"</pre>'");
				throw new ExcepcionChecker(cf);
			}
			
			pGeneroCan = pCanciones.get(y).getGenero();
			xGeneroCan = xCanciones.get(y).getGenero();
			
		   	if (!xGeneroCan.equals(pGeneroCan)) {
				cf = new CheckerFailure("", "20_DIFS", "comparaCanciones: "+usuario+"+"+langActual+": el género de la canción número "+y+" debería ser '"+pGeneroCan+"', pero es '"+xGeneroCan+"'"); 
				throw new ExcepcionChecker(cf);
		   	}
			
		   	pDescCan = pCanciones.get(y).getDescripcion();
		   	xDescCan = xCanciones.get(y).getDescripcion();
			
		   	if (!xDescCan.equals(pDescCan)) {
				cf = new CheckerFailure("", "20_DIFS", "comparaCanciones: "+usuario+"+"+langActual+": la decsripción de la canción número "+y+" debería ser  '<pre>"+pDescCan+"</pre>', pero es '<pre>"+xDescCan+"</pre>'");
				throw new ExcepcionChecker(cf);
		   	}
		   
		}
		
		return;
	}
    
	
	
	
	
	// pide y devuelve la lista de interpretes de canciones de un género
	// levanta ExcepcionChecker si algo va mal
	
	private static ArrayList<String> requestInterpretesCancion (String call) 
									throws ExcepcionChecker 
	{
		CheckerFailure cf;
		Document doc;
		ArrayList<String> listaInterpretes = new ArrayList<String>();

		CommonIMLChecker.errorHandler.clear();

		try {
			doc = CommonIMLChecker.db.parse(call);
		}
		catch (SAXException e) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", e.toString());
			cf.addMotivo("requestInterpretesCancion: SAXException al solicitar y parsear la lista de intérpretes");
			throw new ExcepcionChecker(cf);
		}
		catch (Exception e) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", e.toString());
			cf.addMotivo("requestInterpretesCancion: Exception al solicitar y parsear la lista de intérpretes");
			throw new ExcepcionChecker(cf);
		}

		if (CommonIMLChecker.errorHandler.hasErrors()) {
			CommonIMLChecker.logCall(call);
			
			ArrayList<String> errors = CommonIMLChecker.errorHandler.getErrors();
			String msg="";
			
			for (int x=0; x < errors.size(); x++) 
				if (x == (errors.size()-1)) msg += "++++"+errors.get(x);
				else msg += "++++"+errors.get(x)+"<br>\n";		
			
			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo("requestInterpretesCancion: La lista de intérpretes es inválida, tiene errors");
			throw new ExcepcionChecker(cf);
		}
		
		if (CommonIMLChecker.errorHandler.hasFatalerrors()) {
			CommonIMLChecker.logCall(call);
			
			ArrayList<String> fatalerrors = CommonIMLChecker.errorHandler.getFatalerrors();
			String msg="";
			
			for (int x=0; x < fatalerrors.size(); x++) 
				if (x == (fatalerrors.size()-1)) msg += "++++"+fatalerrors.get(x);
				else msg += "++++"+fatalerrors.get(x)+"<br>\n";
			
			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo("requestInterpretesCancion: La lista de intérpretes es inválida, tiene fatal errors");
			throw new ExcepcionChecker(cf);
		}
		
		
		if (doc == null) {
			cf = new CheckerFailure(call, "", "requestInterpretesCancion: Se recibe 'null' al solicitar y parsear la lista de intérpretes");
			throw new ExcepcionChecker(cf);
		}
		

		NodeList nlInterpretes = doc.getElementsByTagName("interprete");

		// procesamos todos los interpretes

		for (int x=0; x < nlInterpretes.getLength(); x++) {
			Element elemInterprete = (Element)nlInterpretes.item(x);
			
			String interprete = elemInterprete.getTextContent().trim();
			
			listaInterpretes.add(interprete);   // no ponemos los premios de su disco
		}
		
		return listaInterpretes;
	}
	
	
	
	// para comparar el resultado de la F23: listas de intérpretes
	// no devuelve nada si son iguales
	// levanta ExcepcionChecker si hay diferencias
	
	private static void comparaInterpretes (String usuario, String langActual, String genActual, ArrayList<String> pInterpretes, ArrayList<String> xInterpretes) 
				throws ExcepcionChecker
	{	
		CheckerFailure cf;
		
	    if (pInterpretes.size() != xInterpretes.size()) {
			cf = new CheckerFailure("", "20_DIFS", "comparaInterpretes: "+usuario+"+"+langActual+"+"+genActual+": debería devolver '"+pInterpretes.size()+"' intérpretes, pero devuelve '"+xInterpretes.size()+"'"); 
			throw new ExcepcionChecker(cf);
	    }
	    
	    for (int z=0; z < pInterpretes.size(); z++) {
	    	    if (!xInterpretes.get(z).equals(pInterpretes.get(z))) {
	    			cf = new CheckerFailure("", "20_DIFS", "comparaInterpretes: "+usuario+"+"+langActual+"+"+genActual+": el intérprete número "+z+" debería ser '<pre>"+pInterpretes.get(z)+"</pre>', pero es '<pre>"+xInterpretes.get(z)+"</pre>'"); 
	    			throw new ExcepcionChecker(cf);
	    	    }
	    }
	    
	    return;  
	}
	
	
	
	
	// pide y devuelve el resultado: la lista de discos del intérprete anterior en el idioma y que contengan canciones del género 
	// levanta ExcepcionChecker si algo va mal
	
	private static ArrayList<Disco> requestDiscosResult (String call) 
								throws ExcepcionChecker  
	{
		CheckerFailure cf;
		Document doc;
		
		ArrayList<Disco> listaDiscos = new ArrayList<Disco>();
	    
		CommonIMLChecker.errorHandler.clear();

		try {
			doc = CommonIMLChecker.db.parse(call);
		}
		catch (SAXException e) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", e.toString());
			cf.addMotivo("requestDiscosResult: SAXException al solicitar y parsear la lista de discos resultado");
			throw new ExcepcionChecker(cf);
		}
		catch (Exception e) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", e.toString());
			cf.addMotivo("requestDiscosResult: Exception al solicitar y parsear la lista de discos resultado");
			throw new ExcepcionChecker(cf);
		}

		if (CommonIMLChecker.errorHandler.hasErrors()) {
			CommonIMLChecker.logCall(call);
			
			ArrayList<String> errors = CommonIMLChecker.errorHandler.getErrors();
			String msg="";
			
			for (int x=0; x < errors.size(); x++) 
				if (x == (errors.size()-1)) msg += "++++"+errors.get(x);
				else msg += "++++"+errors.get(x)+"<br>\n";
			
			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo("requestDiscosResult: La lista de discos resultado es inválida, tiene errors");
			throw new ExcepcionChecker(cf);
		}
		
		if (CommonIMLChecker.errorHandler.hasFatalerrors()) {
			CommonIMLChecker.logCall(call);
			
			ArrayList<String> fatalerrors = CommonIMLChecker.errorHandler.getFatalerrors();
			String msg="";
			
			for (int x=0; x < fatalerrors.size(); x++) 
				if (x == (fatalerrors.size()-1)) msg += "++++"+fatalerrors.get(x);
				else msg += "++++"+fatalerrors.get(x)+"<br>\n";
			
			cf = new CheckerFailure(call, "", msg);
			cf.addMotivo("requestDiscosResult: La lista de discos resultado es inválida, tiene fatal errors");
			throw new ExcepcionChecker(cf);
		}
	
		
		if (doc == null) {
			cf = new CheckerFailure(call, "", "requestDiscosResult: Se recibe 'null' al solicitar y parsear la lista de discos resultado");
			throw new ExcepcionChecker(cf);
		}
		
		NodeList nlDiscos = doc.getElementsByTagName("disco");
		
		// procesamos todas las canciones resultado

		for (int x=0; x < nlDiscos.getLength(); x++) {
			Element elemDisco = (Element)nlDiscos.item(x);
			
			String titulo = elemDisco.getTextContent().trim();		
			String idd = elemDisco.getAttribute("idd");
			String langs = elemDisco.getAttribute("langs");
				
			listaDiscos.add(new Disco(idd, titulo, "", langs, ""));
		}
		
		return listaDiscos;
	}
	
	
	// para comparar el resultado de la F24: listas de Discos
	// no devuelve nada si son iguales
	// levanta ExcepcionChecker si hay diferencias
	
	private static void comparaDiscos (String usuario, String langActual, String genActual, String intActual, ArrayList<Disco> pDiscos, ArrayList<Disco> xDiscos) 
		throws ExcepcionChecker
	{	
		CheckerFailure cf;
		
	    if (pDiscos.size() != xDiscos.size()) {
			cf = new CheckerFailure("", "20_DIFS", "comparaDiscos: "+usuario+"+"+langActual+"+"+genActual+"+"+intActual+": debería devolver '"+pDiscos.size()+"' discos, pero devuelve '"+xDiscos.size()+"'"); 
			throw new ExcepcionChecker(cf);
	    }
	
	    
	    for (int z=0; z < pDiscos.size(); z++) {
	    	    if (!pDiscos.get(z).getTitulo().equals(xDiscos.get(z).getTitulo())) {
	    			cf = new CheckerFailure("", "20_DIFS", "comparaDiscos: "+usuario+"+"+langActual+"+"+genActual+"+"+intActual+": el título del disco número "+z+" debería ser '<pre>"+pDiscos.get(z).getTitulo()+"</pre>', pero es '<pre>"+xDiscos.get(z).getTitulo()+"</pre>'"); 
	    			throw new ExcepcionChecker(cf);
	    	    }

	    	    if (!pDiscos.get(z).getIDD().equals(xDiscos.get(z).getIDD())) {
	    			cf = new CheckerFailure("", "20_DIFS", "comparaDiscos: "+usuario+"+"+langActual+"+"+genActual+"+"+intActual+": el IDD del disco número "+z+" debería ser '<pre>"+pDiscos.get(z).getIDD()+"</pre>', pero es '<pre>"+xDiscos.get(z).getIDD()+"</pre>'"); 
	    			throw new ExcepcionChecker(cf);
	    	    }

				if (!comparaLangs(pDiscos.get(z).getIdiomas(), xDiscos.get(z).getIdiomas())) {
	    			cf = new CheckerFailure("", "20_DIFS", "comparaDiscos: "+usuario+"+"+langActual+"+"+genActual+"+"+intActual+": los idiomas del disco número "+z+" debería ser '<pre>"+pDiscos.get(z).getIdiomas()+"</pre>', pero son '<pre>"+xDiscos.get(z).getIdiomas()+"</pre>'"); 	     
	    			throw new ExcepcionChecker(cf);
				}
	    }
	    
	    return;  
	}
	
	// auxiliar del método anterior
	
	private static boolean comparaLangs (String pLangs, String xLangs) {
		
		String lang;
		String plista[] = pLangs.split(" ");
		String xlista[] = xLangs.split(" ");
		
		if (plista.length != xlista.length) return false;
		
		for (int x=0; x < plista.length; x++) {
			lang = plista[x];
			
			if (Arrays.asList(xlista).contains(lang)) continue;
			else return false;
		}
		
		return true;
	}

}
