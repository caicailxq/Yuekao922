package com.bawei.yuekao922.shujuku;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/9/22.
 */

public class SQLite extends SQLiteOpenHelper{
    public SQLite(Context context) {
        super(context, "person.db", null, 1);//创建数据库
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user(_id Integer primary key autoincrement,title varchar(20),imageurl varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
