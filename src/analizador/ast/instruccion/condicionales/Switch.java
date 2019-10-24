/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion.condicionales;

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
public class Switch extends Instruccion{

    private Expresion Expresion;
    private ArrayList<Case> Cases;
    private NodoAST SentenciaElse;

    public Switch(Expresion Expresion, ArrayList<Case> Cases, int Linea, int Columna) {
        super(Linea, Columna);
        this.Expresion = Expresion;
        this.Cases = Cases;
        this.SentenciaElse = null;
    }

    public Switch(Expresion Expresion, ArrayList<Case> Cases, NodoAST SentenciaElse, int Linea, int Columna) {
        super(Linea, Columna);
        this.Expresion = Expresion;
        this.Cases = Cases;
        this.SentenciaElse = SentenciaElse;
    }
    
    
    
    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";
        
        String etqSalida = NuevaEtiqueta();
        
        for(Case c: Cases){
            c.setExpr(Expresion);
            c.setEtqSalida(etqSalida);
            codigo += c.GetCuadruplos(e, errores).getCodigo();
        }
        
        if(SentenciaElse != null){
            if (SentenciaElse instanceof Instruccion) {
                codigo += ((Instruccion) SentenciaElse).GetCuadruplos(e, errores).getCodigo();
            } else if (SentenciaElse instanceof Expresion) {
                codigo += ((Expresion) SentenciaElse).GetCuadruplos(e, errores).getCodigo();
            }
            codigo += "jmp, , , " + etqSalida + "\n";
        }
        
        codigo += etqSalida+":\n";
        
        result.setCodigo(codigo);
        return result;
    }

    /**
     * @return the Expresion
     */
    public Expresion getExpresion() {
        return Expresion;
    }

    /**
     * @param Expresion the Expresion to set
     */
    public void setExpresion(Expresion Expresion) {
        this.Expresion = Expresion;
    }

    /**
     * @return the Cases
     */
    public ArrayList<Case> getCases() {
        return Cases;
    }

    /**
     * @param Cases the Cases to set
     */
    public void setCases(ArrayList<Case> Cases) {
        this.Cases = Cases;
    }

    /**
     * @return the SentenciaElse
     */
    public NodoAST getSentenciaElse() {
        return SentenciaElse;
    }

    /**
     * @param SentenciaElse the SentenciaElse to set
     */
    public void setSentenciaElse(NodoAST SentenciaElse) {
        this.SentenciaElse = SentenciaElse;
    }
    
}
