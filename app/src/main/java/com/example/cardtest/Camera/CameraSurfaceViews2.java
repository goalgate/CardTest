package com.example.cardtest.Camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraSurfaceViews2 extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback{

	Context mContext;
	SurfaceHolder mSurfaceHolder;
	private Camera mCamera;

	public CameraSurfaceViews2(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mSurfaceHolder = getHolder();
		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);

	}

	private Camera getCamera2() {
		Camera camera = null;
		try {
			camera = Camera.open(0);
			camera.setDisplayOrientation(0);
		} catch (Exception e) {
			Log.e("exception",e.toString());
		}
		return camera;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = getCamera2();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
			mCamera.setPreviewCallback(this);
			mCamera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
			releaseCamera();
	}


	/**
	 * 释放mCamera
	 */
    public void releaseCamera() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();// 停掉原来摄像头的预览
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void onPreviewFrame(byte[] bytes, Camera camera) {

	}
}
