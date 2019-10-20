package PLA4_Actividad;

/****************************************************************************/
/* SUPOSICIÓN : TODAS LAS TABLAS TIENEN UNA SOLA COLUMNA INT AUTO_INCREMENT */
/****************************************************************************/
import static PLA4_Actividad.Uti.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.sql.SQLIntegrityConstraintViolationException;
import java.lang.reflect.Method;

/*Aunque el enunciado del ejercicio empieza con:
  "El programa nos debe permitir crear, modificar, eliminar y ver las tablas de
   Productos y Proveedores"
  Y podría parecer que he de "crear tablas", entiendo que las tablas ya las
  tenemos creadas, aunque estén vacías, por lo que dice después sobre el menú
  del programa, donde habla de "registros", es decir de "añadir registros", no
  de crear tablas.
  "Para ello sugiero tener un menú que nos pregunte si queremos tratar con
   Productos , Proveedores o salir. Posteriormente tendremos 4 opciones:
    1.- Ver registros
    2.- Añadir registro
    3.- Modificar registro
    4.- Eliminar registro.
  "
  Que también se podría comprobar en el primer menú si la tabla existe y si no
  crearla, pero entiendo que esto no se pide, pues habla de "tratar con", supongo
  que con tablas ya creadas.
  Voy a hacer como en el pdf de ejemplo y a dar la opción de ver todos los
  registros o sólo uno, por la siguiente razón: cuando los presente todos será
  con un formato de tabla, y cortaré aquellos campos que excedan cierto número
  de caracteres. Con ello, uno se hace una idea, pero si quiere consultar el
  registro completo, entonces doy esa opción para presentar los datos sin cortes.
*/

/****************************************************************************/
/* SUPOSICIÓN : TODAS LAS TABLAS TIENEN UNA SOLA COLUMNA INT AUTO_INCREMENT */
/****************************************************************************/
class OpsCRUD{
  static Scanner scnEntrada = new Scanner(System.in);
  
