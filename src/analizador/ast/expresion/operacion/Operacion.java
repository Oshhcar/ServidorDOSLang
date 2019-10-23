/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.expresion.operacion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Tipo;
import analizador.ast.entorno.Type;
import analizador.ast.expresion.Expresion;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Operacion extends Expresion{
    protected Expresion Op1;
    protected Expresion Op2;
    protected Operador Op;
    
    public Operacion(Expresion Op1, Expresion Op2, Operador op, int Linea, int Columna) {
        super(Linea, Columna);
        this.Op1 = Op1;
        this.Op2 = Op2;
        this.Op = op;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        return new Result();
    }

    /**
     * @return the Op1
     */
    public Expresion getOp1() {
        return Op1;
    }

    /**
     * @param Op1 the Op1 to set
     */
    public void setOp1(Expresion Op1) {
        this.Op1 = Op1;
    }

    /**
     * @return the Op2
     */
    public Expresion getOp2() {
        return Op2;
    }

    /**
     * @param Op2 the Op2 to set
     */
    public void setOp2(Expresion Op2) {
        this.Op2 = Op2;
    }

    /**
     * @return the Op
     */
    public Operador getOp() {
        return Op;
    }

    /**
     * @param Op the Op to set
     */
    public void setOp(Operador Op) {
        this.Op = Op;
    }
    
}
