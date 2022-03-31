/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Prácticas de SINT  (paquete común a todas las prácticas)
 *
 *    Autor: Alberto Gil Solla
 ****************************************************************/

// etiquetas de mensajes en distintos idiomas para varias clases
// son mensajes para los checkers, comunes a todos

package docencia.sint.Common;

import java.util.HashMap;

public class MsCC {

	static HashMap<Integer, String[]> mapMsgs = new HashMap<Integer, String[]>();   // el hashmap de mensajes comunes a todas las prácticas


	public static final int CC00=200, CC01=201, CC02=202, CC03=203, CC04=204, CC05=205, CC06=206, CC07=207, CC08=208, CC09=209;
	public static final int CC10=210, CC11=211, CC12=212, CC13=213, CC14=214, CC15=215, CC16=216, CC17=217, CC19=219;
	public static final int CC20=220, CC21=221, CC22=222, CC23=223, CC24=224, CC25=225, CC26=226, CC27=227, CC28=228, CC29=229;
	public static final int CC30=230, CC31=231, CC32=232, CC33=233, CC34=234, CC35=235, CC36=236, CC37=237, CC38=238, CC39=239;
	public static final int CC40=240, CC41=241, CC42=242, CC43=243, CC44=244, CC45=245, CC46=246, CC47=247, CC48=248, CC49=249;
	public static final int CC50=250, CC51=251, CC52=252, CC53=253, CC54=254, CC55=255, CC56=256, CC57=257, CC58=258, CC59=259;
	public static final int CC60=260, CC61=261, CC62=262, CC63=263, CC64=264, CC65=265, CC66=266, CC67=267, CC68=268, CC69=269;

