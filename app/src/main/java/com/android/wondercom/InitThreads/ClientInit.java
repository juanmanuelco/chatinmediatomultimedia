package com.android.wondercom.InitThreads;

import com.android.wondercom.NEGOCIO.DireccionMAC;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientInit extends Thread{
	private static final int SERVER_PORT = 4444;
	private InetAddress mServerAddr;

	public String macClient="";
	
	public ClientInit(InetAddress serverAddr, String mac){
		mServerAddr = serverAddr;
		this.macClient=mac;
	}
	
	@Override
	public void run() {
		Socket socket = new Socket();
		try {
			socket.bind(null);
			socket.connect(new InetSocketAddress(mServerAddr, SERVER_PORT));
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
