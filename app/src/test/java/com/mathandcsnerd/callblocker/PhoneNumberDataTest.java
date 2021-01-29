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

import android.content.Context;

import com.mathandcsnerd.callblocker.data.ContactsGrabber;
import com.mathandcsnerd.callblocker.data.PhoneNumberData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import androidx.test.core.app.ApplicationProvider;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class PhoneNumberDataTest {
    PhoneNumberData testData;
    ContactsGrabber myGrabber;
    Context context = ApplicationProvider.getApplicationContext();

    @Before
    public void setupData(){
        myGrabber = mock(ContactsGrabber.class);
        _setupContacts(null);

        testData = new PhoneNumberData(context, myGrabber);
    }

    private void _setupContacts(Set<String> myset){
        if(myset != null)
          when(myGrabber.getContacts(ArgumentMatchers.anyObject())).thenReturn(myset);
        else
          when(myGrabber.getContacts(ArgumentMatchers.anyObject())).thenReturn(new HashSet<String>());
    }

    @Test
    public void checkValidNumbers() {
        assertTrue(testData.addNumberToBlockList("+15081234345"));
        assertTrue(testData.addNumberToBlockList("5081234345"));
        assertTrue(testData.addNumberToBlockList("5084345"));

        assertTrue(testData.addNumberToBlockList("*1234345"));
        assertTrue(testData.addNumberToBlockList("508*4345"));
        assertTrue(testData.addNumberToBlockList("5*5"));
        assertTrue(testData.addNumberToBlockList("+1*1234345"));

        assertTrue(testData.addNumberToBlockList("*1234345"));
        assertTrue(testData.addNumberToBlockList("508*4345"));
        assertTrue(testData.addNumberToBlockList("5*5"));

        assertTrue(testData.addNumberToBlockList("5*??5??"));
        assertTrue(testData.addNumberToBlockList("503234????"));
    }

    @Test
    public void checkInvalidNumbers() {
        assertFalse(testData.addNumberToBlockList(""));
        assertFalse(testData.addNumberToBlockList("++"));

        assertFalse(testData.addNumberToBlockList("**1234345"));
        assertFalse(testData.addNumberToBlockList("508**4345"));
        assertFalse(testData.addNumberToBlockList("5**5"));

        assertFalse(testData.addNumberToBlockList("**1234345"));
        assertFalse(testData.addNumberToBlockList("508**4345"));
        assertFalse(testData.addNumberToBlockList("5**5"));
    }

    @Test
    public void testAddedNumbersMatch() {
        assertTrue(testData.addNumberToBlockList("+15081234345"));
        assertTrue(testData.addNumberToBlockList("5081264345"));
        assertTrue(testData.addNumberToBlockList("5184235"));

        assertTrue(testData.addNumberToBlockList("*1534321"));
        assertTrue(testData.addNumberToBlockList("548*4412"));
        assertTrue(testData.addNumberToBlockList("878*1452"));
        assertTrue(testData.addNumberToBlockList("5*5"));
        assertTrue(testData.addNumberToBlockList("+1*1234345"));
        assertTrue(testData.addNumberToBlockList("503234????"));
        assertTrue(testData.addNumberToBlockList("5032????34"));
        assertTrue(testData.addNumberToBlockList("523*4????"));

        assertTrue(testData.numIsBlocked("+15081234345"));
        assertTrue(testData.numIsBlocked("5081264345"));
        assertTrue(testData.numIsBlocked("5184235"));

        assertTrue(testData.numIsBlocked("1281534321"));
        assertTrue(testData.numIsBlocked("5484324412"));
        assertTrue(testData.numIsBlocked("8784321452"));
        assertTrue(testData.numIsBlocked("51255761435"));
        assertTrue(testData.numIsBlocked("+1421234345"));
        assertTrue(testData.numIsBlocked("5032343452"));
        assertTrue(testData.numIsBlocked("5032345234"));
        assertTrue(testData.numIsBlocked("5231235234541234"));
    }

    @Test
    public void testMatchedNumbersBlock() {
        assertTrue(testData.addNumberToBlockList("+15081234345"));
        assertTrue(testData.addNumberToBlockList("5081264345"));
        assertTrue(testData.addNumberToBlockList("5184235"));

        assertTrue(testData.addNumberToBlockList("*1534321"));
        assertTrue(testData.addNumberToBlockList("548*4412"));
        assertTrue(testData.addNumberToBlockList("878*1452"));
        assertTrue(testData.addNumberToBlockList("5*5"));
        assertTrue(testData.addNumberToBlockList("+1*1234345"));
        assertTrue(testData.addNumberToBlockList("503234????"));
        assertTrue(testData.addNumberToBlockList("5032????34"));
        assertTrue(testData.addNumberToBlockList("523*4????"));

        assertTrue(testData.numIsBlocked("+15081234345"));
        assertTrue(testData.numIsBlocked("5081264345"));
        assertTrue(testData.numIsBlocked("5184235"));

        assertTrue(testData.numIsBlocked("1281534321"));
        assertTrue(testData.numIsBlocked("5484324412"));
        assertTrue(testData.numIsBlocked("8784321452"));
        assertTrue(testData.numIsBlocked("51255761435"));
        assertTrue(testData.numIsBlocked("+1421234345"));
        assertTrue(testData.numIsBlocked("5032343452"));
        assertTrue(testData.numIsBlocked("5032345234"));
        assertTrue(testData.numIsBlocked("5231235234541234"));
    }

    @Test
    public void testUnmatchedNumbersGood() {
        assertTrue(testData.addNumberToBlockList("+15081234345"));
        assertTrue(testData.addNumberToBlockList("5081264345"));
        assertTrue(testData.addNumberToBlockList("5184235"));

        assertTrue(testData.addNumberToBlockList("*1534321"));
        assertTrue(testData.addNumberToBlockList("548*4412"));
        assertTrue(testData.addNumberToBlockList("878*1452"));
        assertTrue(testData.addNumberToBlockList("5*5"));
        assertTrue(testData.addNumberToBlockList("+1*1234345"));
        assertTrue(testData.addNumberToBlockList("503234????"));
        assertTrue(testData.addNumberToBlockList("5032????34"));
        assertTrue(testData.addNumberToBlockList("523*4????"));

        assertFalse(testData.numIsBlocked("+115081234145"));
        assertFalse(testData.numIsBlocked("5444212442"));
        assertFalse(testData.numIsBlocked("+15444212442"));
        assertFalse(testData.numIsBlocked("+115444212442"));
        assertFalse(testData.numIsBlocked("8781244211252"));
        assertFalse(testData.numIsBlocked("503234"));
        assertFalse(testData.numIsBlocked("50323423"));
        assertFalse(testData.numIsBlocked("503234"));
        assertFalse(testData.numIsBlocked("503234134"));
        assertFalse(testData.numIsBlocked("52334434"));
    }

    @Test
    public void testWhitelistNumbersGood() {
        assertTrue(testData.addNumberToBlockList("512*"));
        assertTrue(testData.addNumberToWhiteList("5122251243"));

        assertTrue(testData.numIsBlocked("5121244345"));
        assertFalse(testData.numIsBlocked("5122251243"));
    }

    @Test
    public void testSave() {
        assertTrue(testData.addNumberToBlockList("512*"));
        assertTrue(testData.addNumberToWhiteList("5122251243"));
        assertTrue(testData.numIsBlocked("5121244345"));
        assertFalse(testData.numIsBlocked("5122251243"));

        testData = new PhoneNumberData(context);

        assertTrue(testData.numIsBlocked("5121244345"));
        assertFalse(testData.numIsBlocked("5122251243"));
    }

    @Test
    public void testContactsGood() {
        _setupContacts(new HashSet<String>(Collections.singleton("5122251243")));
        testData.setContactsAreAllowed(true);
        testData.refreshContacts();

        assertTrue(testData.addNumberToBlockList("512*"));

        assertTrue(testData.numIsBlocked("5121244345"));
        assertFalse(testData.numIsBlocked("5122251243"));
    }

    @Test
    public void testStarBlocksAll() {
        _setupContacts(new HashSet<String>(Collections.singleton("5122251243")));
        testData.setContactsAreAllowed(true);
        testData.refreshContacts();

        assertTrue(testData.addNumberToBlockList("*"));

        assertTrue(testData.numIsBlocked("5121244345"));
        assertTrue(testData.numIsBlocked("42363456244345"));
        assertTrue(testData.numIsBlocked("1244345"));
        assertTrue(testData.numIsBlocked("0458034"));

        assertFalse(testData.numIsBlocked("5122251243"));
    }

    @Test
    public void testContactCallWithPlus() {
        _setupContacts(new HashSet<String>(Collections.singleton("1234525346")));
        testData.setContactsAreAllowed(true);
        testData.refreshContacts();

        assertTrue(testData.addNumberToBlockList("*"));

        assertTrue(testData.numIsBlocked("5121244345"));
        assertTrue(testData.numIsBlocked("42363456244345"));
        assertTrue(testData.numIsBlocked("1244345"));
        assertTrue(testData.numIsBlocked("0458034"));

        assertFalse(testData.numIsBlocked("1234525346"));
        assertFalse(testData.numIsBlocked("+1234525346"));

    }

    @Test
    public void testDisableBlocking() {
        testData.setBlockingIsDisabled(true);

        assertTrue(testData.addNumberToBlockList("+15081234345"));
        assertTrue(testData.addNumberToBlockList("5081264345"));
        assertTrue(testData.addNumberToBlockList("5184235"));

        assertTrue(testData.addNumberToBlockList("*1534321"));
        assertTrue(testData.addNumberToBlockList("548*4412"));
        assertTrue(testData.addNumberToBlockList("878*1452"));
        assertTrue(testData.addNumberToBlockList("5*5"));
        assertTrue(testData.addNumberToBlockList("+1*1234345"));
        assertTrue(testData.addNumberToBlockList("503234????"));
        assertTrue(testData.addNumberToBlockList("5032????34"));
        assertTrue(testData.addNumberToBlockList("523*4????"));

        assertFalse(testData.numIsBlocked("+115081234145"));
        assertFalse(testData.numIsBlocked("5444212442"));
        assertFalse(testData.numIsBlocked("+15444212442"));
        assertFalse(testData.numIsBlocked("+115444212442"));
        assertFalse(testData.numIsBlocked("8781244211252"));
        assertFalse(testData.numIsBlocked("503234"));
        assertFalse(testData.numIsBlocked("50323423"));
        assertFalse(testData.numIsBlocked("503234"));
        assertFalse(testData.numIsBlocked("503234134"));
        assertFalse(testData.numIsBlocked("52334434"));

        assertFalse(testData.numIsBlocked("1281534321"));
        assertFalse(testData.numIsBlocked("5484324412"));
        assertFalse(testData.numIsBlocked("8784321452"));
        assertFalse(testData.numIsBlocked("51255761435"));
        assertFalse(testData.numIsBlocked("+1421234345"));
        assertFalse(testData.numIsBlocked("5032343452"));
        assertFalse(testData.numIsBlocked("5032345234"));
        assertFalse(testData.numIsBlocked("5231235234541234"));
    }

}