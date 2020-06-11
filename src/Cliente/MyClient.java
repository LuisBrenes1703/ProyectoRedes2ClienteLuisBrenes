package Cliente;

import Entity.Usuario;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;

public class MyClient extends Thread {

    private int socketPortNumber;
    private String nombreUsuario;

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
        this.nombreUsuario = "";
        this.socket = new Socket(address, this.socketPortNumber);

        this.sendArchivo = new DataOutputStream(this.socket.getOutputStream());
        this.receiveArchivo = new DataInputStream(this.socket.getInputStream());

        this.logueado = false;
        this.filename = "";

    } // constructor    

    @Override
    public void run() {

        //try {
        while (this.conectado) {
            /*
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
             */
        }

    }

    public void logearUsuario(Usuario usuario) throws IOException, JDOMException {

        this.sendArchivo.writeUTF("logear");
        this.sendArchivo.writeUTF(usuario.getNombre());
        this.sendArchivo.writeUTF(usuario.getContraseña());

        System.out.println("recibe");
        String validar = this.receiveArchivo.readUTF();
        System.out.println("validar: " + validar);

        if (validar.equals("si logueo")) {
            System.out.println("login");
            this.logueado = true;
        }

        int variable = this.receiveArchivo.readInt();
        if (variable != 0) {
            for (int i = 0; i < variable; i++) {
                this.archivosCliente.add(this.receiveArchivo.readUTF());
            }
        }

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

    public void pedirArchivo(String nombre) throws IOException {

        this.sendArchivo.writeUTF("pedirArchivo");
        this.sendArchivo.writeUTF(nombre);

        //identificarse();
        //this.send.writeUTF(Utility.AVISODESCARGA);
        //this.send.writeUTF(this.filename);
        //String mensaje = this.receive.readUTF();
        byte readbytes[] = new byte[1024];
        InputStream in = this.socket.getInputStream();
        System.out.println("FILE: " + nombre);
        //"usuarios" + "\\" + this.usuarioAtentiendose.getNombre() + "\\" + filename

        try (OutputStream file = Files.newOutputStream(Paths.get("usuarios\\" + this.nombreUsuario.trim() + "\\" + nombre.trim()))) {
            System.out.println("FILE: jhavsgvasvu ");
            for (int read = -1; (read = in.read(readbytes)) >= 0;) {
                //System.out.println("FILE: " + nombre);
                file.write(readbytes, 0, read);
                if (read < 1024) {
                    break;
                }
            }
            System.out.println("SALI SAJSAS");
            file.flush();
            file.close();
        }
        System.out.println("sali del ciclo");

        this.receiveArchivo.close();
        this.socket = new Socket(this.address, 5025);
        this.receiveArchivo = new DataInputStream(this.socket.getInputStream());
        this.sendArchivo = new DataOutputStream(this.socket.getOutputStream());
        in.close();

        try {
            sleep(200);
            this.sendArchivo.writeUTF("quiensoy");
            this.sendArchivo.writeUTF("Luis");
        } catch (InterruptedException ex) {
            Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        // this.hiloSalir = false;
    }

    public void enviarArchivo() throws FileNotFoundException, IOException {

        this.sendArchivo.writeUTF("cargarArchivo");

        if (!this.nombreEnviar.equalsIgnoreCase("")) {

            /* Avisa al servidor que se le enviara un archivo /
            this.send.writeUTF(Utility.AVISOENVIO);

            / Se envia el nombre del archivo */
            this.sendArchivo.writeUTF(this.nombreEnviar);

            byte byteArray[] = null;
            byteArray = Files.readAllBytes(Paths.get(this.filename));
            this.sendArchivo.write(byteArray);
            this.sendArchivo.flush();

            this.sendArchivo.close();
            this.socket = new Socket(this.address, 5025);
            this.sendArchivo = new DataOutputStream(this.socket.getOutputStream());
            try {
                // this.accion = Utility.IDENTIFICAR;
                sleep(200);
                this.sendArchivo.writeUTF("quiensoy");
                this.sendArchivo.writeUTF("Luis");
            } catch (InterruptedException ex) {
                Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
            }

            this.filename = "";
        }
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

