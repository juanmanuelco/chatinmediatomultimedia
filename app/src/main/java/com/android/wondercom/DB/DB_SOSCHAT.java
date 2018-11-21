package com.android.wondercom.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.wondercom.Entities.ENCONTRADO;
import com.android.wondercom.Entities.Message;
import com.android.wondercom.Entities.Miembros;
import com.android.wondercom.Entities.grupos;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class DB_SOSCHAT extends SQLiteOpenHelper {

    public static final String DB_NOMBRE = "DB_SOSCHAT_FINAL.db";
    //Tabla Mensajes -------------------------------------------------------------------------------
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
    public static final String COL_16= "KEY";
    //Tabla Encontrados ----------------------------------------------------------------------------
    public static final String TABLA_ENCONTRADO= "ENCONTRADOS_SOSCHAT";
    public static final String COL_1_ENCONTRADO= "MAC_ADDRESS_ENCONTRADO";
    public static final String COL_2_ENCONTRADO= "NICKNAME";
    //Tabla Miembros -------------------------------------------------------------------------------
    public static final String TABLA_MIEMBROS= "MIEMBROS_SOSCHAT";
    public static final String COL_1_MIEMBROS= "ID_MIEMBRO";
    public static final String COL_2_MIEMBROS= "MAC_ADDRESS_ENCONTRADO";
    public static final String COL_3_MIEMBROS= "ID_GRUPOS";
    public static final String COL_4_MIEMBROS= "FECHA_UNION";
    //Tablas grupos --------------------------------------------------------------------------------
    public static final String TABLA_GRUPOS= "GRUPOS_SOSCHAT";
    public static final String COL_1_GRUPOS= "ID_GRUPOS";
    public static final String COL_2_GRUPOS= "NOMBRES";
    public static final String COL_3_GRUPOS= "FECHA_CREACION";
    //Referencias ----------------------------------------------------------------------------------
    public static final String REFERENCIA_ID_MENSAJE = String.format("REFERENCES %s(%s)",TABLA_NOMBRE,COL_1);
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
        db.execSQL(
                String.format(
                        "CREATE TABLE %s (" +
                                "ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                                ", %s INTEGER , %s TEXT, %s BLOB , %s BLOB , %s TEXT , %s NUMERIC" +
                                ", %s TEXT , %s BOOLEAN , %s NUMERIC , %s NUMERIC , %s TEXT , %s TEXT," +
                                " %s BOOLEAN, %s TEXT, %s NUMERIC)"
                        ,TABLA_NOMBRE ,COL_2, COL_3, COL_4, COL_5, COL_6, COL_7, COL_8, COL_9, COL_10,
                        COL_11, COL_12, COL_13, COL_14, COL_15, COL_16
                )
        );
        //Tabla encontrados ------------------------------------------------------------------------
        db.execSQL(String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT)"
                ,TABLA_ENCONTRADO,COL_1_ENCONTRADO,COL_2_ENCONTRADO));

        //Tabla grupos -----------------------------------------------------------------------------
        db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s text)",
                TABLA_GRUPOS,COL_1_GRUPOS,COL_2_GRUPOS,COL_3_GRUPOS));

        //Tabla miembros ---------------------------------------------------------------------------
        //ID AUTOINCREMENTADO CORREGIR CUANDO SE HAGA UNA ELIMINACION EN CASCADA
        db.execSQL(String.format("CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s TEXT," +
                        "FOREIGN KEY(%s) %s ," +
                        "FOREIGN KEY(%s) %s)",
                TABLA_MIEMBROS,COL_1_MIEMBROS,COL_2_MIEMBROS,COL_3_MIEMBROS,COL_4_MIEMBROS,
                COL_2_MIEMBROS,REFERENCIA_ID_ENCONTRADOS,
                COL_3_MIEMBROS,REFERENCIA_ID_GRUPOS
                ));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",TABLA_NOMBRE));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",TABLA_ENCONTRADO));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",TABLA_MIEMBROS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",TABLA_GRUPOS));
        onCreate(db);
    }

    public void guardarMensaje(Message mensaje){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("INSERT INTO %s VALUES ( NULL, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                TABLA_NOMBRE,
                mensaje.getmType(), //1
                mensaje.getChatName(), //2
                mensaje.getByteArray(),//3
                obtenerInet(mensaje.getSenderAddress()),//4
                mensaje.getFileName(),//5
                mensaje.getFileSize(),//6
                mensaje.getFilePath(),//7
                mensaje.isMine(),//8
                mensaje.tiempoEnvio(),
                mensaje.tiempo_recibo(),
                mensaje.getMacOrigen(),//11
                mensaje.getMacDestino(),//12
                mensaje.getActivador(),//13
                mensaje.getmText(),
                mensaje.getKey()));//14

    }
    public Cursor selectVerTodos(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(String.format("select * from %s ORDER BY ID ASC",TABLA_NOMBRE),null);
        return  res;
    }


    public List<Message> mensajesEnDB(int tipo){
        Cursor obtenidos=null;
            obtenidos= selectVerTodos();

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
            mensaje.setKey(obtenidos.getLong(15));
            respuesta.add(mensaje);
            eliminarDatos();
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
        Cursor registro= db.rawQuery(String.format("SELECT * FROM '%s'", TABLA_NOMBRE), null );
        ArrayList<Long> keys = new ArrayList<Long>();
        while (registro.moveToNext()){
            keys.add(registro.getLong(15));
        }
        if(keys.contains(mes.getKey())){
            respuesta=false;
        }
        return respuesta;
    }

    public void actualizarMacDestino(long id, String mac){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format(
                "UPDATE '%s' SET '%s' = '%s' WHERE '%s' = '%s' ",
                TABLA_NOMBRE, COL_13,mac, COL_16, id ));
    }
    //-------------------------------------- CRUD ENCONTRADOS --------------------------------------
    // CURSOR PARA OBTENER TODOS LOS ENCONTRADOS ---------------------------------------------------
    public Cursor Obtener_encontrado(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor sql = db.rawQuery(String.format("select * from '%s' ",TABLA_ENCONTRADO),null);
        return sql;
    }

    public List<ENCONTRADO> encontradosLista(){
        Cursor obtenidos=null;
        obtenidos = Obtener_encontrado();
        List<ENCONTRADO> respuesta= new ArrayList<ENCONTRADO>();
        while (obtenidos.moveToNext()){
            //Log.i("Guardaddos",obtenidos.getString(0)+" "+obtenidos.getString(1));
            ENCONTRADO encontrar = new ENCONTRADO(obtenidos.getString(0),obtenidos.getString(1));
            respuesta.add(encontrar);
        }
        return respuesta;
    }

    // INSERTAR DISPOSITIVOS ENCONTRADOS -----------------------------------------------------------
    public void insertar_Encontrados(String address, String nickname){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor verificar=null;
        verificar=Obtener_encontrado();
        try{
            db.execSQL(String.format("INSERT INTO %s VALUES ( '%s', '%s')",
                    TABLA_ENCONTRADO,address,nickname));

            //Log.i("cursor ",String.valueOf(verificar.getPosition()));
            //Log.i("Count",String.valueOf(verificar.getCount()));
            //Log.i("Contenido",String.valueOf(verificar.getString(0)+" "+ verificar.getString(1)));
        }catch (Exception e){
            Log.i("MAC_Repetida", String.valueOf(e));
        }
    }
    // ELIMINAR DISPOSITIVOS ENCONTRADOS -----------------------------------------------------------
    public void Eliminar_encontrados(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM %s", TABLA_ENCONTRADO));
    }

    //-------------------------------------- CRUD GRUPOS --------------------------------------
    // CURSOR PARA OBTENER TODOS LOS GRUPOS --------------------------------------------------------
    public Cursor Obtener_grupos(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor sql = db.rawQuery(String.format("select * from '%s' ",TABLA_GRUPOS),null);
        return sql;
    }

    public List<grupos> GruposLista(){
        Cursor obtenidos=null;
        obtenidos = Obtener_grupos();
        List<grupos> grupos= new ArrayList<grupos>();
        while (obtenidos.moveToNext()){
            Log.i("Guardaddos",obtenidos.getString(0)+" "+obtenidos.getString(1)+
                                        " "+obtenidos.getString(2));
            grupos encontrados = new grupos(
                    obtenidos.getString(0),
                    obtenidos.getString(1),
                    obtenidos.getString(2));
            grupos.add(encontrados);
        }
        return grupos;
    }
    // INSERTAR DISPOSITIVOS ENCONTRADOS -----------------------------------------------------------
    public void insertar_grupos(grupos grupo){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor verificar=null;
        verificar=Obtener_grupos();
        try{
            db.execSQL(String.format("INSERT INTO %s VALUES ( '%s', '%s', '%s')",
                    TABLA_GRUPOS,grupo.getId_grupo(),grupo.getNombre(),grupo.getFecha()));
        }catch (Exception e){
            Log.i("Grupo_repetido", String.valueOf(e));
        }
    }
    // ELIMINAR DISPOSITIVOS ENCONTRADOS -----------------------------------------------------------
    public void Eliminar_grupos(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM %s", TABLA_GRUPOS));
    }

    //-------------------------------------- CRUD MIEMBROS --------------------------------------
    // CURSOR PARA OBTENER TODOS LOS MIEMBROS --------------------------------------------------------
    public Cursor Obtener_miembros(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor sql = db.rawQuery(String.format("select * from '%s' ",TABLA_MIEMBROS),null);
        return sql;
    }

    public List<Miembros> MiembrosLista(){
        Cursor obtenidos=null;
        obtenidos = Obtener_miembros();
        List<Miembros> grupos= new ArrayList<Miembros>();
        while (obtenidos.moveToNext()){
            Log.i("Guardaddos",obtenidos.getString(0)+" "+obtenidos.getString(1)+
                    " "+obtenidos.getString(2)+" "+obtenidos.getString(3));
            Miembros encontrados = new Miembros(
                    obtenidos.getString(0),
                    obtenidos.getString(1),
                    obtenidos.getString(2),
                    obtenidos.getString(3));
            grupos.add(encontrados);
        }
        return grupos;
    }
    // INSERTAR DISPOSITIVOS ENCONTRADOS -----------------------------------------------------------
    public void insertar_miembros(Miembros miembro){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor verificar=null;
        verificar=Obtener_miembros();
        try{
            db.execSQL(String.format("INSERT INTO %s VALUES ( '%s', '%s', '%s', '%s')",
                    TABLA_GRUPOS,miembro.getId(),miembro.getMac_encontrado(),miembro.getId_grupo(),miembro.getFecha()));
        }catch (Exception e){
            Log.i("Miembro repetido", String.valueOf(e));
        }
    }
    // ELIMINAR DISPOSITIVOS ENCONTRADOS -----------------------------------------------------------
    public void Eliminar_miembro(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM %s", TABLA_MIEMBROS));
    }
}
