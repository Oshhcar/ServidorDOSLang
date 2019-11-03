/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.entorno;

import analizador.ast.expresion.Expresion;
import analizador.ast.instruccion.VarDef;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author oscar
 */
public class Tipo {

    private Type Tipo;
    private String Id;
    private Tipo TipoPadre;
    private ArrayList<String> Lista;
    private String IdEnum;
    private Expresion LimiteInf;
    private Expresion LimiteSup;
    private ArrayList<VarDef> Variables;
    private Entorno Entorno;

    public Tipo(Type Tipo) {
        this.Tipo = Tipo;
        this.Id = null;
        this.TipoPadre = null;
        this.Lista = null;
        this.IdEnum = null;
        this.LimiteInf = null;
        this.LimiteSup = null;
        this.Variables = null;
        this.Entorno = null;
    }

    /**
     * Cuando Es un id(subtipo)
     *
     * @param Id the Id to set
     */
    public Tipo(String Id) {
        this.Tipo = Type.UNDEFINED;
        this.Id = Id;
        this.TipoPadre = null;
        this.Lista = null;
        this.IdEnum = null;
        this.LimiteInf = null;
        this.LimiteSup = null;
        this.Variables = null;
        this.Entorno = null;
    }

    /**
     * Cuando Es un rango
     *
     * @param LimiteInf the LimiteInf to set
     * @param LimiteSup the LimiteSup to set
     */
    public Tipo(Expresion LimiteInf, Expresion LimiteSup) {
        this.Tipo = Type.UNDEFINED;
        this.Id = null;
        this.TipoPadre = null;
        this.Lista = null;
        this.IdEnum = null;
        this.LimiteInf = LimiteInf;
        this.LimiteSup = LimiteSup;
        this.Variables = null;
        this.Entorno = null;
    }
    
    /**
     * Cuando Es un record
     *
     * @param Variables the Variables to set
     * @param Tipo the Tipo to set
     */
    public Tipo(ArrayList<VarDef> Variables, Type Tipo) {
        this.Tipo = Tipo;
        this.Id = null;
        this.TipoPadre = null;
        this.Lista = null;
        this.IdEnum = null;
        this.LimiteInf = null;
        this.LimiteSup = null;
        this.Variables = Variables;
        this.Entorno = null;
    }

    /**
     * Cuando es un enum
     *
     * @param Lista the Lista to set
     */
    public Tipo(ArrayList<String> Lista) {
        this.Tipo = Type.ENUM;
        this.Id = null;
        this.TipoPadre = null;
        this.Lista = Lista;
        this.IdEnum = null;
        this.LimiteInf = null;
        this.LimiteSup = null;
        this.Entorno = null;
    }

