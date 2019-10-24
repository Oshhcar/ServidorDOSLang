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
import analizador.ast.expresion.operacion.Logica;
import analizador.ast.expresion.operacion.Operador;
import analizador.ast.expresion.operacion.Relacional;
import analizador.ast.instruccion.Instruccion;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Case extends Instruccion{

    private ArrayList<Expresion> Expresiones;
    private NodoAST Sentencia;
    private Expresion Expr;
    private String EtqSalida;

    public Case(ArrayList<Expresion> Expresiones, NodoAST Sentencia, int Linea, int Columna) {
        super(Linea, Columna);
        this.Expresiones = Expresiones;
        this.Sentencia = Sentencia;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";
        
        Expresion Condicion = null;
        
        for(Expresion expCase: Expresiones){
            if(Condicion == null){
                Condicion = new Relacional(Expr, expCase, Operador.IGUAL, Linea, Columna);
            } else {
                Relacional act = new Relacional(Expr, expCase, Operador.IGUAL, Linea, Columna);
                Condicion = new Logica(Condicion, act, Operador.OR, Linea, Columna);
            }
        }
        
        if (Condicion instanceof Relacional) {
            ((Relacional) Condicion).setCortoCircuito(true);
        } else if (Condicion instanceof Logica) {
            ((Logica) Condicion).setEvaluar(true);
        }
        
        Result rsCondicion = Condicion.GetCuadruplos(e, errores);
        
        if (Condicion instanceof Relacional) {
            String copy = rsCondicion.getEtiquetaF();
            rsCondicion.setEtiquetaF(rsCondicion.getEtiquetaV());
            rsCondicion.setEtiquetaV(copy);
        }
        
        codigo += rsCondicion.getCodigo();
        codigo += rsCondicion.getEtiquetaV();
        
        if (Sentencia instanceof Instruccion) {
            codigo += ((Instruccion) Sentencia).GetCuadruplos(e, errores).getCodigo();
        } else if (Sentencia instanceof Expresion) {
            codigo += ((Expresion) Sentencia).GetCuadruplos(e, errores).getCodigo();
        }
        
        codigo += "jmp, , , " + EtqSalida+"\n";
        codigo += rsCondicion.getEtiquetaF();
        
        result.setCodigo(codigo);
        return result;
    }

    /**
     * @return the Expresiones
     */
    public ArrayList<Expresion> getExpresiones() {
        return Expresiones;
    }

    /**
     * @param Expresiones the Expresiones to set
     */
    public void setExpresiones(ArrayList<Expresion> Expresiones) {
        this.Expresiones = Expresiones;
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

    /**
     * @return the Expr
     */
    public Expresion getExpr() {
        return Expr;
    }

    /**
     * @param Expr the Expr to set
     */
    public void setExpr(Expresion Expr) {
        this.Expr = Expr;
    }

    /**
     * @return the EtqSalida
     */
    public String getEtqSalida() {
        return EtqSalida;
    }

    /**
     * @param EtqSalida the EtqSalida to set
     */
    public void setEtqSalida(String EtqSalida) {
        this.EtqSalida = EtqSalida;
    }
    
}
