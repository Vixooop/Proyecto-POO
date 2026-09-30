//autores: Vicente Navarrete, Dario
package src;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Medicion {
    private final LocalDateTime fechaHora;
    private final float valor;

    public Medicion(LocalDateTime fechaHora, float valor) {
        this.fechaHora = fechaHora;
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
        Medicion variable = (Medicion) obj;
        return fechaHora.equals(variable.fechaHora);
    }

    @Override
    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaHora.format(formato) + "; " + valor;
    }
}