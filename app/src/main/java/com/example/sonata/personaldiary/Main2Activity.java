package com.example.sonata.personaldiary;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {

    private static final int INSERT_REQUESTCODE = 1;
    private int UPDATE_REQUESTCODE = 2;
    private static final int RESULT_LOAD_IMAGE = 3;
    private static final int RESULT_CUT = 4;
    private int id;
    private Button confirm,cancel;
    private Button UpPictures;
    private ImageView imageView;
    private EditText getTitleId,getTextId;
    private File tempFile;
    private static String changeFromBitmap;
    private static Bitmap changeTobitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getTitleId = (EditText) findViewById(R.id.insert_editTitle);
        getTextId = (EditText) findViewById(R.id.insert_editText);
        this.imageView = (ImageView)findViewById(R.id.imageView);
        confirm = (Button) findViewById(R.id.confirm);
        changeFromBitmap = null;changeTobitmap=null;

        //接收更新要求
        if ((Diary) getIntent().getSerializableExtra("update_diary") != null)
        {
            Diary diary = (Diary) getIntent().getSerializableExtra("update_diary");
            getTitleId.setText(diary.getTitle());
            getTextId.setText(diary.getText());
            id = diary.getId();
            changeFromBitmap = diary.getBitmap();
            changeTobitmap = stringToBitmap(changeFromBitmap);
            System.out.println();
            this.imageView.setImageBitmap(changeTobitmap);

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Diary diary = new Diary();
                    diary.setTitle(getTitleId.getText().toString());
                    diary.setText(getTextId.getText().toString());
                    diary.setData(getTime());
                    diary.setId(id);
                    diary.setBitmap(changeFromBitmap);
                    Intent intent = new Intent();
                    intent.putExtra("update_diary", diary);
                    setResult(UPDATE_REQUESTCODE, intent);
                    finish();
                }
            });

        }
        else {
            //确定创建
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Diary diary = new Diary();
                    diary.setTitle(getTitleId.getText().toString());
                    diary.setText(getTextId.getText().toString());
                    diary.setData(getTime());
                    diary.setBitmap(changeFromBitmap);
                    Intent intent = new Intent();
                    intent.putExtra("insert_diary", diary);
                    setResult(INSERT_REQUESTCODE, intent);
                    finish();

                }
            });
        }

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        UpPictures = (Button) findViewById(R.id.UpPictures);
        UpPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopueWindow();
            }
        });
    }

    private void showPopueWindow()
    {
        View popView = View.inflate(this,R.layout.popuewindow,null);
        Button bt_album = (Button) popView.findViewById(R.id.btn_pop_album);
        Button bt_camera = (Button) popView.findViewById(R.id.btn_pop_camera);
        Button bt_cancel = (Button) popView.findViewById(R.id.btn_pop_cancel);

        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels*1/3;

        final PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setFocusable(true);

        popupWindow.setOutsideTouchable(true);//点击外部popueWindow消失

        //本地获取
        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //激活系统图库，选择一张图片
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                //开启一个带有返回值的Activity
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
                popupWindow.dismiss();
            }
        });

        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //取消
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,50);
    }

    private void crop(Uri uri)
    {
        //裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop","true");

        //裁剪框的比例1：1
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);

        //裁剪后输出图片的尺寸大小
        intent.putExtra("outputX",250);
        intent.putExtra("outputY",250);

        intent.putExtra("outputFormat","JPEG");//图片格式
        intent.putExtra("noFaceDetection",true);//取消人脸识别
        intent.putExtra("return-data",true);

        startActivityForResult(intent,RESULT_CUT);
    }

    //判断sdcard是否被挂载
    private boolean hasSdcard()
    {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        System.out.println(requestCode);
        if(requestCode == RESULT_LOAD_IMAGE)
        {
            if(data!=null)
            {
                Uri uri = data.getData();
                String str = String.valueOf(uri);
                crop(uri);
            }
        }
        else if(requestCode == RESULT_CUT)
        {
            if(data!=null)
            {
                Bitmap bitmap = data.getParcelableExtra("data");
                changeFromBitmap = bitmapToString(bitmap);
                this.imageView.setImageBitmap(bitmap);
            }
            try{
                tempFile.delete();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Log.e("mytest","haha");
        }

    }

    protected Bitmap stringToBitmap(String string){
        //数据库中的String类型转换成Bitmap
        Bitmap bitmap;
        if(string!=null){
            byte[] bytes= Base64.decode(string,Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            return bitmap;
        }
        else {
            return null;
        }
    }

    protected String bitmapToString(Bitmap bitmap) {
        //用户在活动中上传的图片转换成String进行存储
        String string;
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();// 转为byte数组
            string = Base64.encodeToString(bytes, Base64.DEFAULT);
            return string;
        } else {
            return "";
        }
    }

    protected String getTime()
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String time = year + "/" + month + "/" + day;
        return time;
    }


}
