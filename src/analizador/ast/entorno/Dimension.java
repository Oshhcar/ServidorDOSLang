/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.entorno;

import analizador.ast.expresion.Expresion;

/**
 *
 * @author oscar
 */
public class Dimension {
    private Expresion LimiteInf;
    private Expresion LimiteSup;

    public Dimension(Expresion LimiteInf, Expresion LimiteSup) {
        this.LimiteInf = LimiteInf;
        this.LimiteSup = LimiteSup;
    }
    
    /**
     * @return the LimiteInf
     */
    public Expresion getLimiteInf() {
        return LimiteInf;
    }

    /**
     * @param LimiteInf the LimiteInf to set
     */
    public void setLimiteInf(Expresion LimiteInf) {
        this.LimiteInf = LimiteInf;
    }

    /**
     * @return the LimiteSup
     */
    public Expresion getLimiteSup() {
        return LimiteSup;
    }

    /**
     * @param LimiteSup the LimiteSup to set
     */
    public void setLimiteSup(Expresion LimiteSup) {
        this.LimiteSup = LimiteSup;
    }
    
}
