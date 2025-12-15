package com.example.thuchanhtuan15;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LoyalCustomer.db";
    private static final int DATABASE_VERSION = 1;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    // Customers table
    private static final String TABLE_CUSTOMERS = "customers";
    private static final String COL_CUST_ID = "id";
    private static final String COL_PHONE = "phone";
    private static final String COL_POINTS = "points";
    private static final String COL_CREATED_DATE = "created_date";
    private static final String COL_LAST_UPDATED = "last_updated";

    // Transactions table
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String COL_TRANS_ID = "id";
    private static final String COL_TRANS_PHONE = "phone";
    private static final String COL_TRANS_POINTS = "points";
    private static final String COL_TRANS_NOTE = "note";
    private static final String COL_TRANS_TYPE = "type";
    private static final String COL_TRANS_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "(" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_USERNAME + " TEXT UNIQUE," +
                COL_PASSWORD + " TEXT)";

        String createCustomersTable = "CREATE TABLE " + TABLE_CUSTOMERS + "(" +
                COL_CUST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_PHONE + " TEXT UNIQUE," +
                COL_POINTS + " INTEGER," +
                COL_CREATED_DATE + " TEXT," +
                COL_LAST_UPDATED + " TEXT)";

        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + "(" +
                COL_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_TRANS_PHONE + " TEXT," +
                COL_TRANS_POINTS + " INTEGER," +
                COL_TRANS_NOTE + " TEXT," +
                COL_TRANS_TYPE + " TEXT," +
                COL_TRANS_DATE + " TEXT)";

        db.execSQL(createUsersTable);
        db.execSQL(createCustomersTable);
        db.execSQL(createTransactionsTable);

        // Add default user
        ContentValues cv = new ContentValues();
        cv.put(COL_USERNAME, "cuspoint@gmail.com");
        cv.put(COL_PASSWORD, "123456");
        db.insert(TABLE_USERS, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public long addCustomer(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PHONE, customer.getPhone());
        cv.put(COL_POINTS, customer.getPoints());
        cv.put(COL_CREATED_DATE, getCurrentDateTime());
        cv.put(COL_LAST_UPDATED, getCurrentDateTime());
        return db.insert(TABLE_CUSTOMERS, null, cv);
    }

    public int updateCustomer(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_POINTS, customer.getPoints());
        cv.put(COL_LAST_UPDATED, getCurrentDateTime());
        return db.update(TABLE_CUSTOMERS, cv, COL_PHONE + "=?", new String[]{customer.getPhone()});
    }

    public Customer getCustomerByPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CUSTOMERS, null, COL_PHONE + "=?",
                new String[]{phone}, null, null, null);

        Customer customer = null;
        if (cursor.moveToFirst()) {
            customer = new Customer(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
        }
        cursor.close();
        return customer;
    }

    public ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CUSTOMERS +
                " ORDER BY " + COL_LAST_UPDATED + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Customer customer = new Customer(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                customers.add(customer);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return customers;
    }

    public long addTransaction(String phone, int points, String note, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TRANS_PHONE, phone);
        cv.put(COL_TRANS_POINTS, points);
        cv.put(COL_TRANS_NOTE, note);
        cv.put(COL_TRANS_TYPE, type);
        cv.put(COL_TRANS_DATE, getCurrentDateTime());
        return db.insert(TABLE_TRANSACTIONS, null, cv);
    }

    public ArrayList<Transaction> getTransactionsByPhone(String phone) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRANSACTIONS, null, COL_TRANS_PHONE + "=?",
                new String[]{phone}, null, null, COL_TRANS_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)
                );
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transactions;
    }

    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PASSWORD, newPassword);
        int result = db.update(TABLE_USERS, cv, COL_USERNAME + "=?", new String[]{username});
        return result > 0;
    }

    public boolean importCustomersFromXML(ArrayList<Customer> customers) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Customer customer : customers) {
                ContentValues cv = new ContentValues();
                cv.put(COL_PHONE, customer.getPhone());
                cv.put(COL_POINTS, customer.getPoints());
                cv.put(COL_CREATED_DATE, customer.getCreatedDate());
                cv.put(COL_LAST_UPDATED, customer.getLastUpdated());

                db.insertWithOnConflict(TABLE_CUSTOMERS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }
}