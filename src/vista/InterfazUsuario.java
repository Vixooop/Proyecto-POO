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

    public void crearRegion() {
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

    public void crearComuna() {
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

    public void crearEstacion() {
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

    public void listarRegiones() {
        mostrarTitulo("REGIONES");
        String[] titulos = {"CÓDIGO", "NOMBRE", "COMUNAS", "ESTACIONES"};
        mostrarListado(titulos, InstitutoMeteorologia.getInstance().listaRegiones(), "No hay regiones registradas.");
    }

    public void listarComunas() {
        mostrarTitulo("COMUNAS");
        String[] titulos = {"CÓDIGO", "NOMBRE", "REGIÓN", "ESTACIONES", "ACTIVAS"};
        mostrarListado(titulos, InstitutoMeteorologia.getInstance().listaComunas(), "No hay comunas registradas.");
    }

    public void listarEstaciones() {
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

    public void listarSensores() {
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

    public void listarMediciones() {
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

    public void listarResumenMediciones() {
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