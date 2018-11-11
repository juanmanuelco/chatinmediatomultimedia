package com.android.wondercom.AsyncTasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;

import com.android.wondercom.DB.DB_SOSCHAT;
import com.android.wondercom.Entities.Message;
import com.android.wondercom.NEGOCIO.DireccionMAC;

import static com.android.wondercom.NEGOCIO.Mensajes.getMacAddr;

public class ReceiveMessageServer extends AbstractReceiver {
	private static final int SERVER_PORT = 4445;
	private Context mContext;
	private ServerSocket serverSocket;
	DB_SOSCHAT db;


	public ReceiveMessageServer(Context context){
		mContext = context;
		this.db = new DB_SOSCHAT(context);
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			while(true){
				Socket clientSocket = serverSocket.accept();				
				
				InputStream inputStream = clientSocket.getInputStream();				
				ObjectInputStream objectIS = new ObjectInputStream(inputStream);
				Message message = (Message) objectIS.readObject();
				long millis= System.currentTimeMillis();
				message.setMili_recibo(millis);
				//Add the InetAdress of the sender to the message
				InetAddress senderAddr = clientSocket.getInetAddress();
				message.setSenderAddress(senderAddr);

				if(message.getMacOrigen().equals(getMacAddr())){
					if(message.getMacDestino().equals("")){
						message.setMacDestino(DireccionMAC.direccion);
					}
				}else{
					DireccionMAC.direccion=message.getMacOrigen();
					if(message.getMacDestino().equals("")){
						message.setMacDestino(getMacAddr());
					}
				}

				if(db.validarRegistro(message)){
					db.guardarMensaje(message);
				}


				clientSocket.close();
				publishProgress(message);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        
		return null;
	}

	@Override
	protected void onCancelled() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onCancelled();
	}

	@Override
	protected void onProgressUpdate(Message... values) {
		super.onProgressUpdate(values);
		playNotification(mContext, values[0]);
		
		//If the message contains a video or an audio, we saved this file to the external storage
		int type = values[0].getmType();
		if(type==Message.AUDIO_MESSAGE || type==Message.VIDEO_MESSAGE || type==Message.FILE_MESSAGE || type==Message.DRAWING_MESSAGE){
			values[0].saveByteArrayToFile(mContext);
		}
		
		new SendMessageServer(mContext, false).executeOnExecutor(THREAD_POOL_EXECUTOR, values);
	}
	
}
