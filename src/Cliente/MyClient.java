package Cliente;

import Entity.Usuario;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class MyClient extends Thread {

    private int socketPortNumber;
    private String nombreUsuario;

    private PrintStream send;
    private BufferedReader receive;
    private Socket socket;
    private InetAddress address;
    private boolean conectado;
    private boolean logueado;
    private String filename;
    private String nombreEnviar;
    private ArrayList<String> archivosCliente;
    private DataOutputStream sendArchivo;
    private DataInputStream receiveArchivo;

    public MyClient(int socketPortNumber, String ipServidor) throws UnknownHostException, IOException {

        this.archivosCliente = new ArrayList<>();
        this.socketPortNumber = socketPortNumber;
        this.conectado = true;
        this.address = InetAddress.getByName(ipServidor);

        this.socket = new Socket(address, this.socketPortNumber);
        this.send = new PrintStream(socket.getOutputStream());
        this.receive = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        this.sendArchivo = new DataOutputStream(this.socket.getOutputStream());
        this.receiveArchivo = new DataInputStream(this.socket.getInputStream());

        this.logueado = false;
        this.filename = "";

    } // constructor    

    @Override
    public void run() {

        try {

            while (this.conectado) {

                String mensajeServidor = receive.readLine();

                Element element = stringToXML(mensajeServidor);
                mensajeServidor = element.getChild("accion").getValue();
                System.out.println("mensaje del servidor: " + mensajeServidor);
                switch (mensajeServidor) {
                    case "si logueo":
                        this.logueado = true;

                        break;
                    case "no logueo":

                        break;
                    case "verNombres":
                        System.out.println("Voy a recibir: " + element.getChild("nombreAr").getValue());
                        this.archivosCliente.add(element.getChild("nombreAr").getValue());

                        //System.out.println(this.archivosCliente.get(0));
                        break;

                    case "cargarArchivo":

                        String nombreFin = "usuarios\\" + nombreUsuario + "\\" + element.getChild("archivo").getValue();

                        System.out.println("nombre del archivo: " + nombreFin);
                        int lectura;
                        BufferedOutputStream outputFile = new BufferedOutputStream(new FileOutputStream(new File(nombreFin)));

                        byte byteArray[] = new byte[1024];

                        while ((lectura = receiveArchivo.read(byteArray)) != -1) {
                            outputFile.write(byteArray, 0, lectura);
                        }
                        outputFile.close();
                        break;

                    default:
                        break;
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void logearUsuario(Usuario usuario) throws IOException, JDOMException {

        Element elementoActual = generUsuaioXML(usuario);
        Element mAccion = new Element("accion");
        mAccion.addContent("logear");
        elementoActual.addContent(mAccion);
        this.send.println(xmlToString(elementoActual));

    }

    public void escucha() throws IOException {
        System.out.println(leer());
    }

    public String leer() throws IOException {
        return receive.readLine();
    } // leer

    private Element generUsuaioXML(Usuario usuario) {

        Element mUsuario = new Element("Usuario");
        mUsuario.setAttribute("nombre", usuario.getNombre());

        Element mContrasena = new Element("Contrasena");
        mContrasena.addContent(usuario.getContraseña());

        mUsuario.addContent(mContrasena);

        return mUsuario;

    } // generEstudianteXML        

    private String xmlToString(Element element) {
        XMLOutputter output = new XMLOutputter(Format.getCompactFormat());
        String xmlStringElement = output.outputString(element);
        xmlStringElement = xmlStringElement.replace("\n", "");
        return xmlStringElement;
    } // xmlToString 

    private Element stringToXML(String stringMensaje) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        StringReader stringReader = new StringReader(stringMensaje);
        Document doc = saxBuilder.build(stringReader);
        return doc.getRootElement();
    } // stringToXML   

    private Usuario xmlAUsuario(Element elementoActual) {

        Usuario usuarioActual = new Usuario();
        usuarioActual.setNombre(elementoActual.getAttributeValue("nombre"));
        usuarioActual.setContraseña(elementoActual.getChild("Contrasena").getValue());

        return usuarioActual;

    } // xmlAEstudiante

    public PrintStream getSend() {
        return send;
    }

    public void setSend(PrintStream send) {
        this.send = send;
    }

    public BufferedReader getReceive() {
        return receive;
    }

    public void setReceive(BufferedReader receive) {
        this.receive = receive;
    }

    public boolean getLogueado() {
        return logueado;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void enviarArchivo() throws FileNotFoundException, IOException {

        if (!this.filename.equalsIgnoreCase("")) {
            int lectura;

            BufferedInputStream outputFile = new BufferedInputStream(new FileInputStream(new File(this.filename)));

            byte byteArray[] = new byte[1024];

            /* Avisa al servidor que se le enviara un archivo /
            this.send.writeUTF(Utility.AVISOENVIO);

            / Se envia el nombre del archivo */
            Element elementoActual = new Element("MandarArch");
            Element mAccion = new Element("accion");
            mAccion.addContent("cargarArchivo");
            Element mArchivo = new Element("archivo");
            mArchivo.addContent(this.nombreEnviar);
            elementoActual.addContent(mAccion);
            elementoActual.addContent(mArchivo);

            this.send.println(xmlToString(elementoActual));

            while ((lectura = outputFile.read(byteArray)) != -1) {
                this.send.write(byteArray, 0, lectura);
            }

            this.filename = "";
            outputFile.close();
        }
    }

    public void pedirArchivo(String nombre) {
        Element elementoActual = new Element("PedirArch");
        Element mAccion = new Element("accion");
        mAccion.addContent("pedirArchivo");
        Element mArchivo = new Element("archivo");
        mArchivo.addContent(nombre);
        elementoActual.addContent(mAccion);
        elementoActual.addContent(mArchivo);

        this.send.println(xmlToString(elementoActual));
    }

    public String getNombreEnviar() {
        return nombreEnviar;
    }

    public void setNombreEnviar(String nombreEnviar) {
        this.nombreEnviar = nombreEnviar;
    }

    public ArrayList<String> getArchivosCliente() {
        return archivosCliente;
    }

    public void setArchivosCliente(ArrayList<String> archivosCliente) {
        this.archivosCliente = archivosCliente;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

} // fin clase

