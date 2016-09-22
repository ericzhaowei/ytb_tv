/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ider.ytb_tv.ui.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.ider.ytb_tv.R;
import com.ider.ytb_tv.ui.fragment.MainFragment;
import com.ider.ytb_tv.utils.Constant;
import com.ider.ytb_tv.utils.OkhttpManager;
import com.ider.ytb_tv.utils.Utils;

import java.io.IOException;


public class MainActivity extends ServiceActivity {
    String TAG = "MainActivity";
    public MainFragment mainFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.main_browse_fragment);
        getWindow().setBackgroundDrawable(null);

    }


    @Override
        void serviceConnected() {
        mainFragment.requestUpdate(Constant.CATEGORY_POPULAR, null);
        mainFragment.requestUpdate(Constant.CATEGORY_EDITOR_CHOICE, null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

