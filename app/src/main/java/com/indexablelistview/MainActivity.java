package com.indexablelistview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.indexablelistview.widgte.IndexAbleListView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private Toolbar mTopBar;
    private IndexAbleListView mIndexListview;

    private ArrayList<String> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
        ContentAdapter adapter = new ContentAdapter(this,android.R.layout.simple_list_item_1,mItems);
        mIndexListview.setAdapter(adapter);
        mIndexListview.setFastScrollEnabled(true);
    }

    private void assignViews() {
        mTopBar = (Toolbar) findViewById(R.id.topBar);
        mIndexListview = (IndexAbleListView) findViewById(R.id.indexListView);
        mItems = new ArrayList<String>();
        String string = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        for(int i=0;i<string.length();i++){
//            mItems.add(string.charAt(i)+"");
//        }
//        for (int i=0;i<30;i++){
//            mItems.add(ChineseUtils.getRandomChinese());
//        }
        mItems.add("1");
        mItems.add("啊");
        mItems.add("吧");
        mItems.add("擦");
        mItems.add("的");
        mItems.add("额");
        mItems.add("飞");
        mItems.add("个");
        mItems.add("好");
        mItems.add("就");
        mItems.add("看");
        mItems.add("他");
        mItems.add("我");
        mItems.add("想");
        mItems.add("一");
        mItems.add("在");
        mItems.add("了");
        mItems.add("吗");
        mItems.add("你");
        mItems.add("哦");
        mItems.add("平");
        mItems.add("去");
        mItems.add("人");
        mItems.add("是");
        mItems.add("I");
        mItems.add("V");
        mItems.add("U");
        Collections.sort(mItems,new ChineseUtils.pinyinComparator());

    }


}
