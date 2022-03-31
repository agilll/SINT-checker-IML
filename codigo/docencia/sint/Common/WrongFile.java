/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Prácticas de SINT  (paquete común a todas las prácticas)
 *
 *    Autor: Alberto Gil Solla
 ****************************************************************/


// objeto para almacenar la información sobre un fichero que se ha detectado erróneo (no well-formed o inválido)

package docencia.sint.Common;

import java.util.ArrayList;

public class WrongFile  implements Comparable<WrongFile>
{    
	private String file;
	private ArrayList<String> causas;

	public WrongFile (String fichero, ArrayList<String> motivos) 
	{
        file = fichero;
        causas = new ArrayList<String>(new ArrayList<String>(motivos));
    }
	
	public WrongFile (String fichero, String motivo) 
	{
        file = fichero;
        causas = new ArrayList<String>();
        causas.add(motivo);
    }
	
	
	public void addCausa(String motivo) {
	     causas.add(motivo);
	}
	
	public String getFile() {
		return file;
	}
	
	public ArrayList<String> getCausas() {
		return causas;
	}
	
	public int compareTo(WrongFile segundoFichero) {
		return (this.file.compareTo(segundoFichero.file));
	}
	
	public static boolean isWrongFileInList (String fich, ArrayList<WrongFile> lista) {
		
		for (WrongFile wf : lista) {
			if (fich.equals(wf.getFile())) return true;
		}
		return false;
	}
	
}