  static void iniciar(HashMap<String, String> hshParametros) {
    String strConexion = "jdbc:mysql://localhost:3306/";
    /*He usado el nuevo driver 8.0.18, que está en los archivos de instalación
      de MySQL. Este no da un warning de certificado como el v5, tal como se ha
      dicho por el foro, pero sí un error de zona horaria, que, a falta de una
      buena configuración, se puede saltar con alguna de estas dos opciones:*/
    String strParams = "?useLegacyDatetimeCode=false&serverTimezone=UTC";
    strParams = "?useLegacyDatetimeCode=false&serverTimezone=Europe/Madrid";
    /*Además, le añado otras opciones*/
    strParams += "&useSSL=false&allowPublicKeyRetrieval=true";
    strParams += "&useUnicode=true&characterEncoding=utf8";
    /*Si da error el &, se puede poner como &amp;*/
    String strBD = "empresa";
    String strU = "root";
    String strC = "";
    impln("Conectando con la base de datos ...");
    
    try {
      /*Con el nuevo driver 8.0.18 hay que intercalar ".cj."*/
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      Connection con = DriverManager.getConnection(strConexion + strBD
        + strParams, strU, strC);
      
      Statement stmt; //Sentencia;
      PreparedStatement pstmt; //SentPreparada;
      ResultSet rs = null; //Resultados;
      
      /*Parámetros escogidos en los menús, que hemos guardado en un HashMap*/
      String tabla = hshParametros.get("TablaSel");
      int CRUDSel = Integer.parseInt(hshParametros.get("CRUDSel"));
      /*************************/
      /* CANTIDAD de METADATOS */
      /*************************/
      /*Realizaremos una consulta de todos los datos de la Tabla escogida para
        obtener cantidad de datos que me harán falta para poder realizar una
        implementación general de las operaciones CRUD, que sirvan para cualquier
        tabla: por ejmplo, me interesa conocer el número de campos o columnas,
        sus nombres ...*/
      Object[] MD = Metadatos(con, tabla);
      /*Tengo que hacer castings*/
      int numCols = (Integer) (MD[0]);
      int ind0AutoInc = (Integer) (MD[1]);
      String formato = (String) (MD[2]);
      int largoTotal = (Integer) (MD[3]);
      String[] arrNombreCols = (String[]) (MD[4]);
      String[] arrTipoCols = (String[]) (MD[5]);
      int[] arrNullCols = (int[]) (MD[6]);
      int[] arrLongCols = (int[]) (MD[7]);
      int[] arrPrecisCols = (int[]) (MD[8]);
      int[] arrScaleCols = (int[]) (MD[9]);
      String[] arrCabecera = (String[]) (MD[10]);

      int eleccion;
      switch (CRUDSel) {
        /*----------------------*/
        /* LEER TODOS REGISTROS */
        /*----------------------*/
        case 0:
          /*Ahora no uso el número de registros, porque no elijo nada, pero en
            otras opciones sí*/
          imprimirTabla(con, tabla, numCols, largoTotal, formato, arrCabecera);
          break;
        
        /*-----------------------------------*/
        /* LEER UN REGISTRO SIN CORTAR DATOS */
        /*-----------------------------------*/
        case 1:
          imprimirTabla(con, tabla, numCols, largoTotal, formato, arrCabecera);
          imp("Introduzca el número de \"" + arrNombreCols[ind0AutoInc] +
          "\" de la fila que desee consultar en detalle (0 para Salir): ");
          /*ANTES CONTABA EL TOTAL DE REGISTROS Y PEDÍA QUE EL NÚMERO ESTUVIERA
            COMPRENDIDO EN ESE INTERVALO. No caí en los huecos que habrá cuando
            se eliminen registros intermedios. Está claro que si no existe, dará
            error la query, y puedo "catchearlo" y poner un mensaje por pantalla,
            pero quiero hacerlo de forma que no tenga que volver a entrar en el
            menú, que lo sepa ya ahora mismo. Y como lo voy a usar más abajo
            también, sí que voy a hacer una query de test*/
          eleccion = CompruebaRegistro(con, tabla, arrNombreCols[ind0AutoInc]);
          if (eleccion == 0){break;}
          pstmt = con.prepareStatement("SELECT * FROM " + tabla + 
                                       " WHERE " + arrNombreCols[ind0AutoInc] + "=?");
          /*Otra vez, los rs empiezan en 1 y los arrays en 0, por eso corrijo.
            Tal vez habría sido mejor tomar un criterio único*/
          
          pstmt.setInt(1, eleccion);
          rs = pstmt.executeQuery();
          rs.next();
          /*Imprimo todos los campos, uno en cada línea*/
          Subraya(largoTotal, "-");
          for (int i=1; i<=numCols; i++) {
            impln(arrNombreCols[i-1], ": ", rs.getString(i));
          }
          Subraya(largoTotal, "-");
          break;          
        
        /*--------------------*/
        /* AÑADIR UN REGISTRO */
        /*--------------------*/
        case 2:  
          impln("Introduzca los valores para cada campo. Respete las características.");
          /*Los recorro todos desde el primero, por si acaso el AUTO_INCREMENT
            no estuviera en el primer campo o columna y los guardo en un array
            tipo objeto para que admita todo tipo de valor*/
          /*Declarados fuera para que sirvan para otro case también*/
          Object[] valores = new Object[numCols - 1];
          /*En otro array guardo el tipo para usar el "set" adecuado del prepareStatement*/
          String[] tipo = new String[numCols - 1];
          /*Voy construyendo un String con los nombres de las columnas que habré
            de meter en la consulta*/
          String strCampos = "";
          /*Utilizo un índice distinto para saltar la columna del índice autoincrementable*/
          int j = 0;
          for (int i=1; i<=numCols; i++) {
            /*Si es el campo autoincrementable (), salto*/
            if (i == ind0AutoInc + 1) {continue;}
            strCampos += arrNombreCols[i-1] + ",";
            /*Al pedir el valor, doy toda la información del mismo para que el
              usuario se adapte: Obligatorio o no, Tipo y Longitud. */
            valores[j] = PedirValor(arrNombreCols[i-1], arrNullCols[i-1], 
                                    arrTipoCols[i-1], arrLongCols[i-1],
                                    arrPrecisCols[i-1], arrScaleCols[i-1]);
            tipo[j] = arrTipoCols[i-1];
            j++;
          }
          /*Quitamos la coma de más*/
          strCampos = strCampos.substring(0, strCampos.length() - 1);

          /*Prepararemos un String con tantas ? como campos haya que introducir
            en la tabla*/
          String strConsulta = "INSERT INTO " + tabla + " (" + strCampos + ") "
                             + "VALUES (?)";
          strConsulta = insertVarios(numCols - 1, strConsulta);
          pstmt = con.prepareStatement(strConsulta);

          /*Ahora, para cada tipo de valor, hemos de usar un tipo de "set" distinto*/
          for (int i=0; i<valores.length; i++) {
            InsertarValor(i, pstmt, valores[i], tipo[i]);
          }
          /*Y finalizamos la inserción*/
          pstmt.execute();
          impln("Registro añadido con éxito\n");
          break;
        
        /*-----------------------*/
        /* MODIFICAR UN REGISTRO */  
        /*-----------------------*/
        case 3:
          imprimirTabla(con, tabla, numCols, largoTotal, formato, arrCabecera);
          imp("Introduzca el número de la columna \"" + arrNombreCols[ind0AutoInc] +
          "\" de aquel que desee modificar (0 para Salir): ");
          eleccion = CompruebaRegistro(con, tabla, arrNombreCols[ind0AutoInc]);
          if (eleccion == 0){break;}
          /*Y ahora doy a elegir entre el número de campos que tiene el registro,
            a excepción de la que tiene la clave autoincrementable. La "filtro"
            utilizando un índice distinto para que no haya un hueco en los números
            mostrados por pantalla*/
          j = 1;
          for (int i=1; i<=numCols; i++) {
            if (i == ind0AutoInc + 1) {continue;}
            impln(abc(j), ". ", arrNombreCols[i-1]);
            j++;
          }
          imp("Introduzca el número de campo que quiere modificar: ");
          eleccion = Elegir(numCols - 1);
          /*Como el índice del AutoIncrement ocupa lugar en los arrays, si el
            índice escogido es igual o mayor que este, lo corrijo sumándole +1
            Aunque luego le reste 1, pero quiero ver el porqué*/
          if (eleccion >= ind0AutoInc + 1) {eleccion++;}
          impln("Introduzca el nuevo valor para el campo ");
          Object valor = PedirValor(arrNombreCols[eleccion-1], arrNullCols[eleccion-1], 
                                    arrTipoCols[eleccion-1], arrLongCols[eleccion-1],
                                    arrPrecisCols[eleccion-1], arrScaleCols[eleccion-1]);
          /*Preparo el SQL tomando como referencia el valor del índice autoincrementable*/
          pstmt = con.prepareStatement("UPDATE " + tabla + 
                                       " SET " + arrNombreCols[eleccion-1] + "=?" +
                                       " WHERE " + arrNombreCols[ind0AutoInc] + "=?");
          /*Otra vez, los rs empiezan en 1 y los arrays en 0, por eso corrijo.
          /*Y según el tipo de valor, llamaremos a un tipo de "set" u otro, pero
            primero al del id autoincrementable. No liarse, los números enteros
            son las posiciones en las que aparecen las ?. 
            COSAS: + Le resto una posición a la del método "InsertarValor" porque
                     lo definí así cuando trabajé con un array para hacer un insert
                     múltiple
                   + Al del campo autoincrementable, como lo selecciono por el
                     nombre guardado en un array, he de restarle uno a "eleccion",
                     referido a columnas que empiezan por 1
          */
          pstmt.setInt(2, eleccion - 1);
          InsertarValor(0, pstmt, valor, arrTipoCols[eleccion-1]);
          pstmt.execute();
          impln("Registro modificado con éxito\n");
          break;          
        
        /*----------------------*/
        /* ELIMINAR UN REGISTRO */
        /*----------------------*/
        /*ME HE DADO CUENTA DE QUE AL ELIMINAR UN REGISTRO INTERMEDIO, NO SE
          REORDENA EL ID AUTOINCREMENT, PERO ESTO HA DE SER ASÍ!!! SI ELIMINO UN
          PROVEEDOR X Y EL PROVEEDOR Y PASA A LA POSICIÓN X, ENTONCES LOS
          PRODUCTOS QUE PUDIERAN ESTAR RELACIONADOS QUEDARÍAN HUÉRFANOS. HABRÍA
          QUE ACTUALIZAR CAMBIOS EN TODAS LAS TABLAS. AUNQUE CON TRABAJO SUPONGO
          QUE SE PUEDE LLEGAR A HACER*/
        case 4:         
          imprimirTabla(con, tabla, numCols, largoTotal, formato, arrCabecera);
          imp("Introduzca el número de\"" + arrNombreCols[ind0AutoInc] + "\""
              + "que represente a la fila que desee eliminar (0 para Salir): ");
          eleccion = CompruebaRegistro(con, tabla, arrNombreCols[ind0AutoInc]);
          if (eleccion == 0){break;}
          pstmt = con.prepareStatement("DELETE FROM " + tabla + 
                                       " WHERE " + arrNombreCols[ind0AutoInc] + "=?");
          /*Aquí a "eleccion" no hay que restarle nada porque hace referencia a
            un número de registro o fila, tal como se han escrito por pantalla
            es como están en la tabla. Ahora no pertenecen a columnas de un array
            como antes. Ver la diferencia de cuando elegimos columnas o filas*/
          pstmt.setInt(1, eleccion);
          pstmt.execute();
          impln("Registro eliminado con éxito\n");
          break;
      }
      con.close();
    } catch (Exception e) {
      if (e instanceof SQLIntegrityConstraintViolationException) {
        impln("\nOPERACIÓN NO PERMITIDA\n"
            + "========================\n"
            + "Puede ser debido a varias causas de dependencia (constraint) entre tablas:\n"
            + "+ Está tratando de añadir una clave extranjera que no está registrada en su tabla respectiva\n"
            + "+ Está tratando de borrar un registro que es clave extranjera de otros registros, con lo cual\n"
            + "  aquellos quedarían huérfanos\n"
            + "+ Está intentando insertar un identificador de clave primaria que ya existe en la BD.");
      } else {
        System.out.println(e);
      }
    }
    
    impln("Pulse \"Enter\" para volver al Menú Anterior");
    scnEntrada.nextLine();
    //scnEntrada.close();

    try {
      Class clase = Class.forName("PLA4_Actividad.MenuCRUD");
      Method metodo = clase.getDeclaredMethod("iniciar", HashMap.class);
      /*Invoco con parámetros para que me sirva cuando me voy de los menús hacia
        un método externo y final, como cualquiera de las operaciones CRUD*/
      metodo.invoke(null, hshParametros);
    } catch(Exception e){System.out.println(e);}
  }

