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

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import android.os.Handler;

import com.mathandcsnerd.callblocker.data.persistence.PersistentNumberList;

import java.util.regex.Pattern;

public class PhoneNumberData extends MenuOptionsData implements BlockListCallback {
    //these get listed in another view
    private final String blockListFilename = "numbers";
    private final String whiteListFilename = "whitelist";
    private final String rejectedListFilename = "rejectedlist";

    private final PersistentNumberList persistentBlockList;
    private final PersistentNumberList persistentWhiteList;
    private final PersistentNumberList persistentRejectedList;

    public ArrayList<String> blockList;
    public ArrayList<String> whiteList;
    public ArrayList<String> rejectedList;

    private ArrayList<Pattern> contactPatterns;

    private final String TAG = "PhoneNumberData";

    private final Context myContext;
    private final Handler handler;

    private final ContactsGrabber contactsGrabber;

    public void refreshContacts(){
        contactPatterns = new ArrayList<>();

        Set<String> tmpContactList = contactsGrabber.getContacts(myContext);
        for(String val: tmpContactList)
            contactPatterns.add(WildcardMatcherFactory.getPattern(val));
    }

    public PhoneNumberData(Context context){
        this(context, new ContactsGrabber());
    }

    public PhoneNumberData(Context context, ContactsGrabber grabber){
        super(context.getFilesDir());
        myContext = context;
        File filesDir = myContext.getFilesDir();

        persistentBlockList = new PersistentNumberList(new File(filesDir, blockListFilename), true);
        persistentWhiteList = new PersistentNumberList(new File(filesDir, whiteListFilename), true);
        persistentRejectedList = new PersistentNumberList(new File(filesDir, rejectedListFilename), false);

        blockList    = persistentBlockList.numberList;
        whiteList    = persistentWhiteList.numberList;
        rejectedList = persistentRejectedList.numberList;

        contactsGrabber = grabber;
        refreshContacts();

        handler = new Handler();
    }

    public boolean addNumberToBlockList(String num){
        return persistentBlockList.addNumberToList(num);
    }

    public boolean addNumberToWhiteList(String num){
        return persistentWhiteList.addNumberToList(num);
    }

    public void removeNumberFromBlockList(String num){
        persistentBlockList.removeNumberFromList(num);
    }

    public void removeNumberFromWhiteList(String num){
        persistentWhiteList.removeNumberFromList(num);
    }

    public void removeNumberFromRejectedList(String num){
        persistentRejectedList.removeNumberFromList(num);
    }

    private boolean _numIsContact(String num){
        for(Pattern pat: contactPatterns)
            if(pat.matcher(num).find())
                return true;
        return false;
    }

    public boolean numIsBlocked(String num){
        num = _normalizeIncoming(num);
        if(blockingIsDisabled())
            return false;
        if(!persistentBlockList.numIsInList(num))
            return false;
        if(contactsAreAllowed() && _numIsContact(num))
            return false;
        if(persistentWhiteList.numIsInList(num))
            return false;
        return true;
    }

    static private String _normalizeIncoming(String number) {
        if (number.startsWith("+1"))
            number = number.substring(2);
        else if (number.startsWith("1"))
            number = number.substring(1);
        return number;
    }

    @Override
    public boolean isOnBlocklist(String str) {
        boolean blocked = numIsBlocked(str);
        if (blocked) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    persistentRejectedList.addNumberToList(_normalizeIncoming(str));
                }
            });
        }
        return blocked;
    }
}
