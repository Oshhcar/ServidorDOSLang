/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.expresion.Expresion;
import analizador.ast.instruccion.Instruccion;
import analizador.ast.instruccion.TipoDef;
import analizador.ast.instruccion.VarDef;
import java.util.ArrayList;
import servidordoslang.File;

/**
 *
 * @author oscar
 */
public class AST {

    private String Nombre;
    private ArrayList<String> Uses;
    private ArrayList<TipoDef> Tipos;
    private ArrayList<VarDef> Variables;
    private ArrayList<NodoAST> Sentencias;

    public AST(String Nombre, ArrayList<String> Uses, ArrayList<TipoDef> Tipos, ArrayList<VarDef> Variables, ArrayList<NodoAST> Sentencias) {
        this.Nombre = Nombre;
        this.Uses = Uses;
        this.Tipos = Tipos;
        this.Variables = Variables;
        this.Sentencias = Sentencias;
    }

    public String GenerarCuadruplos(Entorno global, ArrayList<ErrorC> errores, ArrayList<File> files) {
        Result result = new Result();
        result.setCodigo("");

        NodoAST.Etiquetas = 0;
        NodoAST.Temporales = 0;
        NodoAST.H = 0;

        Entorno local = new Entorno(Nombre);
        local.setTmpInicio(NodoAST.Temporales + 1);

        /*Primera pasada para saber size y total temporales*/
        /**
         * Ejecuto declaracion Tipos
         */
        if (Tipos != null) {
            Tipos.forEach((tipo) -> {
                tipo.GetCuadruplos(local, errores, global);
            });
        }

        /**
         * Ejecuto declaracion Variables
         */
        if (Variables != null) {
            Variables.forEach((variable) -> {
                variable.GetCuadruplos(local, errores, global);
            });
        }

        /**
         * Ejecuto Sentencias
         */
        if (Sentencias != null) {
            Sentencias.forEach((nodo) -> {
                if (nodo instanceof Instruccion) {
                    ((Instruccion) nodo).GetCuadruplos(local, errores, global);
                } else if (nodo instanceof Expresion) {
                    ((Expresion) nodo).GetCuadruplos(local, errores);
                }
            });
        }

        local.getSimbolos().clear();
        global.getSimbolos().clear();
        errores.clear();

        //Variables locales
        local.setSize(local.getPos());

        //Temporales en ambito
        local.setTmpFin(NodoAST.Temporales);

        System.out.println("inicio: " + local.getTmpInicio() + " fin " + local.getTmpFin());

        //Reinicio el contador de las variables locales y tmp etc
        local.setPos(0);
        NodoAST.Etiquetas = 0;
        NodoAST.Temporales = 0;
        NodoAST.H = 0;

        /*Segunda pasada*/
        /**
         * Ejecuto declaracion Tipos
         */
        if (Tipos != null) {
            Tipos.forEach((tipo) -> {
                tipo.GetCuadruplos(local, errores, global);
            });
        }

        /**
         * Ejecuto declaracion Variables
         */
        if (Variables != null) {
            Variables.forEach((variable) -> {
                Result rsVar = variable.GetCuadruplos(local, errores, global);
                if (rsVar != null) {
                    result.setCodigo(result.getCodigo() + rsVar.getCodigo());
                }
            });
        }

        /**
         * Ejecuto Sentencias
         */
        if (Sentencias != null) {
            Sentencias.forEach((nodo) -> {
                Result rsNodo = null;

                if (nodo instanceof Instruccion) {
                    rsNodo = ((Instruccion) nodo).GetCuadruplos(local, errores, global);
                } else if (nodo instanceof Expresion) {
                    rsNodo = ((Expresion) nodo).GetCuadruplos(local, errores);
                }

                if (rsNodo != null) {
                    result.setCodigo(result.getCodigo() + rsNodo.getCodigo());
                }
            });
        }

        return result.getCodigo();
    }

    /**
     * @return the Sentencias
     */
    public ArrayList<NodoAST> getSentencias() {
        return Sentencias;
    }

    /**
     * @param Sentencias the Sentencias to set
     */
    public void setSentencias(ArrayList<NodoAST> Sentencias) {
        this.Sentencias = Sentencias;
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
     * @return the Uses
     */
    public ArrayList<String> getUses() {
        return Uses;
    }

    /**
     * @param Uses the Uses to set
     */
    public void setUses(ArrayList<String> Uses) {
        this.Uses = Uses;
    }

    /**
     * @return the Tipos
     */
    public ArrayList<TipoDef> getTipos() {
        return Tipos;
    }

    /**
     * @param Tipos the Tipos to set
     */
    public void setTipos(ArrayList<TipoDef> Tipos) {
        this.Tipos = Tipos;
    }

    /**
     * @return the Variables
     */
    public ArrayList<VarDef> getVariables() {
        return Variables;
    }

    /**
     * @param Variables the Variables to set
     */
    public void setVariables(ArrayList<VarDef> Variables) {
        this.Variables = Variables;
    }

}
