//autor: Vicente Navarrete
// 26-10-2026
package modelo;

import controlador.MeteorologiaException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.Arrays;
import java.util.Comparator;

public class EstacionMeteorologica implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codigo;
    private String nombre;
    private float longitud;
    private float latitud;
    private float altitud;
    private Estado estado;
    private Comuna comuna;
    private ArrayList<Sensor> sensores;
    private ArrayList<AlertaMeteorologica> alertas;

    public EstacionMeteorologica(String codigo, String nombre, float longitud, float latitud, float altitud, Comuna comuna) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.longitud = longitud;
        this.latitud = latitud;
        this.altitud = altitud;
        this.comuna = comuna;
        this.estado = Estado.ACTIVO;
        this.sensores = new ArrayList<Sensor>();
        this.alertas = new ArrayList<AlertaMeteorologica>();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPosicion() {
        return "(" + latitud + ", " + longitud + ", " + altitud + " m)";
    }

    public Estado getEstado() {
        return estado;
    }

    public Comuna getComuna() {
        return comuna;
    }

    public void instalaSensor(String codigo, String marca, String modelo, TipoSensor tipo) throws MeteorologiaException {
        if (estado != Estado.ACTIVO) {
            throw new MeteorologiaException("La estación " + this.codigo + " no está activa.");
        }
        if (findSensorById(codigo).isPresent()) {
            throw new MeteorologiaException("La estación " + this.codigo + " ya tiene un sensor con el código " + codigo + ".");
        }
        Optional<Sensor> activo = findSensorActivoDeTipo(tipo);
        if (activo.isPresent()) {
            throw new MeteorologiaException("La estación " + this.codigo + " ya tiene un sensor ACTIVO de tipo " + tipo
                    + " (" + activo.get().getCodigo() + ").");
        }
        Sensor nuevo = null;
        switch (tipo) {
            case HUMEDAD:
                nuevo = new SensorHumedad(codigo, marca, modelo);
                break;
            case TEMPERATURA:
                nuevo = new SensorTemperatura(codigo, marca, modelo);
                break;
            case PRESION:
                nuevo = new SensorPresion(codigo, marca, modelo);
                break;
            case VIENTO:
                nuevo = new SensorViento(codigo, marca, modelo);
                break;
            case PRECIPITACION:
                nuevo = new SensorPrecipitacion(codigo, marca, modelo);
                break;
        }
        sensores.add(nuevo);
    }

    public void cambiaEstadoSensor(String codigoSensor, Estado estado) throws MeteorologiaException {
        Sensor sensor = sensorExistente(codigoSensor);
        if (sensor.getEstado() == estado) {
            throw new MeteorologiaException("El sensor " + codigoSensor + " ya se encuentra en estado " + estado + ".");
        }
        if (estado == Estado.ACTIVO) {
            Optional<Sensor> activo = findSensorActivoDeTipo(sensor.getTipo());
            if (activo.isPresent()) {
                throw new MeteorologiaException("No es posible activar el sensor " + codigoSensor
                        + ": la estación ya tiene activo el sensor " + activo.get().getCodigo()
                        + " de tipo " + sensor.getTipo() + ".");
            }
        }
        sensor.setEstado(estado);
    }

    public void registraMedicion(LocalDateTime fechaHora, float valor, String codigoSensor) throws MeteorologiaException {
        if (estado != Estado.ACTIVO) {
            throw new MeteorologiaException("La estación " + this.codigo + " no está activa.");
        }
        Sensor sensor = sensorExistente(codigoSensor);
        sensor.addMedicion(fechaHora, valor);
        if (sensor instanceof Alertable) {
            Alertable alertable = (Alertable) sensor;
            if (alertable.generaAlerta(valor)) {
                NivelAlerta nivel = alertable.evaluaAlerta(valor).get();
                alertas.add(new AlertaMeteorologica(fechaHora, valor, nivel, alertable.getFenomeno(valor), sensor));
            }
        }
    }

    public Sensor[] getSensores() {
        return sensores.stream().toArray(Sensor[]::new);
    }


    public String[][] getResumenSensores() {
        return sensores.stream()
                .map(this::filaResumen)
                .toArray(String[][]::new);
    }
    public String[][] getMedicionesSensorBetween(String codigoSensor, LocalDateTime inicio, LocalDateTime fin) {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        return findSensorById(codigoSensor)
                .map(sensor -> Arrays.stream(sensor.getMedicionesBetween(inicio, fin))
                        .map(m -> new String[]{
                                m.getFechaHora().format(formatoFecha),
                                m.getFechaHora().format(formatoHora),
                                String.format(Locale.US, "%.1f", m.getValor()),
                                sensor.getUnidad()})
                        .toArray(String[][]::new))
                .orElse(new String[0][4]);
    }

    public AlertaMeteorologica[] getAlertasBetween(LocalDateTime inicio, LocalDateTime fin) {
        return alertas.stream()
                .filter(a -> !a.getFechaHora().isBefore(inicio) && !a.getFechaHora().isAfter(fin))
                .sorted(Comparator.comparing(AlertaMeteorologica::getFechaHora))
                .toArray(AlertaMeteorologica[]::new);
    }

    public Optional<Sensor> findSensorById(String codigo) {
        return sensores.stream()
                .filter(s -> s.getCodigo().equalsIgnoreCase(codigo))
                .findFirst();
    }

    @Override
    public String toString() {
        long operativos = sensores.stream()
                .filter(s -> s.getEstado() == Estado.ACTIVO)
                .count();
        return codigo + "; " + nombre + "; " + getPosicion() + "; " + estado + "; " + operativos;
    }

    private Sensor sensorExistente(String codigoSensor) throws MeteorologiaException {
        return findSensorById(codigoSensor).orElseThrow(() ->
                new MeteorologiaException("No existe el sensor " + codigoSensor + " en la estación " + this.codigo + "."));
    }

    private Optional<Sensor> findSensorActivoDeTipo(TipoSensor tipo) {
        return sensores.stream()
                .filter(s -> s.getEstado() == Estado.ACTIVO && s.getTipo() == tipo)
                .findFirst();
    }

    private String[] filaResumen(Sensor sensor) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        Medicion[] mediciones = sensor.getMediciones();
        String ultima = "Sin mediciones";
        if (mediciones.length > 0) {
            Medicion m = mediciones[mediciones.length - 1];
            ultima = m.getFechaHora().format(formato) + " " + String.format(Locale.US, "%.1f", m.getValor())
                    + " " + sensor.getUnidad();
        }
        return new String[]{sensor.getCodigo(), sensor.getTipo().name(), sensor.getMarca(), sensor.getModelo(),
                sensor.getUnidad(), sensor.getEstado().name(), ultima};
    }
}
