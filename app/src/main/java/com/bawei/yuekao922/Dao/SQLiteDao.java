package com.bawei.yuekao922.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bawei.yuekao922.Bean.Qbean;
import com.bawei.yuekao922.shujuku.SQLite;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/22.
 */

public class SQLiteDao {
    private final SQLiteDatabase db;

    public SQLiteDao(Context context){
        SQLite sqLite = new SQLite(context);
        db = sqLite.getWritableDatabase();
    }
    public boolean addData(String title,String imageurl){
        ContentValues values = new ContentValues();
        values.put("title",title);
        values.put("imageurl",imageurl);
        long result = db.insert("user", null, values);
        if (result!=-1){
            return true;
        }else{
            return false;
        }
    }
    public ArrayList<Qbean.DataBean> findData(){
        Cursor cursor = db.query(false, "user", null, null, null, null, null, null, null);
        ArrayList<Qbean.DataBean> datas = new ArrayList<>();
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String imageurl = cursor.getString(cursor.getColumnIndex("imageurl"));
            Qbean.DataBean dataBean = new Qbean.DataBean();
            dataBean.setNews_title(title);
            dataBean.setPic_url(imageurl);
            datas.add(dataBean);
        }
        return datas;
    }
}
