package com.example.guyuchao.myapplication;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.Answer;
import com.bean.RegUser;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.MediaType;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText register_username;
    private EditText register_password;
    private EditText register_confirm_password;
    private TextView mBtnRegister;
    private ImageView Back;
    String url_for_register="http://39.106.168.133:8080/api/user/reg";
    private View mInputLayout;

    private float mWidth, mHeight;
    MsgHandler handler=new MsgHandler(RegisterActivity.this);
    private LinearLayout mName, mPsw,mPswconfirm;
    RegUser regUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register_main);
        initView();
    }

    private void initView() {
        mBtnRegister = (TextView) findViewById(R.id.btn_register);
        mInputLayout = findViewById(R.id.register_input_layout);
        mName = (LinearLayout) findViewById(R.id.register_input_layout_name);
        mPsw = (LinearLayout) findViewById(R.id.register_input_layout_psw);
        mPswconfirm = (LinearLayout) findViewById(R.id.register_confirm_input_layout_psw);
        register_username=(EditText) findViewById(R.id.register_username);
        register_password=(EditText) findViewById(R.id.register_password);
        register_confirm_password=(EditText) findViewById(R.id.register_confirm_password);

        Back=(ImageView)findViewById(R.id.btn_back);
        mBtnRegister.setOnClickListener(this);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        String user=register_username.getText().toString().trim();
        String password=register_password.getText().toString().trim();
        String confirmpassword=register_confirm_password.getText().toString().trim();
        if(!password.equals(confirmpassword)){
            //Toast.makeText(getApplicationContext(),"s",Toast.LENGTH_SHORT);
            handler.sendEmptyMessage(1);
        }
        else {
            regUser = new RegUser(user, password);
            new Thread() {
                public void run() {

                    OkHttpUtils.postString()
                            .url(url_for_register)
                            .mediaType(MediaType.parse("application/json; charset=utf-8"))
                            .content(new Gson().toJson(regUser))
                            .build()
                            .execute(new MyStringCallback());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }.start();
        }
    }

    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
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
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                mInputLayout.setVisibility(View.INVISIBLE);

                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this,LoginActivity.class);
                intent.putExtra("username",register_username.getText().toString().trim());
                intent.putExtra("password",register_password.getText().toString().trim());
                startActivityForResult(intent, 0);

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });

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
                case 2:
                    mWidth = mBtnRegister.getMeasuredWidth();
                    mHeight = mBtnRegister.getMeasuredHeight();

                    mName.setVisibility(View.INVISIBLE);
                    mPsw.setVisibility(View.INVISIBLE);
                    mPswconfirm.setVisibility(View.INVISIBLE);
                    inputAnimator(mInputLayout, mWidth, mHeight);
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "确认密码不正确!", Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    Toast.makeText(getApplicationContext(), "用户名已被使用!", Toast.LENGTH_SHORT).show();
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
                handler.sendEmptyMessage(2);
            }
            else{
                handler.sendEmptyMessage(0);
            }
            //Toast.makeText(getApplicationContext(), answer.getMsg(), Toast.LENGTH_LONG).show();
        }
    }
}
