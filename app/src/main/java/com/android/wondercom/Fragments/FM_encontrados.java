package com.android.wondercom.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wondercom.ChatActivity;
import com.android.wondercom.InitThreads.ClientInit;
import com.android.wondercom.InitThreads.ServerInit;
import com.android.wondercom.MainActivity;
import com.android.wondercom.NEGOCIO.Dispositivo;
import com.android.wondercom.R;
import com.android.wondercom.Receivers.WifiDirectBroadcastReceiver;


public class FM_encontrados extends Fragment {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_fm_encontrados, container, false);
        //Init the Channel, Intent filter and Broadcast receiver
        init();

        //Button Go to Settings
        goToSettings = (ImageView) v.findViewById(R.id.goToSettings);
        goToSettings();

        //Go to Settings text
        goToSettingsText = (TextView) v.findViewById(R.id.textGoToSettings);

        //Button Go to Chat
        goToChat = (Button) v.findViewById(R.id.goToChat);
        goToChat();

        //Set the chat name
        setChatName = (EditText) v.findViewById(R.id.setChatName);
        setChatNameLabel = (TextView) v.findViewById(R.id.setChatNameLabel);
        setChatName.setText(loadChatName(getActivity().getApplicationContext()));

        //Button Disconnect
        disconnect = (ImageView) v.findViewById(R.id.disconnect);
        disconnect();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mReceiver, mIntentFilter);

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.v(TAG, "Proceso de descubir completado");
            }

            @Override
            public void onFailure(int reason) {
                Log.v(TAG, "Fallo al descubrir");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    public void init(){

        requestPermissionFromDevice();
        mManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(getActivity().getApplicationContext(), getActivity().getMainLooper(), null);
        mReceiver = WifiDirectBroadcastReceiver.createInstance();
        mReceiver.setmManager(mManager);
        mReceiver.setmChannel(mChannel);
        mReceiver.setmActivity(getActivity());

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public void goToChat(){
        goToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(!setChatName.getText().toString().equals("")){
                    chatName = loadChatName(getActivity());
                    if(mReceiver.isGroupeOwner() ==  WifiDirectBroadcastReceiver.IS_OWNER){
                        server = new ServerInit();
                        server.start();
                    }
                    else if(mReceiver.isGroupeOwner() ==  WifiDirectBroadcastReceiver.IS_CLIENT){
                        ClientInit client = new ClientInit(mReceiver.getOwnerAddr());
                        client.start();
                    }
                    Intent intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getActivity(), "Por favor ingrese un nickname", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void disconnect(){
        disconnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mManager.removeGroup(mChannel, null);
                getActivity().finish();
            }
        });
    }

    public void goToSettings(){
        goToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
            }
        });
    }

    public String loadChatName(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("nickname", DEFAULT_CHAT_NAME);
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

}
