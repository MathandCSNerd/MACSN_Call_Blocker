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
import android.os.Handler;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberData extends MenuOptionsData implements BlockListCallback {
    //these get listed in another view
    public ArrayList<String> blockList;
    public ArrayList<String> whiteList;
    public ArrayList<String> rejectedList;

    private List<String> contactList;

    private final Set<Character> validChars;

    private final String blockListFilename = "numbers";
    private final String whiteListFilename = "whitelist";
    private final String rejectedListFilename = "rejectedlist";

    private final String TAG = "PhoneNumberData";

    private final Context myContext;
    private final Handler handler;

    private final ContactsGrabber contactsGrabber;

    public void refreshContacts(){
        contactList = new LinkedList<>();
        Set<String> tmpContactList = contactsGrabber.getContacts(myContext);
        for(String val: tmpContactList)
            contactList.add("*"+val);
    }

    public PhoneNumberData(Context context){
        this(context, new ContactsGrabber());
    }

    public PhoneNumberData(Context context, ContactsGrabber grabber){
        super(context);
        validChars = new HashSet<Character>();
        myContext = context;

        blockList = new ArrayList<String>();
        whiteList = new ArrayList<String>();
        rejectedList = new ArrayList<String>();

        contactsGrabber = grabber;
        refreshContacts();

        Character [] chars = {'?','+','*','1','2','3','4','5','6','7','8','9','0'};
        validChars.addAll(Arrays.asList(chars));

        handler = new Handler();

        loadLists();
    }

    public boolean addNumberToBlockList(String num){
        boolean result = _addToBlockListNoSave(num);
        _appendStringToFile(num, blockListFilename);
        return result;
    }

    public boolean addNumberToWhiteList(String num){
        boolean result = _addToWhiteListNoSave(num);
        _appendStringToFile(num, whiteListFilename);
        return result;
    }

    public synchronized boolean addNumberToRejectedList(String num){
        boolean result = _addToRejectedListNoSave(num);
        _appendStringToFile(num, rejectedListFilename);
        return result;
    }

    public void removeNumberFromBlockList(String num){
        blockList.remove(num);
        saveBlockList();
    }

    public void removeNumberFromWhiteList(String num){
        whiteList.remove(num);
        saveWhiteList();
    }

    public void removeNumberFromRejectedList(String num){
        rejectedList.remove(num);
        saveRejectedList();
    }

    private boolean _addToBlockListNoSave(String num){
        if(!_validateNumber(num))
            return false;
        if(!blockList.contains(num))
            blockList.add(num);
        return true;
    }

    private boolean _addToWhiteListNoSave(String num){
        if(!_validateNumber(num))
            return false;
        if(!whiteList.contains(num))
            whiteList.add(num);
        return true;
    }

    private synchronized boolean _addToRejectedListNoSave(String num){
        return rejectedList.add(num);
    }

    private void _appendStringToFile(String str, String fname) {
        File sfile = new File(myContext.getFilesDir(), fname);

        try(FileOutputStream fos = new FileOutputStream(sfile, false)) {
            fos.write(("\n"+str).getBytes());
        }catch (Exception e) {
            //if there's an error, or the file doesn't exist, just give up
            Log.e(TAG, e.toString());
        }
    }

    private boolean _numIsInList(String num, Iterable<String> list){
        for(String val: list)
            if(_numMatchesEntry(num, val))
                return true;
        return false;
    }
    private boolean _numMatchesBlockList(String num){
        return _numIsInList(num, blockList);
    }
    private boolean _numMatchesWhiteList(String num){
        return _numIsInList(num, whiteList);
    }
    private boolean _numIsContact(String num){
        return _numIsInList(num, contactList);
    }

    public boolean numIsBlocked(String num){
        if(blockingIsDisabled())
            return false;
        if(!_numMatchesBlockList(num))
            return false;
        if(contactsAreAllowed() && _numIsContact(num))
            return false;
        if(_numMatchesWhiteList(num))
            return false;
        return true;
    }

    private boolean _numMatchesEntry(String num, String entry) {
        /*
        there's no point in using regex within these constraints,
        so I've decided to just use a small set of wildcards
         */
        String regStr = entry;
        regStr= regStr.replace("+", "\\+");
        regStr = regStr.replace("*",".*");
        regStr = regStr.replace("?",".");
        regStr = "^" + regStr + "$";
        Pattern regex = Pattern.compile(regStr);
        Matcher m = regex.matcher(num);
        return m.find();
    }

    private boolean _validateNumber(String num) {
        if (num.length() == 0)
            return false;
        if (num.contains("**"))
            return false;
        if (num.contains("++"))
            return false;

        char[] numArr = num.toCharArray();

        for(int i = 0; i < numArr.length; ++i) {
            char c = numArr[i];
            if (!validChars.contains(c))
                return false;
        }
        return true;
    }

    public void saveNumListToFile(ArrayList<String> numList, String fname){
        String strs = Arrays.toString(numList.toArray())
                .replace(", ", "\n")
                .replace("[", "")
                .replace("]", "");

        File sfile = new File(myContext.getFilesDir(), fname);

        myContext.getFilesDir().mkdir();

        try(FileOutputStream fos = new FileOutputStream(sfile, false)) {
            fos.write(strs.getBytes());
        }catch (Exception e) {
            //if there's an error, or the file doesn't exist, just give up
            Log.e(TAG, e.toString());
        }
    }

    public synchronized void saveBlockList() {
        saveNumListToFile(blockList, blockListFilename);
    }

    public synchronized void saveWhiteList() {
        saveNumListToFile(whiteList, whiteListFilename);
    }

    public synchronized void saveRejectedList(){
        saveNumListToFile(rejectedList, rejectedListFilename);
    }

    private void loadListFromFile(String fname, Consumer<String> addToListMethod){
        File sfile = new File(myContext.getFilesDir(), fname);

        try (BufferedReader reader = new BufferedReader(new FileReader(sfile))){
            String line;
            line = reader.readLine();
            while(line != null) {
                addToListMethod.accept(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            //if there's an error, or the file doesn't exist, just start fresh
            Log.e(TAG, e.toString());
        }

    }

    private void loadLists(){
        loadListFromFile(blockListFilename, this::_addToBlockListNoSave);
        loadListFromFile(whiteListFilename, this::_addToWhiteListNoSave);
        loadListFromFile(rejectedListFilename, this::_addToRejectedListNoSave);
    }

    @Override
    public boolean isOnBlocklist(String str) {
        boolean blocked = numIsBlocked(str);
        if (blocked) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //since I'm guaranteed that the last thread will
                    //end with a save, and both of these methods are
                    //synchronized, there's no race condition.
                    //
                    //I'll probably come back and fix this design
                    //later though
                    addNumberToRejectedList(str);
                    saveRejectedList();
                }
            });
        }
        return blocked;
    }
}
