package com.team.witkers.activity.find;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.adapter.ImageListAdapter;
import com.team.witkers.base.BaseActivity2;
import com.team.witkers.bean.MyUser;
import com.team.witkers.bean.TendItems;
import com.team.witkers.eventbus.TendEvent;
import com.team.witkers.utils.MyToast;
import com.team.witkers.utils.TimeUtils2;
import com.team.witkers.utils.liteasysuits.SimpleTask;
import com.team.witkers.utils.liteasysuits.TaskExecutor;
import com.team.witkers.utils.lubancompress.Luban2;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * Created by hys on 2016/8/10.
 */
public class AddTendActivity extends BaseActivity2 {
    private Toolbar toolbar;
    private EditText et_input;
    private TextView tv_input_length;
    private ImageView iv_addImg;
    private GridView gridView;
    private int length;
    private boolean isVisibleInput = true;
    private MyUser myUser;
    private static final int INPUT_LIMITED_LENGTH = 200;
    private ImageButton ib_camera, ib_photo, ib_emoj;
    private ImageListAdapter mAdapter;
    private static final int REQUESTCODE_MULTISELECT = 1;
    private ArrayList<String> imgList = new ArrayList<>();
    private ArrayList<String> compressFileList=new ArrayList<>();
    private  ProgressDialog mDialog;
    private String fileNamePrefix ;
    private List<String> picUrlList0=new ArrayList<>();

    private int ii=0;
    @Override
    protected int setContentId() {
        return R.layout.activity_find_addtend;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    @Override
    protected void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        et_input = (EditText) findViewById(R.id.et_input);
        tv_input_length = (TextView) findViewById(R.id.tv_input_length);
//        ib_camera = (ImageButton) findViewById(R.id.ib_camera);
        ib_photo = (ImageButton) findViewById(R.id.ib_photo);
        ib_emoj = (ImageButton) findViewById(R.id.ib_emoj);
        iv_addImg = (ImageView) findViewById(R.id.iv_addImg);
        gridView = (GridView) findViewById(R.id.gridView);
    }

