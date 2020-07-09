package com.example.milkapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class DeliveryMonsdayDBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_ADDRESS = "ADDRESS";
    public static final String KEY_PASSWORD = "PASSWORD";
    public static final String KEY_MILK = "MILK";
    public static final String KEY_NUMBER = "NUMBER";

    private static final String DATABASE_CREATE = "create table DELIVERY_MONSDAY (_id integer primary key, "
            +"ADDRESS text not null, PASSWORD text, "
            +"MILK text, NUMBER int);";

    private static final String DATABASE_NAME = "MILKDELIVERY_MONSDAY";
    private static final String DATABASE_TABLE = "DELIVERY_MONSDAY";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public DeliveryMonsdayDBAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS DELIVERY_MONSDAY");
            onCreate(db);
        }
    }

    public DeliveryMonsdayDBAdapter open() throws SQLException {
        myDBHelper = new DatabaseHelper(mCtx);
        sqlDB = myDBHelper.getWritableDatabase();
        return this;
    }

    public void databaseReset() {
        sqlDB.delete(DATABASE_TABLE, null, null);
    }

    public void close() {
        myDBHelper.close();
    }

    public long insertMilk(String address, String password, String milk, int number) {
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, address);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_MILK, milk);
        values.put(KEY_NUMBER, number);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteMilk(String address) {
        Log.i("Delete called.", "value___"+address);
        return sqlDB.delete(DATABASE_TABLE, KEY_ADDRESS+"='"+address+"'", null) > 0;
    }

    public boolean deleteAllMilk() {
        Log.i("Delete called.", "value___all");
        return sqlDB.delete(DATABASE_TABLE, null, null) > 0;
    }

    public Cursor fetchAllMilk() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_ADDRESS, KEY_PASSWORD, KEY_MILK, KEY_NUMBER}, null, null, null, null, null);
    }

    /*public Cursor fetch(long rowId) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_WEAPON, KEY_TALENT, KEY_LOCATION, KEY_CONTENT}, KEY_ROWID+"="+rowId, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }*/

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public boolean isEmptyAddress(String address) {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_ADDRESS+" = '"+address+"';", null);
        if (cursor.moveToFirst()) {
            Log.d(TAG, "isEmptyAddress : not empty");
            return false;
        }
        return true;
    }

    public Cursor fetchAddressMilk(String address) {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_ADDRESS, KEY_PASSWORD, KEY_MILK, KEY_NUMBER}, KEY_ADDRESS+" = '"+address+"'", null, null, null, null);
    }

    public boolean updateMilk(String undo_address, String address, String password, String milk, int number) {
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, address);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_MILK, milk);
        values.put(KEY_NUMBER, number);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ADDRESS+"='"+undo_address+"'", null) > 0;
    }
}
