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

package com.mathandcsnerd.callblocker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.mathandcsnerd.callblocker.MainActivity;
import com.mathandcsnerd.callblocker.R;
import com.mathandcsnerd.callblocker.data.PhoneNumberData;

public class BlockedNumbersFragment extends Fragment {

    ArrayAdapter<String> arrayAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.blocked_numbers_fragment, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity activity = ((MainActivity)getActivity());
        PhoneNumberData phoneData = activity.phoneData;

        ListView numListView = activity.findViewById(R.id.blocked_list);
        arrayAdapter = activity.blockListAdapter;
        numListView.setAdapter(arrayAdapter);

        view.findViewById(R.id.button_next_blocked).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(BlockedNumbersFragment.this)
                        .navigate(R.id.action_BlockedFragment_To_Next);
            }
        });

        numListView.setOnItemClickListener((parent, argView, position, id) -> {
            String str = parent.getItemAtPosition(position).toString();
            phoneData.removeNumberFromBlockList(str);
            arrayAdapter.notifyDataSetChanged();
        });
        numListView.setClickable(true);
    }
}