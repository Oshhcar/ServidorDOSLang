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
public class Relacional extends Operacion {

    private boolean CortoCircuito;

    public Relacional(Expresion Op1, Expresion Op2, Operador op, int Linea, int Columna) {
        super(Op1, Op2, op, Linea, Columna);
        CortoCircuito = false;
    }

    /**
     *
     * @param e
     * @param errores
     * @return result Agregar la comparacion entre strucs
     */
    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";

        Result rsOp1 = Op1.GetCuadruplos(e, errores);
        Result rsOp2 = Op2.GetCuadruplos(e, errores);

        if (Op1.getTipo().IsNumeric() && Op2.getTipo().IsNumeric()) { //Si los dos son numéricos
            Tipo.setTipo(Type.BOOLEAN);

            codigo += rsOp1.getCodigo();
            codigo += rsOp2.getCodigo();

            codigo += Comparacion(result, rsOp1, rsOp2, e);

        } else if (Op == Operador.IGUAL || Op == Operador.DIFERENTE) {
            if ((Op1.getTipo().IsString() || Op1.getTipo().IsWord()) && (Op2.getTipo().IsString() || Op2.getTipo().IsWord())) {
                Tipo.setTipo(Type.BOOLEAN);

                codigo += rsOp1.getCodigo();
                codigo += rsOp2.getCodigo();

                codigo += ObtenerValorCadena(rsOp1, e);
                codigo += ObtenerValorCadena(rsOp2, e);

                codigo += Comparacion(result, rsOp1, rsOp2, e);

            } else if (Op1.getTipo().IsBoolean() && Op2.getTipo().IsBoolean()) {
                //booleanos 

                Tipo.setTipo(Type.BOOLEAN);

                codigo += rsOp1.getCodigo();
                codigo += rsOp2.getCodigo();

                codigo += Comparacion(result, rsOp1, rsOp2, e);

            } else { //comprobar otros tipos ---------------->
                errores.add(new ErrorC("Semántico", Linea, Columna, "Error de tipos en operación relacional."));
            }

        } else {
            errores.add(new ErrorC("Semántico", Linea, Columna, "Error de tipos en operación relacional."));
        }

