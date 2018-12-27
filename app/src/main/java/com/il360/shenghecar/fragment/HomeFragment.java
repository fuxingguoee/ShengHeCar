package com.il360.shenghecar.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.goods.IPhoneActivity_;
import com.il360.shenghecar.activity.goods.UserActivity_;
import com.il360.shenghecar.activity.home.LoanActivity_;
import com.il360.shenghecar.activity.home.MessageActivity_;
import com.il360.shenghecar.activity.home.NoticeActivity_;
import com.il360.shenghecar.activity.home.PhotoActivity_;
import com.il360.shenghecar.activity.home.ServiceActivity_;
import com.il360.shenghecar.activity.main.UrlToWebActivity_;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.adapter.GoodsAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.GlobalPara;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.common.Variables;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.goods.ArrayOfGoods;
import com.il360.shenghecar.model.goods.Goods;
import com.il360.shenghecar.model.home.Advert;
import com.il360.shenghecar.model.home.ArrayOfAdvert;
import com.il360.shenghecar.model.home.Contact;
import com.il360.shenghecar.model.hua.ArrayOfCardConfig;
import com.il360.shenghecar.model.hua.CardConfig;
import com.il360.shenghecar.util.ContactUtil;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ImageFromTxyUtil;
import com.il360.shenghecar.util.ImageLoaderUtil;
import com.il360.shenghecar.util.SlideViewLayout;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.view.CustomDialog1;
import com.il360.shenghecar.view.MyGridView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EFragment(R.layout.fra_home)
public class HomeFragment extends MyFragment {

	@ViewById RelativeLayout rlytTop;
	@ViewById TextView tvTextClick;

	@ViewById PullToRefreshScrollView pull_refresh_scrollview;//下拉刷新
	@ViewById MyGridView gvIPhone;

	@ViewById ViewPager vp_adv_change;
	@ViewById LinearLayout ll_adv_circle;
	@ViewById LinearLayout llShare;//广告
	@ViewById ImageView ivShare;
	@ViewById ImageView iwShare;//购物中心
	@ViewById ImageView irShare;//用户中心
	@ViewById ImageView pv;//公司产品
	@ViewById ImageView pw;//二手车
	@ViewById LinearLayout pa;//用户留言
	@ViewById LinearLayout pb;//联系方式
	@ViewById
	MyGridView gvMenu;
	private SimpleAdapter adapterMenu;

	@ViewById
	RelativeLayout rlServiceNum;//客服电话1
	@ViewById
	TextView tvEditServiceNum;
	@ViewById
	RelativeLayout rlServiceNum2;//客服电话2

	private ArrayOfAdvert arrayOfAdvert;
	protected ProgressDialog transDialog;

	private GoodsAdapter adapter;
	private List<Goods> list1 = new ArrayList<Goods>();
	private List<CardConfig> list2 = null;
	private static List<Contact> contactList = new ArrayList<Contact>();

	private String shareUrl = "";
	private String shareName = "";
	private String shareLink = "";

	@AfterViews
	void init() {//选择第一个电话号
		if (GlobalPara.getTelephone() != null) {
			String[] phoneArray = GlobalPara.getTelephone().split("/");
			tvEditServiceNum.setText(phoneArray[0]);
		}
		initView();
		initPull();
		initAdvert();
		initAdvert2();
		initData();
		initConfig();
	}