	static {

		mapMsgs.put(CC00, new String[] {"Servicios de Internet", "Internet Services"});
		mapMsgs.put(CC01, new String[] {"EE Telecomunicación (Universidad de Vigo)", "EE Telecomunicación (University of Vigo)"});
		mapMsgs.put(CC02, new String[] {"Corregir un servicio", "Check a service"});
		mapMsgs.put(CC03, new String[] {"Consulta", "Query"});
		mapMsgs.put(CC04, new String[] {"Corrigiendo un servicio", "Checking a service"});
		mapMsgs.put(CC05, new String[] {"Introduzca el número de la cuenta SINT a comprobar: ", "Type the number of the SINT account to check: "});
		mapMsgs.put(CC06, new String[] {"URL del servicio del alumno: ", "Service URL: "});
		mapMsgs.put(CC07, new String[] {"Passwd del servicio (10 letras o números): ", "Service password (10 letters or digits): "});
		mapMsgs.put(CC08, new String[] {"Comprobando el servicio del usuario ", "Checking the service of the user "});
		mapMsgs.put(CC09, new String[] {"Resultado: ", "Result: "});

		mapMsgs.put(CC10, new String[] {"La passwd proporcionada no coincide con la almacenada en el sistema para ", "Provided passwd doesn't match the one in the system "});
		mapMsgs.put(CC11, new String[] {"Resultado incorrecto comprobando el servicio de ", "Wrong result checking service of "});
		mapMsgs.put(CC12, new String[] {"Error en los ficheros fuente", "Error in source files"});
		mapMsgs.put(CC13, new String[] {"Error al comprobar si el servicio del estudiante está operativo", "Error checking if student service is ready"});
		mapMsgs.put(CC14, new String[] {"Diferencias en la lista de errores", "Differences in error lists"});

		mapMsgs.put(CC15, new String[] {"No existe o no se puede acceder al fichero ", "File does not exist or it can't be accessed: "});
		mapMsgs.put(CC16, new String[] {"Encontrada la cadena prohibida '%s' en el código", "Found forbidden string '%s' in code "});
		mapMsgs.put(CC17, new String[] {"Problema leyendo el fichero ", "Problem reading file "});
		mapMsgs.put(CC19, new String[] {"Fichero prohibido en esa ubicación: ", "File forbidden in such location:"});
		mapMsgs.put(CC20, new String[] {"debe aparecer una vez, y sólo una, en el fichero ", "must appear once, and only once, in the file"});
		mapMsgs.put(CC21, new String[] {"debe aparecer una vez, y sólo una, en el código, pero aparece ", "must appear once, and only once, in the code, but it appears "});
		mapMsgs.put(CC22, new String[] {"debe aparecer dos veces, y sólo dos, en el código, pero aparece ", "must appear twice, and only twice, in the code, but it appears "});
		mapMsgs.put(CC23, new String[] {"El servidor no responde", "The server does not answer"});
		mapMsgs.put(CC24, new String[] {"El servlet no está declarado", "The servlet is not declared"});
		mapMsgs.put(CC25, new String[] {"La codificación de caracteres recibida es incorrecta (no UTF-8)", "The received character encoding is not correct (not UTF-8)"});
		mapMsgs.put(CC26, new String[] {"No se ha encontrado la clase del servlet o ésta devolvió una excepción al pedir el estado", "Servlet class not found, or an exception was returned when asking for status without passwd"});
		mapMsgs.put(CC27, new String[] {"La respuesta al pedir el estado sin passwd está mal construida", "Answer not well-formed when requesting status without passwd"});
		mapMsgs.put(CC28, new String[] {"Error desconocido al realizar la solicitud de estado sin passwd", "Unknown error when requesting status without passwd"});
		mapMsgs.put(CC29, new String[] {"La respuesta al pedir el estado sin passwd es inválida, tiene ", "Answer invalid when requesting status without passwd, it has "});
		mapMsgs.put(CC30, new String[] {"Respuesta incorrecta al pedir el estado sin passwd", "Incorrect answer when requesting status without passwd"});
		mapMsgs.put(CC31, new String[] {"Responde con &lt;wrongRequest> pero no por 'no passwd', sino por: ", "It answers with &lt;wrongRequest> but not with 'no passwd', but with: "});
		mapMsgs.put(CC32, new String[] {"No ha contestado con &lt;wrongRequest> al no enviar passwd, sino con ", "It does not answer with &lt;wrongRequest> when passwd is missing, but with "});
		mapMsgs.put(CC33, new String[] {"La respuesta al pedir el estado con passwd está mal construida", "Answer not well-formed when requesting status wit passwd"});
		mapMsgs.put(CC34, new String[] {"Error desconocido al realizar la solicitud de estado con password", "Unknown error when requesting status with passwd"});
		mapMsgs.put(CC35, new String[] {"La respuesta al pedir el estado con passwd es inválida, tiene ", "Answer invalid when requesting status with passwd, it has "});
		mapMsgs.put(CC36, new String[] {"El servicio dice que la passwd enviada es incorrecta", "The service says the sent passwd is incorrect"});
		mapMsgs.put(CC37, new String[] {"Al pedir el estado, el servicio contesta con &lt;wrongRequest> con razón desconocida: ", "Requesting status, the service answers with &lt;wrongRequest> with unknown reason: "});
		mapMsgs.put(CC38, new String[] {"Al pedir el estado, el servicio contesta con un tag incorrecto ", "Requesting status, the service answers with an incorrect tag "});
		mapMsgs.put(CC39, new String[] {"Todavía no se ha creado el contexto (o este fue eliminado tras un error) de ", "The context has not been created yet (o it has been removed because an error) for "});

		mapMsgs.put(CC40, new String[] {"falta uno de los parámetros (alumnoP, servicioAluP)", "one parameter missing (alumnoP, servicioAluP)"});
		mapMsgs.put(CC41, new String[] {"Imposible recuperar la passwd del contexto de ", "Unable to recover the context passwd for "});
		mapMsgs.put(CC42, new String[] {"No se ha recibido la passwd de ", "Passwd not received for "});
		mapMsgs.put(CC43, new String[] {"Error al preguntar por el estado de la práctica del profesor", "Error requesting status of teacher practice"});
		mapMsgs.put(CC44, new String[] {"Excepción solicitando el informe de errores de ", "Exception requesting errors report to "});
		mapMsgs.put(CC45, new String[] {"Deberían haberse recibido %d warnings, pero se reciben %d", "It should have been received %d warnings, but %d were received"});
		mapMsgs.put(CC46, new String[] {"El warning número %d no es correcto. Fichero: %s", "Incorrect warning number %d. File: %s"});
		mapMsgs.put(CC47, new String[] {"Falta un 'error': %s", "One 'error' missing: %s"});
		mapMsgs.put(CC48, new String[] {"Se recibe un 'error' incorrecto: %s", "Received a wrong 'error': %s"});
		mapMsgs.put(CC49, new String[] {"Deberían haberse recibido %d fatal errors, pero se reciben %d", "It should have been received %d fatal errors, but %d were received"});
		mapMsgs.put(CC50, new String[] {"El fatal error número %d no es correcto. Fichero: %s", "Incorrect fatal error number %d. File: %s"});

		mapMsgs.put(CC51, new String[] {"Excepción al solicitar/parsear la lista de ficheros erróneos", "Exception requesting/parsing the list of wrong files"});
		mapMsgs.put(CC52, new String[] {"Resultado inválido, %s al parsear la lista de ficheros erróneos", "Invalid result, %s parsing the list of wrong files"});
		mapMsgs.put(CC53, new String[] {"El parser devuelve 'null' al parsear la lista de ficheros erróneos", "Parser returns 'null' parsing the list of wrong files"});
		mapMsgs.put(CC54, new String[] {"Al solicitar la lista de ficheros erróneos se recibe &lt;wrongRequest>", "Requesting the list of wrong files returns &lt;wrongRequest>"});
		mapMsgs.put(CC55, new String[] {"Al solicitar la lista de ficheros erróneos se recibe una respuesta incorrecta: ", "Requesting the list of wrong files returns a wrong answer: "});

		mapMsgs.put(CC56, new String[] {"Debería devolver ", "Should return "});
		mapMsgs.put(CC57, new String[] {"pero devuelve ", "but returns"});

		mapMsgs.put(CC58, new String[] {"UTF-8 no soportado para parametro ", "UTF-8 not supported for parameter "});
		mapMsgs.put(CC59, new String[] {"Al solicitar y parsear la lista de resultados sin el parámetro obligatorio ", "Requesting the results without mandatory parameter "});

		mapMsgs.put(CC60, new String[] {"Respuesta inválida (tiene %s) al solicitar y parsear la lista de resultados sin el parámetro obligatorio ", "Invalid answer (with %s) requestimg the list of results without mandatory parameter "});
		mapMsgs.put(CC61, new String[] {"Se recibe 'null' al solicitar la lista de resultados sin el parámetro obligatorio ", "'null' is received requestimg the list of results without mandatory parameter "});

		mapMsgs.put(CC62, new String[] {"Responde con &lt;wrongRequest> pero no por 'no param:%s', sino por: ", "It answers with &lt;wrongRequest> but not with 'no param:%s', but with: "});
		mapMsgs.put(CC63, new String[] {"No se recibe &lt;wrongRequest> al solicitar la lista de resultados sin el parámetro obligatorio ", "&lt;wrongRequest> is not received requestimg the list of results without mandatory parameter "});
		mapMsgs.put(CC64, new String[] {"No responde correctamente si falta algún parámetro obligatorio", "Wrong answer if some required parameter is missing"});
	}


		// obtiene un mensaje (id) dependiendo del idioma (lang = "es" o "en")
		public static String getMsg(int id, String lang) {
			 String value[] = mapMsgs.get(id);
			 if (value == null) return "ERROR MsCC.getMsg key "+Integer.toString(id);
			 if (lang.equals("en"))  return value[1];
			 else return value[0];
		}
}
