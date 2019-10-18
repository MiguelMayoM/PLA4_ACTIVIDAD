package PLA4_Actividad;

//import java.lang.invoke.MethodHandles;
import static PLA4_Actividad.Uti.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class Menu {
  /*ArrayList para guardar las opciones que se pueden elegir en cada subclase de Menu*/
  static ArrayList<String> alstOpciones = new ArrayList<String>(0);
  /*Voy a utilizar un HashMap para guardar las opciones que se vayan escogiendo
    con un nombre específico. Hago esto para intentar que, de alguna manera, el
    código sea cerrado a modificación pero abierto a extensión. Por ejemplo, en
    el primer Menú escojo una Tabla, pues la guardaré con un nombre de clave
    "tablaSeleccionada". Así, si en algún Menú posterior quiero realizar una acción sobre
    una "tablaSeleccionada", la tendré en esta clave del hash. Si por lo que sea
    no existe, será porque se ha eliminado esta "dependencia" y me dará error.
    Pero la solución del error no sería modificar algún archivo, sino añadir todo
    un archivo, el archivo MenuTabla.java, cerrado a modificación, a excepción
    del enum inicial*/
  static HashMap<String, String> hshParametros = new HashMap<String, String>();
  
  /*Esto no me gusta mucho, es para Java 7+... También podría poner el nombre del
    paquete sin más, porque si lo estamos modificamos arriba, ya estamos yendo
    en contra de cerrado a modificación. Se supone que si utilizara estos Menús
    en otro proyecto, los importaria de este paquete, sin cambiar nada, pues esa
    es la gracia del paquete, tener un código metido en un paquete que sirva para
    una tarea en especial e importarlo.*/
  //static String paquete = MethodHandles.lookup().lookupClass().getPackage().getName();
  /*Esto lo declaro aquí para no tener que hacer un import en cada subclase. Me
    sirve para llamar al método static elegido del siguiente menú al que vaya
    desde el menú que ese momento esté visitando*/
  static Method metodo;
  
  /*Así es como empieza todo...*/
  static void iniciar() {
    MenuTabla.iniciar(hshParametros);
  }
  
  static void mostrarOpciones(String cabMenu, ArrayList<String> alstOpciones) {
    impln("\n", cabMenu);
    Subraya(cabMenu.length(),"-");
    
    for(int i = 0; i < alstOpciones.size(); i++) {
      impln(abc(i+1), ". ", alstOpciones.get(i));
    }
    //uyc.impln(uyc.abc(alstOpciones.size() + 1), ". Salir/Atrás");
    Subraya(cabMenu.length(),"-");
  }
  
  static int elegir() {
    imp("Elija una opción: ");
    return Elegir(alstOpciones.size());
  }
}