	private void initView() {
		//顶部设置焦点，防止页面加载时滚动
		rlytTop.setFocusable(true);
		rlytTop.setFocusableInTouchMode(true);
		rlytTop.requestFocus();

		adapter = new GoodsAdapter(list1, getActivity());//适配器
		//gvIPhone.setAdapter(adapter);//首页底部的商品展示页
		//gvIPhone.setOnItemClickListener(new OnItemClickListener());
		adapter.notifyDataSetChanged();

		contactList = ContactUtil.getContactList(getActivity());//通讯录

                                                             //获取图标和名字
		int[] gvMenuPics = { R.drawable.my_service,R.drawable.loan,
				R.drawable.replacement,R.drawable.insurance,R.drawable.news};
		String[] strMenu = getResources().getStringArray(R.array.main_manager_menus);
		ArrayList<HashMap<String, Object>> listMenu = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < gvMenuPics.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("image", gvMenuPics[i]);
			map.put("title", strMenu[i]);
			listMenu.add(map);
		}
		adapterMenu = new SimpleAdapter(getActivity(), listMenu,R.layout.griditem_main_home_menus, new String[] { "image",
				"title" }, new int[] { R.id.main_menus_image,R.id.main_menus_title });
		gvMenu.setAdapter(adapterMenu);
		gvMenu.setOnItemClickListener(new OnItemClickListener());
	}

	@Click
	void pw() {//二手车
		Intent intent = new Intent(getActivity(), IPhoneActivity_.class);
		intent.putExtra("ext1", 1);//  二手车列表
		startActivity(intent);
	}

	@Click
	void pv() {//公司产品
		Intent intent = new Intent(getActivity(), IPhoneActivity_.class);
		intent.putExtra("ext1", 0);// 商品列表
		startActivity(intent);
	}

	@Click
	void pa() {//用户留言
		if (UserUtil.judgeUserInfo()) {
			Intent intent = new Intent(getActivity(), MessageActivity_.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(getActivity(), LoginActivity_.class);
			startActivity(intent);
		}
	}

	@Click
	void pb() {//公司联系方式
		Intent intent = new Intent(getActivity(), UrlToWebActivity_.class);
		intent.putExtra("url",GlobalPara.telephoneBookURL );//传URL
		startActivity(intent);
	}

	class OnItemClickListener implements android.widget.AdapterView.OnItemClickListener {//点击跳转

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			try {
				Intent intent = null;
				switch (position) {

					case 0: //我的服务
						if (UserUtil.judgeUserInfo()) {
							intent = new Intent(getActivity(), ServiceActivity_.class);
						} else {
							intent = new Intent(getActivity(), LoginActivity_.class);
						}
						break;
					case 1: // 贷款申请
						if (UserUtil.judgeUserInfo()) {
							intent = new Intent(getActivity(), LoanActivity_.class);
						} else {
							intent = new Intent(getActivity(), LoginActivity_.class);
						}
						break;
					case 2: //车辆置换
						if (UserUtil.judgeUserInfo()) {
							intent = new Intent(getActivity(), PhotoActivity_.class);
						} else {
							intent = new Intent(getActivity(), LoginActivity_.class);
						}
						break;
					case 3: // 车辆保险:
						showDialog();
						break;
					case 4: // 新闻公告:
						intent = new Intent(getActivity(), NoticeActivity_.class);//新闻公告页面
						//intent = new Intent(getActivity(), TimeActivity_.class);//日期选择器<测试用>
						//intent =new Intent(getActivity(),CallActivity_.class);
						break;

					default:
						showInfo("正在火热开发中...");
				}
				if (null == intent) {
					return;
				}
				getActivity().startActivity(intent);
			} catch (Exception e) {
			}
		}
	}
	private void initPull() {//下拉刷新
		pull_refresh_scrollview.setMode(Mode.PULL_FROM_START);//仅支持下拉操作
		pull_refresh_scrollview.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				initAdvert2();
				initData();//商品展示页
			}
		});
	}

	private void initAdvert() {//广告一：1.2交换显示

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

							FragmentActivity fragAct = getActivity();
							if (fragAct != null) {
								fragAct.runOnUiThread(new Runnable() {
									public void run() {
										if (arrayOfAdvert != null && arrayOfAdvert.getList() != null && arrayOfAdvert.getList().size() > 0) {
											for (Advert adv : arrayOfAdvert.getList()) {
												if(adv.getType() == 2) {
													shareUrl = adv.getPicUrl();
													shareName = adv.getTitle();
													shareLink = adv.getWebUrl();
													llShare.setVisibility(View.VISIBLE);
//													ImageFromTxyUtil.loadImage(getActivity(), shareUrl, ivShare);
													if(shareUrl.startsWith("http")) {//腾讯云
														ImageFromTxyUtil.loadImage(getActivity(), shareUrl, ivShare);
													} else {//本地服务器:拼接
														ImageLoaderUtil.getInstance().displayListItemImage(Variables.APP_BASE_URL + shareUrl, ivShare);
													}
													break;
												} else {//隐藏
													llShare.setVisibility(View.GONE);
												}
											}
										} else {
											llShare.setVisibility(View.GONE);
										}
									}
								});
							}
						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					Log.e("HomeFragment", "initAdvert", e);
				} finally {
					FragmentActivity fragAct = getActivity();
					if (fragAct != null) {
						fragAct.runOnUiThread(new Runnable() {
							public void run() {
								if (arrayOfAdvert != null && arrayOfAdvert.getList() != null && arrayOfAdvert.getList().size() > 0) {
									List<String> listUrl = new ArrayList<String>();
									List<String> nameList = new ArrayList<String>();
									List<String> linkList = new ArrayList<String>();
									for (Advert adv : arrayOfAdvert.getList()) {
										if(adv.getType() == 1){
											listUrl.add(adv.getPicUrl());
											nameList.add(adv.getTitle());
											linkList.add(adv.getWebUrl());
										}
									}
									new SlideViewLayout(getActivity(), ll_adv_circle, vp_adv_change, listUrl, nameList,
											linkList, Variables.APP_BASE_URL);
								} else {
									setting();
								}
							}
						});
					}
				}
			}
		});
	}

	private void initAdvert2() {//广告二：
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
							FragmentActivity fragAct = getActivity();
							if (fragAct != null) {
								fragAct.runOnUiThread(new Runnable() {
									public void run() {
										if (arrayOfAdvert != null && arrayOfAdvert.getList() != null
												&& arrayOfAdvert.getList().size() > 0) {
											for (Advert adv : arrayOfAdvert.getList()) {
												if(adv.getType() == 2) {
													shareUrl = adv.getPicUrl();
													shareName = adv.getTitle();
													shareLink = adv.getWebUrl();
													llShare.setVisibility(View.VISIBLE);
													//ImageFromTxyUtil.loadImage(getActivity(), shareUrl, ivShare);
													if(shareUrl.startsWith("http")) {//从腾讯云
														ImageFromTxyUtil.loadImage(getActivity(), shareUrl, ivShare);
													} else {//从本地服务器
														ImageLoaderUtil.getInstance().displayListItemImage(Variables.APP_BASE_URL + shareUrl, ivShare);
													}
													break;
												} else {
													llShare.setVisibility(View.GONE);
												}
											}
										} else {
											llShare.setVisibility(View.GONE);
										}

									}
								});
							}

						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					Log.e("HomeFragment", "initAdvert2", e);
				}
			}
		});
	}

	private void initData() {
		transDialog = ProgressDialog.show(getActivity(), null, "加载中...", true);
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"goods/queryGoodsList", params);
					if (result.getSuccess()) {
						ArrayOfGoods response = FastJsonUtils.getSingleBean(result.getResult(),ArrayOfGoods.class);
						if(response.getCode() == 1) {
							if(response.getResult() != null && response.getResult().size() > 0) {
								list1.clear();
								list1.addAll(response.getResult());
							} else {
								list1.clear();
								showInfo("暂无数据");
							}

						} else {
							list1.clear();
							showInfo(response.getDesc());
						}

					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					showInfo(getString(R.string.A2));
					Log.e("IPhoneActivity", "initData", e);
				} finally {
					FragmentActivity fragAct = getActivity();
					if (fragAct != null) {
						fragAct.runOnUiThread(new Runnable() {
							public void run() {
								if (transDialog != null && transDialog.isShowing()) {
									transDialog.dismiss();
								}
								adapter.notifyDataSetChanged();
								pull_refresh_scrollview.onRefreshComplete();
							}
						});
					}
				}
			}
		});
	}
	private void setting() {
		List<Integer> imgResId = new ArrayList<Integer>();
		imgResId.add(R.mipmap.bg_my);
		new SlideViewLayout(getActivity(), ll_adv_circle, vp_adv_change, imgResId);
	}

	@Click
	void tvTextClick() {
		Intent intent = new Intent(getActivity(), UserActivity_.class);
		startActivity(intent);
	}

