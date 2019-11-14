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
public class Simbolo {

    private String Id;
    private Tipo Tipo;
    private Rol Rol;
    private int Tam;
    private int Pos;
    private String Ambito;
    private int NumParam;
    private int TipoParam;
    private Entorno Entorno;
    private String Firma;
    private boolean Nil;
    private boolean Constante;
    private Simbolo Record;
    private int TmpEntorno;
    private int FactorTmp;

    public Simbolo(String Id, Tipo Tipo, Rol Rol, int Tam, int Pos, String Ambito, int NumParam, int TipoParam) {
        this.Id = Id;
        this.Tipo = Tipo;
        this.Rol = Rol;
        this.Tam = Tam;
        this.Pos = Pos;
        this.Ambito = Ambito;
        this.NumParam = NumParam;
        this.TipoParam = TipoParam;
        this.Entorno = null;
        this.Firma = null;
        this.Nil = false;
        this.Constante = false;
        this.Record = null;
        this.TmpEntorno = 0;
        this.FactorTmp = 0;
    }

    /**
     * Constructor para Types
     *
     * @param Id the Id to set
     * @param Tipo the Tipo to set
     * @param Ambito the Ambito to set
     */
    public Simbolo(String Id, Tipo Tipo, String Ambito) {
        this.Id = Id;
        this.Tipo = Tipo;
        this.Rol = Rol.TYPE;
        this.Tam = -1;
        this.Pos = -1;
        this.Ambito = Ambito;
        this.NumParam = -1;
        this.TipoParam = -1;
        this.Entorno = null;
        this.Firma = null;
        this.Nil = false;
        this.Constante = false;
        this.Record = null;
        this.TmpEntorno = 0;
        this.FactorTmp = 0;
    }

    /**
     * Constructor para Var
     *
     * @param Id the Id to set
     * @param Tipo the Tipo to set
     * @param Pos the Pos to set
     * @param Ambito the Ambito to set
     */
    public Simbolo(String Id, Tipo Tipo, int Pos, String Ambito) {
        this.Id = Id;
        this.Tipo = Tipo;
        this.Rol = Rol.LOCAL;
        this.Tam = -1;
        this.Pos = Pos;
        this.Ambito = Ambito;
        this.NumParam = -1;
        this.TipoParam = -1;
        this.Entorno = null;
        this.Firma = null;
        this.Nil = false;
        this.Constante = false;
        this.Record = null;
        this.TmpEntorno = 0;
        this.FactorTmp = 0;
    }

    /**
     * Constructor para Var de un record
     *
     * @param Id the Id to set
     * @param Tipo the Tipo to set
     * @param Pos the Pos to set
     * @param Ambito the Ambito to set
     */
    public Simbolo(String Id, Tipo Tipo, int Pos, String Ambito, Simbolo Record) {
        this.Id = Id;
        this.Tipo = Tipo;
        this.Rol = Rol.LOCAL;
        this.Tam = -1;
        this.Pos = Pos;
        this.Ambito = Ambito;
        this.NumParam = -1;
        this.TipoParam = -1;
        this.Entorno = null;
        this.Firma = null;
        this.Nil = false;
        this.Constante = false;
        this.Record = Record;
        this.TmpEntorno = 0;
        this.FactorTmp = 0;
    }

    /**
     * Constructor para parametro
     *
     * @param Id the Id to set
     * @param Tipo the Tipo to set
     * @param Pos the Pos to set
     * @param Ambito the Ambito to set
     * @param TipoParam he TipoParam to set
     */
    public Simbolo(String Id, Tipo Tipo, int Pos, String Ambito, int TipoParam) {
        this.Id = Id;
        this.Tipo = Tipo;
        this.Rol = Rol.PARAMETER;
        this.Tam = -1;
        this.Pos = Pos;
        this.Ambito = Ambito;
        this.NumParam = -1;
        this.TipoParam = TipoParam;
        this.Entorno = null;
        this.Firma = null;
        this.Nil = false;
        this.Constante = false;
        this.Record = null;
        this.TmpEntorno = 0;
        this.FactorTmp = 0;
    }

