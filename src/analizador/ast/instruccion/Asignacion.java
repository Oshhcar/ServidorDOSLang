/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion;

import analizador.ErrorC;
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
public class Asignacion extends Instruccion {

    private Expresion Target;
    private Expresion Valor;

    public Asignacion(Expresion Target, Expresion Valor, int Linea, int Columna) {
        super(Linea, Columna);
        this.Target = Target;
        this.Valor = Valor;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
        Result result = new Result();
        String codigo = "";

        if (Target instanceof Identificador) {
            ((Identificador) Target).setAcceso(false);
        } else if (Target instanceof Atributo) {
            ((Atributo) Target).setAcceso(false);
        }

        Result rsTarget = Target.GetCuadruplos(e, errores);

        if (Target instanceof Identificador) {
            ((Identificador) Target).setAcceso(true);
        } else if (Target instanceof Atributo) {
            ((Atributo) Target).setAcceso(true);
        }

        if (rsTarget.getEstructura() != null) {
            Result rsValor = Valor.GetCuadruplos(e, errores);

            if (!Valor.getTipo().IsUndefined()) {
                boolean bandera = false;

                if (Target.getTipo().IsEnum()) {
                    if (Valor.getTipo().IsEnum()) {
                        if (Target.getTipo().getIdEnum().equals(Valor.getTipo().getIdEnum())) {
                            bandera = true;
                        }
                    }
                } else if (Target.getTipo().IsRecord()) {
                    if (Valor.getTipo().IsNumeric() || Valor.getTipo().IsRecord()) {
                        bandera = true;
                    }
                } else {
                    if (Target.getTipo().getTipo() == Valor.getTipo().getTipo()) {
                        bandera = true;
                    } else {
                        switch (Target.getTipo().getTipo()) {
                            case WORD:
                                if (Valor.getTipo().IsString()) {
                                    bandera = true;
                                }
                                break;
                            case STRING:
                                if (Valor.getTipo().IsWord()) {
                                    bandera = true;
                                }
                                break;
                            case REAL:
                                if (Valor.getTipo().IsChar() || Valor.getTipo().IsInteger()) {
                                    bandera = true;
                                }
                                break;
                            case INTEGER:
                                if (Valor.getTipo().IsChar()) {
                                    bandera = true;
                                }
                                break;
                        }
                    }
                }

                if (bandera) {

                    /*Si target es record, defino los simbolos de sus atributos si hay record*/
                    if (Target.getTipo().IsRecord()) {
                        if (rsTarget.getSimbolo().getEntorno() != null) {
                            for (Simbolo s : rsTarget.getSimbolo().getEntorno().getSimbolos()) {
                                if (s.getTipo().IsRecord()) {
                                    s.setEntorno(new Entorno(s.getId()));
                                    s.getTipo().getEntorno().getSimbolos().forEach((sim) -> {
                                        s.getEntorno().Add(new Simbolo(sim.getId(), sim.getTipo(), sim.getPos(), s.getId(), s));
                                    });
                                }
                            }
                        }
                    }

                    codigo += rsTarget.getCodigo();
                    codigo += rsValor.getCodigo();

                    Expresion limiteInf = Target.getTipo().getLimiteInf();
                    Expresion limiteSup = Target.getTipo().getLimiteSup();
                    if (limiteInf != null && limiteSup != null) {
                        Result rsLimiteInf = limiteInf.GetCuadruplos(e, errores);
                        Result rsLimiteSup = limiteSup.GetCuadruplos(e, errores);

                        codigo += rsLimiteInf.getCodigo();
                        codigo += rsLimiteSup.getCodigo();

                        rsLimiteInf.setEtiquetaV(NuevaEtiqueta());
                        rsLimiteInf.setEtiquetaF(NuevaEtiqueta());

                        codigo += "jge, t" + rsValor.getValor() + ", t" + rsLimiteInf.getValor() + ", " + rsLimiteInf.getEtiquetaV() + "\n";
                        codigo += "jmp, , , " + rsLimiteInf.getEtiquetaF() + "\n";

                        rsLimiteSup.setEtiquetaV(NuevaEtiqueta());
                        rsLimiteSup.setEtiquetaF(NuevaEtiqueta());
                        codigo += rsLimiteInf.getEtiquetaV() + ":\n";
                        codigo += "jle, t" + rsValor.getValor() + ", t" + rsLimiteSup.getValor() + ", " + rsLimiteSup.getEtiquetaV() + "\n";
                        codigo += "jmp, , , " + rsLimiteSup.getEtiquetaF() + "\n";
                        codigo += rsLimiteSup.getEtiquetaV() + ":\n";

                        codigo += "=, t" + rsTarget.getValor() + ", t" + rsValor.getValor() + ", " + rsTarget.getEstructura() + "\n";

                        String etqSalida = NuevaEtiqueta();
                        codigo += "jmp, , , " + etqSalida + "\n";
                        codigo += rsLimiteInf.getEtiquetaF() + ":\n";
                        codigo += rsLimiteSup.getEtiquetaF() + ":\n";

                        codigo += "print(%c, 70)\n"
                                + "print(%c, 117)\n"
                                + "print(%c, 101)\n"
                                + "print(%c, 114)\n"
                                + "print(%c, 97)\n"
                                + "print(%c, 32)\n"
                                + "print(%c, 100)\n"
                                + "print(%c, 101)\n"
                                + "print(%c, 32)\n"
                                + "print(%c, 114)\n"
                                + "print(%c, 97)\n"
                                + "print(%c, 110)\n"
                                + "print(%c, 103)\n"
                                + "print(%c, 111)\n"
                                + "print(%c, 10)\n";

                        codigo += etqSalida + ":\n";

                    } else {
                        codigo += "=, t" + rsTarget.getValor() + ", t" + rsValor.getValor() + ", " + rsTarget.getEstructura() + "\n";
                    }
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "El valor de la expresión no corresponde al Tipo de la variable."));
                }
            }

        }

        result.setCodigo(codigo);
        return result;
    }

    /**
     * @return the Target
     */
    public Expresion getTarget() {
        return Target;
    }

    /**
     * @param Target the Target to set
     */
    public void setTarget(Expresion Target) {
        this.Target = Target;
    }

    /**
     * @return the Valor
     */
    public Expresion getValor() {
        return Valor;
    }

    /**
     * @param Valor the Valor to set
     */
    public void setValor(Expresion Valor) {
        this.Valor = Valor;
    }

}
