/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.expresion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Tipo;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Literal extends Expresion {

    public Object Valor;

    public Literal(Tipo Tipo, Object Valor, int Linea, int Columna) {
        super(Linea, Columna);
        this.Tipo = Tipo;
        this.Valor = Valor;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        result.setCodigo("");

        if (Tipo.IsInteger() || Tipo.IsReal() || Tipo.IsBoolean()) {
            result.setValor(NuevoTemporal());

            result.setCodigo("=, " + Valor.toString() + ", , t" + result.getValor() + "\n");
            result.setCodigo(result.getCodigo() + "=, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t" + result.getValor() + ", stack\n");

        } else if (Tipo.IsNil()) {
            result.setValor(NuevoTemporal());

            result.setCodigo("-, 0, 1, t" + result.getValor() + "\n");
            result.setCodigo(result.getCodigo() + "=, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t" + result.getValor() + ", stack\n");
        } else if (Tipo.IsChar()) {
            int val;

            if (Valor.toString().length() == 1) {
                val = Valor.toString().toCharArray()[0];
            } else {
                val = 0;
                if(Valor.toString().length()>1){
                    errores.add(new ErrorC("Léxico", Linea, Columna, "Cáracter solo puede tener un valor."));
                }
            }

            result.setValor(NuevoTemporal());

            result.setCodigo("=, " + val + ", , t" + result.getValor() + "\n");
            result.setCodigo(result.getCodigo() + "=, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t" + result.getValor() + ", stack\n");

        } else {
            result.setValor(NuevoTemporal());

            String codigo = "=, H, , t" + result.getValor() + "\n";
            codigo += "=, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t" + result.getValor() + ", stack\n";

            for (int c : Valor.toString().toCharArray()) {
                codigo += "=, H, " + c + ", " + "heap\n";
                codigo += "+, H, 1, H\n";
            }

            codigo += "=, H, 0, " + "heap\n";
            codigo += "+, H, 1, H\n";

            result.setCodigo(codigo);
        }

        return result;
    }
}
