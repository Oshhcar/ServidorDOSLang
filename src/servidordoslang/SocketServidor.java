/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidordoslang;

import analizador.ErrorC;
import analizador.Lexico;
import analizador.Sintactico;
import analizador.ast.AST;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Simbolo;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JTextArea;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author oscar
 */
public class SocketServidor extends Thread {

    public ServerSocket ServidorSocket;
    private JTextArea Text;

    public void StartServer() {
        String cadena = "cadena para enviar";
        Socket socket;
        this.Text.setText(this.Text.getText() + "Iniciando el Servidor.\n");

        try {
            // se instancia y se abre un puerto
            ServidorSocket = new ServerSocket(1234);
            System.out.println("Servidor Socket iniciado Puerto:1234");
            this.Text.setText(this.Text.getText() + "Servidor Socket iniciado Puerto:1234\n");
            while (true) {
                //aceptamos la conexion del cliente
                System.out.println("Esperando una conexión...");
                this.Text.setText(this.Text.getText() + "Esperando una conexión...\n");
                socket = ServidorSocket.accept();
                System.out.println("Se conecto un Cliente.");
                this.Text.setText(this.Text.getText() + "Se conecto un Cliente.\n");
                //entrada se recibe los mensajaes del cliente
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String mensaje = entrada.readLine();
                //System.out.println("rec: "+mensaje);

                //mensaje = "{\"data\":[{\"name\":\"file1\",\"content\":\"writeln(\\\"hola mundo\\\");\\n\"}]}";
                String respuesta = "";

                ArrayList<File> files = new ArrayList<>();

                //Se obtienen los files en json y se guardan en array
                JSONParser parser = new JSONParser();
                try {
                    Object obj = parser.parse(mensaje);
                    JSONObject dat = (JSONObject) obj;

                    JSONArray array = (JSONArray) dat.get("data");

                    for (int i = 0; i < array.size(); i++) {
                        JSONObject file = (JSONObject) array.get(i);
                        String name = (String) file.get("name");
                        String content = (String) file.get("content");
                        Object main = file.get("main");

                        if (name != null && content != null && main != null) {
                            File f = new File(name, content, (boolean) main);
                            files.add(f);
                        }
                    }

                } catch (ParseException pe) {

                    System.out.println("position: " + pe.getPosition());
                    System.out.println(pe);
                    socket.close();
                }

                //se busca el archivo principal:
                System.out.println("Se han recibido los siguientes archivos:");
                this.Text.setText(this.Text.getText() + "Se han recibido los siguientes archivos:\n");

                File main = null;
                int i = 1;
                for (File f : files) {
                    if (f.isMain()) {
                        main = f;
                    }
                    System.out.println(i + "- " + f.getName());
                    this.Text.setText(this.Text.getText() + i + "- " + f.getName() + "\n");
                    i++;
                }

                //Si hay archivo principal, se ejecuta:
                ArrayList<ErrorC> errores = new ArrayList<>();
                Entorno global = new Entorno("Global");

                if (main != null) {
                    System.out.println("Archivo principal: " + main.getName());
                    this.Text.setText(this.Text.getText() + "Archivo principal:" + main.getName() + "\n");

                    if (!(main.getContent().isEmpty() && main.getContent().isBlank())) {
                        Lexico lexico = new Lexico(new StringReader(main.getContent()));
                        Sintactico sintactico = new Sintactico(lexico);

                        try {
                            sintactico.parse();
                            AST ast = sintactico.getAST();

                            errores.addAll(lexico.getErrores());
                            errores.addAll(sintactico.getErrores());

                            if (ast != null) {
                                ArrayList<ErrorC> erroresCompilacion = new ArrayList<>();
                                respuesta = ast.GenerarCuadruplos(global, erroresCompilacion, files, main.getContent());
                                errores.addAll(erroresCompilacion);
                            }

                        } catch (Exception ex) {
                            System.out.println("Error al interpretar.");
                            this.Text.setText(this.Text.getText() + "Error al interpretar.\n");

                            System.out.println("Parse: " + ex);
                        }
                    }
                } else {
                    respuesta = "No se encontro el archivo principal";
                    System.out.println("No se encontro el archivo principal.");
                    this.Text.setText(this.Text.getText() + "No se encontro el archivo principal.\n");
                    //Agregar a los errore que no se econtro el main
                }

                //respuesta = "hola mundo " + files.size() ;
                //System.out.println(mensaje);
                //salida se envia los mensajes al cliente
                DataOutputStream salida = new DataOutputStream((socket.getOutputStream()));
                PrintWriter pw = new PrintWriter(salida);

                JSONArray errArray = new JSONArray();

                for (ErrorC e : errores) {
                    JSONObject errObj = new JSONObject();
                    errObj.put("descripcion", e.getDescripcion());
                    errObj.put("columna", e.getColumna() + "");
                    errObj.put("linea", e.getLinea() + "");
                    errObj.put("valor", e.getTipo());
                    errArray.add(errObj);
                }

                JSONArray tableArray = new JSONArray();
                for (Simbolo s : global.getSimbolos()) {
                    JSONObject simObj = new JSONObject();

                    if (s.getTipoParam() < 0) {
                        simObj.put("tipoParam", "--");
                    } else {
                        simObj.put("tipoParam", s.getTipoParam() == 1 ? "Valor" : "Referencia");
                    }

                    if (s.getNumParam() < 0) {
                        simObj.put("numParam", "--");
                    } else {
                        simObj.put("numParam", s.getNumParam() + "");
                    }

                    simObj.put("ambito", s.getAmbito());

                    if (s.getPos() < 0) {
                        simObj.put("pos", "--");
                    } else {
                        simObj.put("pos", s.getPos() + "");
                    }

                    if (s.getTam() < 0) {
                        simObj.put("tam", "--");
                    } else {
                        simObj.put("tam", s.getTam() + "");
                    }

                    simObj.put("rol", s.getRol().name().toLowerCase());
                    if(s.getTipo().IsArray()){
                        if(s.getTipo().getDimensiones() != null){
                            simObj.put("tipo", s.getTipo().toString() + " de " + s.getTipo().getDimensiones().size() + " dim");
                        } else {
                            simObj.put("tipo", s.getTipo().toString());
                        }
                    } else if(s.getTipo().IsRecord()){
                        if(s.getTipo().getEntorno() != null){
                            String rec = "record con ";
                            
                            for(Simbolo sRec: s.getTipo().getEntorno().getSimbolos()){
                                rec += sRec.getId() + ":" + sRec.getTipo().toString() + "; ";
                            }
                            simObj.put("tipo", rec);
                        } else {
                            simObj.put("tipo", s.getTipo().toString());
                        }
                    } else {
                        simObj.put("tipo", s.getTipo().toString());
                    }
                    simObj.put("id", s.getId());
                    tableArray.add(simObj);
                }

                //System.out.println("Errores: "+errArray.toJSONString());
                JSONObject objFile = new JSONObject();
                objFile.put("content", respuesta);
                objFile.put("errors", errArray);
                objFile.put("table", tableArray);
                StringWriter out = new StringWriter();
                objFile.writeJSONString(out);

                //salida.writeUTF("{\"foo-bar\": 12345}");
                pw.println(out.toString());

                pw.close();
                salida.close();
                entrada.close();
                //cerramos la conexion
                socket.close();
                System.out.println("Se responde al Cliente.");
                this.Text.setText(this.Text.getText() + "Se responde al Cliente.\n");
                
                System.out.println("Conexión terminada.\n");
                this.Text.setText(this.Text.getText() + "Conexión terminada.\n\n");
                //ServidorSocket.close();
            }

        } catch (IOException e1) {
            System.out.println("ERROR!\nNo se pudo iniciar la conexión en el puerto:1234.\n");
            this.Text.setText(this.Text.getText() + "ERROR!\nNo se pudo iniciar la conexión en el puerto:1234.\n\n");
            //e1.printStackTrace();
            return;
        }
    }

    @Override
    public void run() {
        StartServer();
    }

    /**
     * @return the Text
     */
    public JTextArea getText() {
        return Text;
    }

    /**
     * @param Text the Text to set
     */
    public void setText(JTextArea Text) {
        this.Text = Text;
    }

}
