package juanmanuelco.facci.com.soschat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import juanmanuelco.facci.com.soschat.NEGOCIO.DireccionMAC;
import juanmanuelco.facci.com.soschat.NEGOCIO.Dispositivo;
import juanmanuelco.facci.com.soschat.NEGOCIO.Mensajes;
import juanmanuelco.facci.com.soschat.NEGOCIO.Validaciones;

public class InicioActivity extends AppCompatActivity {
    EditText ET_Main_Nickname;
    ProgressDialog pDialog;
    SharedPreferences sharedPref;
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
        pDialog=new ProgressDialog(this);
        ET_Main_Nickname= findViewById(R.id.ET_Main_Nickname);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        ET_Main_Nickname.setText(sharedPref.getString("nickname", Dispositivo.getDeviceName()));
    }
    public void bluethoot (View v){
        abrirChat(0);
    }
    public void wifi (View v){
        abrirChat(1);
    }
    public void lan (View v){
        abrirChat(2);
    }
    public void abrirChat(int valor){
        Mensajes.cargando(R.string.VERIFY, pDialog, this);
        String nickname= ET_Main_Nickname.getText().toString();
        DireccionMAC.nombre=nickname;
        DireccionMAC.wifiNombre = nickname;

        if(Validaciones.vacio(new EditText[]{ET_Main_Nickname})){
            GuardarPreferencia();
            Intent act_chat= null;
            act_chat= new Intent(InicioActivity.this, FuncionActivity.class);
            act_chat.putExtra("tipo", valor);
            act_chat.putExtra("nickname", nickname );
            System.setProperty("net.hostname", nickname);

            startActivity(act_chat);
        }else{
            Mensajes.mostrarMensaje(R.string.ERROR, R.string.NONAME, this);
        }
    }
    public void GuardarPreferencia(){
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("nickname", ET_Main_Nickname.getText().toString());
        editor.commit();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (pDialog != null)
            pDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pDialog != null)
            pDialog.dismiss();
    }
}