/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.entorno;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author oscar
 */
public class Entorno {

    private ArrayList<Simbolo> Simbolos;
    private int Pos;
    private String Ambito;
    private Entorno Padre;
    private int Size;
    private int TmpInicio;
    private int TmpFin;
    private Stack<String> SalidaCiclo;
    private Stack<String> ContinueCiclo;

    public Entorno(String Ambito) {
        Simbolos = new ArrayList<>();
        Pos = 0;
        this.Ambito = Ambito;
        Padre = null;
        Size = 0;
        TmpInicio = 0;
        TmpFin = 0;
        SalidaCiclo = new Stack<>();
        ContinueCiclo = new Stack<>();
    }

    public Entorno(String Ambito, Entorno Padre) {
        Simbolos = new ArrayList<>();
        Pos = 0;
        this.Ambito = Ambito;
        this.Padre = Padre;
        Size = 0;
        TmpInicio = 0;
        TmpFin = 0;
    }

    public void Add(Simbolo s) {
        Simbolos.add(s);
    }

    public Simbolo Get(String id) {

        for (Simbolo s : Simbolos) {
            if (s.getRol() != Rol.METHOD && s.getRol() != Rol.FUNCION) {
                if (s.getId().equalsIgnoreCase(id)) {
                    return s;
                }
                
                if(s.getTipo().IsEnum()){
                    if(s.getTipo().ExisteEnum(id)){
                        return s;
                    }
                }
            }
        }
        
        return Padre != null ? Padre.Get(id) : null;
    }

    public Simbolo GetGlobal(String id) {

        for (Simbolo s : Simbolos) {
            if (s.getRol() != Rol.METHOD && s.getRol() != Rol.FUNCION) {
                if (s.getId().equalsIgnoreCase(id)) {
                    return s;
                }
            }
        }

        return Padre != null ? Padre.GetGlobal(id) : null;
    }

    public void Recorrer() {

        for (Simbolo s : Simbolos) {
            System.out.println(s.getId() + ", " + s.getTipo().getTipo().toString() + ", " + s.getRol().toString()
                    + ", " + s.getTam() + ", " + s.getPos() + ", " + s.getAmbito() + ", " + s.getNumParam() + ", " + s.getTipoParam());

            if (s.getEntorno() != null) {
                s.getEntorno().Recorrer();
            }
        }

        if (Padre != null) {
            Padre.Recorrer();
        }
    }

    /**
     * @return the Simbolos
     */
    public ArrayList<Simbolo> getSimbolos() {
        return Simbolos;
    }

    /**
     * @param Simbolos the Simbolos to set
     */
    public void setSimbolos(ArrayList<Simbolo> Simbolos) {
        this.Simbolos = Simbolos;
    }

    /**
     * @return the Pos
     */
    public int getPos() {
        return Pos++;
    }

    /**
     * @param Pos the Pos to set
     */
    public void setPos(int Pos) {
        this.Pos = Pos;
    }

    /**
     * @return the Ambito
     */
    public String getAmbito() {
        return Ambito;
    }

    /**
     * @param Ambito the Ambito to set
     */
    public void setAmbito(String Ambito) {
        this.Ambito = Ambito;
    }

    /**
     * @return the Padre
     */
    public Entorno getPadre() {
        return Padre;
    }

    /**
     * @param Padre the Padre to set
     */
    public void setPadre(Entorno Padre) {
        this.Padre = Padre;
    }

    /**
     * @return the Size
     */
    public int getSize() {
        return Size;
    }

    public int getSizeTotal() {
        return Size + (TmpFin - TmpInicio);
    }

    /**
     * @param Size the Size to set
     */
    public void setSize(int Size) {
        this.Size = Size;
    }

    /**
     * @return the TmpInicio
     */
    public int getTmpInicio() {
        return TmpInicio;
    }

    /**
     * @param TmpInicio the TmpInicio to set
     */
    public void setTmpInicio(int TmpInicio) {
        this.TmpInicio = TmpInicio;
    }

    /**
     * @return the TmpFin
     */
    public int getTmpFin() {
        return TmpFin;
    }

    /**
     * @param TmpFin the TmpFin to set
     */
    public void setTmpFin(int TmpFin) {
        this.TmpFin = TmpFin;
    }

    /**
     * @return the SalidaCiclo
     */
    public Stack<String> getSalidaCiclo() {
        return SalidaCiclo;
    }

    /**
     * @param SalidaCiclo the SalidaCiclo to set
     */
    public void setSalidaCiclo(Stack<String> SalidaCiclo) {
        this.SalidaCiclo = SalidaCiclo;
    }

    /**
     * @return the ContinueCiclo
     */
    public Stack<String> getContinueCiclo() {
        return ContinueCiclo;
    }

    /**
     * @param ContinueCiclo the ContinueCiclo to set
     */
    public void setContinueCiclo(Stack<String> ContinueCiclo) {
        this.ContinueCiclo = ContinueCiclo;
    }
}
