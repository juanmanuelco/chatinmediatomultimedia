package juanmanuelco.facci.com.soschat.NEGOCIO;

import juanmanuelco.facci.com.soschat.Entities.Mensaje;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class eliminarDuplicados {
    List<Mensaje>todos_mensajes;

    public eliminarDuplicados(List<Mensaje> mensajes) {
        this.todos_mensajes=mensajes;
    }
    public List<Mensaje> eiminar(){
        ArrayList<Mensaje> respuesta= new ArrayList<Mensaje>();
        HashSet<Mensaje> hashSet = new HashSet<Mensaje>(this.todos_mensajes);
        respuesta.clear();
        respuesta.addAll(hashSet);
        return respuesta;
    }
}
