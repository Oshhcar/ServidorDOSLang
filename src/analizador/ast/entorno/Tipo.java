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
public class Tipo {
    
    private Type Tipo;
    
    public Tipo(Type Tipo){
        this.Tipo = Tipo;
    }

    public boolean IsChar(){ return this.Tipo == Type.CHAR; }
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
    
    /**
     * @return the Tipo
     */
    public Type getTipo() {
        return Tipo;
    }

    /**
     * @param Tipo the Tipo to set
     */
    public void setTipo(Type Tipo) {
        this.Tipo = Tipo;
    }
}
