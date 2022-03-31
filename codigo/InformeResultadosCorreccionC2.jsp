
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<html>
<head><meta charset='utf-8'/>
<title>${db.title}</title>
<link rel='stylesheet'  type='text/css' href='${db.cssFile}'/>
</head>

<body>
<div id='asignatura'>Servicios de Internet </div>
<div id='grado'>EE Telecomunicación (Universidad de Vigo) </div>
<div id='servicio'>Comprobación de servicios sobre ${db.lang} <br> Curso ${db.curso}<hr></div>

<h2>Consulta 2</h2>

<script>
var numOK=0, numSC=0;
var el;
var source = new EventSource('?screenP=242&numCuentasP=${db.numCuentas}')
source.onmessage = function (event) {
  var rd = event.data;
  var rdl = rd.split(',');
  var num = parseInt(rdl[0]);
  if (num < 0) {
      source.close()
      document.getElementById('CFIN').innerHTML = 'FINALIZADO!';
  } else {
      var pegar = num+'&nbsp;'
      var code = rdl[1];

      switch (code) {
        case 'OK':
	     numOK++;
	     document.getElementById('nOK').innerHTML = numOK;
	     document.getElementById('COK').innerHTML += pegar;
	     break;

        case 'FILES':
	     el = document.getElementById('CFILES')
	     el.innerHTML += pegar;
	     el.style.display = 'block'
	     break;

        case 'DIFS':
	     el = document.getElementById('CDIFS')
	     el.innerHTML += pegar;
	     el.style.display = 'block'	     
	     break;

        case 'IOEXCEPTION':
	     el = document.getElementById('CIOEXCEPTION')
	     el.innerHTML += pegar;
	     el.style.display = 'block'	     
	     break;

        case 'ENCODING':
	     el = document.getElementById('CENCODING')
	     el.innerHTML += pegar;
	     el.style.display = 'block'	     
	     break;

        case 'BF':
	     el = document.getElementById('CBF')
	     el.innerHTML += pegar;
	     el.style.display = 'block'	     
	     break;

        case 'INVALID':
	     el = document.getElementById('CINVALID')
	     el.innerHTML += pegar;
	     el.style.display = 'block'	     
	     break;

        case 'FILENOTFOUND':
	     el = document.getElementById('CFILENOTFOUND')
	     el.innerHTML += pegar;
	     el.style.display = 'block'	     
	     break;
	     
        case 'NOCONTEXT':
	     numSC++
	     document.getElementById('nSC').innerHTML = numSC;	     
	     el = document.getElementById('CNOCONTEXT')
	     el.innerHTML += pegar;
	     break;

        default:
	     el = document.getElementById('CDEFAULT')
	     el.innerHTML += pegar;
	     el.style.display = 'block'
	     break;
      }	     
  }
 }
</script>


<h3>Corrección de todos los servicios (${db.numCuentas})</h3>

<h3 style='color: green'>Servicios OK (<span id='nOK'></span>): <span id='COK' style='overflow-wrap: break-word;' > </span></h3>
<h3 id='CFILES' style='overflow-wrap: break-word; color: red; display: none'>Servicios con errores en los ficheros: </h3>
<h3 id='CDIFS' style='color: red; overflow-wrap: break-word; display: none'>Servicios con diferencias respecto a los resultados esperados: </h3>
<h3 id='CIOEXCEPTION' style='color: red; overflow-wrap: break-word; display: none'>Servicios sin la clase del servlet o que produjo excepción: </h3>
<h3 id='CENCODING' style='color: red; overflow-wrap: break-word; display: none'>Servicios con codificación incorrecta: </h3>
<h3 id='CFILENOTFOUND' style='color: red; overflow-wrap: break-word; display: none'>Servicios sin el servlet declarado: </h3>

<h3>Servicios sin contexto (<span id='nSC'></span>): <span id='CNOCONTEXT' style='overflow-wrap: break-word;' > </span></h3>

<h3 id='CBF' style='color: red; overflow-wrap: break-word; display: none'>Servicios con respuesta mal formada: </h3>
<h3 id='CINVALID' style='color: red; overflow-wrap: break-word; display: none'>Servicios con respuesta inválida: </h3>
<h3 id='CDEFAULT' style='color: red; overflow-wrap: break-word; display: none'>Servicios con otro tipo de error: </h3>

<h3 style='color: green' id='CFIN'></h3>

<form>
<input type='hidden' name='p' value='si'><p>
<input class='home' type='submit' value='Inicio'>
</form>

<div id='foot'><hr>&copy; Alberto Gil Solla (${db.created})</div>

</body>
</html>
