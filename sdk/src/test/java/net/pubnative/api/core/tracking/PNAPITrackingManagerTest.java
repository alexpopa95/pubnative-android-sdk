package net.pubnative.api.core.tracking;

import android.content.Context;

import net.pubnative.sdk.BuildConfig;
import net.pubnative.api.core.tracking.model.PNAPITrackingURLModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PNAPITrackingManagerTest {

    Context applicationContext;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
        PNAPITrackingManager.setList(applicationContext, PNAPITrackingManager.SHARED_PENDING_LIST, null);
        PNAPITrackingManager.setList(applicationContext, PNAPITrackingManager.SHARED_FAILED_LIST, null);
    }

    @Test
    public void testWithNullContext() {

        PNAPITrackingManager.track(null, "www.google.com");
    }

    @Test
    public void testWithEmptyUrl() {

        PNAPITrackingManager.track(applicationContext, "");
        List<PNAPITrackingURLModel> urlModelList = PNAPITrackingManager.getList(applicationContext, PNAPITrackingManager.SHARED_PENDING_LIST);

        assertThat(urlModelList).isEmpty();
    }

    @Test
    public void checkItemEnqued() {

        PNAPITrackingURLModel model = new PNAPITrackingURLModel();
        model.url = "www.google.com";
        model.startTimestamp = System.currentTimeMillis();

        PNAPITrackingManager.enqueueItem(applicationContext, PNAPITrackingManager.SHARED_PENDING_LIST, model);

        List<PNAPITrackingURLModel> urlModelList = PNAPITrackingManager.getList(applicationContext, PNAPITrackingManager.SHARED_PENDING_LIST);

        assertThat(urlModelList).isNotEmpty();
    }

    @Test
    public void checkItemDequeued() {

        PNAPITrackingURLModel model = new PNAPITrackingURLModel();
        model.url = "www.google.com";
        model.startTimestamp = System.currentTimeMillis();

        PNAPITrackingManager.enqueueItem(applicationContext, PNAPITrackingManager.SHARED_PENDING_LIST, model);

        PNAPITrackingURLModel dequeuedItem = PNAPITrackingManager.dequeueItem(applicationContext, PNAPITrackingManager.SHARED_PENDING_LIST);

        assertThat(dequeuedItem.url).isEqualTo(model.url);
        assertThat(dequeuedItem.startTimestamp).isEqualTo(model.startTimestamp);
    }

    @Test
    public void testEnqueueFailedList() {

        List<PNAPITrackingURLModel> failedList = new ArrayList<PNAPITrackingURLModel>();
        PNAPITrackingURLModel model = new PNAPITrackingURLModel();
        model.url = "www.google.com";
        model.startTimestamp = System.currentTimeMillis();
        failedList.add(model);

        PNAPITrackingManager.setList(applicationContext, PNAPITrackingManager.SHARED_FAILED_LIST, failedList);
        PNAPITrackingManager.enqueueFailedList(applicationContext);

        assertThat(PNAPITrackingManager.getList(applicationContext, PNAPITrackingManager.SHARED_FAILED_LIST)).isEmpty();
    }
}
