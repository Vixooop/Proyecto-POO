package controlador;
/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
23/10/2026
 */
import modelo.*;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class InstitutoMeteorologia {
    private static InstitutoMeteorologia instancia;
    private List<Region> listaRegion;
    private List<EstacionMeteorologica> listaEstacion;

    private InstitutoMeteorologia() {
        listaRegion = new ArrayList<Region>();
        listaEstacion = new ArrayList<EstacionMeteorologica>();
    }
    public static InstitutoMeteorologia getInstance() {
        if (instancia == null) {
            instancia = new InstitutoMeteorologia();
        }
        return instancia;
    }
    public void creaRegion(int codigo, String nombre) throws MeteorologiaException {
        if (findRegion(codigo).isPresent()) {
            throw new MeteorologiaException("Ya existe una region con el codigo " + codigo);
        }
        for (Region r : listaRegion) {
            if (r.getNombre().equalsIgnoreCase(nombre)) {
                throw new MeteorologiaException("Ya existe una region con el nombre " + nombre);
            }
        }
        listaRegion.add(new Region(codigo, nombre));
    }

    public void creaComuna(int codigo, String nombre, int codigoRegion) throws MeteorologiaException {
        regionExistente(codigoRegion).addComuna(codigo, nombre);
    }

    public void creaEstacion(String codigo, String nombre, float longitud, float latitud, float altitud,
                             int codRegion, int codComuna) throws MeteorologiaException {
        if (findEstacion(codigo).isPresent()) {
            throw new MeteorologiaException("Ya existe una estacion con el codigo " + codigo);
        }
        Comuna comuna = comunaExistente(codRegion, codComuna);
        EstacionMeteorologica nuevaEstacion = new EstacionMeteorologica(codigo, nombre, longitud, latitud, altitud, comuna);
        comuna.addEstacion(nuevaEstacion);
        listaEstacion.add(nuevaEstacion);
    }

    public void instalaSensor(String codigo, String marca, String modelo, TipoSensor tipo, String codEstacion)
            throws MeteorologiaException {
        estacionExistente(codEstacion).instalaSensor(codigo, marca, modelo, tipo);
    }
    public void registraMedicion(LocalDateTime fechaHora, float valor, String codEstacion, String codSensor)
            throws MeteorologiaException {
        estacionExistente(codEstacion).registraMedicion(fechaHora, valor, codSensor);
    }

    public void cambiaEstadoSensor(String codigoEstacion, String codigoSensor, Estado estado)
            throws MeteorologiaException {
        estacionExistente(codigoEstacion).cambiaEstadoSensor(codigoSensor, estado);
    }
    public void cargaDatos() throws MeteorologiaException {
        if (!listaRegion.isEmpty()) {
            throw new MeteorologiaException("La carga de datos solo puede realizarse cuando no existen datos registrados.");
        }
        List<String[]> regiones = PersistenciaTexto.leeRegistros("regiones.txt");
        List<String[]> comunas = PersistenciaTexto.leeRegistros("comunas.txt");
        List<String[]> estaciones = PersistenciaTexto.leeRegistros("estaciones.txt");
        List<String[]> sensores = PersistenciaTexto.leeRegistros("sensores.txt");
        List<String[]> mediciones = PersistenciaTexto.leeRegistros("mediciones.txt");
        try {
            cargaRegiones(regiones);
            cargaComunas(comunas);
            cargaEstaciones(estaciones);
            cargaSensores(sensores, mediciones);
        } catch (MeteorologiaException e) {
            listaRegion.clear();
            listaEstacion.clear();
            throw e;
        }
    }
    public void guardaDatos() throws MeteorologiaException {
        if (listaRegion.isEmpty()) {
            throw new MeteorologiaException("No existen datos para guardar.");
        }
        String sep = PersistenciaTexto.SEPARADOR;
        DateTimeFormatter formato = formatoFechaHora();
        List<String> regiones = new ArrayList<String>();
        List<String> comunas = new ArrayList<String>();
        List<String> estaciones = new ArrayList<String>();
        List<String> sensores = new ArrayList<String>();
        List<String> mediciones = new ArrayList<String>();
        for (Region r : listaRegion) {
            regiones.add(r.getCodigo() + sep + r.getNombre());
            for (Comuna c : r.getComunas()) {
                comunas.add(r.getCodigo() + sep + c.getCodigo() + sep + c.getNombre());
                for (EstacionMeteorologica e : c.getEstaciones()) {
                    String[] pos = e.getPosicion().replace("(", "").replace(")", "").replace(" m", "").split(", ");
                    estaciones.add(e.getCodigo() + sep + e.getNombre() + sep + pos[1] + sep + pos[0] + sep + pos[2] + sep + r.getCodigo() + sep + c.getCodigo());                    for (Sensor s : e.getSensores()) {
                        sensores.add(e.getCodigo() + sep + s.getCodigo() + sep + s.getTipo() + sep + s.getMarca() + sep + s.getModelo() + sep + s.getEstado());
                        for (Medicion m : s.getMediciones()) {
                            mediciones.add(e.getCodigo() + sep + s.getCodigo() + sep + m.getFechaHora().format(formato) + sep + m.getValor());
                        }
                    }
                }
            }
        }
        PersistenciaTexto.escribeRegistros("regiones.txt", regiones);
        PersistenciaTexto.escribeRegistros("comunas.txt", comunas);
        PersistenciaTexto.escribeRegistros("estaciones.txt", estaciones);
        PersistenciaTexto.escribeRegistros("sensores.txt", sensores);
        PersistenciaTexto.escribeRegistros("mediciones.txt", mediciones);
    }

    private void cargaRegiones(List<String[]> registros) throws MeteorologiaException {
        for (int i = 0; i < registros.size(); i++) {
            String[] campos = registros.get(i);
            if (campos.length != 2) {
                throw errorLinea("regiones.txt", i + 1, "formato invalido.");
            }
            try {
                creaRegion(Integer.parseInt(campos[0].trim()), campos[1].trim());
            } catch (NumberFormatException e) {
                throw errorLinea("regiones.txt", i + 1, "formato invalido.");
            } catch (MeteorologiaException e) {
                throw errorLinea("regiones.txt", i + 1, e.getMessage());
            }
        }
    }

    private void cargaComunas(List<String[]> registros) throws MeteorologiaException {
        for (int i = 0; i < registros.size(); i++) {
            String[] campos = registros.get(i);
            if (campos.length != 3) {
                throw errorLinea("comunas.txt", i + 1, "formato invalido.");
            }
            try {
                creaComuna(Integer.parseInt(campos[1].trim()),campos[2].trim(),Integer.parseInt(campos[0].trim()));
            } catch (NumberFormatException e) {
                throw errorLinea("comunas.txt", i + 1, "formato invalido.");
            } catch (MeteorologiaException e) {
                throw errorLinea("comunas.txt", i + 1, e.getMessage());
            }
        }
    }
    private void cargaEstaciones(List<String[]> registros) throws MeteorologiaException {
        for (int i = 0; i < registros.size(); i++) {
            String[] campos = registros.get(i);
            if (campos.length != 7) {
                throw errorLinea("estaciones.txt", i + 1, "formato invalido.");
            }
            try {
                creaEstacion(campos[0].trim(), campos[1].trim(),
                        Float.parseFloat(campos[2].trim()), Float.parseFloat(campos[3].trim()),
                        Float.parseFloat(campos[4].trim()), Integer.parseInt(campos[5].trim()),
                        Integer.parseInt(campos[6].trim()));
            } catch (NumberFormatException e) {
                throw errorLinea("estaciones.txt", i + 1, "formato invalido.");
            } catch (MeteorologiaException e) {
                throw errorLinea("estaciones.txt", i + 1, e.getMessage());
            }
        }
    }
    private void cargaSensores(List<String[]> sensores, List<String[]> mediciones) throws MeteorologiaException {
        for (int i = 0; i < mediciones.size(); i++) {
            if (mediciones.get(i).length != 4) {
                throw errorLinea("mediciones.txt", i + 1, "formato invalido.");
            }
        }
        boolean[] usadas = new boolean[mediciones.size()];
        for (int i = 0; i < sensores.size(); i++) {
            String[] campos = sensores.get(i);
            if (campos.length != 6) {
                throw errorLinea("sensores.txt", i + 1, "formato invalido.");
            }
            try {
                Estado.valueOf(campos[5].trim());
                instalaSensor(campos[1].trim(), campos[3].trim(), campos[4].trim(),
                        TipoSensor.valueOf(campos[2].trim()), campos[0].trim());
            } catch (IllegalArgumentException e) {
                throw errorLinea("sensores.txt", i + 1, "formato invalido.");
            } catch (MeteorologiaException e) {
                throw errorLinea("sensores.txt", i + 1, e.getMessage());
            }
            cargaMediciones(campos[0].trim(), campos[1].trim(), mediciones, usadas);
            cambiaEstadoEnCarga(i + 1, campos[0].trim(), campos[1].trim(), Estado.INACTIVO);
        }
        for (int j = 0; j < mediciones.size(); j++) {
            if (!usadas[j]) {
                // Medicion de un sensor que no existe
                registraMedicionEnCarga(j + 1, mediciones.get(j));
            }
        }
        for (int i = 0; i < sensores.size(); i++) {
            String[] campos = sensores.get(i);
            if (Estado.valueOf(campos[5].trim()) == Estado.ACTIVO) {
                cambiaEstadoEnCarga(i + 1, campos[0].trim(), campos[1].trim(), Estado.ACTIVO);
            }
        }
    }

    private void cargaMediciones(String codEstacion, String codSensor, List<String[]> mediciones,
                                 boolean[] usadas) throws MeteorologiaException {
        for (int j = 0; j < mediciones.size(); j++) {
            String[] campos = mediciones.get(j);
            if (!usadas[j] && campos[0].trim().equals(codEstacion)
                    && campos[1].trim().equalsIgnoreCase(codSensor)) {
                usadas[j] = true;
                registraMedicionEnCarga(j + 1, campos);
            }
        }
    }

    private void registraMedicionEnCarga(int linea, String[] campos) throws MeteorologiaException {
        try {
            registraMedicion(LocalDateTime.parse(campos[2].trim(), formatoFechaHora()),
                    Float.parseFloat(campos[3].trim()), campos[0].trim(), campos[1].trim());
        } catch (IllegalArgumentException | DateTimeException e) {
            throw errorLinea("mediciones.txt", linea, "formato invalido.");
        } catch (MeteorologiaException e) {
            throw errorLinea("mediciones.txt", linea, e.getMessage());
        }
    }

    private void cambiaEstadoEnCarga(int linea, String codEstacion, String codSensor, Estado estado)
            throws MeteorologiaException {
        try {
            cambiaEstadoSensor(codEstacion, codSensor, estado);
        } catch (MeteorologiaException e) {
            throw errorLinea("sensores.txt", linea, e.getMessage());
        }
    }

    private MeteorologiaException errorLinea(String archivo, int linea, String detalle) {
        return new MeteorologiaException("Linea " + linea + " del archivo " + archivo + ": " + detalle + " No se cargo ningun dato.");
    }
    public String[][] listaRegiones() {
        String[][] listaRSalida = new String[listaRegion.size()][4];
        int i = 0;
        for (Region r : listaRegion) {
            listaRSalida[i][0] = String.valueOf(r.getCodigo());
            listaRSalida[i][1] = r.getNombre();
            Comuna[] comunas = r.getComunas();
            listaRSalida[i][2] = String.valueOf(comunas.length); //N comunas
            listaRSalida[i][3] = String.valueOf(r.getCantidadEstaciones()); //N estaciones
            i++;
        }
        return listaRSalida;
    }
    public String[][] listaComunas() {
        int totalComunas = 0;
        for (Region r : listaRegion) {
            Comuna[] comunas = r.getComunas();
            totalComunas += comunas.length;
        }
        String[][] listaCSalida = new String[totalComunas][5];
        int fila = 0;
        for (Region r : listaRegion) {
            Comuna[] comunas = r.getComunas();
            for (int i = 0; i < comunas.length; i++) {
                listaCSalida[fila][0] = String.valueOf(comunas[i].getCodigo());
                listaCSalida[fila][1] = comunas[i].getNombre();
                listaCSalida[fila][2] = r.getNombre();
                listaCSalida[fila][3] = String.valueOf(comunas[i].getEstaciones().length);
                listaCSalida[fila][4] = String.valueOf(comunas[i].getCantidadEstacionesActivas());
                fila++;
            }
        }
        return listaCSalida;
    }

    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) throws MeteorologiaException {
        EstacionMeteorologica[] estaciones = comunaExistente(codigoRegion, codigoComuna).getEstaciones();
        String[][] estacionSensor = new String[estaciones.length][5];
        for (int i = 0; i < estaciones.length; i++) {
            // toString: codigo; nombre; ubicacion; estado; sensores operativos
            estacionSensor[i] = estaciones[i].toString().split("; ");
        }
        return estacionSensor;
    }

    public String[][] listaSensores(String codigoEstacion) throws MeteorologiaException {
        return estacionExistente(codigoEstacion).getResumenSensores();
    }

    public String[][] listaMediciones(String codEstacion, String codSensor, LocalDateTime inicio,
                                      LocalDateTime fin) throws MeteorologiaException {
        sensorExistente(codEstacion, codSensor);
        validaPeriodo(inicio, fin);
        return estacionExistente(codEstacion).getMedicionesSensorBetween(codSensor, inicio, fin);
    }
    public String[] listaResumenMediciones(String codigoEstacion, String codigoSensor, LocalDateTime inicio, LocalDateTime fin)
            throws MeteorologiaException {
        Sensor sensor = sensorExistente(codigoEstacion, codigoSensor);
        validaPeriodo(inicio, fin);
        Optional<ResumenMediciones> resumen = sensor.getResumenBetween(inicio, fin);
        if (!resumen.isPresent()) {
            return new String[0];
        }
        ResumenMediciones r = resumen.get();
        DateTimeFormatter formato = formatoFechaHora();
        return new String[]{
                sensor.getTipo().name(),
                sensor.getUnidad(),
                String.valueOf(r.getCantidad()),
                unDecimal(r.getMinimo().getValor()),
                r.getMinimo().getFechaHora().format(formato),
                unDecimal(r.getMaximo().getValor()),
                r.getMaximo().getFechaHora().format(formato),
                unDecimal(r.getPromedio())
        };
    }

    public String[][] listaAlertas(String codigoEstacion, LocalDateTime inicio, LocalDateTime fin)
            throws MeteorologiaException {
        EstacionMeteorologica estacion = estacionExistente(codigoEstacion);
        validaPeriodo(inicio, fin);
        AlertaMeteorologica[] alertas = estacion.getAlertasBetween(inicio, fin);
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        String[][] salida = new String[alertas.length][7];
        for (int i = 0; i < alertas.length; i++) {
            AlertaMeteorologica a = alertas[i];
            salida[i][0] = a.getFechaHora().format(formatoFecha);
            salida[i][1] = a.getFechaHora().format(formatoHora);
            salida[i][2] = a.getSensor().getCodigo();
            salida[i][3] = a.getFenomeno();
            salida[i][4] = unDecimal(a.getValor());
            salida[i][5] = a.getSensor().getUnidad();
            salida[i][6] = a.getNivel().name();
        }
        return salida;
    }
    private Optional<Region> findRegion(int codigo) {
        for (Region r : listaRegion) {
            if (r.getCodigo() == codigo) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }
    private Optional<Comuna> findComuna(int codRegion, int codComuna) {
        Optional<Region> region = findRegion(codRegion);
        if (!region.isPresent()) {
            return Optional.empty();
        }
        return region.get().findComunaById(codComuna);
    }
    private Optional<EstacionMeteorologica> findEstacion(String codigo) {
        for (EstacionMeteorologica e : listaEstacion) {
            if (e.getCodigo().equals(codigo)) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }
    private Optional<Sensor> findSensor(String codEstacion, String codSensor) {
        Optional<EstacionMeteorologica> estacion = findEstacion(codEstacion);
        if (!estacion.isPresent()) {
            return Optional.empty();
        }
        return estacion.get().findSensorById(codSensor);
    }
    private Region regionExistente(int codRegion) throws MeteorologiaException {
        return findRegion(codRegion).orElseThrow(() ->
                new MeteorologiaException("No existe una region con el codigo " + codRegion));
    }
    private Comuna comunaExistente(int codRegion, int codComuna) throws MeteorologiaException {
        Region region = regionExistente(codRegion);
        return findComuna(codRegion, codComuna).orElseThrow(() -> new MeteorologiaException("No existe una comuna con el codigo " + codComuna + " en la region " + region.getNombre() ));
    }
    private EstacionMeteorologica estacionExistente(String codEstacion) throws MeteorologiaException {
        return findEstacion(codEstacion).orElseThrow(() -> new MeteorologiaException("No existe una estacion con el codigo " + codEstacion));
    }

    private Sensor sensorExistente(String codEstacion, String codSensor) throws MeteorologiaException {
        estacionExistente(codEstacion);
        return findSensor(codEstacion, codSensor).orElseThrow(() ->
                new MeteorologiaException("No existe el sensor " + codSensor + " en la estacion " + codEstacion));
    }

    private void validaPeriodo(LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {
        if (fin.isBefore(inicio)) {
            DateTimeFormatter formato = formatoFechaHora();
            throw new MeteorologiaException("La fecha y hora de termino (" + fin.format(formato) + ") es anterior a la de inicio (" + inicio.format(formato) + ").");
        }
    }
    private DateTimeFormatter formatoFechaHora() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    }
    private String unDecimal(float valor) {
        return String.format(Locale.US, "%.1f", valor);
    }
}