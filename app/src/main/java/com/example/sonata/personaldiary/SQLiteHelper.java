package com.example.sonata.personaldiary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteHelper extends SQLiteOpenHelper{

    //**创建数据库
    public void onCreate(SQLiteDatabase db)
    {
        Log.e("SqliteHelper","数据库创建");
        //bitmap作为String存储
        String sql = "create table diary(id integer Primary Key autoincrement,title varchar(20), data varchar(20), text varchar(200),bitmap varchar(50));";
        db.execSQL(sql);
    }

    //**
    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }
    //**数据库更新
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.e("SqliteHelper","数据库更新");
    }

    //diary为封装后的对象
    public void addDiary(Diary diary)
    {
        Log.e("SqliteHelper","插入数据");
        //读写形式打开数据库
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(
                "insert into diary(title,data,text,bitmap) values("
                        + String.format("'%s'", diary.getTitle()) + ","
                        + String.format("'%s'", diary.getData()) + ","
                        + String.format("'%s'", diary.getText()) + ","
                        + String.format("'%s'",diary.getBitmap()) +
                        ");"
        ); // 插入数据库
        db.close();
    }

    public void updateDiary(Diary diary)
    {
        Log.e("SqliteHelper","更新数据");

        //以读写形式打开数据库
        SQLiteDatabase db = getWritableDatabase();
        String sql = "update diary set title =" + String.format("'%s'",diary.getTitle())
                + ",data = " + String.format("'%s'",diary.getData())
                + ",text = " + String.format("'%s'",diary.getText())
                + ",bitmap = " + String.format("'%s'",diary.getBitmap())
                + "where id = " + diary.getId();
        db.execSQL(sql);
        db.close();
    }

    public void deleteDiary(int id)
    {
        Log.e("SqliteHelper","删除数据");
        SQLiteDatabase db = getWritableDatabase();
        String sql = "id = ?";
        String wheres[] = {String.valueOf(id)};
        db.delete("diary",sql,wheres);
        db.close();

    }

    public Diary queryDiaryById(int id)
    {
        Diary diary = null;
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"id","title","data","text"};
        String selection = "id=?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query("diary",columns,selection,selectionArgs,null,null,null);
        if (cursor.moveToNext())
        {
            diary = new Diary();
            diary.setId(cursor.getInt(cursor.getColumnIndex("id")));
            diary.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            diary.setData(cursor.getString(cursor.getColumnIndex("data")));
            diary.setText(cursor.getString(cursor.getColumnIndex("text")));
        }
        return diary;
    }

    public List<Map<String, Object>> queryDiaryByTitle(String title1)
    {
        List<Map<String,Object>> items = new ArrayList<Map<String,Object>>();

        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {"id","title","data","text","image"};
        String selection = "title=?";
        String[] selectionArgs = {String.valueOf(title1)};
        String sql = "select * from diary where title = " + title1;
        Cursor cursor = db.rawQuery(sql,null);

        while(cursor.moveToNext())
        {
            Map<String,Object> item = new HashMap<String,Object>();

            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String data = cursor.getString(cursor.getColumnIndex("data"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            String bitmap = cursor.getString(cursor.getColumnIndex("bitmap"));

            item.put("id",id);
            item.put("title",title);
            item.put("data",data);
            item.put("text",text);
            item.put("bitmap",bitmap);
            items.add(item);


            System.out.println("title:" + title + ",data:" + data + ",text:" + text + ",bitmap" + bitmap);
        }
        cursor.close();
        db.close();
        return items;
    }

    public List<Map<String, Object>> queryAllDiary(){
        List<Map<String,Object>> items = new ArrayList<Map<String,Object>>();

        SQLiteDatabase db = getReadableDatabase(); //只读形式打开数据库
        String sql = "select * from diary";
        Cursor cursor = db.rawQuery(sql,null);
        //获取的信息有ID,TITLE,TEXT;
        while(cursor.moveToNext())
        {
            Map<String,Object> item = new HashMap<String,Object>();

            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String data = cursor.getString(cursor.getColumnIndex("data"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            String bitmap = cursor.getString(cursor.getColumnIndex("bitmap"));

            item.put("id",id);
            item.put("title",title);
            item.put("data",data);
            item.put("text",text);
            item.put("bitmap",bitmap);
            items.add(item);


            System.out.println("title:" + title + ",data:" + data + ",text:" + text + ",bitmap" + bitmap);
        }
        cursor.close();
        db.close();
        return items;
    }

}
