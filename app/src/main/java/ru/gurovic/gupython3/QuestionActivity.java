package ru.gurovic.gupython3;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.webkit.WebView;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

// TODO Настроить google analytics:    https://developers.google.com/analytics/devguides/collection/android/v4/campaigns
// TODO feature оповещения о появлении вопросов
// TODO bug разобраться с задержкой отображения на странице ответов (парсинг HTML?)
// TODO enhancement отключить хардварную кнопку Back???
// TODO critical bug 4'' landscape - помещается только 11 строк - проблемы с вопросом 38 - и он может закрывать кнопки собой
// TODO feature ссылки на теорию и видео по теме вопроса
// TODO enhancement на странице ответа вопрос слишком бросается в глаза - затемнить фон?
// TODO design отступ перед словами Ответ и Комментарий
// TODO разобраться с подписью под иконкой (названием приложения) - сейчас Как это работает?


public class QuestionActivity extends ActionBarActivity implements OnClickListener {

    Button btnAnswer;

    DBHelper dbHelper;
    SQLiteDatabase db;
    String answer, question, comment;
    Long question_id;
    Long next_time, current_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        btnAnswer = (Button) findViewById(R.id.button);
        btnAnswer.setOnClickListener(this);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        Cursor data;
        data = db.rawQuery("SELECT questions._id, question, answer, comment, time from questions, nexttime where question_id = questions._id" +
                           " order by time", null);
        data.moveToFirst();

        WebView question_text;
        question_text = (WebView) findViewById(R.id.question);
        next_time = Long.parseLong(data.getString(4));
        current_time = (new java.util.Date()).getTime();
        if (next_time <= current_time) {
            question = data.getString(1);
            question_text.loadData(question, "text/html", "utf-8");
            answer = data.getString(2);
            comment = data.getString(3);
            question_id = data.getLong(0);
        } else {
            question_text.loadData("XXX", "text/html", "utf-8");
            btnAnswer.setVisibility(View.GONE);
        }
        db.close();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("METHOD", "OnResume");
        btnAnswer = (Button) findViewById(R.id.button);
        btnAnswer.setOnClickListener(this);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        Cursor data;
        data = db.rawQuery("SELECT questions._id, question, answer, time from questions, nexttime where question_id = questions._id" +
                " order by time", null);
        data.moveToFirst();

        WebView question_text;
        question_text = (WebView) findViewById(R.id.question);

        next_time = Long.parseLong(data.getString(3));
        current_time = (new java.util.Date()).getTime();
        if (next_time <= current_time) {
            btnAnswer.setVisibility(View.VISIBLE);
            question_text.loadDataWithBaseURL(null, data.getString(1), "text/html", "utf-8", null);
            answer = data.getString(2);
            question_id = data.getLong(0);
        } else {
            question_text.loadDataWithBaseURL(null, getString(R.string.no_questions), "text/html", "utf-8", null);
            btnAnswer.setVisibility(View.GONE);
        }
        db.close();

    }

    @Override
    public void onClick(View v) {
        Log.d("METHOD", "Question-onClick");
        Intent intent = new Intent(this, AnswerActivity.class);
        intent.putExtra("answer", answer);
        intent.putExtra("question_id", question_id);
        intent.putExtra("question", question);
        intent.putExtra("comment", comment);
        startActivity(intent);
    }


    class DBHelper extends SQLiteAssetHelper {

        private static final String DATABASE_NAME = "guPython3-v0.1";
        private static final int DATABASE_VERSION = 9;

        public DBHelper(Context context) {

            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            SQLiteDatabase db = this.getWritableDatabase();
            // добавляем в nexttime начальные записи, время = id вопроса
            updateNexttime(db);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Таблицу questions очищаем и заполняем по-новой,
            // для этого в assets/databases кладем guPython3-v0.1_upgrade_1-X.sql (X - новая версия)
            // Исправление одинарных кавычек на двойные:
            // python3.4 quotes_sqlite.py </Users/vladimirgurovic/questions.sql >/Users/vladimirgurovic/AndroidStudioProjects/guPython3/app/src/main/assets/databases/guPython3-v0.1_upgrade_1-X.sql

            super.onUpgrade(db, 1, newVersion); // oldVersion = 1 для удобства обновления: для любой версии обновление работает одинаково

            // добавляем в nexttime отсутствующие записи
            updateNexttime(db);

            Log.d("UPGRADE", Integer.toString(oldVersion) + " -> " + Integer.toString(newVersion));

        }

        private void updateNexttime(SQLiteDatabase db) {
            Cursor data;
            data = db.rawQuery("SELECT questions._id from questions left join nexttime on questions._id=nexttime.question_id " +
                               "where nexttime.question_id is NULL", null);
            if (data.getCount() > 0) {
                data.moveToFirst();
                do {
                    Long id = data.getLong(0);
                    db.execSQL("INSERT INTO nexttime VALUES(" + id.toString() +"," + id.toString() +"," + id.toString() + ");");
                } while (data.moveToNext());
            }
        }
    }

}
