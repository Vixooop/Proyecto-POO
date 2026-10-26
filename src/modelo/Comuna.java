package src.modelo;
/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
26/10/2026
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Region getRegion() {
        return this.region;
    }

    public void addEstacion(EstacionMeteorologica estacion) {
        estaciones.add(estacion);
    }

    public Optional<EstacionMeteorologica> findEstacionById(String codigo) {
        for (EstacionMeteorologica e : estaciones) {
            if (e.getCodigo().equals(codigo)) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    public EstacionMeteorologica[] getEstaciones() {
        return estaciones.toArray(new EstacionMeteorologica[0]);
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