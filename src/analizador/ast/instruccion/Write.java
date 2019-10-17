/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Tipo;
import analizador.ast.expresion.Expresion;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Write extends Instruccion {

    public boolean IsLn;
    public ArrayList<Expresion> Exprs;

    public Write(boolean IsLn, ArrayList<Expresion> Exprs, int Linea, int Columna) {
        super(Linea, Columna);
        this.IsLn = IsLn;
        this.Exprs = Exprs;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";

        for (Expresion E : Exprs) {
            Result rsExp = E.GetCuadruplos(e, errores);
            Tipo tipoExp = E.GetTipo();

            if (!tipoExp.IsUndefined()) {
                if (rsExp.getValor() != null) {
                    
                    codigo += rsExp.getCodigo();

                    switch (tipoExp.getTipo()) {
                        case CHAR:
                            codigo += "print(%c, " + rsExp.getValor() + ")\n";
                            break;
                        case INTEGER:
                            codigo += "print(%e, " + rsExp.getValor() + ")\n";
                            break;
                        case REAL:
                            codigo += "print(%d, " + rsExp.getValor() + ")\n";
                            break;
                        case STRING:
                            
                            result.setEtiquetaV(NuevaEtiqueta());
                            result.setEtiquetaF(NuevaEtiqueta());
                            String etqCiclo = NuevaEtiqueta();
                            String tmpCiclo = NuevoTemporal();
                            
                            codigo += etqCiclo + ":\n";
                            codigo += "=, heap, " + rsExp.getValor() +", " + tmpCiclo+"\n";
                            codigo += "je, " + tmpCiclo +", 0, " + result.getEtiquetaV()+"\n";
                            codigo += "jmp, , , " + result.getEtiquetaF()+"\n";
                            codigo += result.getEtiquetaF() + ":\n";
                            codigo += "print(%c, " + tmpCiclo + ")\n";
                            codigo += "+, " + rsExp.getValor() +", 1, " + rsExp.getValor() + "\n";
                            codigo += "jmp, , , " + etqCiclo + "\n";
                            codigo += result.getEtiquetaV() + ":\n";
                            
                            break;
                    }
                }
            }
        }

        if (IsLn) {
            codigo += "print(%c, 10)\n";
        }

        result.setCodigo(codigo);
        return result;
    }

}
