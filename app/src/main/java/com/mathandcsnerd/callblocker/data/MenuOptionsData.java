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
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuOptionsData {
    private boolean allowContacts;
    private boolean disableBlocking;

    private final String contactsBoolFilename = "allowContacts";
    private final String disableBlockingFilename = "disableBlocking";

    private final String TAG = "MenuOptionsData";

    private final Context myContext;

    public MenuOptionsData(Context context){
        myContext = context;

        //by default, whitelist contacts, enable blocking
        allowContacts = true;
        disableBlocking = false;

        loadBools();
    }

    public void toggleContactsAreAllowed(){
        allowContacts = !allowContacts;
        saveContactsAllowed();
    }
    public void setContactsAreAllowed(boolean val){
        allowContacts = val;
        saveContactsAllowed();
    }
    public boolean contactsAreAllowed(){
        return allowContacts;
    }

    public void toggleBlockingIsDisabled(){
        disableBlocking = !disableBlocking;
        saveDisableBlocking();
    }
    public void setBlockingIsDisabled(boolean val){
        disableBlocking = val;
        saveDisableBlocking();
    }
    public boolean blockingIsDisabled(){
        return disableBlocking;
    }

    public void saveBoolToFile(boolean val, String fname) {
        File sfile = new File(myContext.getFilesDir(), fname);
        String str = Boolean.toString(val);

        myContext.getFilesDir().mkdir();

        try(FileOutputStream fos = new FileOutputStream(sfile, false)) {
            fos.write(str.getBytes());
        }catch (Exception e) {
            //if there's an error, or the file doesn't exist, just give up
            Log.e(TAG, e.toString());
        }
    }

    public synchronized void saveContactsAllowed() {
        saveBoolToFile( contactsAreAllowed(), contactsBoolFilename);
    }

    public synchronized void saveDisableBlocking() {
        saveBoolToFile( blockingIsDisabled(), disableBlockingFilename);
    }

    private boolean getBoolFromFile(String filename) throws IOException {
        File sfile = new File(myContext.getFilesDir(), filename);

        try (BufferedReader reader = new BufferedReader(new FileReader(sfile))) {
            String line;
            line = reader.readLine();
            return (Boolean.parseBoolean(line));
        }
    }

    private void loadBools() {
        try {
            allowContacts = (getBoolFromFile(contactsBoolFilename));
        }
        catch (Exception e) {
            //if there's an error, or the file doesn't exist, just start fresh
        }

        try {
            disableBlocking = (getBoolFromFile(disableBlockingFilename));
        }
        catch (Exception e) {
            //if there's an error, or the file doesn't exist, just start fresh
        }
    }

}
