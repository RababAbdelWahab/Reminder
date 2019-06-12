package com.example.rabab.androidlab2;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class RemindersDbAdapter extends SQLiteOpenHelper {

    //these are the column names
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_IMPORTANT = "important";

    //these are the corresponding indices
    public static final int INDEX_ID = 0;
    public static final int INDEX_CONTENT = INDEX_ID + 1;
    public static final int INDEX_IMPORTANT = INDEX_ID + 2;
    private final Context mCtx = null;

    //used for logging
    private static final String TAG = "RemindersDbAdapter";

    // Database Version and name
    private static final String DATABASE_NAME = "dba_remdrs";
    public static final String TABLE_NAME = "tbl_remdrs";
    private static final int DATABASE_VERSION = 2;


    //SQL statement used to create the database
    public static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + "("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_CONTENT + " TEXT,"
                    + COL_IMPORTANT + " INTEGER"
                    + ")";



    public RemindersDbAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //TODO implement the function createReminder() which take the name as the content of the reminder and boolean important...note that the id will be created for you automatically
        public long createReminder(String name, int important) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
         values.put(COL_CONTENT, name);
        values.put(COL_IMPORTANT, important);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    //TODO implement the function fetchReminderById() to get a certain reminder given its id
        public Reminder fetchReminderById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
         Cursor cursor = db.query(TABLE_NAME,
                new String[]{COL_ID, COL_CONTENT, COL_IMPORTANT },
                COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();


        Reminder note = new Reminder(
                cursor.getInt(cursor.getColumnIndex(COL_ID)),
                cursor.getString(cursor.getColumnIndex(COL_CONTENT)),
                cursor.getInt(cursor.getColumnIndex(COL_IMPORTANT)));

        cursor.close();

        return note;
    }


    //TODO implement the function fetchAllReminders() which get all reminders
         public List<Reminder> fetchAllReminders() {
        List<Reminder> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Reminder note = new Reminder(cursor.getInt(cursor.getColumnIndex( COL_ID)),
                        cursor.getString(cursor.getColumnIndex( COL_CONTENT)),
                cursor.getInt(cursor.getColumnIndex( COL_IMPORTANT)));

                note.setId(cursor.getInt(cursor.getColumnIndex( COL_ID)));
                note.setContent(cursor.getString(cursor.getColumnIndex( COL_CONTENT)));
                note.setImportant(cursor.getInt(cursor.getColumnIndex( COL_IMPORTANT)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        db.close();
        return notes;
    }


    //TODO implement the function updateReminder() to update a certain reminder
        public int updateReminder(Reminder reminder) {
        SQLiteDatabase mDb = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put( COL_CONTENT, reminder.getContent());
        values.put( COL_IMPORTANT, reminder.getImportant());

        // updating row
        return mDb.update( TABLE_NAME, values,  COL_ID + " = ?",
                new String[]{String.valueOf(reminder.getId())});
    }

    //TODO implement the function deleteReminderById() to delete a certain reminder given its id
    public void deleteNote(Reminder note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete( TABLE_NAME, COL_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }

    //TODO implement the function deleteAllReminders() to delete all reminders
    public void deleteAllReminders() {
        List<Reminder> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Reminder note = new Reminder(cursor.getInt(cursor.getColumnIndex( COL_ID)),
                        cursor.getString(cursor.getColumnIndex( COL_CONTENT)),
                        cursor.getInt(cursor.getColumnIndex( COL_IMPORTANT)));

                note.setId(cursor.getInt(cursor.getColumnIndex( COL_ID)));
                note.setContent(cursor.getString(cursor.getColumnIndex( COL_CONTENT)));
                note.setImportant(cursor.getInt(cursor.getColumnIndex( COL_IMPORTANT)));

                notes.add(note);
            } while (cursor.moveToNext());
        }
//        deleteNote(Reminder note);
        db.close();
     }

    //table
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(TAG, DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
