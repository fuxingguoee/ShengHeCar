package com.il360.shenghecar.activity.home;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.il360.shenghecar.R;
import com.il360.shenghecar.view.CustomDatePicker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@EActivity(R.layout.act_call)
public class CallActivity extends Activity implements View.OnClickListener {
@ViewById
    RelativeLayout selectDate, selectTime;
@ViewById
     TextView currentDate;
    private CustomDatePicker customDatePicker1, customDatePicker2;


    @AfterViews
    void init() {
//    selectTime = (RelativeLayout) findViewById(R.id.selectTime);
        selectTime.setOnClickListener(this);
       // selectDate = (RelativeLayout) findViewById(R.id.selectDate);
        selectDate.setOnClickListener(this);
        initDatePicker();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectDate:
                // 日期格式为yyyy-MM-dd
                customDatePicker1.show(currentDate.getText().toString());
                break;

//            case R.id.selectTime:
//                // 日期格式为yyyy-MM-dd HH:mm
//                customDatePicker2.show(currentTime.getText().toString());
//                break;
       }
    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        currentDate.setText(now.split(" ")[0]);
//        currentTime.setText(now);

        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                currentDate.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动

        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
//                currentTime.setText(time);
            }
        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.showSpecificTime(true); // 显示时和分
        customDatePicker2.setIsLoop(true); // 允许循环滚动
    }

}
