package com.example.todolistnamin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class EditTodo extends AppCompatActivity {

    Intent i;
    TodoAdapter tda;
    ListDatabaseHelper ldb;
    EditText tbAddTitleUpd, tbDateLabelUpd, tbTimeLabelUpd, todo_inputUpd;
    ImageButton btnSelectDateUpd, btnSelectTimeUpd;
    LinearLayout todo_list_containerUpd;
    SharedPreferences sharedPreferences;

    Calendar today;
    String todayDate;

    private static final String TODO_CHECKBOX_STATE = "todo_checkbox_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);

        today = Calendar.getInstance();
        int yearNow = today.get(Calendar.YEAR);
        int monthNow = today.get(Calendar.MONTH);
        int dayNow = today.get(Calendar.DAY_OF_MONTH);
        todayDate = monthNow + "/" + dayNow + "/" + yearNow;

        ldb = new ListDatabaseHelper(this);
        i = getIntent();

        int selectedTitleID = i.getIntExtra("selectedID", -1);

        if(selectedTitleID == -1){ //means walang nakuha
            finish();
        }

        tbAddTitleUpd = findViewById(R.id.tbAddTitleUpd);
        tbDateLabelUpd = findViewById(R.id.tbDateLabelUpd);
        tbTimeLabelUpd = findViewById(R.id.tbTimeLabelUpd);
        todo_inputUpd = findViewById(R.id.todo_inputUpd);
        btnSelectDateUpd = findViewById(R.id.btnSelectDateUpd);
        btnSelectTimeUpd = findViewById(R.id.btnSelectTimeUpd);
        todo_list_containerUpd = findViewById(R.id.todo_list_containerUpd);

        List<TodoModel> todoList = ldb.fetchTodoListByID(selectedTitleID);
        TodoModel selectedTitle = ldb.fetchTodoByID(selectedTitleID);

        tbAddTitleUpd.setText(selectedTitle.getTitle());
        tbDateLabelUpd.setText(selectedTitle.getDate());

        for (TodoModel todo : todoList) {
            createCheckboxForTodo(selectedTitleID, todo.getEntryId(),todo.getEntry(), todo.getTime());
        }

        todo_inputUpd.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String todoText = todo_inputUpd.getText().toString().trim();
                String todoTitle = tbAddTitleUpd.getText().toString().trim();
                String todoDate = tbDateLabelUpd.getText().toString().trim();
                if (!todoText.isEmpty() && !todoTitle.isEmpty() && !todoDate.isEmpty()) {
                    if (!tbTimeLabelUpd.getText().toString().isEmpty()) {
                        long entry_id = ldb.insertEntryAndTime(selectedTitleID, todoText, tbTimeLabelUpd.getText().toString());
                        createCheckboxForTodo(selectedTitleID, entry_id, todoText, tbTimeLabelUpd.getText().toString());
                        tbTimeLabelUpd.setText(null);
                        return true;
                    } else {
                        Toast.makeText(this, "Add Time!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Missing Fields!", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });

        btnSelectDateUpd.setOnClickListener(v -> {
            DatePickerDialog dateDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                    tbDateLabelUpd.setText(month+ 1+ "/" +dayOfMonth+ "/" +year), yearNow, monthNow, dayNow);

            dateDialog.show();
        });

        btnSelectTimeUpd.setOnClickListener(v ->{
            Log.d("clicked clock", "hello");
            TimePickerDialog timeDialog = new TimePickerDialog(this, (view, hourOfDay, minute) ->
                    tbTimeLabelUpd.setText(String.valueOf(hourOfDay)+":"+String.valueOf(minute)), 15, 00, false);

            timeDialog.show();
        });
    }

    private void createCheckboxForTodo(int todoId, long entryId, String todoText, String timeSelected) {
        if (!todoText.isEmpty()) {
            LinearLayout todoItem = new LinearLayout(this);
            todoItem.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            todoItem.setOrientation(LinearLayout.HORIZONTAL);

            ImageButton deleteToDo = new ImageButton(this);
            deleteToDo.setBackgroundResource(R.drawable.baseline_delete_24);
            deleteToDo.setOnClickListener(v -> {

                AlertDialog.Builder alertBuilder;
                alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("Delete this task?")
                        .setMessage("You won't be able to revert this back again.")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ldb.deleteEntryByID(entryId);
                                todo_list_containerUpd.removeView(todoItem);
                                Toast.makeText(EditTodo.this, "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();

            });

            TextView tvTimeSelected = new TextView(this);
            tvTimeSelected.setGravity(Gravity.END);
            tvTimeSelected.setTextSize(20f);
            tvTimeSelected.setText(timeSelected);

            CheckBox checkbox = new CheckBox(this);
            checkbox.setText(todoText);
            checkbox.setTextSize(20f);
            checkbox.setTextColor(Color.BLACK);

            ColorStateList colorStateList = ColorStateList.valueOf(Color.BLACK);
            checkbox.setButtonTintList(colorStateList);

            sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            checkbox.setChecked(sharedPreferences.getBoolean(TODO_CHECKBOX_STATE + todoText, false));

            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(TODO_CHECKBOX_STATE + todoText, true);
                    editor.apply();
                }
            });

            todoItem.addView(checkbox);
            todoItem.addView(deleteToDo);
            todoItem.addView(tvTimeSelected, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f));

            todo_list_containerUpd.addView(todoItem);
            todo_inputUpd.getText().clear();
        }
    }

}