import src.InstitutoMeteorologia;
import src.TipoSensor;

import javax.swing.undo.AbstractUndoableEdit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
public class InterfazUsuario {

    private Scanner sc = new Scanner(System.in);
    private InstitutoMeteorologia instituto;

    public static void main(String[] args) {
        InterfazUsuario interfaz = new InterfazUsuario();
        interfaz.menuPrincipal();
    }
    //Creación del menú de opciones:
    private void menuPrincipal(){
        instituto = new InstitutoMeteorologia();
        int opcion;
        do{
            System.out.println("\nSISTEMA DE INFORMACION METEOROLOGICA");
            System.out.println("1.\tCrear Region");
            System.out.println("2.\tCrear Comuna");
            System.out.println("3.\tCrear Estacion Meteorologica");
            System.out.println("4.\tInstalar sensor");
            System.out.println("5.\tRegistrar medicion");
            System.out.println("6.\tGenerar listados");
            System.out.println("7.\tSalir");
            opcion = leerOpcion("Opcion: ",1, 7);
            switch (opcion) {
                case 1:
                    crearRegion();
                break;
                case 2:
                    crearComuna();
                break;
                case 3:
                    crearEstacionMeteorologica();
                break;
                case 4:
                    instalarSensor();
                break;
                case 5:
                    registrarMedicion();
                break;
                case 6:
                    menuListados();
                break;
                default:
                    System.out.println("Programa finalizado.");
                break;
            }
        }while(opcion != 7);
    }

    private void crearRegion() {
        System.out.println("\nCREAR REGION");
        int codigo = leerEntero("Codigo: ");
        String nombre = leerTexto("Nombre: ");
        boolean creada = instituto.creaRegion(codigo, nombre);
        if (creada) {
            System.out.println("> Region Creada correctamente.");
        }else{
            System.out.println("> No se creo: codigo o nombre repetido.");
        }
    }

    private void crearComuna() {
        System.out.println("\nCREAR COMUNA");
        int codigoRegion = leerEntero("Codigo de Region");
        int codigo = leerEntero("Codigo de Comuna");
        String nombre = leerTexto("Nombre de Comuna: ");
        boolean creada = instituto.creaComuna(codigo, nombre, codigoRegion);
        if (creada) {
            System.out.println("> Comuna Creada correctamente.");
        }else{
            System.out.println("> No se creo: region inexistente o comuna repetida.");
        }
    }
    private void crearEstacionMeteorologica() {
        System.out.println("\nCREAR ESTACION METEOROLOGICA");
        String codigo = leerTexto("Codigo de estacion: ");
        String nombre = leerTexto("Nombre: ");
        float longitud = leerFloat("Longitud:");
        float latitud = leerFloat("Latitud:");
        float altitud = leerFloat("Altitud (m):");
        int codRegion = leerEntero("Codigo de region: ");
        int codComuna = leerEntero("Codigo de comuna: ");
        boolean creada = instituto.creaEstacion(codigo, nombre, longitud, latitud, altitud, codRegion, codComuna);
        if (creada) {
            System.out.println("> Estacion meteorologica Creada correctamente.");
        }else{
            System.out.println("> No se creo: codigo repetido, region o comuna inexistente.");
        }
    }

    //Creacción de los métodos del menú

    private void instalarSensor() {
        System.out.println("\nINSTALAR SENSOR");
        String codEstacion = leerTexto("Codigo de estacion: ");
        int opcion = leerOpcion("Tipo [1 Temp. 2 Hum. 3 Presion. 4 Viento. 5 Precip.]: ", 1, 5);
        TipoSensor tipo = obtenerTipo(opcion);
        String codigo = leerTexto("Codigo del sensor: ");
        String marca = leerTexto("Marca: ");
        String modelo = leerTexto("Modelo: ");
        boolean instalado = instituto.instalaSensor(codigo, marca, modelo, tipo, codEstacion);
        if (instalado) {
            System.out.println("> Sensor instalado correctamente.");
        }else{
            System.out.println("> No se instalo: estacion, estado, codigo o tipo no permitido.");
        }
    }
    private void registrarMedicion() {
        System.out.println("\nREGISTRAR MEDICION");
        String codEstacion = leerTexto("Codigo de region: ");
        String codSensor = leerTexto("Codigo de sensor: ");
        LocalDateTime fechaHora = leerFechaHora("Fecha y hora [dd/MM/aaaa HH:mm]: ");
        float  valor = leerFloat("Valor: ");
        boolean registrada = instituto.registraMedicion(fechaHora, valor, codEstacion, codSensor);
        if (registrada) {
            System.out.println("> Medicion registrada correctamente.");
        }else{
            System.out.println("> No se registro: revise estacion, estado, sensor fecha y valor.");
        }
    }

    //Menu listado

