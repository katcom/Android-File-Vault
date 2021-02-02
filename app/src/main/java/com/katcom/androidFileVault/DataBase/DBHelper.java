package com.katcom.androidFileVault.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import com.katcom.androidFileVault.DataBase.VaultDbSchema.VaultTable;
public class DBHelper extends SQLiteOpenHelper {
    private static final int VERSION =1;
    private static final String DATABASE_NAME = "fileBase.db";
    private static final String SQL_CREATE_VAULT_TABLE=
            "CREATE TABLE " + VaultTable.TABLE_NAME + " (" +
                    VaultTable.COLUMN_VAULT_NAME + " Text not null PRIMARY KEY," +
                    VaultTable.PASSWORD + " Text not null )";


    public DBHelper(@Nullable Context context) {
        super(context,DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_VAULT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
