/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion;

import analizador.ErrorC;
import analizador.Lexico;
import analizador.Sintactico;
import analizador.ast.AST;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import java.io.StringReader;
import java.util.ArrayList;
import servidordoslang.File;

/**
 *
 * @author oscar
 */
public class Use extends Instruccion {

    private String Nombre;
    private ArrayList<File> Files;
    private boolean Declaracion;

    public Use(String Nombre, int Linea, int Columna) {
        super(Linea, Columna);
        this.Nombre = Nombre;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
        Result result = new Result();
        String codigo = "";

        File file = null;

        if (Files != null) {
            for (File f : Files) {
                if (f.getName().equalsIgnoreCase(Nombre)) {
                    file = f;
                }
            }
        }

        ArrayList<ErrorC> localErrores = new ArrayList<>();

        if (file != null) {

            ArrayList<Metodo> Metodos = null;

            Lexico lexico = new Lexico(new StringReader(file.getContent()));
            Sintactico sintactico = new Sintactico(lexico);

            try {
                sintactico.parse();
                AST ast = sintactico.getAST();

                if (ast != null) {
                    Metodos = ast.getMetodos();
                }

            } catch (Exception ex) {
                System.out.println("Parse2: " + ex);
            }

            localErrores.addAll(lexico.getErrores());
            localErrores.addAll(sintactico.getErrores());

            if (Metodos != null) {
                for(Metodo metodo: Metodos) {
                    metodo.setDeclaracion(Declaracion);
                    metodo.setAmbito(Nombre);
                    Result rsMetodo = metodo.GetCuadruplos(e, localErrores, global);
                    
                    if(!Declaracion){
                        codigo += rsMetodo.getCodigo();
                    }
                    
                }
            }

        } else {
            localErrores.add(new ErrorC("Semántico", Linea, Columna, "No se ha encontrado el archivo " + Nombre + "."));
        }
        
        if(!Declaracion){
            for(ErrorC error: localErrores){
                error.setDescripcion(error.getDescripcion() + " en archivo " + Nombre);
            }
            errores.addAll(localErrores);
        }

        result.setCodigo(codigo);
        return result;
    }

    /**
     * @return the Nombre
     */
    public String getNombre() {
        return Nombre;
    }

    /**
     * @param Nombre the Nombre to set
     */
    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    /**
     * @return the Files
     */
    public ArrayList<File> getFiles() {
        return Files;
    }

    /**
     * @param Files the Files to set
     */
    public void setFiles(ArrayList<File> Files) {
        this.Files = Files;
    }

    /**
     * @return the Declaracion
     */
    public boolean isDeclaracion() {
        return Declaracion;
    }

    /**
     * @param Declaracion the Declaracion to set
     */
    public void setDeclaracion(boolean Declaracion) {
        this.Declaracion = Declaracion;
    }

}