    @Override
    protected void setListener() {
        et_input.setOnClickListener(this);

        ib_photo.setOnClickListener(this);
        ib_emoj.setOnClickListener(this);
        // 监听内容输入区，动态显示剩余字数
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

//                MyLog.i("input length_"+s.length());
                length = INPUT_LIMITED_LENGTH - s.length();
                tv_input_length.setText("" + length);

            }
        });
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("编辑");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tend_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这是navigationIcon的点击事件,即返回按钮
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        if (item.getItemId() == R.id.menu_tend_publish) {
            if (et_input.getText().toString().trim().equals(""))
                MyToast.showToast(this, "请输入内容或添加图片");
            else
                uploadTend0();
        }
        return super.onOptionsItemSelected(item);
    }//onOptionsSelected

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_input:
                if (isVisibleInput) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    isVisibleInput = false;
                }
                break;

            case R.id.ib_photo:
                // start multiple photos selector
                Intent intent = new Intent(this, ImagesSelectorActivity.class);
                // max number of images to be selected
                intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 9);
                // min size of image which will be shown; to filter tiny images (mainly icons)
                intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);
                // show camera or not
                intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true);
                // pass current selected images as the initial value
                intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, imgList);
                // start the selector
                startActivityForResult(intent, REQUESTCODE_MULTISELECT);

                break;
            case R.id.ib_emoj:

                break;
        }//switch
    }//onCLick


    private void uploadTend0(){
        mDialog = new ProgressDialog(this, "正在上传中...");
        mDialog.show();
        myUser = BmobUser.getCurrentUser(MyUser.class);
        if(imgList.size()==0){//无图片上传
            uploadTendNoPic();
        }else{//有图片

            //先要将图片文件上传，得到 网址字符串
            // imgList装载着选中的图片数
            MyLog.d("imgList_size_ "+imgList.size());
            fileNamePrefix=myUser.getUsername()+"-tend";
            //将多张图片压缩，填至 compressFileList中
            testOrderedTaskExecutor();
        }//else  有图片
    }//upload0

    //异步任务 顺序处理
    private void testOrderedTaskExecutor(){
        final long startTime = System.currentTimeMillis();
        SimpleTask<String> lastTask = new SimpleTask<String>() {
            @Override
            protected String doInBackground() {//异步任务进行中。。（随便返回字符串）
//                得到压缩文件集合，上传至Bmob
                return "destination_ ";
            }

            @Override
            protected void onPostExecute(String result) {//异步任务完成后
                MyLog.i("压缩用时: "
                        + (System.currentTimeMillis() - startTime) );

                //将文件地址list  转换为相应数组
                final String[] arry=new String [compressFileList.size()];
                compressFileList.toArray(arry);

                BmobFile.uploadBatch(arry, new UploadBatchListener() {
                    @Override
                    public void onSuccess(List<BmobFile> files,List<String> urls) {
                        //1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
                        //2、urls-上传文件的完整url地址
                        if(urls.size()==arry.length){//如果数量相等，则代表文件全部上传完成
                            //do something
                            picUrlList0=urls;
                            uploadTendNoPic();
//                            MyLog.v("picUrls0_ "+picUrlList0.get(0));
                        }
                    }//onSuccess

                    @Override
                    public void onProgress(int curIndex, int curPercent, int total,int totalPercent) {
                        //1、curIndex--表示当前第几个文件正在上传
                        //2、curPercent--表示当前上传文件的进度值（百分比）
                        //3、total--表示总的上传文件数
                        //4、totalPercent--表示总的上传进度（百分比）

                        MyLog.i("上传文件数及总进度_ "+curIndex+"(( "+totalPercent);
                    }

                    @Override
                    public void onError(int statuscode, String errormsg) {
                        MyLog.e("错误码"+statuscode +",错误描述："+errormsg);
                    }
                });

            }//onPost
        };

        TaskExecutor.OrderedTaskExecutor orderedTaskExecutor=TaskExecutor.newOrderedExecutor();
        for(int i=0;i<imgList.size();i++){
            SimpleTask<?> task = getCompressTask(imgList.get(i),i);
            orderedTaskExecutor.put(task);
        }
        orderedTaskExecutor.put(lastTask).start();
    }

    private SimpleTask<String> getCompressTask(final String filePath, final int id) {
        SimpleTask<String> task = new SimpleTask<String>() {

            @Override
            protected String doInBackground() {
                String dateStr = TimeUtils2.longToString(System.currentTimeMillis(), TimeUtils2.FORMAT_TIME_SECOND);
//            MyLog.e(dateStr);
                String fileName1 = fileNamePrefix +"("+ii+")" +dateStr;
                ii++;

//                MyLog.v("oldfile_ "+filePath);
                File file=new File(filePath);
                File newFile2= Luban2.get(AddTendActivity.this)
                        .load(file)
                        .setFileName(fileName1)
                        .thirdCompress();

                String newfileStr=newFile2.getAbsolutePath();
                return newfileStr;
            }

            @Override
            protected void onCancelled() {
                MyLog.i("SimpleTask onCancelled : ");
            }

            @Override
            protected void onPostExecute(String  newfilePath) {
                compressFileList.add(newfilePath);
//                MyLog.i(" finish ( "+newfilePath);
            }
        };
        return task;
    }
    //上传自己的动态信息
    private void uploadTendNoPic() {
        MyLog.i("uploadTend");
        TendItems tend = new TendItems();

//        默认当前 用户不为 null
        String userName = myUser.getUsername();
        String headUrl = myUser.getHeadUrl();
//            MyLog.v("myUser_name/head"+userName+"//"+headUrl);

        tend.setPubUser(myUser);
        tend.setFriendName(userName);
        tend.setFriendHeadUrl(headUrl);

        tend.setContent(et_input.getText().toString());
//        tend.setPicUrl("http://oac7bvp34.bkt.clouddn.com/photo.png");

//        List<TendComments> list=new ArrayList<>();
//        TendComments comment=new TendComments();
//        comment.setCommentContent("我那个去");
//        comment.setCommentUserName("hhhh");
//        comment.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                if(e==null){
////                    BmobToast.successShow(AddTendActivity.this,"评论上传成功");
//                    MyLog.i("评论上传成功");
//                }else{
////                    BmobToast.failureShow(AddTendActivity.this,"评论上传失败",e.getMessage());
//                    MyLog.e("评论上传失败_ "+e.getMessage());
//                }//else
//            }//done
//        });

//        list.add(comment);
//        tend.setCommentsList(list);

        tend.setCommentNum(0);

//        tend.setCreateTime("");

        if(imgList.size()==0){
            //没图片, 就赋值 空字符串
            imgList.add("");
            tend.setPicUrlList(imgList);
        }else{ //若有图片，将图片urls添加至 tend中
            if(picUrlList0.size()!=0)
                tend.setPicUrlList(picUrlList0);

        }


        ////////////////////////////////////////

        tend.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    mDialog.dismiss();
                    MyLog.i("tendItem 上传成功");
//                    MyLog.i("upload success");
                    //通知更新数据
                    EventBus.getDefault().post(new TendEvent());
                    finish();
                } else {
                    MyToast.showToast(AddTendActivity.this, "tendItem 上传失败_" + e.getMessage());
                    MyLog.e("tendItem 上传失败_" + e.getMessage());
                }
            }
        });
    }//upload

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_MULTISELECT:
                    imgList = data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS);

                    //断言关键字  若 mResults为null,则会抛出异常
                    assert imgList != null;
                    MyLog.v("path_ "+imgList.get(0));
                    if (imgList.size() != 0) {
                        iv_addImg.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);
                        mAdapter = new ImageListAdapter(this, imgList);
                        gridView.setAdapter(mAdapter);
                    }


                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}//AddTendAct
