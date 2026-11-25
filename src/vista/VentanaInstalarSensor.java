// Autor Vicente Navarrete //
// 21/11/2026 //

package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import modelo.TipoSensor;

public class VentanaInstalarSensor extends JDialog {
    private final JComboBox<String> cmbEstaciones = new JComboBox<String>();
    private final JTextField txtCodigo = new JTextField(15);
    private final JTextField txtMarca = new JTextField(15);
    private final JTextField txtModelo = new JTextField(15);
    private final ButtonGroup grupoTipos = new ButtonGroup();

    public VentanaInstalarSensor(JFrame owner) {
        super(owner, "Instalar sensor", true);
        VentanaPrincipal.llenaCombo(cmbEstaciones, InstitutoMeteorologia.getInstance().getCodigosEstaciones());

        JPanel formulario = new JPanel(new GridBagLayout());
        VentanaPrincipal.agregaFila(formulario, 0, "Estación:", cmbEstaciones);
        VentanaPrincipal.agregaFila(formulario, 1, "Código del sensor:", txtCodigo);
        VentanaPrincipal.agregaFila(formulario, 2, "Marca:", txtMarca);
        VentanaPrincipal.agregaFila(formulario, 3, "Modelo:", txtModelo);

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

        JPanel contenido = new JPanel(new BorderLayout(0, 6));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        contenido.add(formulario, BorderLayout.NORTH);
        contenido.add(tipos, BorderLayout.CENTER);
        contenido.add(VentanaPrincipal.panelBotones(btnInstalar, btnCancelar), BorderLayout.SOUTH);
        setContentPane(contenido);
        VentanaPrincipal.configuraDialogo(this, owner, btnInstalar);
    }

    private void instalar() {
        String codigo = txtCodigo.getText().trim();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        if (codigo.isEmpty() || marca.isEmpty() || modelo.isEmpty()) {
            VentanaPrincipal.mostrarError(this, "El código, la marca y el modelo estan vacíos.");
            return;
        }
        String estacion = (String) cmbEstaciones.getSelectedItem();
        TipoSensor tipo = TipoSensor.valueOf(grupoTipos.getSelection().getActionCommand());
        try {
            InstitutoMeteorologia.getInstance().instalaSensor(codigo, marca, modelo, tipo, estacion);
            VentanaPrincipal.mostrarInformacion(this, "Instalar sensor", "Sensor " + codigo + " instalado en la estación " + estacion + ".");
            txtCodigo.setText("");
            txtMarca.setText("");
            txtModelo.setText("");
            txtCodigo.requestFocusInWindow();
        } catch (MeteorologiaException e) {
            VentanaPrincipal.mostrarError(this, e.getMessage());
        }
    }
}