  /*--------------------*/
  /* COMPRUEBA REGISTRO */
  /*--------------------*/
  static int CompruebaRegistro(Connection con, String tabla, String nombreAutoInc) {
    int eleccion = 0;
    ResultSet rs;
    PreparedStatement pstmt;
    do{
     /*Primero miro que sea un entero*/
      eleccion = CompruebaEntero(scnEntrada.nextLine().trim());
      if (eleccion == 0){break;}
      /*Y ahora que se corresponda con un registro*/
      try {
        pstmt = con.prepareStatement("SELECT * FROM " + tabla + " WHERE " + nombreAutoInc + "=?");
        pstmt.setInt(1, eleccion);
        rs = pstmt.executeQuery();
        if(!rs.next()){
          imp("Ese número de registro no existe. Vuelva a probar: ");
          continue;
        }
        break;
      } catch (Exception e) {}
    }while(true);
    return eleccion;
  }

  /*-------------*/
  /* PEDIR VALOR */
  /*-------------*/
  static Object PedirValor(String nombre, int nulo, String tipo, int longitud, int precision, int scale) {
    String opciones = "";
    if (nulo == 0) {opciones +="(Obligatorio, ";}
    opciones += tipo;
    if (tipo.equals("DECIMAL")) {opciones += "[" + (precision - scale) + "." + scale
                                           + "](decimales extra se redondean))";}
    else if (tipo.equals("DATE")) {opciones += "[dd/mm/aaaa}";}
    /*Para los integers no quiero que salga nada, sale [11], pero es confuso pues
      en realidad es 2147483647*/
    else if (!tipo.equals("INT")){opciones += "[" + longitud + "]";}
    /*Mensaje para pedir dato, con características*/
    imp(nombre, opciones, "): ");
    
    /*Y ahora, a comprobar que sean válidos (nulo?, tipo?, longitud?)*/
    Object valor = CompruebaValor(nulo, tipo, longitud, precision, scale);
    return valor;
  }
  
