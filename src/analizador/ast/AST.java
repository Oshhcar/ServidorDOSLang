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
    private ArrayList<NodoAST> Sentencias;

    public AST(String Nombre, ArrayList<String> Uses, ArrayList<TipoDef> Tipos, ArrayList<NodoAST> Sentencias) {
        this.Nombre = Nombre;
        this.Uses = Uses;
        this.Tipos = Tipos;
        this.Sentencias = Sentencias;
    }
    
    public String GenerarCuadruplos(Entorno global, ArrayList<ErrorC> errores, ArrayList<File> files) {
        NodoAST.Etiquetas = 0;
        NodoAST.Temporales = 0;
        NodoAST.H = 0;

        /**
         * Ejecuto declaracion Tipos
         */
        if(Tipos != null){
            Tipos.forEach((tipo) -> {
                tipo.GetCuadruplos(global, errores);
            });
        }
        //global.Recorrer();
        
        Result result = new Result();
        result.setCodigo("");
        global.setTmpInicio(NodoAST.Temporales+1);

        if (Sentencias != null) {
            //Primera pasada para saber numero temporales
            for (NodoAST nodo : Sentencias) {
                //Result rsNodo = null;

                if (nodo instanceof Instruccion) {
                    /*rsNodo =*/ ((Instruccion) nodo).GetCuadruplos(global, new ArrayList<>());
                } else if (nodo instanceof Expresion) {
                    /*rsNodo =*/ ((Expresion) nodo).GetCuadruplos(global, new ArrayList<>());
                }

                //if(rsNodo != null){
                //    if(rsNodo.getCodigo() != null){
                //        result.setCodigo(result.getCodigo() + rsNodo.getCodigo());
                //    }
                //}
            }

            //Variables locales
            global.setSize(global.getPos());
            global.setSize(8);
            //Temporales en ambito
            global.setTmpFin(NodoAST.Temporales);

            System.out.println("inicio: " + global.getTmpInicio() + " fin " + global.getTmpFin());

            //Reinicio el contador de las variables locales y tmp etc
            //global.setPos(0);
            NodoAST.Etiquetas = 0;
            NodoAST.Temporales = 0;
            NodoAST.H = 0;

            for (NodoAST nodo : Sentencias) {
                Result rsNodo = null;

                if (nodo instanceof Instruccion) {
                    rsNodo = ((Instruccion) nodo).GetCuadruplos(global, errores);
                } else if (nodo instanceof Expresion) {
                    rsNodo = ((Expresion) nodo).GetCuadruplos(global, errores);
                }

                if (rsNodo != null) {
                    if (rsNodo.getCodigo() != null) {
                        result.setCodigo(result.getCodigo() + rsNodo.getCodigo());
                    }
                }
            }
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

}
