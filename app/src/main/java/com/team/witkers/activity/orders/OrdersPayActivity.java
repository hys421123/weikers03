package com.team.witkers.activity.orders;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.hys.mylog.MyLog;
import com.team.witkers.R;
import com.team.witkers.base.BaseActivity;
import com.team.witkers.bean.ChooseClaimant;
import com.team.witkers.bean.ClaimItems;
import com.team.witkers.bean.Mission;
import com.team.witkers.eventbus.ChooseNotify;
import com.team.witkers.utils.MyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import c.b.BP;
import c.b.PListener;
import c.b.QListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class OrdersPayActivity extends BaseActivity implements View.OnClickListener {
//支付界面

    public final static int RESULT_CODE=1;
    private String BmobID = "01e6a3045fb017cddbc2b79ee38a705c";
    // 此为支付插件的官方最新版本号,请在更新时留意更新说明
    private int PLUGINVERSION = 7;
    ProgressDialog dialog;
    private TextView tv_num1,tv_num2,tv_num3,tv_num4,tv_topTitle;
    private Button btn_pay;
    private ImageView iv_topBack;
    private RelativeLayout rel_bankCarPay,rel_weixinPay,rel_aliPay,rel_offlinePay;
    private RadioButton rb_bankCardPay,rb_weixinPay,rb_aliPay,rb_offlinePay;
    private Mission mission;
    private int position;

    @Override
    protected int setContentId() {
        return R.layout.activity_orders_pay_2;
    }

    @Override
    protected void initView() {
//        BP.init(this, BmobID);

        tv_num1 = (TextView) findViewById(R.id.tv_num1);
        tv_num2 = (TextView) findViewById(R.id.tv_num2);
        tv_num3 = (TextView) findViewById(R.id.tv_num3);
        tv_num4 = (TextView) findViewById(R.id.tv_num4);
        btn_pay = (Button) findViewById(R.id.btn_pay);
        rel_offlinePay= (RelativeLayout) findViewById(R.id.rel_offlinePay);
        rel_bankCarPay = (RelativeLayout) findViewById(R.id.rel_bankCardPay);
        rel_weixinPay = (RelativeLayout) findViewById(R.id.rel_weixinPay);
        rel_aliPay = (RelativeLayout) findViewById(R.id.rel_aliPay);

        rb_offlinePay= (RadioButton) findViewById(R.id.rb_offlinePay);
        rb_bankCardPay = (RadioButton) findViewById(R.id.rb_bankCardPay);
        rb_weixinPay = (RadioButton) findViewById(R.id.tb_weixinPay);
        rb_aliPay = (RadioButton) findViewById(R.id.rb_aliPay);
        iv_topBack = (ImageView) findViewById(R.id.iv_topBack);
        tv_topTitle = (TextView) findViewById(R.id.tv_topTitle);
        tv_topTitle.setText("支付");
        rb_bankCardPay.setEnabled(false);
        rb_weixinPay.setEnabled(false);
        rb_aliPay.setEnabled(false);
    }

    @Override
    protected void setListener() {
        btn_pay.setOnClickListener(this);
        iv_topBack.setOnClickListener(this);
        rel_bankCarPay.setOnClickListener(this);
        rel_weixinPay.setOnClickListener(this);
        rel_aliPay.setOnClickListener(this);
        rel_offlinePay.setOnClickListener(this);
    }

    @Override
    protected void getIntentData() {
        //如果只有单个claimItem传进来，则无法更新mission类，加入chooseClaimant
        mission = (Mission) getIntent().getSerializableExtra("fromOrdersShow");
        position = getIntent().getIntExtra("position",0);
//        MyLog.i("position-->"+position);
        MyLog.i("info-->"+mission.getInfo()+",price-->"+mission.getClaimItemList().get(position).getClaimMoney());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_topBack:
                finish();
                break;
            case R.id.rel_offlinePay:
                setSelect(0);
                break;
            case R.id.rel_bankCardPay:
                setSelect(1);
                break;
            case R.id.rel_weixinPay:
                setSelect(2);
                break;
            case R.id.rel_aliPay:
                setSelect(3);
                break;

            case R.id.btn_pay:
//                MyToast.showToast(this,"pay");
                if(rb_bankCardPay.isChecked()&&rb_weixinPay.isChecked()&&rb_aliPay.isChecked()){
                    MyToast.showToast(this,"请选择一种支付方式");
                }else{
                    if(rb_bankCardPay.isChecked()){
                        MyToast.showToast(this,"银行卡支付还未开通");
                    }else if(rb_aliPay.isChecked()){
//                        MyToast.showToast(this,"支付宝支付还未开通");
                        pay(true);
                    }else if(rb_weixinPay.isChecked()){
                        //选择已经开通的 微信支付
                        pay(false);
                    }
                }
                break;
        }
    }

    /**
     * 调用支付
     */
    private void pay(boolean bool) {//bool为false微信支付， true为支付宝支付

        if (bool) {//true 为支付宝
            if (!checkPackageInstalled("com.eg.android.AlipayGphone",
                    "https://www.alipay.com")) { // 支付宝支付要求用户已经安装支付宝客户端
                Toast.makeText(OrdersPayActivity.this, "请安装支付宝客户端", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        } else {// else 为微信
            if (checkPackageInstalled("com.tencent.mm", "http://weixin.qq.com")) {// 需要用微信支付时，要安装微信客户端，然后需要插件
                // 有微信客户端，看看有无微信支付插件
                int pluginVersion = BP.getPluginVersion(this);
                if (pluginVersion < PLUGINVERSION) {// 为0说明未安装支付插件,
                    // 否则就是支付插件的版本低于官方最新版
                    Toast.makeText(
                            OrdersPayActivity.this,
                            pluginVersion == 0 ? "监测到本机尚未安装支付插件,无法进行支付,请先安装插件(无流量消耗)"
                                    : "监测到本机的支付插件不是最新版,最好进行更新,请先更新插件(无流量消耗)",
                            Toast.LENGTH_SHORT).show();
//                    installBmobPayPlugin("bp.db");

                    installApk("bp.db");
                    return;
                }
            } else {// 没有安装微信
                Toast.makeText(OrdersPayActivity.this, "请安装微信客户端", Toast.LENGTH_SHORT).show();
                return;
            }
        }



        showDialog("正在获取订单...");
        String title = "";
        if(bool)
            title="支付宝支付";
        else
            title="微信支付";

        final String info = mission.getInfo().trim();
        double price = mission.getClaimItemList().get(position).getClaimMoney();
//        一下代码是为了防止小米手机支付出错添加的
        //        这段代码加在BP.pay方法调用之前

        /*
        //*************************支付暂时不能用的代码************************
        //只有付款确认后才能填写选定认领人
        //选定认领项,并填入选定认领人，上传到mission类中
        final ClaimItems claimItem= mission.getClaimItemList().get(position);
        ChooseClaimant chooseClaimant=new ChooseClaimant();
        chooseClaimant.setClaimName(claimItem.getClaimName());
        chooseClaimant.setClaimMoney(claimItem.getClaimMoney());
        chooseClaimant.setClaimStatus(false);
        chooseClaimant.setClaimHeadUrl(claimItem.getClaimHeadUrl());
        mission.setChooseClaimant(chooseClaimant);
        mission.setClaimName(claimItem.getClaimName());
        mission.increment("times");
        mission.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    MyLog.d("添加选定认领人成功");
                    hideDialog();
                    Intent intent=new Intent();
                    intent.putExtra("claimItem", claimItem);
                    setResult(RESULT_CODE,intent);
                    finish();
                }else{
                    MyLog.d("添加选定认领人失败"+e.getMessage());
                }//else
            }
        });

        //*************************支付暂时不能用，跳过这里************************
*/


// 以下是 原来的可支付的代码


        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.bmob.app.sport",
                    "com.bmob.app.sport.wxapi.BmobActivity");
            intent.setComponent(cn);
            this.startActivity(intent);
        } catch (Throwable e) {
//            e.printStackTrace();
            MyLog.e(e.getMessage());
        }

//支付类型，true为支付宝支付,false为微信支付
        // 有bug， 这里的title 实际上对应 订单描述
        BP.pay(title,"商品描述",price,bool, new PListener() {
            @Override
            public void orderId(String s) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
//                MyToast.showToast(OrdersPayActivity.this,"orderId-->"+s);
//                tv.append(name + "'s orderid is " + orderId + "\n\n");
                showDialog("获取订单成功!请等待跳转到支付页面~");
            }

            @Override
            public void succeed() {
                Toast.makeText(OrdersPayActivity.this, "支付成功!", Toast.LENGTH_SHORT).show();
//                tv.append(name + "'s pay status is success\n\n");
                MyLog.d("支付成功");
                hideDialog();

                //只有付款确认后才能填写选定认领人
                //选定认领项,并填入选定认领人，上传到mission类中
               final ClaimItems claimItem= mission.getClaimItemList().get(position);
                ChooseClaimant chooseClaimant=new ChooseClaimant();
                chooseClaimant.setClaimName(claimItem.getClaimName());
                chooseClaimant.setClaimMoney(claimItem.getClaimMoney());
                chooseClaimant.setClaimStatus(false);
                chooseClaimant.setClaimHeadUrl(claimItem.getClaimHeadUrl());
                mission.setChooseClaimant(chooseClaimant);
                mission.setClaimName(claimItem.getClaimName());
                mission.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            MyLog.d("添加选定认领人成功");

                            Intent intent=new Intent();
                            intent.putExtra("claimItem", claimItem);
                            setResult(RESULT_CODE,intent);
                            finish();
                        }else{
                            MyLog.d("添加选定认领人失败"+e.getMessage());
                            }//else
                    }
                });

            }//succeed

            @Override
            public void fail(int code, String s) {
                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
                if (code == -3) {
//                    Toast.makeText(
//                            OrdersPayActivity.this,
//                            "监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付",
//                            Toast.LENGTH_SHORT).show();
//                    installBmobPayPlugin("bp.db");
                    //去下载插件
                    installApk("bp.db");

                } else {
                    Toast.makeText(OrdersPayActivity.this, "支付失败!_", Toast.LENGTH_SHORT).show();
                    MyLog.e("支付失败_ "+s);
                }
//                tv.append(name + "'s pay status is fail, error code is \n"
//                        + code + " ,reason is " + reason + "\n\n");
//                MyLog.d("支付失败");
                hideDialog();
            }//fail

            @Override
            public void unknow() {
//                Toast.makeText(Or.this, "支付结果未知,请稍后手动查询", Toast.LENGTH_SHORT).show();
//                tv.append(name + "'s pay status is unknow\n\n");
                MyToast.showToast(OrdersPayActivity.this,"支付结果未知,请稍后手动查询");
                hideDialog();
            }//unknow
        });//BP.pay

    }//pay

    private boolean checkPackageInstalled(String packageName, String browserUrl) {
        try {
            // 检查是否有支付宝客户端
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // 没有安装支付宝，跳转到应用市场
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + packageName));
                startActivity(intent);
            } catch (Exception ee) {// 连应用市场都没有，用浏览器去支付宝官网下载
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(browserUrl));
                    startActivity(intent);
                } catch (Exception eee) {
                    Toast.makeText(OrdersPayActivity.this,
                            "您的手机上没有没有应用市场也没有浏览器，我也是醉了，你去想办法安装支付宝/微信吧",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }// checkPackageInstalled

    private static final int REQUESTPERMISSION = 101;
    private void installApk(String s) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTPERMISSION);
        } else {
            installBmobPayPlugin(s);
        }
    }

    private void downloadPlugin(){
        AlertDialog.Builder builder=new AlertDialog.Builder(OrdersPayActivity.this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("监测到你尚未安装支付插件,无法进行支付,需要下载插件吗?"); //设置内容
//        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 访问网址下载 插件
                String url="https://github.com/hys421123/servers/raw/master/BmobPayPlugin_7.apk";
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);

//                dialog.dismiss(); //关闭dialog
//                Toast.makeText(MainActivity.this, "确认" + which, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    // 执行订单查询
    void query() {
        showDialog("正在查询订单...");
        final String orderId = "输入你的订单ID";

        BP.query(orderId, new QListener() {

            @Override
            public void succeed(String status) {
                Toast.makeText(OrdersPayActivity.this, "查询成功!该订单状态为 : " + status,
                        Toast.LENGTH_SHORT).show();
                MyLog.d("success_ "+status);
//                tv.append("pay status of" + orderId + " is " + status + "\n\n");
                hideDialog();
            }

            @Override
            public void fail(int code, String reason) {
                Toast.makeText(OrdersPayActivity.this, "查询失败"+reason, Toast.LENGTH_SHORT).show();
//                tv.append("query order fail, error code is " + code
//                        + " ,reason is \n" + reason + "\n\n");
                MyLog.e("fail_ "+reason);
                hideDialog();
            }
        });
    }

    void showDialog(String message) {
        try {
            if (dialog == null) {
                dialog = new ProgressDialog(this,message);
                dialog.setCancelable(true);
            }
            dialog.show();
        } catch (Exception e) {
            // 在其他线程调用dialog会报错
        }
    }

    void hideDialog() {
        if (dialog != null && dialog.isShowing())
            try {
                dialog.dismiss();
                MyLog.d("dialog_hide");
//                finish();
            } catch (Exception e) {
            }
    }

    void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName + ".apk");
            if (file.exists())
                file.delete();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSelect(int Flag){
        switch (Flag){
            case 0:
                resetSelect();
                rb_offlinePay.setChecked(true);

                break;
            case 1:
                resetSelect();
                rb_bankCardPay.setChecked(true);
                break;
            case 2:
                resetSelect();
                rb_weixinPay.setChecked(true);
                break;
            case 3:
                resetSelect();
                rb_aliPay.setChecked(true);
                break;
        }
    }

    private void resetSelect(){
        rb_bankCardPay.setChecked(false);
        rb_weixinPay.setChecked(false);
        rb_aliPay.setChecked(false);
    }
}
