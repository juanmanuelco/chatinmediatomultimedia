package com.android.wondercom.Entities;

public class grupos {

    private String id_grupo;
    private String nombre;
    private String fecha;

    public grupos(String id_grupo, String nombre, String fecha) {
        this.id_grupo = id_grupo;
        this.nombre = nombre;
        this.fecha = fecha;
    }

    public String getId_grupo() {return id_grupo; }

    public void setId_grupo(String id_grupo) {this.id_grupo = id_grupo; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFecha() { return fecha; }

    public void setFecha(String fecha) { this.fecha = fecha; }
}
