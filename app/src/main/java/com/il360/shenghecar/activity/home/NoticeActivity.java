package com.il360.shenghecar.activity.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.main.UrlToWebActivity_;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.Variables;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.home.Advert;
import com.il360.shenghecar.model.home.ArrayOfAdvert;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ImageFromTxyUtil;
import com.il360.shenghecar.util.ImageLoaderUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.act_notice)
public class NoticeActivity extends BaseWidgetActivity {

    @ViewById
    PullToRefreshScrollView pull_refresh_scrollview;
    private ArrayOfAdvert arrayOfAdvert;
    protected ProgressDialog transDialog;
    private String shareUrl = "";
    private String shareName = "";
    private String shareLink = "";
    private String shareUrl1 = "";
    private String shareName1 = "";
    private String shareLink1 = "";
    private String shareUrl2 = "";
    private String shareName2 = "";
    private String shareLink2 = "";

    @ViewById
    ViewPager vp_adv_change;
    @ViewById
    LinearLayout ll_adv_circle;      //测试:advertId:124   //正式:advertId:235
    @ViewById LinearLayout lrShare; //广告1智能锁
    @ViewById LinearLayout lvShare; //广告2盛和公司
    @ViewById LinearLayout lwShare; //广告3新品上线
    @ViewById
    ImageView irShare;//1
    @ViewById
    ImageView ivShare;//2
    @ViewById
    ImageView iwShare;//3

