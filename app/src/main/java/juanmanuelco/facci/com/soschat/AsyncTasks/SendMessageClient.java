package juanmanuelco.facci.com.soschat.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import juanmanuelco.facci.com.soschat.ChatActivity;
import juanmanuelco.facci.com.soschat.Entities.Mensaje;
import juanmanuelco.facci.com.soschat.InicioActivity;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import static juanmanuelco.facci.com.soschat.NEGOCIO.Validaciones.isActivityRunning;

public class SendMessageClient extends AsyncTask<Mensaje, Mensaje, Mensaje>{

	private Context mContext;
	private static final int SERVER_PORT = 4445;
	private InetAddress mServerAddr;
	
	public SendMessageClient(Context context, InetAddress serverAddr){
		mContext = context;
		mServerAddr = serverAddr;
	}
	
	@Override
	protected Mensaje doInBackground(Mensaje... msg) {
		//Display le message on the sender before sending it
		publishProgress(msg);
		
		//Send the message
		Socket socket = new Socket();
		try {
			socket.setReuseAddress(true);
			socket.bind(null);
			socket.connect(new InetSocketAddress(mServerAddr, SERVER_PORT));
			OutputStream outputStream = socket.getOutputStream();
			new ObjectOutputStream(outputStream).writeObject(msg[0]);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (socket != null) {
		        if (socket.isConnected()) {
		            try {
		                socket.close();
		            } catch (IOException e) {
		            	e.printStackTrace();
		            }
		        }
		    }
		}
		
		return msg[0];
	}

	@Override
	protected void onProgressUpdate(Mensaje... msg) {
		super.onProgressUpdate(msg);
		
		if(isActivityRunning(InicioActivity.class, mContext))
			ChatActivity.refreshList(msg[0]);
	}

	@Override
	protected void onPostExecute(Mensaje result) {
		super.onPostExecute(result);
	}
}
