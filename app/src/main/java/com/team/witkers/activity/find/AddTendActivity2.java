package com.team.witkers.activity.find;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.emition_library.fragment.EmotionMainFragment;
import com.hys.mylog.MyLog;
import com.team.witkers.R;
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

import cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by lenovo on 2016/12/3.
 */



public class AddTendActivity2 extends BGAPPToolbarActivity implements BGASortableNinePhotoLayout.Delegate, View.OnClickListener {

    private BGASortableNinePhotoLayout mNinePhotosLayout;
    private ImageButton ib_camera, ib_photo, ib_emoj;
    private EditText et_input;
    private  ProgressDialog mDialog;
    private MyUser myUser;
    private ArrayList<String> imgList = new ArrayList<>();
    private ArrayList<String> compressFileList=new ArrayList<>();
    private String fileNamePrefix ;
    private List<String> picUrlList0=new ArrayList<>();
    private int ii=0;

    private static final int REQUEST_CODE_PERMISSION_PHOTO_PICKER = 1;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_PHOTO_PREVIEW = 2;

    private static final String EXTRA_MOMENT = "EXTRA_MOMENT";

    private EmotionMainFragment emotionMainFragment;

    private boolean isVisibleInput = true;
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_find_addtend_3);

        mNinePhotosLayout = getViewById(R.id.mNinePhotosLayout);
        et_input=getViewById(R.id.et_input);
//        ib_photo=getViewById(R.id.ib_photo);

//        ib_emoj=getViewById(R.id.ib_emoj);

        et_input.setFocusable(true);
        et_input.setFocusableInTouchMode(true);
        et_input.requestFocus();
        InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
        im.showSoftInput(et_input, 0);
    }

    @Override
    protected void setListener() {
// 显示 九宫格 加号(添加图片的 按钮)
        mNinePhotosLayout.setIsPlusSwitchOpened(true);

        mNinePhotosLayout.setIsSortable(true);
        // 设置拖拽排序控件的代理
        mNinePhotosLayout.setDelegate(AddTendActivity2.this);

        et_input.setOnClickListener(this);





//        ib_photo.setOnClickListener(this);
//        ib_emoj.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("编辑");


//        et_input.setFocusable(true);
//        et_input.setFocusableInTouchMode(true);
//        et_input.requestFocus();
//        InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
//        im.showSoftInput(et_input, 0);


        mNinePhotosLayout.init(this);
        initEmotionMainFragment();
    }

    /**
     * 初始化表情面板
     */
    public void initEmotionMainFragment(){
        //构建传递参数
        Bundle bundle = new Bundle();
        //绑定主内容编辑框  fragment与editText 相互绑定
        bundle.putBoolean(EmotionMainFragment.BIND_TO_EDITTEXT,false);
        //隐藏控件
        bundle.putBoolean(EmotionMainFragment.HIDE_BAR_EDITTEXT_AND_BTN,true);
        //替换fragment
        //创建修改实例
        emotionMainFragment =EmotionMainFragment.newInstance(EmotionMainFragment.class,bundle);
        emotionMainFragment.bindToContentView(et_input);
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        // Replace whatever is in thefragment_container view with this fragment,
        // and add the transaction to the backstack
        transaction.replace(R.id.fl_emotionview_main,emotionMainFragment);
        transaction.addToBackStack(null);
        //提交修改
        transaction.commit();
    }


    @Override
    public void onBackPressed() {
        /**
         * 判断是否拦截返回键操作
         */
        if (!emotionMainFragment.isInterceptBackPress()) {
            super.onBackPressed();
        }
    }//onBackPressed


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tend_publish, menu);
        return true;
    }

    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
        Log.v("hys","点击 添加 图片");
        choicePhotoWrapper();
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        mNinePhotosLayout.removeItem(position);
    }

    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        //nineLayout 有图片时，点击图片变大
        MyLog.i("应该是点击图片变大");
        startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent(this, mNinePhotosLayout.getMaxItemCount(),
                models, models, position, false), REQUEST_CODE_PHOTO_PREVIEW);
    }

    private void choicePhotoWrapper() {
//        Log.i("hys","choice PhotoWrapper");
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto");

            startActivityForResult(BGAPhotoPickerActivity.newIntent(this, takePhotoDir,9, mNinePhotosLayout.getData(), false), REQUEST_CODE_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片", REQUEST_CODE_PERMISSION_PHOTO_PICKER, perms);
        }
    }//choicePhotoWrapper


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            MyLog.i("onActivityResult__ ");

            imgList=BGAPhotoPickerActivity.getSelectedImages(data);
            MyLog.v("PhotoSize_ "+imgList.size());

//            for(int i=0;i<imgList.size();i++){
//                MyLog.d(imgList.get(i));
//            }

            mNinePhotosLayout.setData((ArrayList<String>) imgList);
        } else if (requestCode == REQUEST_CODE_PHOTO_PREVIEW) {
            mNinePhotosLayout.setData(BGAPhotoPickerPreviewActivity.getSelectedImages(data));
        }
    }

    // 底边 栏的 图片按钮点击
    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.et_input:

                MyLog.v("et input");
                if (isVisibleInput) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    isVisibleInput = false;
                }
                break;

            case R.id.ib_photo:
                choicePhotoWrapper();

                break;
            case R.id.ib_emoj:
                MyLog.v("emoj");
                break;
        }//switch
    }//click

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_tend_publish) {
            if (et_input.getText().toString().trim().equals(""))
                MyToast.showToast(this, "请输入内容或添加图片");
            else{
                MyLog.d("publish __");
                uploadTend0();
            }

        }//外层if
        return super.onOptionsItemSelected(item);
    }//onOptionsSelected

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

    //上传自己的动态信息
    private void uploadTendNoPic() {
        MyLog.i("uploadTend no Pic");
        TendItems tend = new TendItems();

//        默认当前 用户不为 null
        String userName = myUser.getUsername();
        String headUrl = myUser.getHeadUrl();

            MyLog.v("AddTendAct_ myUser_name/head"+userName+"//"+headUrl);


        tend.setPubUser(myUser);
        tend.setFriendName(userName);
        tend.setFriendHeadUrl(headUrl);

        tend.setContent(et_input.getText().toString());
        tend.setCommentNum(0);


        if(imgList.size()==0){
            //没图片, 就赋值 空字符串
            imgList.add("");
            tend.setPicUrlList(imgList);
        }else{ //若有图片，将图片urls添加至 tend中
            if(picUrlList0.size()!=0)
                tend.setPicUrlList(picUrlList0);

        }
        MyLog.v("pubUser__ "+tend.getPubUser().getUsername());

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
                    MyToast.showToast(AddTendActivity2.this, "tendItem 上传失败_" + e.getMessage());
                    MyLog.e("tendItem 上传失败_" + e.getMessage());
                }
            }
        });
    }//upload


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
                File newFile2= Luban2.get(AddTendActivity2.this)
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
}//AddTendAct2
