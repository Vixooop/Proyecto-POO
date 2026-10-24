package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import controlador.MeteorologiaException;

public class Region {
    private int codigo;
    private String nombre;
    private List<Comuna> comunas = new ArrayList<Comuna>();

    public Region(int cod, String nom) {
        this.codigo = cod;
        this.nombre = nom;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void addComuna(int codigo, String nombre) throws MeteorologiaException {
    if (findComunaById(codigo).isPresent()) {
        throw new MeteorologiaException("Ya existe una comuna con el código " + codigo + "en la region " + this.nombre);
    }
    for (Comuna comuna : comunas) {
        if (comuna.getNombre().equalsIgnoreCase(nombre)) {
            throw new MeteorologiaException("Ya existe una comuna con el código " + codigo + "en la region " + this.nombre);
        }
    }
        comunas.add(new Comuna(codigo, nombre, this));
    }

    public Optional<Comuna> findComunaById(int codigo) {
        for (Comuna comuna : comunas) {
            if (comuna.getCodigo() == codigo) {
                return Optional.of(comuna);
            }
        }
        return Optional.empty();
    }

    public Comuna[] getComunas() {
        return comunas.toArray(new Comuna[0]);
    }

    public int getCantidadEstaciones() {
        int estaciones = 0;
        for (Comuna comuna : comunas) {
            estaciones += comuna.getEstaciones().length;
        }
        return estaciones;
    }
}