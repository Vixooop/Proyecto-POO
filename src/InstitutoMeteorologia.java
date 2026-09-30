package src;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InstitutoMeteorologia {
    private List<Region> listaRegion = new ArrayList<Region>();
    private List<EstacionMeteorologica> listaEstacion = new ArrayList<EstacionMeteorologica>();

    public boolean creaRegion(int codigo, String nombre) {
        Region region = new Region(codigo, nombre.toUpperCase());
        boolean repetido = false;
        for (Region r : listaRegion) {
            if (r.getCodigo() == region.getCodigo()) {
                System.out.println("codigo repetido");
                repetido = true;
            }
            if (r.getNombre().equals(region.getNombre())) {
                System.out.println("nombre repetido");
                repetido = true;
            }
        }
        if (!repetido) {
            listaRegion.add(region);
        }
        return !repetido;
    }

    public boolean creaComuna(int codigo, String nombre, int codigoRegion) {
        boolean existeReg = false;
        Region region = null;
        for (Region r : listaRegion) {
            if (r.getCodigo() == codigoRegion) {
                region = new Region(r.getCodigo(), r.getNombre());
                r.addComuna(codigo, nombre);
                return true;
            }
            }
        return false;
    }
    public boolean creaEstacion(String codigo, String nombre,float longitud, float latitud, float altitud, int codRegion, int codComuna) {
        for (Region r : listaRegion) {
            if (r.getCodigo() == codRegion) {
                Comuna comuna = r.findComunaById(codComuna);
                if (comuna == null) {
                    return false;
                }
                EstacionMeteorologica estacion =
                        comuna.findEstacionById(codigo);

                if (estacion != null) {
                    return false;
                }
                EstacionMeteorologica nuevaEstacion = new EstacionMeteorologica(codigo, nombre, longitud, latitud, altitud, comuna);
                listaEstacion.add(nuevaEstacion);
                return true;
            }
        }

        return false;
    }

    public boolean instalaSensor(String codigo, String marca, String modelo, TipoSensor tipo, String codEstacion) {
        for (EstacionMeteorologica listaEst : listaEstacion) {
            if (listaEst != null && listaEst.getEstado().equals(Estado.ACTIVO) && !buscarSensor(listaEst).equals(codigo)) {
                listaEst.instalaSensor(codigo, marca, modelo, tipo);
                return true;
            }
        }
        return false;
    }
    private String buscarSensor(EstacionMeteorologica estacion) {
        String codSensor = "";
        String[][] estacionSensor = estacion.getResumenSensores();
        for (int i = 0; i < estacionSensor.length; i++) {
            codSensor = estacionSensor[i][0];
            if (!codSensor.equals("")) {
                return codSensor;
            }
        }
        return codSensor;
    }

    public boolean registraMedicion(LocalDateTime fechaHora, float valor, String codEstacion, String codSensor) {
        for(EstacionMeteorologica e : listaEstacion){
            if (e != null) {
                return e.registraMedicion(fechaHora, valor, codSensor);
            }
        }

        return false;
        }
    public String[][] listaRegiones() {
        String[][] listaRSalida = new String[listaRegion.size()][4];
        int i = 0;
        for (Region r : listaRegion) {
            listaRSalida[i][0] = String.valueOf(r.getCodigo());
            listaRSalida[i][1] = r.getNombre();
            Comuna[] comunas = r.getComunas();
            listaRSalida[i][2] = String.valueOf(comunas.length); //N comunas
            listaRSalida[i][3] = String.valueOf(r.getCantidadEstaciones()); //N estaciones
            i++;
        }
        return listaRSalida;
    }

    public String[][] listaComunas() {
        int totalComunas = 0;
        for (Region r : listaRegion) {
            Comuna[] comunas = r.getComunas();
            totalComunas += comunas.length;
        }
        String[][] listaCSalida = new String[totalComunas][5];
        int fila = 0;
        for (Region r : listaRegion) {
            Comuna[] comunas = r.getComunas();
            for (int i = 0; i < comunas.length; i++) {
                listaCSalida[fila][0] = String.valueOf(comunas[i].getCodigo());
                listaCSalida[fila][1] = comunas[i].getNombre();
                listaCSalida[fila][2] = r.getNombre();
                listaCSalida[fila][3] = String.valueOf(comunas[i].getCantidadEstaciones());
                listaCSalida[fila][4] = String.valueOf(comunas[i].getCantidadEstacionesActivas());
                fila++;
            }
        }
        return listaCSalida;
    }
    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) {
        int fila = 0;
        if(listaEstacion.size()<= 0){
            return null;
        }
        String[][] estacionSensor = new String[listaEstacion.size()][5];
        for (EstacionMeteorologica e : listaEstacion) {
            String datos = e.toString();
            String[] listDatos = datos.split(";");
            if (listDatos.length > 0 && listDatos[5].equals(String.valueOf(codigoRegion)) && listDatos[6].equals(String.valueOf(codigoComuna))){
                estacionSensor[fila][0] = listDatos[0];//codigo
                estacionSensor[fila][1] = listDatos[1];//nombre
                estacionSensor[fila][2] = listDatos[2];//ubicacion
                estacionSensor[fila][3] = listDatos[3];//estado
                estacionSensor[fila][4] = listDatos[4];//operativos
            }
            fila++;
        }
        return estacionSensor;
    }

    public String[][] listaSensores(String codigoEstacion) {
        for (Region r : listaRegion) {
            for (Comuna c : r.getComunas()) {
                EstacionMeteorologica estacion = c.findEstacionById(codigoEstacion);
                if(estacion != null) {
                    return estacion.getResumenSensores();
                }
            }
        }
        return null;
    }

    public String[][] listaMediciones(String codEstacion, String codSensor, LocalDateTime inicio, LocalDateTime fin) {
        for(EstacionMeteorologica e : listaEstacion){
            if(e.getCodigo().equals(codEstacion)){
                String[][] sensores = e.getResumenSensores();
                if (sensores != null) {
                    for (int i = 0; i < sensores.length; i++) {
                        return e.getMedicionesSensorBetween(codSensor,inicio,fin);
                    }
                }
            }
        }
        return null;
    }
}
