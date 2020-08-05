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


// Implementación de la comprobación de la consulta 1 (canciones de un intérprete de un disco S2 de un año S1 que duren menos que una dada S3)

package docencia.sint.IML.checker;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.io.IOException;
import java.io.PrintWriter;

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



public class Query1 {
		
		
	// nombres de los parámetros de esta consulta
		
	static final String PANIO = "panio";
	static final String PIDD = "pidd";
	static final String PIDC = "pidc";
	
	// COMPROBACIÓN DE LAS LLAMADAS A SINTPROF
	
	public static void doGetC1CheckSintprofCalls(HttpServletRequest request, PrintWriter out)
						throws IOException, ServletException
	{
		CheckerFailure cf; 
		int esProfesor = 1;  // sólo el profesor debería llegar aquí, podemos poner esto a 1
		
		out.println("<html>");
		CommonIMLChecker.printHead(out);
		CommonIMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 1</h2>");
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
		
		// pedimos la lista de años de sintprof
	 
		String qs = "?auto=si&"+CommonSINT.PFASE+"=11&p="+CommonSINT.PASSWD;
		String call = CommonIMLChecker.servicioProf+qs;
		
		ArrayList<String> pAnios;
		try {
			pAnios = Query1.requestAnios(call);
			out.println("<h4>Años OK: "+pAnios.size()+"</h4>");
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			out.println("<h4 style='color: red'>ExcepcionSINT: sintprof (Años): <br>");
			out.println(cf.toHTMLString());
			out.println("</h4>");
			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonIML.CREATED);
			return;
        }
	
	
	    
		// vamos con la segunda fase, los discos de cada año
		// el bucle X recorre todos los años
		
		
		String anioActual;
	     
		for (int x=0; x < pAnios.size(); x++) {
			String indent ="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	    	
			anioActual = pAnios.get(x);
			
			// pedimos los discos de ese año de sintprof
	   	 
			qs = "?auto=si&"+CommonSINT.PFASE+"=12&"+PANIO+"="+anioActual+"&p="+CommonSINT.PASSWD;
			call = CommonIMLChecker.servicioProf+qs;
			
			ArrayList<Disco> pDiscos;
			try {
				pDiscos = requestDiscosAnio(call);
				out.println("<h4>"+indent+anioActual+": "+pDiscos.size()+"  OK</h4>");
			}
			catch (ExcepcionChecker e) { 
				cf = e.getCheckerFailure();
				out.println("<h4 style='color: red'>sintprof (Discos): <br>");
				out.println(cf.toHTMLString());
				out.println("</h4>");
				CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonIML.CREATED);
				return;
	        }
						        

	        
	        
	        // vamos con la tercera fase, las canciones de un disco
	        // el bucle Y recorre todos los discos
	        
	        Disco disActual;
	        
	        for (int y=0; y < pDiscos.size(); y++) {
	        	
		        	indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		        	
		        	disActual = pDiscos.get(y);
		        	
		    		// pedimos las canciones de ese disco de ese año de sintprof
		       	 
		    		qs = "?auto=si&"+CommonSINT.PFASE+"=13&"+PANIO+"="+anioActual+"&"+PIDD+"="+disActual.getIDD()+"&p="+CommonSINT.PASSWD;    // no es necesario URL-encoded
		    		call = CommonIMLChecker.servicioProf+qs;
		    		
		    		ArrayList<Cancion> pCanciones;
		    		try {
		    			pCanciones = requestCancionesDisco(call);
		    			out.println("<h4>"+indent+anioActual+"+"+disActual.getIDD()+": "+pCanciones.size()+"  OK</h4>");
		    		}
		    		catch (ExcepcionChecker e) { 
						cf = e.getCheckerFailure();
		    			out.println("<h4 style='color: red'>ExcepcionSINT: sintprof (Canciones): <br>");
						out.println(cf.toHTMLString());
		    			out.println("</h4>");
		    			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonIML.CREATED);
		    			return;
		            }
		    	            	          
		            
		            
	            // vamos con la cuarta fase, la lista de canciones resultado (de duración menor a la seleccionada)  
	            // el bucle Z recorre todas las canciones
		            
	            	Cancion canActual;
	            	
