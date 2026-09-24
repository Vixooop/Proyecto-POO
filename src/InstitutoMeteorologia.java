package src;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InstitutoMeteorologia {
    private List<Region> listaRegion = new ArrayList<Region>();
    public boolean creaRegion(int codigo,String nombre) {
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
        return repetido;
    }
    public boolean creaComuna(int codigo, String nombre,int codigoRegion){
        boolean existeReg = false;
        Region region = null;
        for (Region r : listaRegion){
            if(r.getCodigo() == codigoRegion){
                existeReg = true;
                region = new Region(r.getCodigo(),r.getNombre());
            }
        }
        if(!existeReg){
            return false;
        }else{
           region.addComuna(codigo,nombre);
           return true;
        }
    }
    public boolean creaEstacion(String codigo, String nombre, float longitud, float latitud, float altitud, int codRegion, int codComuna){

    }
    public boolean instalaSensor(String codigo, String marca, String modelo, TipoSensor tipo, String codEstacion) {

    }
    public boolean registraMedicion(LocalDateTime fechaHora, float valor, String codEstacion, String codSensor) {

    }
    public String[][] listaRegiones() {
        String[][] listaRSalida = new String[listaRegion.size()][4];
        int i = 0;
        for (Region r : listaRegion){
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
        String[][] listaCSalida = new String[listaRegion.size()][5];
        int fila = 0;
        for (Region r : listaRegion){
            Comuna[] comunas = r.getComunas();
            for (int i = 0;i<comunas.length;i++){
                listaCSalida[fila][0] = String.valueOf(comunas[i].getCodigo());
                listaCSalida[fila][1] = comunas[i].getNombre();
                listaCSalida[fila][2] = String.valueOf(r.getNombre());
                listaCSalida[fila][3] = String.valueOf(comunas[i].getCantidadEstaciones());
                listaCSalida[fila][4] = String.valueOf(comunas[i].getCantidadEstacionesActivas());
                fila++;
            }
        }
        return listaCSalida;


    }
    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) {

    }
    public String[][] listaSensores(String codigoEstacion) {

    }
    public String[][] listaMediciones(String codEstacion, String codSensor, LocalDateTime inicio, LocalDateTime fin) {

    }









    }
