package PLA4_Actividad;

import java.util.HashMap;

public class MenuCRUD extends Menu {
  final static String CABMENU = "Elección tipo operación CRUD";
  enum enmOpciones {
    A("Ver toodos los registros", "OpsCRUD"), B("Ver un registro", "OpsCRUD"),
    C("Añadir un registro", "OpsCRUD"), D("Modificar un registro", "OpsCRUD"), 
    E("Eliminar un registro", "OpsCRUD"), Z("Atrás", "MenuTabla");
    private final String opcion, irMenu;
    enmOpciones(String strO, String strIM){opcion = strO; irMenu = strIM;}
    public String getO() {return opcion;}
    public String getIM() {return irMenu;}
  } 

  static void iniciar(HashMap<String, String> hshParametros) {
    alstOpciones.clear();
    for(enmOpciones o : enmOpciones.values()) {alstOpciones.add(o.getO());}
    mostrarOpciones(CABMENU, alstOpciones);
    
    if (hshParametros.containsKey("CRUDSel")) {hshParametros.remove("CRUDSel");}
    int eleccion = elegir();
    /*Introduzco en el HashMap la clave para la operación CRUD escogida*/
    hshParametros.put("CRUDSel", Uti.abc(eleccion - 1));

    try {
      //OpsCRUD.iniciar(hshParametros);
      Class clase = Class.forName("PLA4_Actividad." + enmOpciones.values()[eleccion - 1].getIM());
      metodo = clase.getDeclaredMethod("iniciar", HashMap.class);
      metodo.invoke(null, hshParametros);
    } catch(Exception e){System.out.println(e);}
  }
}