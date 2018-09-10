package com.android.wondercom.NEGOCIO;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.Iterator;

public class Validaciones {
    public static boolean vacio(EditText[] campos){
        int visor=0;
        /**Obtiene una lista del valor de cada caja de texto*/
        ArrayList list = new ArrayList();
        for (int i = 0; i < campos.length; i++){
            list.add(campos[i].getText().toString().trim());
        }

        /**Usa un patron Iterador para recorrer la lista*/
        Iterator e = list.iterator();
        while (e.hasNext()){
            Object obj = e.next();
            if (!obj.toString().equals(""))
                visor+=1;
        }
        /**Si el tamaño de la lista es igual al tamaño del array entonces no hay espacios en blanco*/
        if(visor==campos.length)
            return true;
        else
            return false;
    }
}
