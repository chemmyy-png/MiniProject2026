package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.miniproject.databinding.ActivityLoginBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityLifecycle";
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        // Dynamically adjust ScrollView bottom padding when soft keyboard opens
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(0, 0, 0, imeInsets.bottom);
            return insets;
        });

        // Department Spinner Adapter
        ArrayAdapter<CharSequence> deptAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.department_array,
                R.layout.spinner_item
        );
        deptAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        binding.loginSpinnerDept.setAdapter(deptAdapter);

        // Semester spinner adapter
        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.createFromResource(
                this, R.array.semester_array, R.layout.spinner_item);
        semesterAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        binding.loginSpinnerSemester.setAdapter(semesterAdapter);

        // Validate required fields on Start Assessment button click
        binding.loginButton.setOnClickListener(v -> validateAndProceed());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }

    private void validateAndProceed() {
        String name = binding.loginEditName.getText().toString().trim();
        String studentId = binding.loginEditId.getText().toString().trim();
        int deptPosition = binding.loginSpinnerDept.getSelectedItemPosition();
        String course = binding.loginEditCourse.getText().toString().trim();
        int semesterPosition = binding.loginSpinnerSemester.getSelectedItemPosition();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            binding.loginEditName.requestFocus();
            return;
        }
        if (studentId.isEmpty()) {
            Toast.makeText(this, "Please enter your Student ID", Toast.LENGTH_SHORT).show();
            binding.loginEditId.requestFocus();
            return;
        }
        if (deptPosition == 0) {
            Toast.makeText(this, "Please select a department", Toast.LENGTH_SHORT).show();
            return;
        }
        if (course.isEmpty()) {
            Toast.makeText(this, "Please enter your course name", Toast.LENGTH_SHORT).show();
            binding.loginEditCourse.requestFocus();
            return;
        }
        if (semesterPosition == 0) {
            Toast.makeText(this, "Please select a semester", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Assessment started successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        intent.putExtra("USER_NAME", name);
        startActivity(intent);
    }
}
