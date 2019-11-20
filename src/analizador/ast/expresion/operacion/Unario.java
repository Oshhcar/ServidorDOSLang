/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizador.ast.expresion.operacion;

import analizador.ErrorC;
import analizador.ast.entorno.Entorno;
import analizador.ast.entorno.Result;
import analizador.ast.entorno.Type;
import analizador.ast.expresion.Acceso;
import analizador.ast.expresion.Atributo;
import analizador.ast.expresion.Call;
import analizador.ast.expresion.Expresion;
import analizador.ast.expresion.Identificador;
import analizador.ast.expresion.Literal;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Unario extends Operacion {

    public Unario(Expresion Op1, Operador op, int Linea, int Columna) {
        super(Op1, null, op, Linea, Columna);
        Evaluar = false;
    }

    private boolean Evaluar;

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";

        if (Op1 instanceof Relacional) {
            ((Relacional) Op1).setCortoCircuito(true);
        } else if (Op1 instanceof Logica) {
            ((Logica) Op1).setEvaluar(true);
        } else if (Op1 instanceof Unario) {
            ((Unario) Op1).setEvaluar(true);
        }
        
        Result rsOp1 = Op1.GetCuadruplos(e, errores);

        if (Op1.getTipo().IsNumeric()) {

            switch (Op) {
                case SUMA:
                    Tipo = Op1.getTipo();
                    codigo += rsOp1.getCodigo();
                    result.setValor(rsOp1.getValor());
                    break;
                case RESTA:
                    Tipo = Op1.getTipo();
                    codigo += rsOp1.getCodigo();

                    codigo += "+, P, " + (rsOp1.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, stack, t0, t" + rsOp1.getValor() + "\n";

                    int factor = NuevoTemporal();
                    codigo += "-, 0, 1, t" + factor + "\n";

                    codigo += "+, P, " + (factor - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, t0, t" + factor + ", stack\n";
                    //codigo += "=, stack, "+(factor-e.getTmpInicio()+e.getSize())+", t"+factor+"\n";

                    result.setValor(NuevoTemporal());
                    codigo += "*, t" + rsOp1.getValor() + ", t" + factor + ", t" + result.getValor() + "\n";

                    codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, t0, t" + result.getValor() + ", stack\n";

                    break;
                default:
                    errores.add(new ErrorC("Semántico", Linea, Columna, "Error de tipos en operación lógica."));
                    break;
            }

        } else if (Op1.getTipo().IsBoolean()) {
            if (Op == Operador.NOT) {
                Tipo.setTipo(Type.BOOLEAN);

                if (Op1 instanceof Literal || Op1 instanceof Call || Op1 instanceof Identificador
                    || Op1 instanceof Acceso || Op1 instanceof Atributo) {
                    String cod = rsOp1.getCodigo();

                    rsOp1.setEtiquetaV(NuevaEtiqueta());
                    rsOp1.setEtiquetaF(NuevaEtiqueta());

                    cod += "je, t" + rsOp1.getValor() + ", 1, " + rsOp1.getEtiquetaF() + "\n";
                    cod += "jmp, , , " + rsOp1.getEtiquetaV() + "\n";

                    rsOp1.setEtiquetaV(rsOp1.getEtiquetaV() + ":\n");
                    rsOp1.setEtiquetaF(rsOp1.getEtiquetaF() + ":\n");

                    rsOp1.setCodigo(cod);
                }

                codigo += rsOp1.getCodigo();

                if(Op1 instanceof Unario){
                    String copy = rsOp1.getEtiquetaF();
                    rsOp1.setEtiquetaF(rsOp1.getEtiquetaV());
                    rsOp1.setEtiquetaV(copy);
                }
                
                if (!Evaluar) {

                    result.setValor(NuevoTemporal());
                    String etqSalida = NuevaEtiqueta();

                    if (rsOp1.getEtiquetaV() != null) {
                        codigo += rsOp1.getEtiquetaV();
                    }

                    codigo += "=, 1, , t" + result.getValor() + "\n";
                    codigo += "jmp, , , " + etqSalida + "\n";

                    if (rsOp1.getEtiquetaF() != null) {
                        codigo += rsOp1.getEtiquetaF();
                    }

                    codigo += "=, 0, , t" + result.getValor() + "\n";
                    codigo += etqSalida + ":\n";
                    codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                    codigo += "=, t0, t" + result.getValor() + ", stack\n";

                } else {
                    if (rsOp1.getEtiquetaV() != null) {
                        result.setEtiquetaV(rsOp1.getEtiquetaV());
                    }
                    if (rsOp1.getEtiquetaF() != null) {
                        result.setEtiquetaF(rsOp1.getEtiquetaF());
                    }
                }

            } else {
                errores.add(new ErrorC("Semántico", Linea, Columna, "Error de tipos en operación lógica."));
            }
        } else {
            errores.add(new ErrorC("Semántico", Linea, Columna, "Error de tipos en operación lógica."));
        }

        result.setCodigo(codigo);
        return result;
    }

    /**
     * @return the Evaluar
     */
    public boolean isEvaluar() {
        return Evaluar;
    }

    /**
     * @param Evaluar the Evaluar to set
     */
    public void setEvaluar(boolean Evaluar) {
        this.Evaluar = Evaluar;
    }

}
