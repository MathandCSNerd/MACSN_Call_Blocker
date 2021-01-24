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

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mathandcsnerd.callblocker.data.PhoneNumberData;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    public PhoneNumberData phoneData;
    public ArrayAdapter<String> blockListAdapter;
    public ArrayAdapter<String> whiteListAdapter;
    public ArrayAdapter<String> rejectedListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(
            this,
            new String[] {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.ANSWER_PHONE_CALLS,
                Manifest.permission.READ_CONTACTS
            }, 0);

        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE  ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG     ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS     ) != PackageManager.PERMISSION_GRANTED
	      ){
            finish();
        }
        phoneData = new PhoneNumberData(getApplicationContext());
        CallReceiver.setCallback(phoneData);
        CallReceiver.setupTelecom(getApplicationContext());
        phoneData.setContactsBool(true);

        blockListAdapter    = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, phoneData.blockList);
        whiteListAdapter    = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, phoneData.whiteList);
        rejectedListAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, phoneData.rejectedList);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.addToBlockList);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = findViewById(R.id.inputDialog);
                phoneData.addNumberToBlockList(et.getText().toString());
                blockListAdapter.notifyDataSetChanged();
                phoneData.saveBlockList();
            }
        });

        FloatingActionButton whitelistButton = findViewById(R.id.addToWhiteList);
        whitelistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = findViewById(R.id.inputDialog);
                phoneData.addNumberToWhiteList(et.getText().toString());
                whiteListAdapter.notifyDataSetChanged();
                phoneData.saveWhiteList();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //this menu item is mostly for testing, since this happens
        //on every start anyway
        if (id == R.id.options_refresh_contacts) {
            phoneData.refreshContacts();
        }

        //this is to toggle whether or not contacts are whitelisted
        //automatically
        if (id == R.id.options_whitelist_contacts) {

            phoneData.toggleContactsBool();

            item.setTitle("Whitelist Contacts: "
                    + phoneData.getContactsBool());

        }

        return super.onOptionsItemSelected(item);
    }
}
