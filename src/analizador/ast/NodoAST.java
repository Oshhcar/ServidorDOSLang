/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast;

/**
 *
 * @author oscar
 */
public class NodoAST {
    protected final int Linea;
    protected final int Columna;

    public static int Etiquetas;
    public static int Temporales;
    public static int H;
    
    public NodoAST(int Linea, int Columna) {
        this.Linea = Linea;
        this.Columna = Columna;
    }
    
    /**
     * @return NuevaEtiqueta
     */
    public String NuevaEtiqueta(){
        return "L"+(++Etiquetas);
    }
    
    /**
     * @return NuevoTemporal
     */
    public int NuevoTemporal(){
        return ++Temporales;
    }

    /**
     * @return the Linea
     */
    public int getLinea() {
        return Linea;
    }

    /**
     * @return the Columna
     */
    public int getColumna() {
        return Columna;
    }
}
