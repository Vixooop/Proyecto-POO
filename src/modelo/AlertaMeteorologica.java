//autor Vicente Navarrete, Osvaldo Baeza
// 26-10-2026
package modelo;
import modelo.NivelAlerta;
import java.io.Serializable;
import java.time.LocalDateTime;

public final class AlertaMeteorologica implements Serializable {

    private static final long serialVersionUID = 1L;
    private final LocalDateTime fechaHora;
    private final float valor;
    private final NivelAlerta nivel;
    private final String fenomeno;
    private final Sensor sensor;

    public AlertaMeteorologica(LocalDateTime fechaHora, float valor, NivelAlerta nivel, String fenomeno, Sensor sensor) {
        this.fechaHora = fechaHora;
        this.valor = valor;
        this.nivel = nivel;
        this.fenomeno = fenomeno;
        this.sensor = sensor;
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