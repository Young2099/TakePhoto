package com.example.croppictrue;

import java.io.File;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class CropPictureActivity extends Activity {

	/** ImageView对象 */
	private ImageView iv_photo;
	private String[] items = new String[]{"选择本地图片", "拍照"};
	/** 头像名称 */
	private static final String IMAGE_FILE_NAME = "image.jpg";

	/** 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		iv_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
	}

	/**
	 * 显示选择对话框
	 */
	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0 :
								Intent intentFromGallery = new Intent();
								intentFromGallery.setType("image/*"); // 设置文件类型
								intentFromGallery
										.setAction(Intent.ACTION_GET_CONTENT);
								startActivityForResult(intentFromGallery,
										IMAGE_REQUEST_CODE);
								break;
							case 1 :
								Intent intentFromCapture = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								// 判断存储卡是否可以用，可用进行存储
								String state = Environment
										.getExternalStorageState();
								if (state.equals(Environment.MEDIA_MOUNTED)) {
									File path = Environment
											.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
									File file = new File(path, IMAGE_FILE_NAME);
									intentFromCapture.putExtra(
											MediaStore.EXTRA_OUTPUT,
											Uri.fromFile(file));
								}

								startActivityForResult(intentFromCapture,
										CAMERA_REQUEST_CODE);
								break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
				case IMAGE_REQUEST_CODE :
					startPhotoZoom(data.getData());
					break;
				case CAMERA_REQUEST_CODE :
					// 判断存储卡是否可以用，可用进行存储
					String state = Environment.getExternalStorageState();
					if (state.equals(Environment.MEDIA_MOUNTED)) {
						File path = Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
						File tempFile = new File(path, IMAGE_FILE_NAME);
						startPhotoZoom(Uri.fromFile(tempFile));
					} else {
						Toast.makeText(getApplicationContext(),
								"未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
					}
					break;
				case RESULT_REQUEST_CODE : // 图片缩放完成后
					if (data != null) {
						getImageToView(data);
					}
					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 340);
		intent.putExtra("outputY", 340);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(this.getResources(), photo);
			iv_photo.setImageDrawable(drawable);
		}
	}
}
