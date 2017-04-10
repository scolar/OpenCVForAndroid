package com.leador.xcjly.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {

    public String mCCDPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // ccd路径
        makeCCDFile();
        copyFromAssetsToSdcard(false, "truemap.db", mCCDPath
                + "/truemap.db");
        copyFromAssetsToSdcard(false, "1.jpg", mCCDPath + "/1.jpg");
        copyFromAssetsToSdcard(false, "2.jpg", mCCDPath + "/2.jpg");

        Intent intent = new Intent(MainActivity.this, LDTVActivity.class);
        intent.putExtra("path", mCCDPath);
        startActivity(intent);
    }

    private void copyFromAssetsToSdcard(boolean isCover, String source,
                                        String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void makeCCDFile() {
        if (mCCDPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory()
                    .toString();
            mCCDPath = sdcardPath + "/ccddata";
        }
        File file = new File(mCCDPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

}
