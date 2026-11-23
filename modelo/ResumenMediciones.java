package modelo;
/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
23/10/2026
 */

import java.util.Arrays;
import java.util.Comparator;

public class ResumenMediciones {
    private final int cantidad;
    private final float promedio;
    private final Medicion minimo;
    private final Medicion maximo;

     public ResumenMediciones(Medicion[]mediciones){
            if (mediciones == null || mediciones.length == 0) {
                throw new IllegalArgumentException("El arreglo de mediciones no puede estar vacío.");
            }
            Comparator<Medicion> porValor = Comparator.comparing(Medicion::getValor);
            // en caso de empate se conserva la medicion mas antigua, tanto en el minimo como en el maximo
            Comparator<Medicion> masAntiguaPrimero = porValor.thenComparing(Medicion::getFechaHora);
            Comparator<Medicion> masAntiguaUltima = porValor.thenComparing(Comparator.comparing(Medicion::getFechaHora).reversed());
            this.cantidad = mediciones.length;
            this.minimo = Arrays.stream(mediciones).min(masAntiguaPrimero).get();
            this.maximo = Arrays.stream(mediciones).max(masAntiguaUltima).get();
            this.promedio = (float) Arrays.stream(mediciones).mapToDouble(Medicion::getValor).average().orElse(0);
        }
        public int getCantidad () {
            return cantidad;
        }
        public float getPromedio () {
            return promedio;
        }
        public Medicion getMinimo () {
            return minimo;
        }
        public Medicion getMaximo () {
            return maximo;
        }
    }