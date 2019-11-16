/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Rol;
import analizador.ast.entorno.Simbolo;
import analizador.ast.entorno.Tipo;
import analizador.ast.expresion.Acceso;
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
    private boolean Inicializacion;

    public Asignacion(Expresion Target, Expresion Valor, int Linea, int Columna) {
        super(Linea, Columna);
        this.Target = Target;
        this.Valor = Valor;
        this.Inicializacion = false;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
        Result result = new Result();
        String codigo = "";

        if (Target instanceof Identificador) {
            ((Identificador) Target).setAcceso(false);
        } else if (Target instanceof Atributo) {
            ((Atributo) Target).setAcceso(false);
        } else if (Target instanceof Acceso) {
            ((Acceso) Target).setAcceso(false);
        }

        Result rsTarget = Target.GetCuadruplos(e, errores);

        if (Target instanceof Identificador) {
            ((Identificador) Target).setAcceso(true);
        } else if (Target instanceof Atributo) {
            ((Atributo) Target).setAcceso(true);
        } else if (Target instanceof Acceso) {
            ((Acceso) Target).setAcceso(true);
        }

        if (rsTarget.getEstructura() != null) {

            if (rsTarget.getSimbolo().isConstante()) {
                if (!Inicializacion) {
                    errores.add(new ErrorC("Semántico", Linea, Columna, rsTarget.getSimbolo().getId() + " es una constante, no se puede cambiar el valor."));
                    result.setCodigo("");
                    return result;
                }
            }

            Result rsValor = Valor.GetCuadruplos(e, errores);

            if (!Valor.getTipo().IsUndefined()) {

                if (ValidarTipo(Target.getTipo(), Valor.getTipo())) {

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

                    //Vuelvo a acceder al target por si se cambio de ambito
                    codigo += "+, P, " + (rsTarget.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, stack, t0, t" + rsTarget.getValor() + "\n";

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

                        //meter lo de parametro
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
                        if (rsTarget.getSimbolo().getRol() == Rol.PARAMETER) {
                            if (rsTarget.getSimbolo().getTipoParam() == 0 && !(rsTarget.getSimbolo().getTipo().IsArray() || rsTarget.getSimbolo().getTipo().IsRecord())) {
                                int tmpDir = NuevoTemporal();
                                codigo += "=, stack, t" + rsTarget.getValor() + ", t" + tmpDir + "\n";
                                codigo += "+, P, " + (tmpDir - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpDir + ", stack\n";

                                int tmp = NuevoTemporal();
                                codigo += "+, P, " + (rsTarget.getSimbolo().getPos() + 1) + ", t" + tmp + "\n";
                                codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmp + ", stack\n";

                                int tmpEstruc = NuevoTemporal();
                                codigo += "=, stack, t" + tmp + ", t" + tmpEstruc + "\n";
                                codigo += "+, P, " + (tmpEstruc - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpEstruc + ", stack\n";

                                result.setEtiquetaV(NuevaEtiqueta());
                                result.setEtiquetaF(NuevaEtiqueta());
                                String etqSalida = NuevaEtiqueta();

                                codigo += "jne, t" + tmpEstruc + ", 0, " + result.getEtiquetaV() + "\n";
                                codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                                codigo += result.getEtiquetaF() + ":\n";
                                codigo += "=, t" + tmpDir + ", t" + rsValor.getValor() + ", stack\n";
                                codigo += "jmp, , , " + etqSalida + "\n";
                                codigo += result.getEtiquetaV() + ":\n";
                                codigo += "=, t" + tmpDir + ", t" + rsValor.getValor() + ", heap\n";
                                codigo += etqSalida + ":\n";

                            } else {
                                codigo += "=, t" + rsTarget.getValor() + ", t" + rsValor.getValor() + ", " + rsTarget.getEstructura() + "\n";
                            }
                        } else {
                            codigo += "=, t" + rsTarget.getValor() + ", t" + rsValor.getValor() + ", " + rsTarget.getEstructura() + "\n";
                        }
                    }
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "El valor de la expresión no corresponde al Tipo de la variable."));
                }
            }
        }

        result.setCodigo(codigo);
        return result;
    }

    private boolean ValidarTipo(Tipo target, Tipo valor) {
        if (target.IsEnum()) {
            if (valor.IsEnum()) {
                if (target.getIdEnum().equals(valor.getIdEnum())) {
                    return true;
                }
            }
        } else if (target.IsRecord()) {
            if (valor.IsInteger()) {
                return true;
            } else if (valor.IsRecord()) {
                if (target.getIdRecord() == valor.getIdRecord()) {
                    return true;
                }
            }
        } else if (target.IsArray()) {
            if (valor.IsArray()) {

                int dimTarget;
                int dimValor;
                //Validar Dimensiones
                if (Target instanceof Acceso) {
                    dimTarget = Target.getTipo().getDimensiones().size() - ((Acceso) Target).getNumAccesos();
                } else {
                    dimTarget = Target.getTipo().getDimensiones().size();
                }

                if (Valor instanceof Acceso) {
                    dimValor = Valor.getTipo().getDimensiones().size() - ((Acceso) Valor).getNumAccesos();
                } else {
                    dimValor = Valor.getTipo().getDimensiones().size();
                }

                if (dimTarget == dimValor) {
                    return ValidarTipo(target.getTipoArray(), valor.getTipoArray());
                }
            }
        } else {
            if (target.getTipo() == valor.getTipo()) {
                return true;
            } else {
                switch (target.getTipo()) {
                    case WORD:
                        if (valor.IsString()) {
                            return true;
                        }
                    case STRING:
                        if (valor.IsWord()) {
                            return true;
                        }
                    case REAL:
                        if (valor.IsChar() || valor.IsInteger()) {
                            return true;
                        }
                    case INTEGER:
                        if (valor.IsChar()) {
                            return true;
                        }
                    case CHAR:
                        if (valor.IsInteger()) {
                            return true;
                        }
                }
            }
        }
        return false;
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

    /**
     * @return the Inicializacion
     */
    public boolean isInicializacion() {
        return Inicializacion;
    }

    /**
     * @param Inicializacion the Inicializacion to set
     */
    public void setInicializacion(boolean Inicializacion) {
        this.Inicializacion = Inicializacion;
    }

}
