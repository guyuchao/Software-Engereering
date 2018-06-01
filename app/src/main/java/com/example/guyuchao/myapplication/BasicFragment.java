package com.example.guyuchao.myapplication;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.Answer_token;
import com.bean.AudioAnswer;
import com.bean.Nearest;
import com.bean.UploadAnswer;
import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.MediaType;


/**
 * A simple {@link Fragment} subclass.
 */
public class BasicFragment extends Fragment {
    private String url_for_basic_info = "http://39.106.168.133:8080/api/museum/";
    private String url_for_audio = "http://39.106.168.133:8080/api/audio/6";
    private String getUrl_for_upload="http://39.106.168.133:8080/api/audio/add";
    private String username;
    private String userid;
    private String museumid;

    private Nearest museuminfo;
    private TextView basicinfo;
    private TextView open_time;
    private TextView education;
    private TextView academic;

    private TextView play;
    private TextView pause;
    private TextView upload;
    /*audio*///////////////////
    private MediaPlayer mediaPlayer =null;

    private ProgressDialog progressDialog;  //上传进度提示框
    private boolean isProgressCancel;  //网络请求过程中是否取消上传或下载
    private UpProgressHandler upProgressHandler;  //七牛SDK的上传进度监听
    private UploadManager uploadManager;  //七牛SDK的上传管理者
    private UploadOptions uploadOptions;  //七牛SDK的上传选项
    private MyUpCompletionHandler mHandler;  //七牛SDK的上传返回监听
    private UpCancellationSignal upCancellationSignal;  //七牛SDK的上传过程取消监听
    private final static String TOKEN_URL = "http://39.106.168.133:8080/api/getqiniutoken";  //服务器请求token的网址
    private String uptoken;  //服务器请求Token值
    private String upKey;  //上传文件的Key值
    private String upLoadData;  //上传的文件路径
    String url_for_music = "http://p6m0gir2c.bkt.clouddn.com/";
    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。
    private int currentPosition;//当前音乐播放的进度
    private SeekBar seekBar;
    private Timer timer;
    private AudioAnswer audioanswer;
    private String playingURL;
    /////////////////////////////


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basic, container, false);
        basicinfo = view.findViewById(R.id.basic_info);
        open_time = view.findViewById(R.id.open_time);
        education = view.findViewById(R.id.education);
        academic = view.findViewById(R.id.academic);

        //播放
        play = view.findViewById(R.id.play);
        pause = view.findViewById(R.id.pause);
        upload = view.findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*"); //选择音频
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //Toast.makeText(getActivity().getApplicationContext(), "dasdsa", Toast.LENGTH_LONG).show();

                startActivityForResult(intent, 1);

            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mediaPlayer.isPlaying())
                {
                    mediaPlayer.start();
                }
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                }
            }
        });
        seekBar = view.findViewById(R.id.playSeekBar);
        seekBar.setOnSeekBarChangeListener(new MySeekBar());

        Bundle bundle = getArguments();
        userid=bundle.getString("userid");
        username = bundle.getString("username");
        museumid = bundle.getString("museumid");
        userid=bundle.getString("userid");
        url_for_basic_info += museumid;

        OkHttpUtils.get()
                .url(url_for_basic_info)
                .build()
                .execute(new MyStringCallback());
        OkHttpUtils.get()
                .url(url_for_audio)
                .build()
                .execute(new MyAudioCallback());

        initProgressBar();
        initData();

        return view;
    }
    private void initProgressBar() {
        progressDialog = new ProgressDialog(getActivity().getApplicationContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("进度提示");
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isProgressCancel = true;
            }
        });
    }

    private void initData() {
        getTokenFromService();
        uploadManager = new UploadManager();
        upProgressHandler = new UpProgressHandler() {
            /**
             * @param key 上传时的upKey；
             * @param percent 上传进度；
             */
            @Override
            public void progress(String key, double percent) {
                progressDialog.setProgress((int) (10 * percent));
            }
        };
        upCancellationSignal = new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return isProgressCancel;
            }
        };
        //定义数据或文件上传时的可选项
        uploadOptions = new UploadOptions(
                null,  //扩展参数，以<code>x:</code>开头的用户自定义参数
                "",  //指定上传文件的MimeType
                true,  //是否启用上传内容crc32校验
                upProgressHandler,  //上传内容进度处理
                upCancellationSignal  //取消上传信号
        );
        mHandler = new MyUpCompletionHandler();
    }

    ////////////////////////////////////////////////////

    public class MyUpCompletionHandler implements UpCompletionHandler {
        /**
         * @param key      上传时的upKey；
         * @param info     Json串表示的上传信息，包括使用版本，请求状态，请求Id等信息；
         * @param response Json串表示的文件信息，包括文件Hash码，文件Mime类型，文件大小等信息；
         */
        @Override
        public void complete(String key, ResponseInfo info, JSONObject response) {
            //Toast.makeText(getActivity().getApplicationContext(), userid, Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity().getApplicationContext(),"上传成功，请等待审核！",Toast.LENGTH_LONG).show();

            progressDialog.dismiss();
            url_for_music=url_for_music+key;
            UploadAnswer uploadAnswer=new UploadAnswer(userid,museumid,url_for_music,"i love wlj");
            /*
            OkHttpUtils.postString()
                    .url(getUrl_for_upload)
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(new Gson().toJson(uploadAnswer))
                    .build()
                    .execute(new MyUploadCallback());*/
        }
    }
    ////////////////////////////////////////////////////////////////////
    private void initMediaPlayer() {

        //获取mp3文件的路径
        try {
            mediaPlayer=new MediaPlayer();

            mediaPlayer.setDataSource(playingURL); //为播放器设置mp3文件的路径
            mediaPlayer.prepareAsync(); //做好准备
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(!isSeekBarChanging){
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            }
                        }
                    },0,50);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////
    private void getTokenFromService() {
        //模拟从服务端获取uptoken
        OkHttpUtils.post()
                .url(TOKEN_URL)
                .build()
                .execute(new MyTokenCallback());
    }

    public class MyTokenCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onResponse(String response, int id) {
            Answer_token tokenAnswer = new Gson().fromJson(response, Answer_token.class);
            uptoken = tokenAnswer.getToken();
            //Toast.makeText(getActivity().getApplicationContext(), tokenAnswer.getToken(), Toast.LENGTH_LONG).show();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onResponse(String response, int id) {
            museuminfo = new Gson().fromJson(response, Nearest.class);
            basicinfo.setText(museuminfo.getIntroduce());
            open_time.setText(museuminfo.getOpen_time());
            academic.setText(museuminfo.getAcademic());
            education.setText(museuminfo.getEdu_activity());
        }
    }
    public class MyUploadCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onResponse(String response, int id) {
            Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_LONG).show();

        }
    }

    public class MyAudioCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        @Override
        public void onResponse(String response, int id) {
            audioanswer = new Gson().fromJson(response, AudioAnswer.class);
            playingURL=audioanswer.getAddr();
            //Toast.makeText(getActivity().getApplicationContext(), playingURL, Toast.LENGTH_LONG).show();

            initMediaPlayer();
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }

        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            mediaPlayer.seekTo(seekBar.getProgress());
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path;
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                path = uri.getPath();
                education.setText(path);
                upLoadData = path;
                upKey=path;
                Toast.makeText(getActivity().getApplicationContext(),"上传成功，请等待审核！",Toast.LENGTH_LONG).show();

                //uploadManager.put(upLoadData, upKey, uptoken, mHandler, uploadOptions);
                //return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后

                path = getPath(getActivity().getApplicationContext(), uri);
                education.setText(path);
                upLoadData = path;
                upKey=path;
                Toast.makeText(getActivity().getApplicationContext(),"上传成功，请等待审核！",Toast.LENGTH_LONG).show();

                //uploadManager.put(upLoadData, upKey, uptoken, mHandler, uploadOptions);

                //uploadManager.put(upLoadData, upKey, uptoken, mHandler, uploadOptions);
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
                education.setText(path);
                upLoadData = path;
                upKey=path;
                //uploadManager.put(upLoadData, upKey, uptoken, mHandler, uploadOptions);

            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////