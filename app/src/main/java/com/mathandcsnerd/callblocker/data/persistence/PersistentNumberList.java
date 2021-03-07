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

import com.mathandcsnerd.callblocker.data.WildcardMatcherFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class PersistentNumberList {
    public ArrayList<String> numberList;
    public Map<String, Pattern> numberPatterns;

    private final boolean isFiltered;
    private final File saveFile;

    private static final Character[] chars = {'?', '+', '*', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
    private static final Set<Character> validChars = new HashSet<Character>();

    private final String TAG = "PersistentNumberList";

    static {
        validChars.addAll(Arrays.asList(chars));
    }

    public PersistentNumberList(File saveFilepathArg, boolean filterVals) {
        isFiltered = filterVals;
        saveFile = saveFilepathArg;
        numberList = new ArrayList<>();
        numberPatterns = new HashMap<>();

        loadListFromFile();
    }

    private static boolean _validateNumber(String num) {
        if (num.length() == 0)
            return false;
        if (num.contains("**"))
            return false;
        if (num.contains("++"))
            return false;

        char[] numArr = num.toCharArray();

        for (int i = 0; i < numArr.length; ++i) {
            char c = numArr[i];
            if (!validChars.contains(c))
                return false;
        }
        return true;
    }

    public synchronized boolean addNumberToList(String num) {
        num = _normalizeListNum(num);
        boolean result = _addToListNoSave(num);
        _appendStringToFile(num);
        return result;
    }

    public synchronized void removeNumberFromList(String num) {
        num = _normalizeListNum(num);
        numberList.remove(num);
        numberPatterns.remove(num);
        saveNumListToFile();
    }

    static private String _normalizeListNum(String number) {
        if (number.startsWith("+1"))
            number = number.substring(2);
        else if (number.startsWith("1"))
            number = number.substring(1);
        return number;
    }

    private synchronized boolean _addToListNoSave(String num) {
        num = _normalizeListNum(num);
        if (isFiltered) {
            if (!_validateNumber(num))
                return false;
            //if the number is already on the list
            //still return true since it is valid
            if (numberList.contains(num))
                return true;
        }
        numberList.add(num);
        numberPatterns.put(num, WildcardMatcherFactory.getPattern(num));
        return true;
    }

    public synchronized boolean numIsInList(String num) {
        for (Pattern val : numberPatterns.values())
            if (val.matcher(num).find())
                return true;
        return false;
    }

    private synchronized void _appendStringToFile(String str) {
        str = _normalizeListNum(str);
        try (FileOutputStream fos = new FileOutputStream(saveFile, true)) {
            fos.write(("\n" + str).getBytes());
        } catch (Exception e) {
            //if there's an error, or the file doesn't exist, just give up
            Log.e(TAG, e.toString());
        }
    }

    public synchronized void saveNumListToFile() {
        String strs = Arrays.toString(numberList.toArray())
                .replace(", ", "\n")
                .replace("[", "")
                .replace("]", "");

        try (FileOutputStream fos = new FileOutputStream(saveFile, false)) {
            fos.write(strs.getBytes());
        } catch (Exception e) {
            //if there's an error, or the file doesn't exist, just give up
            Log.e(TAG, e.toString());
        }
    }

    private synchronized void loadListFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            String line;
            line = reader.readLine();
            while (line != null) {
                _addToListNoSave(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            //if there's an error, or the file doesn't exist, just start fresh
            Log.e(TAG, e.toString());
        }


    }
}
