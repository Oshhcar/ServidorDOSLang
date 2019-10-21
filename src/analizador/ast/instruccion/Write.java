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
                    String tmpCiclo;
                    
                    switch (tipoExp.getTipo()) {
                        case CHAR:
                            codigo += rsExp.getCodigo();
                            codigo += "print(%c, " + rsExp.getValor() + ")\n";
                            break;
                        case INTEGER:
                            codigo += rsExp.getCodigo();
                            codigo += "print(%e, " + rsExp.getValor() + ")\n";
                            break;
                        case REAL:
                            codigo += rsExp.getCodigo();
                            codigo += "print(%d, " + rsExp.getValor() + ")\n";
                            break;
                        case STRING:
                        case WORD:
                            codigo += rsExp.getCodigo();
                            
                            result.setEtiquetaV(NuevaEtiqueta());
                            result.setEtiquetaF(NuevaEtiqueta());
                            String etqCiclo = NuevaEtiqueta();
                            tmpCiclo = NuevoTemporal();
                            
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
                        case BOOLEAN:
                            codigo += rsExp.getCodigo();
                            
                            result.setEtiquetaV(NuevaEtiqueta());
                            result.setEtiquetaF(NuevaEtiqueta());
                            String etqSalida = NuevaEtiqueta();
                            tmpCiclo = NuevoTemporal();
                            
                            codigo += "je, " + rsExp.getValor() + ", 0, " + result.getEtiquetaV() + "\n";
                            codigo += "jmp, , , " + result.getEtiquetaF()+"\n";
                            codigo += result.getEtiquetaV() + ":\n";
                            //false
                            codigo += "print(%c, 102)\n";
                            codigo += "print(%c, 97)\n";
                            codigo += "print(%c, 108)\n";
                            codigo += "print(%c, 115)\n";
                            codigo += "print(%c, 101)\n";
                            codigo += "jmp, , , " + etqSalida+"\n";
                            codigo += result.getEtiquetaF() + ":\n";
                            //true
                            codigo += "print(%c, 116)\n";
                            codigo += "print(%c, 114)\n";
                            codigo += "print(%c, 117)\n";
                            codigo += "print(%c, 101)\n";
                            codigo += etqSalida+":\n";
                            break;
                        case NIL:
                            codigo += rsExp.getCodigo();
                            
                            codigo += "print(%c, 78)\n";
                            codigo += "print(%c, 73)\n";
                            codigo += "print(%c, 76)\n";
                            break;
                        default:
                            errores.add(new ErrorC(3, this.getLinea(), this.getColumna(), "Valor no se puede imprimir."));
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
