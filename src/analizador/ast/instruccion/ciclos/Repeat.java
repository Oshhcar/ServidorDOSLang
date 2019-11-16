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
import analizador.ast.expresion.Literal;
import analizador.ast.expresion.operacion.Logica;
import analizador.ast.expresion.operacion.Relacional;
import analizador.ast.expresion.operacion.Unario;
import analizador.ast.instruccion.Instruccion;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Repeat extends Instruccion {

    private ArrayList<NodoAST> Sentencias;
    private Expresion Condicion;

    public Repeat(ArrayList<NodoAST> Sentencias, Expresion Condicion, int Linea, int Columna) {
        super(Linea, Columna);
        this.Sentencias = Sentencias;
        this.Condicion = Condicion;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
        Result result = new Result();
        String codigo = "";

        String etqCiclo = NuevaEtiqueta();

        codigo += etqCiclo + ":\n";
        e.getSalidaCiclo().push(NuevaEtiqueta());
        e.getContinueCiclo().push(etqCiclo);
        
        for (NodoAST nodo : Sentencias) {
            if (nodo instanceof Instruccion) {
                codigo += ((Instruccion) nodo).GetCuadruplos(e, errores, global).getCodigo();
            } else if (nodo instanceof Expresion) {
                codigo += ((Expresion) nodo).GetCuadruplos(e, errores).getCodigo();
            }
        }
        
        if (Condicion instanceof Relacional) {
            ((Relacional) Condicion).setCortoCircuito(true);
        } else if (Condicion instanceof Logica) {
            ((Logica) Condicion).setEvaluar(true);
        } else if (Condicion instanceof Unario) {
            ((Unario) Condicion).setEvaluar(true);
        }
        
        Result rsCondicion = Condicion.GetCuadruplos(e, errores);
        
        if (Condicion instanceof Literal) {
            String cod = rsCondicion.getCodigo();

            rsCondicion.setEtiquetaV(NuevaEtiqueta());
            rsCondicion.setEtiquetaF(NuevaEtiqueta());

            cod += "je, t" + rsCondicion.getValor() + ", 1, " + rsCondicion.getEtiquetaV() + "\n";
            cod += "jmp, , , " + rsCondicion.getEtiquetaF() + "\n";

            rsCondicion.setEtiquetaV(rsCondicion.getEtiquetaV() + ":\n");
            rsCondicion.setEtiquetaF(rsCondicion.getEtiquetaF() + ":\n");

            rsCondicion.setCodigo(cod);
        }
        
        if (Condicion instanceof Relacional || Condicion instanceof Literal) {
            String copy = rsCondicion.getEtiquetaF();
            rsCondicion.setEtiquetaF(rsCondicion.getEtiquetaV());
            rsCondicion.setEtiquetaV(copy);
        }
        
        codigo += rsCondicion.getCodigo();
        codigo += rsCondicion.getEtiquetaV();
        codigo += "jmp, , , " + etqCiclo +"\n";
        codigo += rsCondicion.getEtiquetaF();
        codigo += e.getSalidaCiclo().pop() + ":\n";
        
        e.getContinueCiclo().pop();
        
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

}
