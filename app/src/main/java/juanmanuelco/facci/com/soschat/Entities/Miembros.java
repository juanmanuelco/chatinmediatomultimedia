package juanmanuelco.facci.com.soschat.Entities;

public class Miembros {

    private String id;
    private String mac_encontrado;
    private String id_grupo;
    private String fecha;

    public Miembros(String id, String mac_encontrado, String id_grupo, String fecha) {
        this.id = id;
        this.mac_encontrado = mac_encontrado;
        this.id_grupo = id_grupo;
        this.fecha = fecha;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getMac_encontrado() { return mac_encontrado; }

    public void setMac_encontrado(String mac_encontrado) { this.mac_encontrado = mac_encontrado; }

    public String getId_grupo() { return id_grupo; }

    public void setId_grupo(String id_grupo) { this.id_grupo = id_grupo; }

    public String getFecha() { return fecha; }

    public void setFecha(String fecha) { this.fecha = fecha;}

}
