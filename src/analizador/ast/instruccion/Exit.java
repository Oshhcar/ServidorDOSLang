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
public class Exit extends Instruccion{

    public Exit(int Linea, int Columna) {
        super(Linea, Columna);
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
        Result result = new Result();
        result.setCodigo("jmp, , , " + e.getEtqSalida() + "\n");
        return result;
    }
    
}
