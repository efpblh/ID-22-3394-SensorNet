package com.example.testsensornet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    protected String ip_adress;
   //protected String port;
    protected String database;
    protected String name;
    protected String pass;

    protected Connection connect;

    protected SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Получаем поля изменяемых данных
        EditText ip_field = findViewById(R.id.ip_field);
        ip_field.setText(preferences.getString("ip_string", null));
        ip_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editor.putString("ip_string", charSequence.toString().trim());
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        EditText database_field = findViewById(R.id.database_field);
        database_field.setText(preferences.getString("database_string", null));
        database_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editor.putString("database_string", charSequence.toString().trim());
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        EditText name_field = findViewById(R.id.name_field);
        name_field.setText(preferences.getString("name_string", null));
        name_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editor.putString("name_string", charSequence.toString().trim());
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        EditText pass_field = findViewById(R.id.pass_field);

        Button enter = findViewById(R.id.enter_button);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ip_adress = ip_field.getText().toString().trim();
                //port = port_field.getText().toString().trim();
                database = database_field.getText().toString().trim();
                name = name_field.getText().toString().trim();
                pass = pass_field.getText().toString().trim();

                try {
                    ConnectionHelper connectionHelper = new ConnectionHelper(ip_adress,
                            name, pass, database);
                    connect = connectionHelper.connectionclass();
                    if (connect != null) {
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        //Toast.makeText(getApplicationContext(), "Connection successful", Toast.LENGTH_SHORT).show();
                        intent.putExtra("name", name);
                        intent.putExtra("pass", pass);
                        intent.putExtra("ip_adress", ip_adress);
                        //intent.putExtra("port", port);
                        intent.putExtra("database", database);
                        connect.close();
                        startActivity(intent);
                        //finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Check connection properties",
                                Toast.LENGTH_LONG).show();
                    }
                }
                catch(Exception ex) {
                    Log.e("Error: ", ex.getMessage());
                }
            }
        });

        //EditText port_field = findViewById(R.id.port_field);
    }
}
