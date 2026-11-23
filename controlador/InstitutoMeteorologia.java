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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if (listaRegion.stream().anyMatch(r -> r.getNombre().equalsIgnoreCase(nombre))) {
            throw new MeteorologiaException("Ya existe una region con el nombre " + nombre);
        }
        listaRegion.add(new Region(codigo, nombre));
    }


    public void creaComuna(int codigo, String nombre, int codigoRegion) throws MeteorologiaException {
        regionExistente(codigoRegion).addComuna(codigo, nombre);
    }

    public void creaEstacion(String codigo, String nombre, float longitud, float latitud, float altitud, int codRegion, int codComuna) throws MeteorologiaException {
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
    public void importaDatosTexto() throws MeteorologiaException {
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

    public void cargaDatos() throws MeteorologiaException {
        if (!listaRegion.isEmpty()) {
            throw new MeteorologiaException("La carga de datos solo puede realizarse cuando no existen datos registrados.");
        }
        Object objeto = PersistenciaObjetos.leeObjeto("meteorologia.dat");
        if (!(objeto instanceof List) || !((List<?>) objeto).stream().allMatch(o -> o instanceof Region)) {
            throw new MeteorologiaException("El archivo meteorologia.dat no contiene datos válidos del sistema.");
        }
        listaRegion = ((List<?>) objeto).stream()
                .map(Region.class::cast)
                .collect(Collectors.toCollection(ArrayList::new));
        listaEstacion = listaRegion.stream()
                .flatMap(r -> Arrays.stream(r.getComunas()))
                .flatMap(c -> Arrays.stream(c.getEstaciones()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    public void guardaDatos() throws MeteorologiaException {
        if (listaRegion.isEmpty()) {
            throw new MeteorologiaException("No existen datos para guardar.");
        }
        PersistenciaObjetos.escribeObjeto("meteorologia.dat", listaRegion);
    }
    public boolean existenDatos() {
        return !listaRegion.isEmpty();
    }
    public String[] getCodigosEstaciones() {
        return listaEstacion.stream()
                .map(EstacionMeteorologica::getCodigo)
                .sorted()
                .toArray(String[]::new);
    }
    public String[] getCodigosSensores(String codEstacion) throws MeteorologiaException {
        return Arrays.stream(estacionExistente(codEstacion).getSensores())
                .map(Sensor::getCodigo)
                .toArray(String[]::new);
    }
    public String[] getDatosSensor(String codEstacion, String codSensor) throws MeteorologiaException {
        Sensor sensor = sensorExistente(codEstacion, codSensor);
        return new String[]{sensor.getTipo().name(), sensor.getUnidad(), sensor.getEstado().name()};
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
        return listaRegion.stream()
                .map(r -> new String[]{
                        String.valueOf(r.getCodigo()),
                        r.getNombre(),
                        String.valueOf(r.getComunas().length),
                        String.valueOf(r.getCantidadEstaciones())})
                .toArray(String[][]::new);
    }
    public String[][] listaComunas() {
        return listaRegion.stream()
                .flatMap(r -> Arrays.stream(r.getComunas())
                        .map(c -> new String[]{
                                String.valueOf(c.getCodigo()),
                                c.getNombre(),
                                r.getNombre(),
                                String.valueOf(c.getEstaciones().length),
                                String.valueOf(c.getCantidadEstacionesActivas())})).toArray(String[][]::new);
    }

    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) throws MeteorologiaException {
        return Arrays.stream(comunaExistente(codigoRegion, codigoComuna).getEstaciones())
                .map(e -> e.toString().split("; "))
                .toArray(String[][]::new);
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
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        return Arrays.stream(estacion.getAlertasBetween(inicio, fin))
                .map(a -> new String[]{
                        a.getFechaHora().format(formatoFecha),
                        a.getFechaHora().format(formatoHora),
                        a.getSensor().getCodigo(),
                        a.getFenomeno(),
                        unDecimal(a.getValor()),
                        a.getSensor().getUnidad(),
                        a.getNivel().name()})
                .toArray(String[][]::new);
    }
    private Optional<Region> findRegion(int codigo) {
        return listaRegion.stream()
                .filter(r -> r.getCodigo() == codigo)
                .findFirst();
    }
    private Optional<Comuna> findComuna(int codRegion, int codComuna) {
        return findRegion(codRegion).flatMap(r -> r.findComunaById(codComuna));
    }

    private Optional<EstacionMeteorologica> findEstacion(String codigo) {
        return listaEstacion.stream()
                .filter(e -> e.getCodigo().equals(codigo))
                .findFirst();
    }
    private Optional<Sensor> findSensor(String codEstacion, String codSensor) {
        return findEstacion(codEstacion).flatMap(e -> e.findSensorById(codSensor));
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
        return findEstacion(codEstacion).orElseThrow(() -> new MeteorologiaException("No existe una estación con el código " + codEstacion + "."));
    }

    private Sensor sensorExistente(String codEstacion, String codSensor) throws MeteorologiaException {
        estacionExistente(codEstacion);
        return findSensor(codEstacion, codSensor).orElseThrow(() ->
                new MeteorologiaException("No existe el sensor " + codSensor + " en la estación " + codEstacion + "."));
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