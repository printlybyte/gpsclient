package com.mj.gpsclient.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mj.gpsclient.R;

/**
 * Created by majin on 15/5/30.
 */
public class SearchBarView extends RelativeLayout  {
    private EditText editText;
    private TextView cancelText;
    private SearchBarCallback mCallback;
    public SearchBarView(Context context) {
        super(context);
        init();
    }

    public SearchBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.search_bar_view, this);
        editText = (EditText) findViewById(R.id.search_edit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                String text = String.valueOf(editText.getText());
                    if(mCallback!=null){
                        mCallback.onchange(text);
                    }
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
        });
        cancelText = (TextView) findViewById(R.id.search_cancel);
        cancelText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback!=null){
                    reset();
                    mCallback.onCancel();
                }
            }
        });

    }


    public  void reset(){
        editText.setText("");
    }

    public void setCallback(SearchBarCallback callback) {
        this.mCallback = callback;
    }

    public interface SearchBarCallback {
        void onchange(String text);
        void onCancel();
    }
}
