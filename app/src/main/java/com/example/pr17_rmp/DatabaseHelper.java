package com.example.pr17_rmp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "userstore.db";
    private static final int SCHEMA = 2;
    static final String TABLE_USERS = "users";
    static final String TABLE_COUNTRIES = "countries";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_COUNTRY = "country";

    public static final String COLUMN_COUNTRY_ID = "_id";
    public static final String COLUMN_COUNTRY_NAME = "country_name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_YEAR + " INTEGER, "
                + COLUMN_COUNTRY + " TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_COUNTRIES + " ("
                + COLUMN_COUNTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_COUNTRY_NAME + " TEXT);");


        db.execSQL("INSERT INTO " + TABLE_USERS + " (" + COLUMN_NAME + ", " + COLUMN_YEAR + ", " + COLUMN_COUNTRY + ") VALUES ('Том Смит', 1981, 'США');");
        db.execSQL("INSERT INTO " + TABLE_USERS + " (" + COLUMN_NAME + ", " + COLUMN_YEAR + ", " + COLUMN_COUNTRY + ") VALUES ('Анна Мюллер', 1995, 'Германия');");
        db.execSQL("INSERT INTO " + TABLE_USERS + " (" + COLUMN_NAME + ", " + COLUMN_YEAR + ", " + COLUMN_COUNTRY + ") VALUES ('Пьер Дюпон', 1978, 'Франция');");
        db.execSQL("INSERT INTO " + TABLE_USERS + " (" + COLUMN_NAME + ", " + COLUMN_YEAR + ", " + COLUMN_COUNTRY + ") VALUES ('Юки Танака', 1990, 'Япония');");
        db.execSQL("INSERT INTO " + TABLE_USERS + " (" + COLUMN_NAME + ", " + COLUMN_YEAR + ", " + COLUMN_COUNTRY + ") VALUES ('Мария Росси', 2000, 'Италия');");

        db.execSQL("INSERT INTO " + TABLE_COUNTRIES + " (" + COLUMN_COUNTRY_NAME + ") VALUES ('США');");
        db.execSQL("INSERT INTO " + TABLE_COUNTRIES + " (" + COLUMN_COUNTRY_NAME + ") VALUES ('Германия');");
        db.execSQL("INSERT INTO " + TABLE_COUNTRIES + " (" + COLUMN_COUNTRY_NAME + ") VALUES ('Франция');");
        db.execSQL("INSERT INTO " + TABLE_COUNTRIES + " (" + COLUMN_COUNTRY_NAME + ") VALUES ('Япония');");
        db.execSQL("INSERT INTO " + TABLE_COUNTRIES + " (" + COLUMN_COUNTRY_NAME + ") VALUES ('Италия');");
        db.execSQL("INSERT INTO " + TABLE_COUNTRIES + " (" + COLUMN_COUNTRY_NAME + ") VALUES ('Россия');");
        db.execSQL("INSERT INTO " + TABLE_COUNTRIES + " (" + COLUMN_COUNTRY_NAME + ") VALUES ('Китай');");
        db.execSQL("INSERT INTO " + TABLE_COUNTRIES + " (" + COLUMN_COUNTRY_NAME + ") VALUES ('Бразилия');");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRIES);
        onCreate(db);
    }
}
