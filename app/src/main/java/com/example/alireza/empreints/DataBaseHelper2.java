package com.example.alireza.empreints;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alireza on 06/11/15.
 */
public class DataBaseHelper2 extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;

    private static final String DATABASE_NAME="records.db";

    private static final String TABLE_NAME="records";

    private static final String COLUMN_ID="id";
    private static final String COLUMN_NAME="recordsName";
    private static final String COLUMN_USERNAME="contactUsername";
    private static final String COLUMN_COMMENTS="commnets";
    private static final String COLUMN_DATE="date";
    private static final String COLUMN_LNG="lng";
    private static final String COLUMN_LAT="lat";
    private static final String COLUMN_RANKING="ranking";

    SQLiteDatabase db;

    private static final String TABLE_CREATE="create table records(id integer primary key not null,"+
            "recordsName text not null, contactUsername text not null, commnets text not null, date text not null, " +
            "lng text not null, lat text not null, ranking text not null)";

    public DataBaseHelper2(Context context){
        super(context , DATABASE_NAME, null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        this.db=db;
    }

    public void insertRecords(Records r){
        db=this.getWritableDatabase();
        ContentValues values=new ContentValues();

        String query="select * from records";
        Cursor cursor=db.rawQuery(query,null);
        int count=cursor.getCount();
        values.put(COLUMN_ID, count);
        //String newName=r.getRecordsName().replaceAll("'", "''");
        values.put(COLUMN_NAME,r.getRecordsName());
        values.put(COLUMN_USERNAME, r.getContactUsername());
        values.put(COLUMN_COMMENTS, r.getCommnets());
        values.put(COLUMN_DATE, r.getDate());
        values.put(COLUMN_LNG, r.getLng());
        values.put(COLUMN_LAT, r.getLat());
        values.put(COLUMN_RANKING, r.getRanking());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Records> searchContactByDate(String uname, String date){
        db=this.getReadableDatabase();
        String query="select contactUsername,recordsName,commnets,date,lng,lat,ranking from "+TABLE_NAME;
        Cursor cursor =db.rawQuery(query,null);
        List<Records> records=new ArrayList<Records>();
        String a,b; Date dt;
        if(cursor.moveToFirst()){
            do{
                a=cursor.getString(0);
                b=cursor.getString(3);
                if(a.equals(uname)&& b.equals(date)){
                    Records newRecord=new Records();
                    newRecord.setContactUsername(uname);
                    newRecord.setRecordsName(cursor.getString(1));
                    newRecord.setCommnets(cursor.getString(2));
                    newRecord.setDate(cursor.getString(3));
                    newRecord.setLng(cursor.getString(4));
                    newRecord.setLat(cursor.getString(5));
                    newRecord.setRanking(cursor.getString(6));
                    records.add(newRecord);
                    //break;
                }
            }
            while(cursor.moveToNext());
        }
        return records;
    }
    public int returnCount(String username){
        db=this.getReadableDatabase();
        int a=0;
        String query="select COUNT(*) from "+TABLE_NAME+" where contactUsername = '"+username+"'";
        Cursor cursor =db.rawQuery(query,null);
        if(cursor.moveToFirst()) {
            do {
                a++;
            }
            while (cursor.moveToNext());
        }

        return a;
    }
    public int existanceOfAnAddresse(String username, String date, String address){
        db=this.getReadableDatabase();
        //address = address.replaceAll("'", "''");
        String query="select * from "+TABLE_NAME
                + " where contactUsername = '"+username+"' AND date='"+date+"'"
                +" AND recordsName = '"+address+"'";
        Cursor cursor =db.rawQuery(query,null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public Records serach_record_by_name(String name, String date, String username){
        db=this.getReadableDatabase();
        name=name.replaceAll("'", "");
        String query="select * from "+TABLE_NAME
                + " where contactUsername = '"+username+"' AND date='"+date+"'"
                +" AND recordsName = '"+name+"'";
        Cursor cursor =db.rawQuery(query,null);
        List<Records> records=new ArrayList<Records>();
        if(cursor.moveToFirst()){
            do{
                    Records newRecord=new Records();
                    newRecord.setContactUsername(cursor.getString(cursor.getColumnIndex("contactUsername")));
                    newRecord.setRecordsName(cursor.getString(cursor.getColumnIndex("recordsName")));
                    newRecord.setCommnets(cursor.getString(cursor.getColumnIndex("commnets")));
                    newRecord.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    newRecord.setLng(cursor.getString(cursor.getColumnIndex("lng")));
                    newRecord.setLat(cursor.getString(cursor.getColumnIndex("lat")));
                    newRecord.setRanking(cursor.getString(cursor.getColumnIndex("ranking")));
                    records.add(newRecord);
                    //break;
            }
            while(cursor.moveToNext());
        }
        return records.get(0);
    }

    public int update_record(Records original, Records newRecord, String str){
        db=this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("recordsName", str);
        values.put("commnets", newRecord.getCommnets());
        values.put("ranking", newRecord.getRanking());

        String [] whereArgs = new String[3];
        whereArgs[0] = original.getContactUsername();
        whereArgs[1] = original.getDate();
        whereArgs[2] = original.getRecordsName();
        String query= "contactUsername =? AND date=? AND recordsName =?";
        return db.update(TABLE_NAME, values, query, whereArgs);
    }
    public int delete_record(Records r){
        db=this.getWritableDatabase();
        String query= "contactUsername =? AND date=? AND recordsName =?";
        String [] whereArgs = new String[3];
        whereArgs[0] = r.getContactUsername();
        whereArgs[1] = r.getDate();
        whereArgs[2] = r.getRecordsName();
        return db.delete(TABLE_NAME,query,whereArgs);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS "+ TABLE_NAME;
        db.execSQL(query);
        this.onCreate(db);
    }
}
