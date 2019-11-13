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
    private Stack<String> ContinueCiclo; //cuando agrege aqui lo de exit, añadirlo a ent en withdo
    private String EtqSalida;
    private int TmpP; //Apuntador al ambito de las variables de record.
    private int SizeTotal; //Size total con temporales
    private boolean GuardarGlobal; //Guardar en tabla global
    private int TmpEntorno; //apuntador al entorno.
    
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
        EtqSalida = "";
        TmpP = 0;
        SizeTotal = 0;
        GuardarGlobal = false;
        TmpEntorno = 0;
    }

    public Entorno(String Ambito, Entorno Padre) {
        Simbolos = new ArrayList<>();
        Pos = 0;
        this.Ambito = Ambito;
        this.Padre = Padre;
        Size = 0;
        TmpInicio = 0;
        TmpFin = 0;
        SalidaCiclo = new Stack<>();
        ContinueCiclo = new Stack<>();
        EtqSalida = "";
        TmpP = 0;
        SizeTotal = 0;
        GuardarGlobal = false;
        TmpEntorno = 0;
    }

    public void Add(Simbolo s) {
        Simbolos.add(s);
    }

    public Simbolo Get(String id) {

        for (Simbolo s : Simbolos) {
            if (s.getRol() != Rol.METHOD && s.getRol() != Rol.FUNCION) {
                if (s.getId().equalsIgnoreCase(id)) {
                    s.setTmpEntorno(TmpEntorno);
                    return s;
                }

                if (s.getTipo().IsEnum()) {
                    if (s.getTipo().ExisteEnum(id)) {
                        s.setTmpEntorno(TmpEntorno);
                        return s;
                    }
                }
            }
        }

        return Padre != null ? Padre.Get(id) : null;
    }

    public Simbolo GetLocal(String id) {

        for (Simbolo s : Simbolos) {
            if (s.getRol() != Rol.METHOD && s.getRol() != Rol.FUNCION) {
                if (s.getId().equalsIgnoreCase(id)) {
                    s.setTmpEntorno(TmpEntorno);
                    return s;
                }
                
                if (s.getTipo().IsEnum()) {
                    if (s.getTipo().ExisteEnum(id)) {
                        s.setTmpEntorno(TmpEntorno);
                        return s;
                    }
                }
            }
        }

        return null;
    }
    
    public Simbolo GetMetodo(String firma) {

        for (Simbolo s : Simbolos) {
            if (s.getRol() == Rol.METHOD || s.getRol() == Rol.FUNCION) {
                //System.out.println(s.getFirma()+  " == " + firma);
                if (s.getFirma().equals(firma)) {
                    return s;
                }
            }
        }

        return Padre != null ? Padre.GetMetodo(firma) : null;
    }


    public Simbolo GetMetodoLocal(String firma) {

        for (Simbolo s : Simbolos) {
            if (s.getRol() == Rol.METHOD || s.getRol() == Rol.FUNCION) {
                if (s.getFirma().equals(firma)) {
                    return s;
                }
            }
        }

        return null;
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

    /**
     * @return the TmpP
     */
    public int getTmpP() {
        return TmpP;
    }

    /**
     * @param TmpP the TmpP to set
     */
    public void setTmpP(int TmpP) {
        this.TmpP = TmpP;
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

    /**
     * @return the SizeTotal
     */
    public int getSizeTotal() {
        return SizeTotal;
    }

    /**
     * @param SizeTotal the SizeTotal to set
     */
    public void setSizeTotal(int SizeTotal) {
        this.SizeTotal = SizeTotal;
    }

    /**
     * @return the GuardarGlobal
     */
    public boolean isGuardarGlobal() {
        return GuardarGlobal;
    }

    /**
     * @param GuardarGlobal the GuardarGlobal to set
     */
    public void setGuardarGlobal(boolean GuardarGlobal) {
        this.GuardarGlobal = GuardarGlobal;
    }

    /**
     * @return the TmpEntorno
     */
    public int getTmpEntorno() {
        return TmpEntorno;
    }

    /**
     * @param TmpEntorno the TmpEntorno to set
     */
    public void setTmpEntorno(int TmpEntorno) {
        this.TmpEntorno = TmpEntorno;
    }

}
