 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.expresion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Tipo;
import analizador.ast.entorno.Type;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Acceso extends Expresion {

    private Expresion Target;
    private ArrayList<Expresion> Accesos;
    private boolean Acceso;
    private boolean ObtenerTipo; //sirve para sizeof

    public Acceso(Expresion Target, ArrayList<Expresion> Accesos, int Linea, int Columna) {
        super(Linea, Columna);
        this.Target = Target;
        this.Accesos = Accesos;
        this.Acceso = true;
        this.ObtenerTipo = false;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";

        Result rsTarget = Target.GetCuadruplos(e, errores);

        result.setSimbolo(rsTarget.getSimbolo());
        
        if (Target.getTipo().IsArray()) {
            if (Accesos.size() <= Target.getTipo().getDimensiones().size()) {
                
                if(ObtenerTipo){
                    Tipo = Target.getTipo().getTipoArray();
                    return result;
                }
                
                if(Target.getTipo().getDimensiones().size() == Accesos.size()){
                    Tipo = Target.getTipo().getTipoArray();
                } else {
                    Tipo = Target.getTipo();
                }
                
                codigo += rsTarget.getCodigo();

                for (int i = 0; i < Accesos.size(); i++) {
                    Expresion dim = Accesos.get(i);

                    int tmpInf = NuevoTemporal();
                    int tmpSup = NuevoTemporal();

                    //Aumento 1 para el limite inf
                    codigo += "+, t" + rsTarget.getValor() + ", 1, t" + rsTarget.getValor() + "\n";
                    codigo += "=, heap, t" + rsTarget.getValor() + ", t" + tmpInf + "\n";
                    codigo += "+, P, " + (tmpInf - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, t0, t" + tmpInf + ", stack\n";

                    //Aumento 1 para el limite sup
                    codigo += "+, t" + rsTarget.getValor() + ", 1, t" + rsTarget.getValor() + "\n";
                    codigo += "=, heap, t" + rsTarget.getValor() + ", t" + tmpSup + "\n";
                    codigo += "+, P, " + (tmpSup - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, t0, t" + tmpSup + ", stack\n";

                    //Aumento 1 para empezar los valores
                    codigo += "+, t" + rsTarget.getValor() + ", 1, t" + rsTarget.getValor() + "\n";
                    
                    Result rsDim = dim.GetCuadruplos(e, errores);

                    if (dim.getTipo().IsInteger() || dim.getTipo().IsChar()) {

                        codigo += rsDim.getCodigo();

                        result.setEtiquetaV(NuevaEtiqueta());
                        result.setEtiquetaF(NuevaEtiqueta());

                        codigo += "jge, t" + rsDim.getValor() + ", t" + tmpInf + ", " + result.getEtiquetaV() + "\n";
                        codigo += "jmp, , , " + result.getEtiquetaF() + "\n";
                        codigo += result.getEtiquetaV() + ":\n";

                        String etqV = NuevaEtiqueta();
                        String etqF = NuevaEtiqueta();
                        String salida = NuevaEtiqueta();

                        codigo += "jle, t" + rsDim.getValor() + ", t" + tmpSup + ", " + etqV + "\n";
                        codigo += "jmp, , , " + etqF + "\n";
                        codigo += etqV + ":\n";

                        //calculo el indice real
                        codigo += "-, t" + rsDim.getValor() + ", t" + tmpInf + ", t" + rsDim.getValor() + "\n";
                        codigo += "+, t" + rsTarget.getValor() + ", t" + rsDim.getValor() + ", t" + rsTarget.getValor() + "\n";

                        if (i + 1 != Accesos.size()) {
                            codigo += "=, heap, t" + rsTarget.getValor() + ", t" + rsTarget.getValor() + "\n";
                        }

                        codigo += "jmp, , , " + salida + "\n";
                        codigo += result.getEtiquetaF() + ":\n";
                        codigo += etqF + ":\n";

                        codigo += "print(%c, 65)\n"
                                + "print(%c, 114)\n"
                                + "print(%c, 114)\n"
                                + "print(%c, 97)\n"
                                + "print(%c, 121)\n"
                                + "print(%c, 73)\n"
                                + "print(%c, 110)\n"
                                + "print(%c, 100)\n"
                                + "print(%c, 101)\n"
                                + "print(%c, 120)\n"
                                + "print(%c, 79)\n"
                                + "print(%c, 117)\n"
                                + "print(%c, 116)\n"
                                + "print(%c, 79)\n"
                                + "print(%c, 102)\n"
                                + "print(%c, 66)\n"
                                + "print(%c, 111)\n"
                                + "print(%c, 117)\n"
                                + "print(%c, 110)\n"
                                + "print(%c, 100)\n"
                                + "print(%c, 115)\n"
                                + "print(%c, 32)\n"
                                + "print(%c, 69)\n"
                                + "print(%c, 120)\n"
                                + "print(%c, 99)\n"
                                + "print(%c, 101)\n"
                                + "print(%c, 112)\n"
                                + "print(%c, 116)\n"
                                + "print(%c, 105)\n"
                                + "print(%c, 111)\n"
                                + "print(%c, 110)\n"
                                + "print(%c, 46)\n"
                                + "print(%c, 10)\n";

                        //Error para detener el programa
                        codigo += "-, 0, 1, t" + rsTarget.getValor() + "\n";
                        codigo += "=, heap, t" + rsTarget.getValor() + ", t" + rsTarget.getValor() + "\n";

                        codigo += salida + ":\n";

                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La dimensión debe ser integer."));
                        Tipo = new Tipo(Type.UNDEFINED);
                        result.setCodigo("");
                        return result;
                    }
                }

                codigo += "+, P, " + (rsTarget.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, t0, t" + rsTarget.getValor() + ", stack\n";

                if (Acceso) {
                    result.setValor(NuevoTemporal());
                    codigo += "=, heap, t" + rsTarget.getValor() + ", t" + result.getValor() + "\n";
                    codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, t0, t" + result.getValor() + ", stack\n";
                } else {
                    result.setEstructura("heap");
                    result.setValor(rsTarget.getValor());
                }
            } else {
                errores.add(new ErrorC("Semántico", Linea, Columna, "Las dimensiones no coinciden."));
            }
        } else {
            errores.add(new ErrorC("Semántico", Linea, Columna, "La variable no es de tipo array."));
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
     * @return the Accesos
     */
    public ArrayList<Expresion> getAccesos() {
        return Accesos;
    }

    /**
     * @param Accesos the Accesos to set
     */
    public void setAccesos(ArrayList<Expresion> Accesos) {
        this.Accesos = Accesos;
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