    /**
     * Constructor para Funcion
     *
     * @param Id the Id to set
     * @param Tipo the Tipo to set
     * @param Tam the Tam to set
     * @param Ambito the Ambito to set
     * @param NumParam the NumParam to set
     * @param Entorno the Entorno to set
     * @param Firma the Firma to set
     */
    public Simbolo(String Id, Tipo Tipo, int Tam, String Ambito, int NumParam, Entorno Entorno, String Firma) {
        this.Id = Id;
        this.Tipo = Tipo;
        this.Rol = Rol.FUNCION;
        this.Tam = Tam;
        this.Pos = -1;
        this.Ambito = Ambito;
        this.NumParam = NumParam;
        this.TipoParam = -1;
        this.Entorno = Entorno;
        this.Firma = Firma;
        this.Nil = false;
        this.Constante = false;
        this.Record = null;
        this.TmpEntorno = 0;
        this.FactorTmp = 0;
    }

    /**
     * Constructor para Metodo
     *
     * @param Id the Id to set
     * @param Tam the Tam to set
     * @param Ambito the Ambito to set
     * @param NumParam the NumParam to set
     * @param Entorno the Entorno to set
     * @param Firma the Firma to set
     */
    public Simbolo(String Id, int Tam, String Ambito, int NumParam, Entorno Entorno, String Firma) {
        this.Id = Id;
        this.Tipo = new Tipo(Type.UNDEFINED);
        this.Rol = Rol.METHOD;
        this.Tam = Tam;
        this.Pos = -1;
        this.Ambito = Ambito;
        this.NumParam = NumParam;
        this.TipoParam = -1;
        this.Entorno = Entorno;
        this.Firma = Firma;
        this.Nil = false;
        this.Constante = false;
        this.Record = null;
        this.TmpEntorno = 0;
        this.FactorTmp = 0;
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

    /**
     * @return the Rol
     */
    public Rol getRol() {
        return Rol;
    }

    /**
     * @param Rol the Rol to set
     */
    public void setRol(Rol Rol) {
        this.Rol = Rol;
    }

    /**
     * @return the Tam
     */
    public int getTam() {
        return Tam;
    }

    /**
     * @param Tam the Tam to set
     */
    public void setTam(int Tam) {
        this.Tam = Tam;
    }

    /**
     * @return the Pos
     */
    public int getPos() {
        return Pos;
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
     * @return the NumParam
     */
    public int getNumParam() {
        return NumParam;
    }

    /**
     * @param NumParam the NumParam to set
     */
    public void setNumParam(int NumParam) {
        this.NumParam = NumParam;
    }

    /**
     * @return the TipoParam
     */
    public int getTipoParam() {
        return TipoParam;
    }

    /**
     * @param TipoParam the TipoParam to set
     */
    public void setTipoParam(int TipoParam) {
        this.TipoParam = TipoParam;
    }

    /**
     * @return the Entorno
     */
    public Entorno getEntorno() {
        return Entorno;
    }

    /**
     * @param Entorno the Entorno to set
     */
    public void setEntorno(Entorno Entorno) {
        this.Entorno = Entorno;
    }

    /**
     * @return the Firma
     */
    public String getFirma() {
        return Firma;
    }

    /**
     * @param Firma the Firma to set
     */
    public void setFirma(String Firma) {
        this.Firma = Firma;
    }

    /**
     * @return the Nil
     */
    public boolean isNil() {
        return Nil;
    }

    /**
     * @param Nil the Nil to set
     */
    public void setNil(boolean Nil) {
        this.Nil = Nil;
    }

    /**
     * @return the Constante
     */
    public boolean isConstante() {
        return Constante;
    }

    /**
     * @param Constante the Constante to set
     */
    public void setConstante(boolean Constante) {
        this.Constante = Constante;
    }

    /**
     * @return the Record
     */
    public Simbolo getRecord() {
        return Record;
    }

    /**
     * @param Record the Record to set
     */
    public void setRecord(Simbolo Record) {
        this.Record = Record;
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

    /**
     * @return the FactorTmp
     */
    public int getFactorTmp() {
        return FactorTmp;
    }

    /**
     * @param FactorTmp the FactorTmp to set
     */
    public void setFactorTmp(int FactorTmp) {
        this.FactorTmp = FactorTmp;
    }
}
