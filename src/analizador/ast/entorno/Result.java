/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.entorno;

/**
 *
 * @author oscar
 */
public class Result {
    
    private String Valor;
    private String Codigo;
    private String EtiquetaV;
    private String EtiquetaF;
    private int PtrStack;
    private Simbolo Simbolo;
    private Tipo Tipo;

    public Result() {
        this.Tipo = new Tipo(Type.UNDEFINED);
    }

    /**
     * @return the Valor
     */
    public String getValor() {
        return Valor;
    }

    /**
     * @param Valor the Valor to set
     */
    public void setValor(String Valor) {
        this.Valor = Valor;
    }

    /**
     * @return the Codigo
     */
    public String getCodigo() {
        return Codigo;
    }

    /**
     * @param Codigo the Codigo to set
     */
    public void setCodigo(String Codigo) {
        this.Codigo = Codigo;
    }

    /**
     * @return the EtiquetaV
     */
    public String getEtiquetaV() {
        return EtiquetaV;
    }

    /**
     * @param EtiquetaV the EtiquetaV to set
     */
    public void setEtiquetaV(String EtiquetaV) {
        this.EtiquetaV = EtiquetaV;
    }

    /**
     * @return the EtiquetaF
     */
    public String getEtiquetaF() {
        return EtiquetaF;
    }

    /**
     * @param EtiquetaF the EtiquetaF to set
     */
    public void setEtiquetaF(String EtiquetaF) {
        this.EtiquetaF = EtiquetaF;
    }

    /**
     * @return the PtrStack
     */
    public int getPtrStack() {
        return PtrStack;
    }

    /**
     * @param PtrStack the PtrStack to set
     */
    public void setPtrStack(int PtrStack) {
        this.PtrStack = PtrStack;
    }

    /**
     * @return the Simbolo
     */
    public Simbolo getSimbolo() {
        return Simbolo;
    }

    /**
     * @param Simbolo the Simbolo to set
     */
    public void setSimbolo(Simbolo Simbolo) {
        this.Simbolo = Simbolo;
    }

    /**
     * @return the Tipo
     */
    public Tipo getTipo() {
        return Tipo;
    }

    /**
     * @param Tipo the Tipo to set
     */
    public void setTipo(Tipo Tipo) {
        this.Tipo = Tipo;
    }
    
    
}
