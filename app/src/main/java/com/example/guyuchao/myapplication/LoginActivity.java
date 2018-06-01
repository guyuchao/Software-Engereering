package com.example.guyuchao.myapplication;

import android.Manifest;
import android.content.Intent;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.Answer;
import com.bean.LoginUser;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.MediaType;


public class LoginActivity extends Activity implements OnClickListener{
    private EditText login_username;
    private EditText login_password;
    private String userid;
    private TextView mBtnLogin;
    private TextView Register;
    private View progress;
    String url_for_login="http://39.106.168.133:8080/api/user/login";
    private View mInputLayout;

    private float mWidth, mHeight;
    MsgHandler handler=new MsgHandler(LoginActivity.this);
    private LinearLayout mName, mPsw;
    LoginUser loginUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_main);
        initView();
        showContacts();
    }

    private void initView() {
        mBtnLogin = (TextView) findViewById(R.id.main_btn_login);
        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        mName = (LinearLayout) findViewById(R.id.input_layout_name);
        mPsw = (LinearLayout) findViewById(R.id.input_layout_psw);
        login_username=(EditText) findViewById(R.id.username);
        login_password=(EditText) findViewById(R.id.password);
        Intent intent=getIntent();
        if(intent.hasExtra("username")&&intent.hasExtra("password")){
            login_username.setText(intent.getStringExtra("username"));
            login_password.setText(intent.getStringExtra("password"));
        }
        Register=(TextView)findViewById(R.id.registry);
        mBtnLogin.setOnClickListener(this);
        Register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        String user=login_username.getText().toString().trim();
        String password=login_password.getText().toString().trim();
        loginUser=new LoginUser(user,password);
        new Thread(){
            public void run(){

                OkHttpUtils.postString()
                        .url(url_for_login)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .content(new Gson().toJson(loginUser))
                        .build()
                        .execute(new MyStringCallback());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }/*

                    OkHttpUtils.get()
                            .url("https://www.baidu.com")
                            .build()
                            .execute(new MyStringCallback());*/

            }
        }.start();
    }

    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 1f, 0.5f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);


                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, HomeActivity.class);
                intent.putExtra("username",login_username.getText().toString().trim());
                intent.putExtra("userid",userid);
                startActivityForResult(intent, 0);

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });

    }

    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(1000);
        animator3.setInterpolator(new GycInterpolator());
        animator3.start();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == 0 && resultCode == RESULT_OK) {
            finish();
        }
    }
    class MsgHandler extends Handler {
        private Activity activity;

        public MsgHandler(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            switch (msg.what){
                case 1:
                    mWidth = mBtnLogin.getMeasuredWidth();
                    mHeight = mBtnLogin.getMeasuredHeight();

                    mName.setVisibility(View.INVISIBLE);
                    mPsw.setVisibility(View.INVISIBLE);
                    inputAnimator(mInputLayout, mWidth, mHeight);

                    break;
                case 0:
                    Toast.makeText(getApplicationContext(), "用户名和密码错误！", Toast.LENGTH_SHORT).show();
                    break;

            }

            super.handleMessage(msg);
        }
    }
    public class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            //handler.sendEmptyMessage(1);
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onResponse(String response, int id) {

            Answer answer=new Gson().fromJson(response,Answer.class);
            if (Integer.parseInt(answer.getValid())==1){
                userid=answer.getId();
                //Toast.makeText(getApplicationContext(),userid,Toast.LENGTH_LONG).show();
                handler.sendEmptyMessage(1);
            }
            else{
                handler.sendEmptyMessage(0);
            }

            //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), answer.getMsg(), Toast.LENGTH_LONG).show();
        }
    }
    public void showContacts(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 0);
        }
    }

}
