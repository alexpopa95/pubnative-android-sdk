package net.pubnative.sdk.core.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import net.pubnative.sdk.BuildConfig;
import net.pubnative.sdk.core.utils.PNBitmapDownloader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PNBitmapDownloaderTest {

    @Test
    public void download_withNullListener_pass() {
        PNBitmapDownloader downloader = new PNBitmapDownloader();
        downloader.download("valid_string", null);
    }

    @Test
    public void download_withEmptyUrl_callOnDownloadFailed() {
        String emptyUrl = "";
        PNBitmapDownloader downloader = new PNBitmapDownloader();
        PNBitmapDownloader.DownloadListener listener = spy(PNBitmapDownloader.DownloadListener.class);
        downloader.download(emptyUrl, listener);

        verify(listener).onDownloadFailed(eq(emptyUrl), any(Exception.class));
    }

    @Test
    public void download_withWrongUrl_callOnDownloadFailed() {
        String wrongUrl = "should_fail";
        PNBitmapDownloader downloader = new PNBitmapDownloader();
        PNBitmapDownloader.DownloadListener listener = spy(PNBitmapDownloader.DownloadListener.class);
        downloader.download(wrongUrl, listener);

        verify(listener).onDownloadFailed(eq(wrongUrl), any(Exception.class));
    }
    @Test
    public void download_withLocalFile_callOnDownloadFinish() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test_image_file.jpg");

        String url = Uri.fromFile(file).toString();
        PNBitmapDownloader downloader = new PNBitmapDownloader();
        PNBitmapDownloader.DownloadListener listener = spy(PNBitmapDownloader.DownloadListener.class);
        downloader.download(url, listener);

        verify(listener).onDownloadFinish(eq(url), any(Bitmap.class));
    }
}