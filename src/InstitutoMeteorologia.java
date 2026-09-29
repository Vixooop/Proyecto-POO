package src;

import java.time.LocalDateTime;

public class InstitutoMeteorologia {
    public boolean creaRegion(int codigo,String nombre){

    }
    public boolean creaComuna(int codigo, String nombre,int codigoRegion){

    }
    public boolean creaEstacion(String codigo, String nombre, float longitud, float latitud, float altitud, int codRegion, int codComuna){

    }
    public boolean instalaSensor(String codigo, String marca, String modelo, TipoSensor tipo, String codEstacion) {

    }
    public boolean registraMedicion(LocalDateTime fechaHora, float valor, String codEstacion, String codSensor) {

    }
    public String[][] listaRegiones() {

    }
    public String[][] listaComunas() {

    }
    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) {

    }
    public String[][] listaSensores(String codigoEstacion) {

    }
    public String[][] listaMediciones(String codEstacion, String codSensor, LocalDateTime inicio, LocalDateTime fin) {

    }









    }
