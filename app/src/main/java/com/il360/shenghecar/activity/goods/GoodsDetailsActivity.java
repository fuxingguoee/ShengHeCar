package com.il360.shenghecar.activity.goods;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.mydata.AuthenActivity_;
import com.il360.shenghecar.activity.user.RecommendActivity_;
import com.il360.shenghecar.adapter.GoodsPicsAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.common.MyApplication;
import com.il360.shenghecar.common.Variables;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.goods.ArrayOfGoodsExt;
import com.il360.shenghecar.model.goods.Goods;
import com.il360.shenghecar.model.goods.GoodsExt;
import com.il360.shenghecar.model.user.ProjectileContent;
import com.il360.shenghecar.model.user.UserAmount;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ImageFromTxyUtil;
import com.il360.shenghecar.util.ImageLoaderUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.view.CustomDialog;
import com.il360.shenghecar.view.ListViewForScrollView;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.act_goods_details)
public class GoodsDetailsActivity extends BaseWidgetActivity {

    @ViewById
    ScrollView scrollView;
    @ViewById
    ImageView ivGoodsPic;
    @ViewById
    TextView tvGoodsDesc;
    @ViewById
    TextView tvGoodsPrice;
    @ViewById
    TextView tvGoodsPrice2;
    @ViewById
    ListViewForScrollView picturesList;
    @ViewById
    TextView tvBuy;

    @Extra
    Goods goods;

    private double size = 0.8;//高比宽:商品详情图片尺寸
    private GoodsPicsAdapter picsAdapter;
    private List<GoodsExt> picsList = new ArrayList<GoodsExt>();

    protected ProgressDialog transDialog;
    DecimalFormat df = new DecimalFormat("0.00");//小数转换

    private ProjectileContent myProjectileContent;

    private UserAmount userAmount;
    private double availableCredit = 0.00;

    public static int listHight = 0;

