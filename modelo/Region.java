package modelo;
/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
29/09/2026
 */

import controlador.MeteorologiaException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Region implements Serializable {
    private static final long serialVersionUID = 1L;
    private int codigo;
    private String nombre;
    private List<Comuna> comunas = new ArrayList<Comuna>();

    public Region(int cod, String nom) {
        this.codigo = cod;
        this.nombre = nom;
        this.comunas = new ArrayList<>();
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void addComuna(int codigo, String nombre) throws MeteorologiaException {
        if (findComunaById(codigo).isPresent()) {
            throw new MeteorologiaException("La region " + this.nombre
                    + " ya tiene una comuna con el codigo " + codigo + ".");
        }
        if (comunas.stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre))) {
            throw new MeteorologiaException("La region " + this.nombre
                    + " ya tiene una comuna con el nombre " + nombre + ".");
        }
        comunas.add(new Comuna(codigo, nombre, this));
    }

    public Optional<Comuna> findComunaById(int codComuna) {
        return comunas.stream()
                .filter(c -> c.getCodigo()== codComuna)
                .findFirst();
    }

    public Comuna[] getComunas() {
        return comunas.stream().toArray(Comuna[]::new);
    }

    public int getCantidadEstaciones() {
        return comunas.stream()
                .mapToInt(c -> c.getEstaciones().length)
                .sum();
    }
}