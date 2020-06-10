package GUI;

import Cliente.MyClient;
import Entity.Usuario;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.jdom.JDOMException;

public class VentanaDelCliente extends JFrame implements Runnable, ActionListener {

    JFileChooser file;

    JButton buttonBuscarArchivo, buttonEnviarArchivo, buttonVerArchivos;
    JComboBox comboArchivos;

    private String usuario;
    private MyClient myClient;
    private Thread hilo;
    private String crearCombo;
    private boolean ejecutar;
    private String nombreRuta;
    private int pintarArchivos;

    private BufferedImage image;
    private Graphics2D graphics2D;

    public VentanaDelCliente(MyClient cliente, String usuario) throws IOException {
        super("crearUnirPartida");
        
        this.myClient = cliente;
        this.myClient.setNombreUsuario(usuario);
        this.usuario = usuario;
        this.setLayout(null);
        this.setSize(800, 750);
        this.nombreRuta = "";
        this.pintarArchivos = 0;
        init();

        this.crearCombo = "si";
        this.hilo = new Thread(this);
        this.ejecutar = true;
        this.hilo.start();
        paint(graphics2D);

    }

    @Override
    public void run() {
        while (this.ejecutar) {
            //System.out.println("");
            if (this.pintarArchivos < this.myClient.getArchivosCliente().size()) {
                System.out.println("Agregue un archivo");
                this.comboArchivos.addItem(this.myClient.getArchivosCliente().get(this.pintarArchivos) + "\n");
                this.pintarArchivos++;
            }
        }
    }

    public void init() {

        this.buttonBuscarArchivo = new JButton("Escoger archivo");
        this.buttonVerArchivos = new JButton("Traer Archivo");

        this.buttonEnviarArchivo = new JButton("Enviar archivo");

        this.buttonBuscarArchivo.setBounds(10, 50, 136, 30);
        this.buttonVerArchivos.setBounds(500, 100, 150, 30);
        this.buttonEnviarArchivo.setBounds(160, 50, 136, 30);

        this.buttonBuscarArchivo.addActionListener(this);
        this.buttonVerArchivos.addActionListener(this);
        this.buttonEnviarArchivo.addActionListener(this);

        this.add(buttonBuscarArchivo);
        this.add(buttonVerArchivos);
        this.add(buttonEnviarArchivo);

        this.comboArchivos = new JComboBox();
        this.comboArchivos.setBounds(500, 50, 150, 30);
        this.comboArchivos.addActionListener(this);
        this.add(this.comboArchivos);
        this.comboArchivos.addItem("");

    }

    private void drawToScree() throws IOException {

        Graphics g = this.getGraphics();
        this.graphics2D = (Graphics2D) g;
        g.dispose();

    } // drawToScree

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.buttonBuscarArchivo) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(fileChooser);

            this.nombreRuta = fileChooser.getSelectedFile().getAbsolutePath();
            System.out.println(this.nombreRuta);

        }

        if (e.getSource() == this.buttonEnviarArchivo) {

            System.out.println("ruta antes de enviar" + this.nombreRuta);
            if (this.nombreRuta != "") {
                try {
                    File f = new File(this.nombreRuta);
                    System.out.println(f.getName());

                    this.myClient.setFilename(this.nombreRuta);
                    this.myClient.setNombreEnviar(f.getName());
                    this.myClient.enviarArchivo();
                    this.myClient.getArchivosCliente().add(f.getName());
                    JOptionPane.showMessageDialog(null, "Archivo cargado");
                    this.setFocusable(true);
                    this.requestFocus();

                } catch (IOException ex) {
                    Logger.getLogger(VentanaDelCliente.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.nombreRuta = "";
            }
        }

        if (e.getSource() == this.buttonVerArchivos) {

            System.out.println(this.comboArchivos.getSelectedItem().toString());
            this.myClient.pedirArchivo(this.comboArchivos.getSelectedItem().toString());
            JOptionPane.showMessageDialog(null, "El archivo se a guardado en su carpeta de usuario");
        }

    }
}
