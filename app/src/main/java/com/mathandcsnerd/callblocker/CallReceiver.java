/* Copyright 2021 Matthew Macallister
 *
 * This file is part of MACSN Call Blocker.
 *
 * MACSN Call Blocker is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MACSN Call Blocker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MACSN Call Blocker.  If not, see
 * <https://www.gnu.org/licenses/>.
 */

package com.mathandcsnerd.callblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.TelecomManager;

import android.telephony.TelephonyManager;
import android.util.Log;

import com.mathandcsnerd.callblocker.data.BlockListCallback;

public class CallReceiver extends BroadcastReceiver {

    static final String TAG = "CallReceiver";

    static private BlockListCallback blocklist;

    static TelecomManager telecomManager;

    static public void setCallback(BlockListCallback cb) {
        blocklist = cb;
    }

    static public void setupTelecom(Context context) {
        telecomManager   = (TelecomManager)  (context.getSystemService(Context.TELECOM_SERVICE));
    }

    private void disconnectCall(Context context) {
        Log.e(TAG, "disconnecting call ");
        //I want this disconnect to run as fast as possible
        //so I'm deliberately not doing a permission check here,
        //instead I'm just quitting the application early if the
        //permission isn't granted since that's the entire point
        //of the application.
        telecomManager.endCall();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                String phoneState = bundle.getString(TelephonyManager.EXTRA_STATE);
                String incomingNumber = bundle.getString("incoming_number");

                if (phoneState != null && phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING) && incomingNumber != null) {
                    Log.d(TAG, "btw number is: " + incomingNumber);

                    if (blocklist.isOnBlocklist(incomingNumber))
                        disconnectCall(context);
                }
            }
            else{
                Log.e(TAG, "receive call could not get extras!");
            }
        } catch (Exception e) {
            Log.e(TAG, "receive call rejection failed! : " + e.toString());
        }
    }
}
