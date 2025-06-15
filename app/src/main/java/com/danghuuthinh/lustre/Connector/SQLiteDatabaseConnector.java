package com.danghuuthinh.lustre.Connector;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.danghuuthinh.lustre.models.Product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class SQLiteDatabaseConnector {
    private static final String DB_NAME = "MycartDatabase.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    private final Context context;
    private SQLiteDatabase database;

    public SQLiteDatabaseConnector(Context context) {
        this.context = context;
        openDatabase();
    }

    private void openDatabase() {
        File dbFile = context.getDatabasePath(DB_NAME);
        if (!dbFile.exists()) {
            copyDatabaseFromAssets();
        }
        database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
    }

    private void copyDatabaseFromAssets() {
        try {
            String outFileName = context.getDatabasePath(DB_NAME).getPath();
            File dbFile = new File(outFileName);


            if (dbFile.exists()) {
                dbFile.delete();
            }

            InputStream is = context.getAssets().open(DB_NAME);
            File f = new File(dbFile.getParent());
            if (!f.exists()) f.mkdirs();

            OutputStream os = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            os.flush();
            os.close();
            is.close();

            Log.d("DB", "Database copied successfully from assets");
        } catch (Exception e) {
            Log.e("DB", "Error copying database", e);
        }
    }


    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Product", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String size = cursor.getString(2);
            double price = cursor.getDouble(3);
            String imageLink = cursor.getString(4);
            list.add(new Product(id, name, size, price, imageLink));
        }
        cursor.close();
        return list;
    }

    public void deleteProductById(int productId) {
        if (database != null && database.isOpen()) {
            database.delete("Product", "Id = ?", new String[]{String.valueOf(productId)});
        } else {
            Log.e("SQLiteDatabaseConnector", "Database is not open!");
        }
    }
}