    /**
     * Busca id en Enum
     *
     * @param id the id to found
     * @return true si lo encontro
     */
    public boolean ExisteEnum(String id) {
        if (this.Lista != null) {
            for (String val : Lista) {
                if (val.equalsIgnoreCase(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Busca posicion Enum
     *
     * @param id the id to found
     * @return posicion int
     */
    public int GetPosicion(String id) {
        int contador = 0;
        if (Lista != null) {
            for (String val : Lista) {
                if (val.equalsIgnoreCase(id)) {
                    return contador;
                }
                contador++;
            }
        }
        return 0;
    }

    public boolean IsChar() {
        return this.Tipo == Type.CHAR;
    }

    public boolean IsInteger() {
        return this.Tipo == Type.INTEGER;
    }

    public boolean IsReal() {
        return this.Tipo == Type.REAL;
    }

    public boolean IsString() {
        return this.Tipo == Type.STRING;
    }

    public boolean IsWord() {
        return this.Tipo == Type.WORD;
    }

    public boolean IsBoolean() {
        return this.Tipo == Type.BOOLEAN;
    }

    public boolean IsEnum() {
        return this.Tipo == Type.ENUM;
    }

    public boolean IsArray() {
        return this.Tipo == Type.ARRAY;
    }

    public boolean IsRecord() {
        return this.Tipo == Type.RECORD;
    }

    public boolean IsNil() {
        return this.Tipo == Type.NIL;
    }

    public boolean IsUndefined() {
        return this.Tipo == Type.UNDEFINED;
    }

    public boolean IsNumeric() {
        return IsInteger() || IsReal() || IsChar() || IsEnum();
    }

    @Override
    public String toString() {
        if (Id != null) {
            return Id;
        }
        return Tipo.name().toLowerCase();
    }

    /**
     * No lo voy a usar porque dicen que no se valida
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Tipo) {
            Tipo t = (Tipo) o;

            if (Id != null) {
                if (t.getId() != null) {
                    return Id.equalsIgnoreCase(t.getId());
                }
            } else {
                if (t.getId() == null) {
                    return Tipo == t.getTipo();
                }
            }

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.Tipo);
        hash = 59 * hash + Objects.hashCode(this.Id);
        return hash;
    }

    /**
     * @return the Tipo
     */
    public Type getTipo() {
        if (TipoPadre != null) {
            return TipoPadre.getTipo();
        }
        return Tipo;
    }

    /**
     * @param Tipo the Tipo to set
     */
    public void setTipo(Type Tipo) {
        this.Tipo = Tipo;
    }

    /**
     * @return the IdPadre
     */
    public String getIdPadre() {
        if (TipoPadre != null) {
            return TipoPadre.getIdPadre();
        }
        return Id;
    }

    /**
     * @return the Id
     */
    public String getId() {
        return Id;
    }

    /**
     * @param Id the Id to set
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     * @return the TipoPadre
     */
    public Tipo getTipoPadre() {
        return TipoPadre;
    }

    /**
     * @param TipoPadre the TipoPadre to set
     */
    public void setTipoPadre(Tipo TipoPadre) {
        this.TipoPadre = TipoPadre;
        this.Tipo = TipoPadre.getTipo();
    }

    /**
     * @return the Lista
     */
    public ArrayList<String> getLista() {
        if (TipoPadre != null) {
            return TipoPadre.getLista();
        }
        return Lista;
    }

    /**
     * @param Lista the Lista to set
     */
    public void setLista(ArrayList<String> Lista) {
        this.Lista = Lista;
    }

    /**
     * @return the IdEnum
     */
    public String getIdEnum() {
        if (TipoPadre != null) {
            return TipoPadre.getIdEnum();
        }
        return IdEnum;
    }

    /**
     * @param IdEnum the IdEnum to set
     */
    public void setIdEnum(String IdEnum) {
        this.IdEnum = IdEnum;
    }

    /**
     * @return the LimiteInf
     */
    public Expresion getLimiteInf() {
        if (TipoPadre != null) {
            return TipoPadre.getLimiteInf();
        }
        return LimiteInf;
    }

    /**
     * @param LimiteInf the LimiteInf to set
     */
    public void setLimiteInf(Expresion LimiteInf) {
        this.LimiteInf = LimiteInf;
    }

    /**
     * @return the LimiteSup
     */
    public Expresion getLimiteSup() {
        if (TipoPadre != null) {
            return TipoPadre.getLimiteSup();
        }
        return LimiteSup;
    }

    /**
     * @param LimiteSup the LimiteSup to set
     */
    public void setLimiteSup(Expresion LimiteSup) {
        this.LimiteSup = LimiteSup;
    }

    /**
     * @return the Variables
     */
    public ArrayList<VarDef> getVariables() {
        return Variables;
    }

    /**
     * @param Variables the Variables to set
     */
    public void setVariables(ArrayList<VarDef> Variables) {
        this.Variables = Variables;
    }

    /**
     * @return the Entorno
     */
    public Entorno getEntorno() {
        if(TipoPadre != null){
            return TipoPadre.getEntorno();
        }
        return Entorno;
    }

    /**
     * @param Entorno the Entorno to set
     */
    public void setEntorno(Entorno Entorno) {
        this.Entorno = Entorno;
    }
}
