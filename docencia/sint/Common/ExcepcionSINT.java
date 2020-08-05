/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Prácticas de SINT  (paquete común a todas las prácticas)
 *
 *    Autor: Alberto Gil Solla
 ****************************************************************/


// excepción común para todos los checkers
package docencia.sint.Common;

public class ExcepcionSINT extends Exception 
{    
	private static final long serialVersionUID = 1L;

	public ExcepcionSINT (String msg) 
	{
        super(msg);
    }
}