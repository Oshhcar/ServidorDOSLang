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
import analizador.ast.entorno.Tipo;
import analizador.ast.expresion.Expresion;
import analizador.ast.expresion.Literal;
import analizador.ast.expresion.operacion.Operacion;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class VarDef extends Instruccion {

    private ArrayList<String> Id;
    private Tipo Tipo;
    private Expresion Expr;

    public VarDef(ArrayList<String> Id, Tipo Tipo, int Linea, int Columna) {
        super(Linea, Columna);
        this.Id = Id;
        this.Tipo = Tipo;
        this.Expr = null;
    }

    public VarDef(ArrayList<String> Id, Tipo Tipo, Expresion Expr, int Linea, int Columna) {
        super(Linea, Columna);
        this.Id = Id;
        this.Tipo = Tipo;
        this.Expr = Expr;
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
                Tipo.setTipoPadre(type.getTipo());
            }
        }

        if (Expr != null) {
            Result rsExpr = Expr.GetCuadruplos(e, errores);

            if (!Expr.getTipo().IsUndefined()) {
                boolean bandera = false;

                if (Expr instanceof Literal || Expr instanceof Operacion) {
                    if (Tipo.getTipo() == Expr.getTipo().getTipo()) {
                        bandera = true;
                    } else {
                        //Hacer cast
                        //Solo va a hacer el cast si es literal o operacion (lo demás verifica el tipo)
                        switch (Tipo.getTipo()) {
                            case WORD:
                                if (Expr.getTipo().IsString()) {
                                    bandera = true;
                                }
                                break;
                            case STRING:
                                if (Expr.getTipo().IsWord()) {
                                    bandera = true;
                                }
                                break;
                            case REAL:
                                if (Expr.getTipo().IsChar() || Expr.getTipo().IsInteger()) {
                                    bandera = true;
                                }
                                break;
                            case INTEGER:
                                if (Expr.getTipo().IsChar()) {
                                    bandera = true;
                                }
                                break;
                        }
                    }
                } else {
                    if (Tipo.equals(Expr.getTipo())) {
                        bandera = true;
                    }
                }
                
                if (bandera) {
                    codigo += rsExpr.getCodigo();
                    result.setValor(rsExpr.getValor());
                }
                else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "El valor de la expresión no corresponde al Tipo."));
                    return null;
                }
            }
        }
        
        for(String id: Id){
            if(e.Get(id) == null){
                Simbolo s = new Simbolo(id, Tipo, e.getPos(), e.getAmbito());
                
                if(result.getValor() > 0){
                    int tmp = NuevoTemporal();
                    codigo += "+, P, " + s.getPos() + ", t" + tmp + "\n";
                    codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, t0, t" + tmp + ", stack\n";
                    codigo += "=, t" + tmp + ", t" + result.getValor()+", stack\n";
                }
                
                e.Add(s);
                global.Add(s);
            } else {
               errores.add(new ErrorC("Semántico", Linea, Columna, "Ya se ha definido una variable con el id: " + id + "."));
            }
        }

        result.setCodigo(codigo);
        return result;
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

}
