package com.il360.shenghecar.activity.goods;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.adapter.NewsAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.home.ArrayOfNews;
import com.il360.shenghecar.model.home.News;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.UserUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lepc on 2018/8/1.
 */
@EActivity(R.layout.act_news_list)
public class NewsActivity extends BaseWidgetActivity {

    @ViewById ListView lvNews;

    private List<News> list = new ArrayList<News>();
    private NewsAdapter adapter;

    protected ProgressDialog transDialog;

    @AfterViews
    void init() {
        initList();
    }

    private void initList() {
        transDialog = ProgressDialog.show(NewsActivity.this, null, "加载中...", true);
        ExecuteTask.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserUtil.getToken());
                    TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
                            "goods/queryMessageList", params);
                    if (result.getSuccess()) {
                        ArrayOfNews arrayOfNews = FastJsonUtils.getSingleBean(result.getResult(), ArrayOfNews.class);
                        if (arrayOfNews.getCode() == 1) {
                            list.clear();
                            list.addAll(arrayOfNews.getResult());
                        } else {
                            showInfo(arrayOfNews.getDesc());
                        }
                    } else {
                        showInfo(getString(R.string.A6));
                    }
                } catch (Exception e) {
                    showInfo(getString(R.string.A2));
                    Log.e("NewsActivity", "initList", e);
                } finally {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            adapter = new NewsAdapter(list, NewsActivity.this);
                            lvNews.setAdapter(adapter);

                            if (transDialog != null && transDialog.isShowing()) {
                                transDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    private void initViews() {
//        adapter = new AddressAdapter(list, NewsActivity.this, 2);
//        lvNews.setAdapter(adapter);
//        lvNews.setOnItemClickListener(new NewsActivity.OnItemClickListener());
    }

    class OnItemClickListener implements android.widget.AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }

    private void showInfo(final String info) {
        runOnUiThread(new Runnable() {
            public void run() {
                if(transDialog!=null && transDialog.isShowing()) {
                    transDialog.dismiss();
                }
                Toast.makeText(NewsActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
