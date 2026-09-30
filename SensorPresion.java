package src;

public class SensorPresion extends Sensor {
    public SensorPresion(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        super(codigo, marca, modelo, estacion);
    }
    @Override
    public String getUnidad(){
    return "hPa";
    }

    @Override
    public boolean esValorAdmisible(float valor) {
        if(valor<=1100 && valor>=800){
            return true;
        }else{
            return false;
        }
    }
}

