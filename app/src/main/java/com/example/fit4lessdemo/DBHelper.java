package com.example.fit4lessdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

//our class extend the SQlite to and use the openHelper  //onCreate //onUpgrade are an abstract functions that must be implemented
public class DBHelper extends SQLiteOpenHelper {
    public static final String CUSTOMER_TABLE = "CUSTOMER_TABLE";
    public static final String COLUMN_CUSTOMER_NAME = "CUSTOMER_NAME";
    public static final String COLUMN_CUSTOMER_AGE = "CUSTOMER_AGE";
    public static final String COLUMN_ACTIVE_CUSTOMER = "ACTIVE_CUSTOMER";
    public static final String COLUMN_ID = "ID";
    //we just want to have the Customer_table to be referred to by this so that
    //we dont have to retype this everyTime and the IDE will suggest that for us
    //to easily do this just right click on any string and choose refactor > introduce constant

    public DBHelper(@Nullable Context context) {
        //factory is unnecessary here
        super(context, "Customer.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //the string value is a standard sql query request
        //there must be spaces around quotations
        String createTableStatement = "CREATE TABLE " + CUSTOMER_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CUSTOMER_NAME + ", " + COLUMN_CUSTOMER_AGE + " INT, " + COLUMN_ACTIVE_CUSTOMER + " BOOL)";

        db.execSQL(createTableStatement);
    }

    //this is called if the database version number changes. It prevents previous users apps from breaking when you change the database design
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //this method is for adding a new customer to the DB
    public boolean addOne(CustomerModel customerModel){
        //the getWritable method comes from the SQlite property
        //this will get our database that we have
        SQLiteDatabase db = this.getWritableDatabase();

        //Content values - similar to and associative array or Hashmap
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CUSTOMER_NAME, customerModel.getName());
        cv.put(COLUMN_CUSTOMER_AGE, customerModel.getAge());
        cv.put(COLUMN_ACTIVE_CUSTOMER, customerModel.isActive());
        //we will not need to get an Id because it will be generated in the DB as an incremented number

        //now we want to insert to the DB by passing the cv to the insert method
        //don't worry about nullColumnHack it is there if you have no columns in your cv but we have 4 columns here
        long insert = db.insert(CUSTOMER_TABLE, null, cv); //the return type from the insert will return long
        //this is just a success variables that indicates if the insertions is successful or not
        //if we get a positive number means it is successfully inserted
        if (insert == -1){
            return false;
        }
        else {
            return true;
        }

    }


    public boolean deleteOne(CustomerModel customerModel){
        //find customerModer in the database, if found then delete it and return true else return false

        SQLiteDatabase db = this.getWritableDatabase(); //we want to write to the database because we are deleting
        String queryString = "DELETE FROM " + CUSTOMER_TABLE + " WHERE " + COLUMN_ID + " = " + customerModel.getId();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            return true;
        }
        else {
            return false;
        }

    }


    public List<CustomerModel> getEveryone(){
        List<CustomerModel> returnList = new ArrayList<>();
        //get data from the database

        String queryString = "SELECT * FROM " + CUSTOMER_TABLE; //this is a standard sql query string

        SQLiteDatabase db = this.getReadableDatabase(); //we need to read from the database
        //getReadable would work here as well but it will lock the database so other processes may not access it

        //we can either choose db.execSql or db.rawQuery we will choose raw here
        //notice the raw query returns a cursor object
        //cursor is the result set //it is a complex arrays of data
        Cursor cursor = db.rawQuery(queryString, null);

        //we will check if the cursor is not empty so we go the the first item in it
        if(cursor.moveToFirst()){
            // loop through the cursor (result set) and create new customer objects. put them into the return list
            do {
                //we will get the data from the cursor
                //we know that the first column is the id so we will use the index 0 of the cursor
                int customerId = cursor.getInt(0);
                String customerName = cursor.getString(1);
                int customerAge = cursor.getInt(2);
                //the problem now is that in sqlite there is no such thing as boolean the value is either 0 or one
                //so we take that int and convert to boolean
                //the following is called ternary operator in case you are confused google it
                boolean cutomerActive = cursor.getInt(3) == 1 ? true: false;

                //now making the customer from the data that we got from the cursor
                CustomerModel newCustomer = new CustomerModel(customerId, customerName, customerAge, cutomerActive);

                //now adding the customer to the list
                returnList.add(newCustomer);

            }while (cursor.moveToNext()); //while we have next
        }
        else {
            // failure. do not add anything to the list
        }

        cursor.close(); //we have to close the cursor just like we close a file after reading or writing
        db.close(); //also the db

        return returnList;
    }

}