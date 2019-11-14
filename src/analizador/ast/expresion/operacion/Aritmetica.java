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
import analizador.ast.expresion.Expresion;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Aritmetica extends Operacion {

    public Aritmetica(Expresion Op1, Expresion Op2, Operador op, int Linea, int Columna) {
        super(Op1, Op2, op, Linea, Columna);
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";

        Result rsOp1 = Op1.GetCuadruplos(e, errores);
        Result rsOp2 = Op2.GetCuadruplos(e, errores);

        TipoDominante();

        if (!Tipo.IsUndefined()) {
            codigo += rsOp1.getCodigo();
            codigo += rsOp2.getCodigo();

            if (Tipo.IsString() || Tipo.IsWord()) {

                if (!(Op1.getTipo().IsString() || Op1.getTipo().IsWord())) {
                    ConvertirString(Op1, rsOp1, e);
                    codigo += rsOp1.getCodigo();
                }
                if (!(Op2.getTipo().IsString() || Op2.getTipo().IsWord())) {
                    ConvertirString(Op2, rsOp2, e);
                    codigo += rsOp2.getCodigo();
                }

                result.setEtiquetaV(NuevaEtiqueta());
                result.setEtiquetaF(NuevaEtiqueta());
                String etqCiclo = NuevaEtiqueta();
                result.setValor(NuevoTemporal());
                int tmpCiclo = NuevoTemporal();

                codigo += "=, H, , t" + result.getValor() + "\n";
                codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, t0, t" + result.getValor() + ", stack\n";

                //Valor del primer operando
                codigo += "+, P, " + (rsOp1.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, stack, t0, t" + rsOp1.getValor() + "\n";
                codigo += "=, heap, t" + rsOp1.getValor() + ", t" + tmpCiclo + "\n";
                codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                codigo += etqCiclo + ":\n";
                codigo += "je, t" + tmpCiclo + ", 0, " + result.getEtiquetaV() + "\n";
                codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                codigo += result.getEtiquetaF() + ":\n";
                codigo += "=, H, t" + tmpCiclo + ", heap\n";
                codigo += "+, H, 1, H\n";
                codigo += "+, t" + rsOp1.getValor() + ", 1, t" + rsOp1.getValor() + "\n";
                codigo += "+, P, " + (rsOp1.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, t0, t" + rsOp1.getValor() + ", stack\n";
                codigo += "=, heap, t" + rsOp1.getValor() + ", t" + tmpCiclo + "\n";
                codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                codigo += "jmp, , , " + etqCiclo + "\n";
                codigo += result.getEtiquetaV() + ":\n";

                result.setEtiquetaV(NuevaEtiqueta());
                result.setEtiquetaF(NuevaEtiqueta());
                etqCiclo = NuevaEtiqueta();
                tmpCiclo = NuevoTemporal();

                //Valor del segundo Operando
                codigo += "+, P, " + (rsOp2.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, stack, t0, t" + rsOp2.getValor() + "\n";
                codigo += "=, heap, t" + rsOp2.getValor() + ", t" + tmpCiclo + "\n";
                codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                codigo += etqCiclo + ":\n";
                codigo += "je, t" + tmpCiclo + ", 0, " + result.getEtiquetaV() + "\n";
                codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                codigo += result.getEtiquetaF() + ":\n";
                codigo += "=, H, t" + tmpCiclo + ", heap\n";
                codigo += "+, H, 1, H\n";
                codigo += "+, t" + rsOp2.getValor() + ", 1, t" + rsOp2.getValor() + "\n";
                codigo += "+, P, " + (rsOp2.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, t0, t" + rsOp2.getValor() + ", stack\n";
                codigo += "=, heap, t" + rsOp2.getValor() + ", t" + tmpCiclo + "\n";
                codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, t0, t" + tmpCiclo + ", stack\n";
                codigo += "jmp, , , " + etqCiclo + "\n";
                codigo += result.getEtiquetaV() + ":\n";

                //Fin de cadena
                codigo += "=, H, 0, heap\n";
                codigo += "+, H, 1, H\n";

            } else {
                result.setValor(NuevoTemporal());
                codigo += "+, P, " + (rsOp1.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, stack, t0, t" + rsOp1.getValor() + "\n";
                codigo += "+, P, " + (rsOp2.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, stack, t0, t" + rsOp2.getValor() + "\n";

                switch (Op) {
                    case SUMA:
                        codigo += "+, t" + rsOp1.getValor() + ", t" + rsOp2.getValor() + ", t" + result.getValor() + "\n";
                        break;
                    case RESTA:
                        codigo += "-, t" + rsOp1.getValor() + ", t" + rsOp2.getValor() + ", t" + result.getValor() + "\n";
                        break;
                    case MULTIPLICACION:
                        codigo += "*, t" + rsOp1.getValor() + ", t" + rsOp2.getValor() + ", t" + result.getValor() + "\n";
                        break;
                    case DIVISION:
                        codigo += "/, t" + rsOp1.getValor() + ", t" + rsOp2.getValor() + ", t" + result.getValor() + "\n";
                        break;
                    case POTENCIA:
                        String etqSalida = NuevaEtiqueta();
                        result.setEtiquetaV(NuevaEtiqueta());
                        result.setEtiquetaF(NuevaEtiqueta());

                        //Si op2 es 0, resultado es 1
                        codigo += "je, t" + rsOp2.getValor() + ", 0, " + result.getEtiquetaV() + "\n";
                        codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                        codigo += result.getEtiquetaV() + ":\n";
                        codigo += "=, 1, , t" + result.getValor() + "\n";
                        codigo += "jmp, , , " + etqSalida + "\n";
                        codigo += result.getEtiquetaF() + ":\n";

                        result.setEtiquetaV(NuevaEtiqueta());
                        result.setEtiquetaF(NuevaEtiqueta());

                        //Si op2 es negativo, resultado es 0
                        codigo += "jl, t" + rsOp2.getValor() + ", 0, " + result.getEtiquetaV() + "\n";
                        codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                        codigo += result.getEtiquetaV() + ":\n";
                        codigo += "=, 0, , t" + result.getValor() + "\n";
                        codigo += "jmp, , , " + etqSalida + "\n";
                        codigo += result.getEtiquetaF() + ":\n";

                        result.setEtiquetaV(NuevaEtiqueta());
                        result.setEtiquetaF(NuevaEtiqueta());
                        String etqCiclo = NuevaEtiqueta();
                        int nuevoValor = NuevoTemporal();

                        //Si op2 es mayor a 0, cálculo
                        codigo += "=, t" + rsOp1.getValor() + ", , t" + nuevoValor + "\n";
                        codigo += "+, P, " + (nuevoValor - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + nuevoValor + ", stack\n";

                        codigo += "=, 1, , t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";
                        
                        codigo += etqCiclo + ":\n";
                        codigo += "+, P, " + (rsOp2.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, stack, t0, t" + rsOp2.getValor() + "\n";
                        codigo += "jle, t" + rsOp2.getValor() + ", 0, " + result.getEtiquetaV() + "\n";
                        codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                        codigo += result.getEtiquetaF() + ":\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, stack, t0, t" + result.getValor() + "\n";
                        codigo += "+, P, " + (nuevoValor - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, stack, t0, t" + nuevoValor + "\n";
                        codigo += "*, t" + result.getValor() + ", t" + nuevoValor + ", t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";
                        codigo += "+, P, " + (rsOp2.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, stack, t0, t" + rsOp2.getValor() + "\n";
                        codigo += "-, t" + rsOp2.getValor() + ", 1, t" + rsOp2.getValor() + "\n";
                        codigo += "+, P, " + (rsOp2.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + rsOp2.getValor() + ", stack\n";
                        codigo += "jmp, , , " + etqCiclo + "\n";
                        codigo += result.getEtiquetaV() + ":\n";

                        codigo += etqSalida + ":\n";

                        break;
                    case MODULO:
                        codigo += "%, t" + rsOp1.getValor() + ", t" + rsOp2.getValor() + ", t" + result.getValor() + "\n";
                        break;
                }

                codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, t0, t" + result.getValor() + ", stack\n";
            }

        } else {
            errores.add(new ErrorC("Semántico", Linea, Columna, "Error de tipos en operación aritmética."));
        }

        result.setCodigo(codigo);
        return result;
    }

    private void TipoDominante() {
        Tipo.setTipo(Type.UNDEFINED);

        if (Op1.getTipo().IsUndefined() || Op2.getTipo().IsUndefined()) {
            return;
        }

        if (Op1.getTipo().IsString() || Op2.getTipo().IsString()) {
            if (Op == Operador.SUMA) {
                Tipo.setTipo(Type.STRING);
            }
        } else if (Op1.getTipo().IsWord() || Op2.getTipo().IsWord()) {
            if (Op == Operador.SUMA) {
                Tipo.setTipo(Type.WORD);
            }
        } else if (Op1.getTipo().IsBoolean() || Op2.getTipo().IsBoolean()) {
        } else if (Op1.getTipo().IsNil() || Op2.getTipo().IsNil()) {
        } else if (Op1.getTipo().IsRecord() || Op2.getTipo().IsRecord()) {
        } else if (Op1.getTipo().IsArray() || Op2.getTipo().IsArray()) {
        } else if (Op1.getTipo().IsEnum() || Op2.getTipo().IsEnum()) {
        } else if (Op1.getTipo().IsReal() || Op2.getTipo().IsReal()) {
            if (!(Op == Operador.POTENCIA)) {
                Tipo.setTipo(Type.REAL);
            }
        } else {
            Tipo.setTipo(Type.INTEGER);
        }

    }

    private void ConvertirString(Expresion op, Result rsOp, Entorno e) {
        String codigo = "";

        if (op.getTipo().IsInteger()) {

            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());

            //Verifico si el número es 0
            int cero = NuevoTemporal();
            codigo += "=, 0, , t" + cero + "\n";
            codigo += "+, P, " + (cero - e.getTmpInicio() + e.getSize())  + ", t0\n";
            codigo += "=, t0, t" + cero + ", stack\n";

            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize())  + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";

            codigo += "jne, t" + rsOp.getValor() + ", 0, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            codigo += "=, 1, , t" + cero + "\n";
            codigo += "+, P, " + (cero - e.getTmpInicio() + e.getSize())  + ", t0\n";
            codigo += "=, t0, t" + cero + ", stack\n";
            codigo += rsOp.getEtiquetaV() + ":\n";

            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());

            //Verifico si el numero es negativo
            int negativo = NuevoTemporal();
            int factor = NuevoTemporal();

            codigo += "=, 0, , t" + negativo + "\n";
            codigo += "+, P, " + (negativo - e.getTmpInicio() + e.getSize())  + ", t0\n";
            codigo += "=, t0, t" + negativo + ", stack\n";
            
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize())  + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";

            codigo += "jge, t" + rsOp.getValor() + ", 0, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            codigo += "=, 1, , t" + negativo + "\n";
            codigo += "+, P, " + (negativo - e.getTmpInicio() + e.getSize())  + ", t0\n";
            codigo += "=, t0, t" + negativo + ", stack\n";
            codigo += "-, 0, 1, t" + factor + "\n";
            codigo += "+, P, " + (factor - e.getTmpInicio() + e.getSize())  + ", t0\n";
            codigo += "=, t0, t" + factor + ", stack\n";
            codigo += "*, t" + rsOp.getValor() + ", t" + factor + ", t" + rsOp.getValor() + "\n";
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize())  + ", t0\n";
            codigo += "=, t0, t" + rsOp.getValor() + ", stack\n";
            codigo += rsOp.getEtiquetaV() + ":\n";

            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());
            String etqCiclo = NuevaEtiqueta();
            int tmpCiclo = NuevoTemporal();

            //guardo los caracteres en heap
            codigo += "=, H, 0, heap\n";
            codigo += "+, H, 1, H\n";

            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";
            codigo += "%, t" + rsOp.getValor() + ", 10, t" + tmpCiclo + "\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmpCiclo + ", stack\n";

            codigo += etqCiclo + ":\n";
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";
            codigo += "jl, t" + rsOp.getValor() + ", 1, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";

            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + tmpCiclo + "\n";
            codigo += "+, t" + tmpCiclo + ", 48, t" + tmpCiclo + "\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmpCiclo + ", stack\n";

            codigo += "=, H, t" + tmpCiclo + ", heap\n";
            codigo += "+, H, 1, H\n";

            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";
            codigo += "/, t" + rsOp.getValor() + ", 10, t" + rsOp.getValor() + "\n";
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + rsOp.getValor() + ", stack\n";
            
            codigo += "%, t" + rsOp.getValor() + ", 10, t" + tmpCiclo + "\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmpCiclo + ", stack\n";
            codigo += "jmp, , , " + etqCiclo + "\n";
            codigo += rsOp.getEtiquetaV() + ":\n";

            //Coloco el signo si es negativo
            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());

            codigo += "+, P, " + (negativo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + negativo + "\n";
            codigo += "je, t" + negativo + ", 0, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            codigo += "=, H, 45, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += rsOp.getEtiquetaV() + ":\n";

            //Si es 0 
            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());

            codigo += "+, P, " + (cero - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + cero + "\n";
            codigo += "je, t" + cero + ", 0, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            codigo += "=, H, 48, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += rsOp.getEtiquetaV() + ":\n";

            //Recorro de retroceso
            int tmp = NuevoTemporal();
            codigo += "-, H, 1, t" + tmp + "\n";
            codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) +", t0\n";
            codigo += "=, t0, t" + tmp + ", stack\n";

            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());
            etqCiclo = NuevaEtiqueta();

            rsOp.setValor(NuevoTemporal());
            codigo += "=, H, , t" + rsOp.getValor() + "\n";
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + rsOp.getValor() + ", stack\n";

            tmpCiclo = NuevoTemporal();
            codigo += "=, heap, t" + tmp + ", t" + tmpCiclo + "\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmpCiclo + ", stack\n";

            codigo += etqCiclo + ":\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + tmpCiclo + "\n";
            codigo += "je, t" + tmpCiclo + ", 0, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + tmpCiclo + "\n";
            codigo += "=, H, t" + tmpCiclo + ", heap\n";
            codigo += "+, H, 1, H\n";
            codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + tmp + "\n";
            codigo += "-, t" + tmp + ", 1, t" + tmp + "\n";
            codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmp + ", stack\n";
            codigo += "=, heap, t" + tmp + ", t" + tmpCiclo + "\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmpCiclo + ", stack\n";
            codigo += "jmp, , , " + etqCiclo + "\n";
            codigo += rsOp.getEtiquetaV() + ":\n";
            codigo += "=, H, 0, heap\n";
            codigo += "+, H, 1, H\n";

        } else if (op.getTipo().IsReal()) {

            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());

            //Obtegmo el valor de la pila
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";

            //verifico si es entero
            int entero = NuevoTemporal();
            codigo += "=, 0, , t" + entero + "\n";
            codigo += "+, P, " + (entero - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + entero + ", stack\n";
            
            int resultado = NuevoTemporal();
            codigo += "%, t" + rsOp.getValor() + ", 1, t" + resultado +"\n";
            codigo += "+, P, " + (resultado - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + resultado + ", stack\n";
            
            codigo += "jne, t" + resultado + ", 0, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            codigo += "=, 1, , t" + entero + "\n";
            codigo += "+, P, " + (entero - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + entero + ", stack\n";
            codigo += rsOp.getEtiquetaV() + ":\n";
            
            
            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());
            
            //Verifico si el número es negativo
            int negativo = NuevoTemporal();
            int factor = NuevoTemporal();
            codigo += "=, 0, , t" + negativo + "\n";
            codigo += "+, P, " + (negativo - e.getTmpInicio() + e.getSize())  + ", t0\n";
            codigo += "=, t0, t" + negativo + ", stack\n";
            codigo += "jge, t" + rsOp.getValor() + ", 0, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            codigo += "=, 1, , t" + negativo + "\n";
            codigo += "+, P, " + (negativo - e.getTmpInicio() + e.getSize()) +", t0\n";
            codigo += "=, t0, t" + negativo + ", stack\n";
            codigo += "-, 0, 1, t" + factor + "\n";
            codigo += "+, P, " + (factor - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + factor + ", stack\n";
            codigo += "*, t" + rsOp.getValor() + ", t" + factor + ", t" + rsOp.getValor() + "\n";
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + rsOp.getValor() + ", stack\n";
            codigo += rsOp.getEtiquetaV() + ":\n";

            //Obtengo el valor de la pila
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";

            //Verifico si es menor que 1.0
            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());
            int menor = NuevoTemporal();
            codigo += "=, 0, , t" + menor + "\n";
            codigo += "+, P, " + (menor - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + menor + ", stack\n";
            codigo += "jge, t" + rsOp.getValor() + ", 1, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            codigo += "=, 1, , t" + menor + "\n";
            codigo += "+, P, " + (menor - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + menor + ", stack\n";
            codigo += rsOp.getEtiquetaV() + ":\n";

            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());
            int contador = NuevoTemporal();
            int tmpCiclo = NuevoTemporal();
            String etqCiclo = NuevaEtiqueta();

            codigo += "=, 0, , t" + contador + "\n";
            codigo += "+, P, " + (contador - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + contador + ", stack\n";
            codigo += "*, t" + rsOp.getValor() + ", 10, t" + rsOp.getValor() + "\n";
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + rsOp.getValor() + ", stack\n";

            codigo += etqCiclo + ":\n";
            //Obtengo el valor de la pila
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";
            codigo += "%, t" + rsOp.getValor() + ", 10, t" + tmpCiclo + "\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmpCiclo + ", stack\n";
            codigo += "jg, t" + tmpCiclo + ", 0, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaV() + ":\n";
            codigo += "+, t" + contador + ", 1, t" + contador + "\n";
            codigo += "+, P, " + (contador - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + contador + ", stack\n";
            codigo += "*, t" + rsOp.getValor() + ", 10, t" + rsOp.getValor() + "\n";
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + rsOp.getValor() + ", stack\n";
            codigo += "jmp, , , " + etqCiclo + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";

            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";
            codigo += "/, t" + rsOp.getValor() + ", 10, t" + rsOp.getValor() + "\n";
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) +", t0\n";
            codigo += "=, t0, t" + rsOp.getValor() + ", stack\n";

            
            codigo += "=, H, 0, heap\n";
            codigo += "+, H, 1, H\n";
            
            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());
            //agrego .0 si no tiene decimales
            codigo += "je, t" + entero + ", 0, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            codigo += "=, H, 48, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += "=, H, 46, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += rsOp.getEtiquetaV() + ":\n";
            
            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());
            etqCiclo = NuevaEtiqueta();
            
            int contador2 = NuevoTemporal();
            String etqV = NuevaEtiqueta();
            String etqF = NuevaEtiqueta();
            tmpCiclo = NuevoTemporal();

            codigo += "=, 0, , t" + contador2 + "\n";
            codigo += "+, P, " + (contador2 - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + contador2 + ", stack\n";

            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";
            codigo += "%, t" + rsOp.getValor() + ", 10, t" + tmpCiclo + "\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmpCiclo + ", stack\n";

            codigo += etqCiclo + ":\n";
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";
            codigo += "jl, t" + rsOp.getValor() + ", 1, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + tmpCiclo + "\n";
            codigo += "+, t" + tmpCiclo + ", 48, t" + tmpCiclo + "\n";
            
            int tmp2 = NuevoTemporal();
            
            codigo += "*, t" + tmpCiclo + ", 10, t" + tmpCiclo + "\n";
            codigo += "%, t" + tmpCiclo +", 10, t" + tmp2 + "\n";
            codigo += "-, t" + tmpCiclo +", t" + tmp2 +", t" +tmpCiclo + "\n";
            codigo += "/, t" + tmpCiclo +", 10, t" + tmpCiclo + "\n";
            
            codigo += "+, P, " + (tmp2 - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmp2 + ", stack\n"; 
            
            codigo += "=, H, t" + tmpCiclo + ", heap\n";
            codigo += "+, H, 1, H\n";
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";
            codigo += "/, t" + rsOp.getValor() + ", 10, t" + rsOp.getValor() + "\n";
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + rsOp.getValor() + ", stack\n";
            codigo += "%, t" + rsOp.getValor() + ", 10, t" + tmpCiclo + "\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmpCiclo + ", stack\n";

            codigo += "+, P, " + (contador2 - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + contador2 + "\n";
            codigo += "+, t" + contador2 + ", 1, t" + contador2 + "\n";
            codigo += "+, P, " + (contador2 - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + contador2 + ", stack\n";
            codigo += "+, P, " + (contador - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + contador + "\n";
            codigo += "je, t" + contador2 + ", t" + contador + ", " + etqV + "\n";
            codigo += "jmp, , , " + etqF + "\n";
            codigo += etqV + ":\n";
            codigo += "=, H, 46, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += etqF + ":\n";

            codigo += "jmp, , , " + etqCiclo + "\n";
            codigo += rsOp.getEtiquetaV() + ":\n";

            //Coloco el 0 antes del punto
            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());

            codigo += "+, P, " + (menor - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + menor + "\n";
            codigo += "je, t" + menor + ", 0, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            codigo += "=, H, 48, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += rsOp.getEtiquetaV() + ":\n";

            //Coloco el (-) 
            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());

            codigo += "+, P, " + (negativo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + negativo + "\n";
            codigo += "je, t" + negativo + ", 0, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            codigo += "=, H, 45, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += rsOp.getEtiquetaV() + ":\n";

            int tmp = NuevoTemporal();
            codigo += "-, H, 1, t" + tmp + "\n";
            codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmp + ", stack\n";

            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());
            etqCiclo = NuevaEtiqueta();

            rsOp.setValor(NuevoTemporal());
            codigo += "=, H, , t" + rsOp.getValor() + "\n";
            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + rsOp.getValor() + ", stack\n";

            tmpCiclo = NuevoTemporal();

            codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) +", t0\n";
            codigo += "=, stack, t0, t" + tmp + "\n";
            codigo += "=, heap, t" + tmp + ", t" + tmpCiclo + "\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmpCiclo + ", stack\n";
            codigo += etqCiclo + ":\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + tmpCiclo + "\n";
            codigo += "je, t" + tmpCiclo + ", 0, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + tmpCiclo + "\n";
            codigo += "=, H, t" + tmpCiclo + ", heap\n";
            codigo += "+, H, 1, H\n";
            codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + tmp + "\n";
            codigo += "-, t" + tmp + ", 1, t" + tmp + "\n";
            codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmp + ", stack\n";
            codigo += "=, heap, t" + tmp + ", t" + tmpCiclo + "\n";
            codigo += "+, P, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0, t" + tmpCiclo + ", stack\n";
            codigo += "jmp, , , " + etqCiclo + "\n";
            codigo += rsOp.getEtiquetaV() + ":\n";
            codigo += "=, H, 0, heap\n";
            codigo += "+, H, 1, H\n";

        } else if (op.getTipo().IsChar()) {

            int tmp = NuevoTemporal();
            codigo += "=, H, , t" + tmp + "\n";
            codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0,  t" + tmp + ", stack\n";

            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";
            codigo += "=, H, t" + rsOp.getValor() + ", heap\n";
            codigo += "+, H, 1, H\n";

            codigo += "=, H, 0, heap\n";
            codigo += "+, H, 1, H\n";
            rsOp.setValor(tmp);

        } else if (op.getTipo().IsBoolean()) {

            int tmp = NuevoTemporal();
            codigo += "=, H, , t" + tmp + "\n";
            codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0,  t" + tmp + ", stack\n";

            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";

            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());
            String etqSalida = NuevaEtiqueta();

            codigo += "je, t" + rsOp.getValor() + ", 0, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaV() + ":\n";
            //false
            codigo += "=, H, 102, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += "=, H, 97, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += "=, H, 108, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += "=, H, 115, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += "=, H, 101, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += "jmp, , , " + etqSalida + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            //true
            codigo += "=, H, 116, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += "=, H, 114, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += "=, H, 117, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += "=, H, 101, heap\n";
            codigo += "+, H, 1, H\n";

            codigo += etqSalida + ":\n";
            codigo += "=, H, 0, heap\n";
            codigo += "+, H, 1, H\n";
            rsOp.setValor(tmp);
        } else {
            int tmp = NuevoTemporal();
            codigo += "=, H, , t" + tmp + "\n";
            codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, t0,  t" + tmp + ", stack\n";

            codigo += "+, P, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
            codigo += "=, stack, t0, t" + rsOp.getValor() + "\n";

            rsOp.setEtiquetaV(NuevaEtiqueta());
            rsOp.setEtiquetaF(NuevaEtiqueta());

            codigo += "jge, t" + rsOp.getValor() + ", 0, " + rsOp.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
            codigo += rsOp.getEtiquetaF() + ":\n";
            codigo += "=, H, 78, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += "=, H, 73, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += "=, H, 76, heap\n";
            codigo += "+, H, 1, H\n";
            codigo += rsOp.getEtiquetaV() + ":\n";

            codigo += "=, H, 0, heap\n";
            codigo += "+, H, 1, H\n";
            rsOp.setValor(tmp);
        }

        rsOp.setCodigo(codigo);
    }
}
