package com.android.wondercom.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.wondercom.Entities.Message;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class DB_SOSCHAT extends SQLiteOpenHelper {

    public static final String DB_NOMBRE = "DB_SOSCHAT_v2.db";
    public static final String TABLA_NOMBRE = "MENSAJES_SOSCHAT";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TIPO";
    public static final String COL_3 = "CHATNAME";
    public static final String COL_4 = "BYTEARRAY";
    public static final String COL_5 = "ADDRESS";
    public static final String COL_6 = "FILENAME";
    public static final String COL_7 = "FILESIZE";
    public static final String COL_8 = "FILEPATH";
    public static final String COL_9 = "ISMINE";
    public static final String COL_10 = "MILI_ENVIO";
    public static final String COL_11 = "MILI_RECIBO";
    public static final String COL_12 = "MAC_ORIGEN";
    public static final String COL_13 = "MAC_DESTINO";
    public static final String COL_14 = "ACTIVADOR";
    public static final String COL_15= "TEXTO";

    public DB_SOSCHAT(Context context) {
        super(context, DB_NOMBRE, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                String.format(
                        "CREATE TABLE %s (" +
                                "ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                                ", %s INTEGER , %s TEXT, %s BLOB , %s BLOB , %s TEXT , %s NUMERIC" +
                                ", %s TEXT , %s BOOLEAN , %s NUMERIC , %s NUMERIC , %s TEXT , %s TEXT, %s BOOLEAN, %s TEXT)"
                        ,TABLA_NOMBRE ,COL_2, COL_3, COL_4, COL_5, COL_6, COL_7, COL_8, COL_9, COL_10, COL_11, COL_12, COL_13, COL_14, COL_15
                )
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",TABLA_NOMBRE));
        onCreate(db);
    }

    public void guardarMensaje(Message mensaje){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("INSERT INTO %s VALUES ( NULL, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                TABLA_NOMBRE,
                mensaje.getmType(), //1
                mensaje.getChatName(), //2
                mensaje.getByteArray(),//3
                obtenerInet(mensaje.getSenderAddress()),//4
                mensaje.getFileName(),//5
                mensaje.getFileSize(),//6
                mensaje.getFilePath(),//7
                mensaje.isMine(),//8
                mensaje.tiempoEnvio(),//9
                mensaje.tiempo_recibo(),//10
                mensaje.getMacOrigen(),//11
                mensaje.getMacDestino(),//12
                mensaje.getActivador(),//13
                mensaje.getmText()));//14
    }
    public Cursor selectVerTodos(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(String.format("select * from %s",TABLA_NOMBRE),null);
        return  res;
    }

    public List<Message> mensajesEnDB(){
        Cursor obtenidos= selectVerTodos();
        List<Message> respuesta= new ArrayList<Message>();
        while (obtenidos.moveToNext()){
            Message mensaje= new Message(
                    obtenidos.getInt(1),
                    obtenidos.getString(14),
                    obtenerInetAddress(obtenidos.getBlob(4)),
                    obtenidos.getString(2)
            );
            mensaje.setByteArray(obtenidos.getBlob(3));
            mensaje.setFileName(obtenidos.getString(5));
            mensaje.setFileSize(obtenidos.getLong(6));
            mensaje.setFilePath(obtenidos.getString(7));
            mensaje.setMine(Boolean.parseBoolean(obtenidos.getString(8)));
            mensaje.setMili_envio(obtenidos.getLong(9));
            mensaje.setMili_recibo(obtenidos.getLong(10));
            mensaje.setMacOrigen(obtenidos.getString(11));
            mensaje.setMacDestino(obtenidos.getString(12));
            mensaje.setActivador(Boolean.parseBoolean(obtenidos.getString(13)));
            respuesta.add(mensaje);
        }
        return respuesta;
    }


    public byte[] obtenerInet(InetAddress direccion){
        byte[] resultado=null;
        try{ resultado=direccion.getAddress(); }catch (Exception e){ }
        return resultado;
    }

    public InetAddress obtenerInetAddress(byte[] valor){
        InetAddress respuesta= null;
        try {
            respuesta=InetAddress.getByAddress(valor);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public void eliminarDatos(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM %s", TABLA_NOMBRE));
    }

    public Boolean validarRegistro(Message mes){
        Boolean respuesta=true;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor registro= db.rawQuery(
                String.format(
                        "SELECT * FROM '%s' WHERE '%s' = '%s' ",
                        TABLA_NOMBRE,
                        COL_10,
                        mes.tiempoEnvio()
                ), null );
        if(registro.getCount()>0)
            respuesta=false;
        return respuesta;
    }

    public void actualizarMacDestino(long id, String mac){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format(
                "UPDATE '%s' SET '%s' = '%s' ",
                TABLA_NOMBRE, COL_13,mac, COL_10, id ));
    }
}
