package controlador;

import Dao.PersonaD;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import modelo.PersonaM;
import org.primefaces.json.JSONException;

@Named(value = "personaC")
@SessionScoped
public class PersonaC implements Serializable {

    private PersonaM persona = new PersonaM();
    private List<PersonaM> lstPersona;
    private PersonaM selectPersona;
    
    
    @PostConstruct
    public void iniciar(){
        try {
            listarPersona();
        } catch (Exception e) {
        }
    }
    
    public void consultarDni() throws Exception {
        PersonaD ds;
        try {
          ds = new PersonaD();
          persona =   ds.consultarDni(persona);
        } catch (IOException | JSONException e) {
            throw e;
        }
    }

    public void guardarPersona() throws Exception {
        PersonaD dao;
        try {
            dao = new PersonaD();
            dao.guardar(persona);
            listarPersona();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", null));
            throw e;
        }
    }

    public void listarPersona() throws Exception {
        PersonaD dao;
        try {
            dao = new PersonaD();
            dao.listar();
        } catch (Exception e) {
            throw e;
        }
    }

    //GETTER AND SETTER 
    public PersonaM getPersona() {
        return persona;
    }

    public void setPersona(PersonaM persona) {
        this.persona = persona;
    }

    public List<PersonaM> getLstPersona() {
        return lstPersona;
    }

    public void setLstPersona(List<PersonaM> lstPersona) {
        this.lstPersona = lstPersona;
    }

    public PersonaM getSelectPersona() {
        return selectPersona;
    }

    public void setSelectPersona(PersonaM selectPersona) {
        this.selectPersona = selectPersona;
    }

}
