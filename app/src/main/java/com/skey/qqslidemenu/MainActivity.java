package com.skey.qqslidemenu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.skey.qqslidemenu.view.MyLinearLayout;
import com.skey.qqslidemenu.view.SlideMenu;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ListView lvMenu;
    private ListView lvMain;
    private SlideMenu slMain;
    private ImageView ivHead;
    private MyLinearLayout myLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initListener();
        initData();
    }


    private void initView() {
        setContentView(R.layout.activity_main);

        lvMenu = (ListView) findViewById(R.id.lv_menu);
        lvMain = (ListView) findViewById(R.id.lv_main);
        slMain = (SlideMenu) findViewById(R.id.sl_main);
        ivHead = (ImageView) findViewById(R.id.iv_head);
        myLayout = (MyLinearLayout) findViewById(R.id.my_layout);
    }

    private void initListener() {
        slMain.setOnDragStateChangeListener(new SlideMenu.OnDragStateChangeListener() {
            @Override
            public void onOpen() {
//                Log.e("tag", "onOpen");
                lvMenu.smoothScrollToPosition(new Random().nextInt(lvMenu.getCount()));
            }

            @Override
            public void onClose() {
//                Log.e("tag", "onClose");
                ivHead.animate().translationX(15).setInterpolator(new CycleInterpolator(4))
                        .setDuration(500).start();
            }

            @Override
            public void onDragging(float fraction) {
//                Log.e("tag", "onDragging fraction" + fraction);
                ivHead.setAlpha(1 - fraction);
            }
        });
    }

    private void initData() {
        lvMenu.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });
        lvMain.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.NAMES) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView == null ? super.getView(position, convertView, parent) : convertView;
                //先缩小
                view.setScaleX(0.5f);
                view.setScaleY(0.5f);
                //以属性动画形式放大
                view.animate().scaleX(1).setDuration(350).start();
                view.animate().scaleY(1).setDuration(350).start();
                return view;
            }
        });
        myLayout.setSlideMenu(slMain);

    }

}
