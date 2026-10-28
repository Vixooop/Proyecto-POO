//autor: Vicente Navarrete
// 26-10-2026
package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;
import modelo.Estado;
import modelo.TipoSensor;

public class InterfazUsuario {

    private static InterfazUsuario instancia;
    private Scanner sc;

    private InterfazUsuario() {
        sc = new Scanner(System.in);
        InstitutoMeteorologia.getInstance();
    }

    public static InterfazUsuario getInstance() {
        if (instancia == null) {
            instancia = new InterfazUsuario();
        }
        return instancia;
    }

    public void menuPrincipal() {
        while (true) {
            mostrarTitulo("SISTEMA DE INFORMACIÓN METEOROLÓGICA");
            System.out.println(" 1. Crear región");
            System.out.println(" 2. Crear comuna");
            System.out.println(" 3. Crear estación meteorológica");
            System.out.println(" 4. Instalar sensor");
            System.out.println(" 5. Registrar medición");
            System.out.println(" 6. Cambiar estado de sensor");
            System.out.println(" 7. Generar listados");
            System.out.println(" 8. Cargar datos");
            System.out.println(" 9. Guardar datos");
            System.out.println("10. Salir");
            int opcion = leerOpcion("Opción: ", 1, 10);
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
                    cambiarEstadoSensor();
                    break;
                case 7:
                    menuListados();
                    break;
                case 8:
                    cargarDatos();
                    break;
                case 9:
                    guardarDatos();
                    break;
                case 10:
                    salir();
                    break;
            }
        }
    }

    private void crearRegion() {
        mostrarTitulo("CREAR REGIÓN");
        int codigo = leerEntero("Código: ");
        String nombre = leerTexto("Nombre: ");
        try {
            InstitutoMeteorologia.getInstance().creaRegion(codigo, nombre);
            System.out.println("> Región " + nombre + " creada correctamente.");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void crearComuna() {
        mostrarTitulo("CREAR COMUNA");
        int codigoRegion = leerEntero("Código de región: ");
        int codigo = leerEntero("Código de comuna: ");
        String nombre = leerTexto("Nombre de comuna: ");
        try {
            InstitutoMeteorologia.getInstance().creaComuna(codigo, nombre, codigoRegion);
            System.out.println("> Comuna " + nombre + " creada correctamente.");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void crearEstacionMeteorologica() {
        mostrarTitulo("CREAR ESTACIÓN METEOROLÓGICA");
        String codigo = leerTexto("Código de estación: ");
        String nombre = leerTexto("Nombre: ");
        float longitud = leerFloat("Longitud [-180 a 180]: ", -180f, 180f);
        float latitud = leerFloat("Latitud [-90 a 90]: ", -90f, 90f);
        float altitud = leerFloat("Altitud (m) [-500 a 9000]: ", -500f, 9000f);
        int codRegion = leerEntero("Código de región: ");
        int codComuna = leerEntero("Código de comuna: ");
        try {
            InstitutoMeteorologia.getInstance().creaEstacion(codigo, nombre, longitud, latitud, altitud, codRegion, codComuna);
            System.out.println("> Estación " + codigo + " creada correctamente.");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void instalarSensor() {
        mostrarTitulo("INSTALAR SENSOR");
        String codEstacion = leerTexto("Código de estación: ");
        int opcion = leerOpcion("Tipo [1 Temp.  2 Hum.  3 Presión  4 Viento  5 Precip.]: ", 1, 5);
        TipoSensor tipo = obtenerTipo(opcion);
        String codigo = leerTexto("Código de sensor: ");
        String marca = leerTexto("Marca: ");
        String modelo = leerTexto("Modelo: ");
        try {
            InstitutoMeteorologia.getInstance().instalaSensor(codigo, marca, modelo, tipo, codEstacion);
            System.out.println("> Sensor " + codigo + " instalado correctamente en la estación " + codEstacion + ".");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void registrarMedicion() {
        mostrarTitulo("REGISTRAR MEDICIÓN");
        String codEstacion = leerTexto("Código de estación: ");
        String codSensor = leerTexto("Código de sensor: ");
        LocalDateTime fechaHora = leerFechaHora("Fecha y hora [dd/MM/yyyy HH:mm]: ");
        float valor = leerFloat("Valor: ", -100000f, 100000f);
        try {
            InstitutoMeteorologia.getInstance().registraMedicion(fechaHora, valor, codEstacion, codSensor);
            System.out.println("> Medición registrada correctamente.");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void cambiarEstadoSensor() {
        mostrarTitulo("CAMBIAR ESTADO DE SENSOR");
        String codEstacion = leerTexto("Código de estación: ");
        String codSensor = leerTexto("Código de sensor: ");
        int opcion = leerOpcion("Nuevo estado [1 ACTIVO  2 INACTIVO]: ", 1, 2);
        Estado estado = Estado.ACTIVO;
        if (opcion == 2) {
            estado = Estado.INACTIVO;
        }
        try {
            InstitutoMeteorologia.getInstance().cambiaEstadoSensor(codEstacion, codSensor, estado);
            System.out.println("> Estado del sensor " + codSensor + " actualizado a " + estado + ".");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void cargarDatos() {
        mostrarTitulo("CARGAR DATOS");
        try {
            InstitutoMeteorologia.getInstance().cargaDatos();
            System.out.println("> Datos cargados correctamente desde la carpeta datos/.");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void guardarDatos() {
        mostrarTitulo("GUARDAR DATOS");
        try {
            InstitutoMeteorologia.getInstance().guardaDatos();
            System.out.println("> Datos guardados correctamente en la carpeta datos/.");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void salir() {
        if (leerSiNo("¿Desea guardar los datos antes de salir? [S/N]: ")) {
            try {
                InstitutoMeteorologia.getInstance().guardaDatos();
                System.out.println("> Datos guardados correctamente en la carpeta datos/.");
            } catch (MeteorologiaException e) {
                mostrarError(e.getMessage());
                return;
            }
        }
        System.out.println("> Fin del programa.");
        System.exit(0);
    }


    private void menuListados() {
        int opcion;
        do {
            mostrarTitulo("GENERAR LISTADOS");
            System.out.println("1. Regiones");
            System.out.println("2. Comunas");
            System.out.println("3. Estaciones de una comuna");
            System.out.println("4. Sensores de una estación");
            System.out.println("5. Mediciones de un sensor");
            System.out.println("6. Resumen estadístico de un sensor");
            System.out.println("7. Alertas de una estación");
            System.out.println("8. Volver al menú principal");
            opcion = leerOpcion("Opción: ", 1, 8);
            switch (opcion) {
                case 1:
                    listarRegiones();
                    break;
                case 2:
                    listarComunas();
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
                case 6:
                    listarResumenMediciones();
                    break;
                case 7:
                    listarAlertas();
                    break;
                default:
                    break;
            }
        } while (opcion != 8);
    }

    private void listarRegiones() {
        mostrarTitulo("REGIONES");
        String[] titulos = {"CÓDIGO", "NOMBRE", "COMUNAS", "ESTACIONES"};
        mostrarListado(titulos, InstitutoMeteorologia.getInstance().listaRegiones(), "No hay regiones registradas.");
    }

    private void listarComunas() {
        mostrarTitulo("COMUNAS");
        String[] titulos = {"CÓDIGO", "NOMBRE", "REGIÓN", "ESTACIONES", "ACTIVAS"};
        mostrarListado(titulos, InstitutoMeteorologia.getInstance().listaComunas(), "No hay comunas registradas.");
    }

    private void listarEstaciones() {
        mostrarTitulo("ESTACIONES DE UNA COMUNA");
        int region = leerEntero("Código de región: ");
        int comuna = leerEntero("Código de comuna: ");
        String[] titulos = {"CÓDIGO", "NOMBRE", "UBICACIÓN", "ESTADO", "SENSORES OPERATIVOS"};
        try {
            String[][] datos = InstitutoMeteorologia.getInstance().listaEstaciones(region, comuna);
            mostrarListado(titulos, datos, "La comuna " + comuna + " no tiene estaciones registradas.");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void listarSensores() {
        mostrarTitulo("SENSORES DE UNA ESTACIÓN");
        String estacion = leerTexto("Código de estación: ");
        String[] titulos = {"CÓDIGO", "TIPO", "MARCA", "MODELO", "UNIDAD", "ESTADO", "ÚLTIMA MEDICIÓN"};
        try {
            String[][] datos = InstitutoMeteorologia.getInstance().listaSensores(estacion);
            mostrarListado(titulos, datos, "La estación " + estacion + " no tiene sensores instalados.");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void listarMediciones() {
        mostrarTitulo("MEDICIONES DE UN SENSOR");
        String estacion = leerTexto("Código de estación: ");
        String sensor = leerTexto("Código de sensor: ");
        LocalDateTime inicio = leerFechaHora("Inicio [dd/MM/yyyy HH:mm]: ");
        LocalDateTime fin = leerFechaHora("Fin    [dd/MM/yyyy HH:mm]: ");
        String[] titulos = {"FECHA", "HORA", "VALOR", "UNIDAD"};
        try {
            String[][] datos = InstitutoMeteorologia.getInstance().listaMediciones(estacion, sensor, inicio, fin);
            mostrarListado(titulos, datos, "El sensor " + sensor + " no registra mediciones en el período indicado.");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void listarResumenMediciones() {
        mostrarTitulo("RESUMEN ESTADÍSTICO DE UN SENSOR");
        String estacion = leerTexto("Código de estación: ");
        String sensor = leerTexto("Código de sensor: ");
        LocalDateTime inicio = leerFechaHora("Inicio [dd/MM/yyyy HH:mm]: ");
        LocalDateTime fin = leerFechaHora("Fin    [dd/MM/yyyy HH:mm]: ");
        try {
            String[] datos = InstitutoMeteorologia.getInstance().listaResumenMediciones(estacion, sensor, inicio, fin);
            if (datos.length == 0) {
                System.out.println("> El sensor " + sensor + " no registra mediciones en el período indicado.");
                return;
            }
            String unidad = datos[1];
            mostrarTitulo("RESUMEN ESTADÍSTICO DEL SENSOR " + sensor.toUpperCase() + " (" + estacion.toUpperCase() + ")");
            System.out.println("Período: " + formatoFechaHora(inicio) + " a " + formatoFechaHora(fin));
            System.out.println("Tipo de sensor         : " + datos[0]);
            System.out.println("Cantidad de mediciones : " + datos[2]);
            System.out.println("Mínimo                 : " + datos[3] + " " + unidad + "   (" + datos[4] + ")");
            System.out.println("Máximo                 : " + datos[5] + " " + unidad + "   (" + datos[6] + ")");
            System.out.println("Promedio               : " + datos[7] + " " + unidad);
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void listarAlertas() {
        mostrarTitulo("ALERTAS DE UNA ESTACIÓN");
        String estacion = leerTexto("Código de estación: ");
        LocalDateTime inicio = leerFechaHora("Inicio [dd/MM/yyyy HH:mm]: ");
        LocalDateTime fin = leerFechaHora("Fin    [dd/MM/yyyy HH:mm]: ");
        try {
            String[][] datos = InstitutoMeteorologia.getInstance().listaAlertas(estacion, inicio, fin);
            String[][] filas = new String[datos.length][6];
            for (int i = 0; i < datos.length; i++) {
                filas[i][0] = datos[i][0];
                filas[i][1] = datos[i][1];
                filas[i][2] = datos[i][2];
                filas[i][3] = datos[i][3];
                filas[i][4] = datos[i][4] + " " + datos[i][5];
                filas[i][5] = datos[i][6];
            }
            if (datos.length > 0) {
                mostrarTitulo("ALERTAS DE LA ESTACIÓN " + estacion.toUpperCase());
                System.out.println("Período: " + formatoFechaHora(inicio) + " a " + formatoFechaHora(fin));
            }
            String[] titulos = {"FECHA", "HORA", "SENSOR", "FENÓMENO", "VALOR", "NIVEL"};
            mostrarListado(titulos, filas, "La estación " + estacion + " no registra alertas en el período indicado.");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private int leerOpcion(String mensaje, int minimo, int maximo) {
        while (true) {
            int opcion = leerEntero(mensaje);
            if (opcion >= minimo && opcion <= maximo) {
                return opcion;
            }
            mostrarError("La opción debe estar entre " + minimo + " y " + maximo + ".");
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                int numero = Integer.parseInt(texto);
                if (numero > 0) {
                    return numero;
                }
                mostrarError("El valor debe ser un número entero positivo.");
            } catch (NumberFormatException e) {
                mostrarError("Debe ingresar un número entero.");
            }
        }
    }

    private float leerFloat(String mensaje, float minimo, float maximo) {
        while (true) {
            String texto = leerTexto(mensaje).replace(',', '.');
            try {
                float numero = Float.parseFloat(texto);
                if (Float.isNaN(numero) || Float.isInfinite(numero)) {
                    mostrarError("Debe ingresar un número válido.");
                } else if (numero < minimo || numero > maximo) {
                    mostrarError("El valor debe estar entre " + (int) minimo + " y " + (int) maximo + ".");
                } else {
                    return numero;
                }
            } catch (NumberFormatException e) {
                mostrarError("Debe ingresar un número (por ejemplo 12.5).");
            }
        }
    }

    private String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = sc.nextLine().trim();
            if (texto.isEmpty()) {
                mostrarError("El dato no puede estar vacío.");
            } else if (texto.contains(";")) {
                mostrarError("El dato no puede contener el carácter ';'.");
            } else {
                return texto;
            }
        }
    }

    private LocalDateTime leerFechaHora(String mensaje) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return LocalDateTime.parse(texto, formato);
            } catch (DateTimeParseException e) {
                mostrarError("La fecha y hora debe tener el formato dd/MM/yyyy HH:mm.");
            }
        }
    }

    private boolean leerSiNo(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            if (texto.equalsIgnoreCase("S")) {
                return true;
            }
            if (texto.equalsIgnoreCase("N")) {
                return false;
            }
            mostrarError("Debe responder S o N.");
        }
    }

    private TipoSensor obtenerTipo(int opcion) {
        switch (opcion) {
            case 1:
                return TipoSensor.TEMPERATURA;
            case 2:
                return TipoSensor.HUMEDAD;
            case 3:
                return TipoSensor.PRESION;
            case 4:
                return TipoSensor.VIENTO;
            default:
                return TipoSensor.PRECIPITACION;
        }
    }

    private String formatoFechaHora(LocalDateTime fecha) {
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private void mostrarTitulo(String titulo) {
        System.out.println();
        System.out.println(titulo);
        System.out.println("-".repeat(60));
    }

    private void mostrarError(String mensaje) {
        System.out.println("> Error: " + mensaje);
    }

    private void mostrarListado(String[] encabezados, String[][] datos, String mensajeVacio) {
        if (datos.length == 0) {
            System.out.println("> " + mensajeVacio);
            return;
        }
        int[] anchos = new int[encabezados.length];
        for (int columna = 0; columna < encabezados.length; columna++) {
            anchos[columna] = encabezados[columna].length();
            for (int fila = 0; fila < datos.length; fila++) {
                if (datos[fila][columna].length() > anchos[columna]) {
                    anchos[columna] = datos[fila][columna].length();
                }
            }
        }
        imprimirFila(encabezados, anchos);
        for (int fila = 0; fila < datos.length; fila++) {
            imprimirFila(datos[fila], anchos);
        }
    }

    private void imprimirFila(String[] valores, int[] anchos) {
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
