package src;
/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
29/09/2026
 */

public class SensorPrecipitacion extends Sensor {

    public SensorPrecipitacion(String codigo, String marca, String modelo, EstacionMeteorologica estacion){
        super(codigo,marca,modelo,estacion);
    }
    public float convertirCelciusAFarenheit(float valor){
        return valor * 9.0f / 5.0f + 32.0f;
    }
    @Override
    public String getUnidad(){
        return "mm";
    }
    @Override
    public boolean esValorAdmisible(float valor){
        if(valor>=0 && valor<=500){
            return true;
        }else{
            return false;
        }
    }
}