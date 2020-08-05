
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

public class ExcepcionChecker extends Exception 
{    
	private static final long serialVersionUID = 1L;
	
	private CheckerFailure checkerFailure;

	public ExcepcionChecker (CheckerFailure cf) 
	{
		checkerFailure = cf;
    }
	
	public CheckerFailure getCheckerFailure() {
		return checkerFailure;
	}
}