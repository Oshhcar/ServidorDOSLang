/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.expresion.operacion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Type;
import analizador.ast.expresion.Acceso;
import analizador.ast.expresion.Atributo;
import analizador.ast.expresion.Call;
import analizador.ast.expresion.Expresion;
import analizador.ast.expresion.Identificador;
import analizador.ast.expresion.Literal;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Logica extends Operacion {

    private boolean Evaluar;

    public Logica(Expresion Op1, Expresion Op2, Operador op, int Linea, int Columna) {
        super(Op1, Op2, op, Linea, Columna);
        Evaluar = false;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";

        if (Op1 instanceof Relacional) {
            ((Relacional) Op1).setCortoCircuito(true);
        } else if (Op1 instanceof Logica) {
            ((Logica) Op1).setEvaluar(true);
        } else if (Op1 instanceof Unario) {
            ((Unario) Op1).setEvaluar(true);
        }

        if (Op2 instanceof Relacional) {
            ((Relacional) Op2).setCortoCircuito(true);
        } else if (Op2 instanceof Logica) {
            ((Logica) Op2).setEvaluar(true);
        } else if (Op2 instanceof Unario) {
            ((Unario) Op2).setEvaluar(true);
        }

        Result rsOp1 = Op1.GetCuadruplos(e, errores);
        Result rsOp2 = Op2.GetCuadruplos(e, errores);

        if (Op1.getTipo().IsBoolean() && Op2.getTipo().IsBoolean()) {
            Tipo.setTipo(Type.BOOLEAN);

            if (Op1 instanceof Literal || Op1 instanceof Call || Op1 instanceof Identificador
                    || Op1 instanceof Acceso || Op1 instanceof Atributo) {
                String cod = rsOp1.getCodigo();

                rsOp1.setEtiquetaV(NuevaEtiqueta());
                rsOp1.setEtiquetaF(NuevaEtiqueta());

                cod += "je, t" + rsOp1.getValor() + ", 1, " + rsOp1.getEtiquetaF() + "\n";
                cod += "jmp, , , " + rsOp1.getEtiquetaV() + "\n";

                rsOp1.setEtiquetaV(rsOp1.getEtiquetaV() + ":\n");
                rsOp1.setEtiquetaF(rsOp1.getEtiquetaF() + ":\n");

                rsOp1.setCodigo(cod);
            }

            if (Op2 instanceof Literal || Op2 instanceof Call || Op2 instanceof Identificador
                    || Op2 instanceof Acceso || Op2 instanceof Atributo) {
                String cod = rsOp2.getCodigo();

                rsOp2.setEtiquetaV(NuevaEtiqueta());
                rsOp2.setEtiquetaF(NuevaEtiqueta());

                cod += "je, t" + rsOp2.getValor() + ", 1, " + rsOp2.getEtiquetaF() + "\n";
                cod += "jmp, , , " + rsOp2.getEtiquetaV() + "\n";

                rsOp2.setEtiquetaV(rsOp2.getEtiquetaV() + ":\n");
                rsOp2.setEtiquetaF(rsOp2.getEtiquetaF() + ":\n");

                rsOp2.setCodigo(cod);
            }

            switch (Op) {
                case AND:
                    codigo += rsOp1.getCodigo();

                    if (Op1 instanceof Relacional || Op1 instanceof Literal || Op1 instanceof Call 
                            || Op1 instanceof Identificador || Op1 instanceof Acceso || Op1 instanceof Atributo) {
                        codigo += rsOp1.getEtiquetaF();
                        rsOp1.setEtiquetaF(rsOp1.getEtiquetaV());
                        rsOp1.setEtiquetaV(null);
                    } else if (Op1 instanceof Logica || Op1 instanceof Unario) {
                        codigo += rsOp1.getEtiquetaV();
                        rsOp1.setEtiquetaV(null);
                    }

                    codigo += rsOp2.getCodigo();

                    if (Op2 instanceof Relacional || Op2 instanceof Literal || Op2 instanceof Call 
                            || Op2 instanceof Identificador || Op2 instanceof Acceso || Op2 instanceof Atributo) {
                        String copy = rsOp2.getEtiquetaV();
                        rsOp2.setEtiquetaV(rsOp2.getEtiquetaF());
                        rsOp2.setEtiquetaF(copy);
                    }

                    codigo += Comparar(result, rsOp1, rsOp2, e);
                    break;
                case OR:
                    codigo += rsOp1.getCodigo();

                    if (Op1 instanceof Relacional || Op1 instanceof Literal || Op1 instanceof Call 
                            || Op1 instanceof Identificador || Op1 instanceof Acceso || Op1 instanceof Atributo) {
                        codigo += rsOp1.getEtiquetaV();
                        rsOp1.setEtiquetaV(rsOp1.getEtiquetaF());
                        rsOp1.setEtiquetaF(null);
                    } else if (Op1 instanceof Logica || Op1 instanceof Unario) {
                        codigo += rsOp1.getEtiquetaF();
                        rsOp1.setEtiquetaF(null);
                    }

                    codigo += rsOp2.getCodigo();

                    if (Op2 instanceof Relacional || Op2 instanceof Literal || Op2 instanceof Call 
                            || Op2 instanceof Identificador || Op2 instanceof Acceso || Op2 instanceof Atributo) {
                        String copy = rsOp2.getEtiquetaV();
                        rsOp2.setEtiquetaV(rsOp2.getEtiquetaF());
                        rsOp2.setEtiquetaF(copy);
                    }

                    codigo += Comparar(result, rsOp1, rsOp2, e);

                    break;
                case NAND:
                    codigo += rsOp1.getCodigo();

                    if (Op1 instanceof Relacional || Op1 instanceof Literal || Op1 instanceof Call 
                            || Op1 instanceof Identificador || Op1 instanceof Acceso || Op1 instanceof Atributo) {
                        codigo += rsOp1.getEtiquetaF();
                        rsOp1.setEtiquetaF(rsOp1.getEtiquetaV());
                        rsOp1.setEtiquetaV(null);
                    } else if (Op1 instanceof Logica || Op1 instanceof Unario) {
                        codigo += rsOp1.getEtiquetaV();
                        rsOp1.setEtiquetaV(null);
                    }

                    codigo += rsOp2.getCodigo();

                    if (Op2 instanceof Relacional || Op2 instanceof Literal || Op2 instanceof Call 
                            || Op2 instanceof Identificador || Op2 instanceof Acceso || Op2 instanceof Atributo) {
                        String copy = rsOp2.getEtiquetaV();
                        rsOp2.setEtiquetaV(rsOp2.getEtiquetaF());
                        rsOp2.setEtiquetaF(copy);
                    }

                    codigo += CompararNot(result, rsOp1, rsOp2, e);
                    break;
                case NOR:
                    codigo += rsOp1.getCodigo();

                    if (Op1 instanceof Relacional || Op1 instanceof Literal || Op1 instanceof Call 
                            || Op1 instanceof Identificador || Op1 instanceof Acceso || Op1 instanceof Atributo) {
                        codigo += rsOp1.getEtiquetaV();
                        rsOp1.setEtiquetaV(rsOp1.getEtiquetaF());
                        rsOp1.setEtiquetaF(null);
                    } else if (Op1 instanceof Logica || Op1 instanceof Unario) {
                        codigo += rsOp1.getEtiquetaF();
                        rsOp1.setEtiquetaF(null);
                    }

                    codigo += rsOp2.getCodigo();

                    if (Op2 instanceof Relacional || Op2 instanceof Literal || Op2 instanceof Call 
                            || Op2 instanceof Identificador || Op2 instanceof Acceso || Op2 instanceof Atributo) {
                        String copy = rsOp2.getEtiquetaV();
                        rsOp2.setEtiquetaV(rsOp2.getEtiquetaF());
                        rsOp2.setEtiquetaF(copy);
                    }

                    codigo += CompararNot(result, rsOp1, rsOp2, e);

                    break;
            }

        } else {
            errores.add(new ErrorC("Semántico", Linea, Columna, "Error de tipos en operación lógica."));
        }

        result.setCodigo(codigo);
        return result;
    }

    private String CompararNot(Result result, Result rsOp1, Result rsOp2, Entorno e) {
        String codigo = "";
        if (!Evaluar) {

            result.setValor(NuevoTemporal());
            String etqSalida = NuevaEtiqueta();

            if (rsOp1.getEtiquetaV() != null) {
                codigo += rsOp1.getEtiquetaV();
            }

            if (rsOp2.getEtiquetaV() != null) {
                codigo += rsOp2.getEtiquetaV();
            }

            codigo += "=, 0, , t" + result.getValor() + "\n";
            codigo += "jmp, , , " + etqSalida + "\n";

            if (rsOp1.getEtiquetaF() != null) {
                codigo += rsOp1.getEtiquetaF();
            }

            if (rsOp2.getEtiquetaF() != null) {
                codigo += rsOp2.getEtiquetaF();
            }

            codigo += "=, 1, , t" + result.getValor() + "\n";
            codigo += etqSalida + ":\n";
            codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + result.getValor() + ", stack\n";

        } else {
            result.setEtiquetaV("");
            result.setEtiquetaF("");

            if (rsOp1.getEtiquetaV() != null) {
                result.setEtiquetaF(result.getEtiquetaF() + rsOp1.getEtiquetaV());
            }
            if (rsOp1.getEtiquetaF() != null) {
                result.setEtiquetaV(result.getEtiquetaV() + rsOp1.getEtiquetaF());
            }
            if (rsOp2.getEtiquetaV() != null) {
                result.setEtiquetaF(result.getEtiquetaF() + rsOp2.getEtiquetaV());
            }
            if (rsOp2.getEtiquetaF() != null) {
                result.setEtiquetaV(result.getEtiquetaV() + rsOp2.getEtiquetaF());
            }
        }
        return codigo;
    }

    private String Comparar(Result result, Result rsOp1, Result rsOp2, Entorno e) {
        String codigo = "";
        if (!Evaluar) {

            result.setValor(NuevoTemporal());
            String etqSalida = NuevaEtiqueta();

            if (rsOp1.getEtiquetaV() != null) {
                codigo += rsOp1.getEtiquetaV();
            }

            if (rsOp2.getEtiquetaV() != null) {
                codigo += rsOp2.getEtiquetaV();
            }

            codigo += "=, 1, , t" + result.getValor() + "\n";
            codigo += "jmp, , , " + etqSalida + "\n";

            if (rsOp1.getEtiquetaF() != null) {
                codigo += rsOp1.getEtiquetaF();
            }

            if (rsOp2.getEtiquetaF() != null) {
                codigo += rsOp2.getEtiquetaF();
            }

            codigo += "=, 0, , t" + result.getValor() + "\n";
            codigo += etqSalida + ":\n";
            codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + result.getValor() + ", stack\n";

        } else {
            result.setEtiquetaV("");
            result.setEtiquetaF("");

            if (rsOp1.getEtiquetaV() != null) {
                result.setEtiquetaV(result.getEtiquetaV() + rsOp1.getEtiquetaV());
            }
            if (rsOp1.getEtiquetaF() != null) {
                result.setEtiquetaF(result.getEtiquetaF() + rsOp1.getEtiquetaF());
            }
            if (rsOp2.getEtiquetaV() != null) {
                result.setEtiquetaV(result.getEtiquetaV() + rsOp2.getEtiquetaV());
            }
            if (rsOp2.getEtiquetaF() != null) {
                result.setEtiquetaF(result.getEtiquetaF() + rsOp2.getEtiquetaF());
            }
        }
        return codigo;
    }

    /**
     * @return the Evaluar
     */
    public boolean isEvaluar() {
        return Evaluar;
    }

    /**
     * @param Evaluar the Evaluar to set
     */
    public void setEvaluar(boolean Evaluar) {
        this.Evaluar = Evaluar;
    }

}
