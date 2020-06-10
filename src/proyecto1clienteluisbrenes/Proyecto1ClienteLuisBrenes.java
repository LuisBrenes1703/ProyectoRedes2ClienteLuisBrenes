package proyecto1clienteluisbrenes;

import GUI.VentanaPrincipal;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Proyecto1ClienteLuisBrenes {

    public static void main(String[] args) throws IOException {
        String ipServidor = "";
       
        
        ipServidor = JOptionPane.showInputDialog("Digite la ip del servidor");
        
        if (ipServidor != "") {
            JFrame jFrame = new JFrame("Batalla de castillo");
            jFrame.add(new VentanaPrincipal("192.168.1.3"));
//            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
//            jFrame.setDefaultCloseOperation(0);
            jFrame.pack();
            jFrame.setLocationRelativeTo(null);
            jFrame.setResizable(false);
            jFrame.setVisible(true);
        }else{
            JOptionPane.showMessageDialog(null, "Es necesario una ip");
            
        }
    }

}
