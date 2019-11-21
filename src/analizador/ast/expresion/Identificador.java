/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.expresion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Rol;
import analizador.ast.entorno.Simbolo;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Identificador extends Expresion {

    private String Id;
    private boolean Acceso;
    private boolean ObtenerTipo; //sirve para sizeof
    private boolean ObtenerSim; //Sirve para withDo

    public Identificador(String Id, int Linea, int Columna) {
        super(Linea, Columna);
        this.Id = Id;
        this.Acceso = true;
        this.ObtenerTipo = false;
        this.ObtenerSim = false;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";

        Simbolo sim = e.Get(Id);

        if (sim != null) {

            result.setSimbolo(sim);

            if (ObtenerTipo || ObtenerSim) {
                Tipo = sim.getTipo();

                if (sim.getTipo().IsArray()) {
                    Tipo = sim.getTipo().getTipoArray();
                }

                result.setCodigo("");
                return result;
            }

            if (sim.getRol() == Rol.LOCAL) {
                if (sim.getRecord() != null) { //si es un atributo de un record //dentro de withdo
                    Tipo = sim.getTipo();

//                    int tmp = NuevoTemporal();
//                    //Valor record
//                    codigo += "+, P, " + sim.getRecord().getPos() + ", t" + tmp + "\n";
//                    codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
//                    codigo += "=, t0, t" + tmp + ", stack\n";
                    int tmpValor = NuevoTemporal();
                    codigo += "+, t" + e.getTmpP() + ", " + sim.getPos() + ", t" + tmpValor + "\n";
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
                    //if (!Acceso && sim.isConstante()) {
                    //    errores.add(new ErrorC("Semántico", Linea, Columna, Id + " es una constante, no se puede cambiar el valor."));
                    //} else {
                    Tipo = sim.getTipo();
                    int tmp = NuevoTemporal();

                    codigo += "+, P, " + sim.getPos() + ", t" + tmp + "\n";
                    codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, t0, t" + tmp + ", stack\n";

                    if (Acceso) {
                        result.setValor(NuevoTemporal());
                        codigo += "=, stack, t" + tmp + ", t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";
                    } else {
                        result.setEstructura("stack");
                        result.setValor(tmp);
                    }
                    //}
                }
            } else if (sim.getRol() == Rol.PARAMETER) {
                Tipo = sim.getTipo();
                int tmp = NuevoTemporal();

                codigo += "+, P, " + sim.getPos() + ", t" + tmp + "\n";
                codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, t0, t" + tmp + ", stack\n";

                if (Acceso) {
                    result.setValor(NuevoTemporal());
                    codigo += "=, stack, t" + tmp + ", t" + result.getValor() + "\n";
                    codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, t0, t" + result.getValor() + ", stack\n";

                    if (sim.getTipoParam() == 0 && !(sim.getTipo().IsArray() || sim.getTipo().IsRecord())) {
                        tmp = NuevoTemporal();
                        codigo += "+, P, " + (sim.getPos() + 1) + ", t" + tmp + "\n";
                        codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmp + ", stack\n";

                        int tmpEstruc = NuevoTemporal();
                        codigo += "=, stack, t" + tmp + ", t" + tmpEstruc + "\n";
                        codigo += "+, P, " + (tmpEstruc - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + tmpEstruc + ", stack\n";

                        result.setEtiquetaV(NuevaEtiqueta());
                        result.setEtiquetaF(NuevaEtiqueta());
                        String etqSalida = NuevaEtiqueta();

                        int res = NuevoTemporal();

                        codigo += "jne, t" + tmpEstruc + ", 0, " + result.getEtiquetaV() + "\n";
                        codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                        codigo += result.getEtiquetaF() + ":\n";
                        codigo += "=, stack, t" + result.getValor() + ", t" + res + "\n";
                        codigo += "jmp, , , " + etqSalida + "\n";
                        codigo += result.getEtiquetaV() + ":\n";
                        codigo += "=, heap, t" + result.getValor() + ", t" + res + "\n";
                        codigo += etqSalida + ":\n";

                        codigo += "+, P, " + (res - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + res + ", stack\n";

                        result.setValor(res);
                    }

                } else {
                    result.setEstructura("stack");
                    result.setValor(tmp);
                }

            } else if (sim.getRol() == Rol.GLOBAL) {
                Tipo = sim.getTipo();
                int tmp = NuevoTemporal();

                codigo += "+, P, " + sim.getPos() + ", t" + tmp + "\n";
                codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, t0, t" + tmp + ", stack\n";

                result.setValor(NuevoTemporal());
                codigo += "=, stack, t" + tmp + ", t" + result.getValor() + "\n";
                codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, t0, t" + result.getValor() + ", stack\n";

                if (Acceso) {
                    tmp = NuevoTemporal();

                    codigo += "=, stack, t" + result.getValor() + ", t" + tmp + "\n";
                    codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, t0, t" + tmp + ", stack\n";
                    result.setValor(tmp);

                } else {
                    result.setEstructura("stack");
                }

            } else if (sim.getTipo().IsEnum()) {
                if (!sim.getId().equalsIgnoreCase(Id)) {
                    if (Acceso) {
                        Tipo = sim.getTipo();

                        int valor = sim.getTipo().GetPosicion(Id);
                        result.setValor(NuevoTemporal());
                        codigo += "=, " + valor + ", , t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, Id + " es un valor enum, no se puede asignar un valor."));
                    }
                } else {
                    if (Acceso) {
                        errores.add(new ErrorC("Semántico", Linea, Columna, Id + " es un tipo enum, no se puede obtener valor."));
                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, Id + " es un tipo enum, no se puede asignar."));
                    }
                }
            } else {
                errores.add(new ErrorC("Semántico", Linea, Columna, Id + " no es una variable."));
            }
        } else {
            errores.add(new ErrorC("Semántico", Linea, Columna, "No se ha definido una variable con el id: " + Id + "."));
        }

        result.setCodigo(codigo);
        return result;
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

    /**
     * @return the ObtenerSim
     */
    public boolean isObtenerSim() {
        return ObtenerSim;
    }

    /**
     * @param ObtenerSim the ObtenerSim to set
     */
    public void setObtenerSim(boolean ObtenerSim) {
        this.ObtenerSim = ObtenerSim;
    }

}
