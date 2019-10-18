package PLA4_Actividad;

import java.util.Scanner;

class Uti {
  final static String CABGENERAL = "Gestión de proveedores, productos y clientes de la empresa";

  static void imp(String... args) {for (String arg : args) {System.out.print(arg);}}
  static void impln(String... args) {for (String arg : args) {System.out.print(arg);} System.out.print("\n");}
  static String abc(int intN) {return Integer.toString(intN);}

  static void Subraya(int intLongitud, String strCaracter) {
    impln(new String(new char[intLongitud]).replace("\0", strCaracter));
  }  

  static void CabeceraGeneral() {
    impln(CABGENERAL);
    Subraya(CABGENERAL.length(),"=");
  }
  
  static Scanner scnEntrada = new Scanner(System.in);
    
  static int CompruebaEntero(String strEntrada) {
    int intEntero = 0;
    do {
      try {
        intEntero = Integer.parseInt(strEntrada);
        break;
      } catch (Exception e) {
        imp("No ha escrito un número entero. Vuelva a probar: ");
        strEntrada = scnEntrada.nextLine().trim();
      }
    }while(true);
    return intEntero;
  }
 
  static int Elegir(int intNumMax) {
    int intNumero;
    String strEntrada = scnEntrada.nextLine().trim();
    do{
      intNumero = CompruebaEntero(strEntrada);
      if ((intNumero >= 1) && (intNumero <= intNumMax)) {
        break;
      }
      imp("El número entero ha de estar entre 1 y ", abc(intNumMax), ". Vuelva a probar: ");
      strEntrada = scnEntrada.nextLine().trim();
    }while(true);
    return intNumero;
  }
}