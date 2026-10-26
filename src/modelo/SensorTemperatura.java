package src.modelo;

import java.util.Optional;

/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
22/10/2026
 */
public class SensorTemperatura extends Sensor implements Alertable{

    public SensorTemperatura(String codigo, String marca, String modelo, EstacionMeteorologica estacion){
        super(codigo,marca,modelo,estacion);
    }
    public static float convertirCelsiusAFahrenheit(float valor) {
        return valor * 9.0f / 5.0f + 32.0f;
    }
    @Override
    public String getUnidad(){
        return "°C";
    }
    @Override
    public TipoSensor getTipo() {
        return TipoSensor.TEMPERATURA;
    }
    @Override
    public boolean esValorAdmisible(float valor){
        if(valor>=-80 && valor<=60){
            return true;
        }else{
            return false;
        }
    }
    @Override
    public Optional<NivelAlerta> evaluaAlerta(float valor) {
        if (valor <= -5 || valor >= 38) {
            return Optional.of(NivelAlerta.ALARMA);
        }
        if (valor <= -2 || valor >= 35) {
            return Optional.of(NivelAlerta.ALERTA);
        }
        if (valor <= 0 || valor >= 32) {
            return Optional.of(NivelAlerta.AVISO);
        }
        return Optional.empty();
    }

    @Override
    public String getFenomeno(float valor) {
        if (valor <= 0) {
            return "Helada";
        }
        if (valor >= 32) {
            return "Calor extremo";
        }
        return "Sin fenomeno";
    }
}

