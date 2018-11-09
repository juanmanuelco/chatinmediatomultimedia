package com.android.wondercom.NEGOCIO;

import com.android.wondercom.DB.DB_SOSCHAT;
import com.android.wondercom.Entities.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.android.wondercom.DB.DB_SOSCHAT;

public class eliminarDuplicados {
    List<Message>todos_mensajes;

    public eliminarDuplicados(List<Message> mensajes) {
        this.todos_mensajes=mensajes;
    }
    public List<Message> eiminar(){
        ArrayList<Message> respuesta= new ArrayList<Message>();
        HashSet<Message> hashSet = new HashSet<Message>(this.todos_mensajes);
        respuesta.clear();
        respuesta.addAll(hashSet);
        return respuesta;
    }
    DB_SOSCHAT db;
    Message msm;
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Message) {
            ArrayList<Message> prueba = (ArrayList<Message>) obj;
            Message prueba2 = (Message)obj;
            return this.todos_mensajes.equals(prueba2.mili_envio);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.todos_mensajes.size();
    }
    public int retorna(){
        HashSet<Message> hashSet = new HashSet<Message>(this.todos_mensajes);
        return hashSet.size();
    }
}
