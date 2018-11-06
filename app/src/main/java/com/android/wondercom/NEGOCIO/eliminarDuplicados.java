package com.android.wondercom.NEGOCIO;

import com.android.wondercom.Entities.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
}
