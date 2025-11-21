package com.wujincheng.zhishaizi;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;


public class MainActivity extends AppCompatActivity implements Logutils.LogListener {
    private static final String TAG = "MainActivity";
    private Button button, auto;
    private ImageView img;
    private TextView textView;
    private TextView mTvLog;
    private int lastNum = 0;
    private MediaPlayer mShaiZiPlayer = null;
    private MediaPlayer mNumberPlayer = null;
    private MediaPlayer mDounleCountPlay = null;
    private MyHandler myHandler;
    private int playCount = 0;
    private StringBuilder sb = new StringBuilder();


    private boolean isAutoPlay = false;

    public int curColor = Color.BLACK;

    DiceNumber diceNumber = DiceNumber.ONE;

/**
 * Method called when a log message is received
 * This method appends the log message to a string buffer and updates the UI if the TextView is available
 *
 * @param log The log message to be processed and displayed
 */
    @Override
    public void onLog(String log) {
    // Append the log message to the string buffer
        sb.append(log);
    // Add a newline character after each log message for better formatting
        sb.append("\n");
    // Check if the TextView for displaying logs is not null
        if (mTvLog != null) {
        // Run the UI update on the main thread to ensure thread safety
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                // Set the TextView's text to the complete log buffer
                    ScrollView scrollView = (ScrollView) mTvLog.getParent();
                    scrollView.fullScroll(View.FOCUS_DOWN);
                    mTvLog.setText(sb.toString());
                }
            });
        }
    }


    enum DiceNumber {
        ONE, TWO, THREE, FOUR, FIVE, SIX
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myHandler = new MyHandler(Looper.getMainLooper());
        initView();
        Logutils.setLogListener(this);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void initView() {
        button = findViewById(R.id.button);
        auto = findViewById(R.id.btn_auto);
        img = findViewById(R.id.imageView);
        textView = findViewById(R.id.number);
        mTvLog = findViewById(R.id.tv_log_show);
        img.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.empty_dice));
        button.setOnClickListener(v -> {
            if (isAutoPlay) {
                myHandler.sendEmptyMessage(2);
            }
            playCount++;
            play();
        });
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isAutoPlay) {
                    isAutoPlay = true;
                    auto.setText(getResources().getText(R.string.stop));
                    myHandler.sendEmptyMessage(1);
                } else {
                    isAutoPlay = false;
                    auto.setText(getResources().getText(R.string.auto));
                    myHandler.sendEmptyMessage(2);
                }
                Logutils.d(TAG, "auto onClick: " + isAutoPlay);
            }
        });
    }

    public void play() {
        playZhiShaiZi();
        int random = new Random().nextInt(6) + 1;
        Logutils.e(TAG, "random(" + playCount + ")  " + random);
        String str = "";
        if (lastNum != random) {
            int sourceID = 0;
            switch (random) {
                case 1:
                    sourceID = R.drawable.dice_1;
                    diceNumber = DiceNumber.ONE;
                    break;
                case 2:
                    sourceID = R.drawable.dice_2;
                    diceNumber = DiceNumber.TWO;
                    break;
                case 3:
                    sourceID = R.drawable.dice_3;
                    diceNumber = DiceNumber.THREE;
                    break;
                case 4:
                    sourceID = R.drawable.dice_4;
                    diceNumber = DiceNumber.FOUR;
                    break;
                case 5:
                    sourceID = R.drawable.dice_5;
                    diceNumber = DiceNumber.FIVE;
                    break;
                case 6:
                    sourceID = R.drawable.dice_6;
                    diceNumber = DiceNumber.SIX;
                    break;
                default:
                    button.setText("error ,please try again");
            }
            str = String.valueOf(random);
            textView.setTextSize(48);
            lastNum = random;
            Drawable drawable = getBaseContext().getResources().getDrawable(sourceID);
            img.setImageDrawable(drawable);
        } else {
            str = "恭喜你 再次掷出和上次一样的数:" + lastNum;
            playDouble();
            textView.setTextSize(24);
        }
        playNumber(diceNumber);
        curColor = getRandomRGBColor();
        textView.setTextColor(curColor);
        if (isAutoPlay) {
            auto.setTextColor(curColor);
        } else {
            button.setTextColor(Color.BLACK);
        }

        textView.setText(str);
    }

    public void playZhiShaiZi() {
        mShaiZiPlayer = MediaPlayer.create(this, R.raw.zhishaizi);
        mShaiZiPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        mShaiZiPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }

    public void playDouble() {
        mDounleCountPlay = MediaPlayer.create(this, R.raw.double_count);

        mDounleCountPlay.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        mDounleCountPlay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }

    public void playNumber(DiceNumber number) {
        if (mNumberPlayer != null && mNumberPlayer.isPlaying()) {
            mNumberPlayer.stop();
        }
        if (mNumberPlayer != null) {
            if (mNumberPlayer.isPlaying()) {
                mNumberPlayer.stop();
            }
            mNumberPlayer = null;

        }
        switch (number) {
            case ONE:
                mNumberPlayer = MediaPlayer.create(this, R.raw.number1);
                break;
            case TWO:
                mNumberPlayer = MediaPlayer.create(this, R.raw.number2);
                break;
            case THREE:
                mNumberPlayer = MediaPlayer.create(this, R.raw.number3);
                break;
            case FOUR:
                mNumberPlayer = MediaPlayer.create(this, R.raw.number4);
                break;
            case FIVE:
                mNumberPlayer = MediaPlayer.create(this, R.raw.number5);
                break;
            case SIX:
                mNumberPlayer = MediaPlayer.create(this, R.raw.number6);
                break;
        }
        mNumberPlayer.start();

    }


    public static int getRandomRGBColor() {
        // 红、绿、蓝三色随机取值（0-255）
        int red = new Random().nextInt(256);
        int green = new Random().nextInt(256);
        int blue = new Random().nextInt(256);
        // 组合为ARGB格式（0xFF开头表示不透明）
        return Color.rgb(red, green, blue);
    }


    class MyHandler extends android.os.Handler {

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    play();
                    if (button.isClickable()) {
                        button.setClickable(false);
                    }
                    sendEmptyMessageDelayed(1, 3000);
                    break;
                case 2:
                    if (hasMessages(1)) {
                        if (!button.isClickable()) {
                            button.setClickable(true);
                        }
                        removeMessages(1);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mShaiZiPlayer != null) {
            mShaiZiPlayer.release();
        }
        if (mDounleCountPlay != null) {
            mDounleCountPlay.release();
        }
        if (mNumberPlayer != null) {
            mNumberPlayer.release();
        }
        playCount = 0;
    }
}