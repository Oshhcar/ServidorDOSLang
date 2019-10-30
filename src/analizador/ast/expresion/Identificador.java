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
public class Identificador extends Expresion{

    private String Id;

    public Identificador(String Id, int Linea, int Columna) {
        super(Linea, Columna);
        this.Id = Id;
    }
    
    @Override
    public Result GetCuadruplos(Entorno e, ArrayList<ErrorC> errores) {
        Result result = new Result();
        String codigo = "";
        
        Simbolo sim = e.Get(Id);
        
        if(sim != null){
            if(sim.getRol() == Rol.LOCAL){
                Tipo = sim.getTipo();
                int tmp = NuevoTemporal();
                result.setValor(NuevoTemporal());
                
                codigo += "+, P, " + sim.getPos() + ", t" + tmp + "\n";
                codigo += "+, P, " + (tmp - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, t0, t" + tmp + ", stack\n";
                codigo += "=, stack, t" + tmp + ", t" + result.getValor() + "\n";
                codigo += "+, P, " + (result.getValor() - e.getTmpInicio() + e.getSize()) + ", t0\n";
                codigo += "=, t0, t" + result.getValor() + ", stack\n";
                
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
    
}
