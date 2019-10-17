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
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class AST {
    
    private ArrayList<NodoAST> Sentencias;

    public AST(ArrayList<NodoAST> Sentencias) {
        this.Sentencias = Sentencias;
    }

    public String GenerarCuadruplos(ArrayList<ErrorC> errores){
        NodoAST.Etiquetas = 0;
        NodoAST.Temporales = 0;
        NodoAST.H = 0;
        
        Entorno global = new Entorno("Global");
        
        Result result = new Result();
        result.setCodigo("");
        
        for(NodoAST nodo: Sentencias){
            Result rsNodo;
            if(nodo instanceof Instruccion){
                rsNodo = ((Instruccion) nodo).GetCuadruplos(global, errores);
            } else {
                rsNodo = ((Expresion) nodo).GetCuadruplos(global, errores);
            }
            
            if(rsNodo != null){
                if(rsNodo.getCodigo() != null){
                    result.setCodigo(result.getCodigo() + rsNodo.getCodigo());
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
    
    
}
