package com.example.teamproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import kotlinx.android.synthetic.main.login_layout.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.text.Typography.quote


class login: AppCompatActivity() {

    var db:Firestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        init()

        // 해당 부분 스레드로 구현 필요

    }

    fun init() {
        login.setOnClickListener {
            // 로그인 버튼
            val id = identifier.text.toString()
            val pw = password.text.toString()
            // 이부분에 if문으로 파이어폭스 접근
            //아이디 확인

            db = Firestore.create(applicationContext)
            getDB(id,pw)
        }

        join.setOnClickListener {
            var i = Intent(this,JoinActivity::class.java)
            startActivity(i)
        }
    }

    override fun onBackPressed() {
//        finishAffinity()
//        System.runFinalization()
//        System.exit(0)
        // 해당 방법을 쓸려했더니, 화면이 까매지면서 강종되는 느낌이 강함
        // so, 방법을 바꾸려고 함.
        // 현재 로그인 창은 스플래쉬로 띄워진 상태
        // 따라서 로그인에서 뒤로버튼을 누르면 메인포함, 전부 없애야 함

        Log.e("login","로그인창 종료")
        Out()
//        super.onBackPressed()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        // 로딩창에서 가능한 정보를 모두 불러올 거고, 정보를 받는 경우, 이쪽에서 결정
//        if (requestCode == code) {
//            if (resultCode == Activity.RESULT_OK) { // 결과로 보내준 상태가 OK 코드면
//                val id = data?.getStringExtra("id") //값을 받아옴
//                val pw = data?.getStringExtra("pw")
//                Log.e("login","$id and $pw")
//                In(id!!) // 받아온 정보를 기반으로 자동로그인
//            }
//            else{
//                Log.e("login","need login")
//            }
//        }
//    }

    fun In(i_id:String){ //정보넘기고 종료함수
        val s = Intent()
        s.putExtra("id", i_id)
        setResult(Activity.RESULT_OK,s)
        finish()
    }

    fun Out(){ // 로그인 거부 시
        val s = Intent()
        setResult(Activity.RESULT_CANCELED,s)
        finish()
    }

    fun getDB(_id: String,_pw:String) {
        Log.e("login", "DB 접속시도")

        Log.e("login", "DB 객체 생성")
        if (db != null) {
            Log.e("login", "NULL은 아님")


            db!!.db!!.collection("User")
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(value: QuerySnapshot?, e: FirebaseFirestoreException?) {

                        if (e != null) {
                            Log.e("database", "database Listen failed.")
                            return
                        }
                        val count = value?.size()
                        var flag = false
                        // list.clear()//일딴 초기화 해줘야 한다. 안 그럼 기존 데이터에 반복해서 뒤에 추가된다.
                        if (value != null) {
                            for (doc in value) {
                                Log.e("database", "$doc 읽는 중")
                                if (doc.get("u_id").toString() == _id) {
                                    if (doc.get("u_pw").toString() == _pw) {
                                        Log.e("database", "해당 데이터 존재합니다")
                                        flag = true
                                        break
                                    }
                                }
                            }
                        } //value!=null

                        if(flag) { // id,pw 확인해서 맞으면
                            Log.e("login","해당 계정 있음")
                            var sqlite = SQLite(this@login,"Login")
                            sqlite.openDatabase("USER")
                            sqlite.insertData(_id,_pw)
                            In(_id) // 있다면 정보를 넘기고 종료
                        }else{ // 아이디가 없으면 다시 시도
                            Log.e("login","해당 계정 없음")
                            val space= findViewById<EditText>(R.id.password)
                            space.setText(null) // 비밀번호 칸은 초기화
                        }
                    }
                })
        }
    }
}