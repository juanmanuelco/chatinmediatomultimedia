package com.android.wondercom.NEGOCIO;

import android.app.ProgressDialog;
import android.content.Context;

public class Mensajes {
    public static void mostrarMensaje(String t, String m, Context c){
        /**Crea el dialogo para mostrarlo*/
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder=new android.support.v7.app.AlertDialog.Builder(c);

        /**Personaliza el dialogo*/
        alertDialogBuilder.setTitle(t);
        alertDialogBuilder.setMessage(m);
        android.support.v7.app.AlertDialog alertDialog=alertDialogBuilder.create();

        /**Muestra el dialogo*/
        alertDialog.show();
    }

    public static void mostrarMensaje(int t, int m, Context c){
        /**Crea el dialogo para mostrarlo*/
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder=new android.support.v7.app.AlertDialog.Builder(c);

        /**Personaliza el dialogo*/
        alertDialogBuilder.setTitle(c.getResources().getString(t));
        alertDialogBuilder.setMessage(c.getResources().getString(m));
        android.support.v7.app.AlertDialog alertDialog=alertDialogBuilder.create();

        /**Muestra el dialogo*/
        alertDialog.show();
    }
    public static void cargando(String m, ProgressDialog p){
        /**Muestra un mensaje de carga*/
        p.setMessage(m);
        p.show();
    }
    public static void cargando(int m, ProgressDialog p, Context C){
        /**Muestra un mensaje de carga*/
        p.setMessage(C.getResources().getString(m));
        p.show();
    }
}
