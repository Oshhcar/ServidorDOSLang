/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.expresion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Simbolo;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Atributo extends Expresion {

    private Expresion Target;
    private String Id;
    private boolean Acceso;
    private boolean ObtenerTipo; //sirve para sizeof

    public Atributo(Expresion Target, String Id, int Linea, int Columna) {
        super(Linea, Columna);
        this.Target = Target;
        this.Id = Id;
        this.Acceso = true;
        this.ObtenerTipo = false;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";

        if (Target instanceof Identificador) {
            ((Identificador) Target).setAcceso(false);
        } else if (Target instanceof Atributo) {
            ((Atributo) Target).setAcceso(false);
        }

        Result rsTarget = Target.GetCuadruplos(e, errores);

        if (rsTarget.getEstructura() != null) {
            if (Target.getTipo().IsRecord()) {
                Simbolo sim = rsTarget.getSimbolo().getEntorno().GetLocal(Id);

                if (sim != null) {
                    Tipo = sim.getTipo();
                    
                    int tmp = NuevoTemporal();
                    //Valor record
                    codigo += "+, P, " + rsTarget.getSimbolo().getPos() + ", t" + tmp + "\n";
                    codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, t0, t" + tmp + ", stack\n";

                    int tmpValor = NuevoTemporal();
                    codigo += "=, stack, t" + tmp + ", t" + tmpValor + "\n";
                    codigo += "+, t" + tmpValor + ", " + sim.getPos() + ", t" + tmpValor + "\n";
                    codigo += "+, P, " + (tmpValor - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, t0, t" + tmpValor + ", stack\n";
                    
                    if (Acceso) {
                        result.setValor(NuevoTemporal());
                        codigo += "=, heap, t" + tmpValor + ", t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";
                    } else {
                        result.setEstructura("heap");
                        result.setValor(tmpValor);
                    }
                    
                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "Atributo " + Id + " no definido."));
                }
            } else {
                errores.add(new ErrorC("Semántico", Linea, Columna, "La variable no es de tipo record."));
            }
        }

        result.setCodigo(codigo);
        return result;
    }

    /**
     * @return the Target
     */
    public Expresion getTarget() {
        return Target;
    }

    /**
     * @param Target the Target to set
     */
    public void setTarget(Expresion Target) {
        this.Target = Target;
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
     * @return the Acceso
     */
    public boolean isAcceso() {
        return Acceso;
    }

    /**
     * @param Acceso the Acceso to set
     */
    public void setAcceso(boolean Acceso) {
        this.Acceso = Acceso;
    }

    /**
     * @return the ObtenerTipo
     */
    public boolean isObtenerTipo() {
        return ObtenerTipo;
    }

    /**
     * @param ObtenerTipo the ObtenerTipo to set
     */
    public void setObtenerTipo(boolean ObtenerTipo) {
        this.ObtenerTipo = ObtenerTipo;
    }

}
