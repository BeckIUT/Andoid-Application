package com.example.oybek.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER = "Answer_TRUE";
    private static final String ANSWER_SHOWN = "Answer SHOWN";
    private static final String KEY_SHOWN = "KEY_SHOWN";
    private static final String KEY_ANSWER = "KEY_ANSWER";
//    private static final String KEY_TOKEN = "KEY_TOKENS";
//
//    private static int mTokens = 3;

    private boolean mAnswerIsTrue;
    private boolean mIsCheated;
    private TextView mAnswerTextView;
    private TextView mAPI;
    private Button mShowAnswerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER, false);

        if (savedInstanceState != null){
            mAnswerIsTrue = savedInstanceState.getBoolean(KEY_ANSWER, false);
            mIsCheated = savedInstanceState.getBoolean(KEY_SHOWN, false);
//            mTokens = savedInstanceState.getInt(KEY_TOKEN, 3);
            setAnswerShownResult(mIsCheated);
        }

        mAnswerTextView = findViewById(R.id.answer_text_view);
        mShowAnswerButton = findViewById(R.id.show_answer_button);

        if(mIsCheated) {
            if(mAnswerIsTrue)
                mAnswerTextView.setText(R.string.true_button);
            else
                mAnswerTextView.setText(R.string.false_button);
            mAnswerTextView.setBackgroundColor(0xFF12FF45);
        }

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAnswerIsTrue)
                    mAnswerTextView.setText(R.string.true_button);
                else
                    mAnswerTextView.setText(R.string.false_button);
                mAnswerTextView.setBackgroundColor(0xFF12FF45);
                mIsCheated = true;
//                mTokens--;
                setAnswerShownResult(mIsCheated);
            }
        });

        mAPI = findViewById(R.id.api);
        mAPI.setText("API Level " + Integer.valueOf(Build.VERSION.SDK));
    }

    private void setAnswerShownResult(boolean answerShown){
        Intent data = new Intent();
        data.putExtra(ANSWER_SHOWN,answerShown);
        setResult(RESULT_OK, data);
    }

    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(ANSWER_SHOWN,false);
    }

//    public static int getTokes(){
//        return mTokens;
//    }

    public static Intent newIntent(Context packageContext, boolean answerIsTrue){
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER, answerIsTrue);
        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState){
        super.onSaveInstanceState(savedState);
        savedState.putBoolean(KEY_SHOWN, mIsCheated);
        savedState.putBoolean(KEY_ANSWER, mAnswerIsTrue);
//        savedState.putInt(KEY_TOKEN, mTokens);
    }
}
