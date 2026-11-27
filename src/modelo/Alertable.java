package modelo;
/*
::::::::::::::autor Osvaldo Baeza::::::::::::::
23/10/2026
 */
import java.util.Optional;

public interface Alertable {
    Optional<NivelAlerta> evaluaAlerta(float valor);

    String getFenomeno(float valor);
    default boolean generaAlerta(float valor) {
        return evaluaAlerta(valor).isPresent();
    }
}
