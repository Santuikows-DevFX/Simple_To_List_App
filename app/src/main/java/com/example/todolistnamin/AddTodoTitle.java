package com.example.todolistnamin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Calendar;

public class AddTodoTitle extends AppCompatActivity {

    TodoModel todoModel;
    ListDatabaseHelper ldb;

    EditText title, tbDate;
    Button btnAddTitle;
    ImageButton btnAddDate;

    String todayDate, selectedDate;
    Calendar today;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo_title);

        todoModel = new TodoModel();
        ldb = new ListDatabaseHelper(this);

        today = Calendar.getInstance();
        int yearNow = today.get(Calendar.YEAR);
        int monthNow = today.get(Calendar.MONTH);
        int dayNow = today.get(Calendar.DAY_OF_MONTH);
        todayDate = monthNow + "/" + dayNow + "/" + yearNow;

        title = findViewById(R.id.tbTitle);
        tbDate = findViewById(R.id.tbDate);
        btnAddTitle = findViewById(R.id.btnAddTitle);
        btnAddDate = findViewById(R.id.btnAddDate);

        btnAddDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selectedDate = (month) + "/" + dayOfMonth + "/" + year;

                if(selectedDate.equalsIgnoreCase(todayDate)){
                    tbDate.setText("Today");

                }else{
                    tbDate.setText(selectedDate);
                }
            }, yearNow, monthNow, dayNow);

            datePickerDialog.show();
        });

        btnAddTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todoModel.setTitle(title.getText().toString().trim());
                todoModel.setDate(selectedDate);

                long id = ldb.insertTitle(todoModel);

                Intent i = new Intent(AddTodoTitle.this, AddTodo.class);
                i.putExtra("title", title.getText().toString());
                i.putExtra("date", selectedDate);
                i.putExtra("titleId", id);
                startActivity(i);
                finish();

            }
        });

//        btnAddTitle.setOnClickListener(v ->{
////            todoModel.setTitle(title.getText().toString().trim());
////            todoModel.setDate(selectedDate);
////
////            long id = ldb.insertTitle(todoModel);
////
////            Intent i = new Intent(AddTodoTitle.this, AddTodo.class);
////            i.putExtra("title", title.getText().toString());
////            i.putExtra("date", selectedDate);
////            i.putExtra("titleId", id);
////            startActivity(i);
////            finish();
//
//        });
    }
}