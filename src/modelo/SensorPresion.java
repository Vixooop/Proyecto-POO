package src.modelo;

/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
22/10/2026
 */
public class SensorPresion extends Sensor {
    public SensorPresion(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        super(codigo, marca, modelo, estacion);
    }
    @Override
    public TipoSensor getTipo() {
        return TipoSensor.PRESION;
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

