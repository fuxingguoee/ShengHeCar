package com.il360.shenghecar.activity.home;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.coupon.UserCoupon;
import com.il360.shenghecar.util.BitmapUtil;
import com.il360.shenghecar.util.PicUtil;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.view.CustomDialog1;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

@EActivity(R.layout.act_car)
public class CarActivity extends BaseWidgetActivity {
    private Context context = this;

    @ViewById
    RelativeLayout rlIDPhoto1;
    @ViewById
    RelativeLayout rlIDPhoto2;
    @ViewById
    RelativeLayout rlIDPhoto3;
    @ViewById
    RelativeLayout rlIDPhoto4;
    @ViewById
    RelativeLayout rlIDPhoto5;
    @ViewById
    RelativeLayout rlIDPhoto6;
    @ViewById
    RelativeLayout rlIDPhoto7;
    @ViewById
    RelativeLayout rlIDPhoto8;
    @ViewById
    RelativeLayout rlIDPhoto9;

    @ViewById
    ImageView IDPhoto1;//车前照
    @ViewById
    ImageView IDPhoto2;//车后照
    @ViewById
    ImageView IDPhoto3;//车左照
    @ViewById
    ImageView IDPhoto4;//车右照
    @ViewById
    ImageView IDPhoto5;//车牌照
    @ViewById
    ImageView IDPhoto6;//车架照
    @ViewById
    ImageView IDPhoto7;//公里数照片
    @ViewById
    ImageView IDPhoto8;//驾驶证正面照
    @ViewById
    ImageView IDPhoto9;//驾驶证背面照
    @ViewById
    Button submit;
    @Extra
    UserCoupon userCoupon;
    private int flag = 0;//
    private Uri imageFilePath;
    private Bitmap photo1 = null;
    private Bitmap photo2 = null;
    private Bitmap photo3 = null;
    private Bitmap photo4 = null;
    private Bitmap photo5 = null;
    private Bitmap photo6 = null;
    private Bitmap photo7 = null;
    private Bitmap photo8 = null;
    private Bitmap photo9 = null;
    private String sourceURL1 = "";
    private String sourceURL2 = "";
    private String sourceURL3 = "";
    // private String password = null;

//	private static final int IMAGE_REQUEST_CODE = 0;//相册
//	private static final int CAMERA_REQUEST_CODE = 10;//拍照
//	private static final int RESULT_REQUEST_CODE = 2;//剪切图片

    private static Timer timer = null;

    Pattern pt = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{8}$");

