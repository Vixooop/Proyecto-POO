package src.modelo;

import java.util.Optional;

/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
22/10/2026
 */
public class SensorViento extends Sensor implements Alertable{

    public SensorViento(String codigo, String marca, String modelo, EstacionMeteorologica estacion){
        super(codigo,marca,modelo,estacion);
    }
    @Override
    public TipoSensor getTipo() {
        return TipoSensor.VIENTO;
    }
    @Override
    public String getUnidad() {
        return "km/h";
    }

    @Override
    public boolean esValorAdmisible(float valor) {
        return valor >= 0 && valor <= 250;
    }

    @Override
    public Optional<NivelAlerta> evaluaAlerta(float valor) {
        if (valor >= 90) {
            return Optional.of(NivelAlerta.ALARMA);
        }
        if (valor >= 70) {
            return Optional.of(NivelAlerta.ALERTA);
        }
        if (valor >= 50) {
            return Optional.of(NivelAlerta.AVISO);
        }
        return Optional.empty();
    }

    @Override
    public String getFenomeno(float valor) {
        return "Viento intenso";
    }
}