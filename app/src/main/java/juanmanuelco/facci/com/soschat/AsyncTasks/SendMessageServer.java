package juanmanuelco.facci.com.soschat.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import juanmanuelco.facci.com.soschat.ChatActivity;
import juanmanuelco.facci.com.soschat.Entities.Mensaje;
import juanmanuelco.facci.com.soschat.InicioActivity;
import juanmanuelco.facci.com.soschat.InitThreads.ServerInit;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import static juanmanuelco.facci.com.soschat.NEGOCIO.Validaciones.isActivityRunning;

public class SendMessageServer extends AsyncTask<Mensaje, Mensaje, Mensaje>{

	private Context mContext;
	private static final int SERVER_PORT = 4446;
	private boolean isMine;

	public SendMessageServer(Context context){
		mContext = context;
	}
	
	@Override
	protected Mensaje doInBackground(Mensaje... msg) {
		publishProgress(msg);
		try {			
			ArrayList<InetAddress> listClients = ServerInit.clients;
			for(InetAddress addr : listClients){
				
				if(msg[0].getAddress()!=null && addr.getHostAddress().equals(msg[0].getAddress().getHostAddress())){
					return msg[0];
				}
				Socket socket = new Socket();
				socket.setReuseAddress(true);
				socket.bind(null);
				socket.connect(new InetSocketAddress(addr, SERVER_PORT));
				OutputStream outputStream = socket.getOutputStream();
				new ObjectOutputStream(outputStream).writeObject(msg[0]);
			    socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
		return msg[0];
	}

	@Override
	protected void onProgressUpdate(Mensaje... values) {
		super.onProgressUpdate(values);
		if(isActivityRunning(InicioActivity.class, mContext))
			ChatActivity.refreshList(values[0]);
	}

	@Override
	protected void onPostExecute(Mensaje result) {

		super.onPostExecute(result);
	}

}