  /*-----------------*/
  /* COMPRUEBA VALOR */
  /*-----------------*/  
  static Object CompruebaValor (int nulo, String tipo, int longitud, int precision, int scale) {
    /*Esto lo pongo en un nuevo método para que sea más fácilmente modificable,
      aunque, en realidad, si se contemplasen todos los tipos posibles de datos,
      en un escenario comercial, entonces sí estaría totalmente cerrada a modifación.
      Quisiera usar los métodos como nextInt del Scanner, el problema es que no
      lee toda la línea y consideraría válido como entero un valor "7 hola". Por
      eso no quiero aprovechar estos métodos y primero leeré toda la línea.
      Aprovecharé para convertir el valor al tipo directamente, de forma que si
      da error volveré a pedir la entrada"*/
    Object Ovalor = null;
    boolean valorValido = false;
    while(!valorValido) {
      /*Primero recojo un String del input y miro si está vacío y si esto es
        posible si el argumento es no obligatorio*/
      String strEntrada = scnEntrada.nextLine().trim();
      if (strEntrada.equals("")) {
        if (nulo != 0) {
          /*Esto significa que puede ser vacío, lo retorno y salgo*/
          Ovalor = null;
          return Ovalor;
        } else {
          /*Si no puede ser nulo, lanzo error y vuelvo a pedir valor*/
          imp("Valor OBLIGATORIO. Introduzca un valor: ");
          continue;
        }
      }
      
      /*Si se escribió algo, aquí la parseamos según el tipo de valor*/
      try {
        switch (tipo) {
          case "VARCHAR":
            String strValor = strEntrada;
            /*El valor será siempre válido, vamos a comprobar longitud*/
            if (strValor.length() > longitud) {
              imp("Valor DEMASIADO LARGO, máximo ", abc(longitud),
                      " caracteres. Vuelva a probar: ");
              continue;
            }
            Ovalor = strValor;
            valorValido = true;
          break;  
            
          case "INT":
            int intValor = Integer.parseInt(strEntrada);
            if (intValor > 2147483647) {
              imp("Valor ha de ser menor de 2147483647. Vuelva a probar: ");
              continue;
            } else if (intValor < 0) {
              imp("Valor NEGATIVO no permitido. Vuelva a probar: ");
              continue;
            }
            Ovalor = intValor;
            valorValido = true;
          break;
          
          case "DECIMAL":
            /*Por si acaso se ha puesto una coma en lugar de punto, que eso no sea un problema*/
            BigDecimal bdValor = new BigDecimal(strEntrada.replace(",",".")).setScale(scale, RoundingMode.HALF_EVEN);   
            /*Comprobar que tenga el número de cifras enteras requeridas. Parece
              ser que al tomar los METADATA de SQL, "rsMD.getPrecision(i)" dice
              la "precisión" es la parte entera, 6 en nuestro caso, y la longitud 8.
              Sin embargo, estaba usando el método .precision() del BigDecimal y
              este me devuelve el total de cifras, 8. Por otra parte, con BigDecimal,
              el método .scale() devuelve los decimales. Esto no me interesa ahora
              porque ya aviso que redondeo*/
            /*Al redondear los decimales*/
            if (bdValor.precision() > precision) {
              imp("El valor sólo puede tener ", abc(precision), " cifras no decimales. Vuelva a probar: ");
              continue;
            } else if (bdValor.compareTo(new BigDecimal(0)) != 1) {
              imp("El valor ha de ser MAYOR QUE CERO. Vuelva a probar: ");
              continue;
            }
            Ovalor = bdValor;
            valorValido = true;
          break;

          case "DATE":
            /*Creo un formato de fecha que parsear*/
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy"); 
            formatoFecha.setLenient(false);
            Date dteValor = formatoFecha.parse(strEntrada);
            Ovalor = dteValor;
            valorValido = true;
          break;
        }
      } catch(Exception e) {
        imp("Valor no correcto. Vuelva a probar: ");
      }
    }
    return Ovalor;
  }
  
