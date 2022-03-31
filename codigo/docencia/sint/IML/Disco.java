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


package docencia.sint.IML;

import java.util.ArrayList;

// objeto Disco que almacena idd, título, premios, interprete, e idiomas
// se usa también en el checker ??

public class Disco implements Comparable<Disco> {

	String idd;
	String titulo;
    String interprete;  
	String idiomas;
	String premios; 
	
	public Disco (String id, String ti, String in, String idi, String pr)  {
		idd = id;
		titulo = ti;
		interprete = in;
		idiomas = idi;	
		premios = pr;
	}
		
	
	public String getIDD () {
		return idd;
	}
	
	public String getTitulo () {
		return titulo;
	}
	
	public String getInterprete () {
		return interprete;
	}

	public String getIdiomas () {
		return idiomas;
	}
	
	public String getPremios () {
		return premios;
	}
	


	// para ver si este Disco ya está contenida en la lista que se le pasa
	
	public boolean isContainedInList (ArrayList<Disco> listaDiscos) {

		String disco;

		for (int x=0; x < listaDiscos.size(); x++) {
			disco = listaDiscos.get(x).getTitulo();
			if (disco.equals(this.getTitulo())) return true;
		}

		return false;
	}

	
	// orden principal: agrupados por intérprete, dispuestos estos en orden alfabético
	// para cada intérprete, por orden alfabético
	
	public int compareTo (Disco segundoDisco) {
		if (this.interprete.compareTo(segundoDisco.interprete) == 0)
			return this.titulo.compareTo(segundoDisco.titulo);
		else
			return this.interprete.compareTo(segundoDisco.interprete);
	}
	
	

}


