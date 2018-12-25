package juanmanuelco.facci.com.soschat.NEGOCIO;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import juanmanuelco.facci.com.soschat.R;

public class Mensajes {
    public static void mostrarMensaje(String t, String m, Context c){
        /**Crea el dialogo para mostrarlo*/
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder=new android.support.v7.app.AlertDialog.Builder(c);

        /**Personaliza el dialogo*/
        alertDialogBuilder.setTitle(t);
        alertDialogBuilder.setMessage(m);
        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
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
        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
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
    public static String datosSenal(int velocidad, int frecuencia, int fuerza){
        String respuesta= "Velocidad: " + velocidad +" Mbps, ";
        respuesta=respuesta.concat("Frecuencia: " + frecuencia+" Mhz, ");
        respuesta=respuesta.concat("Fuerza de señal: " + fuerza);
        return respuesta;
    }
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null)  return "";
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }
                if (res1.length() > 0) res1.deleteCharAt(res1.length() - 1);
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }
}
