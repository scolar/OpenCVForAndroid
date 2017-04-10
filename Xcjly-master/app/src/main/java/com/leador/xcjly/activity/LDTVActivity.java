package com.leador.xcjly.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.leador.TV.Enum.DataTypeEnum;
import com.leador.TV.Enum.ImageBtnShowMode;
import com.leador.TV.Enum.ImageTypeEnum;
import com.leador.TV.Exception.TrueMapException;
import com.leador.TV.Listeners.ImageCompassListener;
import com.leador.TV.Listeners.ImageGetListener;
import com.leador.TV.Listeners.ImageStateListener;
import com.leador.TV.Listeners.ImageTouchEvent;
import com.leador.TV.Listeners.ImageTouchListener;
import com.leador.TV.Marker.MarkerAttribute;
import com.leador.TV.Marker.MarkerInfo;
import com.leador.TV.Station.StationInfo;
import com.leador.TV.Station.StationInfoEx;
import com.leador.TV.TrueVision.TrueVision;
import com.leador.TV.Utils.ConfigureUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LDTVActivity extends Activity implements ImageStateListener,
		ImageTouchListener, ImageCompassListener, ImageGetListener {
	TrueVision ldTV;
	// Spinner tvType;
	int itemIndex;
	// int dropSelectIndex;
	// ArrayAdapter<String> adapter;
	String[] m;
	boolean resh = false;
	// 获取窗口管理器
	WindowManager windowManager;
	// 获取屏幕大小
	Display screenDisplay;
	LinearLayout parent1;
	public String mSampleDirPath;

	@Override
	protected void onResume() {
		super.onResume();
		ldTV.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		ldTV.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ldtv);
		mSampleDirPath = getIntent().getStringExtra("path");
		ldTV = (TrueVision) findViewById(R.id.ldtv);
		findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		findViewById(R.id.tv_measure).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LDTVActivity.this,
						MeasureActivity.class);
				intent.putExtra("path", mSampleDirPath);
				startActivity(intent);
			}
		});

		InitTv();

	}

	private String beforeTag = "Before";
	private String afterTag = "After";
	private String zoominTag = "Zoomin";
	private String zoomOutTag = "Zoomout";
	private String changeTag = "change";

	//
	// String dataPath = Environment.getExternalStorageDirectory().getPath()
	// + "//truemapPadPro/010";
	private void InitTv() {
		// String dataPathTV =
		// Environment.getExternalStorageDirectory().getPath()
		// + "/gongandata/2m/dmi";
		// String dataPathTV =
		// Environment.getExternalStorageDirectory().getPath()
		// + "/dtyDmi";
		ConfigureUtils.mJumpSize = 1;
		// String dataPathTV = getDataPath("gongandata") + "2m/dmi";
		// String dataPathTV = getDataPath("gongandata") + "5m/dmi";
		// String dataPathTV = getDataPath("changshadmi");
		try {
			// ldTV.ldTVInit(DataTypeEnum.offLine_Type, dataPathTV, licenseKey);
			ldTV.ldTVInit(DataTypeEnum.offLine_Type, mSampleDirPath);
			ldTV.setImageType(ImageTypeEnum.oneDMI_Type);
		} catch (TrueMapException e) {
			e.printStackTrace();
		}

		ldTV.setOnTouchViewClick(this);
		ldTV.setOnStateChanged(this);
		ldTV.setOnImgCompassListener(this);
		ldTV.setOnGetImage(this);

		try {
			// 2m DMI 000264-1-201309270430580324
			ldTV.locImgByImgID("000264-1-201309270430580324", mSampleDirPath);
			// 2m PANO 121.3674466070 28.3975952020
			// ldTV.locImgByLonlat(121.3674466070,28.3975952020,0.005,"X");
			// 5m DMI 107.4033234430 29.7054139330
			// ldTV.locImgByLonlat(107.4033234430, 29.7054139330, 0.005, "1");
			// 测量 000000-1-201602020232310918 5M
			// ldTV.locImgByImgID("000000-1-201602020232310918");
		} catch (TrueMapException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param fileName
	 *            传入数据文件夹名称
	 * @return 该文件夹路径，如果没有则返回空""
	 */
	public static String getDataPath(String fileName) {
		String dataPathTV = "";
		List<String> lists = getExterPath();
		List<String> pathList = new ArrayList<String>();
		for (int i = 0; i < lists.size(); i++) {
			String str = lists.get(i);
			if (str != null && !str.equals("")) {
				pathList.add(str + "/" + fileName + "/");
			}
		}
		for (int j = 0; j < pathList.size(); j++) {
			String string = pathList.get(j);
			File fileRes = new File(string);
			if ((fileRes.exists())) {
				dataPathTV = string;
			}
		}
		return dataPathTV;
	}

	/**
	 *
	 * 得到扩展口路径
	 *
	 * @return mnt下各个名称路径
	 */
	public static List<String> getExterPath() {
		List<String> sdcard_path = new ArrayList<String>();
		// 得到路径
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			String line;
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				if (line.contains("secure"))
					continue;
				if (line.contains("asec"))
					continue;

				if (line.contains("fat")) {
					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						sdcard_path.add(columns[1]);
					}
				} else if (line.contains("fuse")) {
					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						sdcard_path.add(columns[1]);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sdcard_path;
	}

	@Override
	public void controlBtnSelected(String controlBtnTag) {
		StationInfo currentStation = new StationInfo();
		try {
			if (controlBtnTag.equals(zoominTag)) {
				ldTV.zoomIn();
			} else if (controlBtnTag.equals(zoomOutTag)) {
				ldTV.zoomOut();
			} else if (controlBtnTag.equals(beforeTag)) {
				if (isPlay) {
					isPlay = false;
					return;
				}
				currentStation = ldTV.getCurrentStationJuction();
				if (currentStation.isNode()
						&& currentStation.getNodeIsBegin() == 1) {
					selectNode(currentStation);
				} else {
					ldTV.findPreImage();
				}
			} else if (controlBtnTag.equals(afterTag)) {
				if (isPlay) {
					isPlay = false;
					return;
				}
				currentStation = ldTV.getCurrentStationJuction();
				if (currentStation.isNode()
						&& currentStation.getNodeIsBegin() == 0) {
					selectNode(currentStation);
				} else {
					ldTV.findNextImage();
				}
			}
		} catch (TrueMapException e) {
			ShowToast(e.getMessage());
		}

	}

	boolean isbeginMark = true;
	private Timer hodingTimer = new Timer();
	private TimerTask hodingTask;
	Handler hodingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				if (isPlay == false) {
					endPlayImage();
					return;
				}
				if (holeingControlBtnTag.equals(preTag)) {
					ldTV.setControlShowMode("After", ImageBtnShowMode.visable);
					ldTV.setControlShowMode("Before", ImageBtnShowMode.down);
				} else if (holeingControlBtnTag.equals(nextTag)) {
					ldTV.setControlShowMode("After", ImageBtnShowMode.down);
					ldTV.setControlShowMode("Before", ImageBtnShowMode.visable);
				}

				StationInfo currentStation = ldTV.getCurrentStationJuction();
				if (holeingControlBtnTag.equals(preTag)) {
					if (currentStation.isNode()
							&& currentStation.getNodeIsBegin() == 1) {
						selectNode(currentStation);
					} else {
						ldTV.findPreImage();
					}
				} else if (holeingControlBtnTag.equals(nextTag)) {
					currentStation = ldTV.getCurrentStationJuction();
					if (currentStation.isNode()
							&& currentStation.getNodeIsBegin() == 0) {
						selectNode(currentStation);
					} else {
						ldTV.findNextImage();
					}
				}
			} catch (TrueMapException e) {
				endPlayImage();
			}
			super.handleMessage(msg);
		}
	};
	String holeingControlBtnTag = "";
	String preTag = "prePlay";
	String nextTag = "nextPlay";
	boolean isPlay = false;

	@Override
	public void controlBtnHold(String controlBtnTag) {
		if (controlBtnTag.equals("Before")) {
			holeingControlBtnTag = preTag;
			isPlay = true;
		} else if (controlBtnTag.equals("After")) {
			holeingControlBtnTag = nextTag;
			isPlay = true;
		}
		beginPlayImage();
	}

	int whichCity = 0;

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "开始离线测量");
		menu.add(1, 1, 1, "切换到在线影像");
		/*
		 * menu.add(0, 0, 0, "换一个点"); menu.add(1, 1, 1, "开始测量"); menu.add(2, 2,
		 * 2, "图片浏览"); menu.add(3, 3, 3, "切换类型"); menu.add(4, 4, 4, "单张");
		 * menu.add(5, 5, 5, "拼接"); menu.add(6, 6, 6, "全景"); menu.add(7, 7, 7,
		 * "添加图像标注"); menu.add(8, 8, 8, "添加symbol标注"); menu.add(9, 9, 9, "换图片");
		 */
		return true;
	}

	int thisCount = 0;

	public boolean onOptionsItemSelected(MenuItem item) {
		// int item_id = item.getItemId();
		// switch (item_id) {
		// case 0:
		// Intent intent = new Intent(this, MeasureActivity.class);
		// startActivity(intent);
		// break;
		// case 1:
		// Intent intent2 = new Intent(this, LDTVOnlineActivity.class);
		// startActivity(intent2);
		// finish();
		// break;
		// }
		return true;
	}

	int a = 0;

	void showExit() {
		AlertDialog dlg = new AlertDialog.Builder(LDTVActivity.this)
				.setTitle("退出程序").setMessage("确认退出程序？")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 关闭当前的Activity
						LDTVActivity.this.finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub\

					}
				}).create();
		dlg.show();
	}

	EditText tvID;
	EditText tvText;
	Spinner tvType;
	int dropSelectIndex;
	ArrayAdapter<String> adapter;
	TextView markerAttkey1;
	TextView markerAttkey2;
	TextView markerAttkey3;
	EditText markerAttValue1;
	EditText markerAttValue2;
	EditText markerAttValue3;
	ArrayList<String> keys = null;

	@Override
	public void imageHold(ImageTouchEvent event) {

	}

	@Override
	public void imageTouch(ImageTouchEvent event) {
	}

	protected void ShowToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void imageFling(ImageTouchEvent eventBegin,
						   ImageTouchEvent eventEnd, float velocityX, float velocityY) {

	}

	@Override
	public void imageonDoubleTap(ImageTouchEvent event) {

	}

	@Override
	public void imageIDChanged(String imageID) {

	}

	private void beginPlayImage() {
		if (isPlay) {
			if (hodingTimer != null && hodingTask != null) {
				hodingTimer.cancel();
				hodingTask.cancel();
			}
			hodingTimer = new Timer();
			hodingTask = new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message message = new Message();
					message.what = 1;
					hodingHandler.sendMessage(message);
				}
			};
			hodingTimer.schedule(hodingTask, 2000, 2000);
		}
	}

	private void endPlayImage() {
		hodingTask.cancel();
		hodingTimer.cancel();
		ldTV.setControlShowMode("Before", ImageBtnShowMode.visable);
		ldTV.setControlShowMode("After", ImageBtnShowMode.visable);
	}

	/**
	 * 路口选择方法
	 *
	 * @param currentStation
	 *            当前测点
	 */
	// boolean juctionIsShowing = false;
	boolean continuePlay = false;
	AlertDialog alert;

	private void selectNode(StationInfo currentStation) throws TrueMapException {
		// if (juctionIsShowing == false) {
		// juctionIsShowing = true;

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}


	@Override
	public void yawChanged(double yaw) {
		Log.i("yawChanged", "yawChanged" + yaw);
	}

	String menKey = "men";

	boolean bshowImg = false;

	// 点击影像上面的部件，回调函数，可能有些部件叠在一起 所以这个回调得到的是一个部件的集合
	@Override
	public void imageMarkerSelected(ImageTouchEvent event,
									ArrayList<MarkerInfo> markerInfos) {
		final ArrayList<MarkerInfo> allMarkers = markerInfos;
		final CharSequence[] items = new CharSequence[markerInfos.size()];
		for (int i = 0; i < allMarkers.toArray().length; i++) {
			items[i] = allMarkers.get(i).getText();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("删除哪一个标注点");
		builder.setSingleChoiceItems(items, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						try {
							String markerGuid = allMarkers.get(item).getId();
							ArrayList<MarkerAttribute> attList = TrueVision
									.getMarkerAttribute(markerGuid);
							String message = "标注的ID为" + markerGuid
									+ ",\n标注的Text为"
									+ allMarkers.get(item).getText() + "\n";
							for (int i = 0; i < attList.size(); i++) {
								message += "属性:     "
										+ attList.get(i).getAttributeName();
								message += "值:      "
										+ attList.get(i).getAttributeValue()
										+ "\n";
							}
							ShowToast(message);
							itemIndex = item;
							ldTV.lightMarker(markerGuid);
						} catch (TrueMapException e) {
							e.printStackTrace();
						}
					}
				});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String order = allMarkers.get(itemIndex).getId();
				try {
					ldTV.deleteMarker(order);
					if (a == 1) {
						TrueVision.deleteMarkerFromDb(order);
					}
				} catch (TrueMapException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.show();

	}

	@Override
	public void zoomScalseChanged(double zoomScale) {
		double configScaleRate = ConfigureUtils.zoomScaleRate;
		if (Math.abs(zoomScale) == 0) {
			ldTV.setControlShowMode(zoominTag, ImageBtnShowMode.disable);
			ldTV.setControlShowMode(zoomOutTag, ImageBtnShowMode.visable);
		} else if (Math.abs(zoomScale - 1) <= 0.01) {
			ldTV.setControlShowMode(zoominTag, ImageBtnShowMode.visable);
			ldTV.setControlShowMode(zoomOutTag, ImageBtnShowMode.disable);
		} else {
			ldTV.setControlShowMode(zoominTag, ImageBtnShowMode.visable);
			ldTV.setControlShowMode(zoomOutTag, ImageBtnShowMode.visable);
		}
	}

	@Override
	public void imageTypeChanged(String imageType) {
		if (imageType.equals(ImageTypeEnum.panorama_Type)) {
			ldTV.setControlShowMode(zoominTag, ImageBtnShowMode.invisable);
			ldTV.setControlShowMode(zoomOutTag, ImageBtnShowMode.invisable);
			ldTV.setControlShowMode(beforeTag, ImageBtnShowMode.visable);
			ldTV.setControlShowMode(afterTag, ImageBtnShowMode.visable);
			// ldTV.setControlShowMode(changeTag, ImageBtnShowMode.visable);
		} else {
			ldTV.setControlShowMode(zoominTag, ImageBtnShowMode.visable);
			ldTV.setControlShowMode(zoomOutTag, ImageBtnShowMode.visable);
			ldTV.setControlShowMode(beforeTag, ImageBtnShowMode.visable);
			ldTV.setControlShowMode(afterTag, ImageBtnShowMode.visable);
			// ldTV.setControlShowMode(changeTag, ImageBtnShowMode.visable);
		}

	}

	@Override
	public void imageClick(ImageTouchEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void imageGetOver(boolean ishaveImage, String image) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNeedUpdateCompassPos(StationInfoEx station) {
		// TODO Auto-generated method stub

		// ArrayList<Point> pnts = new ArrayList<Point> ();
		// Point pnt1 = new Point(30.588412 ,114.296773262193);
		// pnt1.setName("人民法院");
		// pnts.add(pnt1);
		//
		// ldTV.updateCompass(pnts);
	}

	@Override
	public void getCamerasComplete(boolean isOk, TrueMapException ex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getStationComplete(boolean isOk, String imageId,
								   TrueMapException ex) {
		Log.i("getStationComplete", imageId);
		if (isOk) {
			// try {
			// double lon = ldTV.getCurrentStation().getCoord().getLon();
			// double lat = ldTV.getCurrentStation().getCoord().getLat();
			// } catch (TrueMapException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

		}

	}

	@Override
	public void getSmallImageComplete(boolean isOk, String imageId,
									  TrueMapException ex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getCutImageComplete(boolean isOk, String imageId,
									TrueMapException ex) {
		// TODO Auto-generated method stub

	}

}
