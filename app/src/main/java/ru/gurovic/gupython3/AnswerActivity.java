package ru.gurovic.gupython3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class AnswerActivity extends ActionBarActivity implements OnClickListener {

    Button btnEasy, btnMedium, btnDifficult;
    DBHelper dbHelper;
    SQLiteDatabase db;
    Long question_id;
    Integer delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        btnEasy = (Button) findViewById(R.id.button4);
        btnEasy.setOnClickListener(this);
        btnMedium = (Button) findViewById(R.id.button3);
        btnMedium.setOnClickListener(this);
        btnDifficult = (Button) findViewById(R.id.button2);
        btnDifficult.setOnClickListener(this);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        Intent intent = getIntent();

        // answer is always non-empty
        WebView answer_text;
        answer_text = (WebView) findViewById(R.id.answer);
        answer_text.loadDataWithBaseURL(null, intent.getStringExtra("answer"), "text/html", "utf-8", null);

        WebView question_text;
        question_text = (WebView) findViewById(R.id.question);
        question_text.loadDataWithBaseURL(null, intent.getStringExtra("question"), "text/html", "utf-8", null);


        WebView comment_text;
        comment_text = (WebView) findViewById(R.id.comment);
        if (intent.getStringExtra("comment")!=null) {
            comment_text.loadDataWithBaseURL(null, intent.getStringExtra("comment"), "text/html", "utf-8", null);
        } else {
            findViewById(R.id.comment_title).setVisibility(View.INVISIBLE);
        }

        question_id = intent.getLongExtra("question_id", 0);

        db.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button4:
                // кнопка EASY
                delay = 1000 * 60 * 60 * 30;
                break;
            case R.id.button3:
                // кнопка Medium
                delay = 1000 * 60 * 60 * 1;
                break;
            case R.id.button2:
                // кнопка Difficult
                delay = 0;
                break;
        }

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE nexttime SET TIME = " +
                ((new java.util.Date()).getTime() + delay) + " WHERE QUESTION_ID=" + question_id.toString(), new Object[]{});
        db.close();
        Intent intent = new Intent(this, QuestionActivity.class);
        startActivity(intent);

    }

    class DBHelper extends SQLiteAssetHelper {

        // TODO refactor разобраться, как не дублировать этот класс в двух файлах
        private static final String DATABASE_NAME = "guPython3-v0.1";
        private static final int DATABASE_VERSION = 9;

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

    }


}
