package juanmanuelco.facci.com.soschat.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import juanmanuelco.facci.com.soschat.CustomAdapters.AdaptadorDispositivos;
import juanmanuelco.facci.com.soschat.Entities.ENCONTRADO;
import juanmanuelco.facci.com.soschat.FuncionActivity;
import juanmanuelco.facci.com.soschat.InitThreads.ServerInit;
import juanmanuelco.facci.com.soschat.NEGOCIO.DireccionMAC;
import juanmanuelco.facci.com.soschat.NEGOCIO.Dispositivo;
import juanmanuelco.facci.com.soschat.NEGOCIO.Validaciones;
import juanmanuelco.facci.com.soschat.R;
import juanmanuelco.facci.com.soschat.Receivers.WifiDirectBroadcastReceiver;
import juanmanuelco.facci.com.soschat.Reflexion.ReflectionUtils;


public class FM_encontrados extends Fragment {
    WifiManager wifiManager;
    public static final String TAG = "MainActivity";
    public static final String DEFAULT_CHAT_NAME = Dispositivo.getDeviceName();
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiDirectBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private Button goToChat;
    private ImageView goToSettings;
    private TextView goToSettingsText;
    private TextView setChatNameLabel;
    private EditText setChatName;
    private ImageView disconnect;
    public static String chatName;
    public static ServerInit server;
    public static final int request_code = 1000;
    RecyclerView rv_participants;
    int intentos=0;
    ProgressDialog pDialog;
    WifiP2pManager mWifiP2pManager;

    private SearchView searchView = null;
    private boolean searchViewShow = false;
    private SearchView.OnQueryTextListener queryTextListener;
    public ArrayList<String> listado2 = new ArrayList<>();
    public ArrayList<String[]> nueva = new ArrayList<>();
    private ArrayList<ENCONTRADO> ListaFound;
    private ENCONTRADO encontrados;
    private String[] caracter;
    private AdaptadorDispositivos adapter;


    //Getters and Setters
    public WifiP2pManager getmManager() { return mManager; }
    public WifiP2pManager.Channel getmChannel() { return mChannel; }
    public WifiDirectBroadcastReceiver getmReceiver() { return mReceiver; }
    public IntentFilter getmIntentFilter() { return mIntentFilter; }
    public Button getGoToChat(){ return goToChat; }
    public TextView getSetChatNameLabel() { return setChatNameLabel; }
    public ImageView getGoToSettings() { return goToSettings; }
    public EditText getSetChatName() { return setChatName; }
    public TextView getGoToSettingsText() { return goToSettingsText; }
    public ImageView getDisconnect() { return disconnect; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_fm_encontrados, container, false);
        setHasOptionsMenu(true);
        pDialog=new ProgressDialog(getActivity());
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
        requestPermissionFromDevice();
        mWifiP2pManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        if (mWifiP2pManager != null) {
            mChannel = mWifiP2pManager.initialize(getContext(),  getActivity().getMainLooper(), null);
            if (mChannel == null)  mWifiP2pManager = null;
        }

        rv_participants=v.findViewById(R.id.participants_rv);
        rv_participants.setLayoutManager(new LinearLayoutManager(getActivity()));

        mManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(getActivity().getApplicationContext(), getActivity().getMainLooper(), null);
        mReceiver = WifiDirectBroadcastReceiver.createInstance();
        mReceiver.setmManager(mManager);
        mReceiver.setmChannel(mChannel);
        mReceiver.setmActivity(getActivity());
        mReceiver.setFragment(this);
        mReceiver.setRecycler(rv_participants);
        mReceiver.setDialogo(pDialog);

        FuncionActivity.server=server;
        FuncionActivity.chatName=Validaciones.loadChatName(getActivity(), "nickname", DEFAULT_CHAT_NAME);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        descubrir();

        FloatingActionButton fab = FuncionActivity.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    descubrir();
                }catch (Exception e){
                    Toast.makeText(getContext(), "No se puedo buscar", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragments, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Aqui tendran los eventos los iconos en el toolbar
        switch (item.getItemId()) {
            case R.id.configuracion:
                Toast.makeText(getContext(),"Configuración",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void descubrir(){
        int numberOfParams = 3;
        Class[] methodParameters = new Class[numberOfParams];
        methodParameters[0] = WifiP2pManager.Channel.class;
        methodParameters[1] = String.class;
        methodParameters[2] = WifiP2pManager.ActionListener.class;

        Object arglist[] = new Object[numberOfParams];
        arglist[0] = mChannel;
        arglist[1] = DireccionMAC.wifiNombre;
        arglist[2] = new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        };
        ReflectionUtils.executePrivateMethod(mWifiP2pManager,WifiP2pManager.class,"setDeviceName",methodParameters,arglist);
        try {
            Thread.sleep(300);
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getActivity(), R.string.search_toast, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(int reason) {
                    Toast.makeText(getActivity(), "Error #"+ reason, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (InterruptedException e) {
            Toast.makeText(getActivity(), R.string.no_search, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mReceiver, mIntentFilter);
        descubrir();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void requestPermissionFromDevice() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE
        },request_code);
    }

    // opciones en el toolbar, busqueda, recarga y configuracion

}
