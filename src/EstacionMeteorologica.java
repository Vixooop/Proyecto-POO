package src;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EstacionMeteorologica {
    private String codigo;
    private String nombre;
    private float longitud;
    private float latitud;
    private float altitud;
    private Estado estado;
    private Comuna comuna;
    private ArrayList<Sensor> sensores;

    public EstacionMeteorologica(String codigo, String nombre, float longitud, float latitud, float altitud, Comuna comuna) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.longitud = longitud;
        this.latitud = latitud;
        this.altitud = altitud;
        this.comuna = comuna;
        this.estado = Estado.ACTIVO;
        this.sensores = new ArrayList<Sensor>();
    }

    public boolean instalaSensor(String codigo, String marca, String modelo, TipoSensor tipo) {
        if (estado != Estado.ACTIVO) {
            return false;
        }
        for (Sensor sensor : sensores) {
            if (sensor.getCodigo().equalsIgnoreCase(codigo)) {
                return false;
            }
            if (sensor.getEstado() == Estado.ACTIVO
                    && tipoDeSensor(sensor).equals(tipo.name())) {
                return false;
            }
        }

        Sensor nuevo;
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
            default:
                return false;
        }
        sensores.add(nuevo);
        return true;
    }

    public boolean registraMedicion(LocalDateTime fechaHora, float valor, String codigoSensor) {
        if (estado != Estado.ACTIVO) {
            return false;
        }
        Sensor sensor = buscarSensor(codigoSensor);
        if (sensor == null) {
            return false;
        }
        return sensor.addMedicion(fechaHora, valor);
    }

    @Override
    public String toString() {
        int operativos = 0;
        for (Sensor sensor : sensores) {
            if (sensor.getEstado() == Estado.ACTIVO) {
                operativos++;
            }
        }
        String ubicacion = "(" + latitud + ", " + longitud + ", " + altitud + " m)";
        return codigo + "; " + nombre + "; " + ubicacion + "; " + estado + "; " + operativos;
    }

    public String[][] getResumenSensores() {
        String[][] datos = new String[sensores.size()][7];
        for (int i = 0; i < sensores.size(); i++) {
            Sensor sensor = sensores.get(i);
            Medicion ultima = sensor.getLastMedicion();
            datos[i][0] = sensor.getCodigo();
            datos[i][1] = tipoDeSensor(sensor);
            datos[i][2] = sensor.getMarca();
            datos[i][3] = sensor.getModelo();
            datos[i][4] = sensor.getUnidad();
            datos[i][5] = sensor.getEstado().name();
            if (ultima == null) {
                datos[i][6] = "Sin mediciones";
            } else {
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                datos[i][6] = ultima.getFechaHora().format(formato) + " " + ultima.getValor() + " " + sensor.getUnidad();
            }
        }
        return datos;
    }

    public String[][] getMedicionesSensorBetween(String codigoSensor, LocalDateTime inicio, LocalDateTime fin) {
        Sensor sensor = buscarSensor(codigoSensor);
        if (sensor == null) {
            return new String[0][4];
        }
        Medicion[] mediciones = sensor.getMedicionesBetween(inicio, fin);
        String[][] datos = new String[mediciones.length][4];
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 0; i < mediciones.length; i++) {
            datos[i][0] = mediciones[i].getFechaHora().format(formatoFecha);
            datos[i][1] = mediciones[i].getFechaHora().format(formatoHora);
            datos[i][2] = String.valueOf(mediciones[i].getValor());
            datos[i][3] = sensor.getUnidad();
        }
        return datos;
    }

    private Sensor buscarSensor(String codigoBuscado) {
        for (Sensor sensor : sensores) {
            if (sensor.getCodigo().equalsIgnoreCase(codigoBuscado)) {
                return sensor;
            }
        }
        return null;
    }

    private String tipoDeSensor(Sensor sensor) {
        String unidad = sensor.getUnidad();
        if (unidad.equals("%")) {
            return TipoSensor.HUMEDAD.name();
        }
        if (unidad.equals("°C")) {
            return TipoSensor.TEMPERATURA.name();
        }
        if (unidad.equals("hPa")) {
            return TipoSensor.PRESION.name();
        }
        if (unidad.equals("km/h")) {
            return TipoSensor.VIENTO.name();
        }
        return TipoSensor.PRECIPITACION.name();
    }
}
