package com.fcb.fogcomputingbox;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;



public class UIUtils {

    /**
     * 得到上下文
     *
     * @return
     */
    public static Context getContext() {
        return AppClass.getContext();
    }

    /**
     * 得到Resource对象
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 得到String.xml中定义的字符串信息
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 得到String.xml中定义的字符串数组信息
     */
    public static String[] getStrings(int resId) {
        return getResources().getStringArray(resId);
    }

    /**
     * 得到Color.xml中定义的颜色信息
     */
    public static int getColor(@ColorRes int resId) {

        return  ContextCompat.getColor(UIUtils.getContext(),resId);
    }

    /**
     * 得到应用程序的包名
     *
     * @return
     */
    public static String getPackageName() {
        return getContext().getPackageName();
    }

    /**
     * dp-->px
     *
     * @param dp
     * @return
     */
    public static int dp2Px(int dp) {
        //dp和px相互转换的公式
        //公式一:px/dp = density
        //公式二:px/(ppi/160) = dp
        /*
           480x800  ppi=240    1.5
           1280x720 ppi = 320   2
         */
        float density = getResources().getDisplayMetrics().density;
        int px = (int) (dp * density + .5f);
        return px;
    }
    public static int dp2Px(float dp) {
        //dp和px相互转换的公式
        //公式一:px/dp = density
        //公式二:px/(ppi/160) = dp
        /*
           480x800  ppi=240    1.5
           1280x720 ppi = 320   2
         */
        float density = getResources().getDisplayMetrics().density;
        int px = (int) (dp * density );
        return px;
    }
    /**
     * px-->do
     *
     * @param px
     * @return
     */
    public static int px2Dp(int px) {
        //dp和px相互转换的公式
        //公式一:px/dp = density
        //公式二:px/(ppi/160) = dp
        /*
           480x800  ppi=240    1.5
           1280x720 ppi = 320   2
         */
        float density = getResources().getDisplayMetrics().density;
        int dp = (int) (px / density + .5f);
        return dp;
    }
    public static int px2Dp(float px) {
        //dp和px相互转换的公式
        //公式一:px/dp = density
        //公式二:px/(ppi/160) = dp
        /*
           480x800  ppi=240    1.5
           1280x720 ppi = 320   2
         */
        float density = getResources().getDisplayMetrics().density;
        int dp = (int) (px / density );
        return dp;
    }

    /**
     * sp-->px
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    public static String hintEmail(String email) {

                String emailStr = email.substring(0, email.lastIndexOf("@"));
        if (emailStr.length() < 2) {//0位、1位
            String string=email.replaceAll("(\\w?)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "*$3");
            return string;
        } else if (2 <= emailStr.length() && emailStr.length() < 3) {//2位
            return email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1*$3$4");
        } else {//3位以上
            return email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1*$3$4");
        }
    }



    /**
     * 根据一个包含汉字的字符串返回一个汉字拼音首字母的字符串 最重要的一个方法，思路如下：一个个字符读入、判断、输出
     */
    public static String cn2py(String SourceStr) {
        String Result = "";
        try {
            Result = Char2Initial(SourceStr.charAt(0)) + "";
        } catch (Exception e) {
            Result = "";
        }
        return Result.toUpperCase();
    }
    private static int BEGIN = 45217;
    private static int END = 63486;

    // 按照声母表示，这个表是在GB2312中的出现的第一个汉字，也就是说“啊”是代表首字母a的第一个汉字。
    // i, u, v都不做声母, 自定规则跟随前面的字母
    private static char[] chartable = {'啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈',
            '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然', '撒', '塌', '塌',
            '塌', '挖', '昔', '压', '匝',};

    // 二十六个字母区间对应二十七个端点
    // GB2312码汉字区间十进制表示
    private static int[] table = new int[27];

    static {
        for (int i = 0; i < 26; i++) {
            table[i] = gbValue(chartable[i]);// 得到GB2312码的首字母区间端点表，十进制。
        }
        table[26] = END;// 区间表结尾
    }

    // 对应首字母区间表
    private static char[] initialtable = {'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            't', 't', 'w', 'x', 'y', 'z',};

    /**
     * 输入字符,得到他的声母,英文字母返回对应的大写字母,其他非简体汉字返回 '0'
     */
    private static char Char2Initial(char ch) {
        // 对英文字母的处理：小写字母转换为大写，大写的直接返回
        if (ch >= 'a' && ch <= 'z')
            return (char) (ch - 'a' + 'A');
        if (ch >= 'A' && ch <= 'Z')
            return ch;

        // 对非英文字母的处理：转化为首字母，然后判断是否在码表范围内，
        // 若不是，则直接返回。
        // 若是，则在码表内的进行判断。
        int gb = gbValue(ch);// 汉字转换首字母

        if ((gb < BEGIN) || (gb > END))// 在码表区间之前，直接返回
            return ch;

        int i;
        for (i = 0; i < 26; i++) {// 判断匹配码表区间，匹配到就break,判断区间形如“[,)”
            if ((gb >= table[i]) && (gb < table[i + 1]))
                break;
        }

        if (gb == END) {//补上GB2312区间最右端
            i = 25;
        }
        return initialtable[i]; // 在码表区间中，返回首字母
    }
    /**
     * 取出汉字的编码 cn 汉字
     */
    private static int gbValue(char ch) {// 将一个汉字（GB2312）转换为十进制表示。
        String str = new String();
        str += ch;
        try {
            byte[] bytes = str.getBytes("GB2312");
            if (bytes.length < 2)
                return 0;
            return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
        } catch (Exception e) {
            return 0;
        }
    }
    private static int mScrollHeight;

    public static void addListener(final ViewGroup main, final Button scroll) {

        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                main.getWindowVisibleDisplayFrame(rect);

                int mainVisibleHeight = main.getRootView().getHeight() - rect.bottom;
                if (mainVisibleHeight > 100) {
                    //软键盘已弹出
                    int[] location = new int[2];
                    scroll.getLocationInWindow(location);
                    mScrollHeight = (location[1] + scroll.getHeight() - rect.bottom);
                    if (mScrollHeight < 0) {
                        return;
                    }
                    ValueAnimator animator = ValueAnimator.ofFloat(0f, mScrollHeight + 20);
                    animator.setDuration(200);
                    animator.start();
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float fraction = animation.getAnimatedFraction();
                            float value = (float) animation.getAnimatedValue();
                            main.scrollTo(0, (int) value);
                        }
                    });
                } else {
                    ValueAnimator animator = ValueAnimator.ofFloat(mScrollHeight + 20, 0f);
                    animator.setDuration(200);
                    animator.start();
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {

                            float fraction = animation.getAnimatedFraction();
                            float value = (float) animation.getAnimatedValue();
                            main.scrollTo(0, (int) value);
                        }
                    });
                    mScrollHeight = -20;

                }
            }
        });
    }
    /** 对TextView设置不同状态时其文字颜色。 */
    public   static ColorStateList createColorStateList(int normal, int pressed, int focused, int unable) {
        int[] colors = new int[] { pressed, focused, normal, focused, unable, normal };
        int[][] states = new int[6][];
        states[0] = new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled };
        states[1] = new int[] { android.R.attr.state_enabled, android.R.attr.state_focused };
        states[2] = new int[] { android.R.attr.state_enabled };
        states[3] = new int[] { android.R.attr.state_focused };
        states[4] = new int[] { android.R.attr.state_window_focused };
        states[5] = new int[] {};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }
}
