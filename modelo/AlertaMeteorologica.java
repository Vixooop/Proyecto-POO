//autor Vicente Navarrete
package modelo;
import java.time.LocalDateTime;

public final class AlertaMeteorologica {

    private final LocalDateTime fechaHora;
    private final float valor;
    private final NivelAlerta nivel;
    private final String fenomeno;

    public AlertaMeteorologica(LocalDateTime fechaHora, float valor, NivelAlerta nivel, String fenomeno) {
        this.fechaHora = fechaHora;
        this.valor = valor;
        this.nivel = nivel;
        this.fenomeno = fenomeno;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public float getValor() {
        return valor;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public String getFenomeno() {
        return fenomeno;
    }

    public Sensor getSensor() {
        return sensor;
    }
}