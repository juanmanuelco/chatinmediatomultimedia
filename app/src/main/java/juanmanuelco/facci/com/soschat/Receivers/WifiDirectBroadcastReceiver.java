package juanmanuelco.facci.com.soschat.Receivers;

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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import juanmanuelco.facci.com.soschat.ChatActivity;
import juanmanuelco.facci.com.soschat.CustomAdapters.AdaptadorDispositivos;
import juanmanuelco.facci.com.soschat.DB.DB_SOSCHAT;
import juanmanuelco.facci.com.soschat.Entities.ENCONTRADO;
import juanmanuelco.facci.com.soschat.Fragments.FM_encontrados;
import juanmanuelco.facci.com.soschat.FuncionActivity;
import juanmanuelco.facci.com.soschat.InitThreads.ClientInit;
import juanmanuelco.facci.com.soschat.InitThreads.ServerInit;
import juanmanuelco.facci.com.soschat.NEGOCIO.DireccionMAC;
import juanmanuelco.facci.com.soschat.R;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static juanmanuelco.facci.com.soschat.NEGOCIO.Mensajes.cargando;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver{

	private WifiP2pManager mManager;
	private Channel mChannel;
	private Activity mActivity;
	boolean conteo=true;

	WifiManager wifiManager;
	static DB_SOSCHAT db;



	public static final int IS_OWNER = 1;
	public static final int IS_CLIENT = 2;

	private List<String> peersName = new ArrayList<String>();
	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
	private int isGroupeOwner;
	private InetAddress ownerAddr;

	public ArrayList <String[]> listado;
	public ArrayList<String> listado2;
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
		listado2= new ArrayList<>();
		wifiManager = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

		db = new DB_SOSCHAT(context);
		encontrados = new ENCONTRADO();

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
                                listado2.add(device.deviceName+","+device.deviceAddress);
								db.insertar_Encontrados(device.deviceAddress,device.deviceName);
								deviceArray[index]= device;
								index++;
							}

							AdaptadorDispositivos adapter= new AdaptadorDispositivos(listado);
							adapter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
									final WifiP2pDevice device = deviceArray[RV.getChildAdapterPosition(v)];
									WifiP2pConfig config =  new WifiP2pConfig();
									config.deviceAddress=device.deviceAddress;
									DireccionMAC.direccion=device.deviceAddress;
									mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
										@Override
										public void onSuccess() {
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

			if(networkInfo.isConnected() && conteo){
				mManager.requestConnectionInfo(mChannel, new ConnectionInfoListener() {
					
					@Override
					public void onConnectionInfoAvailable(WifiP2pInfo info) {
						InetAddress groupOwnerAddress = info.groupOwnerAddress;
						ownerAddr= groupOwnerAddress;
						if (info.groupFormed && info.isGroupOwner) {
							isGroupeOwner = IS_OWNER;
							fm.server=new ServerInit();
							fm.server.start();
							FuncionActivity.server=fm.server;
						}
						else if (info.groupFormed) {
							isGroupeOwner = IS_CLIENT;
							ClientInit client = new ClientInit(getOwnerAddr());
							client.start();
						}
						Intent intent = new Intent(mActivity.getApplicationContext(), ChatActivity.class);
						mActivity.startActivity(intent);
						conteo=false;
					}
				});				
			}
		}
	}
	public ArrayList<String> retornar(){
		return listado2;
	}
}
