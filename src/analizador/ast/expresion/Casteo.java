/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.expresion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Tipo;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Casteo extends Expresion {

    private Tipo TipoTarget;
    private Tipo TipoValor;
    private Result ResultValor;

    public Casteo(Tipo TipoTarget, Tipo TipoValor, Result ResultValor, int Linea, int Columna) {
        super(Linea, Columna);
        this.TipoTarget = TipoTarget;
        this.TipoValor = TipoValor;
        this.ResultValor = ResultValor;
    }
    
    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";
        
        
        result.setCodigo(codigo);
        return result;
    }

    /**
     * @return the TipoTarget
     */
    public Tipo getTipoTarget() {
        return TipoTarget;
    }

    /**
     * @param TipoTarget the TipoTarget to set
     */
    public void setTipoTarget(Tipo TipoTarget) {
        this.TipoTarget = TipoTarget;
    }

    /**
     * @return the TipoValor
     */
    public Tipo getTipoValor() {
        return TipoValor;
    }

    /**
     * @param TipoValor the TipoValor to set
     */
    public void setTipoValor(Tipo TipoValor) {
        this.TipoValor = TipoValor;
    }

    /**
     * @return the ResultValor
     */
    public Result getResultValor() {
        return ResultValor;
    }

    /**
     * @param ResultValor the ResultValor to set
     */
    public void setResultValor(Result ResultValor) {
        this.ResultValor = ResultValor;
    }
    
}
