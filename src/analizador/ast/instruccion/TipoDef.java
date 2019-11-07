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
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class TipoDef extends Instruccion {

    private ArrayList<String> Id;
    private Tipo Tipo;

    public TipoDef(ArrayList<String> Id, Tipo Tipo, int Linea, int Columna) {
        super(Linea, Columna);
        this.Id = Id;
        this.Tipo = Tipo;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
        boolean guardado = false;

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
                guardado = true;
                //Guardo Tipos antes por si hace referencia a el mismo
                Id.forEach((id) -> {
                    if (e.GetLocal(id) == null) {
                        e.Add(new Simbolo(id, Tipo, e.getAmbito()));
                        //global.Add(new Simbolo(id, Tipo, e.getAmbito()));
                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "Ya se ha definido una variable con el id: " + id + "."));
                    }
                });

                Tipo.setIdRecord(this.hashCode());
                Tipo.setEntorno(new Entorno("record", e));
                Tipo.getVariables().forEach((variable) -> {
                    variable.GetCuadruplos(Tipo.getEntorno(), errores, global);
                });
                Tipo.getEntorno().setSize(Tipo.getEntorno().getPos());
                Tipo.getEntorno().setPadre(null);
            } else if (Tipo.getDimensiones() != null) {
                
                while (Tipo.getTipoArray().IsArray()) {
                    Tipo.getDimensiones().addAll(Tipo.getTipoArray().getDimensiones());
                    Tipo.setTipoArray(Tipo.getTipoArray().getTipoArray());
                }

                //Verifico el tipoArray
                if (Tipo.getTipoArray().getId() != null) {
                    Simbolo type = e.Get(Tipo.getTipoArray().getId());
                    if (type == null) {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "No se ha definido un tipo con el id: " + Tipo.getTipoArray().getId() + "."));
                        return null;
                    } else {
                        if (type.getRol() == Rol.TYPE) {

                            if (type.getTipo().IsArray()) {
                                Tipo tipSim = type.getTipo();

                                while (tipSim.IsArray()) {
                                    Tipo.getDimensiones().addAll(tipSim.getDimensiones());
                                    tipSim = type.getTipo().getTipoArray();
                                }
                                Tipo.getTipoArray().setId(Tipo.getTipoArray().getId().toLowerCase());
                                Tipo.getTipoArray().setTipoPadre(tipSim);
                            } else {
                                Tipo.getTipoArray().setId(Tipo.getTipoArray().getId().toLowerCase());
                                Tipo.getTipoArray().setTipoPadre(type.getTipo());
                            }
                        } else {
                            errores.add(new ErrorC("Semántico", Linea, Columna, Tipo.getTipoArray().getId() + " no es un tipo."));
                            return null;
                        }
                    }
                } else {
                    if (Tipo.getTipoArray().getLimiteInf() != null && Tipo.getTipoArray().getLimiteSup() != null) {
                        Tipo.getTipoArray().getLimiteInf().GetCuadruplos(e, errores);
                        Tipo.getTipoArray().getLimiteSup().GetCuadruplos(e, errores);

                        if (Tipo.getTipoArray().getLimiteInf().getTipo().IsNumeric() && Tipo.getTipoArray().getLimiteSup().getTipo().IsNumeric()) {
                            if (Tipo.getTipoArray().getLimiteInf().getTipo().getTipo() == Tipo.getTipoArray().getLimiteSup().getTipo().getTipo()) {
                                Tipo.getTipoArray().setTipo(Tipo.getTipoArray().getLimiteInf().getTipo().getTipo());
                            } else {
                                errores.add(new ErrorC("Semántico", Linea, Columna, "El tipo del límite inferior no coincide con el del límite superior."));
                                return null;
                            }
                        } else {
                            errores.add(new ErrorC("Semántico", Linea, Columna, "El tipo subrango solo acepta tipos numéricos y carácteres."));
                            return null;
                        }
                    } else if (Tipo.getTipoArray().getVariables() != null) {
                        guardado = true;
                        //Guardo Tipos antes por si hace referencia a el mismo
                        Id.forEach((id) -> {
                            if (e.GetLocal(id) == null) {
                                e.Add(new Simbolo(id, Tipo, e.getAmbito()));
                                //global.Add(new Simbolo(id, Tipo, e.getAmbito()));
                            } else {
                                errores.add(new ErrorC("Semántico", Linea, Columna, "Ya se ha definido una variable con el id: " + id + "."));
                            }
                        });

                        Tipo.getTipoArray().setIdRecord(this.hashCode());
                        Tipo.getTipoArray().setEntorno(new Entorno("record", e));
                        Tipo.getTipoArray().getVariables().forEach((variable) -> {
                            variable.GetCuadruplos(Tipo.getTipoArray().getEntorno(), errores, global);
                        });
                        Tipo.getTipoArray().getEntorno().setSize(Tipo.getTipoArray().getEntorno().getPos());
                        Tipo.getTipoArray().getEntorno().setPadre(null);
                    }
                }

                for (Dimension dimension : Tipo.getDimensiones()) {
                    dimension.getLimiteInf().GetCuadruplos(e, errores);
                    dimension.getLimiteSup().GetCuadruplos(e, errores);

                    if (!dimension.getLimiteInf().getTipo().IsInteger()) {
                        if (!dimension.getLimiteInf().getTipo().IsChar()) {
                            errores.add(new ErrorC("Semántico", Linea, Columna, "La dimensión debe ser integer."));
                            return null;
                        }
                    }

                    if (!dimension.getLimiteSup().getTipo().IsInteger()) {
                        if (!dimension.getLimiteSup().getTipo().IsChar()) {
                            errores.add(new ErrorC("Semántico", Linea, Columna, "La dimensión debe ser integer."));
                            return null;
                        }
                    }

                }
            }
        }

        if (!guardado) {
            Id.forEach((id) -> {
                if (e.GetLocal(id) == null) {

                    if (Tipo.IsEnum()) {
                        if (Tipo.getId() == null) {
                            Tipo.setIdEnum(id.toLowerCase());
                        }
                    }

                    if (Tipo.IsArray()) {
                        if (Tipo.getTipoArray().IsEnum()) {
                            if (Tipo.getTipoArray().getId() == null) {
                                Tipo.getTipoArray().setIdEnum(id.toLowerCase());
                            }
                        }
                    }

                    e.Add(new Simbolo(id, Tipo, e.getAmbito()));
                    //global.Add(new Simbolo(id, Tipo, e.getAmbito()));
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "Ya se ha definido una variable con el id: " + id + "."));
                }
            });
        }

        return null;
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

}
