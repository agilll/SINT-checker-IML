/****************************************************************
 *    SERVICIOS DE INTERNET
 *    EE TELECOMUNICACIÓN
 *    UNIVERSIDAD DE VIGO
 *
 *    Prácticas de SINT  (paquete común a todas las prácticas)
 *
 *    Autor: Alberto Gil Solla
 ****************************************************************/


// filtro para comprobar la passwd

package docencia.sint.Common;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class PasswdFilter implements Filter {

	    protected FilterConfig filterConfig;

	    // Se llama cuando se crea una instancia del filtro.

	    public void init(FilterConfig filterConfig) throws ServletException {
	       this.filterConfig = filterConfig;
	    }

	    public void destroy() {
	       this.filterConfig = null;
	    }

	    // Se llama por cada solicitud asociada a este filtro.

	    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	        throws java.io.IOException, ServletException   {

	        // comprobaciones del filtro

			       String passwd = request.getParameter("p");

             // si no hay passwd o es incorrecta, el filtro devuelve una respuesta y termina
		         if (passwd == null) {
		    		       CommonSINT.doBadRequest("no passwd", request, response);
                   return;
			       }

             if (!passwd.equals(CommonSINT.PPWD)) {
                   CommonSINT.doBadRequest("bad passwd", request, response);
        				   return;
        		 }

             // si todo va bien, el filtro permite que continúe la cadena de la llamada
	           chain.doFilter(request, response);
	    }

}
