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

import junit.framework.TestCase;

public class ContactsGrabberTest extends TestCase {

    public void testBusyContacts() {
        String str1 = "(345)-423-345,#,11231;";
        String str2 = "(345)-423-345";
        String prefix1 = "+1";
        String prefix2 = "1 ";
        String prefix3 = "1-";
        String prefix4 = "1";

        String str1Expected = "345423345";
        String str2Expected = "345423345";
        assertEquals(ContactsGrabber._normalizeContact(          str1), str1Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix1 + str1), str1Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix2 + str1), str1Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix3 + str1), str1Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix4 + str1), str1Expected);

        assertEquals(ContactsGrabber._normalizeContact(          str2), str2Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix1 + str2), str2Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix2 + str2), str2Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix3 + str2), str2Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix4 + str2), str2Expected);
    }

    public void testSimpleContacts() {
        String str1 = "345423345";
        String str2 = "345-423-345";
        String prefix1 = "+1";
        String prefix2 = "1 ";
        String prefix3 = "1-";
        String prefix4 = "1";

        String str1Expected = "345423345";
        String str2Expected = "345423345";
        assertEquals(ContactsGrabber._normalizeContact(          str1), str1Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix1 + str1), str1Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix2 + str1), str1Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix3 + str1), str1Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix4 + str1), str1Expected);

        assertEquals(ContactsGrabber._normalizeContact(          str2), str2Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix1 + str2), str2Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix2 + str2), str2Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix3 + str2), str2Expected);
        assertEquals(ContactsGrabber._normalizeContact(prefix4 + str2), str2Expected);
    }
}