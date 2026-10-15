package src;

import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class Sensor {
    private String codigo;
    private String marca;
    private String modelo;
    private Estado estado;
    private EstacionMeteorologica estacion;
    private ArrayList <EstacionMeteorologica> listadoEstacion = new ArrayList<EstacionMeteorologica>();
    private ArrayList <Medicion> mediciones;

    protected Sensor(String codigo, String marca, String modelo, EstacionMeteorologica estacion){
        this.codigo = codigo;
        this.marca = marca;
        this.modelo = modelo;
        this.estacion = estacion;
    }
    //GETS
    public abstract String getUnidad();
    public abstract boolean esValorAdmisible(float valor);

    public String getCodigo() {
        return codigo;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    //  Set Estado
    public EstacionMeteorologica getEstacion() {
        return estacion;
    }
    //Medicion
    public boolean addMedicion(LocalDateTime fechaHora, float valor){
        if(estado != Estado.ACTIVO){
        return false;
        }
        if(!esValorAdmisible(valor)){
            return false;
        }
        Medicion m = new Medicion(fechaHora, valor);
        for(Medicion medicion : mediciones){
            if(medicion.equals(m)){
                return false;
            }
        }
        mediciones.add(m);
        return true;
    }
    public Medicion getLastMedicion(){
        Medicion ultima = null;
        for(Medicion medicion : mediciones){
            if(ultima == null || medicion.getFechaHora().isAfter(ultima.getFechaHora())){
                ultima = medicion;
            }
        }
        return ultima;
    }
    public medicion[] getMedicionesBetween(LocalDateTime inicio, LocalDateTime fin){
        if(inicio.isAfter(fin)){
            return new Medicion[0];
        }
        int cantidad = 0;
        for(Medicion medicion : mediciones) {
            LocalDateTime fecha = medicion.getFechaHora();
            if(!fecha.isBefore(inicio) && !fecha.isAfter(fin)){
                cantidad++;
            }
        }
        Medicion[] resultado = new Medicion[cantidad];
        int posicion = 0;
        for(Medicion medicion : mediciones){
            LocalDateTime fecha = medicion.getFechaHora;
            if(!fecha.isBefore(inicio) && !fecha.isAfter(fin)){
                resultado[posicion] = medicion;
                posicion++;
            }
        }
        for (int i = 0; i < resultado.length -1; i++) {
            for (int j = 0; j < resultado.length; j++) {
                if(resultado[i].getFechaHora().isAfter(resultado[j].getFechaHora())){
                    Medicion auxiliar = resultado[i];
                    resultado[i] = resultado[j];
                    resultado[i] = auxiliar;
                }
            }
        }
        return resultado;

    }
}