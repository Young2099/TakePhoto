package com.example.croppictrue;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	/** ImageView对象 */
	private ImageView iv_photo;
	/** TextView对象 */
	private TextView tv_enter_crop;
	/** 选择文件 */
	public static final int TO_SELECT_PHOTO = 1;
	/** 图片路径 */
	private String picPath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		iv_photo.setOnClickListener(this);
		tv_enter_crop = (TextView) findViewById(R.id.tv_enter_crop);
		tv_enter_crop.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.iv_photo:
			intent = new Intent(this, SelectPhotoActivity.class);
			startActivityForResult(intent, TO_SELECT_PHOTO);
			intent = null;
			break;
		case R.id.tv_enter_crop: // 进入下一个界面
			intent = new Intent(this, CropPictureActivity.class);
			startActivity(intent);
			intent = null;
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO) {
			picPath = data.getStringExtra(SelectPhotoActivity.KEY_PHOTO_PATH);
			Bitmap bm = BitmapFactory.decodeFile(picPath);
			Bitmap zoomBitmap = zoomBitmap(bm, 300, 300); // 将图片设置到ImageView
			iv_photo.setBackgroundDrawable(new BitmapDrawable(bm));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 将原图按照指定的宽高进行缩放
	 * 
	 * @param oldBitmap
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	private Bitmap zoomBitmap(Bitmap oldBitmap, int newWidth, int newHeight) {
		int width = oldBitmap.getWidth();
		int height = oldBitmap.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawBitmap(newBitmap, matrix, null);
		return newBitmap;
	}

}
