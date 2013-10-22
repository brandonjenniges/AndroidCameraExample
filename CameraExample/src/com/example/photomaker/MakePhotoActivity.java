package com.example.photomaker;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MakePhotoActivity extends Activity implements PictureCallback {

	CameraSurfaceView cameraSurfaceView;
	Button shutterButton;
	Button zoomInButton;
	Button zoomOutButton;
	int zoom = 0;
	private final static String DEBUG_TAG = "MakePhotoActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// set up our preview surface
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		cameraSurfaceView = new CameraSurfaceView(this);
		preview.addView(cameraSurfaceView);

		// grab out shutter button so we can reference it later
		shutterButton = (Button) findViewById(R.id.shutter_button);
		shutterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				takePicture();
			}
		});

		zoomInButton = (Button) findViewById(R.id.zoom_in);
		zoomInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				zoomIn();

			}
		});

		zoomOutButton = (Button) findViewById(R.id.zoom_out);
		zoomOutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				zoomOut();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_camera, menu);
		return true;
	}

	private void takePicture() {
		shutterButton.setEnabled(false);
		cameraSurfaceView.takePicture(this);
	}

	private void zoomOut() {
		zoom -= 10;
		if (zoom < 0) {
			zoom = 0;
		}
		zoom = cameraSurfaceView.changeZoom(zoom);
	}

	private void zoomIn() {
		zoom = cameraSurfaceView.changeZoom(zoom += 10);
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {

		File pictureFileDir = getDir();

		if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

			Log.d(MakePhotoActivity.DEBUG_TAG,
					"Can't create directory to save image.");
			Toast.makeText(getApplicationContext(),
					"Can't create directory to save image.", Toast.LENGTH_LONG)
					.show();
			return;

		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String date = dateFormat.format(new Date());
		String photoFile = "TruckInspection_123456_" + date + ".jpg";

		String filename = pictureFileDir.getPath() + File.separator + photoFile;

		File pictureFile = new File(filename);

		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			fos.write(data);
			fos.close();
			Toast.makeText(getApplicationContext(),
					"New Image saved:" + photoFile, Toast.LENGTH_LONG).show();
		} catch (Exception error) {
			Log.d(MakePhotoActivity.DEBUG_TAG, "File" + filename
					+ "not saved: " + error.getMessage());
			Toast.makeText(getApplicationContext(),
					"Image could not be saved.", Toast.LENGTH_LONG).show();
		}

		camera.startPreview();
		shutterButton.setEnabled(true);
	}

	private File getDir() {
		File sdDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		return new File(sdDir, "CameraAPIDemo");
	}
}