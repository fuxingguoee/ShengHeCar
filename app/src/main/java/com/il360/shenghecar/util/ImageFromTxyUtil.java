package com.il360.shenghecar.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.il360.shenghecar.activity.WelcomeActivity;
import com.il360.shenghecar.activity.goods.GoodsDetailsActivity;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.tencent.cos.model.COSRequest;
import com.tencent.cos.model.COSResult;
import com.tencent.cos.model.GetObjectRequest;
import com.tencent.cos.task.listener.IDownloadTaskListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFromTxyUtil {
	
	public static void loadImage(Context context, String url, ImageView view) {
		initSignForUrl(context, url, view, 0);
	}

	public static void loadImage(Context context, String url, ImageView view, int width) {
		initSignForUrl(context, url, view, width);
	}

	public static void initSignForUrl(final Context context, final String url, final ImageView view, final int width) {
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					// params.put("token", UserUtil.getToken());
					params.put("picurl", url);
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL, "goods/querySign",
							params);
					if (result.getSuccess()) {
						final JSONObject obj = new JSONObject(result.getResult());
						final String sign = obj.getString("result");
						if (obj.getInt("code") == 1) {
//							if(width > 0){
//								((Activity) context).runOnUiThread(new Runnable() {
//									public void run() {
//
//										ImageLoaderUtil.getInstance().displayListItemImage(url + "?sign=" + sign, view);
//									}
//								});
//							} else {
								loadPic(context, url, sign, view, width);
//							}
						}
					}
				} catch (Exception e) {
				}
			}
		});
	}

	public static void loadPic(final Context context, final String url, String sign, final ImageView view, final int width) {
		final String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "load";

		GetObjectRequest getObjectRequest = new GetObjectRequest(url, savePath);
		getObjectRequest.setSign(sign);
		getObjectRequest.setListener(new IDownloadTaskListener() {
			@Override
			public void onProgress(COSRequest cosRequest, final long currentSize, final long totalSize) {
			}

			@Override
			public void onSuccess(COSRequest cosRequest, COSResult cosResult) {
				
				String fileName = getNameFromUrl(url);
				final String picPath = savePath + File.separator + fileName;

				((Activity) context).runOnUiThread(new Runnable() {
					public void run() {

						BitmapFactory.Options options = new BitmapFactory.Options(); 
						options.inPreferredConfig = Bitmap.Config.RGB_565;  
						Bitmap signBitmap = BitmapFactory.decodeFile(picPath,options);
						if(width > 0) {
							ViewGroup.LayoutParams lp = view.getLayoutParams();
							lp.width = width;
							lp.height = Math.round(width * signBitmap.getHeight() / signBitmap.getWidth());
							view.setLayoutParams(lp);
						}
						view.setImageBitmap(signBitmap);
//						PicUtil.deleteTempFile(picPath);
					}
				});
			}

			@Override
			public void onFailed(COSRequest COSRequest, COSResult cosResult) {
				Log.w("TEST", "code =" + cosResult.code + "; msg =" + cosResult.msg);
			}

			@Override
			public void onCancel(COSRequest arg0, COSResult arg1) {

			}
		});

		WelcomeActivity.getCOSClient().getObject(getObjectRequest);
	}

	public static String getNameFromUrl(String loadUrl) {
		String[] a = loadUrl.split("/");
		String s = a[a.length - 1];
		return s;
	}
}
