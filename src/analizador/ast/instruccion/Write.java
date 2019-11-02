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
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
        Result result = new Result();
        String codigo = "";

        for (Expresion E : Exprs) {
            Result rsExp = E.GetCuadruplos(e, errores);
            Tipo tipoExp = E.getTipo();

            if (!tipoExp.IsUndefined()) {
                if (rsExp.getValor() != 0) {
                    int tmpCiclo;

                    switch (tipoExp.getTipo()) {
                        case CHAR:
                            codigo += rsExp.getCodigo();
                            codigo += "+, P, " + (rsExp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, stack, t0, t" + rsExp.getValor() + "\n";
                            codigo += "print(%c, t" + rsExp.getValor() + ")\n";
                            break;
                        case INTEGER:
                            codigo += rsExp.getCodigo();
                            codigo += "+, P, " + (rsExp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, stack, t0, t" + rsExp.getValor() + "\n";
                            codigo += "print(%e, t" + rsExp.getValor() + ")\n";
                            break;
                        case REAL:
                            codigo += rsExp.getCodigo();

                            codigo += "+, P, " + (rsExp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, stack, t0, t" + rsExp.getValor() + "\n";
                            codigo += "print(%d, t" + rsExp.getValor() + ")\n";
                            break;
                        case STRING:
                        case WORD:
                            codigo += rsExp.getCodigo();

                            codigo += "+, P, " + (rsExp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, stack, t0, t" + rsExp.getValor() + "\n";

                            result.setEtiquetaV(NuevaEtiqueta());
                            result.setEtiquetaF(NuevaEtiqueta());
                            String etqCiclo = NuevaEtiqueta();
                            tmpCiclo = NuevoTemporal();

                            codigo += etqCiclo + ":\n";
                            codigo += "=, heap, t" + rsExp.getValor() + ", t" + tmpCiclo + "\n";
                            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                            codigo += "je, t" + tmpCiclo + ", 0, " + result.getEtiquetaV() + "\n";
                            codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                            codigo += result.getEtiquetaF() + ":\n";
                            codigo += "print(%c, t" + tmpCiclo + ")\n";
                            codigo += "+, t" + rsExp.getValor() + ", 1, t" + rsExp.getValor() + "\n";
                            codigo += "jmp, , , " + etqCiclo + "\n";
                            codigo += result.getEtiquetaV() + ":\n";

                            break;
                        case BOOLEAN:
                            codigo += rsExp.getCodigo();

                            codigo += "+, P, " + (rsExp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, stack, t0, t" + rsExp.getValor() + "\n";

                            result.setEtiquetaV(NuevaEtiqueta());
                            result.setEtiquetaF(NuevaEtiqueta());
                            String etqSalida = NuevaEtiqueta();

                            codigo += "je, t" + rsExp.getValor() + ", 0, " + result.getEtiquetaV() + "\n";
                            codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                            codigo += result.getEtiquetaV() + ":\n";
                            //false
                            codigo += "print(%c, 70)\n";
                            codigo += "print(%c, 65)\n";
                            codigo += "print(%c, 76)\n";
                            codigo += "print(%c, 83)\n";
                            codigo += "print(%c, 69)\n";
                            codigo += "jmp, , , " + etqSalida + "\n";
                            codigo += result.getEtiquetaF() + ":\n";
                            //true
                            codigo += "print(%c, 84)\n";
                            codigo += "print(%c, 82)\n";
                            codigo += "print(%c, 85)\n";
                            codigo += "print(%c, 69)\n";
                            codigo += etqSalida + ":\n";
                            break;
                        case NIL:
                            codigo += rsExp.getCodigo();

                            codigo += "+, P, " + (rsExp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, stack, t0, t" + rsExp.getValor() + "\n";

                            codigo += "print(%c, 78)\n";
                            codigo += "print(%c, 73)\n";
                            codigo += "print(%c, 76)\n";
                            break;
                        case ENUM:
                            codigo += rsExp.getCodigo();
                            codigo += "+, P, " + (rsExp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, stack, t0, t" + rsExp.getValor() + "\n";

                            if (tipoExp.getLista() != null) {
                                String salidaIf = NuevaEtiqueta();
                                int contador = 0;

                                for (String id : tipoExp.getLista()) {

                                    result.setEtiquetaV(NuevaEtiqueta());
                                    result.setEtiquetaF(NuevaEtiqueta());

                                    codigo += "je, t" + rsExp.getValor() + ", " + contador + ", " + result.getEtiquetaV() + "\n";
                                    codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                                    codigo += result.getEtiquetaV() + ":\n";
                                    
                                    for(char c: id.toCharArray()){
                                        int valChar = c;
                                        codigo += "print(%c, " + valChar + ")\n";
                                    }
                                    
                                    codigo += "jmp, , , " + salidaIf + "\n";
                                    codigo += result.getEtiquetaF() + ":\n";
                                    contador++;
                                }
                                codigo += salidaIf + ":\n";
                            } else {
                                codigo += "print(%e, t" + rsExp.getValor() + ")\n";
                            }

                            break;
                        default:
                            errores.add(new ErrorC("Semántico", this.getLinea(), this.getColumna(), "Valor no se puede imprimir."));
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
