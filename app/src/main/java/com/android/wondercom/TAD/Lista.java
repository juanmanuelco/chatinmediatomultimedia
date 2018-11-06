package com.android.wondercom.TAD;

import com.android.wondercom.Entities.Message;

import java.util.ArrayList;
import java.util.List;

public class Lista {
    Nodo cabeza;
    Nodo ultimo;
    List<Message>listado;

    public Lista(List<Message> mensajes) {
        this.cabeza = null;
        this.ultimo=null;
        this.listado=mensajes;
        inserciones();
    }

    private void inserciones() {
        for(int i=0; i < this.listado.size(); i++){
            Insertar(this.listado.get(i));
        }
    }


    public void Insertar(Message mes){
        Nodo temp= new Nodo();
        temp.info=mes;
        if(this.cabeza==null){
            this.cabeza=temp;
            this.cabeza.sig=null;
            this.ultimo=this.cabeza;
        }else{
            this.ultimo.sig=temp;
            temp.sig=null;
            this.ultimo=temp;
        }
        ordenar();
    }

    public void ordenar(){
        Message aux;
        Nodo cab, sgt2;
        cab=this.cabeza;
        sgt2=this.cabeza.sig;
        while (cab!=null){
            while (sgt2 != null){
                if(cab.info.tiempoEnvio() > sgt2.info.tiempoEnvio()){
                    aux = cab.info;
                    cab.info = sgt2.info;
                    sgt2.info= aux;
                }
                sgt2 = sgt2.sig;
            }
            sgt2 = cab.sig;
            cab = cab.sig;

        }
    }

    public List<Message> Ordenados(){
        Nodo actual= new Nodo();
        List<Message> respuesta= new ArrayList<Message>();
        actual= cabeza;
        while (actual != null){
            respuesta.add(actual.info);
            actual=actual.sig;
        }
        return respuesta;
    }
}
