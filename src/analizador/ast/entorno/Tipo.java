/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.entorno;

import java.util.Objects;

/**
 *
 * @author oscar
 */
public class Tipo {
    
    private Type Tipo;
    private String Id;
    private Tipo TipoPadre;
    
    public Tipo(Type Tipo){
        this.Tipo = Tipo;
        this.Id = null;
        this.TipoPadre = null;
    }
    
    /**
     * Cuando Es un id(subtipo)
     * @param Id the Id to set
     */
    public Tipo(String Id){ 
        this.Tipo = Type.UNDEFINED;
        this.Id = Id;
        this.TipoPadre = null;
}

    public boolean IsChar() { return this.Tipo == Type.CHAR; }
    public boolean IsInteger(){ return this.Tipo == Type.INTEGER; }
    public boolean IsReal(){ return this.Tipo == Type.REAL; }
    public boolean IsString(){ return this.Tipo == Type.STRING; }
    public boolean IsWord(){ return this.Tipo == Type.WORD; }
    public boolean IsBoolean(){ return this.Tipo == Type.BOOLEAN; }
    public boolean IsEnum(){ return this.Tipo == Type.ENUM; }
    public boolean IsArray(){ return this.Tipo == Type.ARRAY; }
    public boolean IsRecord(){ return this.Tipo == Type.RECORD; }
    public boolean IsNil(){ return this.Tipo == Type.NIL; }
    public boolean IsUndefined(){ return this.Tipo == Type.UNDEFINED; }
    
    public boolean IsNumeric(){
        return IsInteger() || IsReal() || IsChar();
    }
    
    @Override
    public String toString(){
        if(Id != null){
            return Id;
        }
        return Tipo.name().toLowerCase();
    }
    
    /**
     *No lo voy a usar porque dicen que no se valida
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o){
        if(o instanceof Tipo){
            Tipo t = (Tipo)o;
            
            if(Id != null){
                if(t.getId() != null){
                    return Id.equals(t.getId());
                }
            } else {
                if(t.getId() == null){
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
        if(TipoPadre != null)
            return TipoPadre.getTipo();
        return Tipo;
    }

    /**
     * @param Tipo the Tipo to set
     */
    public void setTipo(Type Tipo) {
        this.Tipo = Tipo;
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
}
