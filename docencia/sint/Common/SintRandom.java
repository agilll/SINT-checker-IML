/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Prácticas de SINT  (paquete común a todas las prácticas)
 *
 *    Autor: Alberto Gil Solla
 ****************************************************************/


package docencia.sint.Common;

import java.util.Random;

public class SintRandom {
	
	// variables estáticas para un Randomizer compartido
	static Random sharedRandomizer;
	static int sharedRandom;
	
	// variables de objeto para un randomizer propio
	Random privateReandomizer;
	int privateRandom;
	
	
	// generador de números aleatorios privado
	
	// para crear un objeto SRandom
	
	public SintRandom () {
		try {
			privateReandomizer = new Random(System.currentTimeMillis());
		}
		catch (Exception ex) {}
	}
	
	// para obtener valores al azar, entre min y max
	
	public int getRandomNumero (int min, int max) {
		privateRandom = min + privateReandomizer.nextInt(max-min+1);
		return privateRandom;
	}
	
	
	
	
	
	// generador de números alatorios global, compartido
	
    // para inicializarlo
	
	public static void init () {
		try {
			sharedRandomizer = new Random(System.currentTimeMillis());
		}
		catch (Exception ex) {}
	}
	
	
	// para obtener valores al azar, entre min y max
	
	public static int getRandomNumber (int min, int max) {
		sharedRandom = min + sharedRandomizer.nextInt(max-min+1);
		return sharedRandom;
	}
	

	
}
