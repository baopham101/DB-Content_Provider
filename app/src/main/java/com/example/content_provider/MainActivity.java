package com.example.content_provider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickAddStudent(View view) {
        // Add a new Student record
        ContentValues values = new ContentValues();
        values.put(DataProvider.VALUE, ((EditText) findViewById(R.id.edtStudentName)).getText().toString());

        Uri result = getContentResolver().insert(DataProvider.CONTENT_URI, values);

        if (result != null) {
            Toast.makeText(getBaseContext(), "Add new student successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Add new student failed", Toast.LENGTH_LONG).show();
        }

    }

    public void onClickUpdateStudent(View view) {
        // Update student based on ID
        String id = ((EditText) findViewById(R.id.edtStudentId)).getText().toString();
        String selection = "_id = \"" + id + "\"";
        ContentValues values = new ContentValues();
        values.put(DataProvider.VALUE, ((EditText) findViewById(R.id.edtStudentName)).getText().toString());

        int result = getContentResolver().update(DataProvider.CONTENT_URI, values, selection, null);
        if (result > 0) {
            Toast.makeText(getBaseContext(), "Update student with ID " + id + " successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Update student failed", Toast.LENGTH_LONG).show();

        }
    }

    public void onClickDeleteStudent(View view) {
        // Delete student based on ID
        String id = ((EditText) findViewById(R.id.edtStudentId)).getText().toString();
        String selection = "_id = \"" + id + "\"";
        ContentValues values = new ContentValues();
        values.put(DataProvider.VALUE, ((EditText) findViewById(R.id.edtStudentName)).getText().toString());

        int result = getContentResolver().delete(DataProvider.CONTENT_URI, selection, null);
        if (result > 0) {
            Toast.makeText(getBaseContext(), "Delete student with ID " + id + " successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Delete student failed", Toast.LENGTH_LONG).show();

        }
    }
}