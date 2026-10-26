package src.modelo;
/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
23/10/2026
 */
public class ResumenMediciones {
    private final int cantidad;
    private final float promedio;
    private final Medicion minimo;
    private final Medicion maximo;

    public ResumenMediciones(Medicion[] mediciones) {
        if (mediciones == null || mediciones.length == 0) {
            throw new IllegalArgumentException("El arreglo de mediciones no puede estar vacío.");
        }
        Medicion min = mediciones[0];
        Medicion max = mediciones[0];
        double suma = 0;
        for (Medicion m : mediciones) {
            suma += m.getValor();
            if (m.getValor() < min.getValor() || (m.getValor() == min.getValor() && m.getFechaHora().isBefore(min.getFechaHora()))) {
                min = m;
            }
            if (m.getValor() > max.getValor() || (m.getValor() == max.getValor() && m.getFechaHora().isBefore(max.getFechaHora()))) {
                max = m;
            }
        }
        this.cantidad = mediciones.length;
        this.promedio = (float) (suma / mediciones.length);
        this.minimo = min;
        this.maximo = max;
    }
    public int getCantidad() {
        return cantidad;
    }
    public float getPromedio() {
        return promedio;
    }
    public Medicion getMinimo() {
        return minimo;
    }
    public Medicion getMaximo() {
        return maximo;
    }
}