package com.example.oybek.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;
    private TextView mTokens;
    private boolean[] mIsCheater = new boolean[]{ false, false, false, false, false, false, };

    private int mCurrentIndex = 0;
    private int mScore = 0;
    private int mAnswered = 0;
    private int mCheatTokens = 3;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_SCORE = "score";
    private static final String KEY_ANSWERED = "answered";
    private static final String KEY_NUMBER = "answered_number";
    private static final String KEY_CHEATER = "is_Cheater";
    private static final String KEY_TOKEN = "TOKENS";
    private static final int REQUEST_CODE_CHEAT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);
        String mAnsweredQuestions;

        if(savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mScore = savedInstanceState.getInt(KEY_SCORE, 0);
            mAnsweredQuestions = savedInstanceState.getString(KEY_ANSWERED);
            mAnswered = savedInstanceState.getInt(KEY_NUMBER,0);
            mIsCheater[mCurrentIndex] = savedInstanceState.getBoolean(KEY_CHEATER,false);
            for (int i = 0; i < mQuestionBank.length; i++) {
                if (mAnsweredQuestions.charAt(i) == '1')
                    mQuestionBank[i].setAnswered(true);
                else
                    mQuestionBank[i].setAnswered(false);
            }
            mCheatTokens = savedInstanceState.getInt(KEY_TOKEN,3);
        }

        mQuestionTextView = findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mTokens = findViewById(R.id.cheat_tokens);
        if(mCheatTokens > 0)
            mTokens.setText("TOKENS LEFT : " + mCheatTokens);
        else
            mTokens.setText("TOKENS LEFT : 0");

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mPrevButton = findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                if(mCurrentIndex < 0)
                    mCurrentIndex = mQuestionBank.length - 1;
                updateQuestion();
            }
        });

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent,REQUEST_CODE_CHEAT);
            }
        });

        if(mCheatTokens <= 0)
            mCheatButton.setClickable(false);

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK)
            return;

        if(requestCode == REQUEST_CODE_CHEAT)
            if(data != null)
                mIsCheater[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
        manageTokens();
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putInt(KEY_SCORE, mScore);
        outState.putInt(KEY_NUMBER, mAnswered);
        outState.putBoolean(KEY_CHEATER, mIsCheater[mCurrentIndex]);
        outState.putInt(KEY_TOKEN, mCheatTokens);
        String questionsAnswered = new String();
        for (int i = 0; i < mQuestionBank.length; i++){
            if(mQuestionBank[i].isAnswered())
                questionsAnswered = questionsAnswered + '1';
            else
                questionsAnswered = questionsAnswered + '0';
        }
         outState.putString(KEY_ANSWERED, questionsAnswered);
    }

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        mTrueButton.setClickable(!mQuestionBank[mCurrentIndex].isAnswered());
        mFalseButton.setClickable(!mQuestionBank[mCurrentIndex].isAnswered());

        if(mQuestionBank[mCurrentIndex].isAnswered())
            mQuestionTextView.setBackgroundColor(0xFF12FF45);
        else
            mQuestionTextView.setBackgroundColor(0xffff0000);

        if(allAnswered()) {
            Toast.makeText(this, "Your score is " + mScore + "/" + mQuestionBank.length, Toast.LENGTH_SHORT).show();
            mNextButton.setClickable(false);
            mPrevButton.setClickable(false);
            mQuestionTextView.setClickable(false);
        }
    }

    private void checkAnswer(boolean userAnswer){
        boolean answerTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        mQuestionBank[mCurrentIndex].setAnswered(true);
        mQuestionTextView.setBackgroundColor(0xFF12FF45);
        int messageResId;
        if(mIsCheater[mCurrentIndex]){
            messageResId = R.string.judgement_toast;
        }else{
            if(userAnswer == answerTrue) {
                messageResId = R.string.correct_toast;
                mScore++;
            }else
                messageResId = R.string.incorrect_toast;
        }
        mAnswered++;

        mTrueButton.setClickable(false);
        mFalseButton.setClickable(false);

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private boolean allAnswered(){
        boolean allAnswered = false;
        if(mAnswered == mQuestionBank.length)
            allAnswered = true;
        return allAnswered;
    }

    private void manageTokens(){
        if(mIsCheater[mCurrentIndex])
            mCheatTokens--;

        mTokens.setText("TOKENS LEFT : " + mCheatTokens);

        if(mCheatTokens <= 0) {
            mCheatButton.setClickable(false);
            mTokens.setText("TOKENS LEFT : 0");

        }
    }
}