  /*------------------------*/
  /* INSERTAR PREPARED STMT */
  /*------------------------*/
  static void InsertarValor(int i, PreparedStatement pstmt, Object valor, String tipo) {
    /*Esta sería la única parte del código que tal vez quedaría abierta a
      modificación, pues no voy a poner todos los tipos de datos, sino sólo los
      que sé que vamos a utilizar. Pero bueno, en plan profesional se podría
      hacer un método que sí tuviera todos los tipos de datos en cuenta, y ya
      quedaría cerrado a modificción*/
    /*Lo primero, incrementarlo pues viene de un array y aquí los índices empiezan
      en 0*/
    i++;
    try{
      switch (tipo) {
        case "VARCHAR":
          String strValor = (String) valor;
          pstmt.setString(i, strValor);
          break;
       
        case "INT":
          int intValor = (int) valor;
          pstmt.setInt(i, intValor);
          break;
        
        case "DECIMAL":
          BigDecimal bdValor = (BigDecimal) valor;
          pstmt.setBigDecimal(i, bdValor);
          break;
        
        /*Esto tendría que probarlo con la tabla Cliente...*/
        case "DATE":
          /*Con fechas no vale poner "" si no quiero una fecha, porque no es
            un String. Tampoco quiero que ponga una fecha 0000-00-00. He de usar
            null*/
          Date dteValor = (Date) valor;
          /*En nuestra tabla el valor fecha no es obligatorio, pero si no escribo
            nada, me da error, no interpreta el String vacío ""*/
          if (dteValor == null) {
             pstmt.setNull(i, java.sql.Types.DATE);
          } else {
          /*Si la variable es tipo java.util.Date, la convierto a java.sql.Date*/
          pstmt.setDate(i, new java.sql.Date(dteValor.getTime()));
          }
          break;
      }
    } catch(Exception e) {System.out.println(e);}  
  }

