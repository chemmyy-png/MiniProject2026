package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.miniproject.databinding.ActivityResultBinding;
import com.example.miniproject.databinding.ItemQuestionBreakdownBinding;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivityLifecycle";
    private ActivityResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_result);

        QuizResultModel resultModel = (QuizResultModel) getIntent().getSerializableExtra("RESULT_MODEL");
        if (resultModel == null) {
            resultModel = new QuizResultModel("Student", 8, 10);
        }
        binding.setResult(resultModel);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnRestart.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        @SuppressWarnings("unchecked")
        List<QuestionBreakdown> breakdowns = (List<QuestionBreakdown>) getIntent().getSerializableExtra("BREAKDOWN_LIST");
        if (breakdowns == null || breakdowns.isEmpty()) {
            breakdowns = createSampleBreakdowns();
        }

        populateBreakdown(breakdowns);
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

    private void populateBreakdown(List<QuestionBreakdown> breakdowns) {
        binding.containerBreakdown.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (QuestionBreakdown item : breakdowns) {
            ItemQuestionBreakdownBinding itemBinding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.item_question_breakdown,
                    binding.containerBreakdown,
                    true
            );
            itemBinding.setItem(item);
            itemBinding.executePendingBindings();
        }
    }

    private List<QuestionBreakdown> createSampleBreakdowns() {
        List<QuestionBreakdown> list = new ArrayList<>();
        list.add(new QuestionBreakdown("01", "Geography", "Paris", "Paris", true));
        list.add(new QuestionBreakdown("02", "Science", "Venus", "Mars", false));
        list.add(new QuestionBreakdown("03", "Computing", "CPU", "CPU", true));
        list.add(new QuestionBreakdown("04", "History", "1945", "1945", true));
        list.add(new QuestionBreakdown("05", "General Knowledge", "Pacific", "Pacific", true));
        return list;
    }
}
