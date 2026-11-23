//autor: Vicente Navarrete
// 19-10-2026
package modelo;

import controlador.MeteorologiaException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;

public abstract class Sensor implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codigo;
    private String marca;
    private String modelo;
    private Estado estado;
    private ArrayList<Medicion> mediciones;

    protected Sensor(String codigo, String marca, String modelo) {
        this.codigo = codigo;
        this.marca = marca;
        this.modelo = modelo;
        this.estado = Estado.ACTIVO;
        this.mediciones = new ArrayList<Medicion>();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void addMedicion(LocalDateTime fechaHora, float valor) throws MeteorologiaException {
        if (estado != Estado.ACTIVO) {
            throw new MeteorologiaException("El sensor " + codigo + " no está activo.");
        }
        if (!esValorAdmisible(valor)) {
            throw new MeteorologiaException("El valor " + String.format(Locale.US, "%.1f", valor) + " " + getUnidad()
                    + " no es admisible para un sensor de tipo " + getTipo() + ".");
        }
        if (mediciones.stream().anyMatch(m -> m.getFechaHora().equals(fechaHora))) {
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
            throw new MeteorologiaException("El sensor " + codigo + " ya registra una medición el "
                    + fechaHora.format(formatoFecha) + " a las " + fechaHora.format(formatoHora) + ".");
        }
        mediciones.add(new Medicion(fechaHora, valor));
    }

    public Medicion[] getMediciones() {
        return mediciones.stream()
                .sorted(Comparator.comparing(Medicion::getFechaHora))
                .toArray(Medicion[]::new);
    }

    public Medicion[] getMedicionesBetween(LocalDateTime inicio, LocalDateTime fin) {
        return Arrays.stream(getMediciones())
                .filter(m -> !m.getFechaHora().isBefore(inicio) && !m.getFechaHora().isAfter(fin))
                .toArray(Medicion[]::new);
    }

    public Optional<ResumenMediciones> getResumenBetween(LocalDateTime inicio, LocalDateTime fin) {
        Medicion[] elegidas = getMedicionesBetween(inicio, fin);
        if (elegidas.length == 0) {
            return Optional.empty();
        }
        return Optional.of(new ResumenMediciones(elegidas));
    }

    public abstract TipoSensor getTipo();

    public abstract String getUnidad();

    public abstract boolean esValorAdmisible(float valor);
}