	            	for (int z=0; z < pCanciones.size(); z++) {
			        	indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			        	
			        	canActual = pCanciones.get(z);
		            			
		        		// pedimos la lista de canciones resultado de sintprof
		           	 
			    		qs = "?auto=si&"+CommonSINT.PFASE+"=14&"+PANIO+"="+anioActual+"&"+PIDD+"="+disActual.getIDD()+"&"+PIDC+"="+canActual.getIDC()+"&p="+CommonSINT.PASSWD;     // no es necesario URL-encoded
			    	    call = CommonIMLChecker.servicioProf+qs;
			    	    
			        	ArrayList<Cancion> pSongs;
			    		
		        		try {
		        			pSongs = requestSongsResult(call);
		        			out.println("<h4>"+indent+anioActual+"+"+disActual.getIDD()+"+"+canActual.getIDC()+": "+pSongs.size()+"  OK</h4>");
		        		}
		        		catch (ExcepcionChecker e) { 
							cf = e.getCheckerFailure();
		        			out.println("<h4 style='color: red'>ExcepcionSINT: sintprof (Canciones resultado): <br>");
							out.println(cf.toHTMLString());
		        			out.println("</h4>");
		        			CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonIML.CREATED);
		        			return;
		                }	        		        		
		            
		        } // for z
	            
	        } // for y
	         
	    } // for x
	    
		
		out.println("<h4>sintprof: Todo OK</h4>");
			
		CommonSINT.printEndPageChecker(out,  "0", esProfesor, CommonIML.CREATED);
	}
	
	

	
	
	
	
	
	// COMPROBACIÓN DEL SERVICIO DE UN ÚNICO ESTUDIANTE

	// pantalla para ordenar comprobar un estudiante (se pide su número de login)
	// debería unificarse en uno sólo con el de la consulta 2, son casi iguales

	public static void doGetC1CorrectOneForm (HttpServletRequest request, PrintWriter out, int esProfesor)
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
		
		out.println("<h2>Consulta 1</h2>");                               // consulta 1
		out.println("<h3>Corrección de un único servicio</h3>");
		
		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='121'>");  // conduce a doGetC1CorrectOneReport

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

	public static void doGetC1CorrectOneReport(HttpServletRequest request, PrintWriter out, int esProfesor)
						throws IOException, ServletException
	{	
		out.println("<html>");
		CommonIMLChecker.printHead(out);
		CommonIMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 1</h2>");
		out.println("<h3>Corrección de un único servicio</h3>");
		
		// leemos los datos del estudiante
		
		String alumnoP = request.getParameter("alumnoP");
		String servicioAluP = request.getParameter("servicioAluP");
		
		if ((alumnoP == null) || (servicioAluP == null)) {
			out.println("<h4 style='color: red'>Falta uno de los parámetros</h4>");  // si falta algún parámetro no se hace nada
			CommonSINT.printEndPageChecker(out,  "12", esProfesor, CommonIML.CREATED);
			return; 
		}
		
		String usuario="sint"+alumnoP;
		String passwdAlu, passwdRcvd;
		


		try {
			passwdAlu = CommonIMLChecker.getAluPasswd(usuario);
		}
		catch (ExcepcionSINT ex) {
			if (ex.getMessage().equals("NOCONTEXT"))
				out.println("<h4 style='color: red'>ExcepcionSINT: Todavía no se ha creado el contexto de "+usuario+"</h4>"); 
			else 
				out.println("<h4 style='color: red'>"+ex.getMessage()+": Imposible recuperar la passwd del contexto de "+usuario+"</h4>"); 
			CommonSINT.printEndPageChecker(out,  "12", esProfesor, CommonIML.CREATED);
			return; 
		}
		
		if (esProfesor == 0) {  // lo está probando un alumno, es obligatorio que haya introducido su passwd
			passwdRcvd = request.getParameter("passwdAlu");   // leemos la passwd del alumno del parámetro
			
			if (passwdRcvd == null) {
				out.println("<h4 style='color: red'>No se ha recibido la passwd de "+usuario+"</h4>"); 
				CommonSINT.printEndPageChecker(out,  "12", esProfesor, CommonIML.CREATED);
				return; 
			}
			
			if (!passwdAlu.equals(passwdRcvd)) {
				out.println("<h4 style='color: red'>La passwd proporcionada no coincide con la almacenada en el sistema para "+usuario+"</h4>"); 
				CommonSINT.printEndPageChecker(out,  "12", esProfesor, CommonIML.CREATED);
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
			CommonSINT.printEndPageChecker(out,  "12", esProfesor, CommonIML.CREATED);
			return;
		}
		
		
		try {
			Query1.correctC1OneStudent(request, usuario, alumnoP, servicioAluP, passwdAlu);
		}
		catch (ExcepcionChecker e) {
			CheckerFailure cf = e.getCheckerFailure();
			out.println("<h4 style='color: red'>Resultado incorrecto para la práctica de "+usuario+" <br>");
			out.println(cf.toHTMLString());
			out.println("</h4>");
			CommonSINT.printEndPageChecker(out,  "12", esProfesor, CommonIML.CREATED);
			return;
		}
		
		out.println("<h3>Resultado: OK</h3>");

	
	
		// terminamos con botones Atrás e Inicio

		CommonSINT.printEndPageChecker(out,  "12", esProfesor, CommonIML.CREATED);
	}
	
	
	
	
	// método que corrige la consulta 1 de un estudiante

    private static void correctC1OneStudent (HttpServletRequest request,String usuario, String aluNum, String servicioAluP, String passwdAlu) 
    			throws ExcepcionChecker
	{
    	CheckerFailure cf;
		
		// para la consulta directa final, vamos a escoger anio, película y actor al azar y a guardarlas en esas variables
		
		SintRandom.init(); // inicializamos el generador de números aleatorios
		int posrandom;
		
		String dqAnio="";
		String dqIDD="";
		String dqIDC="";
		
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
			cf.addMotivo("correctC1OneStudent: Diferencias en la lista de errores");
			throw new ExcepcionChecker(cf);
		}

		
		
		
		// y ahora todas y cada una de las consultas
		
		// pedimos la lista de años de sintprof
	 
		String qs = "?auto=si&"+CommonSINT.PFASE+"=11&p="+CommonSINT.PASSWD;
		String call = CommonIMLChecker.servicioProf+qs;
		
		ArrayList<String> pAnios;
		try {
			pAnios = Query1.requestAnios(call);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo("correctC1OneStudent: ExcepcionChecker solicitando la lista de años a sintprof");
			throw new ExcepcionChecker(cf);
		}
	
	
		// pedimos la lista de anios del sintX
		
		qs = "?auto=si&"+CommonSINT.PFASE+"=11&p="+passwdAlu;
		call = servicioAluP+qs;
		
		ArrayList<String> xAnios;
		try {
			xAnios = Query1.requestAnios(call);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo("correctC1OneStudent: ExcepcionChecker solicitando la lista de años a "+usuario);
			throw new ExcepcionChecker(cf);
		}
	
		
		// comparamos las listas de sintprof y sintX
		
		try {
		     Query1.comparaAnios(usuario, pAnios, xAnios);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setUrl(call);
			cf.addMotivo("correctC1OneStudent: Diferencias en la lista de años ");
			throw new ExcepcionChecker(cf);
		}
		
		
	    
		// las listas de años son iguales
		// elegimos un año al azar para la consulta directa final
	    
		posrandom = SintRandom.getRandomNumber(0, pAnios.size()-1);
		dqAnio = pAnios.get(posrandom);
	    
		
		
	    
		// vamos con la segunda fase, los discos de cada año
		// el bucle X recorre todos los años
		
		
		String anioActual;
	     
		for (int x=0; x < pAnios.size(); x++) {
	    	
			anioActual = pAnios.get(x);
			
			// pedimos los discos de ese año de sintprof
	   	 
			qs = "?auto=si&"+CommonSINT.PFASE+"=12&"+PANIO+"="+anioActual+"&p="+CommonSINT.PASSWD;
			call = CommonIMLChecker.servicioProf+qs;
			
			ArrayList<Disco> pDiscos;
			try {
				pDiscos = requestDiscosAnio(call);
			}
			catch (ExcepcionChecker e) { 
				cf = e.getCheckerFailure();
				cf.setCodigo("20_DIFS");
				cf.addMotivo("correctC1OneStudent: ExcepcionChecker solicitando la lista de discos a sintprof");
				throw new ExcepcionChecker(cf);
			}
		
			
			// pedimos los discos de ese año del sintX
			
			qs = "?auto=si&"+CommonSINT.PFASE+"=12&"+PANIO+"="+anioActual+"&p="+passwdAlu;
			call = servicioAluP+qs;
			
			ArrayList<Disco> xDiscos;
			try {
				xDiscos = requestDiscosAnio(call);
			}
			catch (ExcepcionChecker e) { 
				cf = e.getCheckerFailure();
				cf.setCodigo("20_DIFS");
				cf.addMotivo("correctC1OneStudent: Excepción solicitando la lista de discos de "+anioActual+" a "+usuario);
				throw new ExcepcionChecker(cf);
			}			
			
			
			// comparamos las listas de sintprof y sintX
			
			try {
				Query1.comparaDiscos(usuario, anioActual, pDiscos, xDiscos);
			}
			catch (ExcepcionChecker e) { 
				cf = e.getCheckerFailure();
				cf.setUrl(call);
				cf.addMotivo("correctC1OneStudent: Diferencias en la lista de discos de "+anioActual);
				throw new ExcepcionChecker(cf);
			}
			
	       	        
	        
	        // las listas de discos para este año son iguales
		    // si este año es el de la consulta directa final, elegimos un disco al azar para la consulta directa final
	        
	        
	        if (anioActual.equals(dqAnio)) {
	            posrandom = SintRandom.getRandomNumber(0, pDiscos.size()-1);
	            Disco pDis = pDiscos.get(posrandom);
	          	dqIDD = pDis.getIDD();
	        }
	        
	        
	        
	        // vamos con la tercera fase, las canciones de un disco
	        // el bucle Y recorre todos los discos
	        
	        Disco disActual;
	        
	        for (int y=0; y < pDiscos.size(); y++) {
	        	
	        	disActual = pDiscos.get(y);
		        	
	    		// pedimos las canciones de ese disco de ese año de sintprof
	       	 
	    		qs = "?auto=si&"+CommonSINT.PFASE+"=13&"+PANIO+"="+anioActual+"&"+PIDD+"="+disActual.getIDD()+"&p="+CommonSINT.PASSWD;    // no es necesario URL-encoded
	    		call = CommonIMLChecker.servicioProf+qs;
	    		
	    		ArrayList<Cancion> pCanciones;
	    		try {
	    			pCanciones = requestCancionesDisco(call);
	    		}
	    		catch (ExcepcionChecker e) { 
	    			cf = e.getCheckerFailure();
					cf.setCodigo("20_DIFS");
					cf.addMotivo("correctC1OneStudent: ExcepcionChecker solicitando la lista de canciones a sintprof");
					throw new ExcepcionChecker(cf);
	    		}
	    		
	    		
	    		// pedimos las canciones de ese disco de ese año del sintX
	    		
	    		qs = "?auto=si&"+CommonSINT.PFASE+"=13&"+PANIO+"="+anioActual+"&"+PIDD+"="+disActual.getIDD()+"&p="+passwdAlu;    // no es necesario URL-encoded
	    		call = servicioAluP+qs;
	    		
	    		ArrayList<Cancion> xCanciones;
	    		try {
	    			xCanciones = requestCancionesDisco(call);
	    		}
	    		catch (ExcepcionChecker e) { 
	    			cf = e.getCheckerFailure();
					cf.setCodigo("20_DIFS");
					cf.addMotivo("correctC1OneStudent: ExcepcionChecker solicitando la lista de canciones a "+usuario);
					throw new ExcepcionChecker(cf);
	    		}
	    		
	    		
	    		// comparamos las listas de sintprof y sintX
		    			
	    		try {
	    			Query1.comparaCanciones(usuario, anioActual, disActual.getIDD(), pCanciones, xCanciones);
				}
				catch (ExcepcionChecker e) { 
					cf = e.getCheckerFailure();
					cf.setUrl(call);
					cf.addMotivo("correctC1OneStudent: Diferencias en la lista de canciones");
					throw new ExcepcionChecker(cf);
				}
	            
	            
	            // las listas de canciones de este disco son iguales
	            // si este disco es la de la consulta directa final, elegimos una canción al azar para la consulta directa final
	            
	            if ( (anioActual.equals(dqAnio)) && (disActual.getIDD().equals(dqIDD))) {
		            posrandom = SintRandom.getRandomNumber(0, pCanciones.size()-1);
		        	    dqIDC = pCanciones.get(posrandom).getIDC();
		        }
	            
	            
	            // vamos con la cuarta fase, la lista de canciones resultado
	            // el bucle Z recorre todas las canciones
		            
	            Cancion cancionActual;
	            	
		        for (int z=0; z < pCanciones.size(); z++) {
	            	
		        	cancionActual = pCanciones.get(z);
		            			
	        		// pedimos la lista de canciones resultado de sintprof
	           	 
		    		qs = "?auto=si&"+CommonSINT.PFASE+"=14&"+PANIO+"="+anioActual+"&"+PIDD+"="+disActual.getIDD()+"&"+PIDC+"="+cancionActual.getIDC()+"&p="+CommonSINT.PASSWD;     // no es necesario URL-encoded
		    	    call = CommonIMLChecker.servicioProf+qs;
		    	    
	        	    ArrayList<Cancion> pSongs;
	        		
	        		try {
	        			pSongs = requestSongsResult(call);
	        		}
	        		catch (ExcepcionChecker e) { 
						cf = e.getCheckerFailure();
						cf.setCodigo("20_DIFS");
						cf.addMotivo("correctC1OneStudent: ExcepcionChecker solicitando la lista de canciones resultado a sintprof");
						throw new ExcepcionChecker(cf);
	        		}
	        		
	        		
	        		// pedimos la filmografía de ese actor del sintX
	        		
		    		qs = "?auto=si&"+CommonSINT.PFASE+"=14&"+PANIO+"="+anioActual+"&"+PIDD+"="+disActual.getIDD()+"&"+PIDC+"="+cancionActual.getIDC()+"&p="+passwdAlu;     // no es necesario URL-encoded
		    	    call = servicioAluP+qs;
		    	    
	        		ArrayList<Cancion> xSongs;
	        		try {
	        			xSongs = requestSongsResult(call);
	        		}
	        		catch (ExcepcionChecker e) { 
						cf = e.getCheckerFailure();
						cf.setCodigo("20_DIFS");
						cf.addMotivo("correctC1OneStudent: ExcepcionChecker solicitando la lista de canciones resultado a "+usuario);
						throw new ExcepcionChecker(cf);
	        		}
	        			
	        		
	        		// comparamos las filmografías de sintprof y sintX
		        		
	        		try {
	        			Query1.comparaSongs(usuario, anioActual, disActual.getIDD(), cancionActual.getIDC(), pSongs, xSongs);
					}
					catch (ExcepcionChecker e) { 
						cf = e.getCheckerFailure();
						cf.setUrl(call);
						cf.addMotivo("correctC1OneStudent: Diferencias en la lista de canciones resultado");
						throw new ExcepcionChecker(cf);
					}
	            
	            } // for z
	            
	        } // for y
	         
	    } // for x
	    
	    
		// finalmente la consulta directa
		
		try {
			Query1.checkDirectQueryC1(CommonIMLChecker.servicioProf, usuario, servicioAluP, dqAnio, dqIDD, dqIDC, passwdAlu);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo("correctC1OneStudent: Resultado erróneo en la consulta directa");
			throw new ExcepcionChecker(cf);
		}
		
		// todas las consultas coincidieron
		
	    return;
	}
	
	
	

	
	

	
	
	// comprueba que las consultas directas son iguales   
	
	private static void checkDirectQueryC1(String servicioProf, String usuario, String servicioAluP, String anio, String idd, String idc, String passwdAlu) 
		throws ExcepcionChecker
	{
		CheckerFailure cf;
		ArrayList<Cancion> pSongs, xSongs;
			
		// primero comprobamos que responde con el error apropiado si falta algún parámetro
		
  		try {
  			CommonIMLChecker.checkLackParam(servicioAluP, passwdAlu, "14", PANIO, anio, PIDD, idd, PIDC, idc);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo("checkDirectQueryC1: No responde correctamente si falta algún parámetro obligatorio");
			throw new ExcepcionChecker(cf);
		}
  		
  		
 		// ahora comprobamos que los resultados son correctos
  		
		String qs = "?auto=si&"+CommonSINT.PFASE+"=14&"+PANIO+"="+anio+"&"+PIDD+"="+idd+"&"+PIDC+"="+idc+"&p="+CommonSINT.PASSWD;     // AQUI no es necesario URL-encoded, en otras consultas puede que si
	    String call = CommonIMLChecker.servicioProf+qs;
	    
   		try {
   			pSongs = Query1.requestSongsResult(call);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo("checkDirectQueryC1: ExcepcionChecker al pedir la consulta directa a sintprof");
			throw new ExcepcionChecker(cf);
		}
   		
		qs = "?auto=si&"+CommonSINT.PFASE+"=14&"+PANIO+"="+anio+"&"+PIDD+"="+idd+"&"+PIDC+"="+idc+"&p="+passwdAlu;     // no es necesario URL-encoded
	    call = servicioAluP+qs;
	    
  		try {
  			xSongs = Query1.requestSongsResult(call);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setCodigo("20_DIFS");
			cf.addMotivo("checkDirectQueryC1: ExcepcionChecker al pedir la consulta directa a "+usuario);
			throw new ExcepcionChecker(cf);
		}
   		
   		
		// comparamos las listas de canciones resultado de sintprof y sintX
		
		try {
			Query1.comparaSongs(usuario, anio, idd, idc, pSongs, xSongs);
		}
		catch (ExcepcionChecker e) { 
			cf = e.getCheckerFailure();
			cf.setUrl(call);
			cf.addMotivo("checkDirectQueryC1: Diferencias en la lista de canciones resultado");
			throw new ExcepcionChecker(cf);
		}
  	
	
		// todo coincidió
		
		return;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// COMPROBACIÓN DEL SERVICIO DE TODOS LOS ESTUDIANTES

	// pantalla para comprobar todos los estudiantes, se pide el número de cuentas a comprobar (corregir su práctica)

	public static void doGetC1CorrectAllForm (HttpServletRequest request, PrintWriter out)
	{
		int esProfesor = 1;
		
		out.println("<html>");
		CommonIMLChecker.printHead(out);
		CommonIMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 1</h2>");
		out.println("<h3>Corrección de todos los servicios</h3>");

		out.println("<form>");
		out.println("<input type='hidden' name='screenP' value='131'>");

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
		
	public static void doGetC1CorrectAllReport(HttpServletRequest request, PrintWriter out)
						throws IOException, ServletException
	{
		int esProfesor = 1;
		
		out.println("<html>");
		CommonIMLChecker.printHead(out);
		CommonIMLChecker.printBodyHeader(out);
		
		out.println("<h2>Consulta 1</h2>");

		// si no se recibe el parámetro con el número de cuentas no se hace nada

		String numCuentasP = request.getParameter("numCuentasP");
		if (numCuentasP == null) {
			out.println("<h4>Error: no se ha recibido el número de cuentas</h4>");
			CommonSINT.printEndPageChecker(out,  "13", esProfesor, CommonIML.CREATED);
			return;
		}

		int numCuentas=0;

		try {
			numCuentas = Integer.parseInt(numCuentasP);
		}
		catch (NumberFormatException e) {
			out.println("<h4>Error: el número de cuentas recibido no es un número válido</h4>");
			CommonSINT.printEndPageChecker(out,  "13", esProfesor, CommonIML.CREATED);
			return;
		}

		if (numCuentas < 1) {  
			out.println("<h4>Error: el número de cuentas recibido es menor que uno</h4>");
			CommonSINT.printEndPageChecker(out,  "13", esProfesor, CommonIML.CREATED);
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
				Query1.correctC1OneStudent(request, sintUser, Integer.toString(x), servicioAlu, passwdAlu);
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

		CommonSINT.printEndPageChecker(out,  "13", esProfesor, CommonIML.CREATED);
	}

	
	

	
	// Métodos auxiliares para la correción de un alumno de la consulta 1
	
	
	// pide y devuelve la lista de años de un usuario
	// levanta ExcepcionChecker si algo va mal
	
	private static ArrayList<String> requestAnios (String call) 
									throws ExcepcionChecker  
	{
		CheckerFailure cf;
		Document doc;
		ArrayList<String> listaAnios = new ArrayList<String>();
		
		CommonIMLChecker.errorHandler.clear();

		try {
			doc = CommonIMLChecker.db.parse(call);
		}
		catch (SAXException ex) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", ex.toString());
			cf.addMotivo("requestAnios: SAXException al solicitar y parsear la lista de años");
			throw new ExcepcionChecker(cf);
		}
		catch (Exception ex) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", ex.toString());
			cf.addMotivo("requestAnios: Exception al solicitar y parsear la lista de años");
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
			cf.addMotivo("requestAnios: La lista de años es inválida, tiene errors");
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
			cf.addMotivo("requestAnios: La lista de años es inválida, tiene fatal errors");
			throw new ExcepcionChecker(cf);
		}
		
		if (doc == null) {
			cf = new CheckerFailure(call, "", "requestAnios: Se recibe 'null' al solicitar y parsear la lista de años");
			throw new ExcepcionChecker(cf);
		}
		

		NodeList nlAnios = doc.getElementsByTagName("anio");

		// procesamos todos los años

		for (int x=0; x < nlAnios.getLength(); x++) {
			Element elemAnio = (Element)nlAnios.item(x);
			String anio = elemAnio.getTextContent().trim();
			
			listaAnios.add(anio);
		}
		
		return listaAnios;
	}
	
	
	// para comparar el resultado de la F11: listas de años
	// no devuelve nada si son iguales
	// levanta ExcepcionChecker si hay diferencias
	
	private static void comparaAnios (String usuario, ArrayList<String> pAnios, ArrayList<String> xAnios) 
			throws ExcepcionChecker
	{	
		CheckerFailure cf;
		
		if (pAnios.size() != xAnios.size()) {
			cf = new CheckerFailure("", "20_DIFS", "comparaAnios: Debería devolver "+pAnios.size()+" años, pero devuelve "+xAnios.size()); 
			throw new ExcepcionChecker(cf);
		}

			
		for (int x=0; x < pAnios.size(); x++) 
			if (!xAnios.get(x).equals(pAnios.get(x))) {
				cf = new CheckerFailure("", "20_DIFS", "comparaAnios: El año número "+x+" debería ser '"+pAnios.get(x)+"', pero es '"+xAnios.get(x)+"'"); 
				throw new ExcepcionChecker(cf);
			}
		
		return;
	}
	
	
	

	
	// pide y devuelve la lista de discos de un año
	// levanta ExcepcionChecker si algo va mal
	
	private static ArrayList<Disco> requestDiscosAnio (String call) 
					throws ExcepcionChecker  
	{
		CheckerFailure cf;
		Document doc;
		ArrayList<Disco> listaDiscos = new ArrayList<Disco>();
		
		CommonIMLChecker.errorHandler.clear();

		try {
			doc = CommonIMLChecker.db.parse(call);   
		}
		catch (SAXException ex) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", ex.toString()); 
			cf.addMotivo("requestDiscosAnio: SAXException al solicitar y parsear la lista de discos");
			throw new ExcepcionChecker(cf);
		}
		catch (Exception ex) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", ex.toString());
			cf.addMotivo("requestDiscosAnio: Exception al solicitar y parsear la lista de discos");
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
			cf.addMotivo("requestDiscosAnio: La lista de discos es inválida, tiene errors");
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
			cf.addMotivo("requestDiscosAnio: La lista de discos es inválida, tiene fatal errors");
			throw new ExcepcionChecker(cf);
		}
		
		
		if (doc == null) {
			cf = new CheckerFailure(call, "", "requestDiscosAnio: Se recibe 'null' al solicitar y parsear la lista de discos");
			throw new ExcepcionChecker(cf);
		}
		

		NodeList nlDiscos = doc.getElementsByTagName("disco");

		// procesamos todos los discos

		for (int x=0; x < nlDiscos.getLength(); x++) {
			Element elemDisco = (Element)nlDiscos.item(x);
			String titulo = elemDisco.getTextContent().trim();
			
			String idd = elemDisco.getAttribute("idd");
			String interprete = elemDisco.getAttribute("interprete");
			String langs = elemDisco.getAttribute("langs");
			
			listaDiscos.add(new Disco(idd,titulo,interprete, langs,""));  // no ponemos los premios del disco
		}
		
		return listaDiscos;
	}
	
	
	// para comparar el resultado de la F12: listas de discos
	// no devuelve nada si son iguales
	// levanta ExcepcionChecker si hay diferencias
	
	private static void comparaDiscos (String usuario, String anioActual, ArrayList<Disco> pDiscos, ArrayList<Disco> xDiscos) 
			throws ExcepcionChecker
	{
		CheckerFailure cf;
		String pIDDDis, xIDDDis, pTituloDis, xTituloDis, pInterpreteDis, xInterpreteDis, pLangsDis, xLangsDis, pPremiosDis, xPremiosDis;
		
		if (pDiscos.size() != xDiscos.size()) {
			cf = new CheckerFailure("", "20_DIFS", "comparaDiscos: "+usuario+"+"+anioActual+": debería devolver "+pDiscos.size()+" discos, pero devuelve "+xDiscos.size()); 
			throw new ExcepcionChecker(cf);
		}
	
		for (int y=0; y < pDiscos.size(); y++) {
			
			pIDDDis = pDiscos.get(y).getIDD();
			xIDDDis = xDiscos.get(y).getIDD();
			
			if (!xIDDDis.equals(pIDDDis)) {
				cf = new CheckerFailure("", "20_DIFS", "comparaDiscos: "+usuario+"+"+anioActual+": el IDD del disco número "+y+" debería ser '<pre>"+pIDDDis+"</pre>', pero es '<pre>"+xIDDDis+"</pre>'"); 
				throw new ExcepcionChecker(cf);
			}
			
			pTituloDis = pDiscos.get(y).getTitulo();
			xTituloDis = xDiscos.get(y).getTitulo();
			
			if (!xTituloDis.equals(pTituloDis)) {
				cf = new CheckerFailure("", "20_DIFS", "comparaDiscos: "+usuario+"+"+anioActual+": el disco número "+y+" debería ser '<pre>"+pTituloDis+"</pre>', pero es '<pre>"+xTituloDis+"</pre>'");
				throw new ExcepcionChecker(cf);
			}
			
			pInterpreteDis = pDiscos.get(y).getInterprete();
			xInterpreteDis = xDiscos.get(y).getInterprete();
			
		   	if (!xInterpreteDis.equals(pInterpreteDis)) {
				cf = new CheckerFailure("", "20_DIFS", "comparaDiscos: "+usuario+"+"+anioActual+": el disco número "+y+" debería ser de '"+pInterpreteDis+"', pero es de '"+xInterpreteDis+"'"); 
				throw new ExcepcionChecker(cf);
		   	}
			
		   	pLangsDis = pDiscos.get(y).getIdiomas();
		   	xLangsDis = xDiscos.get(y).getIdiomas();
			
		   	if (!xLangsDis.equals(pLangsDis)) {
				cf = new CheckerFailure("", "20_DIFS", "comparaDiscos: "+usuario+"+"+anioActual+": el disco número "+y+" debería tener idiomas '<pre>"+pLangsDis+"</pre>', pero tiene '<pre>"+xLangsDis+"</pre>'");
				throw new ExcepcionChecker(cf);
		   	}
		   	
		   	pPremiosDis = pDiscos.get(y).getPremios();
		   	xPremiosDis = xDiscos.get(y).getPremios();
			
		   	if (!xPremiosDis.equals(pPremiosDis)) {
				cf = new CheckerFailure("", "20_DIFS", "comparaDiscos: "+usuario+"+"+anioActual+": el disco número "+y+" debería tener premios '<pre>"+pPremiosDis+"</pre>', pero tiene '<pre>"+xPremiosDis+"</pre>'"); 
				throw new ExcepcionChecker(cf);
		   	}
		}
		
		return;
	}
    
	
	
	
	
	// pide y devuelve la lista de canciones de un disco de un año
	// levanta ExcepcionChecker si algo va mal
	
	private static ArrayList<Cancion> requestCancionesDisco (String call) 
									throws ExcepcionChecker 
	{
		CheckerFailure cf;
		Document doc;
		ArrayList<Cancion> listaCanciones = new ArrayList<Cancion>();

		CommonIMLChecker.errorHandler.clear();

		try {
			doc = CommonIMLChecker.db.parse(call);
		}
		catch (SAXException e) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", e.toString());
			cf.addMotivo("requestCancionesDisco: SAXException al solicitar y parsear la lista de canciones");
			throw new ExcepcionChecker(cf);
		}
		catch (Exception e) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", e.toString());
			cf.addMotivo("requestCancionesDisco: Exception al solicitar y parsear la lista de canciones");
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
			cf.addMotivo("requestCancionesDisco: La lista de canciones es inválida, tiene errors");
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
			cf.addMotivo("requestCancionesDisco: La lista de canciones es inválida, tiene fatal errors");
			throw new ExcepcionChecker(cf);
		}
		
		
		if (doc == null) {
			cf = new CheckerFailure(call, "", "requestCancionesDisco: Se recibe 'null' al solicitar y parsear la lista de canciones");
			throw new ExcepcionChecker(cf);
		}
		

		NodeList nlCanciones = doc.getElementsByTagName("cancion");

		// procesamos todas las canciones

		for (int x=0; x < nlCanciones.getLength(); x++) {
			Element elemCancion = (Element)nlCanciones.item(x);
			
			String titulo = elemCancion.getTextContent().trim();
			
			String idc = elemCancion.getAttribute("idc");
			String sDuracion = elemCancion.getAttribute("duracion");
			int duracion = Integer.parseInt(sDuracion);
			
			String descripcion = CommonSINT.getTextContent(elemCancion);
			String genero = elemCancion.getAttribute("genero");
			
			listaCanciones.add(new Cancion(idc, titulo, duracion, descripcion, genero, ""));   // no ponemos los premios de su disco
		}
		
		return listaCanciones;
	}
	
	
	
	// para comparar el resultado de la F13: listas de canciones
	// no devuelve nada si son iguales
	// levanta ExcepcionChecker si hay diferencias
	
	private static void comparaCanciones (String usuario, String anioActual, String disActual, ArrayList<Cancion> pCanciones, ArrayList<Cancion> xCanciones) 
				throws ExcepcionChecker
	{	
		CheckerFailure cf;
		
	    if (pCanciones.size() != xCanciones.size()) {
			cf = new CheckerFailure("", "20_DIFS", "comparaCanciones: "+usuario+"+"+anioActual+"+"+disActual+": debería devolver '"+pCanciones.size()+"' canciones, pero devuelve '"+xCanciones.size()+"'"); 
			throw new ExcepcionChecker(cf);
	    }
	    
	    for (int z=0; z < pCanciones.size(); z++) {
	    	    if (!xCanciones.get(z).getIDC().equals(pCanciones.get(z).getIDC())) {
	    			cf = new CheckerFailure("", "20_DIFS", "comparaCanciones: "+usuario+"+"+anioActual+"+"+disActual+": el IDC de la canción número "+z+" debería ser '<pre>"+pCanciones.get(z).getIDC()+"</pre>', pero es '<pre>"+xCanciones.get(z).getIDC()+"</pre>'"); 
	    			throw new ExcepcionChecker(cf);
	    	    }
	    	
	    	    if (!xCanciones.get(z).getTitulo().equals(pCanciones.get(z).getTitulo())) {
	    			cf = new CheckerFailure("", "20_DIFS", "comparaCanciones: "+usuario+"+"+anioActual+"+"+disActual+": el título de la canción número "+z+" debería ser '<pre>"+pCanciones.get(z).getTitulo()+"</pre>', pero es '<pre>"+xCanciones.get(z).getTitulo()+"</pre>'"); 
	    			throw new ExcepcionChecker(cf);
	    	    }

	    	    if (xCanciones.get(z).getDuracion() != pCanciones.get(z).getDuracion()) {
	    			cf = new CheckerFailure("", "20_DIFS", "comparaCanciones: "+usuario+"+"+anioActual+"+"+disActual+": la duración de la canción número "+z+" debería ser '<pre>"+pCanciones.get(z).getDuracion()+"</pre>', pero es '<pre>"+xCanciones.get(z).getDuracion()+"</pre>'");  
	    			throw new ExcepcionChecker(cf);
	    	    }

	    	    if (!xCanciones.get(z).getGenero().equals(pCanciones.get(z).getGenero())) {
	    			cf = new CheckerFailure("", "20_DIFS", "comparaCanciones: "+usuario+"+"+anioActual+"+"+disActual+": el género de la canción número "+z+" debería ser '<pre>"+pCanciones.get(z).getGenero()+"</pre>', pero es '<pre>"+xCanciones.get(z).getGenero()+"</pre>'"); 	    
	    			throw new ExcepcionChecker(cf);
	    	    }    
	    }
	    
	    return;  
	}
	
	
	
	
	// pide y devuelve el resultado: la lista de canciones que duran menos que la seleccionada
	// levanta ExcepcionChecker si algo va mal
	
	private static ArrayList<Cancion> requestSongsResult (String call) 
								throws ExcepcionChecker  
	{
		CheckerFailure cf;
		Document doc;
		
		ArrayList<Cancion> listaCanciones = new ArrayList<Cancion>();
	    
		CommonIMLChecker.errorHandler.clear();

		try {
			doc = CommonIMLChecker.db.parse(call);
		}
		catch (SAXException e) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", e.toString());
			cf.addMotivo("requestSongsResult: SAXException al solicitar y parsear la lista de canciones resultado");
			throw new ExcepcionChecker(cf);
		}
		catch (Exception e) {
			CommonIMLChecker.logCall(call);
			
			cf = new CheckerFailure(call, "", e.toString());
			cf.addMotivo("requestSongsResult: Exception al solicitar y parsear la lista de canciones resultado");
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
			cf.addMotivo("requestSongsResult: La lista de canciones resultado es inválida, tiene errors");
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
			cf.addMotivo("requestSongsResult: La lista de canciones resultado es inválida, tiene fatal errors");
			throw new ExcepcionChecker(cf);
		}
	
		
		if (doc == null) {
			cf = new CheckerFailure(call, "", "requestSongsResult: Se recibe 'null' al solicitar y parsear la lista de canciones resultado");
			throw new ExcepcionChecker(cf);
		}
		
		NodeList nlCanciones = doc.getElementsByTagName("song");
		
		// procesamos todas las canciones resultado

		for (int x=0; x < nlCanciones.getLength(); x++) {
			Element elemCancion = (Element)nlCanciones.item(x);
			
			String titulo = elemCancion.getTextContent().trim();		
			String descripcion = elemCancion.getAttribute("descripcion");
			String premios = elemCancion.getAttribute("premios");
				
			listaCanciones.add(new Cancion("", titulo, 0, descripcion, "", premios));
		}
		
		return listaCanciones;
	}
	
	
	// para comparar el resultado de la F14: listas de songs
	// no devuelve nada si son iguales
	// levanta ExcepcionChecker si hay diferencias
	
	private static void comparaSongs (String usuario, String anioActual, String disActual, String canActual, ArrayList<Cancion> pCanciones, ArrayList<Cancion> xCanciones) 
		throws ExcepcionChecker
	{	
		CheckerFailure cf;
		
	    if (pCanciones.size() != xCanciones.size()) {
			cf = new CheckerFailure("", "20_DIFS", "comparaSongs: "+usuario+"+"+anioActual+"+"+disActual+"+"+canActual+": debería devolver '"+pCanciones.size()+"' songs, pero devuelve '"+xCanciones.size()+"'"); 
			throw new ExcepcionChecker(cf);
	    }
	
	    
	    for (int z=0; z < pCanciones.size(); z++) {
	    	    if (!xCanciones.get(z).getTitulo().equals(pCanciones.get(z).getTitulo())) {
	    			cf = new CheckerFailure("", "20_DIFS", "comparaSongs: "+usuario+"+"+anioActual+"+"+disActual+"+"+canActual+": el título de la song número "+z+" debería ser '<pre>"+pCanciones.get(z).getTitulo()+"</pre>', pero es '<pre>"+xCanciones.get(z).getTitulo()+"</pre>'"); 
	    			throw new ExcepcionChecker(cf);
	    	    }

	    	    if (!xCanciones.get(z).getDescripcion().equals(pCanciones.get(z).getDescripcion())) {
	    			cf = new CheckerFailure("", "20_DIFS", "comparaSongs: "+usuario+"+"+anioActual+"+"+disActual+"+"+canActual+": la descripción de la song número "+z+" debería ser '<pre>"+pCanciones.get(z).getDescripcion()+"</pre>', pero es '<pre>"+xCanciones.get(z).getDescripcion()+"</pre>'"); 
	    			throw new ExcepcionChecker(cf);
	    	    }

				if (!comparaPremios(pCanciones.get(z).getPremios(), xCanciones.get(z).getPremios())) {
	    			cf = new CheckerFailure("", "20_DIFS", "comparaSongs: "+usuario+"+"+anioActual+"+"+disActual+"+"+canActual+": los premios de la song número "+z+" debería ser '<pre>"+pCanciones.get(z).getPremios()+"</pre>', pero es '<pre>"+xCanciones.get(z).getPremios()+"</pre>'"); 	     
	    			throw new ExcepcionChecker(cf);
				}
	    }
	    
	    return;  
	}
	
	// auxiliar del método anterior
	
	private static boolean comparaPremios (String pPremios, String xPremios) {
		
		String premio;
		String plista[] = pPremios.split(" ");
		String xlista[] = xPremios.split(" ");
		
		if (plista.length != xlista.length) return false;
		
		for (int x=0; x < plista.length; x++) {
			premio = plista[x];
			
			if (Arrays.asList(xlista).contains(premio)) continue;
			else return false;
		}
		
		return true;
	}
		

}
