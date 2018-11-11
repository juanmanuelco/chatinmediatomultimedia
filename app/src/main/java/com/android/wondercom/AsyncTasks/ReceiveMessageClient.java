package com.android.wondercom.AsyncTasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

import com.android.wondercom.ChatActivity;
import com.android.wondercom.DB.DB_SOSCHAT;
import com.android.wondercom.InicioActivity;
import com.android.wondercom.MainActivity;
import com.android.wondercom.Entities.Message;
import com.android.wondercom.NEGOCIO.DireccionMAC;

import static com.android.wondercom.NEGOCIO.Mensajes.getMacAddr;

public class ReceiveMessageClient extends AbstractReceiver {
	private static final int SERVER_PORT = 4446;
	private Context mContext;
	private ServerSocket socket;
	DB_SOSCHAT db;

	public ReceiveMessageClient(Context context){
		mContext = context;
		this.db = new DB_SOSCHAT(context);
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			socket = new ServerSocket(SERVER_PORT);
			while(true){
				Socket destinationSocket = socket.accept();
				
				InputStream inputStream = destinationSocket.getInputStream();
				BufferedInputStream buffer = new BufferedInputStream(inputStream);
				ObjectInputStream objectIS = new ObjectInputStream(buffer);

				long millis= System.currentTimeMillis();
				Message message = (Message) objectIS.readObject(); //Aqui va el agregado
				message.setMili_recibo(millis);

				if(message.getMacOrigen().equals(getMacAddr())){
					if(message.getMacDestino().equals("")){
						message.setMacDestino(DireccionMAC.direccion);
					}
				}else{
					DireccionMAC.direccion=(message.macDestino.equals(getMacAddr()))?message.getMacOrigen():"";
					if(message.getMacDestino().equals("")){
						message.setMacDestino(getMacAddr());
					}
				}

				if(db.validarRegistro(message)){
					db.guardarMensaje(message);
					//db.actualizarMacDestino(mes.getKey(), getMacAddr());
				}

				destinationSocket.close();
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
			socket.close();
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
		
		if(isActivityRunning(InicioActivity.class)){
			ChatActivity.refreshList(values[0], false);
		}			
	}
	
	@SuppressWarnings("rawtypes")
	public Boolean isActivityRunning(Class activityClass) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
	}
}
