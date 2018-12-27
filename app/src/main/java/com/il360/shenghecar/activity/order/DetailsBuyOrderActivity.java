package com.il360.shenghecar.activity.order;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.order.Order;
import com.il360.shenghecar.util.DataUtil;
import com.il360.shenghecar.util.ImageFromTxyUtil;
import com.il360.shenghecar.util.UserUtil;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@EActivity(R.layout.act_details_buy)
public class DetailsBuyOrderActivity extends BaseWidgetActivity {

    @ViewById RelativeLayout rlOrderStatus;
    @ViewById TextView tvStatus;

    @ViewById TextView tvUserInfo;
    @ViewById TextView tvAddress;

    @ViewById ImageView ivOrderPic;
    @ViewById TextView tvOrderName;
    @ViewById TextView tvGoodsPrice;
    @ViewById TextView tvGoodsNum;
    @ViewById TextView tvOrderPrice;

    @ViewById TextView tvOrderNo;
    @ViewById TextView tvOrderTime;

    @ViewById TextView tvRefund;
    @ViewById TextView tvCancel;

    @Extra Order myOrder;

    DecimalFormat df = new DecimalFormat("0.00");

    @AfterViews
    void init() {
        if(myOrder != null) {
            initViews();
        } else {
            showInfo("内部数据传递错误，请返回重试！");
        }

    }

    private void initViews() {
        tvStatus.setText(myOrder.getStatusDesc());

        tvUserInfo.setText(myOrder.getName() + " " + myOrder.getPhone());
        tvAddress.setText(myOrder.getProvince() + myOrder.getCity() + myOrder.getArea() + myOrder.getAddress());

        ImageFromTxyUtil.loadImage(DetailsBuyOrderActivity.this, myOrder.getSmallPic(), ivOrderPic);
        tvOrderName.setText(myOrder.getGoodsDesc());
        tvGoodsPrice.setText("￥" + df.format(myOrder.getGoodsPrice()));
        tvGoodsNum.setText("x" + myOrder.getOrderNum());
        double orderPrice = myOrder.getGoodsPrice().doubleValue() * myOrder.getOrderNum();
        tvOrderPrice.setText("￥" + df.format(orderPrice));

        tvOrderNo.setText("订单编号：" + myOrder.getOrderNo());
        tvOrderTime.setText("下单时间：" + DataUtil.getLongToDate(Long.valueOf(myOrder.getCreateTime())));

        if(myOrder.getStatus() != null && myOrder.getStatus() == 0) {
            tvRefund.setVisibility(View.VISIBLE);
            tvCancel.setVisibility(View.VISIBLE);
        } else {
            tvRefund.setVisibility(View.GONE);
            tvCancel.setVisibility(View.GONE);
        }
    }

    @Click(R.id.rlOrderStatus)
    void rlOrderStatus() {
        Intent intent = new Intent(DetailsBuyOrderActivity.this,OrderSatusActivity_.class);
        intent.putExtra("type","1");
        intent.putExtra("orderNo",myOrder.getOrderNo());
        startActivity(intent);
    }

    @Click(R.id.tvRefund)
    void tvRefund() {
        Intent intent = new Intent(DetailsBuyOrderActivity.this,ReturnGoodsActivity_.class);
        intent.putExtra("myOrder",myOrder);
        startActivityForResult(intent,1);
    }

    @Click(R.id.tvCancel)
    void tvCancel() {
        tvCancel.setClickable(false);
        cancelOrder();
    }

    private void cancelOrder() {
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserUtil.getToken());
                    params.put("orderNo", myOrder.getOrderNo());
                    TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
                            "order/cancelOrder", params);

                    if (result != null && result.getSuccess()) {
                        JSONObject obj = new JSONObject(result.getResult());
                        if (obj.getInt("code") == 1) {
                            showInfo("取消成功");
                            DetailsBuyOrderActivity.this.setResult(1001);
                            DetailsBuyOrderActivity.this.finish();
                        }
                    }
                } catch (Exception e) {
                    Log.e("DetailsBuyOrderActivity", "cancelOrder", e);
                    LogUmeng.reportError(DetailsBuyOrderActivity.this, e);
                } finally {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            tvCancel.setClickable(true);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1001) {
            DetailsBuyOrderActivity.this.setResult(resultCode);
            DetailsBuyOrderActivity.this.finish();
        }
    }

    private void showInfo(final String info) {

        runOnUiThread(new Runnable() {
            public void run() {

                Toast.makeText(DetailsBuyOrderActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }
	
}
