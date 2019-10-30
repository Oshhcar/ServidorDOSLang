/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion;

import analizador.ErrorC;
import analizador.ast.NodoAST;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.expresion.Expresion;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Bloque extends Instruccion {

    private ArrayList<NodoAST> Sentencias;

    public Bloque(ArrayList<NodoAST> Sentencias, int Linea, int Columna) {
        super(Linea, Columna);
        this.Sentencias = Sentencias;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
        Result result = new Result();
        String codigo = "";

        if (Sentencias != null) {
            for (NodoAST nodo : Sentencias) {
                if (nodo instanceof Instruccion) {
                    codigo  += ((Instruccion) nodo).GetCuadruplos(e, errores, global).getCodigo();
                } else if (nodo instanceof Expresion) {
                    codigo += ((Expresion) nodo).GetCuadruplos(e, errores).getCodigo();
                }
            }
        }

        result.setCodigo(codigo);
        return result;
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
