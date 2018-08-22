package com.fanfan.novel.utils.media;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import com.seabreeze.log.Print;

import java.io.File;

/**
 * Created by android on 2018/1/31.
 */

public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

    private MediaScannerConnection mMs;
    private File mFile;
    private ScanListener listener;

    public SingleMediaScanner(Context context, File f, ScanListener l) {
        listener = l;
        mFile = f;
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(mFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String s, Uri uri) {
        mMs.disconnect();
        listener.onScanFinish(s, uri);
    }

    public interface ScanListener {
        void onScanFinish(String s, Uri uri);
    }
}
