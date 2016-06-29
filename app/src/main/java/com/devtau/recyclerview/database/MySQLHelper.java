package com.devtau.recyclerview.database;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.devtau.recyclerview.database.tables.DummyItemsTable;
import com.devtau.recyclerview.util.Logger;

public class MySQLHelper extends SQLiteOpenHelper {
    //helper is one no matter how much tables there are
    private static final String DB_NAME = "SQLiteDemoDB";
    private static final int DB_VERSION = 1;
    private static MySQLHelper instance;
    public static final String CREATE_TABLE = "CREATE TABLE %s ( %s);";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS %s";
    public static final String PRIMARY_KEY = BaseColumns._ID + " integer primary key autoincrement, ";
    private final Resources res;

    //singleton protects db from multi thread concurrent access
    private MySQLHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        res = context.getResources();
    }

    public static MySQLHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MySQLHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
        Logger.d("MySQLHelper: onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.d("Found new DB version. About to update to: " + String.valueOf(DB_VERSION));
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            if (oldVersion < 1) {
                //здесь перечисляем создаваемые таблицы
                db.execSQL(getCreateSql(DummyItemsTable.TABLE_NAME, DummyItemsTable.FIELDS));
            }
            if (oldVersion < 2) {
                //если после релиза мы захотим внести изменения в уже созданные таблицы, то делать это нужно так:
//                db.execSQL("ALTER TABLE " + DummyItemsTable.TABLE_NAME + " ADD COLUMN " + DummyItemsTable.SHIFT_ID + " NUMERIC;");
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private String getCreateSql(String tableName, String fields) {
        return String.format(CREATE_TABLE, tableName, fields);
    }

    //is accessible from each of the Sources because each of them hold link to singleton dbHelper
    public void dropAllTablesInDB(){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL(getDropSql(DummyItemsTable.TABLE_NAME));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        updateMyDatabase(db, 0, DB_VERSION);
    }
    private String getDropSql(String tableName) {
        return String.format(DROP_TABLE, tableName);
    }
}
