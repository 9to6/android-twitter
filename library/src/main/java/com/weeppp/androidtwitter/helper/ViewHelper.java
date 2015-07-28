/*
 * author: Teukgeon Kwon
 * email: ktg@weeppp.com
 * blog: weeppp.com
 *
 * Copyright (c) 2015. all rights reserved.
 */

package com.weeppp.androidtwitter.helper;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by ktg on 2015-07-27.
 */
public class ViewHelper {
    public static View getRootView(Context context)
    {
        return ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
    }
}
