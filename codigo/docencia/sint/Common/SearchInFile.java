/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Prácticas de SINT  (paquete común a todas las prácticas)
 *
 *    Autor: Alberto Gil Solla
 ****************************************************************/

// en esta clase van variables y métodos usados para la búsqueda en ficheros

package docencia.sint.Common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class SearchInFile {

		public SearchInFile () {
				return;
			}

		public int isInFile (String cad, File fd)  throws IOException {
			return searchString(cad, fd, false);
		}

		public int isInFile(String cad, File fd, boolean log)  throws IOException {
			return searchString(cad, fd, log);
		}


		public int searchString (String cad, File fd, boolean log)  throws IOException {
				String line, line2;
				int veces = 0, idx1, idx2;
				boolean inComment = false;

				BufferedReader br = null;

				br = new BufferedReader(new FileReader(fd));
				line = br.readLine();

				while (line != null)  // procesamos cada línea hasta acabar
				{
						line = line.trim();

						if (inComment) {  // estamos dentro de un comentario
										 idx1 = line.indexOf("*/");
										 if (idx1 == -1) {  // si no contiene */ leemos otra línea
														 line = br.readLine();
														 continue;
										 }
										 else {  // contiene */, se terminó el comentario
														 inComment = false;
														 line = line.substring(idx1+2);  // continuamos procesando la línea tras el */
														 continue;
										 }
						}


					 idx1 = line.indexOf("/*");
					 idx2 = line.indexOf("//");

					 // la línea no contiene inicio de comentario
					 if ((idx1 == -1) && (idx2 == -1)) {
							 if ((line.startsWith("System.out.print")) || (line.startsWith("System.err.print"))) {
									 line = br.readLine();
										continue;  // descartamos los prints
								}
								if (line.contains(cad)) veces++;  // buscamos la cadena
								line = br.readLine();
								continue;
					 }

							 // contiene un //, pero no un /*
					 if ( (idx1 == -1) && (idx2 != -1)) {
							 line = line.substring(0, idx2);  // nos quedamos con la parte anterior y la procesamos
							 continue;
					 }

							 // contiene un /*, pero no un //
					 if ( (idx1 != -1) && (idx2 == -1)) {
									 inComment = true;
									 line2 = line.substring(0, idx1);  // nos quedamos con la parte antes del principio del comentario
									 if (line2.startsWith("System.out.print")) {
											 line = line.substring(idx1+2);  // nos quedamos con la parte posterior al inicio del comentario y la procesamos
											 continue;  // descartamos los prints
										}

									 if (line2.contains(cad)) veces++;  // y buscamos la cadena
									 line = line.substring(idx1+2);  // nos quedamos con la parte posterior al inicio del comentario y la procesamos
									 continue;
					 }
						 // contiene un /*  y un //
							 // el // está antes
					 if (idx2 < idx1) {
									line = line.substring(0, idx2);  // nos quedamos con la parte anterior y la procesamos
									continue;
					 }

							 // el /* está antes
					 inComment = true;
					 line2 = line.substring(0, idx1);  // nos quedamos con la parte antes del principio del comentario
					 if ((line.startsWith("System.out.print")) || (line.startsWith("System.err.print"))) {
											 line = line.substring(idx1+2);  // nos quedamos con la parte posterior al inicio del comentario y la procesamos
											 continue;  // descartamos los prints
					 }

					 if (line2.contains(cad)) veces++;  // y buscamos la cadena
					 line = line.substring(idx1+2);  // nos quedamos con la parte posterior al inicio del comentario y la procesamos
					 continue;

				}

			 if (br != null)
			 br.close();

			 return veces;
		}



	public void log(String linea, boolean log) {
																			if (log) {
																					System.out.println(linea);
																					System.out.flush();
																			}
	}


}
