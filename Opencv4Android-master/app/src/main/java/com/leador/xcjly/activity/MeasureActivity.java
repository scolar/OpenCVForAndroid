package com.leador.xcjly.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.leador.TV.Exception.TrueMapException;
import com.leador.TV.Listeners.ImageTouchEvent;
import com.leador.TV.Listeners.ImageTouchListener;
import com.leador.TV.Marker.MarkerInfo;
import com.leador.TV.Measure.TrueVisionMeasure;
import com.leador.TV.Station.Coord;
import com.leador.TV.TrueVision.TrueVision;
import com.leador.xcjly.jni.MatchJni;

import java.util.ArrayList;


public class MeasureActivity extends Activity {
    TrueVisionMeasure meaSureView1;
    TrueVisionMeasure meaSureView2;
    private String path;
    private TextView mTextView, mTextViewMeasure;
    private ProgressDialog progressDialog;
    private int[] values;
    private ImageTouchEvent imageTouchEvent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_measure);

        findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        meaSureView1 = (TrueVisionMeasure) findViewById(R.id.ldmeasuretv1);
        meaSureView2 = (TrueVisionMeasure) findViewById(R.id.ldmeasuretv2);
        mTextViewMeasure = (TextView) findViewById(R.id.tv_measure);
        path = getIntent().getStringExtra("path");
        meaSureView1.setBitmap(path + "/1.jpg");
        meaSureView2.setBitmap(path + "/2.jpg");
        new OpenSetTask().execute();
        //meaSureView1.zoomScale = 3.0;
        mTextView = (TextView) findViewById(R.id.result);

        findViewById(R.id.tv_measure).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
               if (mode == 1) {
                    mode = 2;
                    mTextViewMeasure.setText("测线");
                } else if (mode == 2) {
                    mode = 1;
                    mTextViewMeasure.setText("测点");
                }
            }
        });

        meaSureView1.setOnTouchViewClick(new ImageTouchListener() {
            @Override
            public void imageTouch(ImageTouchEvent event) {
            }

            @Override
            public void imageClick(ImageTouchEvent event) {
                if (mode == drawPointMode) {
                    if (meaSureView2.getPoints().size() == 0
                            && meaSureView1.getPoints().size() == 0) {
                        int[] points2 = {(int) (event.imageX), (int) (event.imageY)};
                        meaSureView1.addPoint(event);
                        imageTouchEvent = new ImageTouchEvent(this);
                        new OpenTask().execute(points2);
                    } else if (meaSureView1.getPoints().size() == 1
                            && meaSureView2.getPoints().size() == 1) {
                        meaSureView1.deletePoint(0);
                        meaSureView1.addPoint(event);
                        meaSureView2.deletePoint(0);
                        int[] points = {(int) (event.imageX), (int) (event.imageY)};
                        imageTouchEvent = new ImageTouchEvent(this);
                        new OpenTask().execute(points);
                    } else if (meaSureView1.getPoints().size() == 2
                            && meaSureView2.getPoints().size() == 2) {
                        meaSureView1.getPoints().clear();
                        meaSureView2.getPoints().clear();
                        int[] points2 = {(int) (event.imageX), (int) (event.imageY)};
                        meaSureView1.addPoint(event);
                        imageTouchEvent = new ImageTouchEvent(this);
                        new OpenTask().execute(points2);
                    }
                    //testMesurePoint();
                } else if (mode == drawLineMode) {
                    if (meaSureView2.getPoints().size() == 0
                            && meaSureView1.getPoints().size() == 0) {
                        int[] points2 = {(int) (event.imageX), (int) (event.imageY)};
                        meaSureView1.addPoint(event);
                        imageTouchEvent = new ImageTouchEvent(this);
                        new OpenTask().execute(points2);
                    } else if (meaSureView1.getPoints().size() == 1
                            && meaSureView2.getPoints().size() == 1) {
                        int[] points2 = {(int) (event.imageX), (int) (event.imageY)};
                        meaSureView1.addPoint(event);
                        imageTouchEvent = new ImageTouchEvent(this);
                        new OpenTask().execute(points2);
                    } else if (meaSureView1.getPoints().size() == 2
                            && meaSureView2.getPoints().size() == 2) {
                        meaSureView1.deletePoint(1);
                        meaSureView2.deletePoint(1);
                        //meaSureView2.addPoint(event);
                        int[] points2 = {(int) (event.imageX), (int) (event.imageY)};
                        meaSureView1.addPoint(event);
                        imageTouchEvent = new ImageTouchEvent(this);
                        new OpenTask().execute(points2);
                    }
                    //testMesureLine();
                }
                //meaSureView1.invalidate();
                //meaSureView2.invalidate();
            }

            @Override
            public void imageHold(ImageTouchEvent event) {

            }

            @Override
            public void imageonDoubleTap(ImageTouchEvent event) {

            }

            @Override
            public void imageFling(ImageTouchEvent eventBegin,
                                   ImageTouchEvent eventEnd, float velocityX, float velocityY) {

            }

            @Override
            public void imageMarkerSelected(ImageTouchEvent event,
                                            ArrayList<MarkerInfo> markerInfos) {

            }

            @Override
            public void controlBtnSelected(String controlBtnTag) {

            }

            @Override
            public void controlBtnHold(String controlBtnTag) {

            }

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MatchJni.release();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    int mode = 1;
    int drawPointMode = 1;
    int drawLineMode = 2;

    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    class OpenSetTask extends AsyncTask<Void, Void, Integer> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MeasureActivity.this);
            progressDialog.setMessage("设置匹配影像中...");
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int result = MatchJni.setPointMatch(path + "/1.jpg", path + "/2.jpg", 0);
            //value =callOpenCV3(709, 265, 1);
            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void testMesurePoint() {
        if (meaSureView1.getPoints().size() == 0
                || meaSureView2.getPoints().size() == 0) {
            return;
        }
        ImageTouchEvent leftevent = meaSureView1.getPoints().get(0);
        ImageTouchEvent rightevent = meaSureView2.getPoints().get(0);
        double leftimageScaleX = leftevent.imageScaleX;
        double leftimageScaleY = leftevent.imageScaleY;
        double rightimageScaleX = rightevent.imageScaleX;
        double rightimageScaleY = rightevent.imageScaleY;
        Coord coord = null;
        try {
            // coord = TrueVision.getResultMesurePoint(imageId1, imageId2,
            // leftimageScaleX, leftimageScaleY, rightimageScaleX,
            // rightimageScaleY);
            coord = TrueVision.getResultMesurePointOffline(leftimageScaleX,
                    leftimageScaleY, rightimageScaleX, rightimageScaleY);
        } catch (TrueMapException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String result = "经度：" + coord.getLon() + "," + "纬度：" + coord.getLat();
        mTextView.setText(result);

    }

    public void testMesureLine() {
        if (meaSureView1.getPoints().size() <= 1
                || meaSureView2.getPoints().size() <= 1) {
            return;
        }
        ImageTouchEvent leftevent0 = meaSureView1.getPoints().get(0);
        ImageTouchEvent rightevent0 = meaSureView2.getPoints().get(0);
        ImageTouchEvent leftevent1 = meaSureView1.getPoints().get(1);
        ImageTouchEvent rightevent1 = meaSureView2.getPoints().get(1);
        double leftimageScaleX0 = leftevent0.imageScaleX;
        double leftimageScaleY0 = leftevent0.imageScaleY;
        double rightimageScaleX0 = rightevent0.imageScaleX;
        double rightimageScaleY0 = rightevent0.imageScaleY;

        double leftimageScaleX1 = leftevent1.imageScaleX;
        double leftimageScaleY1 = leftevent1.imageScaleY;
        double rightimageScaleX1 = rightevent1.imageScaleX;
        double rightimageScaleY1 = rightevent1.imageScaleY;

        Coord coord0 = null;
        Coord coord1 = null;
        double distance = 0;
        try {
            coord0 = TrueVision.getResultMesurePointOffline(leftimageScaleX0,
                    leftimageScaleY0, rightimageScaleX0, rightimageScaleY0);
            coord1 = TrueVision.getResultMesurePointOffline(leftimageScaleX1,
                    leftimageScaleY1, rightimageScaleX1, rightimageScaleY1);
            distance = TrueVision.getDistanceByPoint(coord0, coord1);
        } catch (TrueMapException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Toast.makeText(this, "distance  " + distance,
        // Toast.LENGTH_LONG).show();
        mTextView.setText("距离：" + distance + "米");
    }

    class OpenTask extends AsyncTask<int[], Void, Integer> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MeasureActivity.this);
            progressDialog.setMessage("匹配中");
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(int[]... voids) {
            values = MatchJni.findLPointMatch(voids[0][0], voids[0][1], 1);
            return values[0];
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            progressDialog.dismiss();
            progressDialog = null;
            imageTouchEvent.imageX = values[0];
            imageTouchEvent.imageY = values[1];
            imageTouchEvent.imageScaleX = values[0] / (meaSureView2.offLineImage.getCurrentShowBm().getWidth() * 1.000);
            imageTouchEvent.imageScaleY = values[1] / (meaSureView2.offLineImage.getCurrentShowBm().getHeight() * 1.000);
            Log.i("imageScaleX:", "imageScaleX:" + imageTouchEvent.imageScaleX);
//            imageTouchEvent.imageX=437.0;
//            imageTouchEvent.imageY=65.0;
//            imageTouchEvent.imageScaleX = 0.17853;
//            imageTouchEvent.imageScaleY = 0.03173;

            meaSureView2.addPoint(imageTouchEvent);
            //meaSureView2.invalidate();
            if (mode==1){
                testMesurePoint();
            }else {
                testMesureLine();
            }

        }
    }
}
