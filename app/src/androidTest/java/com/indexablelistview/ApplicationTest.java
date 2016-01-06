package com.indexablelistview;

import android.app.Application;
import android.test.ActivityTestCase;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;
import android.util.Log;

import junit.framework.TestCase;

public class ApplicationTest extends TestCase {
    public void testoutput() throws Exception {

        Log.d("ApplicationTest", StringMatcher.getAllFirstLetter("仇明秀你好C,Java"));

    }

}