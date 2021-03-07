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

package com.mathandcsnerd.callblocker.data.persistence;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class PersistentBoolean {
    private boolean currVal;
    private final File saveFile;

    private final String TAG = "PersistentBoolean";

    public PersistentBoolean(File saveFileArg, boolean defaultVal) {
        currVal = defaultVal;
        saveFile = saveFileArg;
        load();
    }

    public synchronized void setVal(boolean val) {
        currVal = val;
        save();
    }

    public synchronized boolean getVal() {
        return currVal;
    }

    public synchronized void save() {
        String str = Boolean.toString(currVal);

        try (FileOutputStream fos = new FileOutputStream(saveFile, false)) {
            fos.write(str.getBytes());
        } catch (Exception e) {
            //if there's an error, or the file doesn't exist, just give up
            Log.e(TAG, e.toString());
        }
    }

    private synchronized boolean getBoolFromFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            String line;
            line = reader.readLine();
            return (Boolean.parseBoolean(line));
        }
    }

    private synchronized void load() {
        try {
            if (saveFile.exists())
                currVal = (getBoolFromFile());
        } catch (Exception e) {
            //if there's an error, or the file doesn't exist, just start fresh
        }
    }
}
