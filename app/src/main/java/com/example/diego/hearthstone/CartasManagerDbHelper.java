package com.example.diego.hearthstone;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.diego.hearthstone.CartasManagerContract;

/**
 * Created by blukstack on 31/03/2015.
 */
public class CartasManagerDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CartasManager.db";
    private Context contexto;

    private static final String SQL_CREATE_CARTAS =
            "CREATE TABLE " + CartasManagerContract.Carta.TABLE_NAME + " (" +
                    CartasManagerContract.Carta._ID + " INTEGER PRIMARY KEY," +
                    CartasManagerContract.Carta.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                    CartasManagerContract.Carta.COLUMN_NAME_CLASS + " TEXT," +
                    CartasManagerContract.Carta.COLUMN_NAME_OBTENIDA + " BOOLEAN NOT NULL," +
                    CartasManagerContract.Carta.COLUMN_NAME_CANTIDAD + " INTEGER NOT NULL)";


    private static final String SQL_CREATE_MAZOS =
            "CREATE TABLE " + CartasManagerContract.Mazo.TABLE_NAME + " (" +
                    CartasManagerContract.Mazo._ID + " INTEGER PRIMARY KEY," +
                    CartasManagerContract.Mazo.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                    CartasManagerContract.Mazo.COLUMN_NAME_PREDEFINIDO + " BOOLEAN NOT NULL," +
                    CartasManagerContract.Mazo.COLUMN_NAME_CLASS + " TEXT NOT NULL)";

    private static final String SQL_CREATE_CARTAS_MAZOS =
            "CREATE TABLE " + CartasManagerContract.Carta_Mazo.TABLE_NAME + " (" +
                    CartasManagerContract.Carta_Mazo._ID + " INTEGER PRIMARY KEY," +
                    CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDCARTA + " INTEGER NOT NULL," +
                    CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDMAZO + " INTEGER NOT NULL," +
                    "FOREIGN KEY(" +
                    CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDCARTA +
                    ") REFERENCES " + CartasManagerContract.Carta.TABLE_NAME +
                    "(" + CartasManagerContract.Carta._ID + "), " +
                    "FOREIGN KEY(" +
                    CartasManagerContract.Carta_Mazo.COLUMN_NAME_IDMAZO +
                    ") REFERENCES " + CartasManagerContract.Mazo.TABLE_NAME +
                    "(" + CartasManagerContract.Mazo._ID + ") )";
    private static final String SQL_DELETE_CARTAS_MAZOS =
            "DROP TABLE IF EXISTS " + CartasManagerContract.Carta_Mazo.TABLE_NAME;
    private static final String SQL_DELETE_CARTAS =
            "DROP TABLE IF EXISTS " +
                    CartasManagerContract.Carta.TABLE_NAME;
    private static final String SQL_DELETE_MAZOS =
            "DROP TABLE IF EXISTS " +
                    CartasManagerContract.Mazo.TABLE_NAME;


    public CartasManagerDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        contexto=context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CARTAS);
        db.execSQL(SQL_CREATE_MAZOS);
        db.execSQL(SQL_CREATE_CARTAS_MAZOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CARTAS_MAZOS);
        db.execSQL(SQL_DELETE_CARTAS);
        db.execSQL(SQL_DELETE_MAZOS);
        onCreate(db);
    }

    public void borrar(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_CARTAS_MAZOS);
        db.execSQL(SQL_DELETE_CARTAS);
        db.execSQL(SQL_DELETE_MAZOS);
        onCreate(db);
    }
}