    private void menuListados() {
        int opcion;
        do{
            System.out.println("\nGENERAR LISTADOS");
            System.out.println("1.\tRegiones");
            System.out.println("2.\tComunas");
            System.out.println("3.\tEstaciones de una comuna");
            System.out.println("4.\tSensores de una estacion");
            System.out.println("5.\tMediciones de un sensor");
            System.out.println("6.\tVolver al menu");
            opcion = leerOpcion("Opcion: ", 1, 6);
            switch (opcion){
                case 1:
                    listarRegiones();
                break;
                case 2:
                    listarCoumas();
                break;
                case 3:
                    listarEstaciones();
                break;
                case 4:
                    listarSensores();
                break;
                case 5:
                    listarMediciones();
                break;
                default: break;
            }

        }while(opcion != 6);
    }


    //Listados

    private void listarRegiones() {
        String[] titulos = {"CODIGO", "NOMBRE", "COMUNAS", "ESTACIONES"};
        mostrarTabla("REGIONES", titulos, instituto.listaComunas());
    }
    private void listarCoumas() {
        String[] titulos = {"CODIGO", "NOMBRE", "REGION", "ESTACIONES", "ACTIVAS"};
        mostrarTabla("COMUNAS", titulos, instituto.listaComunas());
    }

    private void listarEstaciones() {
        int region = leerEntero("Codigo de region: ");
        int comuna = leerEntero("Codigo de comuna: ");
        String[] titulos = {"CODIGO","NOMBRE","UBICACION","ESTADO","SENSORES OPERATIVOS"};
        mostrarTabla("ESTACIONES DE LA COMUNA", titulos, instituto.listaEstaciones(region, comuna));
    }
    private void listarSensores() {
        String estacion = leerTexto("Codigo de estacion: ");
        String[] titulos = {"CODIGO", "TIPO", "MARCA", "MODELO", "UNIDAD", "ESTADO", "ULTIMA MEDICION"};
        mostrarTabla("SENSORES DE " + estacion.toUpperCase(), titulos, instituto.listaSensores(estacion));
    }

    private void listarMediciones() {
        String estacion = leerTexto("Codigo de estacion: ");
        String sensor = leerTexto("Codigo de sensor: ");
        LocalDateTime inicio = leerFechaHora("Inicio [dd/MM/aaaa HH:mm]:");
        LocalDateTime fin = leerFechaHora("Fin [dd/MM/aaaa HH:mm]:");
        String[] titulos = {"FECHA", "HORA", "VALOR", "UNIDAD"};
        mostrarTabla("MEDICIONES DEL SENSOR " + sensor.toUpperCase(), titulos, instituto.listaMediciones(estacion, sensor, inicio, fin));
    }
    //Métodos que se usan para la funcionalidad de cada menú
    private int leerOpcion(String mensaje, int minimo, int maximo) {
        int opcion;
        do{
            opcion = leerEntero(mensaje);
            if(opcion < minimo || opcion > maximo) {
                System.out.println("> Opcion fuera de rango. Intente otra vez");
            }
        }while(opcion < minimo || opcion > maximo);
        return opcion;
    }

    private int leerEntero(String mensaje) {
        return Integer.parseInt(leerTexto(mensaje));
    }

    private float leerFloat(String mensaje) {
        return Float.parseFloat(leerTexto(mensaje).replace(',','.'));
    }

    private String leerTexto(String mensaje) {
        System.out.println(mensaje);
        return sc.nextLine().trim();
    }
    private LocalDateTime leerFechaHora(String mensaje){
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/aaaa HH:mm");
        return LocalDateTime.parse(leerTexto(mensaje),formato);
    }
    private TipoSensor obtenerTipo(int opcion){
        switch (opcion){
            case 1:
                return TipoSensor.TEMPERATURA;
            case 2:
                return TipoSensor.HUMEDAD;
            case 3:
                return TipoSensor.PRESION;
            case 4:
                return TipoSensor.VIENTO;
            default:
                return TipoSensor.HUMEDAD;
        }
    }
    private void mostrarTabla(String titulo, String[] encabezados, String [][] datos){
        System.out.println("\n" + titulo);
        if(datos.length == 0){
            System.out.println("> No existen datos para este criterio");
            return;
        }
        int[] anchos = new int[encabezados.length];
        for (int columna = 0; columna < encabezados.length; columna++) {
            anchos[columna] = encabezados[columna].length();
            for (int fila = 0; fila < datos.length; fila++) {
                if(datos[fila][columna].length() > anchos[columna]){
                    anchos[columna] = datos[fila][columna].length();
                }
            }
        }
        imprimirFila(encabezados, anchos);
        for (int fila = 0; fila < datos.length; fila++) {
            imprimirFila(datos[fila], anchos);
        }
    }
    private void imprimirFila(String[] valores, int[] anchos){
        for (int columna = 0; columna < valores.length; columna++) {
            String texto = valores[columna];
            System.out.print(texto);
            int espacios = anchos[columna] - texto.length() + 3;
            for (int i = 0; i < espacios; i++) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }
}