        result.setCodigo(codigo);
        return result;
    }

    public String Comparacion(Result result, Result rsOp1, Result rsOp2, Entorno e) {
        String codigo = "";

        String op = "";

        switch (Op) {
            case MAYOR:
                op = "jg";
                break;
            case MENOR:
                op = "jl";
                break;
            case MAYORIGUAL:
                op = "jge";
                break;
            case MENORIGUAL:
                op = "jle";
                break;
            case IGUAL:
                op = "je";
                break;
            case DIFERENTE:
                op = "jne";
                break;
        }

        result.setEtiquetaV(NuevaEtiqueta());
        result.setEtiquetaF(NuevaEtiqueta());

        if (!CortoCircuito) {

            result.setValor(NuevoTemporal());

            codigo += "=, 0, , t" + result.getValor() + "\n";

            codigo += "=, stack, " + (rsOp1.getValor() - e.getTmpInicio() + e.getSize()) + ", t" + rsOp1.getValor() + "\n";
            codigo += "=, stack, " + (rsOp2.getValor() - e.getTmpInicio() + e.getSize()) + ", t" + rsOp2.getValor() + "\n";
            codigo += op + ", t" + rsOp1.getValor() + ", t" + rsOp2.getValor() + ", " + result.getEtiquetaV() + "\n";
            codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
            codigo += result.getEtiquetaV() + ":\n";
            codigo += "=, 1, , t" + result.getValor() + "\n";
            codigo += result.getEtiquetaF() + ":\n";

            codigo += "=, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t" + result.getValor() + ", stack\n";
        } else {
            
            codigo += "=, stack, " + (rsOp1.getValor() - e.getTmpInicio() + e.getSize()) + ", t" + rsOp1.getValor() + "\n";
            codigo += "=, stack, " + (rsOp2.getValor() - e.getTmpInicio() + e.getSize()) + ", t" + rsOp2.getValor() + "\n";
            codigo += op + ", t" + rsOp1.getValor() + ", t" + rsOp2.getValor() + ", " + result.getEtiquetaF() + "\n";
            codigo += "jmp, , , " + result.getEtiquetaV() + "\n";
            
            result.setEtiquetaV(result.getEtiquetaV()+":\n");
            result.setEtiquetaF(result.getEtiquetaF()+":\n");
        }

        return codigo;
    }

    public String ObtenerValorCadena(Result rsOp, Entorno e) {
        String codigo = "";

        rsOp.setEtiquetaV(NuevaEtiqueta());
        rsOp.setEtiquetaF(NuevaEtiqueta());
        String etqCiclo = NuevaEtiqueta();
        int contador = NuevoTemporal();
        int tmpCiclo = NuevoTemporal();
        int factor = NuevoTemporal();

        codigo += "=, 0, , t" + contador + "\n";
        codigo += "=, " + (contador - e.getTmpInicio() + e.getSize()) + ", t" + contador + ", stack\n";
        codigo += "=, 1, , t" + factor + "\n";
        codigo += "=, " + (factor - e.getTmpInicio() + e.getSize()) + ", t" + factor + ", stack\n";

        codigo += etqCiclo + ":\n";
        codigo += "=, stack, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t" + rsOp.getValor() + "\n";
        codigo += "=, heap, t" + rsOp.getValor() + ", t" + tmpCiclo + "\n";
        codigo += "=, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t" + tmpCiclo + ", stack\n";
        codigo += "=, stack, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t" + tmpCiclo + "\n";
        codigo += "je, t" + tmpCiclo + ", 0, " + rsOp.getEtiquetaV() + "\n";
        codigo += "jmp, , , " + rsOp.getEtiquetaF() + "\n";
        codigo += rsOp.getEtiquetaF() + ":\n";
        codigo += "=, stack, " + (factor - e.getTmpInicio() + e.getSize()) + ", t" + factor + "\n";
        codigo += "=, stack, " + (tmpCiclo - e.getTmpInicio() + e.getSize()) + ", t" + tmpCiclo + "\n";
        codigo += "*, t" + tmpCiclo + ", t" + factor + ", t" + tmpCiclo + "\n";
        codigo += "=, stack, " + (contador - e.getTmpInicio() + e.getSize()) + ", t" + contador + "\n";
        codigo += "+, t" + contador + ", t" + tmpCiclo + ", t" + contador + "\n";
        codigo += "=, " + (contador - e.getTmpInicio() + e.getSize()) + ", t" + contador + ", stack\n";
        codigo += "=, stack, " + (factor - e.getTmpInicio() + e.getSize()) + ", t" + factor + "\n";
        codigo += "*, t" + factor + ", 100, t" + factor + "\n";
        codigo += "=, " + (factor - e.getTmpInicio() + e.getSize()) + ", t" + factor + ", stack\n";
        codigo += "=, stack, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t" + rsOp.getValor() + "\n";
        codigo += "+, t" + rsOp.getValor() + ", 1, t" + rsOp.getValor() + "\n";
        codigo += "=, " + (rsOp.getValor() - e.getTmpInicio() + e.getSize()) + ", t" + rsOp.getValor() + ", stack\n";
        codigo += "jmp, , , " + etqCiclo + "\n";
        codigo += rsOp.getEtiquetaV() + ":\n";

        //codigo += "print(%e, t" + contador + ")//IMPRIMO EL TOTAL DE LA CADENA\n";
        //codigo += "print(%c, 10)\n";
        rsOp.setValor(contador);

        return codigo;
    }

    /**
     * @return the CortoCircuito
     */
    public boolean isCortoCircuito() {
        return CortoCircuito;
    }

    /**
     * @param CortoCircuito the CortoCircuito to set
     */
    public void setCortoCircuito(boolean CortoCircuito) {
        this.CortoCircuito = CortoCircuito;
    }
}
