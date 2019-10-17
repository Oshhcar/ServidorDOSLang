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
public class Literal extends Expresion{
    
    public Tipo Tipo;
    public Object Valor;
    
    public Literal(Tipo Tipo, Object Valor, int Linea, int Columna) {
        super(Linea, Columna);
        this.Tipo = Tipo;
        this.Valor = Valor;
    }

    @Override
    public Tipo GetTipo() {
        return this.Tipo;
    }
    
    
    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        result.setCodigo("");
        
        if(Tipo.IsInteger() || Tipo.IsReal() || Tipo.IsBoolean()){
            result.setValor(Valor.toString());
        } else if(Tipo.IsNil()){
            result.setValor(NuevoTemporal());
            result.setCodigo("-, 0, 1, " + result.getValor() + "\n");
        } else if(Tipo.IsChar()){
            if(Valor.toString().length() == 1){
                int val = Valor.toString().toCharArray()[0];
                result.setValor(val+"");
            }
        } else {
            result.setValor(NuevoTemporal());
            String codigo = "=, H, , " + result.getValor() + "\n";
            
            for(int c: Valor.toString().toCharArray()){
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
