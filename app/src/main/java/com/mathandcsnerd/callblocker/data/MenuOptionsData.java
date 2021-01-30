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

import com.mathandcsnerd.callblocker.data.persistence.PersistentBoolean;

import java.io.File;

public class MenuOptionsData {
    private final String contactsBoolFilename = "allowContacts";
    private final String disableBlockingFilename = "disableBlocking";

    //by default, whitelist contacts, enable blocking
    private final PersistentBoolean allowContactsBool;
    private final PersistentBoolean disableBlockingBool;

    private final String TAG = "MenuOptionsData";

    public MenuOptionsData(File filesDir) {
        File contactsBoolFile = new File(filesDir, contactsBoolFilename);
        File disableBlockingFile = new File(filesDir, disableBlockingFilename);

        allowContactsBool = new PersistentBoolean(contactsBoolFile, true);
        disableBlockingBool = new PersistentBoolean(disableBlockingFile, false);
    }

    public void toggleContactsAreAllowed(){
        allowContactsBool.setVal(!allowContactsBool.getVal());
    }
    public void setContactsAreAllowed(boolean val){
        allowContactsBool.setVal(val);
    }
    public boolean contactsAreAllowed(){
        return allowContactsBool.getVal();
    }

    public void toggleBlockingIsDisabled(){
        disableBlockingBool.setVal(!disableBlockingBool.getVal());
    }
    public void setBlockingIsDisabled(boolean val){
        disableBlockingBool.setVal(val);
    }
    public boolean blockingIsDisabled(){
        return disableBlockingBool.getVal();
    }

}
