package controlador;
/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PersistenciaObjetos {
    public static final String CARPETA = "datos";

    private PersistenciaObjetos() {
    }

    public static void escribeObjeto(String nombreArchivo, Object objeto) throws MeteorologiaException {
        File carpeta = new File(CARPETA);
        carpeta.mkdirs();
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(new File(carpeta, nombreArchivo)))) {
            salida.writeObject(objeto);
        } catch (IOException e) {
            throw new MeteorologiaException("No fue posible escribir el archivo " + nombreArchivo + ".");
        }
    }

    public static Object leeObjeto(String nombreArchivo) throws MeteorologiaException {
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(new File(CARPETA, nombreArchivo)))) {
            return entrada.readObject();
        } catch (FileNotFoundException e) {
            throw new MeteorologiaException("No se encontró el archivo " + nombreArchivo + ".");
        } catch (IOException | ClassNotFoundException e) {
            throw new MeteorologiaException("No fue posible leer el archivo " + nombreArchivo + ".");
        }
    }
}