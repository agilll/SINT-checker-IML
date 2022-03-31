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
// son mensajes para las prácticas, comunes a todas, y alguno también se utiliza en los checkers

package docencia.sint.Common;

import java.util.HashMap;

public class MsCP {

	static HashMap<Integer, String[]> mapMsgs = new HashMap<Integer, String[]>();   // el hashmap de mensajes comunes a todas las prácticas

	public static final int CPC00=0, CPC01=1, CPC02=2;
	public static final int CP00=100, CP01=101, CP02=102, CP03=103, CP04=104, CP05=105, CP06=106, CP07=107, CP08=108, CP09=109;


	static {
		mapMsgs.put(CPC00, new String[] {"Enviar", "Send"});
		mapMsgs.put(CPC01, new String[] {"Inicio", "Home"});
		mapMsgs.put(CPC02, new String[] {"Atrás", "Back"});

		mapMsgs.put(CP00, new String[] {"Bienvenido a este servicio", "Welcome to this service"});
		mapMsgs.put(CP01, new String[] {"Ver los ficheros erróneos", "Show error files"});
		mapMsgs.put(CP02, new String[] {"Ocultar los ficheros erróneos", "Hide error files"});
		mapMsgs.put(CP03, new String[] {"Selecciona una consulta:", "Please, select a query:"});
		mapMsgs.put(CP04, new String[] {"Ficheros con warnings: ", "Files with warnings: "});
		mapMsgs.put(CP05, new String[] {"Ficheros con errores: ", "Files with errors: "});
		mapMsgs.put(CP06, new String[] {"Ficheros con errores fatales: ", "Files with fatal errors: "});
		mapMsgs.put(CP07, new String[] {"Este es el resultado de la consulta: ", "This is the query result: "});
		mapMsgs.put(CP08, new String[] {"No param:", "No param:"});
		mapMsgs.put(CP09, new String[] {"el parámetro %s tiene un valor incorrecto (%s)", "parameter %s has a wrong value (%s)"});
	}


		// obtiene un mensaje (id) dependiendo del idioma (lang = "es" o "en")
		public static String getMsg(int id, String lang) {
			 String value[] = mapMsgs.get(id);
			 if (value == null) return "ERROR MsCP.getMsg key "+Integer.toString(id);
			 if (lang.equals("en"))  return value[1];
			 else return value[0];
		}
}
