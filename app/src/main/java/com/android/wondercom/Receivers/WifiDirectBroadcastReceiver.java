package com.android.wondercom.Receivers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.android.wondercom.FuncionActivity;
import com.android.wondercom.MainActivity;
import com.android.wondercom.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver{
	public static final int IS_OWNER = 1;
	public static final int IS_CLIENT = 2;
	private static final String TAG = "WifiDirectBroadcastReceiver";
	private WifiP2pManager mManager;
	private Channel mChannel;
	private Activity mActivity;
	private List<String> peersName = new ArrayList<String>();
	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
	private int isGroupeOwner;
	private InetAddress ownerAddr;
	
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

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)){
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){

			} else{

			}
			return;
		}

		if(action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)){
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
			if(networkInfo.isConnected()){
				mManager.requestConnectionInfo(mChannel, new ConnectionInfoListener() {
					
					@Override
					public void onConnectionInfoAvailable(WifiP2pInfo info) {
						InetAddress groupOwnerAddress = info.groupOwnerAddress;
						ownerAddr= groupOwnerAddress;
						if (info.groupFormed && info.isGroupOwner) {
							isGroupeOwner = IS_OWNER;
							activateGoToChat("server");
						}
						else if (info.groupFormed) {
							isGroupeOwner = IS_CLIENT;
							activateGoToChat("client");
						}
					}
				});				
			}
		}
	}
	
	public void activateGoToChat(String role){
		if(mActivity.getClass() == FuncionActivity.class){

			Button gotochat = mActivity.findViewById(R.id.goToChat);
			gotochat.setText("Start the chat "+role);
			gotochat.setVisibility(View.VISIBLE);

			EditText setChatName= mActivity.findViewById(R.id.setChatName);
			setChatName.setVisibility(View.VISIBLE);
			TextView setChatNameLabel = mActivity.findViewById(R.id.setChatNameLabel);
			setChatNameLabel.setVisibility(View.VISIBLE);

			ImageView disconnect= mActivity.findViewById(R.id.disconnect);
			disconnect.setVisibility(View.VISIBLE);

			ImageView goToSettings= mActivity.findViewById(R.id.goToSettings);
			goToSettings.setVisibility(View.GONE);

			TextView textGoToSettings= mActivity.findViewById(R.id.textGoToSettings);
			textGoToSettings.setVisibility(View.GONE);

			/*
			((FuncionActivity)mActivity).getGoToChat().setText("Start the chat "+role);
			((FuncionActivity)mActivity).getGoToChat().setVisibility(View.VISIBLE);
			((FuncionActivity)mActivity).getSetChatName().setVisibility(View.VISIBLE);
			((FuncionActivity)mActivity).getSetChatNameLabel().setVisibility(View.VISIBLE);
			((FuncionActivity)mActivity).getDisconnect().setVisibility(View.VISIBLE);
			((FuncionActivity)mActivity).getGoToSettings().setVisibility(View.GONE);
			((FuncionActivity)mActivity).getGoToSettingsText().setVisibility(View.GONE);
			*/
		}
	}

}
