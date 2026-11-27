// Autor Vicente Navarrete //
// 21/11/2026 //

package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import modelo.TipoSensor;

public class VentanaInstalarSensor extends JDialog {
    private static final long serialVersionUID = 1L;

    private final InstitutoMeteorologia instituto = InstitutoMeteorologia.getInstance();
    private final JComboBox<String> cmbEstaciones = new JComboBox<String>(instituto.getCodigosEstaciones());
    private final JTextField txtCodigo = new JTextField(15);
    private final JTextField txtMarca = new JTextField(15);
    private final JTextField txtModelo = new JTextField(15);
    private final ButtonGroup grupoTipos = new ButtonGroup();

    public VentanaInstalarSensor(JFrame owner) {
        super(owner, "Instalar sensor", true);

        JPanel etiquetas = new JPanel(new GridLayout(0, 1, 0, 6));
        JPanel campos = new JPanel(new GridLayout(0, 1, 0, 6));
        etiquetas.add(new JLabel("Estación:", SwingConstants.RIGHT));
        campos.add(cmbEstaciones);
        etiquetas.add(new JLabel("Código del sensor:", SwingConstants.RIGHT));
        campos.add(txtCodigo);
        etiquetas.add(new JLabel("Marca:", SwingConstants.RIGHT));
        campos.add(txtMarca);
        etiquetas.add(new JLabel("Modelo:", SwingConstants.RIGHT));
        campos.add(txtModelo);
        JPanel formulario = new JPanel(new BorderLayout(6, 0));
        formulario.add(etiquetas, BorderLayout.WEST);
        formulario.add(campos, BorderLayout.CENTER);

        JPanel tipos = new JPanel(new GridLayout(2, 3, 8, 2));
        tipos.setBorder(BorderFactory.createTitledBorder("Tipo de sensor"));
        for (TipoSensor tipo : TipoSensor.values()) {
            JRadioButton radio = new JRadioButton(tipo.name());
            radio.setActionCommand(tipo.name());
            radio.setSelected(grupoTipos.getButtonCount() == 0);
            grupoTipos.add(radio);
            tipos.add(radio);
        }

        JButton btnInstalar = new JButton("Instalar");
        JButton btnCancelar = new JButton("Cancelar");
        btnInstalar.addActionListener(e -> instalar());
        btnCancelar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        botones.add(btnInstalar);
        botones.add(btnCancelar);

        JPanel contenido = new JPanel(new BorderLayout(0, 6));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        contenido.add(formulario, BorderLayout.NORTH);
        contenido.add(tipos, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().setDefaultButton(btnInstalar);
        pack();
        setLocationRelativeTo(owner);
    }

    private void instalar() {
        String codigo = txtCodigo.getText().trim();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        if (codigo.isEmpty() || marca.isEmpty() || modelo.isEmpty()) {
            mostrarError("El código, la marca y el modelo no pueden estar vacíos.");
            return;
        }
        String estacion = (String) cmbEstaciones.getSelectedItem();
        TipoSensor tipo = TipoSensor.valueOf(grupoTipos.getSelection().getActionCommand());
        try {
            instituto.instalaSensor(codigo, marca, modelo, tipo, estacion);
            JOptionPane.showMessageDialog(this, "Sensor " + codigo + " instalado en la estación " + estacion + ".",
                    getTitle(), JOptionPane.INFORMATION_MESSAGE);
            txtCodigo.setText("");
            txtMarca.setText("");
            txtModelo.setText("");
            txtCodigo.requestFocusInWindow();
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}