package vesnell.pl.lsportfolio.utils;

import vesnell.pl.lsportfolio.App;

public class Resources {

    public static String getString(int resId, Object ... params) {
        return App.getContext().getString(resId, params);
    }

}
