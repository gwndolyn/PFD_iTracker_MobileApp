package sg.edu.np.mad.dontslack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class Timer extends AppCompatActivity {
    private EditText mEditTextInput;
    private TextView mTextViewCountDown;
    private Button mButtonSet;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private Button mButtonFocusMode;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;
    private boolean mLockTask;
    private boolean mIsFocus;

    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;

    private ImageView backHomePage;

    public static String TAG = "Timer Task";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        mEditTextInput = findViewById(R.id.edit_text_input);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        mButtonSet = findViewById(R.id.button_set);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);
        mButtonFocusMode = findViewById(R.id.button_focus_mode);
        mLockTask = false;
        mIsFocus = false;
        backHomePage = findViewById(R.id.backHomeTimer);

        /* Hiding the top bar */
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //back button
        backHomePage.setOnClickListener(v -> {
            Intent myIntent = new Intent(Timer.this, HomePage.class);
            startActivity(myIntent);
        });

        mButtonSet.setOnClickListener(v -> {
            String input = mEditTextInput.getText().toString();
            if (input.length() == 0) {
                Toast.makeText(Timer.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            long millisInput = Long.parseLong(input) * 60000;
            if (millisInput == 0) {
                Toast.makeText(Timer.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                return;
            }

            setTime(millisInput);
            mEditTextInput.setText("");
        });

        mButtonStartPause.setOnClickListener(v -> {
            if (mTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        mButtonFocusMode.setOnClickListener(v -> {
            if (mTimerRunning) {
                pauseTimer();
            } else {
                startFocusTimer();
            }
        });

        mButtonReset.setOnClickListener(v -> resetTimer());
    }

    private void setTime(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        resetTimer();
        closeKeyboard();
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mLockTask = false;
                updateWatchInterface();
            }
        }.start();

        mTimerRunning = true;
        updateWatchInterface();
    }

    private void startFocusTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mIsFocus = true;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mLockTask = false;
                setTaskLock();
                updateWatchInterface();
            }
        }.start();

        mTimerRunning = true;
        mLockTask = true;
        setTaskLock();
        updateWatchInterface();
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateWatchInterface();
    }

    private void resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis;
        stopLockTask();
        updateCountDownText();
        updateWatchInterface();

    }

    private void updateCountDownText() {
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    @SuppressLint("SetTextI18n")
    private void updateWatchInterface() {
        if (mIsFocus){
            if (mTimerRunning) {
                mEditTextInput.setVisibility(View.INVISIBLE);
                mButtonSet.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.INVISIBLE);
                mButtonFocusMode.setVisibility(View.INVISIBLE);
                mButtonStartPause.setText("Pause");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                backHomePage.setVisibility(View.INVISIBLE);

            } else {
                mEditTextInput.setVisibility(View.VISIBLE);
                mButtonSet.setVisibility(View.VISIBLE);
                mButtonFocusMode.setVisibility(View.VISIBLE);
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                backHomePage.setVisibility(View.VISIBLE);

                if (mTimeLeftInMillis < 1000) {
                    mButtonStartPause.setVisibility(View.INVISIBLE);
                } else {
                    mButtonStartPause.setVisibility(View.VISIBLE);
                }

                if (mTimeLeftInMillis < mStartTimeInMillis) {
                    mButtonReset.setVisibility(View.VISIBLE);
                } else {
                    mButtonReset.setVisibility(View.INVISIBLE);
                }
            }
        }else {
            if (mTimerRunning) {
                mEditTextInput.setVisibility(View.INVISIBLE);
                mButtonSet.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.INVISIBLE);
                mButtonFocusMode.setVisibility(View.INVISIBLE);
                mButtonStartPause.setText("Pause");
            } else {
                mEditTextInput.setVisibility(View.VISIBLE);
                mButtonSet.setVisibility(View.VISIBLE);
                mButtonFocusMode.setVisibility(View.VISIBLE);
                mButtonStartPause.setText("Start");

                if (mTimeLeftInMillis < 1000) {
                    mButtonStartPause.setVisibility(View.INVISIBLE);
                } else {
                    mButtonStartPause.setVisibility(View.VISIBLE);
                }

                if (mTimeLeftInMillis < mStartTimeInMillis) {
                    mButtonReset.setVisibility(View.VISIBLE);
                } else {
                    mButtonReset.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void setTaskLock(){
        if (mLockTask){
            Toast.makeText(getApplicationContext(), "Starting lock task mode", Toast.LENGTH_SHORT).show();
            startLockTask();
        } else {
            Toast.makeText(getApplicationContext(), "Stopping lock task mode", Toast.LENGTH_SHORT).show();
            stopLockTask();
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis", mStartTimeInMillis);
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();
        updateWatchInterface();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if (mIsFocus) {
                if (mTimeLeftInMillis < 0) {
                    mTimeLeftInMillis = 0;
                    mTimerRunning = false;
                    updateCountDownText();
                    updateWatchInterface();
                } else {
                    startFocusTimer();
                }
            }else{
                if (mTimeLeftInMillis < 0) {
                    mTimeLeftInMillis = 0;
                    mTimerRunning = false;
                    updateCountDownText();
                    updateWatchInterface();
                } else {
                    startTimer();
                }
            }
        }
    }
}
