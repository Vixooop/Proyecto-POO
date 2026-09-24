package src;

import java.util.ArrayList;
import java.util.List;

public class Comuna {
    private int codigo;
    private String nombre;
    private Region region;
    private List<EstacionMeteorologica> estaciones = new ArrayList<EstacionMeteorologica>();


    public Comuna(int codigo, String nombre, Region region) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.region = region;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {

        return nombre;
    }

    public void addEstacion(EstacionMeteorologica estacion) {
        estaciones.add(estacion);
    }

    public EstacionMeteorologica findEstacionById(String codigo) {
        EstacionMeteorologica estacionSalida = null;
        for (EstacionMeteorologica e : estaciones) {
            if (e.getCodigo().equals(codigo)) {
                estacionSalida = e;
                return estacionSalida;
            }
        }
        return null;
    }

    public Region getRegion() {
        return this.region;
    }

    public int getCantidadEstaciones() {
        return estaciones.size();

    }

    public int getCantidadEstacionesActivas() {
        int cuentaActivas = 0;
        for (EstacionMeteorologica estacion : estaciones) {
            if (estacion.getEstado().equals(Estado.ACTIVO)) {
                cuentaActivas++;
            }
        }
        return cuentaActivas;
    }
}
