//autor: Vicente Navarrete
// 19-10-2026
package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public final class Medicion {
    private final LocalDateTime fechaHora;
    private final float valor;

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

    @Override
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

    @Override
    public int hashCode() {
        return fechaHora.hashCode();
    }

    @Override
    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaHora.format(formato) + "; " + String.format(Locale.US, "%.1f", valor);
    }
}