    @AfterViews
    void init() {
        initAdvert();
        initAdvert2();
        initAdvert3();
        initPull();
    }
    private void initPull() {
        pull_refresh_scrollview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pull_refresh_scrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
            initAdvert();
           initAdvert2();
           initAdvert3();
            }
        });
    }

    @Click
    void lvShare() {//2
        Intent intent = new Intent(NoticeActivity.this, UrlToWebActivity_.class);
        intent.putExtra("supportZoom", false);//不支持缩放
        intent.putExtra("title", shareName1);
        intent.putExtra("type", 1);
        intent.putExtra("advertId", 3);
        intent.putExtra("url", shareLink1);
        startActivity(intent);
    }

    private void initAdvert() {//智能锁
        ExecuteTask.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
                            "goods/queryAdvertList", params);//
                    if (result.getSuccess()) {//
                        JSONObject obj = new JSONObject(result.getResult());
                        if (obj.getInt("code") == 1) {
                            JSONObject objRes = obj.getJSONObject("result");//1
                            JSONObject objRetRes = objRes.getJSONObject("returnResult");//2
                            arrayOfAdvert = FastJsonUtils.getSingleBean(objRetRes.toString(), ArrayOfAdvert.class);
                            {runOnUiThread(new Runnable() {
                                public void run() {
                                    if (arrayOfAdvert != null && arrayOfAdvert.getList() != null
                                            && arrayOfAdvert.getList().size() > 0) {
                                        for (Advert adv : arrayOfAdvert.getList()) {
                                            if(adv.getType() == 1&&adv.getAdvertId() == 2) {
                                                shareUrl = adv.getPicUrl();
                                                shareName = adv.getTitle();
                                                shareLink = adv.getWebUrl();
                                                lrShare.setVisibility(View.VISIBLE);
                                                //ImageFromTxyUtil.loadImage(NoticeActivity.this, shareUrl, irShare);
                                                if(shareUrl.startsWith("http")) {//腾讯云
                                                    ImageFromTxyUtil.loadImage(NoticeActivity.this, shareUrl, irShare);
                                                } else {//本地服务器
                                                    ImageLoaderUtil.getInstance().displayListItemImage(Variables.APP_BASE_URL + shareUrl, irShare);
                                                }
                                                break;
                                            } else {
                                                lrShare.setVisibility(View.GONE);
                                            }
                                        }
                                    } else {
                                        lrShare.setVisibility(View.GONE);
                                    }
                                }
                            });
                            }

                        }
                    } else {
                        showInfo(getString(R.string.A6));
                    }
                } catch (Exception e) {
                    Log.e("NoticeActivity", "initAdvert", e);
                }
            }
        });
    }

    private void initAdvert2() {//盛和公司
        ExecuteTask.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
                            "goods/queryAdvertList", params);
                    if (result.getSuccess()) {
                        JSONObject obj = new JSONObject(result.getResult());
                        if (obj.getInt("code") == 1) {
                            JSONObject objRes = obj.getJSONObject("result");
                            JSONObject objRetRes = objRes.getJSONObject("returnResult");
                            arrayOfAdvert = FastJsonUtils.getSingleBean(objRetRes.toString(), ArrayOfAdvert.class);
                            {runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (arrayOfAdvert != null && arrayOfAdvert.getList() != null
                                                && arrayOfAdvert.getList().size() > 0) {
                                            for (Advert adv : arrayOfAdvert.getList()) {
                                                if(adv.getType() == 1&&adv.getAdvertId() == 3) {
                                                    shareUrl1 = adv.getPicUrl();
                                                    shareName1 = adv.getTitle();
                                                    shareLink1 = adv.getWebUrl();
                                                    lvShare.setVisibility(View.VISIBLE);
                                                    //ImageFromTxyUtil.loadImage(NoticeActivity.this, shareUrl1, ivShare);
                                                    if(shareUrl1.startsWith("http")) {//腾讯云
                                                        ImageFromTxyUtil.loadImage(NoticeActivity.this, shareUrl1, ivShare);
                                                    } else {//本地服务器
                                                        ImageLoaderUtil.getInstance().displayListItemImage(Variables.APP_BASE_URL + shareUrl1, ivShare);
                                                    }
                                                    break;
                                                } else {
                                                    lvShare.setVisibility(View.GONE);
                                                }
                                            }
                                        } else {
                                            lvShare.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }

                        }
                    } else {
                        showInfo(getString(R.string.A6));
                    }
                } catch (Exception e) {
                    Log.e("NoticeActivity", "initAdvert2", e);
                }
            }
        });
    }

    private void initAdvert3() {//新品上线
        ExecuteTask.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
                            "goods/queryAdvertList", params);
                    if (result.getSuccess()) {
                        JSONObject obj = new JSONObject(result.getResult());
                        if (obj.getInt("code") == 1) {
                            JSONObject objRes = obj.getJSONObject("result");
                            JSONObject objRetRes = objRes.getJSONObject("returnResult");
                            arrayOfAdvert = FastJsonUtils.getSingleBean(objRetRes.toString(), ArrayOfAdvert.class);
                            {
                                runOnUiThread(new Runnable() {
                                public void run() {
                                    if (arrayOfAdvert != null && arrayOfAdvert.getList() != null
                                            && arrayOfAdvert.getList().size() > 0) {
                                        for (Advert adv : arrayOfAdvert.getList()) {
                                            if(adv.getType() == 2) {
                                                shareUrl2 = adv.getPicUrl();
                                                shareName2 = adv.getTitle();
                                                shareLink2 = adv.getWebUrl();
                                                lwShare.setVisibility(View.VISIBLE);
                                               // ImageFromTxyUtil.loadImage(NoticeActivity.this, shareUrl2, iwShare);
                                                if(shareUrl2.startsWith("http")) {//腾讯云
                                                    ImageFromTxyUtil.loadImage(NoticeActivity.this, shareUrl2, iwShare);
                                                } else {//本地服务器
                                                    ImageLoaderUtil.getInstance().displayListItemImage(Variables.APP_BASE_URL + shareUrl2, iwShare);
                                                }
                                                break;
                                            } else {
                                                lwShare.setVisibility(View.GONE);
                                            }
                                        }
                                    } else {
                                        lwShare.setVisibility(View.GONE);
                                    }
                                    pull_refresh_scrollview.onRefreshComplete();
                                }
                            });
                            }

                        }
                    } else {
                        showInfo(getString(R.string.A6));
                    }
                } catch (Exception e) {
                    Log.e("NoticeActivity", "initAdvert3", e);
                }
            }
        });
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
                Toast.makeText(NoticeActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }

}