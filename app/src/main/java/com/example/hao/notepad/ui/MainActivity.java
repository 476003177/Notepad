package com.example.hao.notepad.ui;
/**
 * Created by 李 on 2017/1/26.
 * Updated by 梁 on 2018/5/5
 */
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hao.notepad.R;
import com.example.hao.notepad.bean.DiaryBean;
import com.example.hao.notepad.db.DiaryDatabaseHelper;
import com.example.hao.notepad.event.StartUpdateDiaryEvent;
import com.example.hao.notepad.utils.AppManager;
import com.example.hao.notepad.utils.GetDate;
import com.example.hao.notepad.utils.SpHelper;
import com.example.hao.notepad.utils.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.common_iv_back)
    ImageView mCommonIvBack;
    @BindView(R.id.common_tv_title)
    TextView mCommonTvTitle;
    @BindView(R.id.common_iv_test)
    ImageView mCommonIvTest;
    @BindView(R.id.common_title_ll)
    LinearLayout mCommonTitleLl;
    @BindView(R.id.main_iv_circle)
    ImageView mMainIvCircle;
    @BindView(R.id.main_tv_date)
    TextView mMainTvDate;
    @BindView(R.id.main_tv_content)
    TextView mMainTvContent;
    @BindView(R.id.item_ll_control)
    LinearLayout mItemLlControl;

    @BindView(R.id.main_rv_show_diary)
    RecyclerView mMainRvShowDiary;
    @BindView(R.id.main_fab_enter_edit)
    FloatingActionButton mMainFabEnterEdit;
    @BindView(R.id.main_rl_main)
    RelativeLayout mMainRlMain;
    @BindView(R.id.item_first)
    LinearLayout mItemFirst;
    @BindView(R.id.main_ll_main)
    LinearLayout mMainLlMain;
    private List<DiaryBean> mDiaryBeanList;
    
    private DiaryDatabaseHelper mHelper;

    private static String IS_WRITE = "true";

    private int mEditPosition = -1;
    /**
     * 标识今天是否已经写了日记
     */
    private boolean isWrite = false;
    private static TextView mTvTest;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBmob();//初始化Bmob

        setContentView(R.layout.activity_main);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);
        StatusBarCompat.compat(this, Color.parseColor("#161414"));
        mHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        EventBus.getDefault().register(this);
        SpHelper spHelper = SpHelper.getInstance(this);
        getDiaryBeanList();
        initTitle();
        mMainRvShowDiary.setLayoutManager(new LinearLayoutManager(this));
        mMainRvShowDiary.setAdapter(new DiaryAdapter(this, mDiaryBeanList));
        /*mTvTest = new TextView(this);
        mTvTest.setText("hello world");*/
    }

    private void initBmob() {
        //提供以下两种方式进行初始化Bmob操作：

        //第一：默认初始化，需要输入自己Bmob后端的Application ID
        Bmob.initialize(this, "e952b8aa9b5b3733a7d2706377987823");
        // 注:自v3.5.2开始，数据sdk内部缝合了统计sdk，开发者无需额外集成，传渠道参数即可，不传默认没开启数据统计功能
        //Bmob.initialize(this, "Your Application ID","bmob");

        //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        //BmobConfig config =new BmobConfig.Builder(this)
        ////设置appkey
        //.setApplicationId("Your Application ID")
        ////请求超时时间（单位为秒）：默认15s
        //.setConnectTimeout(30)
        ////文件分片上传时每片的大小（单位字节），默认512*1024
        //.setUploadBlockSize(1024*1024)
        ////文件的过期时间(单位为秒)：默认1800s
        //.setFileExpiration(2500)
        //.build();
        //Bmob.initialize(config);
    }

    private void initTitle() {
        mMainTvDate.setText("今天，" + GetDate.getDate());
        mCommonTvTitle.setText("日记");
        mCommonIvBack.setVisibility(View.INVISIBLE);
        mCommonIvTest.setVisibility(View.INVISIBLE);
    }

    private List<DiaryBean> getDiaryBeanList() {

        mDiaryBeanList = new ArrayList<>();
        List<DiaryBean> diaryList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = mHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Diary", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String dateSystem = GetDate.getDate().toString();
                if (date.equals(dateSystem)) {
                    mMainLlMain.removeView(mItemFirst);
                    break;
                }
            } while (cursor.moveToNext());
        }


        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String tag = cursor.getString(cursor.getColumnIndex("tag"));
                mDiaryBeanList.add(new DiaryBean(date, title, content, tag));
            } while (cursor.moveToNext());
        }
        cursor.close();

        for (int i = mDiaryBeanList.size() - 1; i >= 0; i--) {
            diaryList.add(mDiaryBeanList.get(i));
        }

        mDiaryBeanList = diaryList;
        return mDiaryBeanList;
    }

    @Subscribe
    public void startUpdateDiaryActivity(StartUpdateDiaryEvent event) {
        String title = mDiaryBeanList.get(event.getPosition()).getTitle();
        String content = mDiaryBeanList.get(event.getPosition()).getContent();
        String tag = mDiaryBeanList.get(event.getPosition()).getTag();
        UpdateDiaryActivity.startActivity(this, title, content, tag);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.main_fab_enter_edit)
    public void onClick() {
        AddDiaryActivity.startActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppManager.getAppManager().AppExit(this);
    }
}