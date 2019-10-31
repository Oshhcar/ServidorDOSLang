/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.expresion.Expresion;
import analizador.ast.expresion.Identificador;
import analizador.ast.expresion.Literal;
import analizador.ast.expresion.operacion.Operacion;
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
        }

        Result rsTarget = Target.GetCuadruplos(e, errores);

        if (Target instanceof Identificador) {
            ((Identificador) Target).setAcceso(true);
        }

        if (rsTarget.getEstructura() != null) {
            Result rsValor = Valor.GetCuadruplos(e, errores);

            if (!Valor.getTipo().IsUndefined()) {
                boolean bandera = false;

                if (Valor instanceof Literal || Valor instanceof Operacion) {
                    if (Target.getTipo().getTipo() == Valor.getTipo().getTipo()) {
                        bandera = true;
                    } else {
                        //Hacer cast
                        //Solo va a hacer el cast si es literal o operacion (lo demás verifica el tipo)
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
                } else {
                    if (Target.getTipo().equals(Valor.getTipo())) {
                        bandera = true;
                    }
                }

                if (bandera) {
                    codigo += rsTarget.getCodigo();
                    codigo += rsValor.getCodigo();

                    codigo += "=, t" + rsTarget.getValor() + ", t" + rsValor.getValor() + ", " + rsTarget.getEstructura() + "\n";

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
