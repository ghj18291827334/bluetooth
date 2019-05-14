package com.allen.bluetoothtest;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    public static int String_length(String value) {
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @return
     */
    public static List<String> getStrList(String inputString, int length) {
        ArrayList<String> strings = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        char[] chars = inputString.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sb.append(chars[i]);
            try {
                if (sb.toString().getBytes("gbk").length > length) {
                    strings.add(sb.toString());
                    sb.delete(0, sb.length());
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        strings.add(sb.toString());
        return strings;
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @param size        指定列表大小
     * @return
     */
    public static List<String> getStrList(String inputString, int length,
                                          int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    /**
     * 分割字符串，如果开始位置大于字符串长度，返回空
     *
     * @param str 原始字符串
     * @param f   开始位置
     * @param t   结束位置
     * @return
     */
    public static String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        String str = "123456789012341234567890\n一二三四五六七八九十一二三四一二三四五六七八九十一二三四一二三四五六七八九十一二三四一二三四五六七八九十一二三四\n 333333\n djakld;aj\n一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十";
        String[] split = str.split("\n");
        List<String> strs = Arrays.asList(split);
        ArrayList<String> strings = new ArrayList<>(strs);
//        int maxWidth = 14;
//        for (int i = 0; i < strings.size(); i++) {
//            if (strings.get(i).length()> maxWidth) {
//                String s = strings.get(i);
//                strings.remove(i);
//                int length = s.length();
//                int count = length / maxWidth;
//                for (int j = 0; j < count; j++) {
//                    String substring = s.substring(j * maxWidth, ((j + 1) * maxWidth));
//
//
//                    strings.add(i + j, substring);
//                }
//                String substring = s.substring(length - (length % maxWidth), length);
//                strings.add(i + count, substring);
//            }
//        }
        System.out.println(strings);

        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i).length() > 14) {
                List<String> strList = getStrList(strings.get(i), 14);
                strings.remove(i);
                strings.addAll(i, strList);
            }
        }
        System.out.println(strings);


    }
}