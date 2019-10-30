/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion.ciclos;

import analizador.ErrorC;
import analizador.ast.NodoAST;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.expresion.Expresion;
import analizador.ast.instruccion.Instruccion;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class While extends Instruccion{
    
    private Expresion Condicion;
    private NodoAST Sentencia;

    public While(Expresion Condicion, NodoAST Sentencia, int Linea, int Columna) {
        super(Linea, Columna);
        this.Condicion = Condicion;
        this.Sentencia = Sentencia;
    }
    
    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
        Result result = new Result();
        String codigo = "";
        
        result.setCodigo(codigo);
        return result;
    }

    /**
     * @return the Condicion
     */
    public Expresion getCondicion() {
        return Condicion;
    }

    /**
     * @param Condicion the Condicion to set
     */
    public void setCondicion(Expresion Condicion) {
        this.Condicion = Condicion;
    }

    /**
     * @return the Sentencia
     */
    public NodoAST getSentencia() {
        return Sentencia;
    }

    /**
     * @param Sentencia the Sentencia to set
     */
    public void setSentencia(NodoAST Sentencia) {
        this.Sentencia = Sentencia;
    }
    
}
