package com.indexablelistview;

import java.io.UnsupportedEncodingException;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 * Created by Jumy on 16/1/5.
 * 汉字工具
 */
public class ChineseUtils {

    public static String getRandomChinese() {
        Random random = new Random();
        //[20480,36864]
        //常用汉字[21475,21917]
        int ret = random.nextInt(442) + 21475;
        return (char) ret + "";
    }

    public static class pinyinComparator implements Comparator<String> {
        Collator cmp = Collator.getInstance(Locale.CHINA);

        @Override
        public int compare(String lstr, String rstr) {
            if (lstr.length() == lstr.getBytes().length && rstr.getBytes().length == rstr.length()) {
                //all english
                cmp = Collator.getInstance(Locale.ENGLISH);
                if (cmp.compare(lstr, rstr) > 0)
                    return 1;
                else if (cmp.compare(lstr, rstr) < 0)
                    return -1;
                return 0;
            } else if (lstr.length() != lstr.getBytes().length && rstr.getBytes().length != rstr.length()) {
                //all chinese
                if (cmp.compare(lstr, rstr) > 0) {
                    return 1;
                } else if (cmp.compare(lstr, rstr) < 0) {
                    return -1;
                }
                return 0;
            } else {
                lstr = StringMatcher.getAllFirstLetter(lstr);
                rstr = StringMatcher.getAllFirstLetter(rstr);
                cmp = Collator.getInstance(Locale.ENGLISH);
                if (cmp.compare(lstr, rstr) > 0)
                    return 1;
                else if (cmp.compare(lstr, rstr) < 0)
                    return -1;
                return 0;
            }
        }
    }
}
