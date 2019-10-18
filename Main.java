package PLA4_Actividad;

public class Main {
  /*Vamos a tener "tres" menús:
    1.- Primero hemos de escoger la Tabla con la que vamos a trabajar. Ahora sólo
        serán dos, Proveedor o Producto, pero, en un futuro, tal vez querramos
        operar con otras tablas. Podría poner estas opciones aquí en el Main y
        las podríamos pasar como parámetros al "MenuTabla" y así este último no
        habría que modificarlo pero prefiero ponerlas en cada archivo de Menu, en
        la parte superior para que sean fácilmente modificables. Es lo máximo
        cerrado a modificación que puedo hacer en este sentido. Por otra parte,
        si hiciera un switch o un if para elegir, ante el escenario de trabajar
        con otras tablas, tendría que modificarlo añadiendo las nuevas tablas,
        así que esto no lo voy a hacer así. Para permitir que la dinámica de
        elección quede cerrada a modificación, voy a crear una clase Menu que
        imprima opciones por pantalla, permita elegir opción y actuar en
        consecuencia, así como Salir/Atrás. Para ello se "iterará" sobre los
        elementos proporcionados en la parte superior del Menu.
    2.- Seguidamente, habiendo escogido el nombre de la Tabla con la que trabajar,
        el siguiente menú sí que será cerrado en sentido estricto, porque hacer
        CRUD implica realizar únicamente 4 tipo de operaciones.
    3.- Por último, al leer o borrar los registros, no habrá problema, pero al
        crearlos o modificarlos dependerá de las propiedades que tenga cada tabla,
        pero aquí también hallaremos los campos de cada tabla en cada caso
        particular e "iteraremos" sobre los mismos para que el código sea cerrado
        a modificación.
  */
  public static void main(String[] args) {
    Uti.CabeceraGeneral();
    Menu.iniciar();
  }
  
  /*Otra forma que he pensado después, tiene que ver con el hecho de que la Tabla
    es una dependencia del Menu CRUD. Por tanto, implementando los Menús de otra
    forma (no como están ahora, aunque utilice los mismos nombres de funciones),
    podría hacer algo del estilo (a semejanza del ejercicio anterior de PPTLS):
    String[] tablas = {"Proveedor", "Producto"};
    String tablaElegida = MenuTabla.elegir();
    Y entonces "inyectar" este dato en un MenuCRUD: todo sin instanciar, pues uso
    métodos estáticos
    MenuCRUD.elegir(tablaElegida);
  */
}