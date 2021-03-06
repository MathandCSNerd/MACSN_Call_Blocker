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

import java.util.regex.Pattern;

public class WildcardMatcherFactory {

    public static Pattern getPattern(String entry) {
        /*
        there's no point in using regex within these constraints,
        so I've decided to just use a small set of wildcards
        */
        String regStr = entry;
        regStr = regStr.replace("+", "\\+");
        regStr = regStr.replace("*",".*");
        regStr = regStr.replace("?",".");
        regStr = "^" + regStr + "$";
        return Pattern.compile(regStr);
    }

    public static boolean numMatchesEntry(String num, String entry) {
        return getPattern(entry).matcher(num).find();
    }
}
