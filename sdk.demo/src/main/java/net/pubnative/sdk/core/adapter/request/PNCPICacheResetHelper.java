package net.pubnative.sdk.core.adapter.request;

public class PNCPICacheResetHelper {

    public static void resetCPICache() {
        PubnativeLibraryCPICache.getInstance().clear();
    }
}
