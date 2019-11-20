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
    private boolean DeclararVar;

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
            ArrayList<Use> Uses = null;
            ArrayList<TipoDef> Tipos = null;
            ArrayList<VarDef> Constantes = null;
            ArrayList<VarDef> Variables = null;
            ArrayList<Metodo> Metodos = null;

            Lexico lexico = new Lexico(new StringReader(file.getContent()));
            Sintactico sintactico = new Sintactico(lexico);

            try {
                sintactico.parse();
                AST ast = sintactico.getAST();

                if (ast != null) {
                    Uses = ast.getUses();
                    Tipos = ast.getTipos();
                    Constantes = ast.getConstantes();
                    Variables = ast.getVariables();
                    Metodos = ast.getMetodos();
                }

            } catch (Exception ex) {
                System.out.println("Parse2: " + ex);
            }

            localErrores.addAll(lexico.getErrores());
            localErrores.addAll(sintactico.getErrores());

            if (DeclararVar) {
                ArrayList<ErrorC> otrosErrores = new ArrayList<>();

                if (Tipos != null) {
                    Tipos.forEach((tipo) -> {
                        tipo.GetCuadruplos(e, otrosErrores, global);
                    });
                }

                if (Constantes != null) {
                    for (VarDef constante : Constantes) {
                        constante.setConstante(true);
                        Result rsCons = constante.GetCuadruplos(e, otrosErrores, global);
                        if (rsCons != null) {
                            codigo += rsCons.getCodigo();
                        }
                    }
                }

                if (Variables != null) {
                    for (VarDef variable : Variables) {
                        Result rsVar = variable.GetCuadruplos(e, otrosErrores, global);
                        if (rsVar != null) {
                            codigo += rsVar.getCodigo();
                        }
                    }
                }

                if (Uses != null) {
                    for(Use Use: Uses){
                        Use.setFiles(Files);
                        Use.setDeclaracion(Declaracion);
                        Use.setDeclararVar(true);
                        Result rsUse = Use.GetCuadruplos(e, otrosErrores, global);
                        codigo += rsUse.getCodigo();
                    }
                }

            } else {
                if (Metodos != null) {
                    for (Metodo metodo : Metodos) {
                        metodo.setDeclaracion(Declaracion);
                        metodo.setAmbito(Nombre);
                        Result rsMetodo = metodo.GetCuadruplos(e, localErrores, global);
                        codigo += rsMetodo.getCodigo();
                    }
                }
                
                if (Uses != null) {
                    for(Use Use: Uses){
                        Use.setFiles(Files);
                        Use.setDeclaracion(Declaracion);
                        Use.setDeclararVar(false);
                        Result rsUse = Use.GetCuadruplos(e, localErrores, global);
                        codigo += rsUse.getCodigo();
                    }
                }
            }

        } else {
            localErrores.add(new ErrorC("Semántico", Linea, Columna, "No se ha encontrado el archivo " + Nombre + "."));
        }

        if (!Declaracion) {
            for (ErrorC error : localErrores) {
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

    /**
     * @return the DeclararVar
     */
    public boolean isDeclararVar() {
        return DeclararVar;
    }

    /**
     * @param DeclararVar the DeclararVar to set
     */
    public void setDeclararVar(boolean DeclararVar) {
        this.DeclararVar = DeclararVar;
    }

}
