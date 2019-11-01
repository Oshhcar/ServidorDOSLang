/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion.ciclos;

import analizador.ErrorC;
import analizador.ast.NodoAST;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Tipo;
import analizador.ast.entorno.Type;
import analizador.ast.expresion.Expresion;
import analizador.ast.expresion.Literal;
import analizador.ast.expresion.operacion.Aritmetica;
import analizador.ast.expresion.operacion.Operador;
import analizador.ast.expresion.operacion.Relacional;
import analizador.ast.instruccion.Asignacion;
import analizador.ast.instruccion.Instruccion;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class For extends Instruccion {

    private Asignacion Asignacion;
    private Expresion Expresion;
    private boolean Downto;
    private NodoAST Sentencia;

    public For(Asignacion Asignacion, Expresion Expresion, boolean Downto, NodoAST Sentencia, int Linea, int Columna) {
        super(Linea, Columna);
        this.Asignacion = Asignacion;
        this.Expresion = Expresion;
        this.Downto = Downto;
        this.Sentencia = Sentencia;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores, Entorno global) {
        Result result = new Result();
        String codigo = "";

        Result rsAsignacion = Asignacion.GetCuadruplos(e, errores, global);

        if (!rsAsignacion.getCodigo().equals("")) {
            codigo += rsAsignacion.getCodigo();

            Operador op;
            Operador op2;

            if (Downto) {
                op = Operador.MAYORIGUAL;
                op2 = Operador.RESTA;
            } else {
                op = Operador.MENORIGUAL;
                op2 = Operador.SUMA;
            }

            Relacional condicion = new Relacional(Asignacion.getTarget(), Expresion, op, Linea, Columna);
            condicion.setCortoCircuito(true);

            Result rsCondicion = condicion.GetCuadruplos(e, errores);

            String copy = rsCondicion.getEtiquetaF();
            rsCondicion.setEtiquetaF(rsCondicion.getEtiquetaV());
            rsCondicion.setEtiquetaV(copy);

            String etqCiclo = NuevaEtiqueta();
            e.getSalidaCiclo().push(NuevaEtiqueta());
            String etqContinue = NuevaEtiqueta();
            e.getContinueCiclo().push(etqContinue);
            
            codigo += etqCiclo + ":\n";
            codigo += rsCondicion.getCodigo();
            codigo += rsCondicion.getEtiquetaV();

            //Bloque
            if (Sentencia instanceof Instruccion) {
                codigo += ((Instruccion) Sentencia).GetCuadruplos(e, errores, global).getCodigo();
            } else if (Sentencia instanceof Expresion) {
                codigo += ((Expresion) Sentencia).GetCuadruplos(e, errores).getCodigo();
            }
            
            
            
            codigo += etqContinue +":\n";
            //sumo o resto
            Asignacion asigna = new Asignacion(Asignacion.getTarget(),new Aritmetica(Asignacion.getTarget(), new Literal(new Tipo(Type.INTEGER), 1, Linea, Columna), op2, Linea, Columna), Linea, Columna);
            codigo += asigna.GetCuadruplos(e, errores, global).getCodigo();
            
            codigo += "jmp, , , " + etqCiclo+"\n";
            codigo += rsCondicion.getEtiquetaF();
            codigo += e.getSalidaCiclo().pop() + ":\n";
            
            e.getContinueCiclo().pop();
            
        }

        result.setCodigo(codigo);
        return result;
    }

    /**
     * @return the Asignacion
     */
    public Asignacion getAsignacion() {
        return Asignacion;
    }

    /**
     * @param Asignacion the Asignacion to set
     */
    public void setAsignacion(Asignacion Asignacion) {
        this.Asignacion = Asignacion;
    }

    /**
     * @return the Expresion
     */
    public Expresion getExpresion() {
        return Expresion;
    }

    /**
     * @param Expresion the Expresion to set
     */
    public void setExpresion(Expresion Expresion) {
        this.Expresion = Expresion;
    }

    /**
     * @return the Downto
     */
    public boolean isDownto() {
        return Downto;
    }

    /**
     * @param Downto the Downto to set
     */
    public void setDownto(boolean Downto) {
        this.Downto = Downto;
    }

    /**
     * @return the Sentencia
     */
    public NodoAST getSentencia() {
        return Sentencia;
    }

    /**
     * @param Sentencia the Sentencia to set
     */
    public void setSentencia(NodoAST Sentencia) {
        this.Sentencia = Sentencia;
    }

}
