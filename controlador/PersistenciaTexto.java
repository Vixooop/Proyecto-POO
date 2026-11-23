package controlador;
/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
23/10/2026
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class PersistenciaTexto {
    public static final String SEPARADOR = ";";
    private static PersistenciaTexto instancia;
    private PersistenciaTexto() {
    }

    public static PersistenciaTexto getInstance() {
        if (instancia == null) {
            instancia = new PersistenciaTexto();
        }
        return instancia;
    }

    public static List<String[]> leeRegistros(String nombreArchivo) throws MeteorologiaException {
        return getInstance().lee(nombreArchivo);
    }

    private List<String[]> lee(String nombreArchivo) throws MeteorologiaException {
        try {
            return Files.readAllLines(Paths.get("datos", nombreArchivo), StandardCharsets.UTF_8).stream()
                    .map(linea -> linea.split(SEPARADOR, -1))
                    .collect(Collectors.toList());
        } catch (NoSuchFileException e) {
            throw new MeteorologiaException("No se encontró el archivo " + nombreArchivo + ".");
        } catch (IOException e) {
            throw new MeteorologiaException("No fue posible leer el archivo " + nombreArchivo + ".");
        }
    }
}