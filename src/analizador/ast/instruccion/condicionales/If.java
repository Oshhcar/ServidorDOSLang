/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion.condicionales;

import analizador.ErrorC;
import analizador.ast.NodoAST;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.expresion.Expresion;
import analizador.ast.expresion.Literal;
import analizador.ast.expresion.operacion.Logica;
import analizador.ast.expresion.operacion.Relacional;
import analizador.ast.expresion.operacion.Unario;
import analizador.ast.instruccion.Instruccion;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class If extends Instruccion {

    private Expresion Condicion;
    private NodoAST Sentencia;
    private NodoAST SentenciaElse;
    private String EtqSalida;

    public If(Expresion Condicion, NodoAST Sentencia, int Linea, int Columna) {
        super(Linea, Columna);
        this.Condicion = Condicion;
        this.Sentencia = Sentencia;
        this.SentenciaElse = null;
        this.EtqSalida = null;
    }

    public If(Expresion Condicion, NodoAST Sentencia, NodoAST SentenciaElse, int Linea, int Columna) {
        super(Linea, Columna);
        this.Condicion = Condicion;
        this.Sentencia = Sentencia;
        this.SentenciaElse = SentenciaElse;
        this.EtqSalida = null;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";

        if (Condicion instanceof Relacional) {
            ((Relacional) Condicion).setCortoCircuito(true);
        } else if (Condicion instanceof Logica) {
            ((Logica) Condicion).setEvaluar(true);
        } else if (Condicion instanceof Unario) {
            ((Unario) Condicion).setEvaluar(true);
        }

        Result rsCondicion = Condicion.GetCuadruplos(e, errores);

        if (Condicion instanceof Literal) {
            String cod = rsCondicion.getCodigo();

            rsCondicion.setEtiquetaV(NuevaEtiqueta());
            rsCondicion.setEtiquetaF(NuevaEtiqueta());

            cod += "je, t" + rsCondicion.getValor() + ", 1, " + rsCondicion.getEtiquetaF() + "\n";
            cod += "jmp, , , " + rsCondicion.getEtiquetaV() + "\n";

            rsCondicion.setEtiquetaV(rsCondicion.getEtiquetaV() + ":\n");
            rsCondicion.setEtiquetaF(rsCondicion.getEtiquetaF() + ":\n");

            rsCondicion.setCodigo(cod);
        }

        if (Condicion instanceof Relacional || Condicion instanceof Literal) {
            String copy = rsCondicion.getEtiquetaF();
            rsCondicion.setEtiquetaF(rsCondicion.getEtiquetaV());
            rsCondicion.setEtiquetaV(copy);
        }

        codigo += rsCondicion.getCodigo();
        codigo += rsCondicion.getEtiquetaV();
        
        boolean isPrincipal = false;
        
        if(EtqSalida == null){
            EtqSalida = NuevaEtiqueta();
            isPrincipal = true;
        }
        
        //Bloque
        if (Sentencia instanceof Instruccion) {
            codigo += ((Instruccion) Sentencia).GetCuadruplos(e, errores).getCodigo();
        } else if (Sentencia instanceof Expresion) {
            codigo += ((Expresion) Sentencia).GetCuadruplos(e, errores).getCodigo();
        }

        codigo += "jmp, , , " + EtqSalida + "\n";
        codigo += rsCondicion.getEtiquetaF();

        //Else
        if (SentenciaElse != null) {
            
            if(SentenciaElse instanceof If){
                ((If) SentenciaElse).setEtqSalida(EtqSalida);
            }
            
            if (SentenciaElse instanceof Instruccion) {
                codigo += ((Instruccion) SentenciaElse).GetCuadruplos(e, errores).getCodigo();
            } else if (SentenciaElse instanceof Expresion) {
                codigo += ((Expresion) SentenciaElse).GetCuadruplos(e, errores).getCodigo();
            }
            
            if(!(SentenciaElse instanceof If)){
                codigo += "jmp, , , " + EtqSalida + "\n";
            } else {
                ((If) SentenciaElse).setEtqSalida(null);
            }
            
        } 
        
        if(isPrincipal){
            codigo += EtqSalida +":\n";
        }
        
        EtqSalida = null;
        
        result.setCodigo(codigo);
        return result;
    }

    /**
     * @return the Condicion
     */
    public Expresion getCondicion() {
        return Condicion;
    }

    /**
     * @param Condicion the Condicion to set
     */
    public void setCondicion(Expresion Condicion) {
        this.Condicion = Condicion;
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

    /**
     * @return the SentenciaElse
     */
    public NodoAST getSentenciaElse() {
        return SentenciaElse;
    }

    /**
     * @param SentenciaElse the SentenciaElse to set
     */
    public void setSentenciaElse(NodoAST SentenciaElse) {
        this.SentenciaElse = SentenciaElse;
    }

    /**
     * @return the EtqSalida
     */
    public String getEtqSalida() {
        return EtqSalida;
    }

    /**
     * @param EtqSalida the EtqSalida to set
     */
    public void setEtqSalida(String EtqSalida) {
        this.EtqSalida = EtqSalida;
    }

}
