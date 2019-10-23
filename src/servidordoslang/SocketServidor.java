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

    public void StartServer() {
        String cadena = "cadena para enviar";
        Socket socket;

        try {
            // se instancia y se abre un puerto
            ServidorSocket = new ServerSocket(1234);
            while (true) {
                //aceptamos la conexion del cliente
                System.out.println("Esperando...");
                socket = ServidorSocket.accept();
                System.out.println("Se conecto un Cliente.");
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
                File main = null;
                for (File f : files) {
                    if (f.isMain()) {
                        main = f;
                        break;
                    }
                }

                //Si hay archivo principal, se ejecuta:
                ArrayList<ErrorC> errores = new ArrayList<>();
                if (main != null) {
                    if (!(main.getContent().isEmpty() && main.getContent().isBlank())) {
                        Lexico lexico = new Lexico(new StringReader(main.getContent()));
                        Sintactico sintactico = new Sintactico(lexico);

                        try {
                            sintactico.parse();
                            AST ast = sintactico.getAST();
                            
                            errores.addAll(lexico.getErrores());
                            errores.addAll(sintactico.getErrores());

                            if (ast != null) {
                                respuesta = ast.GenerarCuadruplos(errores, files);
                            }

                        } catch (Exception ex) {
                            System.out.println("Parse: " + ex);
                        }
                    }
                } else {
                    respuesta = "No se encontro el archivo principal";
                    //Agregar a los errore que no se econtro el main
                }

                //respuesta = "hola mundo " + files.size() ;
                //System.out.println(mensaje);
                //salida se envia los mensajes al cliente
                DataOutputStream salida = new DataOutputStream((socket.getOutputStream()));
                PrintWriter pw = new PrintWriter(salida);
                
                JSONArray errArray = new JSONArray();
                
                for(ErrorC e: errores){
                    JSONObject errObj = new JSONObject();
                    errObj.put("descripcion", e.getDescripcion());
                    errObj.put("columna", e.getColumna()+"");
                    errObj.put("linea", e.getLinea()+"");
                    errObj.put("valor", e.getTipo());
                    errArray.add(errObj);
                }
                
                //System.out.println("Errores: "+errArray.toJSONString());

                JSONObject objFile = new JSONObject();
                objFile.put("content", respuesta);
                objFile.put("errors", errArray);
                //objFile.put("table", "<table><tr><th>id<th></tr><tr><td>id1<td></tr><tr><td>id2<td></tr><table>");
                StringWriter out = new StringWriter();
                objFile.writeJSONString(out);
                
                //salida.writeUTF("{\"foo-bar\": 12345}");
                pw.println(out.toString());

                pw.close();
                salida.close();
                entrada.close();
                //cerramos la conexion
                socket.close();
                //ServidorSocket.close();
            }

        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }
    }

    @Override
    public void run() {
        StartServer();
    }

}