  /*-----------*/
  /* METADATOS */
  /*-----------*/  
  static Object[] Metadatos(Connection con, String tabla) {
    /*Número de columnas*/
    int numCols = 0;
     
    Statement stmt;
    ResultSet rs;
    ResultSetMetaData rsMD = null;
    try {
    //  stmt = con.createStatement();
    //  rs = stmt.executeQuery("SELECT * FROM " + tabla);
      /*No tiene sentido que lea toda la tabla para simplemente obtener la
        información de las columnas. Puedo usar este "truco", dando una condición
        lógica falsa; o con LIMIT=0*/
      stmt = con.createStatement();
      //rs = stmt.executeQuery("SELECT * FROM " + tabla + " WHERE 1=0");
      rs = stmt.executeQuery("SELECT * FROM " + tabla + " LIMIT 0");
      
      /*O bien de esta otra forma, e ir metiendo los valores en un array como
        hago más abajo...*/
      //rs = con.getMetaData().getColumns(null, null, tabla, null);
      //while(rs.next()) {
      //  String nombreCol = rs.getString("COLUMN_NAME");
      //  /*Lo mismo para DATA_TYPE, COLUMN_SIZE, DECIMAL_DIGITS, IS_NULLABLE, IS_AUTOINCREMENT ...*/
      //}
      rsMD = rs.getMetaData();
      numCols = rsMD.getColumnCount();
    }catch(Exception e){System.out.println(e);}  
      
    /*Índice de la columna AUTO_INCREMENT, asumimos que sólo hay una. Le pongo
      un 0 en medio del nombre ind-0-AutoInc para recordar que está referido a
      un comienzo desde 0, puesto que lo aplicaré a arrays sobretodo, y me
      interesa más así*/
    int ind0AutoInc = 0;
    /*Datos varios de cada columna, como el nombre*/
    String[] arrNombreCols = new String[numCols];
    /*Formato de cada columna, para hacer comprobaciones*/
    String[] arrTipoCols = new String[numCols];
    /*Si los campos pueden ser nulos, retorna un entero*/
    int[] arrNullCols = new int[numCols];
    /*Longitud. No para decimales*/
    int[] arrLongCols  = new int[numCols];
    
    /*Para DECIMALES, aunque lo guardaré en todas las columnas porque no sé donde
      van a estar los decimales. En SQL he puesto DECIMAL(10,4). Esto significa que como máximo habrá 10
      números, entre parte entera y decimal. Por otra parte, 4 es el número de
      decimales. Y aunque pusiera un valor con menos, SQL siempre le añade los
      que faltarán hasta los 4, o sea que la parte entera quedará con 6 y si
      pongo 1234567 dará error, porque en realidad es 1234567.8901
      Por otra parte, si pongo más decimales de la cuenta, por defecto se truncan,
      redondeando el último, así que se mantendrá siempre el mismo número máximo
      de cifras enteras y decimales*/
    int[] arrPrecisCols = new int[numCols];
    int[] arrScaleCols = new int[numCols];
    
    /*CABECERA para imprimir por pantalla*/
    String[] arrCabecera = new String[numCols];
    String formato = ""; /*de impresión*/
    int largoTotal = 0; /*de la cabecera, para subrayarla*/
    /*Para cálculos intermedios*/
    String nombreCol; 
    int intLargo;

    try {
      /*Mientras recorro los metadatos de cada columna para llenar los arrays
        presentados, aprovecho para crear también una cabecera para la tabla.
        Para otorgar menos anchura a aquellos campos que ocupan menos, voy a
        hacer que si el campo tiene una longitud de hasta 15 caracteres, se le
        asigne la longitud que tenga + 5. Y en caso contrario, que lo corte en 25,
        bueno en 22 + ...*/
      for (int i=1; i<=numCols; i++) {
        /*Compruebo si la columna es la AUTO_INCREMENT, que sólo puede haber una
          en la tabla. En los siguientes arrays que voy llenando de propiedades
          de las columnas, no me deshago de ella. Si estuviera la primera, podría.
          Pero si no lo fuera, sería más liado no conservar la columna. Es mejor
          mantenerla y luego filtrar lo que sea*/
        /*He de restarle 1 porque lo aplicaré a información de columnas que
          guardaré en arrays, que comenzarán en índice 0*/
        if (rsMD.isAutoIncrement(i)) {ind0AutoInc = i-1;}
        
        arrTipoCols[i-1] = rsMD.getColumnTypeName(i);
        arrNullCols[i-1] = rsMD.isNullable(i);
        arrLongCols[i-1] = rsMD.getColumnDisplaySize(i);
        arrPrecisCols[i-1] = rsMD.getPrecision(i);
        arrScaleCols[i-1] = rsMD.getScale(i);
        /*Los nombres de las columnas los guardo en el arrNombreCols pero también
          los uso para construir la cabecera*/
        nombreCol = rsMD.getColumnName(i).toString();
        arrNombreCols[i-1] = nombreCol;
        intLargo = nombreCol.length();
        /*Los registros empiezan por 1, los arrays quiero que como siempre, por 0*/
        if (intLargo <= 20) {arrCabecera[i-1] = nombreCol;}
        else {arrCabecera[i-1] = nombreCol.replaceAll("(.{20})(.{3,})","$1...");}
        
        /*Largo asignado a cada columna*/
        intLargo = rsMD.getColumnDisplaySize(i);
        if (intLargo <= 15) {
          formato += "%" + (intLargo + 5) + "s";
          largoTotal += intLargo + 5;
        }
        else {formato += "%25s"; largoTotal += 25;}
      }
      formato += "\n";
    } catch(Exception e) {System.out.println(e);}
    return new Object[] {numCols, ind0AutoInc, formato, largoTotal,
                         arrNombreCols, arrTipoCols, arrNullCols, arrLongCols, arrPrecisCols, arrScaleCols, arrCabecera};
  }
 
