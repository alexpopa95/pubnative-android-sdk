// The MIT License (MIT)
//
// Copyright (c) 2017 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package net.pubnative.sdk.core.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;


public class PNAdvertisingId {

    private static final String TAG = PNAdvertisingId.class.getSimpleName();

    public interface Listener {
        void onPubnativeAdvertisingIdFinish(String advertisingId);
    }

    protected Listener mListener;

    public void request(Context context, Listener listener) {

        mListener = listener;
        new AdvertisingIdTask(context).execute();
    }

    protected void invokeOnFinish(String advertisingId) {
        if(mListener != null) {
            mListener.onPubnativeAdvertisingIdFinish(advertisingId);
        }
    }

    protected class AdvertisingIdTask extends AsyncTask<Void, Void, String> {

        protected Context  mContext;

        public AdvertisingIdTask(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            AdInfo adInfo = null;
            try {
                PackageManager pm = mContext.getPackageManager();
                pm.getPackageInfo("com.android.vending", 0);
                Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                intent.setPackage("com.google.android.gms");
                AdvertisingConnection connection = new AdvertisingConnection();
                try {
                    if (mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
                        AdvertisingInterface adInterface = new AdvertisingInterface(connection.getBinder());
                        adInfo = new AdInfo(adInterface.getId(), adInterface.isLimitAdTrackingEnabled(true));
                    }
                } catch (Exception exception) {
                    Log.e(TAG, "getAdvertisingIdInfo - Error: " + exception);
                } finally {
                    mContext.unbindService(connection);
                }
            } catch (Exception exception) {
                Log.e(TAG, "getAdvertisingIdInfo - Error: " + exception);
            }

            String advertisingId = null;
            if(adInfo != null) {
                if (adInfo.isLimitAdTrackingEnabled()) {
                    Log.i(TAG, "Error: cannot get advertising id, limit ad tracking is enabled");
                } else {
                    advertisingId = adInfo.getId();
                }
            }
            return advertisingId;
        }

        @Override
        protected void onPostExecute(String advertisingId) {

            PNAdvertisingId.this.invokeOnFinish(advertisingId);
        }
    }

    /**
     * Ad Info data class with the results
     */
    public class AdInfo {

        private final String  mAdvertisingId;
        private final boolean mLimitAdTrackingEnabled;

        AdInfo(String advertisingId, boolean limitAdTrackingEnabled) {

            mAdvertisingId = advertisingId;
            mLimitAdTrackingEnabled = limitAdTrackingEnabled;
        }

        public String getId() {
            return mAdvertisingId;
        }

        public boolean isLimitAdTrackingEnabled() {

            return mLimitAdTrackingEnabled;
        }
    }

    //==============================================================================================
    // Inner classes
    //==============================================================================================

    /**
     * Advertising Service Connection
     */
    protected class AdvertisingConnection implements ServiceConnection {

        boolean retrieved = false;
        private final LinkedBlockingQueue<IBinder> queue = new LinkedBlockingQueue<IBinder>(1);

        public void onServiceConnected(ComponentName name, IBinder service) {

            try {
                this.queue.put(service);
            } catch (InterruptedException localInterruptedException) {
                Log.e(TAG, "Error: can't connect to AdvertisingId service", localInterruptedException);
            }
        }

        public void onServiceDisconnected(ComponentName name) {}

        public IBinder getBinder() throws InterruptedException {

            if (this.retrieved) { throw new IllegalStateException(); }
            this.retrieved = true;
            return (IBinder) this.queue.take();
        }
    }

    /**
     * Advertising IInterface to get the ID
     */
    protected class AdvertisingInterface implements IInterface {

        private IBinder binder;

        public AdvertisingInterface(IBinder pBinder) {

            binder = pBinder;
        }

        public IBinder asBinder() {

            return binder;
        }

        public String getId() throws RemoteException {

            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            String id = null;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                binder.transact(1, data, reply, 0);
                reply.readException();
                id = reply.readString();
            } catch (Exception ex) {
                Log.e(TAG, "Error: Can't read AdvertisingId from the service", ex);
            } finally {
                reply.recycle();
                data.recycle();
            }
            return id;
        }

        public boolean isLimitAdTrackingEnabled(boolean paramBoolean) throws RemoteException {

            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            boolean limitAdTracking = false;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                data.writeInt(paramBoolean ? 1 : 0);
                binder.transact(2, data, reply, 0);
                reply.readException();
                limitAdTracking = 0 != reply.readInt();
            } catch (Exception ex) {
                Log.e(TAG, "Error: Can't get is limit Ad tracking enabled", ex);
            } finally {
                reply.recycle();
                data.recycle();
            }
            return limitAdTracking;
        }
    }
}
