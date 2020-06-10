package GUI;

import Cliente.MyClient;
import Entity.Usuario;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.jdom.JDOMException;

public class IniciarJuego extends JFrame implements ActionListener {

    JLabel labelContrasena, labelCliente, labelTitulo;
    JTextField textJugador;
    JPasswordField textContrasena;
    JButton buttonJugar, buttonCargar;
    private MyClient myClient;
    private Usuario usuario;
    private boolean jugadorAceptado;
    private Graphics2D graphics2D;

    public IniciarJuego(MyClient cliente) throws IOException {
        super();
        this.myClient = cliente;
        this.jugadorAceptado = false;
        this.setLayout(null);
        this.setSize(600, 250);
        init();
        paint(graphics2D);
    }

    public void init() {

        this.labelTitulo = new JLabel("Login cliente");
        this.labelCliente = new JLabel("Cliente: ");
        this.labelContrasena = new JLabel("Contraseña: ");
        this.textContrasena = new JPasswordField();
        this.textJugador = new JTextField();
        this.buttonCargar = new JButton("Login");
        this.buttonJugar = new JButton("Conectarse a red");
        this.labelTitulo.setBounds(10, 10, 100, 30);
        this.labelCliente.setBounds(10, 70, 100, 30);
        this.labelContrasena.setBounds(210, 70, 100, 30);
        this.textJugador.setBounds(80, 70, 100, 30);
        this.textContrasena.setBounds(310, 70, 100, 30);
        this.buttonCargar.setBounds(440, 70, 100, 30);
        this.buttonJugar.setBounds(25, 100, 135, 30);
        this.buttonCargar.addActionListener(this);
        this.buttonJugar.addActionListener(this);
        this.add(labelContrasena);
        this.add(labelTitulo);
        this.add(labelCliente);
        this.add(textContrasena);
        this.add(textJugador);
        this.add(buttonCargar);
        this.add(buttonJugar);
        this.buttonJugar.setVisible(false);
    }

    private void drawToScree() throws IOException {
        Graphics g = this.getGraphics();
        this.graphics2D = (Graphics2D) g;
        g.dispose();

    } // drawToScree

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.buttonCargar) {
            try {

                //this.usuario.setNombre(this.textJugador.getText());
                //this.usuario.setContraseña(Utility.MyUtility.obtenerPasswordCifrada(this.textContrasena.getText(), Utility.MyUtility.SHA256));
                this.usuario = new Usuario(this.textJugador.getText(), this.textContrasena.getText());

                this.myClient.logearUsuario(this.usuario);

                Thread.sleep(2000);

                this.jugadorAceptado = myClient.getLogueado();
                //System.out.println(jugadorAceptado + "  interfaz boleano");

                if (this.jugadorAceptado == true) {

                    System.out.println("el cliente se logueo con exito");

                    File directorio = new File("usuarios\\" + this.usuario.getNombre());
                    if (!directorio.exists()) {
                        if (directorio.mkdirs()) {
                            System.out.println("Directorio creado");
                        } else {
                            System.out.println("Error al crear directorio");
                        }
                    }

                    dispose();
                    VentanaDelCliente ventana;
                    ventana = new VentanaDelCliente(this.myClient, this.usuario.getNombre());
                    ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    ventana.setVisible(true);
                    ventana.setLocationRelativeTo(null);
                    ventana.setResizable(false);

                }

            } catch (IOException ex) {
                Logger.getLogger(IniciarJuego.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JDOMException ex) {
                Logger.getLogger(IniciarJuego.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(IniciarJuego.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
