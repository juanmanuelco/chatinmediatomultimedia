package juanmanuelco.facci.com.soschat.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;


import juanmanuelco.facci.com.soschat.Entidades.Mensaje;
import juanmanuelco.facci.com.soschat.R;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static juanmanuelco.facci.com.soschat.NEGOCIO.Mensajes.getMacAddr;

public class DB_SOSCHAT extends SQLiteOpenHelper {

    public static final String DB_NOMBRE = "DB_SOSCHAT_FINAL_V2.db";
    //Tabla Mensajes -------------------------------------------------------------------------------
    public static final String TABLA_MENSAJES = "MENSAJES_SOSCHAT";
    public static final String COL_1 = "ID"; //0
    public static final String COL_2 = "TIPO"; //1
    public static final String COL_3 = "TEXTO"; //2
    public static final String COL_4 = "CHATNAME"; //3
    public static final String COL_5 = "BYTEARRAY"; //4
    public static final String COL_6 = "DIRECCION"; //5
    public static final String COL_7 = "NOMBRE_ARCHIVO"; //6
    public static final String COL_8 = "TAMANO_ARCHIVO"; //7
    public static final String COL_9 = "PATH_ARCHIVO"; //8
    public static final String COL_10 = "MAC_ORIGEN"; //9
    public static final String COL_11 = "MAC_DESTINO"; //10
    public static final String COL_12 = "TIEMPO_ENVIO"; //11
    public static final String COL_13 = "TIEMPO_RECIBO"; //12

    //Tabla usuario --------------------------------------------------------------------------------
    public static final String TABLA_ENCONTRADO= "USUARIOS";
    public static final String COL_1_ENCONTRADO= "USER_MAC";
    public static final String COL_2_ENCONTRADO= "USER_NAME";
    public static final String COL_3_ENCONTRADO= "USER_ESTADO";
    //Tabla Miembros -------------------------------------------------------------------------------
    public static final String TABLA_MIEMBROS= "MIEMBROS_SOSCHAT";
    public static final String COL_1_MIEMBROS= "MEMBER_ID";
    public static final String COL_2_MIEMBROS= "MEMBER_MAC";
    public static final String COL_3_MIEMBROS= "GROUP_ID";
    public static final String COL_4_MIEMBROS= "MEMBER_DATE";
    //Tablas grupos --------------------------------------------------------------------------------
    public static final String TABLA_GRUPOS= "GRUPOS_SOSCHAT";
    public static final String COL_1_GRUPOS= "GROUP_ID";
    public static final String COL_2_GRUPOS= "GROUP_NAME";
    public static final String COL_3_GRUPOS= "GROUP_DATE";
    //Referencias ----------------------------------------------------------------------------------
    public static final String REFERENCIA_ID_MENSAJE = String.format("REFERENCES %s(%s)",TABLA_MENSAJES,COL_1);
    public static final String REFERENCIA_ID_MIEMBROS = String.format("REFERENCES %s(%s)",TABLA_MIEMBROS,COL_1_MIEMBROS);
    public static final String REFERENCIA_ID_GRUPOS = String.format("REFERENCES %s(%s)",TABLA_GRUPOS,COL_1_GRUPOS);
    public static final String REFERENCIA_ID_ENCONTRADOS = String.format(" REFERENCES %s(%s)",TABLA_ENCONTRADO,COL_1_ENCONTRADO);



    public DB_SOSCHAT(Context context) {
        super(context, DB_NOMBRE, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Tabla mensaje ----------------------------------------------------------------------------

        db.execSQL(String.format(
                "CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s INTEGER, %s TEXT, %s TEXT, %s BLOB , %s BLOB, %s TEXT, %s NUMERIC, %s TEXT" +
                        ",%s TEXT , %s TEXT, %s NUMERIC, %s NUMERIC )", TABLA_MENSAJES,
                COL_2, COL_3, COL_4, COL_5, COL_6, COL_7, COL_8, COL_9, COL_10, COL_11, COL_12, COL_13
        ));

        //Tabla usuario ----------------------------------------------------------------------------
        db.execSQL(String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT, %s NUMERIC)"
                ,TABLA_ENCONTRADO,COL_1_ENCONTRADO,COL_2_ENCONTRADO,COL_3_ENCONTRADO));

