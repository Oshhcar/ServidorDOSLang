/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.expresion;

import analizador.ErrorC;
import analizador.ast.NodoAST;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Tipo;
import analizador.ast.entorno.Type;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public abstract class Expresion extends NodoAST{
    
    protected Tipo Tipo;
    
    public Expresion(int Linea, int Columna) {
        super(Linea, Columna);
        Tipo = new Tipo(Type.UNDEFINED);
    }
    
    public abstract Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores);

    /**
     * @return the Tipo
     */
    public Tipo getTipo() {
        return Tipo;
    }

    /**
     * @param Tipo the Tipo to set
     */
    public void setTipo(Tipo Tipo) {
        this.Tipo = Tipo;
    }
}
