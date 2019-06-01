package juanmanuelco.facci.com.soschat.NEGOCIO;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public static String loadChatName(Context context, String key, String defaultText) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultText);
    }

    public static Boolean isActivityRunning(Class activityClass, Context mContext) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }
        return false;
    }

    public static String  obtenerPeso(int peso){
        String mensaje= "";
        if(peso>1024 && peso < 1048576) mensaje= Math.round(peso/1024)+" KB";
        else mensaje= Math.round(peso/1048576)+" MB";
        if(peso<1024) mensaje= peso +" Bytes";
        return mensaje;
    }

}
