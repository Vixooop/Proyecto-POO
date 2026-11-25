// Autor Vicente Navarrete //
// 19/11/2026 //

package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.time.LocalDateTime;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class VentanaRegistrarMedicion extends JDialog {
    private final JComboBox<String> cmbEstaciones = new JComboBox<String>();
    private final JComboBox<String> cmbSensores = new JComboBox<String>();
    private final JLabel lblTipo = new JLabel("-");
    private final JLabel lblUnidad = new JLabel(" ");
    private final JTextField txtFechaHora = new JTextField(14);
    private final JTextField txtValor = new JTextField(14);

    public VentanaRegistrarMedicion(JFrame owner) {
        super(owner, "Registrar medición", true);
        VentanaPrincipal.llenaCombo(cmbEstaciones, InstitutoMeteorologia.getInstance().getCodigosEstaciones());

        JPanel fechaHora = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        fechaHora.add(txtFechaHora);
        fechaHora.add(new JLabel("dd/MM/yyyy HH:mm"));
        JPanel valor = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        valor.add(txtValor);
        valor.add(lblUnidad);

        JPanel formulario = new JPanel(new GridBagLayout());
        VentanaPrincipal.agregaFila(formulario, 0, "Estación:", cmbEstaciones);
        VentanaPrincipal.agregaFila(formulario, 1, "Sensor:", cmbSensores);
        VentanaPrincipal.agregaFila(formulario, 2, "Tipo de sensor:", lblTipo);
        VentanaPrincipal.agregaFila(formulario, 3, "Fecha y hora:", fechaHora);
        VentanaPrincipal.agregaFila(formulario, 4, "Valor:", valor);

        JButton btnRegistrar = new JButton("Registrar");
        JButton btnCancelar = new JButton("Cancelar");
        btnRegistrar.addActionListener(e -> registrar());
        btnCancelar.addActionListener(e -> dispose());
        cmbEstaciones.addActionListener(e -> actualizaSensores());
        cmbSensores.addActionListener(e -> actualizaDatosSensor());
        actualizaSensores();

        JPanel contenido = new JPanel(new BorderLayout(0, 6));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        contenido.add(formulario, BorderLayout.CENTER);
        contenido.add(VentanaPrincipal.panelBotones(btnRegistrar, btnCancelar), BorderLayout.SOUTH);
        setContentPane(contenido);
        VentanaPrincipal.configuraDialogo(this, owner, btnRegistrar);
    }

    private void actualizaSensores() {
        VentanaPrincipal.llenaSensores(this, cmbEstaciones, cmbSensores);
        actualizaDatosSensor();
    }

    private void actualizaDatosSensor() {
        String sensor = (String) cmbSensores.getSelectedItem();
        if (sensor == null) {
            lblTipo.setText("-");
            lblUnidad.setText(" ");
            return;
        }
        try {
            String[] datos = InstitutoMeteorologia.getInstance().getDatosSensor((String) cmbEstaciones.getSelectedItem(), sensor);
            lblTipo.setText(datos[0]);
            lblUnidad.setText(datos[1]);
        } catch (MeteorologiaException e) {
            VentanaPrincipal.mostrarError(this, e.getMessage());
        }
    }

    private void registrar() {
        String estacion = (String) cmbEstaciones.getSelectedItem();
        String sensor = (String) cmbSensores.getSelectedItem();
        if (sensor == null) {
            VentanaPrincipal.mostrarInformacion(this, "Registrar medición", "La estación " + estacion + " no tiene sensores instalados.");
            return;
        }
        LocalDateTime fechaHora = VentanaPrincipal.parseaFechaHora(this, txtFechaHora.getText());
        if (fechaHora == null) {
            return;
        }
        Float valor = leeValor();
        if (valor == null) {
            return;
        }
        try {
            InstitutoMeteorologia.getInstance().registraMedicion(fechaHora, valor, estacion, sensor);
            VentanaPrincipal.mostrarInformacion(this, "Registrar medición", "Medición registrada correctamente.");
            txtValor.setText("");
            txtValor.requestFocusInWindow();
        } catch (MeteorologiaException e) {
            VentanaPrincipal.mostrarError(this, e.getMessage());
        }
    }

    private Float leeValor() {
        try {
            float valor = Float.parseFloat(txtValor.getText().trim().replace(',', '.'));
            if (!Float.isNaN(valor) && !Float.isInfinite(valor)) {
                return valor;
            }
        } catch (NumberFormatException e) {
        }
        VentanaPrincipal.mostrarError(this, "Debe ingresar un número.");
        return null;
    }
}