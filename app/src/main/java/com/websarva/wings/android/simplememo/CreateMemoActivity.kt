package com.websarva.wings.android.simplememo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.*

class CreateMemoActivity : AppCompatActivity() {

    // MemoOpenHelperクラスを定義
    internal var helper: MemoOpenHelper? = null
    // 新規フラグ
    internal var newFlag = false
    // id
    internal var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_memo)

        // データベースから値を取得する
        if (helper == null) {
            helper = MemoOpenHelper(this@CreateMemoActivity)
        }

        // ListActivityからインテントを取得
        val intent = this.intent
        // 値を取得
        id = intent.getStringExtra("id")
        // 画面に表示
        if (id == "") {
            // 新規作成の場合
            newFlag = true

        } else {
            // 編集の場合 データベースから値を取得して表示
            // データベースを取得する
            val db = helper!!.writableDatabase
            try {
                // rawQueryというSELECT専用メソッドを使用してデータを取得する
                val c = db.rawQuery("select body from MEMO_TABLE where uuid = '$id'", null)
                // Cursorの先頭行があるかどうか確認
                var next = c.moveToFirst()
                // 取得した全ての行を取得
                while (next) {
                    // 取得したカラムの順番(0から始まる)と型を指定してデータを取得する
                    val dispBody = c.getString(0)
                    val body = findViewById<View>(R.id.body) as EditText
                    body.setText(dispBody, TextView.BufferType.NORMAL)
                    next = c.moveToNext()
                }
            } finally {
                // finallyは、tryの中で例外が発生した時でも必ず実行される
                // dbを開いたら確実にclose
                db.close()
            }
        }

        /**
         * 登録ボタン処理
         */
        // idがregisterのボタンを取得
        val registerButton = findViewById<View>(R.id.register) as Button
        // clickイベント追加
        registerButton.setOnClickListener {
            // 入力内容を取得する
            val body = findViewById<View>(R.id.body) as EditText
            val bodyStr = body.text.toString()

            // データベースに保存する
            val db = helper!!.writableDatabase
            try {
                if (newFlag) {
                    // 新規作成の場合
                    // 新しくuuidを発行する
                    id = UUID.randomUUID().toString()
                    // INSERT
                    db.execSQL("insert into MEMO_TABLE(uuid, body) VALUES('$id', '$bodyStr')")
                } else {
                    // UPDATE
                    db.execSQL("update MEMO_TABLE set body = '$bodyStr' where uuid = '$id'")
                }
            } finally {
                // finallyは、tryの中で例外が発生した時でも必ず実行される
                // dbを開いたら確実にclose
                db.close()
            }

            // 保存後に一覧へ戻る
            val intent = Intent(this@CreateMemoActivity, MainActivity::class.java)
            startActivity(intent)
        }


        /**
         * 戻るボタン処理
         */
        // idがbackのボタンを取得
        val backButton = findViewById<View>(R.id.back) as Button
        // clickイベント追加
        backButton.setOnClickListener {
            // 保存せずに一覧へ戻る
            finish()
        }
    }
}
