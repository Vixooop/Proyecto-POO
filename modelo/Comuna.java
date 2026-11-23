package modelo;
/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
26/10/2026
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Comuna implements Serializable {
    private int codigo;
    private String nombre;
    private Region region;
    private static final long serialVersionUID = 1L;
    private List<EstacionMeteorologica> estaciones = new ArrayList<EstacionMeteorologica>();

    public Comuna(int codigo, String nombre, Region region) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.region = region;
        this.estaciones = new ArrayList<>();
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

    public Optional<EstacionMeteorologica> findEstacionById(String codEstacion) {
        return estaciones.stream()
                .filter(e -> e.getCodigo().equalsIgnoreCase(codEstacion))
                .findFirst();
    }

    public EstacionMeteorologica[] getEstaciones() {
        return estaciones.stream().toArray(EstacionMeteorologica[]::new);
    }

    public int getCantidadEstacionesActivas() {
        return (int) estaciones.stream()
                .filter(e -> e.getEstado() == Estado.ACTIVO)
                .count();
    }
}