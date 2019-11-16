/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion;

import analizador.ErrorC;
import analizador.ast.NodoAST;
import analizador.ast.entorno.Dimension;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Rol;
import analizador.ast.entorno.Simbolo;
import analizador.ast.entorno.Tipo;
import analizador.ast.entorno.Type;
import analizador.ast.expresion.Expresion;
import analizador.ast.expresion.Literal;
import analizador.ast.expresion.operacion.Aritmetica;
import analizador.ast.expresion.operacion.Operador;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Metodo extends Instruccion {

    private boolean Funcion;
    private String Id;
    private ArrayList<Parametro> Parametros;
    private Tipo Tipo;
    private ArrayList<VarDef> Variables;
    private ArrayList<Metodo> Metodos;
    private ArrayList<NodoAST> Sentencias;
    private boolean Declaracion;
    private String Ambito;

    public Metodo(String Id, ArrayList<Parametro> Parametros, Tipo Tipo, ArrayList<VarDef> Variables, ArrayList<Metodo> Metodos, ArrayList<NodoAST> Sentencias, int Linea, int Columna) {
        super(Linea, Columna);
        this.Funcion = true;
        this.Id = Id;
        this.Parametros = Parametros;
        this.Tipo = Tipo;
        this.Variables = Variables;
        this.Metodos = Metodos;
        this.Sentencias = Sentencias;
        this.Declaracion = false;
    }

    public Metodo(String Id, ArrayList<Parametro> Parametros, ArrayList<VarDef> Variables, ArrayList<Metodo> Metodos, ArrayList<NodoAST> Sentencias, int Linea, int Columna) {
        super(Linea, Columna);
        this.Funcion = false;
        this.Id = Id;
        this.Parametros = Parametros;
        this.Tipo = null;
        this.Variables = Variables;
        this.Metodos = Metodos;
        this.Sentencias = Sentencias;
        this.Declaracion = false;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
        Result result = new Result();
        String codigo = "";

        Entorno local = new Entorno(Id, e);
        local.setTmpInicio(NodoAST.Temporales + 1);

        ArrayList<Simbolo> Simbolos = new ArrayList<>();

        if (Funcion) {

            if (DefinirTipo(e, errores, global) == null) {
                return null;
            }

            Simbolo s = new Simbolo(Id, Tipo, local.getPos(), local.getAmbito());

            if (Tipo.IsRecord()) {
                s.setEntorno(new Entorno(Id));
                Tipo.getEntorno().getSimbolos().forEach((sim) -> {
                    s.getEntorno().Add(new Simbolo(sim.getId(), sim.getTipo(), sim.getPos(), Id, s));
                });
            }

            if (Tipo.IsArray()) {

                //Si es record guardo simbolos
                if (Tipo.getTipoArray().IsRecord()) {
                    s.setEntorno(new Entorno(Id));
                    Tipo.getTipoArray().getEntorno().getSimbolos().forEach((sim) -> {
                        s.getEntorno().Add(new Simbolo(sim.getId(), sim.getTipo(), sim.getPos(), Id, s));
                    });
                }

//                if (!Declaracion) {
//                    int tmp = NuevoTemporal();
//                    codigo += "+, P, " + s.getPos() + ", t" + tmp + "\n";
//                    codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
//                    codigo += "=, t0, t" + tmp + ", stack\n";
//
//                    codigo += "=, t" + tmp + ", H, stack\n";
//
//                    codigo += LlenarDimension(0, e, errores);
//                }
            }

            local.Add(s); //Simolo retorno.
        } else {
            //reservo siempre un espacio return
            local.getPos();
        }

        String firma = Id.toLowerCase();

        if (Parametros != null) {
            for (Parametro parametro : Parametros) {
                parametro.setDeclaracion(Declaracion);
                Result rsParametro = parametro.GetCuadruplos(local, errores, global);

                if (rsParametro != null) {
                    firma += rsParametro.getEstructura();
                    if (!Declaracion) {
                        codigo += rsParametro.getCodigo();
                    }
                } else {
                    return null;
                }
            }
        }

        Simbolos.addAll(local.getSimbolos());

        Simbolo metodo = e.GetMetodoLocal(firma);
        if (metodo == null) {
            Simbolo s;

            if (Funcion) {
                s = new Simbolo(Id, Tipo, 0, Ambito, local.getSimbolos().size() - 1, local, firma);
            } else {
                s = new Simbolo(Id, 0, Ambito, local.getSimbolos().size(), local, firma);
            }
            //Uso la variable constante para saber si ya lo definí:
            
            /**
             * Ejecuto declaracion Variables
             */
            if (Variables != null) {
                for (VarDef variable : Variables) {
                    variable.GetCuadruplos(local, new ArrayList<>(), global);
                }
            }

            /**
             * Meto al Entorno
             */
            for (Simbolo sim : e.getSimbolos()) {
                if (sim.getRol() != Rol.FUNCION && sim.getRol() != Rol.METHOD && sim.getRol() != Rol.TYPE) {
                    Simbolo tmpS = new Simbolo(sim.getId(), sim.getTipo(), local.getPos(), sim.getAmbito());
                    tmpS.setConstante(sim.isConstante());
                    tmpS.setRol(Rol.GLOBAL);
                    local.Add(tmpS);
                }
            }
            
            e.Add(s);
        } else {
            if (!Declaracion) {
                if (!metodo.isConstante()) {
                    metodo.setConstante(true); //para saber si ya generé

                    int temporales = NodoAST.Temporales; //temporales al iniciar;
                    int posicion = local.getPos2();
                    
                    if (Funcion) {
                        if (Tipo.IsArray()) {
                            int tmp = NuevoTemporal();
                            LlenarDimension(0, local, errores);

                        }
                    }

                    /**
                     * Ejecuto declaracion Variables
                     */
                    if (Variables != null) {
                        for (VarDef variable : Variables) {
                            variable.GetCuadruplos(local, new ArrayList<>(), global);
                        }
                    }

                    /**
                     * Meto al Entorno
                     */
                    for (Simbolo sim : e.getSimbolos()) {
                        if (sim.getRol() != Rol.FUNCION && sim.getRol() != Rol.METHOD && sim.getRol() != Rol.TYPE) {
                            Simbolo tmpS = new Simbolo(sim.getId(), sim.getTipo(), local.getPos(), sim.getAmbito());
                            tmpS.setConstante(sim.isConstante());
                            tmpS.setRol(Rol.GLOBAL);
                            local.Add(tmpS);
                        }
                    }

                    /**
                     * Ejecuto declaracion Métodos
                     */
                    if (Metodos != null) {
                        for (Metodo met : Metodos) {
                            met.setDeclaracion(true);
                            met.setAmbito(metodo.getAmbito() + "_" + metodo.getId().toLowerCase());
                            met.GetCuadruplos(local, errores, global);
                        }
                    }

                    /**
                     * Ejecuto Sentencias
                     */
                    if (Sentencias != null) {
                        for (NodoAST nodo : Sentencias) {
                            if (nodo instanceof Instruccion) {
                                ((Instruccion) nodo).GetCuadruplos(local, new ArrayList<>(), global);
                            } else if (nodo instanceof Expresion) {
                                ((Expresion) nodo).GetCuadruplos(local, new ArrayList<>());
                            }
                        }
                    }

                    metodo.getEntorno().setTmpInicio(local.getTmpInicio());
                    metodo.getEntorno().setSize(local.getPos());
                    metodo.setTam(metodo.getEntorno().getSize());
                    metodo.getEntorno().setTmpFin(NodoAST.Temporales);
                    local = metodo.getEntorno();
                    local.setSimbolos(Simbolos);
                    local.setPos(posicion);

                    local.setSizeTotal(local.getSize() + (local.getTmpFin() - local.getTmpInicio() + 1));
                    local.setGuardarGlobal(true);

                    System.out.println("inicio metodo: " + local.getTmpInicio() + " fin " + local.getTmpFin());

                    NodoAST.Temporales = temporales;

                    codigo = "begin, , , " + metodo.getAmbito().toLowerCase() + "_" + metodo.getFirma() + "\n" + codigo;
                    //Seunda pasada ya con el size

                    if (Funcion) {
                        if (Tipo.IsArray()) {
                            int tmp = NuevoTemporal();
                            codigo += "+, P, 0, t" + tmp + "\n";
                            codigo += "+, P, " + (tmp - local.getTmpInicio() + local.getSize()) + ", t0\n";
                            codigo += "=, t0, t" + tmp + ", stack\n";

                            codigo += "=, t" + tmp + ", H, stack\n";

                            codigo += LlenarDimension(0, local, errores);
                        }
                    }

                    /**
                     * Ejecuto declaracion Variables
                     */
                    if (Variables != null) {
                        for (VarDef variable : Variables) {
                            Result rsVar = variable.GetCuadruplos(local, errores, global);
                            if (rsVar != null) {
                                codigo += rsVar.getCodigo();
                            }
                        }
                    }

                    /**
                     * Meto al Entorno
                     */
                    for (Simbolo sim : e.getSimbolos()) {
                        if (sim.getRol() != Rol.FUNCION && sim.getRol() != Rol.METHOD && sim.getRol() != Rol.TYPE) {
                            Simbolo tmpS = new Simbolo(sim.getId(), sim.getTipo(), local.getPos(), sim.getAmbito());
                            tmpS.setConstante(sim.isConstante());
                            tmpS.setRol(Rol.GLOBAL);
                            local.Add(tmpS);
                        }
                    }

                    /**
                     * Ejecuto declaracion Métodos
                     */
                    if (Metodos != null) {
                        for (Metodo met : Metodos) {
                            met.setDeclaracion(true);
                            met.setAmbito(metodo.getAmbito() + "_" + metodo.getId().toLowerCase());
                            met.GetCuadruplos(local, errores, global);
                        }
                    }

                    //Seteo etiqueta Exit
                    NodoAST.Etiquetas++;
                    local.setEtqSalida("L" + NodoAST.Etiquetas);

                    /**
                     * Ejecuto Sentencias
                     */
                    if (Sentencias != null) {
                        for (NodoAST nodo : Sentencias) {
                            Result rsNodo = null;

                            if (nodo instanceof Instruccion) {
                                rsNodo = ((Instruccion) nodo).GetCuadruplos(local, errores, global);
                            } else if (nodo instanceof Expresion) {
                                rsNodo = ((Expresion) nodo).GetCuadruplos(local, errores);
                            }

                            if (rsNodo != null) {
                                codigo += rsNodo.getCodigo();
                            }
                        }
                    }

                    codigo += local.getEtqSalida() + ":\n";
                    codigo += "end, , , " + metodo.getAmbito().toLowerCase() + "_" + metodo.getFirma() + "\n\n";

                    /**
                     * Ejecuto definicion Métodos
                     */
                    if (Metodos != null) {
                        for (Metodo met : Metodos) {
                            met.setDeclaracion(false);
                            met.setAmbito(metodo.getAmbito() + "_" + metodo.getId().toLowerCase());
                            codigo += met.GetCuadruplos(local, errores, global).getCodigo();
                        }
                    }

                    //Agrego Simbolos que se mostraran en reporte
                    for (Simbolo s : local.getSimbolos()) {
                        if (s.getRol() == Rol.FUNCION || s.getRol() == Rol.METHOD) {
                            global.Add(s);
                            //global.getSimbolos().addAll(s.getEntorno().getSimbolos());
                            for (Simbolo s2 : s.getEntorno().getSimbolos()) {
                                if (s2.getRol() != Rol.FUNCION && s2.getRol() != Rol.METHOD) {
                                    global.Add(s2);
                                }
                            }
                        }
                    }

                }
            } else {
                errores.add(new ErrorC("Semántico", Linea, Columna, "Ya se ha definido un Método con la misma firma de: " + Id + "."));
            }
        }

        result.setCodigo(codigo);
        return result;
    }

    public String DefinirTipo(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
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
        return "";
    }

    public String LlenarDimension(int pos, Entorno e, ArrayList<ErrorC> errores) {
        String codigo = "";

        Dimension dim = Tipo.getDimensiones().get(pos);

        //Cálculo su tamaño
        Aritmetica suma = /*new Aritmetica(*/ new Aritmetica(dim.getLimiteSup(), dim.getLimiteInf(), Operador.RESTA, Linea, Columna)/*, new Literal(new Tipo(Type.INTEGER), 1, Linea, Columna), Operador.SUMA, Linea, Columna)*/;
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
     * @return the Funcion
     */
    public boolean isFuncion() {
        return Funcion;
    }

    /**
     * @param Funcion the Funcion to set
     */
    public void setFuncion(boolean Funcion) {
        this.Funcion = Funcion;
    }

    /**
     * @return the Id
     */
    public String getId() {
        return Id;
    }

    /**
     * @param Id the Id to set
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     * @return the Parametros
     */
    public ArrayList<Parametro> getParametros() {
        return Parametros;
    }

    /**
     * @param Parametros the Parametros to set
     */
    public void setParametros(ArrayList<Parametro> Parametros) {
        this.Parametros = Parametros;
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
     * @return the Variables
     */
    public ArrayList<VarDef> getVariables() {
        return Variables;
    }

    /**
     * @param Variables the Variables to set
     */
    public void setVariables(ArrayList<VarDef> Variables) {
        this.Variables = Variables;
    }

    /**
     * @return the Metodos
     */
    public ArrayList<Metodo> getMetodos() {
        return Metodos;
    }

    /**
     * @param Metodos the Metodos to set
     */
    public void setMetodos(ArrayList<Metodo> Metodos) {
        this.Metodos = Metodos;
    }

    /**
     * @return the Sentencias
     */
    public ArrayList<NodoAST> getSentencias() {
        return Sentencias;
    }

    /**
     * @param Sentencias the Sentencias to set
     */
    public void setSentencias(ArrayList<NodoAST> Sentencias) {
        this.Sentencias = Sentencias;
    }

    /**
     * @return the Declaracion
     */
    public boolean isDeclaracion() {
        return Declaracion;
    }

    /**
     * @param Declaracion the Declaracion to set
     */
    public void setDeclaracion(boolean Declaracion) {
        this.Declaracion = Declaracion;
    }

    /**
     * @return the Ambito
     */
    public String getAmbito() {
        return Ambito;
    }

    /**
     * @param Ambito the Ambito to set
     */
    public void setAmbito(String Ambito) {
        this.Ambito = Ambito;
    }

}
