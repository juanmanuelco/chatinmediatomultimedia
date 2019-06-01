package juanmanuelco.facci.com.soschat.InitThreads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerInit extends Thread{
	private static final String TAG = "ServerInit";
	private static final int SERVER_PORT = 4444;
	public static ArrayList<InetAddress> clients;
	private ServerSocket serverSocket;

	
	public ServerInit(){
		clients = new ArrayList<InetAddress>();
	}

	@Override
	public void run() {
		clients.clear();
	    
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		    while(true) {
		       Socket clientSocket = serverSocket.accept();
		       if(!clients.contains(clientSocket.getInetAddress()))
		    	   clients.add(clientSocket.getInetAddress());
		       clientSocket.close();
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void interrupt() {
		super.interrupt();
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
