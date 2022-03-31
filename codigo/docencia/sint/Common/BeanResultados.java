package docencia.sint.Common;

public class BeanResultados {

    private String cssFile;
    private String title;
    private String lang;
    private String curso;
    private int numCuentas;
    private int esProfesor;
    private String created;
    	   
    public BeanResultados ()  {}   

    public String getCssFile() {
	  return cssFile;
    }
    
    public String getTitle() {
	  return title;
    }

    public String getLang() {
	  return lang;
    }

    public String getCurso() {
	  return curso;
    }
	   
    public int getNumCuentas() {
	  return numCuentas;
    }

    public int getEsProfesor() {
	  return esProfesor;
    }

    public String getCreated() {
	  return created;
    }


    public void setCssFile(String c) {
	 cssFile = c;
    }
    
    public void setTitle(String t) {
	 title = t;
    }

    public void setLang(String l) {
	  lang = l;
    }

    public void setCurso(String c) {
	 curso = c;
    }
	   
    public void setNumCuentas(int n) {
	 numCuentas = n;
    }

    public void setEsProfesor(int e) {
        esProfesor = e;
    }

    public void setCreated(String c) {
        created= c;
    }

}
