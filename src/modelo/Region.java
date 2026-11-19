package src.modelo;
/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
29/09/2026
 */

import src.controlador.MeteorologiaException;

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

    public void addComuna(Comuna comuna) throws MeteorologiaException {
        if (comunas.stream().anyMatch(c -> c.getCodigo() ==(comuna.getCodigo()))) {
            throw new MeteorologiaException("Ya existe una comuna con el código " + comuna.getCodigo()
                    + " en la región " + codigo + ".");
        }
        comunas.add(comuna);
    }

    public Optional<Comuna> findComunaById(int codComuna) {
        return comunas.stream()
                .filter(c -> c.getCodigo()== codComuna)
                .findFirst();
    }

    public Comuna[] getComunas() {
        return comunas.toArray(new Comuna[0]);
    }

    public int getCantidadEstaciones() {
        return comunas.stream()
                .mapToInt(c -> c.getEstaciones().length)
                .sum();
    }
}