package org.upm.pregonacid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This is called when android.intent.action.BOOT_COMPLETED is executed
 * See AndroidManifest.xml
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, EventPoolingService.class));
    }

}