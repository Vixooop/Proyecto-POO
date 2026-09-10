package src;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public final class Medicion {
    private final LocalDateTime fechaHora;
    private final float valor;
}
    public Medicion(LocalDateTime fechaHora, float valor) {
        this.fechaHora = Objects.requireNonNull(fechaHora);
        this.valor = valor;
    }
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    public float getValor() {
        return valor;
    }
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Medicion)) {
            return false;
        }
        Medicion otra = (Medicion) obj;
        return fechaHora.equals(otra.fechaHora);
    }
    public int hashCode() {
        return fechaHora.hashCode();
    }
    public String toString() {

    }
}
