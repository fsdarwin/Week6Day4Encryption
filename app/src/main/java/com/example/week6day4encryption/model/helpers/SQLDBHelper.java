package com.example.week6day4encryption.model.helpers;
//DEPENDENCY IMPORTS||||||||||||||||||||||||||-----------
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
//||||||||||||||||||||||||||||||||||||||||||||------------

//DATABASE FIELD NAME IMPORTS||||||||||||||||----------------------------------------------
import com.example.week6day4encryption.model.pojos.User;

import java.util.ArrayList;
import static com.example.week6day4encryption.model.constants.DbConstants.DATABASE_NAME;
import static com.example.week6day4encryption.model.constants.DbConstants.DATABASE_VERSION;
import static com.example.week6day4encryption.model.constants.DbConstants.FIELD_NAME;
import static com.example.week6day4encryption.model.constants.DbConstants.FIELD_PASSWORD;
import static com.example.week6day4encryption.model.constants.DbConstants.TABLE_NAME;
//||||||||||||||||||||||||||||||||||||||||--------------------------------------------------

public class SQLDBHelper extends SQLiteOpenHelper {
    //Constuctor
    public SQLDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    int count;
    public void setCount(int count){
        this.count = count;
    }
    public int getCount(){
        return count;
    }

    @Override//CREATE table with necessary fields
    public void onCreate(SQLiteDatabase db) {
        String createStmt = "CREATE TABLE " + TABLE_NAME + " ("
                + FIELD_NAME + " TEXT PRIMARY KEY, "
                + FIELD_PASSWORD + " TEXT)";
        db.execSQL(createStmt);//Creates the database


    }

    @Override//Update version number
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public User selectUser(String userName) {
        User returnStudent = null;//Set return value to null
        if (userName != null && !userName.isEmpty()) {//IF the value passed in is not null
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();//OPEN database for read only
            //SELECT
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + FIELD_NAME + " = \"" + userName + "\"";
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            setCount(cursor.getCount());
            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(FIELD_NAME));
                String pass = cursor.getString(cursor.getColumnIndex(FIELD_PASSWORD));
                returnStudent = new User(name, pass);
            }
            cursor.close();
        }
        return returnStudent;
    }
    public ArrayList<User> selectAllUsers() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            ArrayList<User> arrayList = new ArrayList<>();
            do {
                String name = cursor.getString(cursor.getColumnIndex(FIELD_NAME));
                String pass = cursor.getString(cursor.getColumnIndex(FIELD_PASSWORD));
                arrayList.add(new User(name, pass));
            } while (cursor.moveToNext());
            return arrayList;
        } else {
            return null;
        }
    }
    public void insertUser(User user) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        if (user != null) {
            String name = user.getUserName();
            String pass = user.getPassword();

            contentValues.put(FIELD_NAME, name);
            contentValues.put(FIELD_PASSWORD, pass);

            database.insert(TABLE_NAME, null, contentValues);
        }
    }
    public int updateUser(User user) {
        if (user != null) {
            String whereClause = FIELD_NAME + " = \"" + user.getUserName() + "\"";
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FIELD_NAME, user.getUserName());
            contentValues.put(FIELD_PASSWORD, user.getPassword());
            return sqLiteDatabase.update(TABLE_NAME, contentValues, whereClause, null);
        } else {
            return 0;
        }
    }
    public int deleteUser(User user) {
        String whereClause = FIELD_NAME + " = \"" + user.getUserName() + "\"";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME, whereClause, null);
    }
}