    protected ProgressDialog transDialog;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            if(outState != null) {
                String path = "content://media" + imageFilePath.getPath();
                outState.putString("picPath", path);
            }
            super.onSaveInstanceState(outState);
        } catch (Exception e) {
            LogUmeng.reportError(CarActivity.this, e);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            if(savedInstanceState != null) {
                String path = savedInstanceState.getString("picPath");
                imageFilePath = Uri.parse(path);
            }
            super.onRestoreInstanceState(savedInstanceState);
        } catch (Exception e) {
            LogUmeng.reportError(CarActivity.this, e);
        }
    }

    @AfterViews
    void init() {

    }

    @Click
    void submit() {
        if (isOk()) {
            Intent intent = new Intent(context, CeActivity_.class);
            //  intent.putExtra("realName", realName.getText().toString());
            startActivityForResult(intent, 0);

        }
    }

    private void showInfo(final String info) {
        if (transDialog != null && transDialog.isShowing()) {
            transDialog.dismiss();
        }
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(CarActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Click
    void rlIDPhoto1() {
        flag = 1;
        rlIDPhoto1.setClickable(false);
        showDialog();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        rlIDPhoto1.setClickable(true);
                    }
                });
            }
        };
        timer.schedule(task, 1000 * 1);
    }

    @Click
    void rlIDPhoto2() {
        flag = 2;
        rlIDPhoto2.setClickable(false);
        showDialog();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        rlIDPhoto2.setClickable(true);
                    }
                });
            }
        };
        timer.schedule(task, 1000 * 1);
    }

    @Click
    void rlIDPhoto3() {
        flag = 3;
        rlIDPhoto3.setClickable(false);
        showDialog();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        rlIDPhoto3.setClickable(true);
                    }
                });
            }
        };
        timer.schedule(task, 1000 * 1);
    }

    /** 验证输入 */
    private boolean isOk() {
        if (photo1 == null) {
            showInfo("请上传车前照");
        } else if (photo2 == null) {
            showInfo("请上传车后照");
        } else if (photo3 == null) {
            showInfo("请上传车左照");
        } else if (photo4 == null) {
            showInfo("请上传车右照");
        } else if (photo3 == null) {
            showInfo("请上传车左照");
        } else if (photo3 == null) {
            showInfo("请上传车左照");
        } else if (photo3 == null) {
            showInfo("请上传车左照");
        } else if (photo3 == null) {
            showInfo("请上传车左照");
        } else if (photo3 == null) {
            showInfo("请上传车左照");
        } else {
            return true;
        }
        return false;
    }

    // 提示框
    private void showDialog() {
        CustomDialog1.Builder builder = new CustomDialog1.Builder(context);
        builder.setTitle(R.string.app_name);
        builder.setMessage("请选择图片来源");
        builder.setPositiveButton("取消", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("拍照 ",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String state = Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            Intent intent = new Intent(
                                    "android.media.action.IMAGE_CAPTURE");
                            ContentValues values = new ContentValues(3);
                            values.put(MediaStore.Images.Media.DISPLAY_NAME, "Forun Image");
                            values.put(MediaStore.Images.Media.DESCRIPTION, "this is description");
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                            imageFilePath = CarActivity.this.getContentResolver().insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath); // 这样就将文件的存储方式和uri指定到了Camera应用中
//							Intent intent = new Intent(VerifiedActivity.this,
//									CameraActivity.class);
//							intent.putExtra("flag", flag);
                            startActivityForResult(intent, flag);
                        } else {
                            Toast.makeText(CarActivity.this,
                                    "请确认已经插入SD卡", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                        try {
                            timer = new Timer(true);
                            timer.schedule(new TimerTask() {

                                @Override
                                public void run() {
                                    CarActivity.this.finish();
                                }
                            }, 180 * 1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUmeng.reportError(CarActivity.this, e);
                        }
                    }
                });
        builder.create().show();
    }


    private Bitmap getImageToView(Intent data) {//
        Bitmap photo = null;
        if (data == null) {
            String filePath = PicUtil.uri2Path(imageFilePath, this);
            photo = PicUtil.getSmallBitmap(filePath);
            PicUtil.deleteTempFile(filePath);
        } else {
            String filePath = BitmapUtil.getimgpath(data, this);
            // 是否选择图片类型
            if (filePath.endsWith("jpg") || filePath.endsWith("png")
                    || filePath.endsWith("JPG") || filePath.endsWith("PNG")) {
                photo = PicUtil.getSmallBitmap(filePath);
            } else {
                Toast.makeText(CarActivity.this, R.string.C14,
                        Toast.LENGTH_SHORT).show();
            }
        }
        return photo;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode != android.app.Activity.RESULT_CANCELED) {
                switch (requestCode) {
                    case 1:
                        photo1 = getImageToView(data);
                        IDPhoto1.setImageBitmap(photo1);
                        initPic(1,photo1);
                        break;
                    case 2:
                        photo2 = getImageToView(data);
                        IDPhoto2.setImageBitmap(photo2);
                        initPic(2,photo2);
                        break;
                    case 3:
                        photo3 = getImageToView(data);
                        IDPhoto3.setImageBitmap(photo3);
                        initPic(3,photo3);
                        break;
                }
            }
        } catch (Exception e) {
            Log.e("TypeActivity", "获取照片异常", e);
            LogUmeng.reportError(CarActivity.this, e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private String initData(final Bitmap photo){
        JSONObject json = new JSONObject();
        try {
            json.put("type", "1");
            json.put("imgEnd", ".jpg");
            json.put("imgName","img_"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) );
            json.put("imgData", PicUtil.bitmapToString(photo));
            json.put("token", UserUtil.getToken());
            return json.toString();

        } catch (Exception e) {
            Log.e("CarActivity", "initData", e);
            LogUmeng.reportError(CarActivity.this, e);
        }
        return null;
    }

    private final static String ALBUM_PATH  = Environment.getExternalStorageDirectory() + "/download_test/";

    /**
     * 保存文件
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public void saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }//
        File myCaptureFile = new File(ALBUM_PATH + fileName+ ".jpg");

        FileOutputStream outputStream = new FileOutputStream(myCaptureFile); // 文件输出流

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        outputStream.write(baos.toByteArray()); // 写入sd卡中
        outputStream.close(); // 关闭输出流
    }

    @Override
    protected void onDestroy() {//销毁
        super.onDestroy();
        try {
            if(!photo1.isRecycled()) {
                photo1.recycle();
            }
            if(!photo2.isRecycled()) {
                photo2.recycle();
            }
            if(!photo3.isRecycled()) {
                photo3.recycle();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    private void initPic(final int photoNo, final Bitmap photo) {//图片上传

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String Data = initData(photo);
                    if (Data != null) {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("uploadFileJson", Data);//参数
                        TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL, "upload/uploadPic", params);
                        if (result.getSuccess()) {
                            if (ResultUtil.isOutTime(result.getResult()) != null) {
                                showInfo(ResultUtil.isOutTime(result.getResult()));
                                Intent intent = new Intent(CarActivity.this, LoginActivity_.class);
                                startActivity(intent);
                            } else {
                                JSONObject obj = new JSONObject(result.getResult());
                                if (obj.getInt("code") == 1) {
                                    if (photoNo == 1) {
                                        sourceURL1 = "";
                                    } else if (photoNo == 2) {
                                        sourceURL2 = "";
                                    } else if (photoNo == 3) {
                                        sourceURL3 = "";
                                    }
                                } else {
                                    showInfo("上传失败，请重试");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("UserInfoFragment", "initPic", e);
                    LogUmeng.reportError(CarActivity.this, e);
                }

            }

        }).start();
    }
}


