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
import analizador.ast.entorno.Type;
import java.util.ArrayList;

/**
 *
 * @author oscar
 */
public class Call extends Expresion {

    private String Id;
    private ArrayList<Expresion> Parametros;

    public Call(String Id, int Linea, int Columna) {
        super(Linea, Columna);
        this.Id = Id;
        this.Parametros = null;
    }

    public Call(String Id, ArrayList<Expresion> Parametros, int Linea, int Columna) {
        super(Linea, Columna);
        this.Id = Id;
        this.Parametros = Parametros;
    }

    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";

        switch (Id.toLowerCase()) {
            case "sizeof": //Por el momento solo acepta id de parametro
                if (Parametros != null) {

                    if (Parametros.size() > 1) {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "La función sizeof solo necesita un record como parámetro."));
                    }

                    Expresion parametro = Parametros.get(0);

                    if (parametro instanceof Identificador) {
                        ((Identificador) parametro).setObtenerTipo(true);
                    }

                    parametro.GetCuadruplos(e, errores);

                    if (parametro.getTipo().IsRecord()) {
                        Tipo.setTipo(Type.INTEGER);

                        result.setValor(NuevoTemporal());

                        codigo += "=, " + parametro.getTipo().getEntorno().getSize() + ", , t" + result.getValor() + "\n";
                        codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                        codigo += "=, t0, t" + result.getValor() + ", stack\n";

                    } else {
                        errores.add(new ErrorC("Semántico", Linea, Columna, "Parametro no es de tipo record."));
                    }

                } else {
                    errores.add(new ErrorC("Semántico", Linea, Columna, "La función sizeof necesita un record como parámetro."));
                }
                break;
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
     * @return the Parametros
     */
    public ArrayList<Expresion> getParametros() {
        return Parametros;
    }

    /**
     * @param Parametros the Parametros to set
     */
    public void setParametros(ArrayList<Expresion> Parametros) {
        this.Parametros = Parametros;
    }
}
