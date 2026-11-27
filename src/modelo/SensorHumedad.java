package modelo;
/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
22/10/2026
 */
public class SensorHumedad extends Sensor {
    public SensorHumedad(String codigo, String marca, String modelo) {
        super(codigo, marca, modelo);
    }
    @Override
    public TipoSensor getTipo() {
        return TipoSensor.HUMEDAD;
    }
    @Override
    public String getUnidad() {
        return "%";
    }
    @Override
    public boolean esValorAdmisible(float valor) {
        return valor >= 0.0f && valor <= 100.0f;
    }
}