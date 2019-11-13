/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.expresion;

import analizador.ErrorC;
import analizador.ast.entorno.Dimension;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Rol;
import analizador.ast.entorno.Simbolo;
import analizador.ast.entorno.Tipo;
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
                            codigo += "+, P, " + e.getSizeTotal() + ", t" + tmpAmbito + "\n"; //cambio simulado
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

                            codigo += "+, P, " + e.getSizeTotal() + ", P\n";
                            codigo += "call, , , $_in_value\n";
                            codigo += "-, P, " + e.getSizeTotal() + ", P\n";

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
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función length necesita un String o Word como parámetro."));
                }
                break;
            case "replace":
                if (Parametros != null) {
                    if (Parametros.size() >= 2) {
                        if (Parametros.size() > 2) {
                            errores.add(new ErrorC("Semántico", Linea, Columna, "La función replace solo necesita un String o Word como parámetro."));
                        }

                        Expresion cadena1 = Parametros.get(0);
                        Expresion cadena2 = Parametros.get(1);

                        Result rsCadena1 = cadena1.GetCuadruplos(e, errores);

                        if (cadena1.getTipo().IsString() || cadena1.getTipo().IsWord()) {
                            Result rsCadena2 = cadena2.GetCuadruplos(e, errores);

                            if (cadena2.getTipo().IsString() || cadena2.getTipo().IsWord()) {
                                Tipo.setTipo(cadena1.getTipo().getTipo());
                                codigo += rsCadena1.getCodigo();
                                codigo += rsCadena2.getCodigo();

                                result.setEtiquetaV(NuevaEtiqueta());
                                result.setEtiquetaF(NuevaEtiqueta());
                                String etqCiclo = NuevaEtiqueta();
                                result.setValor(NuevoTemporal());
                                int tmpCiclo = NuevoTemporal();

                                codigo += "=, H, , t" + result.getValor() + "\n";
                                codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + result.getValor() + ", stack\n";

                                codigo += "+, P, " + (rsCadena1.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, stack, t0, t" + rsCadena1.getValor() + "\n";

                                codigo += "+, P, " + (rsCadena2.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, stack, t0, t" + rsCadena2.getValor() + "\n";

                                codigo += "=, heap, t" + rsCadena1.getValor() + ", t" + tmpCiclo + "\n";
                                codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                                codigo += etqCiclo + ":\n";
                                codigo += "je, t" + tmpCiclo + ", 0, " + result.getEtiquetaV() + "\n";
                                codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                                codigo += result.getEtiquetaF() + ":\n";

                                String etqV = NuevaEtiqueta();
                                String etqF = NuevaEtiqueta();
                                int tmpCiclo2 = NuevoTemporal();

                                codigo += "=, heap, t" + rsCadena2.getValor() + ", t" + tmpCiclo2 + "\n";
                                codigo += "+, P, " + (tmpCiclo2 - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo2 + ", stack\n";

                                codigo += "jne, t" + tmpCiclo + ", t" + tmpCiclo2 + ", " + etqV + "\n";
                                codigo += "jmp, , , " + etqF + "\n";
                                codigo += etqF + ":\n";

                                int tmpCadena1 = NuevoTemporal();
                                int tmpCadena2 = NuevoTemporal();
                                int tmpCiclo1_1 = NuevoTemporal();

                                codigo += "+, t" + rsCadena1.getValor() + ", 1, t" + tmpCadena1 + "\n";
                                codigo += "+, P, " + (tmpCadena1 - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCadena1 + ", stack\n";
                                codigo += "=, heap, t" + tmpCadena1 + ", t" + tmpCiclo1_1 + "\n";
                                codigo += "+, P, " + (tmpCiclo1_1 - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo1_1 + ", stack\n";

                                codigo += "+, t" + rsCadena2.getValor() + ", 1, t" + tmpCadena2 + "\n";
                                codigo += "+, P, " + (tmpCadena2 - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCadena2 + ", stack\n";
                                codigo += "=, heap, t" + tmpCadena2 + ", t" + tmpCiclo2 + "\n";
                                codigo += "+, P, " + (tmpCiclo2 - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo2 + ", stack\n";

                                String etqV2 = NuevaEtiqueta();
                                String etqF2 = NuevaEtiqueta();
                                String etqCiclo2 = NuevaEtiqueta();

                                codigo += etqCiclo2 + ":\n";
                                codigo += "je, t" + tmpCiclo2 + ", 0, " + etqV2 + "\n";
                                codigo += "jmp, , , " + etqF2 + "\n";
                                codigo += etqF2 + ":\n";

                                String etqV3 = NuevaEtiqueta();
                                String etqF3 = NuevaEtiqueta();

                                codigo += "je, t" + tmpCiclo1_1 + ", t" + tmpCiclo2 + ", " + etqV3 + "\n";
                                codigo += "jmp, , , " + etqF3 + "\n";
                                codigo += etqV3 + ":\n";

                                codigo += "+, t" + tmpCadena1 + ", 1, t" + tmpCadena1 + "\n";
                                codigo += "+, P, " + (tmpCadena1 - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCadena1 + ", stack\n";
                                codigo += "=, heap, t" + tmpCadena1 + ", t" + tmpCiclo1_1 + "\n";
                                codigo += "+, P, " + (tmpCiclo1_1 - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo1_1 + ", stack\n";

                                codigo += "+, t" + tmpCadena2 + ", 1, t" + tmpCadena2 + "\n";
                                codigo += "+, P, " + (tmpCadena2 - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCadena2 + ", stack\n";
                                codigo += "=, heap, t" + tmpCadena2 + ", t" + tmpCiclo2 + "\n";
                                codigo += "+, P, " + (tmpCiclo2 - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo2 + ", stack\n";

                                codigo += "jmp, , , " + etqCiclo2 + "\n";

                                codigo += etqV2 + ":\n";
                                codigo += "=, t" + tmpCiclo1_1 + ", , t" + tmpCiclo + "\n";
                                codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo + ", stack\n";

                                codigo += "=, t" + tmpCadena1 + ", , t" + rsCadena1.getValor() + "\n";
                                codigo += "+, P, " + (rsCadena1.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + rsCadena1.getValor() + ", stack\n";

                                codigo += etqF3 + ":\n";
                                codigo += etqV + ":\n";
                                codigo += "=, H, t" + tmpCiclo + ", heap\n";
                                codigo += "+, H, 1, H\n";

                                codigo += "+, t" + rsCadena1.getValor() + ", 1, t" + rsCadena1.getValor() + "\n";
                                codigo += "+, P, " + (rsCadena1.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + rsCadena1.getValor() + ", stack\n";
                                codigo += "=, heap, t" + rsCadena1.getValor() + ", t" + tmpCiclo + "\n";
                                codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                                codigo += "jmp, , , " + etqCiclo + "\n";
                                codigo += result.getEtiquetaV() + ":\n";

                                codigo += "=, H, 0, heap\n";
                                codigo += "+, H, 1, H\n";

                            } else {
                                errores.add(new ErrorC("Semántico", Linea, Columna, "La función replace necesita dos String o Word como parámetros."));
                            }
                        } else {
                            errores.add(new ErrorC("Semántico", Linea, Columna, "La función replace necesita dos String o Word como parámetros."));
                        }
                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función replace necesita dos String o Word como parámetros."));
                    }
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función replace necesita dos String o Word como parámetros."));
                }
                break;
            case "tochararray":
                if (Parametros != null) {

                    if (Parametros.size() > 1) {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función toCharArray solo necesita un String o Word como parámetro."));
                    }

                    Expresion cadena = Parametros.get(0);
                    Result rsCadena = cadena.GetCuadruplos(e, errores);

                    if (cadena.getTipo().IsString() || cadena.getTipo().IsWord()) {
                        Tipo.setTipo(Type.ARRAY);
                        Tipo.setDimensiones(new ArrayList<>());
                        Tipo.getDimensiones().add(new Dimension(null, null));
                        Tipo.setTipoArray(new Tipo(Type.CHAR));

                        codigo += rsCadena.getCodigo();
                        result.setValor(NuevoTemporal());

                        codigo += "=, H, , t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

                        result.setEtiquetaV(NuevaEtiqueta());
                        result.setEtiquetaF(NuevaEtiqueta());
                        String etqCiclo = NuevaEtiqueta();
                        int length = NuevoTemporal();
                        int tmpCadena = NuevoTemporal();
                        int tmpCiclo = NuevoTemporal();

                        codigo += "=, 0, , t" + length + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

                        codigo += "+, P, " + (rsCadena.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, stack, t0, t" + rsCadena.getValor() + "\n";
                        codigo += "=, t" + rsCadena.getValor() + ", , t" + tmpCadena + "\n";
                        codigo += "+, P, " + (tmpCadena - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmpCadena + ", stack\n";

                        codigo += "=, heap, t" + tmpCadena + ", t" + tmpCiclo + "\n";
                        codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                        codigo += etqCiclo + ":\n";
                        codigo += "je, t" + tmpCiclo + ", 0, " + result.getEtiquetaV() + "\n";
                        codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                        codigo += result.getEtiquetaF() + ":\n";
                        codigo += "+, t" + length + ", 1, t" + length + "\n";
                        codigo += "+, P, " + (length - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + length + ", stack\n";

                        codigo += "+, t" + tmpCadena + ", 1, t" + tmpCadena + "\n";
                        codigo += "+, P, " + (tmpCadena - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmpCadena + ", stack\n";
                        codigo += "=, heap, t" + tmpCadena + ", t" + tmpCiclo + "\n";
                        codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                        codigo += "jmp, , , " + etqCiclo + "\n";
                        codigo += result.getEtiquetaV() + ":\n";

                        codigo += "=, H, t" + length + ", heap\n";
                        codigo += "+, H, 1, H\n";
                        codigo += "=, H, 0, heap\n";
                        codigo += "+, H, 1, H\n";
                        codigo += "-, t" + length + ", 1, t" + length + "\n";

                        codigo += "=, H, t" + length + ", heap\n";
                        codigo += "+, H, 1, H\n";

                        result.setEtiquetaV(NuevaEtiqueta());
                        result.setEtiquetaF(NuevaEtiqueta());
                        etqCiclo = NuevaEtiqueta();
                        tmpCiclo = NuevoTemporal();

                        codigo += "+, P, " + (rsCadena.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, stack, t0, t" + rsCadena.getValor() + "\n";

                        codigo += "=, heap, t" + rsCadena.getValor() + ", t" + tmpCiclo + "\n";
                        codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                        codigo += etqCiclo + ":\n";
                        codigo += "je, t" + tmpCiclo + ", 0, " + result.getEtiquetaV() + "\n";
                        codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                        codigo += result.getEtiquetaF() + ":\n";

                        codigo += "=, H, t" + tmpCiclo + ", heap\n";
                        codigo += "+, H, 1, H\n";

                        codigo += "+, t" + rsCadena.getValor() + ", 1, t" + rsCadena.getValor() + "\n";
                        codigo += "+, P, " + (rsCadena.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + rsCadena.getValor() + ", stack\n";
                        codigo += "=, heap, t" + rsCadena.getValor() + ", t" + tmpCiclo + "\n";
                        codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                        codigo += "jmp, , , " + etqCiclo + "\n";
                        codigo += result.getEtiquetaV() + ":\n";

                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función toCharArray necesita un String o Word como parámetro."));
                    }
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función toCharArray necesita un String o Word como parámetro."));
                }
                break;
            case "tolowercase":
                if (Parametros != null) {
                    if (Parametros.size() > 1) {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función toLowerCase solo necesita un String o Word como parámetro."));
                    }

                    Expresion cadena = Parametros.get(0);
                    Result rsCadena = cadena.GetCuadruplos(e, errores);

                    if (cadena.getTipo().IsString() || cadena.getTipo().IsWord()) {
                        Tipo.setTipo(cadena.getTipo().getTipo());

                        codigo += rsCadena.getCodigo();

                        result.setEtiquetaV(NuevaEtiqueta());
                        result.setEtiquetaF(NuevaEtiqueta());
                        String etqCiclo = NuevaEtiqueta();
                        result.setValor(NuevoTemporal());
                        int tmpCiclo = NuevoTemporal();

                        codigo += "=, H, , t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

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
                        String etqV2 = NuevaEtiqueta();
                        String etqF2 = NuevaEtiqueta();

                        codigo += "jl, t" + tmpCiclo + ", 65, " + etqV + "\n";
                        codigo += "jmp, , , " + etqF + "\n";
                        codigo += etqF + ":\n";
                        codigo += "jg, t" + tmpCiclo + ", 90, " + etqV2 + "\n";
                        codigo += "jmp, , , " + etqF2 + "\n";
                        codigo += etqF2 + ":\n";
                        codigo += "+, t" + tmpCiclo + ", 32, t" + tmpCiclo + "\n";
                        codigo += etqV2 + ":\n";
                        codigo += etqV + ":\n";

                        codigo += "=, H, t" + tmpCiclo + ", heap\n";
                        codigo += "+, H, 1, H\n";

                        codigo += "+, t" + rsCadena.getValor() + ", 1, t" + rsCadena.getValor() + "\n";
                        codigo += "+, P, " + (rsCadena.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + rsCadena.getValor() + ", stack\n";
                        codigo += "=, heap, t" + rsCadena.getValor() + ", t" + tmpCiclo + "\n";
                        codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                        codigo += "jmp, , , " + etqCiclo + "\n";
                        codigo += result.getEtiquetaV() + ":\n";
                        codigo += "=, H, 0, heap\n";
                        codigo += "+, H, 1, H\n";

                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función toLowerCase necesita un String o Word como parámetro."));
                    }
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función toLowerCase necesita un String o Word como parámetro."));
                }
                break;
            case "touppercase":
                if (Parametros != null) {
                    if (Parametros.size() > 1) {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función toUpperCase solo necesita un String o Word como parámetro."));
                    }

                    Expresion cadena = Parametros.get(0);
                    Result rsCadena = cadena.GetCuadruplos(e, errores);

                    if (cadena.getTipo().IsString() || cadena.getTipo().IsWord()) {
                        Tipo.setTipo(cadena.getTipo().getTipo());

                        codigo += rsCadena.getCodigo();

                        result.setEtiquetaV(NuevaEtiqueta());
                        result.setEtiquetaF(NuevaEtiqueta());
                        String etqCiclo = NuevaEtiqueta();
                        result.setValor(NuevoTemporal());
                        int tmpCiclo = NuevoTemporal();

                        codigo += "=, H, , t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

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
                        String etqV2 = NuevaEtiqueta();
                        String etqF2 = NuevaEtiqueta();

                        codigo += "jl, t" + tmpCiclo + ", 97, " + etqV + "\n";
                        codigo += "jmp, , , " + etqF + "\n";
                        codigo += etqF + ":\n";
                        codigo += "jg, t" + tmpCiclo + ", 122, " + etqV2 + "\n";
                        codigo += "jmp, , , " + etqF2 + "\n";
                        codigo += etqF2 + ":\n";
                        codigo += "-, t" + tmpCiclo + ", 32, t" + tmpCiclo + "\n";
                        codigo += etqV2 + ":\n";
                        codigo += etqV + ":\n";

                        codigo += "=, H, t" + tmpCiclo + ", heap\n";
                        codigo += "+, H, 1, H\n";

                        codigo += "+, t" + rsCadena.getValor() + ", 1, t" + rsCadena.getValor() + "\n";
                        codigo += "+, P, " + (rsCadena.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + rsCadena.getValor() + ", stack\n";
                        codigo += "=, heap, t" + rsCadena.getValor() + ", t" + tmpCiclo + "\n";
                        codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                        codigo += "jmp, , , " + etqCiclo + "\n";
                        codigo += result.getEtiquetaV() + ":\n";
                        codigo += "=, H, 0, heap\n";
                        codigo += "+, H, 1, H\n";

                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función toUpperCase necesita un String o Word como parámetro."));
                    }
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función toUpperCase necesita un String o Word como parámetro."));
                }
                break;
            case "equals":
                if (Parametros != null) {
                    if (Parametros.size() >= 2) {
                        if (Parametros.size() > 2) {
                            errores.add(new ErrorC("Semántico", Linea, Columna, "La función equals solo necesita dos String o Word como parámetros."));
                        }

                        Expresion cadena1 = Parametros.get(0);
                        Expresion cadena2 = Parametros.get(1);

                        Result rsCadena1 = cadena1.GetCuadruplos(e, errores);

                        if (cadena1.getTipo().IsString() || cadena1.getTipo().IsWord()) {
                            Result rsCadena2 = cadena2.GetCuadruplos(e, errores);

                            if (cadena2.getTipo().IsString() || cadena2.getTipo().IsWord()) {
                                Tipo.setTipo(Type.BOOLEAN);
                                codigo += rsCadena1.getCodigo();
                                codigo += rsCadena2.getCodigo();

                                result.setEtiquetaV(NuevaEtiqueta());
                                result.setEtiquetaF(NuevaEtiqueta());
                                String etqCiclo = NuevaEtiqueta();
                                result.setValor(NuevoTemporal());
                                int tmpCiclo = NuevoTemporal();

                                String etqV = NuevaEtiqueta();
                                String etqF = NuevaEtiqueta();
                                int tmpCiclo2 = NuevoTemporal();
                                String etqSalida = NuevaEtiqueta();

                                codigo += "=, 0, , t" + result.getValor() + "\n";
                                codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + result.getValor() + ", stack\n";

                                //Valor
                                codigo += "+, P, " + (rsCadena1.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, stack, t0, t" + rsCadena1.getValor() + "\n";

                                codigo += "+, P, " + (rsCadena2.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, stack, t0, t" + rsCadena2.getValor() + "\n";

                                codigo += "=, heap, t" + rsCadena2.getValor() + ", t" + tmpCiclo2 + "\n";
                                codigo += "+, P, " + (tmpCiclo2 - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo2 + ", stack\n";

                                codigo += "=, heap, t" + rsCadena1.getValor() + ", t" + tmpCiclo + "\n";
                                codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo + ", stack\n";

                                codigo += "jne, t" + tmpCiclo + ", t" + tmpCiclo2 + ", " + etqSalida + "\n";
                                codigo += "=, 1, , t" + result.getValor() + "\n";
                                codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + result.getValor() + ", stack\n";

                                codigo += etqCiclo + ":\n";
                                codigo += "je, t" + tmpCiclo + ", 0, " + result.getEtiquetaV() + "\n";
                                codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                                codigo += result.getEtiquetaF() + ":\n";

                                codigo += "=, heap, t" + rsCadena2.getValor() + ", t" + tmpCiclo2 + "\n";
                                codigo += "+, P, " + (tmpCiclo2 - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo2 + ", stack\n";

                                codigo += "je, t" + tmpCiclo + ", t" + tmpCiclo2 + ", " + etqV + "\n";
                                codigo += "jmp, , , " + etqF + "\n";
                                codigo += etqF + ":\n";
                                codigo += "=, 0, , t" + result.getValor() + "\n";
                                codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + result.getValor() + ", stack\n";
                                codigo += "jmp, , , " + etqSalida + "\n";
                                codigo += etqV + ":\n";

                                codigo += "+, t" + rsCadena2.getValor() + ", 1, t" + rsCadena2.getValor() + "\n";
                                codigo += "+, P, " + (rsCadena2.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + rsCadena2.getValor() + ", stack\n";
                                codigo += "=, heap, t" + rsCadena2.getValor() + ", t" + tmpCiclo2 + "\n";
                                codigo += "+, P, " + (tmpCiclo2 - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo2 + ", stack\n";

                                codigo += "+, t" + rsCadena1.getValor() + ", 1, t" + rsCadena1.getValor() + "\n";
                                codigo += "+, P, " + (rsCadena1.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + rsCadena1.getValor() + ", stack\n";
                                codigo += "=, heap, t" + rsCadena1.getValor() + ", t" + tmpCiclo + "\n";
                                codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                                codigo += "jmp, , , " + etqCiclo + "\n";
                                codigo += result.getEtiquetaV() + ":\n";
                                codigo += "je, t" + tmpCiclo + ", t" + tmpCiclo2 + ", " + etqSalida + "\n";
                                codigo += "=, 0, , t" + result.getValor() + "\n";
                                codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + result.getValor() + ", stack\n";

                                codigo += etqSalida + ":\n";

                            } else {
                                errores.add(new ErrorC("Semántico", Linea, Columna, "La función equals necesita dos String o Word como parámetros."));
                            }
                        } else {
                            errores.add(new ErrorC("Semántico", Linea, Columna, "La función equals necesita dos String o Word como parámetros."));
                        }
                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función equals necesita dos String o Word como parámetros."));
                    }
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función equals necesita dos String o Word como parámetros."));
                }
                break;
            case "trunk":
                if (Parametros != null) {
                    if (Parametros.size() > 1) {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función trunk solo necesita un Real como parámetro."));
                    }

                    Expresion real = Parametros.get(0);
                    Result rsReal = real.GetCuadruplos(e, errores);

                    if (real.getTipo().IsReal()) {
                        Tipo.setTipo(Type.INTEGER);

                        codigo += rsReal.getCodigo();

                        int tmp = NuevoTemporal();
                        result.setValor(NuevoTemporal());

                        codigo += "+, P, " + (rsReal.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, stack, t0, t" + rsReal.getValor() + "\n";

                        codigo += "=, t" + rsReal.getValor() + ", , t" + result.getValor() + "\n";

                        codigo += "*, t" + result.getValor() + ", 10, t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

                        codigo += "%, t" + result.getValor() + ", 10, t" + tmp + "\n";
                        codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmp + ", stack\n";

                        codigo += "-, t" + result.getValor() + ", t" + tmp + ", t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

                        codigo += "/, t" + result.getValor() + ", 10, t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";
                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función trunk necesita un Real como parámetro."));
                    }
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función trunk necesita un Real como parámetro."));
                }
                break;
            case "round":
                if (Parametros != null) {
                    if (Parametros.size() > 1) {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función trunk solo necesita un Real como parámetro."));
                    }

                    Expresion real = Parametros.get(0);
                    Result rsReal = real.GetCuadruplos(e, errores);

                    if (real.getTipo().IsReal()) {
                        Tipo.setTipo(Type.INTEGER);

                        codigo += rsReal.getCodigo();

                        int tmp = NuevoTemporal();
                        result.setValor(NuevoTemporal());

                        codigo += "+, P, " + (rsReal.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, stack, t0, t" + rsReal.getValor() + "\n";

                        codigo += "=, t" + rsReal.getValor() + ", , t" + result.getValor() + "\n";

                        codigo += "*, t" + result.getValor() + ", 10, t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

                        codigo += "%, t" + result.getValor() + ", 10, t" + tmp + "\n";
                        codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmp + ", stack\n";

                        int condicion = NuevoTemporal();
                        result.setEtiquetaV(NuevaEtiqueta());
                        result.setEtiquetaF(NuevaEtiqueta());

                        codigo += "=, 0, , t" + condicion + "\n";
                        codigo += "+, P, " + (condicion - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + condicion + ", stack\n";

                        codigo += "jl, t" + tmp + ", 5, " + result.getEtiquetaV() + "\n";
                        codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                        codigo += result.getEtiquetaF() + ":\n";
                        codigo += "=, 1, , t" + condicion + "\n";
                        codigo += "+, P, " + (condicion - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + condicion + ", stack\n";
                        codigo += result.getEtiquetaV() + ":\n";

                        codigo += "-, t" + result.getValor() + ", t" + tmp + ", t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

                        codigo += "/, t" + result.getValor() + ", 10, t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

                        result.setEtiquetaV(NuevaEtiqueta());
                        result.setEtiquetaF(NuevaEtiqueta());

                        codigo += "jne, t" + condicion + ", 1, " + result.getEtiquetaV() + "\n";
                        codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                        codigo += result.getEtiquetaF() + ":\n";
                        codigo += "+, t" + result.getValor() + ", 1, t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";
                        codigo += result.getEtiquetaV() + ":\n";

                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función trunk necesita un Real como parámetro."));
                    }
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función trunk necesita un Real como parámetro."));
                }
                break;
            default:
                String firma = Id.toLowerCase();

                ArrayList<Result> rsParametros = new ArrayList<>();
                String codigoParametro = "";

                if (Parametros != null) {
                    for (Expresion parametro : Parametros) {
                        Result rsParametro = parametro.GetCuadruplos(e, errores);
                        if (!parametro.getTipo().IsUndefined()) {
                            firma += "_" + parametro.getTipo().toStringMetodo();
                            rsParametros.add(rsParametro);
                            codigoParametro += rsParametro.getCodigo();
                        } else {
                            errores.add(new ErrorC("Semántico", Linea, Columna, "Error en pámetros."));
                            break;
                        }
                    }
                }

                Simbolo metodo = e.GetMetodo(firma);

                if (metodo != null) {
                    Tipo = metodo.getTipo();
                    codigo += codigoParametro;

                    if (Parametros != null) {
                        for (int i = 0; i < Parametros.size(); i++) {
                            Result rsParametro = rsParametros.get(i);

                            Simbolo simParametro;
                            if(metodo.getRol() == Rol.FUNCION){
                                simParametro = metodo.getEntorno().getSimbolos().get(i + 1);
                            } else {
                                simParametro = metodo.getEntorno().getSimbolos().get(i);
                            }

                            if (simParametro.getTipoParam() == 0 && !(simParametro.getTipo().IsArray() || simParametro.getTipo().IsRecord())) {
                                //si es por referencia vuelvo a ejecutar su valor;
                                Expresion parametro = Parametros.get(i);
                                if (parametro instanceof Identificador) {
                                    ((Identificador) parametro).setAcceso(false);
                                } else if (parametro instanceof Atributo) {
                                    ((Atributo) parametro).setAcceso(false);
                                } else if (parametro instanceof Acceso) {
                                    ((Acceso) parametro).setAcceso(false);
                                } else {
                                    errores.add(new ErrorC("Semántico", Linea, Columna, "El parámetro no se puede enviar como referencia en: " + Id + "."));
                                    Tipo = new Tipo(Type.UNDEFINED);
                                    result.setCodigo("");
                                    return result;
                                }

                                rsParametro = parametro.GetCuadruplos(e, errores);

                                if (rsParametro.getEstructura() == null) {
                                    errores.add(new ErrorC("Semántico", Linea, Columna, "El parámetro no se puede enviar como referencia en: " + Id + "."));
                                    Tipo = new Tipo(Type.UNDEFINED);
                                    result.setCodigo("");
                                    return result;
                                }
                                codigo += rsParametro.getCodigo();
                            }

                            int tmpAmbito = NuevoTemporal();
                            codigo += "+, P, " + e.getSizeTotal() + ", t" + tmpAmbito + "\n"; //cambio simulado
                            codigo += "+, P, " + (tmpAmbito - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, t0, t" + tmpAmbito + ", stack\n";

                            int posDireccion = NuevoTemporal();
                            codigo += "+, t" + tmpAmbito + ", " + simParametro.getPos() + ", t" + posDireccion + "\n";
                            codigo += "+, P, " + (posDireccion - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, t0, t" + posDireccion + ", stack\n";

                            codigo += "+, P, " +(rsParametro.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                            codigo += "=, stack, t0, t" + rsParametro.getValor() + "\n";
                            
                            codigo += "=, t" + posDireccion + ", t" + rsParametro.getValor() + ", stack\n";

                            if (simParametro.getTipoParam() == 0 && !(simParametro.getTipo().IsArray() || simParametro.getTipo().IsRecord())) {
                                //Envio la estructura en su siguiente posicion
                                posDireccion = NuevoTemporal();
                                codigo += "+, t" + tmpAmbito + ", " + (simParametro.getPos() + 1) + ", t" + posDireccion + "\n";
                                codigo += "+, P, " + (posDireccion - e.getTmpInicio() + e.getSize()) + ", t0\n";
                                codigo += "=, t0, t" + posDireccion + ", stack\n";

                                if (rsParametro.getEstructura().equals("stack")) {
                                    codigo += "=, t" + posDireccion + ", 0, stack\n";
                                } else {
                                    codigo += "=, t" + posDireccion + ", 1, stack\n";
                                }
                            }

                        }
                    }

                    codigo += "+, P, " + e.getSizeTotal() + ", P\n";
                    codigo += "call, , , " + metodo.getAmbito().toLowerCase() + "_" + firma + "\n";
                    codigo += "-, P, " + e.getSizeTotal() + ", P\n";

                    if (metodo.getRol() == Rol.FUNCION) {
                        int tmpAmbito = NuevoTemporal();
                        codigo += "+, P, " + e.getSizeTotal() + ", t" + tmpAmbito + "\n"; //cambio simulado
                        codigo += "+, P, " + (tmpAmbito - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmpAmbito + ", stack\n";

                        int posDireccion = NuevoTemporal();
                        codigo += "+, t" + tmpAmbito + ", 0, t" + posDireccion + "\n";
                        codigo += "+, P, " + (posDireccion - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + posDireccion + ", stack\n";

                        result.setValor(NuevoTemporal());
                        codigo += "=, stack, t" + posDireccion + ", t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

                    }

                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "No se ha definido un método con la firma de: " + Id + "."));
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
