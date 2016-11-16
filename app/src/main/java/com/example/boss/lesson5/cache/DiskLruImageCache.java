package com.example.boss.lesson5.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.boss.lesson5.BuildConfig;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by BOSS on 04.11.2016.
 */

public class DiskLruImageCache {

    private DiskLruCache mDiskCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private int mCompressQuality = 70;
    private static final int APP_VERSION = 15;
    private static final int VALUE_COUNT = 1;
    private static final String TAG = "DiskLruImageCache";

    public DiskLruImageCache(Context context, String uniqueName, int diskCacheSize) {
        new InitCacheTask(context, uniqueName, diskCacheSize).execute();
    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor)
            throws IOException, FileNotFoundException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(0), Utils.IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Utils.isExternalStorageRemovable() ?
                        Utils.getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    public void put(String key, Bitmap data) {
        synchronized (mDiskCacheLock) {
            DiskLruCache.Editor editor = null;
            try {
                editor = mDiskCache.edit(key);
                if (editor == null) {
                    return;
                }
                if (writeBitmapToFile(data, editor)) {
                    editor.commit();
                    mDiskCache.flush();
                    if (BuildConfig.DEBUG) {
                        Log.d("myLogs", "image put on disk cache " + key);
                    }
                } else {
                    editor.abort();
                    if (BuildConfig.DEBUG) {
                        Log.d("myLogs", "ERROR on: image put on disk cache " + key);
                    }
                }
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    Log.d("myLogs", "ERROR on: image put on disk cache " + key);
                }
                try {
                    if (editor != null) {
                        editor.abort();
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }

    public Bitmap getBitmap(String key) {
        synchronized (mDiskCacheLock) {
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Bitmap bitmap = null;
            DiskLruCache.Snapshot snapshot = null;
            try {
                snapshot = mDiskCache.get(key);
                if (snapshot == null) {
                    return null;
                }
                final InputStream in = snapshot.getInputStream(0);
                if (in != null) {
                    final BufferedInputStream buffIn =
                            new BufferedInputStream(in);
                    bitmap = BitmapFactory.decodeStream(buffIn);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (snapshot != null) {
                    snapshot.close();
                }
            }

            if (BuildConfig.DEBUG) {
                Log.d("myLogs", bitmap == null ? "" : "image read from disk " + key);
            }
            return bitmap;
        }
    }

    public boolean containsKey(String key) {

        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get(key);
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        return contained;

    }

    public void clearCache() {
        if (BuildConfig.DEBUG) {
            Log.d("cache_test_DISK_", "disk cache CLEARED");
        }
        try {
            mDiskCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getCacheFolder() {
        return mDiskCache.getDirectory();
    }

    public class InitCacheTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private String uniqueName;
        private int diskCacheSize;

        public InitCacheTask(Context context, String uniqueName, int diskCacheSize) {
            this.context = context;
            this.uniqueName = uniqueName;
            this.diskCacheSize = diskCacheSize;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            synchronized (mDiskCacheLock) {
                try {
                    final File diskCacheDir = getDiskCacheDir(context, uniqueName);
                    mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mDiskCacheStarting = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }

    }
}
