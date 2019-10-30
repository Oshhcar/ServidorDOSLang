/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion;

import analizador.ErrorC;
import analizador.ast.NodoAST;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public abstract class Instruccion extends NodoAST{
    
    public Instruccion(int Linea, int Columna) {
        super(Linea, Columna);
    }
    
    public abstract Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global);
}
