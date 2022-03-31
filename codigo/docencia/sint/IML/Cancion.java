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
import java.util.Comparator;

// objeto Cancion, que almacena su idc, titulo, duracion, descripcion, genero y los premios de su disco
// se usa también en el checker??

public class Cancion implements Comparable<Cancion> {

	String idc, titulo, descripcion, genero;
	int duracion;
	String premios; // de su disco


	public Cancion (String id, String ti, int du, String de, String ge, String pr)  {
		idc = id;
		titulo = ti;
		duracion = du;
		descripcion = de;
		genero = ge;
		premios = pr;
	}
	
	public String getIDC () {
		return idc;
	}

	public String getTitulo () {
		return titulo;
	}

	public int getDuracion () {
		return duracion;
	}
	
	public String getDescripcion () {
		return descripcion;
	}
	
	public String getGenero () {
		return genero;
	}
	
	public String getPremios () {
		return premios;
	}


	
	// para ver si esta cancion ya está contenida en la lista que se le pasa
	
	public boolean isContainedInList (ArrayList<Cancion> listaCanciones) {

		String idc;

		for (int x=0; x < listaCanciones.size(); x++) {
			idc = listaCanciones.get(x).getIDC();
			if (idc.equals(this.getIDC())) return true;
		}

		return false;
	}

	// orden principal: orden alfabético 
	
	public int compareTo(Cancion segundaCancion) {
		return (this.titulo.compareTo(segundaCancion.titulo));
	}
	
	// orden alternativo: por duración de la canción
	
	static final Comparator<Cancion> DURACION = 
			new Comparator<Cancion>() {
		public int compare(Cancion c1, Cancion c2) {
			if (c1.duracion < c2.duracion)  return -1;
			else return 1;
		}
	};
	
	// orden alternativo: por clasificadas por género y dentro del género alfabéticamente
	
	static final Comparator<Cancion> GENERO = 
			new Comparator<Cancion>() {
		public int compare(Cancion c1, Cancion c2) {
			if (c1.genero.compareTo(c2.genero) < 0 )  return -1;
			else 
				if (c1.genero.compareTo(c2.genero) > 0 )  return 1;
				else return c1.titulo.compareTo(c2.titulo);
		}
	};
	
}



