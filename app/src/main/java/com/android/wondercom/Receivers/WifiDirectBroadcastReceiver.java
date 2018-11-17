package com.android.wondercom.Receivers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.android.wondercom.ChatActivity;
import com.android.wondercom.CustomAdapters.AdaptadorDispositivos;
import com.android.wondercom.DB.DB_SOSCHAT;
import com.android.wondercom.Entities.ENCONTRADO;
import com.android.wondercom.Fragments.FM_encontrados;
import com.android.wondercom.InitThreads.ClientInit;
import com.android.wondercom.InitThreads.ServerInit;
import com.android.wondercom.MainActivity;
import com.android.wondercom.NEGOCIO.DireccionMAC;
import com.android.wondercom.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import static com.android.wondercom.NEGOCIO.Mensajes.cargando;
import static com.android.wondercom.NEGOCIO.Mensajes.getMacAddr;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver{

	private WifiP2pManager mManager;
	private Channel mChannel;
	private Activity mActivity;
	int conteo=0;

	WifiManager wifiManager;
	static DB_SOSCHAT db;



	public static final int IS_OWNER = 1;
	public static final int IS_CLIENT = 2;
	private static final String TAG = "WifiDirectBroadcastReceiver";

	private List<String> peersName = new ArrayList<String>();
	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
	private int isGroupeOwner;
	private InetAddress ownerAddr;

	public ArrayList <String[]> listado;
	WifiP2pDevice[] deviceArray;
	FM_encontrados fm;
	ENCONTRADO encontrados;

	RecyclerView RV;
	ProgressDialog pDialog;

	private static WifiDirectBroadcastReceiver instance;
	
	private WifiDirectBroadcastReceiver(){
		super();
	}
	
	public static WifiDirectBroadcastReceiver createInstance(){

		if(instance == null){
			instance = new WifiDirectBroadcastReceiver();
		}
		return instance;
	}
	
	public List<String> getPeersName() { return peersName; }
	public List<WifiP2pDevice> getPeers() { return peers; }
	public int isGroupeOwner() { return isGroupeOwner; }
	public InetAddress getOwnerAddr() { return ownerAddr; }
	public void setmManager(WifiP2pManager mManager) { this.mManager = mManager; }
	public void setmChannel(Channel mChannel) { this.mChannel = mChannel; }
	public void setmActivity(Activity mActivity) { this.mActivity = mActivity; }
	public void setFragment(FM_encontrados FM){ this.fm= FM;}
	public void setRecycler(RecyclerView  recycler){ this.RV=recycler;}

	public void setDialogo(ProgressDialog dialogo){this.pDialog=dialogo;}

	@Override
	public void onReceive(final Context context, Intent intent) {

		String action = intent.getAction();
		listado= new ArrayList<String[]>();
		wifiManager = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

		db = new DB_SOSCHAT(context);

		if(action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)){
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if(state != WifiP2pManager.WIFI_P2P_STATE_ENABLED){
				if(!wifiManager.isWifiEnabled())
					wifiManager.setWifiEnabled(true);
			}
			return;
		}

		if(action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)){
			if(mManager!=null){
				mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
					@Override
					public void onPeersAvailable(WifiP2pDeviceList peerList) {
						if(!peerList.getDeviceList().equals(peers)){
							peers.clear();
							listado.clear();
							peers.addAll(peerList.getDeviceList());
							deviceArray= new WifiP2pDevice[peerList.getDeviceList().size()];
							int index=0;
							for(WifiP2pDevice device : peerList.getDeviceList()){
								listado.add(new String[]{device.deviceName, device.deviceAddress});
                                db.insertar_Encontrados(device.deviceAddress,device.deviceName);
								deviceArray[index]= device;
								index++;
							}
							Log.i("ListaEncontrados",String.valueOf(db.encontradosLista()));

							AdaptadorDispositivos adapter= new AdaptadorDispositivos(listado);
							adapter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
									final WifiP2pDevice device = deviceArray[RV.getChildAdapterPosition(v)];
									WifiP2pConfig config =  new WifiP2pConfig();
									config.deviceAddress=device.deviceAddress;
									mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
										@Override
										public void onSuccess() {
											DireccionMAC.direccion=device.deviceAddress;
											cargando(R.string.CONECT, pDialog, context);
										}
										@Override
										public void onFailure(int reason) {
											Toast.makeText(context, "Error al conectarse con "+ device.deviceName, Toast.LENGTH_SHORT).show();
										}
									});
                                }
                            });
							RV.setAdapter(adapter);
						}
					}
				});
			}
			return;
		}
		if(action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)){
			return;
		}

		if(action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)){
			
			if(mManager == null){
				return;
			}
			NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			if(networkInfo.isConnected() && conteo==0){
				mManager.requestConnectionInfo(mChannel, new ConnectionInfoListener() {
					
					@Override
					public void onConnectionInfoAvailable(WifiP2pInfo info) {
						InetAddress groupOwnerAddress = info.groupOwnerAddress;
						ownerAddr= groupOwnerAddress;
						if (info.groupFormed && info.isGroupOwner) {
							isGroupeOwner = IS_OWNER;
							fm.server=new ServerInit(getMacAddr());
							fm.server.start();
							MainActivity.server=fm.server;
						}
						else if (info.groupFormed) {
							isGroupeOwner = IS_CLIENT;
							ClientInit client = new ClientInit(getOwnerAddr(), getMacAddr());
							client.start();
						}
						Intent intent = new Intent(mActivity.getApplicationContext(), ChatActivity.class);
						mActivity.startActivity(intent);
						conteo++;
					}
				});				
			}
		}
	}
}
