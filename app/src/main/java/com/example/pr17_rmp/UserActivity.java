package com.example.pr17_rmp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    EditText nameBox;
    EditText yearBox;
    Spinner countryBox;
    Button delButton;
    Button saveButton;

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    long userId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        nameBox = findViewById(R.id.name);
        yearBox = findViewById(R.id.year);
        countryBox = findViewById(R.id.country);
        delButton = findViewById(R.id.deleteButton);
        saveButton = findViewById(R.id.saveButton);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        loadCountrySpinner();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("id");
        }

        if (userId > 0) {
            // Загружаем данные
            userCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_USERS + " WHERE " +
                    DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
            if (userCursor.moveToFirst()) {
                nameBox.setText(userCursor.getString(1));  // name
                yearBox.setText(String.valueOf(userCursor.getInt(2)));  // year

                String userCountry = userCursor.getString(3);  // country
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) countryBox.getAdapter();
                int position = adapter.getPosition(userCountry);
                if (position >= 0) {
                    countryBox.setSelection(position);
                }
            } else {
                Toast.makeText(this, "Запись не найдена", Toast.LENGTH_SHORT).show();
                finish();
            }
            userCursor.close();
        } else {
            delButton.setVisibility(View.GONE);
        }
    }

    private void loadCountrySpinner() {
        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.COLUMN_COUNTRY_NAME +
                " FROM " + DatabaseHelper.TABLE_COUNTRIES +
                " ORDER BY " + DatabaseHelper.COLUMN_COUNTRY_NAME, null);

        ArrayList<String> countries = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                countries.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryBox.setAdapter(adapter);
    }
    public void save(View view) {
        String name = nameBox.getText().toString().trim();
        String yearStr = yearBox.getText().toString().trim();

        if (name.isEmpty() || yearStr.isEmpty()) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        int year = Integer.parseInt(yearStr);
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        if (year < 1900 || year > currentYear) {
            Toast.makeText(this, "Введите год от 1900 до " + currentYear, Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, name);
        cv.put(DatabaseHelper.COLUMN_YEAR, year);
        cv.put(DatabaseHelper.COLUMN_COUNTRY, countryBox.getSelectedItem().toString());

        if (userId > 0) {
            db.update(DatabaseHelper.TABLE_USERS, cv, DatabaseHelper.COLUMN_ID + "=" + userId, null);
        } else {
            db.insert(DatabaseHelper.TABLE_USERS, null, cv);
        }
        goHome();
    }

    public void delete(View view) {
        db.delete(DatabaseHelper.TABLE_USERS, "_id = ?", new String[]{String.valueOf(userId)});
        goHome();
    }

    private void goHome() {
        db.close();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}