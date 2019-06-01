package juanmanuelco.facci.com.soschat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import juanmanuelco.facci.com.soschat.NEGOCIO.DireccionMAC;
import juanmanuelco.facci.com.soschat.NEGOCIO.Dispositivo;
import juanmanuelco.facci.com.soschat.NEGOCIO.Mensajes;
import juanmanuelco.facci.com.soschat.NEGOCIO.Validaciones;

public class InicioActivity extends AppCompatActivity {
    EditText editText_Nickname;
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
        editText_Nickname= findViewById(R.id.ET_Main_Nickname);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editText_Nickname.setText(sharedPref.getString("nickname", Dispositivo.getDeviceName()));
    }

    public void bluethoot (View v){
        abrirChat(0);
    }
    public void wifi (View v){
        abrirChat(1);
    }

    public void abrirChat(int valor){
        Mensajes.cargando(R.string.VERIFY, pDialog, this);
        String nickname= editText_Nickname.getText().toString();
        DireccionMAC.nombre=nickname;
        DireccionMAC.wifiNombre = nickname;

        if(Validaciones.vacio(new EditText[]{editText_Nickname})){
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
        editor.putString("nickname", editText_Nickname.getText().toString());
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

    //  iconos en el toolbar, para la configuración

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activitidad_inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Aqui tendran los eventos los iconos en el toolbar
        switch (item.getItemId()) {
            case R.id.emergencia:
                Toast.makeText(this,"Emergencia",Toast.LENGTH_LONG).show();
                return true;
            case R.id.configuracion:
                Toast.makeText(this,"Configuración",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}