package com.example.testsensornet;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    // Массив значений фильтра для модели датчика
    protected String[] model_array;
    protected boolean[] model_choice;
    // Массив значений фильтра для типа значений датчика
    protected String[] valtype_array;
    protected boolean[] valtype_choice;
    // Массив значений фильтра для позиции датчика
    protected String[] position_array;
    protected boolean[] position_choice;

    protected Connection connect;
    // Название таблицы с данными
    protected String db_table = "sensordatatable";

    // Данные для фильтра по времени
    // Сами фильтра
    protected Calendar left_border = Calendar.getInstance();
    protected Calendar right_border = Calendar.getInstance();
    // Граничные значения
    protected Calendar min_value = Calendar.getInstance();
    protected Calendar max_value = Calendar.getInstance();
    // Вспомогательная переменная, позволяющая правильно устанавливать время соответствующей переменной
    protected boolean setting_order;
    protected int cnt;

    protected DatePickerDialog right_dpd;
    protected DatePickerDialog left_dpd;
    protected TimePickerDialog left_tpd;
    protected TimePickerDialog right_tpd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Получаем параметры подключения от предыдущей активности
        Bundle arg = getIntent().getExtras();

        try {
            ConnectionHelper connectionHelper = new ConnectionHelper(arg.getString("ip_adress"),
                    arg.getString("name"), arg.getString("pass"), arg.getString("database"));
            // По полученным параметрам устанавливаем соединение (по-хорошему остлеживать потерю соединения и выкидывать на начальный экран пользователя)
            connect = connectionHelper.connectionclass();
            if (connect != null) {
                Toast.makeText(getApplicationContext(), "Connection successful!", Toast.LENGTH_SHORT).show();
                // Получение значений для фильтров
                model_array = getFilterSet("SensorName");
                model_choice = new boolean[model_array.length];
                valtype_array = getFilterSet("DataType");
                valtype_choice = new boolean[valtype_array.length];
                position_array = getFilterSet("Position");
                position_choice = new boolean[position_array.length];
                // СМЕРТНЫЙ ГРЕХ - ХАРДКОД, - ТУТА ПО-ХОРОШЕМУ ЭТИ КРАЙНИЕ ЗНАЧЕНИЯ ВРЕМЕНИ ИЗ БД ПОЛУЧИТЬ
                max_value.set(2022, 3, 17, 23, 50, 10);
                min_value.set(2022, 3, 15, 0, 0, 10);
                setDefault();
            }
            else Toast.makeText(getApplicationContext(), "Connection FAILURE", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex) {
            // ДОПИСАТЬ ОБРАБОТЧИК ИСКЛЮЧЕНИЙ
        }

        // ФИЛЬТР ПО МОДЕЛИ ДАТЧИКА
        Button model_button = findViewById(R.id.model_filter);
        model_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Какой-то билдер, код в инете нашел, важный объект для такого окна
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                // Массив хранит предыдущее состояние, которое остается в случае "отмены"
                boolean[] model_choice_approved = model_choice.clone();
                // Задаем заглавие
                builder.setTitle("Выберите модель датчика");
                // set multichoice (тоже из интернета взяд, ниче не менял, по сути сами варианты выбора)
                builder.setMultiChoiceItems(model_array, model_choice, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // в массиве "выборов" "выбираем" то, что "выбрал" пользователь
                        model_choice[which] = isChecked;
                    }
                });
                // Кнопка подтверждения и отклик на нее
                builder.setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                // Кнопка отмены и отклик на нее
                builder.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.arraycopy(model_choice_approved, 0, model_choice, 0, model_choice_approved.length);
                    }
                });
                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface (хз)
                dialog.show();
            }
        });

        // ФИЛЬТР ПО ТИПУ ЗНАЧЕНИЯ ДАТЧИКА
        Button value_button = findViewById(R.id.value_filter);
        value_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Какой-то билдер, код в инете нашел, важный объект для такого окна
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                // Массив хранит предыдущее состояние, которое остается в случае "отмены"
                boolean[] valtype_choice_approved = valtype_choice.clone();
                // Задаем заглавие
                builder.setTitle("Выберите тип данных датчика");
                // set multichoice (тоже из интернета взяд, ниче не менял, по сути сами варианты выбора)
                builder.setMultiChoiceItems(valtype_array, valtype_choice, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // в массиве "выборов" "выбираем" то, что "выбрал" пользователь
                        valtype_choice[which] = isChecked;
                    }
                });
                // Кнопка подтверждения и отклик на нее
                builder.setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                // Кнопка отмены и отклик на нее
                builder.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.arraycopy(valtype_choice_approved, 0, valtype_choice, 0, valtype_choice_approved.length);
                    }
                });
                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface (хз)
                dialog.show();
            }
        });

        // ФИЛЬТР ПО ПОЛОЖЕНИЮ ДАТЧИКА
        Button position_button = findViewById(R.id.position_filter);
        position_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Какой-то билдер, код в инете нашел, важный объект для такого окна
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                // Массив хранит предыдущее состояние, которое остается в случае "отмены"
                boolean[] position_choice_approved = position_choice.clone();
                // Задаем заглавие
                builder.setTitle("Выберите позицию датчика");
                // set multichoice (тоже из интернета взяд, ниче не менял, по сути сами варианты выбора)
                builder.setMultiChoiceItems(position_array, position_choice, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // в массиве "выборов" "выбираем" то, что "выбрал" пользователь
                        position_choice[which] = isChecked;
                    }
                });
                // Кнопка подтверждения и отклик на нее
                builder.setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                // Кнопка отмены и отклик на нее
                builder.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.arraycopy(position_choice_approved, 0, position_choice, 0, position_choice_approved.length);
                    }
                });
                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface (хз)
                dialog.show();
            }
        });

        // ФИЛЬТР ПО ДАТЕ И ВРЕМЕНИ
        Button date_button = findViewById(R.id.date_filter);
        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cnt = 0;
                setting_order = true;
                setTime(view, "Выберите конечное время поиска");
                setDate(view, "Выберите конечную дату поиска");
                setting_order = false;
                setTime(view, "Выберите начальное время поиска");
                setDate(view, "Выберите начальную дату поиска");
            }
        });

        // Кнопка поиска
        Button search_button = findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Integer> id_list = new ArrayList<Integer>();
                ArrayList<String> model_list = new ArrayList<String>();
                ArrayList<String> valtype_list = new ArrayList<String>();
                ArrayList<String> value_list = new ArrayList<String>();
                ArrayList<String> date_list = new ArrayList<String>();
                ArrayList<String> position_list = new ArrayList<String>();
                // Получаем данные из таблицы и записываем в соответствующие ArrayList
                try {
                    Statement st = connect.createStatement();
                    String query = getQueryString();
                    //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Подождите, загрузка данных",
                            Toast.LENGTH_LONG).show();
                    ResultSet result = st.executeQuery(query);

                    while (result.next()) {
                        id_list.add(result.getInt(1));
                        model_list.add(result.getString(2));
                        valtype_list.add(result.getString(3));
                        value_list.add(result.getString(4));
                        date_list.add(result.getString(5));
                        position_list.add(result.getString(6));
                    }
                }
                catch (SQLException ex) {
                    Log.e("SQLEXCEPTION: ", ex.getMessage());
                    Intent intent = new Intent(MainActivity.this, StartActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                catch (Exception ex) {
                    // ИСКЛЮЧЕНИЯ !!!!!!!
                    Log.e("Error: ", ex.getMessage());
                }

                // Таблица
                TableLayout main_table = findViewById(R.id.main_table);
                main_table.removeAllViews();
                main_table.setStretchAllColumns(true);
                main_table.setBackground(getDrawable(R.drawable.cellborder_bold));
                String[] header_names = {"ID", "SensorName", "DataType", "Value", "Date", "Position"};

                // Шапка таблицы (МБ ЧЕ ТО С ЗЫСАМИ НЕ РАБОТАЕТ)
                TableRow row = new TableRow(MainActivity.this);
                for (int i = 0; i < header_names.length; i++) {
                    TextView text = stdText(header_names[i]);
                    text.setBackground(getDrawable(R.drawable.cellborder_bold));
                    row.addView(text, i);
                }
                main_table.addView(row, 0);

                // Рисуем основное тело таблицы (ТОЖЕ ОЧЕНЬ ВНИМАТЕЛЬНО С ЗЫСАМИ)
                for (int i = 0; i < id_list.size(); i++) {
                    row = new TableRow(getApplicationContext());
                    row.addView(stdText(Integer.toString(id_list.get(i))), 0);
                    row.addView(stdText(model_list.get(i)), 1);
                    row.addView(stdText(valtype_list.get(i)), 2);
                    row.addView(stdText(value_list.get(i)), 3);
                    row.addView(stdText(date_list.get(i)), 4);
                    row.addView(stdText(position_list.get(i)), 5);
                    main_table.addView(row, i+1);
                }
                TextView num_of_elem = findViewById(R.id.elem_num_text);
                num_of_elem.setText("Число элементов: " + Integer.toString(id_list.size()) + ".");
            }
        });

    }

    // Метод для получения значений фильтров из самой базы данных
    protected String[] getFilterSet(String filterName) {
        ArrayList<String> filters_list = new ArrayList<String>();
        try {
            Statement st = connect.createStatement();
            String query = "SELECT DISTINCT " + filterName + " FROM " + db_table;
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                filters_list.add(rs.getString(1));
            }
        }
        catch (Exception ex) {
            // где обработчик исключений МММММ
        }
        // ОМЕРЗИТЕЛЬНО ЗАХАРДКОДИЛ ГОВНО С ПОГАНЫМ ПРИВЕДЕНИЕМ ТИПОВ
        return filters_list.toArray(new String[1]);
    }

    protected String getQueryString() {

        boolean filter_flag = false;
        boolean global_flag = false;
        // Записываем начало строки
        String query = "SELECT * FROM " + db_table + " WHERE ";
        // Фильтруем по моделям датчика
        query += "(";
        for (int i = 0; i < model_choice.length; i++) {
            if (model_choice[i]) {
                query +=  "SensorName = '" + model_array[i] + "' OR ";
                filter_flag = true;
            }
        }
        query = query.substring(0, query.length() - (filter_flag ? 4 : 1));
        query += filter_flag ? ") AND " : "";
        global_flag = false | filter_flag;
        // Фильтруем по типу данных датчика
        filter_flag = false;
        query += "(";
        for (int i = 0; i < valtype_choice.length; i++) {
            if (valtype_choice[i]) {
                query += "DataType = '" + valtype_array[i] + "' OR ";
                filter_flag = true;
            }
        }
        query = query.substring(0, query.length() - (filter_flag ? 4 : 1));
        query += filter_flag ? ") AND " : "";
        global_flag = false | filter_flag;
        // Фильтруем по позиции датчика
        filter_flag = false;
        query += "(";
        for (int i = 0; i < position_choice.length; i++) {
            if (position_choice[i]) {
                query += "Position = '" + position_array[i] + "' OR ";
                filter_flag = true;
            }
        }
        query = query.substring(0, query.length() - (filter_flag ? 4 : 1));
        query += filter_flag ? ") AND " : "";
        global_flag = false | filter_flag;
        // Фильтр по времени
        if (left_border.after(right_border)) {
            Toast.makeText(getApplicationContext(), "Wrong time order", Toast.LENGTH_SHORT).show();
            setDefault();
        }

        else {
            query += "Date < '" + Integer.toString(right_border.get(Calendar.YEAR)) + "-" +
                    Integer.toString(right_border.get(Calendar.MONTH)) + "-" +
                    Integer.toString(right_border.get(Calendar.DAY_OF_MONTH)) + " " +
                    Integer.toString(right_border.get(Calendar.HOUR_OF_DAY)) + ":" +
                    Integer.toString(right_border.get(Calendar.MINUTE)) + ":" +
                    Integer.toString(right_border.get(Calendar.SECOND)) + "' AND  Date > '" +
                    Integer.toString(left_border.get(Calendar.YEAR)) + "-" +
                    Integer.toString(left_border.get(Calendar.MONTH)) + "-" +
                    Integer.toString(left_border.get(Calendar.DAY_OF_MONTH)) + " " +
                    Integer.toString(left_border.get(Calendar.HOUR_OF_DAY)) + ":" +
                    Integer.toString(left_border.get(Calendar.MINUTE)) + ":" +
                    Integer.toString(left_border.get(Calendar.SECOND)) + "'";
            global_flag = false;
        }
        if (global_flag)
            query = query.substring(0, query.length() - 5);
        query += ";";
        return query;
    }

    protected TextView stdText (String value) {
        TextView std = new TextView(getApplicationContext());
        std.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        std.setTextSize(15);
        std.setTextColor(Color.BLACK);
        std.setText(value);
        std.setBackground(getDrawable(R.drawable.cellborder));
        return std;
    }



    public void setDate(View view, String title) {
        if (setting_order) {
            right_dpd = new DatePickerDialog(MainActivity.this, d,
                    right_border.get(Calendar.YEAR),
                    right_border.get(Calendar.MONTH),
                    right_border.get(Calendar.DAY_OF_MONTH));
            right_dpd.getDatePicker().setMaxDate(max_value.getTimeInMillis());
            right_dpd.getDatePicker().setMinDate(min_value.getTimeInMillis());
            right_dpd.setTitle(title);
            right_dpd.show();
        }
        else {
            left_dpd = new DatePickerDialog(MainActivity.this, d,
                    left_border.get(Calendar.YEAR),
                    left_border.get(Calendar.MONTH),
                    left_border.get(Calendar.DAY_OF_MONTH));
            left_dpd.getDatePicker().setMaxDate(max_value.getTimeInMillis());
            left_dpd.getDatePicker().setMinDate(min_value.getTimeInMillis());
            left_dpd.setTitle(title);
            left_dpd.show();
        }
    }

    public void setTime(View view, String title) {
        if (setting_order) {
            right_tpd = new TimePickerDialog(MainActivity.this, t,
                    right_border.get(Calendar.HOUR_OF_DAY),
                    right_border.get(Calendar.MINUTE), true);
            right_tpd.setTitle(title);
            right_tpd.show();
        }
        else {
            left_tpd = new TimePickerDialog(MainActivity.this, t,
                    left_border.get(Calendar.HOUR_OF_DAY),
                    left_border.get(Calendar.MINUTE), true);
            left_tpd.setTitle(title);
            left_tpd.show();
        }
    }

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            if (cnt == 2) {
                right_border.set(Calendar.HOUR_OF_DAY, hourOfDay);
                right_border.set(Calendar.MINUTE, minute);
            }
            else if (cnt == 1) {
                left_border.set(Calendar.HOUR_OF_DAY, hourOfDay);
                left_border.set(Calendar.MINUTE, minute);
            }
            /*
            if (setting_order) {
                right_border.set(Calendar.HOUR_OF_DAY, hourOfDay);
                right_border.set(Calendar.MINUTE, minute);
                //
            }
            else {
                left_border.set(Calendar.HOUR_OF_DAY, hourOfDay);
                left_border.set(Calendar.MINUTE, minute);
                //
            }*/
        }
    };

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            if (datePicker.equals(right_dpd.getDatePicker())) {
                right_border.set(Calendar.YEAR, year);
                right_border.set(Calendar.MONTH, monthOfYear);
                right_border.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
            else if (datePicker.equals(left_dpd.getDatePicker())) {
                left_border.set(Calendar.YEAR, year);
                left_border.set(Calendar.MONTH, monthOfYear);
                left_border.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
            cnt++;
            /*
            if (setting_order) {
                right_border.set(Calendar.YEAR, year);
                right_border.set(Calendar.MONTH, monthOfYear);
                right_border.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
            else {
                left_border.set(Calendar.YEAR, year);
                left_border.set(Calendar.MONTH, monthOfYear);
                left_border.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }*/
        }
    };

    public void setDefault() {
        left_border.set(min_value.get(Calendar.YEAR), min_value.get(Calendar.MONTH),
                min_value.get(Calendar.DAY_OF_MONTH), min_value.get(Calendar.HOUR_OF_DAY),
                min_value.get(Calendar.MINUTE), min_value.get(Calendar.SECOND));
        right_border.set(max_value.get(Calendar.YEAR), max_value.get(Calendar.MONTH),
                max_value.get(Calendar.DAY_OF_MONTH), max_value.get(Calendar.HOUR_OF_DAY),
                max_value.get(Calendar.MINUTE), max_value.get(Calendar.SECOND));
    }

}