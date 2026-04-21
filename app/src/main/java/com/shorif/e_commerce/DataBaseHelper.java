package com.shorif.e_commerce;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "PRODUCT_DB_2";
    private static final int DATABASE_VERSION = 1;

    // Table Info
    private static final String TABLE_NAME = "product_table";
    private static final String COLUMN_ID = "id";//0
    private static final String COLUMN_TITLE = "title";//1
    private static final String COLUMN_PRICE = "price";//2
    private static final String COLUMN_PIC = "pic";//3


    private static final String COLUMN_quantity= "quantity";//4

    private static final String COLUMN_product_id = "product_id";//5

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_PRICE + " TEXT, "
                + COLUMN_PIC + " TEXT,"
                + COLUMN_quantity + " TEXT,"
                + COLUMN_product_id + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public String storeData(String title, String price, String pic,String product_id) {

        if (isProductExists(product_id)) {
            return "Already added";
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_PIC, pic);
        values.put(COLUMN_quantity, "1");
        values.put(COLUMN_product_id,product_id);

        long result = db.insert(TABLE_NAME, null, values);

        if (result != -1) {
            return "Successfully Added";
        } else {
            return "Error to save";
        }
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean isProductExists(String product_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_product_id + "=?",
                new String[]{product_id});

        boolean uniqueOrNot = cursor.getCount() > 0;
        cursor.close();
        return uniqueOrNot;
    }

    public void plusMethod(String product_id) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_product_id + "=?",
                new String[]{product_id});

        if (cursor.moveToFirst()) {

            int currentQty = Integer.parseInt(cursor.getString(4));
            int newQty = currentQty + 1;

            ContentValues values = new ContentValues();
            values.put(COLUMN_quantity, newQty);

            db.update(TABLE_NAME,
                    values,
                    COLUMN_product_id + "=?",
                    new String[]{product_id});
        }

        cursor.close();
    }


    public void minusMethod(String product_id) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_product_id + "=?",
                new String[]{product_id});

        if (cursor.moveToFirst()) {

            int currentQty = Integer.parseInt(cursor.getString(4));

            if (currentQty > 1) {
                int newQty = currentQty - 1;

                ContentValues values = new ContentValues();
                values.put(COLUMN_quantity, newQty);

                db.update(TABLE_NAME,
                        values,
                        COLUMN_product_id + "=?",
                        new String[]{product_id});

            }
        }
        cursor.close();
    }

    public String deleteById(String product_id){

        SQLiteDatabase db = this.getWritableDatabase();

        int result=db.delete(
                TABLE_NAME,
                COLUMN_product_id + "=?",
                new String[]{product_id}
        );

        if(result > 0){
            return "Successfully deleted";
        }else{
            return "Error to delete";
        }
    }

}
