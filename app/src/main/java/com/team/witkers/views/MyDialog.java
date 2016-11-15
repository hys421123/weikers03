package com.team.witkers.views;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hys.mylog.MyLog;
import com.team.witkers.R;

/**
 * Created by hys on 2016/7/16.
 */
public class MyDialog {
    private Context context;
    private String title;
    public MyDialog(Context context,String title){
        this.context=context;
        this.title=title;
    }
    public MyDialog(Context context){
        this.context=context;
    }

    public void setDialotTitle(String title){
        this.title=title;
    }
    public  AlertDialog getDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        final AlertDialog dialog=builder.create();
        View dialogview=View.inflate(context, R.layout.dialog_view,null);
        Button btn_dialog_yes= (Button) dialogview.findViewById(R.id.btn_dialog_yes);
        TextView tv_dialog_title= (TextView) dialogview.findViewById(R.id.tv_dialog_title);

        tv_dialog_title.setText(title);
        btn_dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyLog.d("dialog yes click");
                dialog.dismiss();
            }
        });
        dialog.setView(dialogview,0,0,0,0);

        return dialog;
    }//getDialog
}//MyDialog_cls