    @AfterViews
    void init() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

//		initPop();
        initViews();
        initDetailPic();
    }

    private void initViews() {
        picsAdapter = new GoodsPicsAdapter(picsList, GoodsDetailsActivity.this, size);
        picturesList.setAdapter(picsAdapter);
        picsAdapter.notifyDataSetChanged();

        if(goods.getBigPic() != null) {//大图
            if(goods.getBigPic().startsWith("http")) {//腾讯云
                ImageFromTxyUtil.loadImage(GoodsDetailsActivity.this, goods.getBigPic(), ivGoodsPic);
            } else {//本地服务器
                ImageLoaderUtil.getInstance().displayListItemImage(Variables.APP_BASE_URL + goods.getBigPic() , ivGoodsPic);
            }
        }

        tvGoodsDesc.setText(goods.getGoodsName() + goods.getGoodsDesc());
        tvGoodsPrice.setText("￥" + goods.getDiscountPrice());
        tvGoodsPrice2.setText("￥" + goods.getGoodsPrice());
        tvGoodsPrice2.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线
        tvGoodsPrice2.getPaint().setAntiAlias(true);// 抗锯齿
    }

    private void initDetailPic() {
        transDialog = ProgressDialog.show(GoodsDetailsActivity.this, null, "加载中...", true);
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("goodsId", goods.getGoodsId() + "");//相关参数
                    TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
                            "goods/queryGoodsExt", params);//端口+地址
                    if (result.getSuccess()) {
                        ArrayOfGoodsExt pics = FastJsonUtils.getSingleBean(result.getResult(),ArrayOfGoodsExt.class);
                        if(pics.getCode() == 1) {
                            if(pics.getResult() != null && pics.getResult().size() > 0) {
                                picsList.clear();
                                picsList.addAll(pics.getResult());
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("GoodsDetailsActivity", "initDetailPic", e);
                    LogUmeng.reportError(GoodsDetailsActivity.this, e);
                } finally {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            picsAdapter.notifyDataSetChanged();

                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    if (transDialog != null && transDialog.isShowing()) {
                                        transDialog.dismiss();
                                    }
                                }
                            }, 1000);//

                        }
                    });
                }
            }
        });
    }

    /**
     * 设置listView高度
     */
    public static void setListViewHeight(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();
        if(listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for(int i = 0;i< listAdapter.getCount();i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }


    @Click
    void tvBuy() {
//        if (UserUtil.judgeUserInfo()) {
//            if(UserUtil.judgeAuthentication() && UserUtil.judgeBankCard() && UserUtil.judgeOperator() && UserUtil.judgeTaoBao()) {
//                if(UserUtil.getUserInfo().getBext1()!= null && UserUtil.getUserInfo().getBext1() == 1) {
//                    showInfo("暂时无法购买，请稍后再试！");
//                } else {
//                    initCreditLine();
//                }
//            } else {
//                showDialog();
//            }
//        } else {
//            Intent intent = new Intent(GoodsDetailsActivity.this, LoginActivity_.class);
//            startActivity(intent);
//        }

        showInfo("购买请联系客服");
    }

    private void initCreditLine() {
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserUtil.getToken());//参数
                    TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
                            "user/queryUserAmount", params);
                    if (result.getSuccess()) {
                        JSONObject obj = new JSONObject(result.getResult());
                        if (obj.getInt("code") == 1) {
                            JSONObject objRes = obj.getJSONObject("result");
                            userAmount = FastJsonUtils.getSingleBean(objRes.toString(), UserAmount.class);

                            availableCredit = userAmount.getAllAmount().doubleValue() - userAmount.getUseAmount().doubleValue();

//                            if(availableCredit < goods.getGoodsPrice().doubleValue()) {
//                                showInfo("白条额度不足，请提升额度再购买!");
//                            } else {
//                                Intent intent = new Intent(GoodsDetailsActivity.this, PlaceOrderActivity_.class);
//                                intent.putExtra("goods",goods);
//                                intent.putExtra("availableCredit",availableCredit);
//                                startActivity(intent);
//                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("GoodsDetailsActivity", "initCreditLine", e);
                    LogUmeng.reportError(GoodsDetailsActivity.this, e);
                }
            }
        });
    }


    private void initTop() {
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserUtil.getToken());
                    TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
                            "common/loadprojectile", params);
                    if (result.getSuccess()) {
                        JSONObject obj = new JSONObject(result.getResult());
                        if (obj.getInt("code") == 1) {
                            JSONObject objRes = obj.getJSONObject("result");
                            myProjectileContent = FastJsonUtils.getSingleBean(objRes.toString(), ProjectileContent.class);

                        }
                    }
                } catch (Exception e) {
                    Log.e("GoodsDetailsActivity", "initTop", e);
                    LogUmeng.reportError(GoodsDetailsActivity.this, e);
                } finally {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (myProjectileContent != null) {
                                showCoupon(myProjectileContent);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * pop
     */
    private View pop;
    private PopupWindow popWin;

    /**
     * 初始化Pop
     */
    private void initPop() {
        pop = LayoutInflater.from(GoodsDetailsActivity.this).inflate(R.layout.view_pop_login_show, null);
        popWin = new PopupWindow(pop, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        popWin.setFocusable(true);
    }

    private void showCoupon(final ProjectileContent projectileContent) {
        TextView textview = (TextView) pop.findViewById(R.id.tv_periphery);
        ImageView ivCancel = (ImageView) pop.findViewById(R.id.ivCancel);
        ImageView ivShowCoupon = (ImageView) pop.findViewById(R.id.ivShowCoupon);
        ImageView ivShowCoupon2 = (ImageView) pop.findViewById(R.id.ivShowCoupon2);
        TextView tvTitle = (TextView) pop.findViewById(R.id.tvTitle);
        LinearLayout linearLayout = (LinearLayout) pop.findViewById(R.id.linearLayout);
        LinearLayout linearLayout2 = (LinearLayout) pop.findViewById(R.id.linearLayout2);//红包

        if (projectileContent.getFrameId() != null && projectileContent.getFrameId() == 1) {
            linearLayout.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.VISIBLE);
            if (projectileContent.getPicUrl() != null) {
                ImageFromTxyUtil.loadImage(GoodsDetailsActivity.this, projectileContent.getPicUrl(), ivShowCoupon2);
            }
        } else if (projectileContent.getFrameId() != null && projectileContent.getFrameId() == 2) {
            linearLayout.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.GONE);
            if (projectileContent.getPicUrl() != null) {
                ImageFromTxyUtil.loadImage(GoodsDetailsActivity.this, projectileContent.getPicUrl(), ivShowCoupon);
            }
            tvTitle.setText(projectileContent.getDesc());
            MobclickAgent.onEvent(MyApplication.getContextObject(), "drainage_show", projectileContent.getTitle());

        } else if (projectileContent.getFrameId() != null && projectileContent.getFrameId() == 3) {
            linearLayout.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.GONE);
            if (projectileContent.getPicUrl() != null) {
                ImageFromTxyUtil.loadImage(GoodsDetailsActivity.this, projectileContent.getPicUrl(), ivShowCoupon);
            }
            tvTitle.setText(projectileContent.getDesc());
        }

        MobclickAgent.onEvent(MyApplication.getContextObject(), "top_show", projectileContent.getTitle());

        linearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(MyApplication.getContextObject(), "top_go", projectileContent.getTitle());
                if (projectileContent.getFrameId() != null && projectileContent.getFrameId() == 2) {
                    MobclickAgent.onEvent(MyApplication.getContextObject(), "drainage_go", projectileContent.getTitle());
                }

                Uri uri = Uri.parse(projectileContent.getHtmlUrl());
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                GoodsDetailsActivity.this.startActivity(it);

                popWin.dismiss();
            }

        });

        linearLayout2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoodsDetailsActivity.this, RecommendActivity_.class);
                GoodsDetailsActivity.this.startActivity(intent);

                popWin.dismiss();
            }
        });

        textview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popWin.dismiss();//点击pop外围消失
            }
        });

        Looper.myQueue().addIdleHandler(new IdleHandler() {
            @Override
            public boolean queueIdle() {
                popWin.showAtLocation(pop, Gravity.CENTER, 0, 0);
                return false;
            }
        });
    }

//    private void showDialog() {
//        runOnUiThread(new Runnable() {
//            public void run() {
//                CustomDialog.Builder builder = new CustomDialog.Builder(GoodsDetailsActivity.this);
//                builder.setTitle(R.string.app_name);
//                builder.setMessage("您还未设置过交易密码，请前往设置！");
//                builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(GoodsDetailsActivity.this, DealPwdModifyActivity_.class);
//                        startActivity(intent);
//                        dialog.dismiss();
//                    }
//                });
//                builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builder.create().show();
//            }
//        });
//    }

    private void showDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                CustomDialog.Builder builder = new CustomDialog.Builder(GoodsDetailsActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage("请先申请通过信用评估！");
                builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(GoodsDetailsActivity.this, AuthenActivity_.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    private void showInfo(final String info) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (transDialog != null && transDialog.isShowing()) {
                    transDialog.dismiss();
                }
                Toast.makeText(GoodsDetailsActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
