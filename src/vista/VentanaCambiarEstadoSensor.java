// Autor Vicente Navarrete //
// 21/11-2026 //

package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import modelo.Estado;

public class VentanaCambiarEstadoSensor extends JDialog {
    private static final long serialVersionUID = 1L;

    private final InstitutoMeteorologia instituto = InstitutoMeteorologia.getInstance();
    private final JComboBox<String> cmbEstaciones = new JComboBox<String>(instituto.getCodigosEstaciones());
    private final JComboBox<String> cmbSensores = new JComboBox<String>();
    private final JLabel lblEstadoActual = new JLabel("-");
    private final ButtonGroup grupoEstados = new ButtonGroup();

    public VentanaCambiarEstadoSensor(JFrame owner) {
        super(owner, "Cambiar estado de sensor", true);

        JPanel estados = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        for (Estado estado : Estado.values()) {
            JRadioButton radio = new JRadioButton(estado.name());
            radio.setActionCommand(estado.name());
            radio.setSelected(grupoEstados.getButtonCount() == 0);
            grupoEstados.add(radio);
            estados.add(radio);
        }

        JPanel etiquetas = new JPanel(new GridLayout(0, 1, 0, 6));
        JPanel campos = new JPanel(new GridLayout(0, 1, 0, 6));
        etiquetas.add(new JLabel("Estación:", SwingConstants.RIGHT));
        campos.add(cmbEstaciones);
        etiquetas.add(new JLabel("Sensor:", SwingConstants.RIGHT));
        campos.add(cmbSensores);
        etiquetas.add(new JLabel("Estado actual:", SwingConstants.RIGHT));
        campos.add(lblEstadoActual);
        etiquetas.add(new JLabel("Nuevo estado:", SwingConstants.RIGHT));
        campos.add(estados);
        JPanel formulario = new JPanel(new BorderLayout(6, 0));
        formulario.add(etiquetas, BorderLayout.WEST);
        formulario.add(campos, BorderLayout.CENTER);

        JButton btnCambiar = new JButton("Cambiar estado");
        JButton btnCancelar = new JButton("Cancelar");
        btnCambiar.addActionListener(e -> cambiarEstado());
        btnCancelar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        botones.add(btnCambiar);
        botones.add(btnCancelar);

        cmbEstaciones.addActionListener(e -> actualizaSensores());
        cmbSensores.addActionListener(e -> actualizaEstadoActual());
        actualizaSensores();

        JPanel contenido = new JPanel(new BorderLayout(0, 6));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        contenido.add(formulario, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().setDefaultButton(btnCambiar);
        pack();
        setLocationRelativeTo(owner);
    }

    private void actualizaSensores() {
        String[] codigos = new String[0];
        try {
            codigos = instituto.getCodigosSensores((String) cmbEstaciones.getSelectedItem());
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
        cmbSensores.setModel(new DefaultComboBoxModel<String>(codigos));
        actualizaEstadoActual();
    }

    private void actualizaEstadoActual() {
        String sensor = (String) cmbSensores.getSelectedItem();
        if (sensor == null) {
            lblEstadoActual.setText("-");
            return;
        }
        try {
            lblEstadoActual.setText(instituto.getDatosSensor((String) cmbEstaciones.getSelectedItem(), sensor)[2]);
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void cambiarEstado() {
        String estacion = (String) cmbEstaciones.getSelectedItem();
        String sensor = (String) cmbSensores.getSelectedItem();
        if (sensor == null) {
            mostrarInformacion("La estación " + estacion + " no tiene sensores.");
            return;
        }
        Estado nuevo = Estado.valueOf(grupoEstados.getSelection().getActionCommand());
        try {
            instituto.cambiaEstadoSensor(estacion, sensor, nuevo);
            mostrarInformacion("Estado del sensor " + sensor + " actualizado a " + nuevo + ".");
            actualizaEstadoActual();
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarInformacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, getTitle(), JOptionPane.INFORMATION_MESSAGE);
    }
}