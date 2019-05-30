package juanmanuelco.facci.com.soschat.Hilos;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import juanmanuelco.facci.com.soschat.NEGOCIO.DireccionMAC;

public class ClientInit extends Thread{
	private static final int SERVER_PORT = 4444;
	private InetAddress mServerAddr;
	
	public ClientInit(InetAddress serverAddr){
		mServerAddr = serverAddr;
	}

	public void Destino(String mac_destino){
		DireccionMAC.MacOnclick=mac_destino;
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
