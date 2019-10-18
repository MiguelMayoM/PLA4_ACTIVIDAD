package PLA4_Actividad;

import java.util.HashMap;

public class MenuTabla extends Menu {
  final static String CABMENU = "Elección de TABLA con la que trabajar";
  /*Aquí también podría hallar las tablas que se encuentran en la BD y entonces
    darlas a elegir, y entonces el código sería cerrado*/
  enum enmOpciones {
    A("Producto", "MenuCRUD"), B("Proveedor", "MenuCRUD"), 
    Z("Salir", "");
    private final String opcion, irMenu;
    enmOpciones(String strO, String strIM){opcion = strO; irMenu = strIM;}
    public String getO() {return opcion;}
    public String getIM() {return irMenu;}
  }
  
  static void iniciar(HashMap<String, String> hshParametros) {
    alstOpciones.clear();
    /*Cargo las opciones del menú en el ArrayList para seguidamente mostrarlas*/
    for(enmOpciones o : enmOpciones.values()) {alstOpciones.add(o.getO());}
    mostrarOpciones(CABMENU, alstOpciones);
    
    /*Elegimos una opción, que nos llevará a un nuevo menú o a un método final que
      ejecutar. Si la elección conlleva el establecimiento de un parámetro, como
      ahora es el caso con el nombre de la tabla elegida, antes elimino esa clave
      del hash de parámetros si es que ya la había fijado anteriormente.*/
    try{
      if (hshParametros.containsKey("TablaSel")) {hshParametros.remove("TablaSel");}
    } catch(Exception e) {}
    /*Y elijo una opción de las posibles*/
    int eleccion = elegir();
    /*Sólo en el caso del menú de nivel superior, éste, miro si se ha escogido la
      opción para Salir*/
    if (enmOpciones.values()[eleccion - 1].getO().equals("Salir")) {System.exit(0);}
    
    /*Introduzco en el Hash la clave para la tabla escogida*/
    hshParametros.put("TablaSel", alstOpciones.get(eleccion - 1));
      
    /*Me pide ejecutar dentro de un try*/
    try {
      Class clase = Class.forName("PLA4_Actividad." + enmOpciones.values()[eleccion - 1].getIM());
      metodo = clase.getDeclaredMethod("iniciar", HashMap.class);
      /*Invoco con parámetros para que me sirva cuando me voy de los menús hacia
        un método externo y final, como cualquiera de las operaciones CRUD*/
      metodo.invoke(null, hshParametros);
    } catch(Exception e){System.out.println(e);}
  }
}