        //Tabla grupos -----------------------------------------------------------------------------
        db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT)",
                TABLA_GRUPOS,COL_1_GRUPOS,COL_2_GRUPOS,COL_3_GRUPOS));

        //Tabla miembros ---------------------------------------------------------------------------
        //ID AUTOINCREMENTADO CORREGIR CUANDO SE HAGA UNA ELIMINACION EN CASCADA
        /*db.execSQL(String.format("CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT," +
                        "FOREIGN KEY(%s) %s)",
                TABLA_MIEMBROS,COL_1_MIEMBROS,COL_2_MIEMBROS,COL_3_MIEMBROS,COL_4_MIEMBROS,
                COL_3_MIEMBROS,REFERENCIA_ID_GRUPOS
                ));*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",TABLA_MENSAJES));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",TABLA_ENCONTRADO));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",TABLA_MIEMBROS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",TABLA_GRUPOS));
        onCreate(db);
    }

    //_______________________________________MENSAJES_______________________________________________

    public Cursor selectVerTodos(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(String.format("select * from %s ORDER BY ID ASC",TABLA_MENSAJES),null);
        return  res;
    }

    public void guardarMensaje(Mensaje m){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("INSERT INTO %s VALUES ( NULL, '%s', '%s', '%s', '%s', '%s', " +
                        "'%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                TABLA_MENSAJES,
                m.getTipo(),
                m.getTexto(),
                m.getChatName(),
                m.getByteArray(),
                obtenerInet(m.getAddress()),
                m.getNombreArchivo(),
                m.getTamanoArchivo(),
                m.getPathArchivo(),
                m.getMacOrigen(),
                m.getMacDestino(),
                m.getTiempoEnvio(),
                m.getTiempoRecibo()
        ));
    }

    public List<Mensaje> mensajesEnDB(){
        Cursor obtenidos = selectVerTodos();
        List<Mensaje> respuesta= new ArrayList<Mensaje>();
        while (obtenidos.moveToNext()){
            Mensaje mensaje= new Mensaje(
                    obtenidos.getInt(1),
                    obtenidos.getString(2),
                    obtenerInetAddress(obtenidos.getBlob(5)),
                    obtenidos.getString(3)
            );
            mensaje.setByteArray(obtenidos.getBlob(4));
            mensaje.setNombreArchivo(obtenidos.getString(6));
            mensaje.setTamanoArchivo(obtenidos.getLong(7));
            mensaje.setPathArchivo(obtenidos.getString(8));
            mensaje.setMacOrigen(obtenidos.getString(9));
            mensaje.setMacDestino(obtenidos.getString(10));
            mensaje.setTiempoEnvio(obtenidos.getLong(11));
            mensaje.setTiempoRecibo(obtenidos.getLong(12));
            respuesta.add(mensaje);
        }
        return respuesta;
    }

    public ArrayList<String[]> mensajesRecibidos(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String[]> respuesta= new ArrayList<>();
        Cursor obtenidos = db.rawQuery(
                String.format(
                        "SELECT * FROM %s WHERE (%s IN (SELECT MAX (%s) FROM %s GROUP BY %s ) ) AND (%s = '%s') ORDER BY %s DESC",
                        TABLA_MENSAJES, COL_12, COL_12, TABLA_MENSAJES, COL_10, COL_11, getMacAddr(), COL_12),null);
        while (obtenidos.moveToNext()){
            respuesta.add(new String[]{ obtenidos.getString(3),obtenidos.getString(2), obtenidos.getLong(11)+""});
        }
        return respuesta;
    }




    public InetAddress obtenerInetAddress(byte[] valor){
        InetAddress respuesta= null;
        try {respuesta=InetAddress.getByAddress(valor);
        } catch (UnknownHostException e) { e.printStackTrace();}
        return respuesta;
    }

    public byte[] obtenerInet(InetAddress direccion){
        byte[] resultado=null;
        try{ resultado=direccion.getAddress(); }catch (Exception e){ }
        return resultado;
    }

    public Boolean validarRegistro(Mensaje mes){
        Boolean respuesta=true;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor registro= db.rawQuery(String.format("SELECT * FROM '%s'", TABLA_MENSAJES), null );
        ArrayList<Long> keys = new ArrayList<Long>();
        while (registro.moveToNext()){
            keys.add(registro.getLong(11));
        }
        if(keys.contains(mes.getTiempoEnvio())) respuesta=false;
        return respuesta;
    }

    public void actualizarMacDestino(long id, String mac){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+ TABLA_MENSAJES+ " SET MAC_DESTINO = '"+mac+"' WHERE TIEMPO_ENVIO = "+ id + " AND MAC_DESTINO = "+ "''");
    }

    public void actualizarTiempoRecibo(long id, long tiempo){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format(
                "UPDATE '%s' SET '%s' = '%s' WHERE '%s' = '%s' ",
                TABLA_MENSAJES, COL_11,tiempo, COL_12, id ));
        db.execSQL("UPDATE "+ TABLA_MENSAJES+ " SET TIEMPO_RECIBO = "+tiempo+" WHERE TIEMPO_ENVIO = "+ id + " AND TIEMPO_RECIBO = 0");
    }

    public void eliminarMensajes(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM %s", TABLA_MENSAJES));
    }

    public void finVidaMensaje(long milisegundo){
        milisegundo=milisegundo-86400000;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM %s WHERE %s < %s ", TABLA_MENSAJES, COL_12, milisegundo));
    }


    //-------------------------------------- CRUD ENCONTRADOS --------------------------------------
    // CURSOR PARA OBTENER TODOS LOS ENCONTRADOS ---------------------------------------------------

    public Boolean validarAgregado(String mac){
        Boolean respuesta= false;
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement s = db.compileStatement( "SELECT (USER_ESTADO) from USUARIOS WHERE USER_MAC = '"+mac+"'" );
        Boolean data=Boolean.parseBoolean(s.simpleQueryForString());
        if(data) respuesta=true;
        return respuesta;
    }


    public void insertarUsuario(String Mac, String Nombre){
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement s = db.compileStatement( "SELECT COUNT (USER_MAC) from USUARIOS WHERE USER_MAC = '"+Mac+"'" );
        long count = s.simpleQueryForLong();
        if(count <1) db.execSQL(String.format("INSERT INTO %s VALUES ( '%s', '%s', 'false')", TABLA_ENCONTRADO, Mac, Nombre));
    }
    public int ActualizarUsuario(String Mac, Boolean estado){
        int respuesta=0;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("UPDATE %s SET %s = '%s' WHERE %s = '%s'", TABLA_ENCONTRADO, COL_3_ENCONTRADO, estado, COL_1_ENCONTRADO, Mac ));
        if(estado) respuesta=R.string.ADD;
        else respuesta=R.string.NADD;
        return respuesta;
    }

    public ArrayList<String[]> listaEncontrados(){
        ArrayList<String[]> respuesta= new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor res = db.rawQuery(String.format("select * from %s ",TABLA_ENCONTRADO),null);
            while (res.moveToNext()) {
                if (Boolean.parseBoolean(res.getString(2)))
                    respuesta.add(new String[]{res.getString(1), res.getString(0)});
            }
        }catch (SQLException e){
            Log.i("error",e.toString());
        }
        return respuesta;
    }

    public ArrayList<String> buscador(){
        ArrayList<String> respuesta= new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor res = db.rawQuery(String.format("select * from %s ",TABLA_ENCONTRADO),null);
            while (res.moveToNext()) {
                if (Boolean.parseBoolean(res.getString(2)))
                    respuesta.add(res.getString(1)+","+res.getString(0));
            }
        }catch (SQLException e){
            Log.i("error",e.toString());
        }
        return respuesta;
    }

}
