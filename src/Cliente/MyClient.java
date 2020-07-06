package Cliente;

import Entity.Usuario;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
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
import javax.net.ssl.SSLSocketFactory;
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
    private ArrayList<String> archivosServer;
    private DataOutputStream sendArchivo;
    private DataInputStream receiveArchivo;
    private boolean comenzarTransferencia;

    public MyClient(int socketPortNumber, String ipServidor) throws UnknownHostException, IOException {

        this.archivosCliente = new ArrayList<>();
        this.archivosServer = new ArrayList<>();
        this.socketPortNumber = socketPortNumber;

        this.comenzarTransferencia = false;
        this.conectado = true;
        this.address = InetAddress.getByName(ipServidor);
        this.nombreUsuario = "";
        //this.socket = new Socket(address, this.socketPortNumber);
        configurar();
        this.sendArchivo = new DataOutputStream(this.socket.getOutputStream());
        this.receiveArchivo = new DataInputStream(this.socket.getInputStream());

        this.logueado = false;
        this.filename = "";
        
        

    } // constructor    
    
    
    private void configurar() throws IOException {
        System.setProperty("javax.net.ssl.keyStore", "certs\\serverKey.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "112358");
        System.setProperty("javax.net.ssl.trustStore", "certs\\clientTrustedCerts.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "112358");

        SSLSocketFactory clientFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        this.socket = clientFactory.createSocket(this.address, this.socketPortNumber);
    }

    @Override
    public void run() {

        while (this.conectado) {
            System.out.println("estoy en el while");
            if (this.comenzarTransferencia == true) {

                try {

                    System.out.println("entre al if");
                    this.sendArchivo.writeUTF("archivosExistentes");

                    int variable = this.receiveArchivo.readInt();
                    System.out.println("luego del if   " + variable);
                   
                        for (int i = 0; i < variable; i++) {
                            this.archivosServer.add(this.receiveArchivo.readUTF());
                        }

                        System.out.println("obtuve los archivos en el server");
                        listarArchivosCliente();
                        System.out.println("luego del if   " + this.archivosCliente.size());
                        System.out.println("liste los archivos del cliente");

                        if (this.archivosCliente.size() < this.archivosServer.size()) {

                            boolean archivoEnviar = false;

                            for (int i = 0; i < this.archivosServer.size(); i++) {
                                for (int j = 0; j < this.archivosCliente.size(); j++) {
                                    if (this.archivosServer.get(i) == this.archivosCliente.get(j)) {
                                        archivoEnviar = true;
                                    }
                                }
                                if (archivoEnviar == false) {
                                    //this.sendArchivo.writeUTF(this.archivosServer.get(i));
                                    pedirArchivo(this.archivosServer.get(i));

                                } else {
                                    archivoEnviar = false;
                                }
                            }

                        } else if (this.archivosCliente.size() > this.archivosServer.size()) {
                            boolean archivoEnviar = false;

                            for (int i = 0; i < this.archivosCliente.size(); i++) {
                                for (int j = 0; j < this.archivosServer.size(); j++) {
                                    if (this.archivosCliente.get(i) == this.archivosServer.get(j)) {
                                        archivoEnviar = true;
                                    }
                                }
                                if (archivoEnviar == false) {
                                    //this.sendArchivo.writeUTF(this.archivosServer.get(i));
                                    System.out.println("Nombre del archivo" + this.archivosCliente.get(i));
                                     String nombreFin = "usuarios\\" + this.nombreUsuario + "\\"+this.archivosCliente.get(i);
                                    File f = new File(nombreFin);
                                    System.out.println(f.getName());
                                    System.out.println(f.getAbsolutePath());

                                    setFilename(f.getAbsolutePath());
                                    setNombreEnviar(f.getName());
                                    enviarArchivo();

                                } else {
                                    archivoEnviar = false;
                                }
                            }
                        } else {
                            System.out.println("Ambos contienen todos archivos");
                        }
                    

                    this.archivosCliente.clear();
                    this.archivosServer.clear();
                    //sleep(4000);

                } catch (IOException ex) {
                    Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            try {
                sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public void listarArchivosCliente() throws IOException {

        String nombreFin = "usuarios\\" + this.nombreUsuario;
        File carpeta = new File(nombreFin);
        String[] listado = carpeta.list();
        if (listado == null || listado.length == 0) {
            //this.sendArchivo.writeInt(listado.length);
            System.out.println("No hay elementos dentro de la carpeta actual");
        } else {
            //this.sendArchivo.writeInt(listado.length);
            for (int i = 0; i < listado.length; i++) {
                this.archivosCliente.add(listado[i]);
            }
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
            this.comenzarTransferencia = true;
        }

        /*
        int variable = this.receiveArchivo.readInt();
        if (variable != 0) {
            for (int i = 0; i < variable; i++) {
                this.archivosCliente.add(this.receiveArchivo.readUTF());
            }
        }
         */
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
        //this.socket = new Socket(this.address, 5025);
        configurar();
        this.receiveArchivo = new DataInputStream(this.socket.getInputStream());
        this.sendArchivo = new DataOutputStream(this.socket.getOutputStream());
        in.close();

        try {
            sleep(200);
            this.sendArchivo.writeUTF("quiensoy");
            this.sendArchivo.writeUTF(this.nombreUsuario);
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
            //this.socket = new Socket(this.address, 5025);
            configurar();
            this.sendArchivo = new DataOutputStream(this.socket.getOutputStream());
            this.receiveArchivo = new DataInputStream(this.socket.getInputStream());
            try {
                // this.accion = Utility.IDENTIFICAR;
                sleep(200);
                this.sendArchivo.writeUTF("quiensoy");
                this.sendArchivo.writeUTF(this.nombreUsuario);
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

