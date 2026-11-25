// Autor Vicente Navarrete //
// 21/11/2026 //

package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import modelo.Estado;

public class VentanaCambiarEstadoSensor extends JDialog {
    private final JComboBox<String> cmbEstaciones = new JComboBox<String>();
    private final JComboBox<String> cmbSensores = new JComboBox<String>();
    private final JLabel lblEstadoActual = new JLabel("-");
    private final ButtonGroup grupoEstados = new ButtonGroup();

    public VentanaCambiarEstadoSensor(JFrame owner) {
        super(owner, "Cambiar estado de sensor", true);
        VentanaPrincipal.llenaCombo(cmbEstaciones, InstitutoMeteorologia.getInstance().getCodigosEstaciones());

        JPanel estados = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        for (Estado estado : Estado.values()) {
            JRadioButton radio = new JRadioButton(estado.name());
            radio.setActionCommand(estado.name());
            radio.setSelected(grupoEstados.getButtonCount() == 0);
            grupoEstados.add(radio);
            estados.add(radio);
        }

        JPanel formulario = new JPanel(new GridBagLayout());
        VentanaPrincipal.agregaFila(formulario, 0, "Estación:", cmbEstaciones);
        VentanaPrincipal.agregaFila(formulario, 1, "Sensor:", cmbSensores);
        VentanaPrincipal.agregaFila(formulario, 2, "Estado actual:", lblEstadoActual);
        VentanaPrincipal.agregaFila(formulario, 3, "Nuevo estado:", estados);

        JButton btnCambiar = new JButton("Cambiar estado");
        JButton btnCancelar = new JButton("Cancelar");
        btnCambiar.addActionListener(e -> cambiarEstado());
        btnCancelar.addActionListener(e -> dispose());

        cmbEstaciones.addActionListener(e -> actualizaSensores());
        cmbSensores.addActionListener(e -> actualizaEstadoActual());
        actualizaSensores();

        JPanel contenido = new JPanel(new BorderLayout(0, 6));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        contenido.add(formulario, BorderLayout.CENTER);
        contenido.add(VentanaPrincipal.panelBotones(btnCambiar, btnCancelar), BorderLayout.SOUTH);
        setContentPane(contenido);
        VentanaPrincipal.configuraDialogo(this, owner, btnCambiar);
    }

    private void actualizaSensores() {
        VentanaPrincipal.llenaSensores(this, cmbEstaciones, cmbSensores);
        actualizaEstadoActual();
    }

    private void actualizaEstadoActual() {
        String sensor = (String) cmbSensores.getSelectedItem();
        if (sensor == null) {
            lblEstadoActual.setText("-");
            return;
        }
        try {
            lblEstadoActual.setText(InstitutoMeteorologia.getInstance().getDatosSensor((String) cmbEstaciones.getSelectedItem(), sensor)[2]);
        } catch (MeteorologiaException e) {
            VentanaPrincipal.mostrarError(this, e.getMessage());
        }
    }

    private void cambiarEstado() {
        String estacion = (String) cmbEstaciones.getSelectedItem();
        String sensor = (String) cmbSensores.getSelectedItem();
        if (sensor == null) {
            VentanaPrincipal.mostrarInformacion(this, "Cambiar estado de sensor", "La estación " + estacion + " no tiene sensores instalados.");
            return;
        }
        Estado nuevo = Estado.valueOf(grupoEstados.getSelection().getActionCommand());
        try {
            InstitutoMeteorologia.getInstance().cambiaEstadoSensor(estacion, sensor, nuevo);
            VentanaPrincipal.mostrarInformacion(this, "Cambiar estado de sensor", "Estado del sensor " + sensor + " actualizado a " + nuevo + ".");
            actualizaEstadoActual();
        } catch (MeteorologiaException e) {
            VentanaPrincipal.mostrarError(this, e.getMessage());
        }
    }
}