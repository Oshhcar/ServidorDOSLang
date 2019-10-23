/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.expresion.operacion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.expresion.Expresion;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Unario extends Operacion {

    public Unario(Expresion Op1, Expresion Op2, Operador op, int Linea, int Columna) {
        super(Op1, Op2, op, Linea, Columna);
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";

        Result rsOp1 = Op1.GetCuadruplos(e, errores);

        if (Op1.getTipo().IsNumeric() || Op1.getTipo().IsBoolean()) {
            
            switch (Op) {
                case SUMA:
                    Tipo = Op1.getTipo();
                    codigo += rsOp1.getCodigo();
                    result.setValor(rsOp1.getValor());
                    break;
                case RESTA:
                    Tipo = Op1.getTipo();
                    codigo += rsOp1.getCodigo();
                    
                    codigo += "=, stack, "+(rsOp1.getValor()-e.getTmpInicio()+e.getSize())+", t"+rsOp1.getValor()+"\n";
                    
                    int factor = NuevoTemporal();
                    codigo += "-, 0, 1, t" + factor + "\n";
                 
                    codigo += "=, "+(factor-e.getTmpInicio()+e.getSize())+", t"+factor+", stack\n";
                    //codigo += "=, stack, "+(factor-e.getTmpInicio()+e.getSize())+", t"+factor+"\n";
                    
                    result.setValor(NuevoTemporal());
                    codigo += "*, t" + rsOp1.getValor()+", t" + factor + ", t" + result.getValor()+"\n";
                    
                    codigo += "=, "+(result.getValor()-e.getTmpInicio()+e.getSize())+", t"+result.getValor()+", stack\n";
                    
                    break;
                //not
            }

        }

        result.setCodigo(codigo);
        return result;
    }

}
