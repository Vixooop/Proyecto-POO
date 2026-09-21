package src;

public class Comuna {
    private  int codigo;
    private  String nombre;
    private Region region;

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

    }
    public EstacionMeteorologica findEstacionById(String codigo) {

    }
    public Region getRegion() {
        return region;
    }
    
    public int getCantidadEstaciones() {
        return estaciones.size();
    }

    public int getCantidadEstacionesActivas() {

    }






}
