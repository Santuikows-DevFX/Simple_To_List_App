package com.example.todolistnamin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

public class ListActivity extends AppCompatActivity {


    FloatingActionButton fab;
    Calendar today;
    String selectedDate, todayDate;
    ImageButton btnSelectDate;
    EditText tbDateLabel;
    DatePicker datePicker;
    RecyclerView recListTodos;
    CheckBox cbSeeAllTasks;

    ListDatabaseHelper ldb;
    TodoAdapter todoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //get today's date convert to string month/day/year format
        today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DAY_OF_MONTH);
        todayDate = month + "/" + day + "/" + year;

        //findview by id of the components in the activity
        cbSeeAllTasks = findViewById(R.id.cbSeeAll);
        fab = findViewById(R.id.fab);
        recListTodos = findViewById(R.id.reclistTodos);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        tbDateLabel = findViewById(R.id.tbDateLabel);
        datePicker = findViewById(R.id.calendarDialog);

        //fetching the data from sqlite db
        ldb = new ListDatabaseHelper(this);
        todoAdapter = new TodoAdapter(ldb.fetchTodoTitles(), this);
        recListTodos.setLayoutManager(new LinearLayoutManager(this));
        recListTodos.setAdapter(todoAdapter);

        cbSeeAllTasks.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                ldb = new ListDatabaseHelper(this);
                todoAdapter = new TodoAdapter(ldb.fetchTodoTitles(), this);
                recListTodos.setLayoutManager(new LinearLayoutManager(this));
                recListTodos.setAdapter(todoAdapter);
            }
        });

        fab.setOnClickListener(v ->{
            Intent i = new Intent(this, AddTodoTitle.class);
            startActivity(i);
        });

        btnSelectDate.setOnClickListener(v -> {

            DatePickerDialog datePickerDialog = new DatePickerDialog(ListActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    selectedDate = (month) + "/" + dayOfMonth + "/" + year;

                    if(selectedDate.equalsIgnoreCase(todayDate)){
                        tbDateLabel.setText("Today");

                    }else{
                        tbDateLabel.setText(selectedDate);
                    }

                    todoAdapter = new TodoAdapter(ldb.fetchTodoTitlesByDate(selectedDate), ListActivity.this);
                    recListTodos.setLayoutManager(new LinearLayoutManager(ListActivity.this));
                    recListTodos.setAdapter(todoAdapter);
                }

            }, year, month, day);

            datePickerDialog.show();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        todoAdapter.refresh(ldb.fetchTodoTitles());
    }
}