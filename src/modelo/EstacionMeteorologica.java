//autor: Vicente Navarrete
// 26-10-2026
package modelo;

import controlador.MeteorologiaException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

public class EstacionMeteorologica {
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
        for (Sensor s : sensores) {
            if (s.getCodigo().equalsIgnoreCase(codigo)) {
                throw new MeteorologiaException("La estación " + this.codigo + " ya tiene un sensor con el código " + codigo + ".");
            }
        }
        for (Sensor s : sensores) {
            if (s.getEstado() == Estado.ACTIVO && s.getTipo() == tipo) {
                throw new MeteorologiaException("La estación " + this.codigo + " ya tiene un sensor ACTIVO de tipo " + tipo
                        + " (" + s.getCodigo() + ").");
            }
        }
        Sensor nuevo = null;
        switch (tipo) {
            case HUMEDAD:
                nuevo = new SensorHumedad(codigo, marca, modelo, this);
                break;
            case TEMPERATURA:
                nuevo = new SensorTemperatura(codigo, marca, modelo, this);
                break;
            case PRESION:
                nuevo = new SensorPresion(codigo, marca, modelo, this);
                break;
            case VIENTO:
                nuevo = new SensorViento(codigo, marca, modelo, this);
                break;
            case PRECIPITACION:
                nuevo = new SensorPrecipitacion(codigo, marca, modelo, this);
                break;
        }
        sensores.add(nuevo);
    }

    public void cambiaEstadoSensor(String codigoSensor, Estado estado) throws MeteorologiaException {
        Optional<Sensor> buscado = findSensorById(codigoSensor);
        if (!buscado.isPresent()) {
            throw new MeteorologiaException("No existe el sensor " + codigoSensor + " en la estación " + this.codigo + ".");
        }
        Sensor sensor = buscado.get();
        if (sensor.getEstado() == estado) {
            throw new MeteorologiaException("El sensor " + codigoSensor + " ya se encuentra en estado " + estado + ".");
        }
        if (estado == Estado.ACTIVO) {
            for (Sensor s : sensores) {
                if (s != sensor && s.getEstado() == Estado.ACTIVO && s.getTipo() == sensor.getTipo()) {
                    throw new MeteorologiaException("No es posible activar el sensor " + codigoSensor
                            + ": la estación ya tiene activo el sensor " + s.getCodigo() + " de tipo " + s.getTipo() + ".");
                }
            }
        }
        sensor.setEstado(estado);
    }

    public void registraMedicion(LocalDateTime fechaHora, float valor, String codigoSensor) throws MeteorologiaException {
        if (estado != Estado.ACTIVO) {
            throw new MeteorologiaException("La estación " + this.codigo + " no está activa.");
        }
        Optional<Sensor> buscado = findSensorById(codigoSensor);
        if (!buscado.isPresent()) {
            throw new MeteorologiaException("No existe el sensor " + codigoSensor + " en la estación " + this.codigo + ".");
        }
        Sensor sensor = buscado.get();
        sensor.addMedicion(fechaHora, valor);
        if (sensor instanceof Alertable) {
            Alertable alertable = (Alertable) sensor;
            if (alertable.generaAlerta(valor)) {
                NivelAlerta nivel = alertable.evaluaAlerta(valor).get();
                AlertaMeteorologica alerta = new AlertaMeteorologica(fechaHora, valor, nivel, alertable.getFenomeno(valor), sensor);
                alertas.add(alerta);
            }
        }
    }

    public Sensor[] getSensores() {
        Sensor[] arreglo = new Sensor[sensores.size()];
        for (int i = 0; i < sensores.size(); i++) {
            arreglo[i] = sensores.get(i);
        }
        return arreglo;
    }

    public String[][] getResumenSensores() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String[][] datos = new String[sensores.size()][7];
        for (int i = 0; i < sensores.size(); i++) {
            Sensor sensor = sensores.get(i);
            Medicion[] mediciones = sensor.getMediciones();
            datos[i][0] = sensor.getCodigo();
            datos[i][1] = sensor.getTipo().name();
            datos[i][2] = sensor.getMarca();
            datos[i][3] = sensor.getModelo();
            datos[i][4] = sensor.getUnidad();
            datos[i][5] = sensor.getEstado().name();
            if (mediciones.length == 0) {
                datos[i][6] = "Sin mediciones";
            } else {
                Medicion ultima = mediciones[mediciones.length - 1];
                datos[i][6] = ultima.getFechaHora().format(formato) + " " + String.format(Locale.US, "%.1f", ultima.getValor())
                        + " " + sensor.getUnidad();
            }
        }
        return datos;
    }

    public String[][] getMedicionesSensorBetween(String codigoSensor, LocalDateTime inicio, LocalDateTime fin) {
        Optional<Sensor> buscado = findSensorById(codigoSensor);
        if (!buscado.isPresent()) {
            return new String[0][4];
        }
        Sensor sensor = buscado.get();
        Medicion[] mediciones = sensor.getMedicionesBetween(inicio, fin);
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        String[][] datos = new String[mediciones.length][4];
        for (int i = 0; i < mediciones.length; i++) {
            datos[i][0] = mediciones[i].getFechaHora().format(formatoFecha);
            datos[i][1] = mediciones[i].getFechaHora().format(formatoHora);
            datos[i][2] = String.format(Locale.US, "%.1f", mediciones[i].getValor());
            datos[i][3] = sensor.getUnidad();
        }
        return datos;
    }

    public AlertaMeteorologica[] getAlertasBetween(LocalDateTime inicio, LocalDateTime fin) {
        ArrayList<AlertaMeteorologica> elegidas = new ArrayList<AlertaMeteorologica>();
        for (AlertaMeteorologica a : alertas) {
            if (!a.getFechaHora().isBefore(inicio) && !a.getFechaHora().isAfter(fin)) {
                elegidas.add(a);
            }
        }
        AlertaMeteorologica[] arreglo = new AlertaMeteorologica[elegidas.size()];
        for (int i = 0; i < elegidas.size(); i++) {
            arreglo[i] = elegidas.get(i);
        }
        for (int i = 0; i < arreglo.length; i++) {
            for (int j = 0; j < arreglo.length - 1 - i; j++) {
                if (arreglo[j].getFechaHora().isAfter(arreglo[j + 1].getFechaHora())) {
                    AlertaMeteorologica auxiliar = arreglo[j];
                    arreglo[j] = arreglo[j + 1];
                    arreglo[j + 1] = auxiliar;
                }
            }
        }
        return arreglo;
    }

    public Optional<Sensor> findSensorById(String codigo) {
        for (Sensor s : sensores) {
            if (s.getCodigo().equalsIgnoreCase(codigo)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        int operativos = 0;
        for (Sensor s : sensores) {
            if (s.getEstado() == Estado.ACTIVO) {
                operativos++;
            }
        }
        return codigo + "; " + nombre + "; " + getPosicion() + "; " + estado + "; " + operativos;
    }
}
