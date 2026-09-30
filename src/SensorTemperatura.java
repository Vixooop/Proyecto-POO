package src;
/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
29/09/2026
 */
public class SensorTemperatura extends Sensor {

    public SensorTemperatura(String codigo, String marca, String modelo, EstacionMeteorologica estacion){
        super(codigo,marca,modelo,estacion);
    }
    public float convertirCelciusAFarenheit(float valor){
        return valor * 9.0f / 5.0f + 32.0f;
    }
    @Override
    public String getUnidad(){
        return "°C";
    }
    @Override
    public boolean esValorAdmisible(float valor){
        if(valor>=-80 && valor<=60){
            return true;
        }else{
            return false;
        }
    }
}
