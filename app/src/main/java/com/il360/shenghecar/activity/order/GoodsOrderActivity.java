package com.il360.shenghecar.activity.order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.adapter.OrderBuyAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.order.ArrayOfOrder;
import com.il360.shenghecar.model.order.Order;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.view.ListViewForScrollView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lepc on 2018/7/10.
 */
@EActivity(R.layout.act_goods_order)
public class GoodsOrderActivity extends BaseWidgetActivity {

    @ViewById
    PullToRefreshScrollView pull_refresh_scrollview;
    @ViewById
    ListViewForScrollView goodsOrderList;

    private List<Order> myOrder = new ArrayList<Order>();
    private OrderBuyAdapter myAdapter;

    /**
     * 当前页码
     **/
    private int pageNo = 1;
    /**
     * 默认每页加载个数
     **/
    private int pageSize = 20;

    private ArrayOfOrder arrayOfOrder;

    protected ProgressDialog transDialog;

    @AfterViews
    void init() {
        initViews();

        transDialog = ProgressDialog.show(GoodsOrderActivity.this, null, "加载中...", true);
        initData();
        initRefreshListener();
    }

    private void initRefreshListener() {
        pull_refresh_scrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                goodsOrderList.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageNo = 1;
                        initData();
                    }
                }, 500);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (isLast()) {
                    goodsOrderList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pull_refresh_scrollview.onRefreshComplete();
                            showInfo("已经到底部了！");
                        }
                    }, 1000);
                } else {
                    pageNo++;
                    initData();
                }
            }
        });
    }

    protected boolean isLast() {
        if (arrayOfOrder != null && pageNo * pageSize < arrayOfOrder.getTotalCount()) {
            return false;
        } else {
            return true;
        }
    }

    private void initViews() {
        pull_refresh_scrollview.setMode(PullToRefreshBase.Mode.BOTH);
        myAdapter = new OrderBuyAdapter(myOrder, GoodsOrderActivity.this, new ListCallback());
        goodsOrderList.setAdapter(myAdapter);
        goodsOrderList.setOnItemClickListener(new goodsOrderListClickListener());
        myAdapter.notifyDataSetChanged();
    }

    private void initData() {
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("pageNo", pageNo + "");//相关参数
                    params.put("pageSize", pageSize + "");
                    params.put("token", UserUtil.getToken());
                    TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
                            "order/queryOrderList", params);
                    if (result.getSuccess()) {
                        JSONObject obj = new JSONObject(result.getResult());
                        if (obj.getInt("code") == 1) {
                            JSONObject objRes = obj.getJSONObject("result");
                            JSONObject objResult = objRes.getJSONObject("returnResult");
                            arrayOfOrder = FastJsonUtils.getSingleBean(objResult.toString(), ArrayOfOrder.class);
                            if (arrayOfOrder != null && arrayOfOrder.getList() != null && arrayOfOrder.getList().size() > 0) {
                                if (pageNo == 1) {
                                    myOrder.clear();
                                }
                                myOrder.addAll(arrayOfOrder.getList());
                            }
                        } else {
                            showInfo(obj.getString("desc"));
                        }
                    } else {
                        showInfo(getString(R.string.A6));
                    }
                } catch (Exception e) {
                    Log.e("GoodsOrderActivity", "initData", e);
                    LogUmeng.reportError(GoodsOrderActivity.this, e);
                } finally {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            if (transDialog != null && transDialog.isShowing()) {
                                transDialog.dismiss();
                            }
                            myAdapter.notifyDataSetChanged();
                            pull_refresh_scrollview.onRefreshComplete();
                        }
                    });

                }
            }
        });
    }

    class goodsOrderListClickListener implements android.widget.AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(GoodsOrderActivity.this, DetailsBuyOrderActivity_.class);
            intent.putExtra("myOrder", myOrder.get(position));
            startActivityForResult(intent, 1);
        }
    }

    public class ListCallback {
        public void initList() {
            myOrder.clear();
            pageNo = 1;
            initData();
        }

        public void goToRefund(Order myOrder) {
            Intent intent = new Intent(GoodsOrderActivity.this, ReturnGoodsActivity_.class);
            intent.putExtra("myOrder",myOrder);
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1001) {
            myOrder.clear();
            pageNo = 1;
            initData();
        }
    }

    public void showInfo(final String info) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (transDialog != null && transDialog.isShowing()) {
                    transDialog.dismiss();
                }
                Toast.makeText(GoodsOrderActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
