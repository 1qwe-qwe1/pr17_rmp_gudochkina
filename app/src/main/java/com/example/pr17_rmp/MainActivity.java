package com.example.pr17_rmp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    EditText nameInput, yearInput;
    Spinner countrySpinner, countryFilterSpinner;
    Button addButton, sortAscButton, sortDescButton, sortDefaultButton;
    ListView userList;

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    private String currentSort = " ORDER BY " + DatabaseHelper.COLUMN_YEAR + " ASC";
    private String currentFilter = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nameInput = findViewById(R.id.nameInput);
        yearInput = findViewById(R.id.yearInput);
        countrySpinner = findViewById(R.id.countrySpinner);
        countryFilterSpinner = findViewById(R.id.countryFilterSpinner);
        addButton = findViewById(R.id.addButton);
        sortAscButton = findViewById(R.id.sortAscButton);
        sortDescButton = findViewById(R.id.sortDescButton);
        sortDefaultButton = findViewById(R.id.sortDefaultButton);
        userList = findViewById(R.id.userList);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        loadCountrySpinners();
        loadUsers();

        // Добавление
        addButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String yearStr = yearInput.getText().toString().trim();

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
            cv.put(DatabaseHelper.COLUMN_COUNTRY, countrySpinner.getSelectedItem().toString());

            long result = db.insert(DatabaseHelper.TABLE_USERS, null, cv);
            if (result > 0) {
                Toast.makeText(this, "Добавлено", Toast.LENGTH_SHORT).show();
                nameInput.setText("");
                yearInput.setText("");
                loadUsers();
            } else {
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
            }
        });

        // Сортировка
        sortAscButton.setOnClickListener(v -> {
            currentSort = " ORDER BY " + DatabaseHelper.COLUMN_YEAR + " ASC";
            loadUsers();
        });

        sortDescButton.setOnClickListener(v -> {
            currentSort = " ORDER BY " + DatabaseHelper.COLUMN_YEAR + " DESC";
            loadUsers();
        });

        sortDefaultButton.setOnClickListener(v -> {
            currentSort = " ORDER BY " + DatabaseHelper.COLUMN_ID + " ASC";
            loadUsers();
        });

        // Фильтр по стране
        countryFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                currentFilter = selected.equals("Все страны") ? "" : " WHERE " + DatabaseHelper.COLUMN_COUNTRY + " = '" + selected + "'";
                loadUsers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Переход к редактированию
        userList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        });
    }

    private void loadCountrySpinners() {
        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.COLUMN_COUNTRY_NAME + " FROM " + DatabaseHelper.TABLE_COUNTRIES + " ORDER BY " + DatabaseHelper.COLUMN_COUNTRY_NAME, null);

        ArrayList<String> countries = new ArrayList<>();
        countries.add("Все страны");
        if (cursor.moveToFirst()) {
            do {
                countries.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        countrySpinner.setAdapter(adapter);
        countryFilterSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries) {{
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }});
    }

    private void loadUsers() {
        userCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_USERS + currentFilter + currentSort, null);

        String[] headers = new String[]{DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_YEAR};
        int[] to = new int[]{android.R.id.text1, android.R.id.text2};

        userAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, userCursor, headers, to, 0);
        userList.setAdapter(userAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userCursor != null && !userCursor.isClosed()) {
            userCursor.close();
        }
        db.close();
    }

}