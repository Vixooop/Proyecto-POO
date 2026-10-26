package src.controlador;
/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
23/10/2026
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PersistenciaTexto {
    public static final String SEPARADOR = ";";
    private static PersistenciaTexto instancia;
    private PersistenciaTexto() {
    }

    private static PersistenciaTexto obtenerInstancia() {
        if (instancia == null) {
            instancia = new PersistenciaTexto();
        }
        return instancia;
    }

    public static List<String[]> leeRegistros(String nombreArchivo) throws MeteorologiaException {
        return obtenerInstancia().lee(nombreArchivo);
    }

    public static void escribeRegistros(String nombreArchivo, List<String> lineas) throws MeteorologiaException {
        obtenerInstancia().escribe(nombreArchivo, lineas);
    }

    private List<String[]> lee(String nombreArchivo) throws MeteorologiaException {
        List<String[]> registros = new ArrayList<String[]>();
        try {
            for (String linea : Files.readAllLines(Paths.get("datos", nombreArchivo), StandardCharsets.UTF_8)) {
                registros.add(linea.split(SEPARADOR, -1));
            }
        } catch (NoSuchFileException e) {
            throw new MeteorologiaException("No se encontró el archivo " + nombreArchivo + ".");
        } catch (IOException e) {
            throw new MeteorologiaException("No fue posible leer el archivo " + nombreArchivo + ".");
        }
        return registros;
    }

    private void escribe(String nombreArchivo, List<String> lineas) throws MeteorologiaException {
        try {
            Path carpeta = Paths.get("datos");
            Files.createDirectories(carpeta);
            Files.write(carpeta.resolve(nombreArchivo), lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new MeteorologiaException("No fue posible escribir el archivo " + nombreArchivo + ".");
        }
    }
}