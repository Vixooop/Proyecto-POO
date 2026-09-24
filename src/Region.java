package src;

import java.util.ArrayList;
import java.util.List;

public class Region {
    private  int codigo;
    private  String nombre;
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

    public boolean addComuna(int codigo, String nombre) {
        boolean repetido = false;
        for (Comuna c : comunas) {
            if (c.getCodigo() == codigo) {
                System.out.println("codigo repetido");
                repetido = true;
            }
            if (c.getNombre().equals(nombre)) {
                System.out.println("nombre repetido");
                repetido = true;
            }
        }
        if (!repetido) {
            Comuna nueva = new Comuna( codigo, nombre,this);
            comunas.add(nueva);
        }
        return !repetido;
    }


    public Comuna findComunaById(int codigo) {
        for(Comuna comuna : comunas){
            if(comuna.getCodigo() == codigo){
                return comuna;
            }
        }
        return null;
    }
    public Comuna[] getComunas() {
        Comuna[] salidaCom = new Comuna[comunas.size()];
        int i =0;
        for(Comuna comuna : comunas){
            salidaCom[i]= comuna;
            i++;
        }
        return salidaCom;
    }
    public int getCantidadEstaciones() {
        int estaciones = 0;
        for(Comuna comuna : comunas){
            estaciones += comuna.getCantidadEstaciones();
        }
        return estaciones;
    }
    }
