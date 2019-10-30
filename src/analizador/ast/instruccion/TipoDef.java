/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.instruccion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Simbolo;
import analizador.ast.entorno.Tipo;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class TipoDef extends Instruccion {

    private ArrayList<String> Id;
    private Tipo Tipo;

    public TipoDef(ArrayList<String> Id, Tipo Tipo, int Linea, int Columna) {
        super(Linea, Columna);
        this.Id = Id;
        this.Tipo = Tipo;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {

        //Si es un tipo definido
        if (Tipo.getId() != null) {
            Simbolo type = e.Get(Tipo.getId());
            if (type == null) {
                errores.add(new ErrorC("Semántico", Linea, Columna, "No se ha definido un tipo con el id: " + Tipo.getId() + "."));
                return null;
            } else {
                Tipo.setTipoPadre(type.getTipo());
            }
        }

        Id.forEach((id) -> {
            if (e.Get(id) == null) {
                e.Add(new Simbolo(id, Tipo));
            } else {
                errores.add(new ErrorC("Semántico", Linea, Columna, "Ya se ha definido una variable con el id: " + id + "."));
            }
        });

        return null;
    }

    /**
     * @return the Id
     */
    public ArrayList<String> getId() {
        return Id;
    }

    /**
     * @param Id the Id to set
     */
    public void setId(ArrayList<String> Id) {
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

}
