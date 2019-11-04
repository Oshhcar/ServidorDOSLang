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
import analizador.ast.entorno.Simbolo;
import analizador.ast.expresion.Atributo;
import analizador.ast.expresion.Expresion;
import analizador.ast.expresion.Identificador;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class WithDo extends Instruccion {

    private ArrayList<Expresion> Target;
    private ArrayList<NodoAST> Sentencias;
    
    public WithDo(ArrayList<Expresion> Target, int Linea, int Columna) {
        super(Linea, Columna);
        this.Target = Target;
        this.Sentencias = null;
    }

    public WithDo(ArrayList<Expresion> Target, ArrayList<NodoAST> Sentencias, int Linea, int Columna) {
        super(Linea, Columna);
        this.Target = Target;
        this.Sentencias = Sentencias;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
        Result result = new Result();
        String codigo = "";

        Entorno local = e;

        for (Expresion target : Target) { 
            
            if(target instanceof Identificador){
                ((Identificador) target).setObtenerSim(true);
            } else if(target instanceof Atributo){
                ((Atributo) target).setObtenerSim(true);
            }
            
            Result rsTarget = target.GetCuadruplos(e, errores);
            
            
            Simbolo s = rsTarget.getSimbolo();

            if (s != null) {
                if (s.getTipo().IsRecord()) {
                    Entorno tmp = new Entorno(s.getId(), local);
                    tmp.setSize(local.getSize());
                    tmp.setTmpInicio(local.getTmpInicio());
                    tmp.setTmpFin(local.getTmpFin());
                    tmp.setSalidaCiclo(local.getSalidaCiclo());
                    tmp.setContinueCiclo(local.getContinueCiclo());
                    
                    tmp.getSimbolos().addAll(s.getEntorno().getSimbolos());//agregar tmpinicio y fin
                    local = tmp;
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, s.getId() + " no es de tipo record."));
                }
            } 
        }

        if (Sentencias != null) {
            for (NodoAST nodo : Sentencias) {
                if (nodo instanceof Instruccion) {
                    codigo += ((Instruccion) nodo).GetCuadruplos(local, errores, global).getCodigo();
                } else if (nodo instanceof Expresion) {
                    codigo += ((Expresion) nodo).GetCuadruplos(local, errores).getCodigo();
                }
            }
        }

        result.setCodigo(codigo);
        return result;
    }

}
