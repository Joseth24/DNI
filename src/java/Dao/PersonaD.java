package Dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.PersonaM;
import org.primefaces.json.JSONObject;

public class PersonaD extends Conexion {

    public PersonaM consultarDni(PersonaM dl) throws org.json.JSONException, IOException {

        //Crea es json de Solicitud
        JSONObject obj = new JSONObject();
        obj.put("dni", dl.getDNI_PER());

        //Realiza la consulta
        String json = obj.toString();
        URL url = new URL("https://tecactus.com/api/reniec/dni");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("X-RateLimit-Limit", "100");
        con.setRequestProperty("X-RateLimit-Remaining", "58");
        con.setRequestProperty("Authorization", "Bearer " + dl.getTokendni());
        con.setDoOutput(true);
        con.getOutputStream().write(json.getBytes("UTF-8"));
        Reader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

        //Trae la cadene de respuesta
        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;) {
            sb.append((char) c);
        }
        String response = sb.toString(); //String Respuesta
        System.out.println(response);
        JSONObject json2 = new JSONObject(response); //JSON con la respuesta

        //Contenido del json rellenando en cada modelo
        dl.setNOM_PER((String) json2.get("nombres"));
        dl.setAPE_PAT_MAT((String) json2.get("apellido_paterno"));
        dl.setAPE_MAT_PER((String) json2.get("apellido_materno"));
        return dl;
    }

    public void guardar(PersonaM persona) throws Exception {
        try {
            this.conectar();
            String sql = "INSERT INTO PERSONA (DNI_PER,NOM_PER,APE_PAT_PER,APE_MAT_PER,FECNAC_PER,DIR_PER,SEX_PER,OBSER_PER,UBIGEO_COD_UBI) VALUES (?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = this.getCn().prepareStatement(sql);
            ps.setString(1, persona.getDNI_PER());
            ps.setString(2, persona.getNOM_PER());
            ps.setString(3, persona.getAPE_PAT_MAT());
            ps.setString(4, persona.getAPE_MAT_PER());
            ps.setString(5, persona.getFECNAC_PER());
            ps.setString(6, persona.getDIR_PER());
            ps.setString(7, persona.getSEX_PER());
            ps.setString(8, persona.getOBSER_PER());
            ps.setString(9, persona.getCOD_UBI());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            this.cerrar();
        }
    }

    public List<PersonaM> listar() throws SQLException, Exception {
        List<PersonaM> lista;
        ResultSet rs;
        try {
            String sql = "SELECT * FROM PERSONA";
            PreparedStatement ps = this.getCn().prepareCall(sql);
            rs = ps.executeQuery();
            lista = new ArrayList<>();
            while (rs.next()) {
                PersonaM persona = new PersonaM();
                persona.setCOD_PER(rs.getString("CODPER"));
                persona.setDNI_PER(rs.getString("DNI_PER"));
                persona.setNOM_PER(rs.getString("NOM_PER"));
                persona.setAPE_PAT_MAT(rs.getString("APE_PAT_PER"));
                persona.setAPE_MAT_PER(rs.getString("APE_MAT_PER"));
                persona.setFECNAC_PER(rs.getString("FECNAC_PER"));
                persona.setDIR_PER(rs.getString("DIR_PER"));
                persona.setSEX_PER(rs.getString("SEX_PER"));
                persona.setOBSER_PER(rs.getString("OBSER_PER"));
                persona.setCOD_UBI(rs.getString("COD_UBI"));
                lista.add(persona);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            this.cerrar();
        }
        return lista;
    }

    public List<String> autocompletUbigeo(String ubigeo) throws SQLException, ClassNotFoundException, Exception {
        this.conectar();
        ResultSet rs;
        List<String> listaUbi;
        try {
            String sql = "SELECT COD_UBI,CONCAT(DEP_UBI,', ',PRO_UBI,', ',DIS_UBI) AS UBIGEO FROM UBIGEO WHERE DEP_UBI LIKE ? OR PRO_UBI LIKE ? OR DIS_UBI LIKE ?";
            PreparedStatement ps = this.getCn().prepareCall(sql);
            ps.setString(1, "%" + ubigeo + "%");
            ps.setString(2, "%" + ubigeo + "%");
            ps.setString(3, "%" + ubigeo + "%");
            rs = ps.executeQuery();
            listaUbi = new ArrayList();
            while (rs.next()) {
                listaUbi.add(rs.getString("UBIGEO"));
            }
            return listaUbi;
        } catch (SQLException e) {
            throw e;
        } finally {
            this.cerrar();
        }
    }

    public String leerUbi(String ubigeo) throws Exception {
        this.conectar();
        ResultSet rs;
        try {
            String sql = "SELECT COD_UBI FROM UBIGEO WHERE CONCAT(DEP_UBI,', ',PRO_UBI,', ',DIS_UBI) = ?";
            PreparedStatement ps = this.getCn().prepareCall(sql);
            ps.setString(1, ubigeo);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("COD_UBI");
            }
            return null;
        } catch (SQLException e) {
            throw e;
        } finally {
            this.cerrar();
        }
    }
}
