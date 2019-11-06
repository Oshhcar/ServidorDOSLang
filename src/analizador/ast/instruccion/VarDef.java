/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion;

import analizador.ErrorC;
import analizador.ast.entorno.Dimension;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Rol;
import analizador.ast.entorno.Simbolo;
import analizador.ast.entorno.Tipo;
import analizador.ast.entorno.Type;
import analizador.ast.expresion.Expresion;
import analizador.ast.expresion.Identificador;
import analizador.ast.expresion.Literal;
import analizador.ast.expresion.operacion.Aritmetica;
import analizador.ast.expresion.operacion.Operador;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class VarDef extends Instruccion {

    private ArrayList<String> Id;
    private Tipo Tipo;
    private Expresion Expr;
    private boolean Constante;

    public VarDef(ArrayList<String> Id, Tipo Tipo, int Linea, int Columna) {
        super(Linea, Columna);
        this.Id = Id;
        this.Tipo = Tipo;
        this.Expr = null;
        this.Constante = false;
    }

    public VarDef(ArrayList<String> Id, Tipo Tipo, Expresion Expr, int Linea, int Columna) {
        super(Linea, Columna);
        this.Id = Id;
        this.Tipo = Tipo;
        this.Expr = Expr;
        this.Constante = false;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
        Result result = new Result();
        String codigo = "";

        //Si es un tipo definido
        if (Tipo.getId() != null) {
            Simbolo type = e.Get(Tipo.getId());
            if (type == null) {
                errores.add(new ErrorC("Semántico", Linea, Columna, "No se ha definido un tipo con el id: " + Tipo.getId() + "."));
                return null;
            } else {
                if (type.getRol() == Rol.TYPE) {
                    Tipo.setId(Tipo.getId().toLowerCase());
                    Tipo.setTipoPadre(type.getTipo());
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, Tipo.getId() + " no es un tipo."));
                    return null;
                }
            }
        } else {
            if (Tipo.getLimiteInf() != null && Tipo.getLimiteSup() != null) {
                Tipo.getLimiteInf().GetCuadruplos(e, errores);
                Tipo.getLimiteSup().GetCuadruplos(e, errores);

                if (Tipo.getLimiteInf().getTipo().IsNumeric() && Tipo.getLimiteSup().getTipo().IsNumeric()) {
                    if (Tipo.getLimiteInf().getTipo().getTipo() == Tipo.getLimiteSup().getTipo().getTipo()) {
                        Tipo.setTipo(Tipo.getLimiteInf().getTipo().getTipo());
                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "El tipo del límite inferior no coincide con el del límite superior."));
                        return null;
                    }
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "El tipo subrango solo acepta tipos numéricos y carácteres."));
                    return null;
                }
            } else if (Tipo.getVariables() != null) {
                Tipo.setEntorno(new Entorno("record", e));
                Tipo.getVariables().forEach((variable) -> {
                    variable.GetCuadruplos(Tipo.getEntorno(), errores, global);
                });
                Tipo.getEntorno().setSize(Tipo.getEntorno().getPos());
                Tipo.getEntorno().setPadre(null);
            }
        }

        for (String id : Id) {
            if (e.GetLocal(id) == null) {
                Simbolo s = new Simbolo(id, Tipo, e.getPos(), e.getAmbito());
                s.setConstante(Constante);

                if (Tipo.IsRecord()) {
                    s.setEntorno(new Entorno(id));
                    Tipo.getEntorno().getSimbolos().forEach((sim) -> {
                        s.getEntorno().Add(new Simbolo(sim.getId(), sim.getTipo(), sim.getPos(), id, s));
                    });

                }

                //Si es arreglo lo instancio
                if (Tipo.IsArray()) {

                    //Si es record guardo simbolos
                    if (Tipo.getTipoArray().IsRecord()) {
                        s.setEntorno(new Entorno(id));
                        Tipo.getTipoArray().getEntorno().getSimbolos().forEach((sim) -> {
                            s.getEntorno().Add(new Simbolo(sim.getId(), sim.getTipo(), sim.getPos(), id, s));
                        });
                    }

                    int tmp = NuevoTemporal();
                    codigo += "+, P, " + s.getPos() + ", t" + tmp + "\n";
                    codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, t0, t" + tmp + ", stack\n";

                    codigo += "=, t" + tmp + ", H, stack\n";

                    codigo += LlenarDimension(0, e, errores);
                    //codigo += "+, H, t" + rsSuma.getValor() + ", H\n"; //reservo memoria
                }

                e.Add(s);
                //global.Add(s);
            } else {
                errores.add(new ErrorC("Semántico", Linea, Columna, "Ya se ha definido una variable con el id: " + id + "."));
            }
        }

        if (Expr != null) {
            for (String id : Id) {
                Identificador target = new Identificador(id, Linea, Columna);
                Asignacion asigna = new Asignacion(target, Expr, Linea, Columna);

                codigo += asigna.GetCuadruplos(e, errores, global).getCodigo();
            }
        }

        result.setCodigo(codigo);
        return result;
    }

    public String LlenarDimension(int pos, Entorno e, ArrayList<ErrorC> errores) {
        String codigo = "";

        Dimension dim = Tipo.getDimensiones().get(pos);

        //Cálculo su tamaño
        Aritmetica suma = new Aritmetica(new Aritmetica(dim.getLimiteSup(), dim.getLimiteInf(), Operador.RESTA, Linea, Columna), new Literal(new Tipo(Type.INTEGER), 1, Linea, Columna), Operador.SUMA, Linea, Columna);
        Result rsSuma = suma.GetCuadruplos(e, errores);

        //Guardo el tamaño en su primera posicion
        codigo += rsSuma.getCodigo();
        codigo += "=, H, t" + rsSuma.getValor() + ", heap\n";
        codigo += "+, H, 1, H\n";

        //Guardo el limite inf en su segunda posicion
        Result rsInf = dim.getLimiteInf().GetCuadruplos(e, errores);
        codigo += rsInf.getCodigo();
        codigo += "=, H, t" + rsInf.getValor() + ", heap\n";
        codigo += "+, H, 1, H\n";

        //Guardo el limite sup en su tercera posicion
        Result rsSup = dim.getLimiteSup().GetCuadruplos(e, errores);
        codigo += rsSup.getCodigo();
        codigo += "=, H, t" + rsSup.getValor() + ", heap\n";
        codigo += "+, H, 1, H\n";

        int contador = NuevoTemporal();
        codigo += "=, 0, , t" + contador + "\n";
        codigo += "+, P, " + (contador - e.getTmpInicio() + e.getSize()) + ", t0\n";
        codigo += "=, t0, t" + contador + ", stack\n";

        int tmpInicio = 0;
        //Si tiene más dimensiones reservo todo y guardo el inicio
        if ((Tipo.getDimensiones().size() - 1) != pos) {
            tmpInicio = NuevoTemporal();
            codigo += "=, H, , t" + tmpInicio + "\n";
            codigo += "+, P, " + (tmpInicio - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmpInicio + ", stack\n";

            codigo += "+, H, t" + rsSuma.getValor() + ", H\n"; //reservo el espacio del a primera dim
        }

        String etqV = NuevaEtiqueta();
        String etqF = NuevaEtiqueta();
        String etqCiclo = NuevaEtiqueta();

        codigo += etqCiclo + ":\n";
        codigo += "+, P, " + (contador - e.getTmpInicio() + e.getSize()) + ", t0\n";
        codigo += "=, stack, t0, t" + contador + "\n";
        codigo += "jge, t" + contador + ", t" + rsSuma.getValor() + ", " + etqV + "\n";
        codigo += "jmp, , , " + etqF + "\n";
        codigo += etqF + ":\n";

        if ((Tipo.getDimensiones().size() - 1) == pos) {
            //codigo += "=, H, 0, heap\n";
            codigo += "+, H, 1, H\n";
        } else {

            codigo += "+, P, " + (tmpInicio - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + tmpInicio + "\n";
            codigo += "=, t" + tmpInicio + ", H, heap\n";
            codigo += "+, t" + tmpInicio + ", 1, t" + tmpInicio + "\n";
            codigo += "+, P, " + (tmpInicio - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmpInicio + ", stack\n";

            codigo += LlenarDimension(pos + 1, e, errores);

        }

        codigo += "+, t" + contador + ", 1, t" + contador + "\n";
        codigo += "+, P, " + (contador - e.getTmpInicio() + e.getSize()) + ", t0\n";
        codigo += "=, t0, t" + contador + ", stack\n";
        codigo += "jmp, , , " + etqCiclo + "\n";
        codigo += etqV + ":\n";

        return codigo;
    }

    /**
     * @return the Id
     */
    public ArrayList<String> getId() {
        return Id;
    }

    /**
     * @param Id the Id to set
     */
    public void setId(ArrayList<String> Id) {
        this.Id = Id;
    }

    /**
     * @return the Tipo
     */
    public Tipo getTipo() {
        return Tipo;
    }

    /**
     * @param Tipo the Tipo to set
     */
    public void setTipo(Tipo Tipo) {
        this.Tipo = Tipo;
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
     * @return the Constante
     */
    public boolean isConstante() {
        return Constante;
    }

    /**
     * @param Constante the Constante to set
     */
    public void setConstante(boolean Constante) {
        this.Constante = Constante;
    }

}
