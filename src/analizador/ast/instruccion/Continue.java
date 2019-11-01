/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Continue extends Instruccion{

    public Continue(int Linea, int Columna) {
        super(Linea, Columna);
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
        Result result = new Result();
        String codigo = "";
        
        if(e.getContinueCiclo().size() > 0){
            codigo += "jmp, , , " + e.getContinueCiclo().lastElement() + "\n";
        } else {
            errores.add(new ErrorC("Semántico", Linea, Columna, "Sentencia Continue no se encuentra dentro de un ciclo."));
        }
        
        result.setCodigo(codigo);
        return result;
    }
    
}
