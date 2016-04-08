package com.example.llw.demo_addheaderview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by llw on 2016/4/7.
 */
public class Header_show extends ListView implements AbsListView.OnScrollListener {
    private View view;
    private LayoutInflater layoutInflater;
    int headHeiht;//下拉距离基值
    int scrollState;//当前滚动状态 用于下拉到一定的距离时变成释放状态


    //关于listview
    int firstVisibleItem;//listview当前第一个可见item的位置
    boolean isremark;//标记
    int statrY;//按下时的Y值;


    //listview刷新状态
    int state;
    final int NONE = 0;//正常状态
    final int PULL = 1;//提示下拉状态
    final int RELESE = 2;//提示松开状态
    final int RELEING = 3;//刷新的状态


    IRefreshlistener iRfreshlistenner;//刷新数据接口

    public Header_show(Context context) {
        super(context);
        initview(context);
    }

    public Header_show(Context context, AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }

    public Header_show(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initview(context);
    }

    public void initview(Context context) {
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.header_view, null);
        measureView(view);//通知父布局，占多少空间
        headHeiht = view.getMeasuredHeight();//获取header布局的高度
        Log.e("tag", "headheighr" + headHeiht);
        TopPadding(-headHeiht);
        this.addHeaderView(view);
        this.setOnScrollListener(this);
    }

    //通知父布局header占多少spce
    public void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();//??????
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//?????
        }


        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);//？？？？参数及why???
        int height;
        int tempHeight = p.height;//???

        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);

        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        view.measure(width, height);
    }

    //监听listview是否滑动到顶部????????????????????????????????????????????????????????????????????????
    public void TopPadding(int head_top) {
        //设置头部布局的四个边距
        view.setPadding(view.getPaddingLeft(), head_top, view.getPaddingRight(), view.getPaddingBottom());
        view.invalidate();//隐藏
    }

    //滚动
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;//获取listview当前滚动状态
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
    }

    //判断listview当前操作是否是向下，向上等动作
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem == 0) {
                    isremark = true;
                    statrY = (int) ev.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (state == RELESE) {
                    state = RELEING;
                    //如果下拉到释放状态的一瞬间应该将状态改为正在释放状态
                    //加载最新数据
                    RefreshViewByState();

                    iRfreshlistenner.onReflash();//????????????????????????????????????????调用主界面中

                } else if (state == PULL) {
                    state = NONE;
                    isremark = false;
                    RefreshViewByState();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;
        }
        return super.onTouchEvent(ev);
    }


    public void onMove(MotionEvent ev) {
        if (!isremark) {
            return;
        }

        //记录当前移动的Y值和移动的距离
        int tempY = (int) ev.getY();
        int space = tempY - statrY;
        int topPadding = space - headHeiht;//????????
        switch (state) {
            case NONE:
                if (space > 0) {
                    state = PULL;
                    RefreshViewByState();
                }//如果向下移动的距离大于0说明listview正处于下拉状态
                break;
            case PULL:
                TopPadding(topPadding);//????
                if (space > headHeiht + 30 && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    state = RELESE;
                    RefreshViewByState();
                }
                break;

            case RELESE:
                TopPadding(topPadding);
                //如果下拉距离小于一定的距离时是下拉状态
                if (space < headHeiht + 30) {
                    state = PULL;
                    RefreshViewByState();
                } else if (space <= 0) {
                    state = NONE;
                    isremark = false;
                    RefreshViewByState();
                }
                break;

        }
    }

    //根据listview的状态头部信息作出相应的改变：如下拉刷新，释放刷新，正在刷新
    public void RefreshViewByState() {
        final TextView textView = (TextView) view.findViewById(R.id.loser_hand);
        textView.setTextSize(20);
        final ImageView imageView = (ImageView) view.findViewById(R.id.image_show);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        //设置箭头动画
        RotateAnimation rotateAnimation = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);//???????
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);

        RotateAnimation rotateAnimation1 = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation1.setDuration(500);
        rotateAnimation1.setFillAfter(true);


        switch (state) {
            case NONE:
                imageView.clearAnimation();
                TopPadding(-headHeiht);//正常情况下隐藏header
                break;
            case PULL:
                //下拉时箭头显示，进度条隐藏,提示信息
                imageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                textView.setText("下拉刷新了!!");

                imageView.clearAnimation();
                imageView.setAnimation(rotateAnimation1);
                break;
            case RELESE:
                //达到释放状态的一瞬间时：箭头显示，进度条隐藏，提示信息
                imageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                textView.setText("松开刷新!!");

                imageView.clearAnimation();
                imageView.setAnimation(rotateAnimation);
                break;
            case RELEING:
                TopPadding(headHeiht);//正在刷新时有一个固定的高度

                //释放刷新时，箭头隐藏，进度条显示，提示信息
                imageView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                textView.setText("正在刷新!!!!!!!!!!");
                imageView.clearAnimation();
                break;
        }
    }

    //用于刷新
    public void RefreshComplete() {
        state = NONE;//刷新完成后变成正常状态
        isremark = false;
        RefreshViewByState();//刷新界面
        TextView lastUpdate_time = (TextView) view.findViewById(R.id.lastupdate_time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy年mm月dd日hh时 hh::mm::ss");
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        lastUpdate_time.setText(time);

    }

    //???????????????????????????????????????
    public void setInterface(IRefreshlistener iRfreshlistenner) {
        this.iRfreshlistenner = iRfreshlistenner;
    }

    //数据刷新接口  ??????????????????????????????????????????????
    public interface IRefreshlistener {
        public void onReflash();
    }


}
