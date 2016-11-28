package com.team.witkers.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hys.mylog.MyLog;

import com.lyft.android.scissors.CropView;
import com.team.witkers.R;
import com.team.witkers.eventbus.CropAvatarEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;
import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static android.graphics.Bitmap.CompressFormat.JPEG;
import static android.graphics.Bitmap.CompressFormat.WEBP;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

/**
 * Created by hys on 2016/7/18.
 */

public class CropActivity extends AppCompatActivity implements View.OnClickListener {
    private CropView crop_view;
//    private ImageView iv_photo,iv_crop_use;
    private TextView tv_crop_use;
    private Bitmap mBitmap;
    private Toolbar cropToolbar;

    private static final float[] ASPECT_RATIOS = { 0f, 1f, 6f/4f, 16f/9f };
    private static final String[] ASPECT_LABELS = { "\u00D8", "1:1", "6:4", "16:9" };
    CompositeSubscription subscriptions = new CompositeSubscription();
    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_crop_2);

        Uri uri=getIntent().getParcelableExtra("uri");

        try {
             mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initView();
        initToolBar();
        initClickListener();
    }
    private void initToolBar(){
        cropToolbar.setTitle("");
        setSupportActionBar(cropToolbar);
    }

    private void initView(){
        cropToolbar=(Toolbar)findViewById(R.id.cropToolbar);
        crop_view= (CropView) findViewById(R.id.crop_view);
        tv_crop_use= (TextView) findViewById(R.id.tv_crop_use);

        if(mBitmap==null){
            Toast.makeText(this,"图片错误",Toast.LENGTH_SHORT).show();
        }
        crop_view.setImageBitmap(mBitmap);

        ObjectAnimator viewportRatioAnimator = ObjectAnimator.ofFloat(crop_view, "viewportRatio", 0.75f, 1.0f)
                .setDuration(2);
        autoCancel(viewportRatioAnimator);
        viewportRatioAnimator.addListener(animatorListener);
        viewportRatioAnimator.start();
    }

    private void initClickListener(){
        tv_crop_use.setOnClickListener(this);
    }

    //裁剪按钮操作
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_crop_use:
//                Toast.makeText(this,"use_btn",Toast.LENGTH_SHORT).show();
//                MyLog.i("cropClick");
                final File croppedFile = new File(getCacheDir(), "cropped.webp");

                Observable<Void> onSave = Observable.from(crop_view.extensions()
                        .crop()
                        .quality(40)
                        .format(WEBP)
                        .into(croppedFile))
                        .subscribeOn(io())
                        .observeOn(mainThread());

                subscriptions.add(onSave
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void nothing) {
                                MyLog.d("subscribe_call");

                                final BmobFile bFile=new BmobFile(croppedFile);
                                bFile.uploadblock(new UploadFileListener() {
                                    @Override
                                    public void done(BmobException e) {
//                                        MyLog.v("done");
                                        if (e == null) {
                                            //bmobFile.getFileUrl()--返回的上传文件的完整地址
                                            MyLog.i("上传文件成功:" + bFile.getFileUrl());
                                            EventBus.getDefault().post(new CropAvatarEvent(croppedFile,bFile.getFileUrl()));

                                        } else {
                                            MyLog.e("上传文件失败：" + e.getMessage());
                                        }
                                    }
                                });
                                finish();
                            }
                        }));
                break;
        }//switch

    }//onCLick

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这是navigationIcon的点击事件,即返回按钮back2_icon
        if(item.getItemId()==android.R.id.home){
            finish();
        }
//        //使用按钮
//        if(item.getItemId()==R.id.menu_crop_item_use){
//            Toast.makeText(this,"使用啊",Toast.LENGTH_SHORT).show();
//        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_crop, menu);
//
////        MenuItem item = menu.findItem(R.id.menu_crop_item_use);
//        return true;
//    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    static void autoCancel(ObjectAnimator objectAnimator) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            objectAnimator.setAutoCancel(true);
        }
    }
}//CropActivity_cls
