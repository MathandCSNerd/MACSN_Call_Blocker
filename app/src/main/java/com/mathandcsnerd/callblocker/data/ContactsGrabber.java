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

package com.mathandcsnerd.callblocker.data;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Character.isDigit;

public class ContactsGrabber {
    private final String TAG = "ContactsGrabber";

    /*
      ex:
      1 (509) 3434-4532,,,,#,,###,
      apparently it gives the contacts however they were
      typed in the interface. probably an implementation
      detail for the vendor
     */
    private String _normalizeContact(String contact){
        if (contact.startsWith("+1"))
            contact = contact.substring(2);

        int numericSign = contact.indexOf("#");
        if(numericSign > 0)
          contact = contact.substring(0,numericSign);

        int comma = contact.indexOf(",");
        if(comma > 0)
          contact = contact.substring(0,comma);

        StringBuilder retStr = new StringBuilder();
        for(char c : contact.toCharArray()){
            if(isDigit(c)) {
                retStr.append(c);
            }
        }
        return retStr.toString();
    }

    public Set<String> getContacts(Context context){
        Set<String> contactList = new HashSet<String>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        if (phones != null) {
            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactList.add(_normalizeContact(phoneNumber));
            }
            phones.close();
        } else {
            Log.e(TAG, "contacts list could not be retrieved");
        }
        return contactList;
    }

}
