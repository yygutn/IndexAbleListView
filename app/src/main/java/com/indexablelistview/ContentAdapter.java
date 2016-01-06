package com.indexablelistview;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

import java.util.List;

/**
 * Created by Jumy on 16/1/5.
 * 索引数据适配器
 */
public class ContentAdapter extends ArrayAdapter<String> implements SectionIndexer{

    private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public ContentAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public Object[] getSections() {
        String[] sections = new String[mSections.length()];
        for (int i = 0; i < mSections.length(); i++)
            sections[i] = String.valueOf(mSections.charAt(i));
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        // If there is no item for current section, previous section will be selected
        for (int i = sectionIndex; i >= 0; i--) {
            for (int j = 0; j < getCount(); j++) {
                String temp;
                if (i == 0) {
                    // For numeric section
                    for (int k = 0; k <= 9; k++) {
                        temp = StringMatcher.getAllFirstLetter(String.valueOf(getItem(j).charAt(0)));
                        if (StringMatcher.match(temp, String.valueOf(k)))
                            return j;
                    }
                } else {
                    temp = StringMatcher.getAllFirstLetter(String.valueOf((getItem(j)).charAt(0)));
                    if (StringMatcher.match(temp, String.valueOf(mSections.charAt(i))))
                        return j;
                }
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }
}
