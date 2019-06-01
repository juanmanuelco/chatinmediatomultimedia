package juanmanuelco.facci.com.soschat.Receivers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.MacAddress;
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
import juanmanuelco.facci.com.soschat.Entities.Mac;
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
	private WifiP2pInfo infor;
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

	String MacCalculo;
	StringBuilder MacAddress;

	Mac mac = new Mac();

	AdaptadorDispositivos adapter;
	int posicion;
	String[] newString;
	Boolean estado= true;

	private static WifiDirectBroadcastReceiver instance;

	private WifiDirectBroadcastReceiver(){
		super();
	}

	public static WifiDirectBroadcastReceiver createInstance(){
		if(instance == null) instance = new WifiDirectBroadcastReceiver();
		return instance;
	}
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
			if(state != WifiP2pManager.WIFI_P2P_STATE_ENABLED)
				if(!wifiManager.isWifiEnabled()) wifiManager.setWifiEnabled(true);
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
								if(device.primaryDeviceType.equals("10-0050F204-5")){
									MacCalculo = restarHexadecimal(device.deviceAddress.substring(0,2));
									Log.i("RestaMac", " Mac calculada: "+MacCalculo+" Mac normal: "+device.deviceAddress);
									MacAddress = new StringBuilder(device.deviceAddress);
									MacAddress.setCharAt(0,MacCalculo.charAt(0));
									MacAddress.setCharAt(1,MacCalculo.charAt(1));

									listado.add(new String[]{device.deviceName, MacAddress.toString().toLowerCase()});
									listado2.add(device.deviceName+","+device.deviceAddress);
									db.insertarUsuario(MacAddress.toString().toLowerCase(), device.deviceName);
								}
								estado=false;
								deviceArray[index]= device;
								index++;
							}

							adapter= new AdaptadorDispositivos(listado, context);
							adapter.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {

									final WifiP2pDevice device = deviceArray[RV.getChildAdapterPosition(v)];
									WifiP2pConfig config =  new WifiP2pConfig();
									config.deviceAddress=device.deviceAddress;
									DireccionMAC.direccion=device.deviceAddress;
									DireccionMAC.MacOnclick= listado.get(RV.getChildAdapterPosition(v))[1];
									mac.setMac(listado.get(RV.getChildAdapterPosition(v))[1]);
									posicion= RV.getChildAdapterPosition(v);

									mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
										@Override
										public void onSuccess() {
											cargando(R.string.CONECT, pDialog, context);
										}
										@Override
										public void onFailure(int reason) {
											Toast.makeText(context, R.string.error_connect + device.deviceName, Toast.LENGTH_SHORT).show();
										}
									});
								}
							});
							adapter.notifyDataSetChanged();
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
			if(mManager == null) return;
			NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			if(networkInfo.isConnected() && conteo){
				try {
					mManager.requestConnectionInfo(mChannel, new ConnectionInfoListener() {
						@Override
						public void onConnectionInfoAvailable(WifiP2pInfo info) {

							/*pDialog.hide();
							adapter.estadoActulizar(true,posicion);
							adapter.notifyDataSetChanged();
							Toast.makeText(context, "conectado", Toast.LENGTH_LONG).show();*/
							InetAddress groupOwnerAddress = info.groupOwnerAddress;
							ownerAddr= groupOwnerAddress;
							if (info.groupFormed && info.isGroupOwner) {
								isGroupeOwner = IS_OWNER;
								fm.server=new ServerInit();
								fm.server.start();
								FuncionActivity.server=fm.server;
								Toast.makeText(context,"servidor",Toast.LENGTH_LONG).show();
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
				}catch (Exception e){
					Toast.makeText(context,"Has cancelado la conexion",Toast.LENGTH_LONG).show();
				}
			}
		}
	}
	public ArrayList<String> retornar(){
		return listado2;
	}

	private static String restarHexadecimal(String primervalor) {
		String primerOperando = convertirHexadecimalADecimal(primervalor);
		String segundoOperando = convertirHexadecimalADecimal("02");

		int primero = Integer.parseInt(primerOperando);
		int segundo = Integer.parseInt(segundoOperando);
		int operacion = primero - segundo;

		String resultado = "" + operacion;
		return convertirDecimalAhexadecimal(resultado);
	}

	public static String convertirHexadecimalADecimal(String base){
		String digitos = "0123456789ABCDEF";
		String decimal = "";
		base = base.toUpperCase();
		int decimalInt = 0;
		for (int i = 0; i < base.length(); i++)
		{
			char c = base.charAt(i);
			int d = digitos.indexOf(c);
			decimalInt = 16 * decimalInt + d;
		}
		decimal += decimalInt;
		return decimal;
	}

	public static String convertirDecimalAhexadecimal(String bas){
		int decimal = Integer.parseInt(bas);
		String digits = "0123456789ABCDEF";
		if (decimal == 0)
			return "0";
		String hexde = "";
		while (decimal > 0) {
			int mod = decimal % 16; // DÃ­gito de la derecha
			hexde = digits.charAt(mod) + hexde; // ConcatenaciÃ³n de cadenas
			decimal = decimal / 16;
		}
		return hexde;
	}
}
