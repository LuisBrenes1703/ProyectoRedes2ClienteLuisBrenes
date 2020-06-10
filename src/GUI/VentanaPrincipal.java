package GUI;

import Cliente.MyClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import org.jdom.JDOMException;

public class VentanaPrincipal extends JFrame implements ActionListener {

    int contElementos;
    private String ipServidor;
    JMenuBar barraMenu;
    JMenu menu;
    JMenuItem itemMenuRegistar;
    JMenuItem itemMenuIniciar;
    JMenuItem itemMenuBuscar;
//    JMenuItem itemTablero;

    JMenuItem itemMenuSalir;
    JDesktopPane desktopPane;
    JSeparator separador;

//    private JTextField jtfPeso;
    private MyClient myClient;

    public VentanaPrincipal(String ipServidor) throws IOException {

        super();
        this.ipServidor = ipServidor;
        this.setSize(1200, 1000);
        this.setLayout(null);

        init();
        this.myClient = new MyClient(Utility.MyUtility.SOCKETPORTNUMBER, ipServidor);
        this.myClient.start();
    }

    public void init() {
        this.barraMenu = new JMenuBar();
        this.desktopPane = new JDesktopPane();

        this.barraMenu.setBounds(0, 0, 60, 30);

        this.menu = new JMenu("Menu");

        this.menu.setBounds(0, 0, 100, 30);
        this.itemMenuRegistar = new JMenuItem("Registar jugador");
        this.itemMenuIniciar = new JMenuItem("Cargar Jugador");

        this.itemMenuSalir = new JMenuItem("Salir");
        this.separador = new JSeparator();

        this.menu.add(itemMenuRegistar);
        this.menu.add(itemMenuIniciar);

        this.itemMenuRegistar.addActionListener(this);
        this.itemMenuIniciar.addActionListener(this);

        this.menu.add(separador);
        this.menu.add(itemMenuSalir);

        this.barraMenu.add(menu);
        this.desktopPane.setBounds(0, 30, 1200, 1000);
        this.add(this.barraMenu);
        this.add(this.desktopPane);
        this.setVisible(true);
//        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);

    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == this.itemMenuIniciar) {

            IniciarJuego ventana;
            try {
                dispose();
                ventana = new IniciarJuego(this.myClient);
                ventana.setVisible(true);
//                ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                ventana.setLocationRelativeTo(null);
                ventana.setResizable(false);
            } catch (IOException ex) {
                Logger.getLogger(VentanaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        if (e.getSource() == this.itemMenuRegistar) {
            
        }

    }
}
