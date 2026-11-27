// Autor Vicente Navarrete //
// 19/11/2026 //

package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class VentanaRegistrarMedicion extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final String PATRON_FECHA_HORA = "dd/MM/yyyy HH:mm";
    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private final InstitutoMeteorologia instituto = InstitutoMeteorologia.getInstance();
    private final JComboBox<String> cmbEstaciones = new JComboBox<String>(instituto.getCodigosEstaciones());
    private final JComboBox<String> cmbSensores = new JComboBox<String>();
    private final JLabel lblTipo = new JLabel("-");
    private final JLabel lblUnidad = new JLabel(" ");
    private final JTextField txtFechaHora = new JTextField(14);
    private final JTextField txtValor = new JTextField(14);

    public VentanaRegistrarMedicion(JFrame owner) {
        super(owner, "Registrar medición", true);

        JPanel fechaHora = new JPanel(new BorderLayout(6, 0));
        fechaHora.add(txtFechaHora, BorderLayout.CENTER);
        fechaHora.add(new JLabel(PATRON_FECHA_HORA), BorderLayout.EAST);
        JPanel valor = new JPanel(new BorderLayout(6, 0));
        valor.add(txtValor, BorderLayout.CENTER);
        valor.add(lblUnidad, BorderLayout.EAST);

        JPanel etiquetas = new JPanel(new GridLayout(0, 1, 0, 6));
        JPanel campos = new JPanel(new GridLayout(0, 1, 0, 6));
        etiquetas.add(new JLabel("Estación:", SwingConstants.RIGHT));
        campos.add(cmbEstaciones);
        etiquetas.add(new JLabel("Sensor:", SwingConstants.RIGHT));
        campos.add(cmbSensores);
        etiquetas.add(new JLabel("Tipo de sensor:", SwingConstants.RIGHT));
        campos.add(lblTipo);
        etiquetas.add(new JLabel("Fecha y hora:", SwingConstants.RIGHT));
        campos.add(fechaHora);
        etiquetas.add(new JLabel("Valor:", SwingConstants.RIGHT));
        campos.add(valor);
        JPanel formulario = new JPanel(new BorderLayout(6, 0));
        formulario.add(etiquetas, BorderLayout.WEST);
        formulario.add(campos, BorderLayout.CENTER);

        JButton btnRegistrar = new JButton("Registrar");
        JButton btnCancelar = new JButton("Cancelar");
        btnRegistrar.addActionListener(e -> registrar());
        btnCancelar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        botones.add(btnRegistrar);
        botones.add(btnCancelar);

        cmbEstaciones.addActionListener(e -> actualizaSensores());
        cmbSensores.addActionListener(e -> actualizaDatosSensor());
        actualizaSensores();

        JPanel contenido = new JPanel(new BorderLayout(0, 6));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        contenido.add(formulario, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().setDefaultButton(btnRegistrar);
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
            String[] datos = instituto.getDatosSensor((String) cmbEstaciones.getSelectedItem(), sensor);
            lblTipo.setText(datos[0]);
            lblUnidad.setText(datos[1]);
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void registrar() {
        String estacion = (String) cmbEstaciones.getSelectedItem();
        String sensor = (String) cmbSensores.getSelectedItem();
        if (sensor == null) {
            mostrarInformacion("La estación " + estacion + " no tiene sensores.");
            return;
        }
        LocalDateTime fechaHora = leeFechaHora();
        if (fechaHora == null) {
            return;
        }
        Float valor = leeValor();
        if (valor == null) {
            return;
        }
        try {
            instituto.registraMedicion(fechaHora, valor, estacion, sensor);
            mostrarInformacion("Medición registrada correctamente.");
            txtValor.setText("");
            txtValor.requestFocusInWindow();
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private LocalDateTime leeFechaHora() {
        try {
            return LocalDateTime.parse(txtFechaHora.getText().trim(), FORMATO_FECHA_HORA);
        } catch (DateTimeParseException e) {
            mostrarError("La fecha y hora debe tener el formato " + PATRON_FECHA_HORA + ".");
            return null;
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
        mostrarError("Debe ingresar un número.");
        return null;
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarInformacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, getTitle(), JOptionPane.INFORMATION_MESSAGE);
    }
}