  /*----------------*/
  /* IMPRIMIR TABLA */
  /*----------------*/
  static void imprimirTabla(Connection con, String tabla, int numCols, 
                            int largoTotal, String formato, String[] arrLinea) {
    /*Imprimimos la cabecera*/
    Subraya(largoTotal, "-");
    System.out.format(formato, arrLinea);
    Subraya(largoTotal, "-");
    
    //int numRegistros = 0;
    /*Y para recorrer los registros me pide un try*/
    try {
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM " + tabla);
      //rs.last();
      /*Para cuando borre un registro o modifique un registro, habré de seleccionar
        un número de línea que exista, que como máximo será... ESTO NO ME SERVIRÁ
        EN CUANTO BORRE UN REGISTRO INTERMEDIO Y QUEDEN HUECOS EN LOS ID's
      AUTOINCREMENT!!!!!!!! */
      //numRegistros = rs.getRow();
      /*Vuelvo a retroceder, pero al "beforeFirst", para que al hacer un
        while(rs.next()) entonces vaya al primer registro*/
      //rs.beforeFirst();      
      while (rs.next()) {
        for (int i=1; i<=numCols; i++) {         
          /*El formato fecha se guarda Año-mes-día y lo voy a presentar al revés
            Si se hace en una consulta, se puede escoger el formato así:
            SELECT DATE_FORMAT(campo, '%Y-%m-%d') campo FROM tabla;
            Pero claro, nosotros estamos leyendo todos los campos así que, en vez
            de haber hecho un SELECT * tendría que haber llamado a los campos uno
            por uno...
          */
          if (rs.getString(i) == null) {
            arrLinea[i-1] = "";
          } else {
            if(rsMD.getColumnTypeName(i).equals("DATE")) {
              arrLinea[i-1] = rs.getDate(i).toString().replaceAll("(\\d{4})-(\\d{2})-(\\d{2})","$3/$2/$1");
            } else {
              arrLinea[i-1] = rs.getString(i).replaceAll("(.{20})(.{3,})","$1...");
            }
          }
        }
        System.out.format(formato, arrLinea);
      }
    } catch(Exception e) {System.out.println(e);}
    Subraya(largoTotal, "-");
    //return numRegistros;
  }
  
  /*-----------------*/
  /* INSERT MÚLTIPLE */
  /*-----------------*/
  /*Tengo que restar uno a numCol, porque el campo*/
  static String insertVarios(int numCampos, String strSQL) {
    String sbInterr = new String(new char[numCampos]).replace("\0", "?,");
    /*He de quitar la última coma*/
    sbInterr = sbInterr.substring(0, sbInterr.length() - 1);
    /*Substituimos la query para un solo parámetro por todos los campos, ya que
      se introducirán todos, tengan valor o no*/
    strSQL = strSQL.replace("(?)", "(" + sbInterr + ")");
    return strSQL;
  }  
}
