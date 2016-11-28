package com.team.witkers.views;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team.witkers.R;

/**
 * Created by jin on 2016/9/3.
 */
public class ItemLongOnClickDialog extends Dialog {

    public static final int STATE1 = 1;
    public static final int STATE2 = 2;

    public ItemLongOnClickDialog(Context context) {
        super(context);
    }

    public ItemLongOnClickDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String message1;
        private String message2;
        private View contentView;
        private OnClickListener tvStickClickListener;
        private OnClickListener tvDeleteClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage1(String message1, OnClickListener listener) {
            this.message1 = message1;
            this.tvStickClickListener = listener;
            return this;
        }

        public Builder setMessage2(String message2, OnClickListener listener) {
            this.message2 = message2;
            this.tvDeleteClickListener = listener;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }


        public ItemLongOnClickDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final ItemLongOnClickDialog dialog = new ItemLongOnClickDialog(context, R.style.dialog);
            View layout = inflater.inflate(R.layout.dialog_long_click_item_2, null);

            dialog.addContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));



            if(message1!=null){
                ((TextView)layout.findViewById(R.id.tv_stick)).setText(message1);
                if(tvStickClickListener !=null){
                    ((TextView)layout.findViewById(R.id.tv_stick)).setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            tvStickClickListener.onClick(dialog, STATE1);
                        }
                    });
                }
            }else{
                layout.findViewById(R.id.tv_stick).setVisibility(View.GONE);
            }

            if(message2!=null){
                ((TextView)layout.findViewById(R.id.tv_delete)).setText(message2);
                if(tvDeleteClickListener !=null){
                    ((TextView)layout.findViewById(R.id.tv_delete)).setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            tvDeleteClickListener.onClick(dialog,STATE2);
                        }
                    });
                }
            }else{
                layout.findViewById(R.id.tv_delete).setVisibility(View.GONE);
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }
}