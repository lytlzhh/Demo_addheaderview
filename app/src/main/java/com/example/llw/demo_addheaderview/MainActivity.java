package com.example.llw.demo_addheaderview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, Header_show.IRefreshlistener {
    private Header_show listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Mygetitem> list;
    private Mybaseadapter mybaseadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intniview();
    }

    public void intniview() {
        listView = (Header_show) findViewById(R.id.listview_show);
        listView.setInterface(this);//?????????????????????????????????
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        list = new ArrayList<>();
    }


    public void onRefresh() {
        // swipeRefreshLayout.setRefreshing(true);
       /* new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < 2; i++) {
                    list.add(new Mygetitem("hell world!!"));
                }
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
        //  swipeRefreshLayout.setRefreshing(false);
        listView.setAdapter(new Mybaseadapter(list, MainActivity.this));*/


        //swipeRefreshLayout.setRefreshing(false);
        onReflash();
    }


    String[] newstr = {"最新数据", "刷新书籍"};

    //获取最新数据
    public void Get_New_Date() {
        for (int s = 0; s < 2; s++) {
            list.add(0, new Mygetitem(newstr[s]));
        }
    }


    public void ShowList(List<Mygetitem> list) {
        if (mybaseadapter == null) {
            mybaseadapter = new Mybaseadapter(list, MainActivity.this);
            listView.setAdapter(new Mybaseadapter(list, MainActivity.this));
        } else {
            mybaseadapter.onDateChange(list);
        }
    }

    //在header_view类中定义的接口
    @Override
    public void onReflash() {
        //在实际中是不需要的Handler的，因为网络加载书籍会延时
        android.os.Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //1,获取最新数据
                Get_New_Date();
                // 2，通知界面显示
                ShowList(list);
                // 3，通知listview刷新数据完毕
                listView.RefreshComplete();

            }
        }, 200);

    }
}
