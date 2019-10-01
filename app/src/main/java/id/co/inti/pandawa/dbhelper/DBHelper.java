package id.co.inti.pandawa.dbhelper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteAssetHelper {
    private static final String DBNAME = "database.db";
    private static final int VERSION = 1;
    private SQLiteDatabase myDB;

    public DBHelper(Context context) {

        //super(context,"/storage/emulated/0/"+DBNAME,null,VERSION);
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME1 );

        onCreate(db);
    }

    public void openDB() {
        myDB = getWritableDatabase();
    }

    public void closeDB() {
        if (myDB != null && myDB.isOpen())
            myDB.close();
    }





    public int insertDevice(String value, String parser, String names, String username){

        String admin = null;
        int i=0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "INSERT INTO device (id_device, parser, device_name, username) VALUES ( '"+ value+"',  '"+parser+"',  '"+names+"',  '"+ username +"' )";
        try{
            Log.d("SQL_update =",query);
            myDB.execSQL(query);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return -1;
        }
        return i;

    }

    public int insertPhoto(String names, byte[] photo){

        String admin = null;
        int i=0;
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "REPLACE INTO user (username,photo) VALUES(?,?)";
        SQLiteStatement insertStmt = db.compileStatement(sql);
        insertStmt.clearBindings();
        insertStmt.bindString(1, names);
        insertStmt.bindBlob(2, photo);
        insertStmt.executeInsert();
        Log.d("tag","SUKSES");
        return i;

    }

    public int insertWidget(String usernames, String names, String descriptions, String types, String id_devices, String data_keys, String units, String parser){

        String admin = null;
        int i=0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "INSERT INTO widget (username, name, description, type , id_device, data_key, unit, parser) VALUES ('"+ usernames +"',  '"+ names +"',  '"+ descriptions +"',  '"+ types +"',  '"+ id_devices +"',  '"+ data_keys +"',  '"+ units +"',  '"+ parser +"') ";
        try{
            Log.d("SQL_update =",query);
            myDB.execSQL(query);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return -1;
        }
        return i;
    }



    public void updatewidget(String id,String field){
        String query = "update  device SET  '"+field+"' = 1 where id_device = '"+ id +"'" ;
        try{
            Log.d("SQL_update =",query);
            myDB.execSQL(query);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


    public Cursor getspecifiRecords(int id ) {
        String query = "SELECT  id_device, type, temp,humid, light, map, door, magnet FROM device where id_device = '"+ id +"'" ;
        return this.myDB.rawQuery(query, (String[])null);
    }

    public Cursor getDevice(String user_name) {
        String query = "SELECT  id_device FROM device where username = '"+user_name+"'" ;
        return this.myDB.rawQuery(query, (String[])null);
    }

    public Cursor getImageDevice( String username) {
        String query = "SELECT  device.id_device , device.device_name, device.parser, category.image FROM device INNER JOIN category WHERE device.username =  '"+ username +"'" ;
        return this.myDB.rawQuery(query, (String[])null);
    }


    public String[] getDevices() {
        String query = "SELECT  id_device FROM device" ;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<String> names = new ArrayList<String>();
        while(!cursor.isAfterLast() && cursor.getCount()!=0) {
            names.add(cursor.getString(cursor.getColumnIndex("name")));
            cursor.moveToNext();
        }
        cursor.close();
        return names.toArray(new String[names.size()]);
    }

    public String[] getNameWidget(String username) {
        String query = "SELECT  name FROM widget WHERE username = '" + username + "' " ;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<String> names = new ArrayList<String>();
        while(!cursor.isAfterLast() && cursor.getCount()!=0) {
            names.add(cursor.getString(cursor.getColumnIndex("name")));
            cursor.moveToNext();
        }
        cursor.close();
        return names.toArray(new String[names.size()]);
    }

    public String[] getdescriptionWidget(String username) {
        String query = "SELECT  description FROM widget WHERE username = '" + username + "' " ;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<String> names = new ArrayList<String>();
        while(!cursor.isAfterLast() && cursor.getCount()!=0) {
            names.add(cursor.getString(cursor.getColumnIndex("description")));
            cursor.moveToNext();
        }
        cursor.close();
        return names.toArray(new String[names.size()]);
    }

    public String[] gettypeWidget(String username) {
        String query = "SELECT  type FROM widget WHERE username = '" + username + "' " ;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<String> names = new ArrayList<String>();
        while(!cursor.isAfterLast() && cursor.getCount()!=0) {
            names.add(cursor.getString(cursor.getColumnIndex("type")));
            cursor.moveToNext();
        }
        cursor.close();
        return names.toArray(new String[names.size()]);
    }

    public String[] getDeviceWidget(String username) {
        String query = "SELECT  id_device FROM widget WHERE username = '" + username + "' " ;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<String> names = new ArrayList<String>();
        while(!cursor.isAfterLast() && cursor.getCount()!=0) {
            names.add(cursor.getString(cursor.getColumnIndex("id_device")));
            cursor.moveToNext();
        }
        cursor.close();
        return names.toArray(new String[names.size()]);
    }

    public String[] getDataWidget(String username) {
        String query = "SELECT  data_key FROM widget WHERE username = '" + username + "' " ;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<String> names = new ArrayList<String>();
        while(!cursor.isAfterLast() && cursor.getCount()!=0) {
            names.add(cursor.getString(cursor.getColumnIndex("data_key")));
            cursor.moveToNext();
        }
        cursor.close();
        return names.toArray(new String[names.size()]);
    }
    public String[] getunitWidget(String username) {
        String query = "SELECT  unit FROM widget WHERE username = '" + username + "' " ;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<String> names = new ArrayList<String>();
        while(!cursor.isAfterLast() && cursor.getCount()!=0) {
            names.add(cursor.getString(cursor.getColumnIndex("unit")));
            cursor.moveToNext();
        }
        cursor.close();
        return names.toArray(new String[names.size()]);
    }

    public String[] getparserWidget(String username) {
        String query = "SELECT  parser FROM widget WHERE username = '" + username + "' " ;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<String> names = new ArrayList<String>();
        while(!cursor.isAfterLast() && cursor.getCount()!=0) {
            names.add(cursor.getString(cursor.getColumnIndex("parser")));
            cursor.moveToNext();
        }
        cursor.close();
        return names.toArray(new String[names.size()]);
    }

    public String getparser(String username, String id) {
        String query = "SELECT  parser FROM device WHERE username = '" + username + "' AND id_device = '" + id + "' " ;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        String names=null;
        while(!cursor.isAfterLast() && cursor.getCount()!=0) {
            names=(cursor.getString(cursor.getColumnIndex("parser")));
            cursor.moveToNext();
        }
        cursor.close();
        return names;
    }







    public Cursor getCategory() {
        String query = "SELECT  type,image  FROM category" ;
        return this.myDB.rawQuery(query, (String[])null);
    }
    public byte [] getBitmap(String NamaKantor) {

        String admin = null;
        byte [] add = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT image FROM category WHERE type ='" + NamaKantor + "'";
        Cursor mCursor = db.rawQuery(query, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            while (mCursor.isAfterLast() == false) {
                byte [] adm = mCursor.getBlob(mCursor.getColumnIndex("image"));
                add = new byte[adm.length];
                add= adm;
                mCursor.moveToNext();
            }
        }
        return add;
    }

    public byte [] getPhoto(String NamaKantor) {

        String admin = null;
        byte [] add = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT photo FROM user WHERE username ='" + NamaKantor + "'";
        Cursor mCursor = db.rawQuery(query, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            while (mCursor.isAfterLast() == false) {
                byte [] adm = mCursor.getBlob(mCursor.getColumnIndex("photo"));
                add = new byte[adm.length];
                add= adm;
                mCursor.moveToNext();
            }
        }
        return add;
    }

    public int getCountWidget(String username) {

        String admin = null;
        int i=0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(id_device) AS jumlah FROM widget WHERE username ='" + username + "'";
        Cursor mCursor = db.rawQuery(query, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            while (mCursor.isAfterLast() == false) {
                i = mCursor.getInt(mCursor.getColumnIndex("jumlah"));
                mCursor.moveToNext();
            }
        }
        return i;
    }

    public int getCountDevice(String username, String id) {

        String admin = null;
        int i=0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(id_device) AS jumlah FROM device WHERE username ='" + username + "'  AND id_device ='" + id + "'";
        Cursor mCursor = db.rawQuery(query, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            while (mCursor.isAfterLast() == false) {
                i = mCursor.getInt(mCursor.getColumnIndex("jumlah"));
                mCursor.moveToNext();
            }
        }
        return i;
    }

    public int deleteWidget(String username, String id) {
        String admin = null;
        int i=0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "DELETE FROM widget WHERE username ='" + username + "'  AND name ='" + id + "'";
        Log.d("tag",query);

        Cursor mCursor = db.rawQuery(query, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            while (mCursor.isAfterLast() == false) {
                i = mCursor.getInt(mCursor.getColumnIndex("jumlah"));
                mCursor.moveToNext();
            }
        }
        return i;
    }


}
