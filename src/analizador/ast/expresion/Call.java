/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.expresion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Type;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Call extends Expresion {

    private String Id;
    private ArrayList<Expresion> Parametros;

    public Call(String Id, int Linea, int Columna) {
        super(Linea, Columna);
        this.Id = Id;
        this.Parametros = null;
    }

    public Call(String Id, ArrayList<Expresion> Parametros, int Linea, int Columna) {
        super(Linea, Columna);
        this.Id = Id;
        this.Parametros = Parametros;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";

        switch (Id.toLowerCase()) {
            case "sizeof": //Por el momento solo acepta id de parametro
                if (Parametros != null) {

                    if (Parametros.size() > 1) {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función sizeof solo necesita un record como parámetro."));
                    }

                    Expresion parametro = Parametros.get(0);

                    if (parametro instanceof Identificador) {
                        ((Identificador) parametro).setObtenerTipo(true);
                    } else if (parametro instanceof Atributo) {
                        ((Atributo) parametro).setObtenerTipo(true);
                    } else if (parametro instanceof Acceso) {
                        ((Acceso) parametro).setObtenerTipo(true);
                    }

                    parametro.GetCuadruplos(e, errores);

                    if (parametro.getTipo().IsRecord()) {
                        Tipo.setTipo(Type.INTEGER);

                        result.setValor(NuevoTemporal());

                        codigo += "=, " + parametro.getTipo().getEntorno().getSize() + ", , t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "Parametro no es de tipo record."));
                    }

                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función sizeof necesita un record como parámetro."));
                }
                break;
            case "malloc":
                if (Parametros != null) {
                    if (Parametros.size() > 1) {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función malloc solo necesita un entero como parámetro."));
                    }

                    Expresion parametro = Parametros.get(0);
                    Result rsParametro = parametro.GetCuadruplos(e, errores);

                    if (!parametro.getTipo().IsUndefined()) {
                        if (parametro.getTipo().IsInteger()) {
                            Tipo.setTipo(Type.INTEGER);

                            codigo += rsParametro.getCodigo();

                            result.setValor(NuevoTemporal());
                            codigo += "=, H, , t" + result.getValor() + "\n";
                            codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, t0, t" + result.getValor() + ", stack\n";

                            codigo += "+, H, t" + rsParametro.getValor() + ", H\n";

                        } else {
                            errores.add(new ErrorC("Semántico", Linea, Columna, "La función malloc necesita un entero como parámetro."));
                        }
                    }

                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función malloc necesita un entero como parámetro."));
                }
                break;
            case "free":
                if (Parametros != null) {

                    if (Parametros.size() > 1) {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función free solo necesita un record como parámetro."));
                    }

                    Expresion parametro = Parametros.get(0);

                    if (parametro instanceof Identificador) {
                        ((Identificador) parametro).setAcceso(false);
                    }

                    Result rsParametro = parametro.GetCuadruplos(e, errores);

                    if (rsParametro.getEstructura() != null) {
                        if (parametro.getTipo().IsRecord()) {
                            codigo += rsParametro.getCodigo();

                            int negativo = NuevoTemporal();
                            codigo += "-, 0, 1, t" + negativo;
                            codigo += "+, P, " + (negativo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, t0, t" + negativo + ", stack\n";

                            codigo += "=, t" + rsParametro.getValor() + ", t" + negativo + ", " + rsParametro.getEstructura() + "\n";

                        } else {
                            errores.add(new ErrorC("Semántico", Linea, Columna, "El parametro no es de tipo record."));
                        }
                    }

                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función sizeof necesita un record como parámetro."));
                }
                break;
            case "read":
                if (Parametros != null) {
                    if (Parametros.size() > 1) {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función read solo necesita un target como parámetro."));
                    }

                    Expresion parametro = Parametros.get(0);
                    boolean bandera = false;

                    if (parametro instanceof Identificador) {
                        ((Identificador) parametro).setAcceso(false);
                        bandera = true;
                    } else if (parametro instanceof Atributo) {
                        ((Atributo) parametro).setAcceso(false);
                        bandera = true;
                    } else if (parametro instanceof Acceso) {
                        ((Acceso) parametro).setAcceso(false);
                        bandera = true;
                    }

                    if (bandera) {
                        Result rsTarget = parametro.GetCuadruplos(e, errores);

                        if (rsTarget.getEstructura() != null) {
                            codigo += rsTarget.getCodigo();

                            int tipo = 1;

                            if (parametro.getTipo().IsString() || parametro.getTipo().IsWord()) {
                                tipo = 3;
                            } else if (parametro.getTipo().IsChar()) {
                                tipo = 0;
                            } else if (parametro.getTipo().IsBoolean()) {
                                tipo = 4;
                            } else if (parametro.getTipo().IsReal()) {
                                tipo = 2;
                            }

                            int tmpAmbito = NuevoTemporal();
                            codigo += "+, P, " + (e.getSize() + e.getTmpFin() - e.getTmpInicio() + 1) + ", t" + tmpAmbito + "\n"; //cambio simulado
                            codigo += "+, P, " + (tmpAmbito - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, t0, t" + tmpAmbito + ", stack\n";

                            int posDireccion = NuevoTemporal();
                            codigo += "+, t" + tmpAmbito + ", 1, t" + posDireccion + "\n";
                            codigo += "+, P, " + (posDireccion - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, t0, t" + posDireccion + ", stack\n";

                            codigo += "=, t" + posDireccion + ", t" + rsTarget.getValor() + ", stack\n";

                            int posTipo = NuevoTemporal();
                            codigo += "+, t" + tmpAmbito + ", 3, t" + posTipo + "\n";
                            codigo += "+, P, " + (posTipo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, t0, t" + posTipo + ", stack\n";

                            codigo += "=, t" + posTipo + ", " + tipo + ", stack\n";

                            int posStruc = NuevoTemporal();
                            codigo += "+, t" + tmpAmbito + ", 4, t" + posStruc + "\n";
                            codigo += "+, P, " + (posStruc - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, t0, t" + posStruc + ", stack\n";

                            if (rsTarget.getEstructura().equals("stack")) {
                                codigo += "=, t" + posStruc + ", 0, stack\n";
                            } else {
                                codigo += "=, t" + posStruc + ", 1, stack\n";
                            }

                            codigo += "+, P, " + (e.getSize() + e.getTmpFin() - e.getTmpInicio() + 1) + ", P\n";
                            codigo += "call, , , $_in_value\n";
                            codigo += "-, P, " + (e.getSize() + e.getTmpFin() - e.getTmpInicio() + 1) + ", P\n";

                        }
                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función read necesita un target como parámetro."));

                    }

                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función read necesita un target como parámetro."));
                }
                break;
            case "charat":
                if (Parametros != null) {
                    if (Parametros.size() >= 2) {
                        if (Parametros.size() > 2) {
                            errores.add(new ErrorC("Semántico", Linea, Columna, "La función charAt solo necesita un String o Word y un Integer como parámetros."));
                        }

                        Expresion cadena = Parametros.get(0);
                        Result rsCadena = cadena.GetCuadruplos(e, errores);

                        if (cadena.getTipo().IsString() || cadena.getTipo().IsWord()) {
                            Expresion posicion = Parametros.get(1);
                            Result rsPosicion = posicion.GetCuadruplos(e, errores);

                            if (posicion.getTipo().IsInteger()) {
                                Tipo.setTipo(Type.CHAR);
                                codigo += rsCadena.getCodigo();
                                codigo += rsPosicion.getCodigo();

                                result.setEtiquetaV(NuevaEtiqueta());
                                result.setEtiquetaF(NuevaEtiqueta());
                                String etqCiclo = NuevaEtiqueta();
                                int contador = NuevoTemporal();
                                int tmpCiclo = NuevoTemporal();
                                result.setValor(tmpCiclo);

                                codigo += "=, 0, , t" + contador + "\n";
                                codigo += "+, P, " + (contador - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + contador + ", stack\n";

                                codigo += "+, P, " + (rsPosicion.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, stack, t0, t" + rsPosicion.getValor() + "\n";
                                //Valor
                                codigo += "+, P, " + (rsCadena.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, stack, t0, t" + rsCadena.getValor() + "\n";
                                codigo += "=, heap, t" + rsCadena.getValor() + ", t" + tmpCiclo + "\n";
                                codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                                codigo += etqCiclo + ":\n";
                                codigo += "je, t" + tmpCiclo + ", 0, " + result.getEtiquetaV() + "\n";
                                codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                                codigo += result.getEtiquetaF() + ":\n";

                                String etqV = NuevaEtiqueta();
                                String etqF = NuevaEtiqueta();
                                
                                codigo += "je, t" + contador + ", t" + rsPosicion.getValor() + ", " + etqV + "\n";
                                codigo += "jmp, , , " + etqF + "\n";
                                codigo += etqF + ":\n";
                                
                                codigo += "+, t" + contador + ", 1, t" + contador + "\n";
                                codigo += "+, P, " + (contador - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + contador + ", stack\n";

                                codigo += "+, t" + rsCadena.getValor() + ", 1, t" + rsCadena.getValor() + "\n";
                                codigo += "+, P, " + (rsCadena.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + rsCadena.getValor() + ", stack\n";
                                codigo += "=, heap, t" + rsCadena.getValor() + ", t" + tmpCiclo + "\n";
                                codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                                codigo += "jmp, , , " + etqCiclo + "\n";
                                codigo += result.getEtiquetaV() + ":\n";
                                codigo += "=, 0, , t" + tmpCiclo + "\n";
                                codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo + ", stack\n";

                                codigo += "print(%c, 73)\n"
                                        + "print(%c, 110)\n"
                                        + "print(%c, 100)\n"
                                        + "print(%c, 101)\n"
                                        + "print(%c, 120)\n"
                                        + "print(%c, 79)\n"
                                        + "print(%c, 117)\n"
                                        + "print(%c, 116)\n"
                                        + "print(%c, 79)\n"
                                        + "print(%c, 102)\n"
                                        + "print(%c, 66)\n"
                                        + "print(%c, 111)\n"
                                        + "print(%c, 117)\n"
                                        + "print(%c, 110)\n"
                                        + "print(%c, 100)\n"
                                        + "print(%c, 115)\n"
                                        + "print(%c, 69)\n"
                                        + "print(%c, 120)\n"
                                        + "print(%c, 99)\n"
                                        + "print(%c, 101)\n"
                                        + "print(%c, 112)\n"
                                        + "print(%c, 116)\n"
                                        + "print(%c, 105)\n"
                                        + "print(%c, 111)\n"
                                        + "print(%c, 110)\n"
                                        + "print(%c, 32)\n"
                                        + "print(%e, " + Linea + ")\n"
                                        + "print(%c, 10)\n";

                                codigo += etqV + ":\n";

                            } else {
                                errores.add(new ErrorC("Semántico", Linea, Columna, "La función charAt necesita un Integer como segundo parámetro."));
                            }

                        } else {
                            errores.add(new ErrorC("Semántico", Linea, Columna, "La función charAt necesita un String o Word como primer parámetro."));
                        }
                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función charAt necesita un String o Word y un Integer como parámetros."));
                    }
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función charAt necesita parámetros."));
                }
                break;
            case "length":
                if (Parametros != null) {
                    if (Parametros.size() > 1) {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función length solo necesita un String o Word como parámetro."));
                    }

                    Expresion parametro = Parametros.get(0);
                    Result rsParametro = parametro.GetCuadruplos(e, errores);

                    if (parametro.getTipo().IsString() || parametro.getTipo().IsWord()) {
                        Tipo.setTipo(Type.INTEGER);

                        codigo += rsParametro.getCodigo();

                        result.setEtiquetaV(NuevaEtiqueta());
                        result.setEtiquetaF(NuevaEtiqueta());
                        String etqCiclo = NuevaEtiqueta();
                        result.setValor(NuevoTemporal());
                        int tmpCiclo = NuevoTemporal();

                        codigo += "=, 0, , t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

                        //Valor
                        codigo += "+, P, " + (rsParametro.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, stack, t0, t" + rsParametro.getValor() + "\n";
                        codigo += "=, heap, t" + rsParametro.getValor() + ", t" + tmpCiclo + "\n";
                        codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                        codigo += etqCiclo + ":\n";
                        codigo += "je, t" + tmpCiclo + ", 0, " + result.getEtiquetaV() + "\n";
                        codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                        codigo += result.getEtiquetaF() + ":\n";
                        codigo += "+, t" + result.getValor() + ", 1, t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

                        codigo += "+, t" + rsParametro.getValor() + ", 1, t" + rsParametro.getValor() + "\n";
                        codigo += "+, P, " + (rsParametro.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + rsParametro.getValor() + ", stack\n";
                        codigo += "=, heap, t" + rsParametro.getValor() + ", t" + tmpCiclo + "\n";
                        codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                        codigo += "jmp, , , " + etqCiclo + "\n";
                        codigo += result.getEtiquetaV() + ":\n";

                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función length necesita un String o Word como parámetros."));
                    }
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función length necesita un String o Word como parámetros."));
                }
                break;
        }

        result.setCodigo(codigo);
        return result;
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
    public ArrayList<Expresion> getParametros() {
        return Parametros;
    }

    /**
     * @param Parametros the Parametros to set
     */
    public void setParametros(ArrayList<Expresion> Parametros) {
        this.Parametros = Parametros;
    }
}