//	@Click
//	void llShare() {//新品上线
//		Intent intent = new Intent(getActivity(), UrlToWebActivity_.class);
//		intent.putExtra("supportZoom", false);
//		intent.putExtra("title", shareName);
//		intent.putExtra("url", shareLink);
//		startActivity(intent);
//	}

	public void showInfo(final String info) {
		FragmentActivity fragAct = getActivity();
		if (fragAct != null) {
			fragAct.runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	private void initConfig() {//配置信息接口:
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"goods/queryConfig", params);
					if (result.getSuccess()) {
						JSONObject obj = new JSONObject(result.getResult());
						if (obj.getInt("code") == 1) {
							JSONObject objRes = obj.getJSONObject("result");
							JSONObject objRetRes = objRes.getJSONObject("returnResult");
							ArrayOfCardConfig arrayOfCardConfig = FastJsonUtils.getSingleBean(objRetRes.toString(),ArrayOfCardConfig.class);
							if (arrayOfCardConfig.getList() != null && arrayOfCardConfig.getList().size() > 0) {
								list2 = arrayOfCardConfig.getList();
								GlobalPara.cardConfigList = arrayOfCardConfig.getList();
								for (int i = 0; i < list2.size(); i++) {//获取公司联系方式+保险电话
									if(list2.get(i).getConfigGroup().equals("app") && list2.get(i).getConfigName().equals("insuranceTele")) {
										GlobalPara.insuranceTele = list2.get(i).getConfigValue();
									}else if(list2.get(i).getConfigGroup().equals("app") && list2.get(i).getConfigName().equals("telephoneBookURL")){
										GlobalPara.telephoneBookURL = list2.get(i).getConfigValue();
									}

								}
							}
						} else {
							showInfo(obj.getString("desc"));
						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					Log.e("HomeFragment", "initConfig()", e);
					LogUmeng.reportError(getActivity(), e);
					showInfo(getString(R.string.A2));
				}
			}
		});

	}
	private void showDialog() {//保险电话
		CustomDialog1.Builder builder = new CustomDialog1.Builder(getActivity());
		builder.setTitle("保险电话");
		builder.setMessage(GlobalPara.insuranceTele);
		builder.setPositiveButton("拨打", new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + GlobalPara.insuranceTele));
					startActivity(intent);
					dialog.dismiss();//让对话框消失
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
}
