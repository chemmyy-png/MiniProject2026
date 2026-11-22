package com.example.miniproject;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.miniproject.databinding.ActivityQuizBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivityLifecycle";
    private ActivityQuizBinding binding;
    private RequestQueue queue;

    private SoundPool soundPool;
    private int soundCorrectId;
    private int soundWrongId;

    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    private static class Question {
        String category;
        String questionText;
        String correctAnswer;
        List<String> options;
        int selectedOptionIndex = -1; // -1 means unselected

        Question(String category, String questionText, String correctAnswer, List<String> options) {
            this.category = category;
            this.questionText = questionText;
            this.correctAnswer = correctAnswer;
            this.options = options;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz);
        queue = Volley.newRequestQueue(this);

        initSoundPool();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnCloseFeedback.setOnClickListener(v -> binding.cardFeedback.setVisibility(View.GONE));
        binding.btnContinue.setOnClickListener(v -> onContinueClicked());

        binding.btnPrevious.setOnClickListener(v -> onPreviousClicked());
        binding.btnNextMain.setOnClickListener(v -> onContinueClicked());

        setupOptionListeners();
        fetchQuizQuestions();
    }

    private void initSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();

        soundCorrectId = soundPool.load(this, R.raw.sound_correct, 1);
        soundWrongId = soundPool.load(this, R.raw.sound_wrong, 1);
    }

    private void playSoundCorrect() {
        if (soundPool != null && soundCorrectId != 0) {
            soundPool.play(soundCorrectId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    private void playSoundWrong() {
        if (soundPool != null && soundWrongId != 0) {
            soundPool.play(soundWrongId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
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
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    private void fetchQuizQuestions() {
        binding.loadingContainer.setVisibility(View.VISIBLE);
        binding.quizScrollView.setVisibility(View.GONE);

        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.pixel_bounce);
        binding.ivCuteLoader.startAnimation(bounce);

        String url = "https://opentdb.com/api.php?amount=10&category=18&difficulty=easy&type=multiple";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            questionList.clear();

                            for (int i = 0; i < results.length(); i++) {
                                JSONObject qObj = results.getJSONObject(i);
                                String category = decodeHtml(qObj.optString("category", "Science"));
                                String question = decodeHtml(qObj.getString("question"));
                                String correct = decodeHtml(qObj.getString("correct_answer"));

                                JSONArray rawIncorrect = qObj.getJSONArray("incorrect_answers");
                                List<String> options = new ArrayList<>();
                                options.add(correct);
                                for (int j = 0; j < rawIncorrect.length(); j++) {
                                    options.add(decodeHtml(rawIncorrect.getString(j)));
                                }
                                Collections.shuffle(options);

                                questionList.add(new Question(category, question, correct, options));
                            }

                            binding.ivCuteLoader.clearAnimation();
                            binding.loadingContainer.setVisibility(View.GONE);
                            binding.quizScrollView.setVisibility(View.VISIBLE);
                            displayQuestion(0);

                        } catch (JSONException e) {
                            Log.e("QuizActivity", "JSON Parsing error", e);
                            Toast.makeText(QuizActivity.this, "Failed to parse questions", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        binding.ivCuteLoader.clearAnimation();
                        binding.loadingContainer.setVisibility(View.GONE);
                        Log.e("QuizActivity", "Volley Error: " + error.getMessage());
                        Toast.makeText(QuizActivity.this, "Error fetching questions", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        queue.add(request);
    }

    private String decodeHtml(String text) {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString();
    }

    private void displayQuestion(int index) {
        if (index < 0 || index >= questionList.size()) {
            return;
        }

        currentQuestionIndex = index;
        Question q = questionList.get(index);

        binding.tvQuestionCounter.setText("Question " + (index + 1) + " of " + questionList.size());
        calculateScore();
        binding.tvScoreBadge.setText("Score: " + score);

        int progressPercent = ((index + 1) * 100) / questionList.size();
        binding.pbQuestionProgress.setProgress(progressPercent);
        binding.tvProgressPercent.setText(progressPercent + "%");

        binding.tvCategoryTag.setText("🧪 " + q.category);
        binding.tvQuestionText.setText(q.questionText);

        resetOptionViews();

        RelativeLayout[] optionLayouts = {
                binding.layoutOptionA,
                binding.layoutOptionB,
                binding.layoutOptionC,
                binding.layoutOptionD
        };
        TextView[] optionTexts = {
                binding.tvOptionAText,
                binding.tvOptionBText,
                binding.tvOptionCText,
                binding.tvOptionDText
        };
        ImageView[] optionChecks = {
                binding.ivOptionACheck,
                binding.ivOptionBCheck,
                binding.ivOptionCCheck,
                binding.ivOptionDCheck
        };

        for (int i = 0; i < optionLayouts.length; i++) {
            if (i < q.options.size()) {
                optionLayouts[i].setVisibility(View.VISIBLE);
                optionTexts[i].setText(q.options.get(i));
            } else {
                optionLayouts[i].setVisibility(View.GONE);
            }
        }

        // Configure Previous button
        if (index == 0) {
            binding.btnPrevious.setVisibility(View.INVISIBLE);
        } else {
            binding.btnPrevious.setVisibility(View.VISIBLE);
        }

        // Configure Next / Submit button
        if (index == questionList.size() - 1) {
            binding.btnNextMain.setText("Finish Assessment");
            binding.btnContinue.setText("Finish Assessment");
        } else {
            binding.btnNextMain.setText("Next");
            binding.btnContinue.setText("Continue");
        }

        // Restore previously selected option if already answered
        if (q.selectedOptionIndex != -1) {
            disableAllOptions();
            int selectedIdx = q.selectedOptionIndex;
            String chosenAnswer = q.options.get(selectedIdx);
            boolean isCorrect = chosenAnswer.equals(q.correctAnswer);

            if (isCorrect) {
                optionLayouts[selectedIdx].setBackgroundResource(R.drawable.bg_option_correct);
                optionChecks[selectedIdx].setImageResource(R.drawable.ic_check_small);
                optionChecks[selectedIdx].setVisibility(View.VISIBLE);

                binding.ivFeedbackIcon.setImageResource(R.drawable.ic_check_circle);
                binding.tvFeedbackTitle.setText("Correct!");
                binding.tvFeedbackTitle.setTextColor(ContextCompat.getColor(this, R.color.quiz_green_text));
                binding.tvFeedbackText.setText(q.correctAnswer);
            } else {
                optionLayouts[selectedIdx].setBackgroundResource(R.drawable.bg_option_incorrect);
                highlightCorrectOption(q.correctAnswer);

                binding.ivFeedbackIcon.setImageResource(R.drawable.ic_close);
                binding.tvFeedbackTitle.setText("Incorrect");
                binding.tvFeedbackTitle.setTextColor(ContextCompat.getColor(this, R.color.quiz_red_text));
                binding.tvFeedbackText.setText("Correct answer: " + q.correctAnswer);
            }
            binding.cardFeedback.setVisibility(View.VISIBLE);
        } else {
            binding.cardFeedback.setVisibility(View.GONE);
        }
    }

    private void setupOptionListeners() {
        binding.layoutOptionA.setOnClickListener(v -> handleOptionSelected(0, binding.layoutOptionA, binding.tvOptionAText, binding.ivOptionACheck));
        binding.layoutOptionB.setOnClickListener(v -> handleOptionSelected(1, binding.layoutOptionB, binding.tvOptionBText, binding.ivOptionBCheck));
        binding.layoutOptionC.setOnClickListener(v -> handleOptionSelected(2, binding.layoutOptionC, binding.tvOptionCText, binding.ivOptionCCheck));
        binding.layoutOptionD.setOnClickListener(v -> handleOptionSelected(3, binding.layoutOptionD, binding.tvOptionDText, binding.ivOptionDCheck));
    }

    private void resetOptionViews() {
        RelativeLayout[] layouts = {binding.layoutOptionA, binding.layoutOptionB, binding.layoutOptionC, binding.layoutOptionD};
        ImageView[] checks = {binding.ivOptionACheck, binding.ivOptionBCheck, binding.ivOptionCCheck, binding.ivOptionDCheck};

        for (int i = 0; i < layouts.length; i++) {
            layouts[i].setBackgroundResource(R.drawable.bg_option_default);
            layouts[i].setEnabled(true);
            checks[i].setVisibility(View.GONE);
        }
    }

    private void handleOptionSelected(int optionIndex, RelativeLayout selectedLayout, TextView selectedText, ImageView selectedCheck) {
        Question currentQ = questionList.get(currentQuestionIndex);
        if (currentQ.selectedOptionIndex != -1) return;

        currentQ.selectedOptionIndex = optionIndex;
        disableAllOptions();

        String chosenAnswer = selectedText.getText().toString();
        boolean isCorrect = chosenAnswer.equals(currentQ.correctAnswer);

        if (isCorrect) {
            playSoundCorrect();

            selectedLayout.setBackgroundResource(R.drawable.bg_option_correct);
            selectedCheck.setImageResource(R.drawable.ic_check_small);
            selectedCheck.setVisibility(View.VISIBLE);

            binding.ivFeedbackIcon.setImageResource(R.drawable.ic_check_circle);
            binding.tvFeedbackTitle.setText("Correct!");
            binding.tvFeedbackTitle.setTextColor(ContextCompat.getColor(this, R.color.quiz_green_text));
            binding.tvFeedbackText.setText(currentQ.correctAnswer);
        } else {
            playSoundWrong();

            selectedLayout.setBackgroundResource(R.drawable.bg_option_incorrect);

            highlightCorrectOption(currentQ.correctAnswer);

            binding.ivFeedbackIcon.setImageResource(R.drawable.ic_close);
            binding.tvFeedbackTitle.setText("Incorrect");
            binding.tvFeedbackTitle.setTextColor(ContextCompat.getColor(this, R.color.quiz_red_text));
            binding.tvFeedbackText.setText("Correct answer: " + currentQ.correctAnswer);
        }

        calculateScore();
        binding.tvScoreBadge.setText("Score: " + score);
        binding.cardFeedback.setVisibility(View.VISIBLE);
    }

    private void calculateScore() {
        score = 0;
        for (Question q : questionList) {
            if (q.selectedOptionIndex != -1) {
                String chosen = q.options.get(q.selectedOptionIndex);
                if (chosen.equals(q.correctAnswer)) {
                    score++;
                }
            }
        }
    }

    private void highlightCorrectOption(String correctAnswer) {
        TextView[] optionTexts = {binding.tvOptionAText, binding.tvOptionBText, binding.tvOptionCText, binding.tvOptionDText};
        RelativeLayout[] layouts = {binding.layoutOptionA, binding.layoutOptionB, binding.layoutOptionC, binding.layoutOptionD};
        ImageView[] checks = {binding.ivOptionACheck, binding.ivOptionBCheck, binding.ivOptionCCheck, binding.ivOptionDCheck};

        for (int i = 0; i < optionTexts.length; i++) {
            if (optionTexts[i].getText().toString().equals(correctAnswer)) {
                layouts[i].setBackgroundResource(R.drawable.bg_option_correct);
                checks[i].setImageResource(R.drawable.ic_check_small);
                checks[i].setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    private void disableAllOptions() {
        binding.layoutOptionA.setEnabled(false);
        binding.layoutOptionB.setEnabled(false);
        binding.layoutOptionC.setEnabled(false);
        binding.layoutOptionD.setEnabled(false);
    }

    private void onPreviousClicked() {
        if (currentQuestionIndex > 0) {
            displayQuestion(currentQuestionIndex - 1);
        }
    }

    private void onContinueClicked() {
        Question currentQ = questionList.get(currentQuestionIndex);
        if (currentQ.selectedOptionIndex == -1) {
            Toast.makeText(this, "Please select an answer first!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentQuestionIndex < questionList.size() - 1) {
            displayQuestion(currentQuestionIndex + 1);
        } else {
            ArrayList<QuestionBreakdown> breakdowns = new ArrayList<>();
            for (int i = 0; i < questionList.size(); i++) {
                Question q = questionList.get(i);
                String numStr = String.format(java.util.Locale.US, "%02d", i + 1);
                String userAns = (q.selectedOptionIndex != -1) ? q.options.get(q.selectedOptionIndex) : "No Answer";
                boolean isCorr = userAns.equals(q.correctAnswer);
                breakdowns.add(new QuestionBreakdown(numStr, q.category, userAns, q.correctAnswer, isCorr));
            }

            String userName = getIntent().getStringExtra("USER_NAME");
            QuizResultModel resultModel = new QuizResultModel(userName, score, questionList.size());

            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("RESULT_MODEL", resultModel);
            intent.putExtra("BREAKDOWN_LIST", breakdowns);
            startActivity(intent);
            finish();
        }
    }
}
