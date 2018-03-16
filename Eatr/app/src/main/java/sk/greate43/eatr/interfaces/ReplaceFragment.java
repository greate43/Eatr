package sk.greate43.eatr.interfaces;

import android.support.v4.app.Fragment;

/**
 * Created by great on 11/28/2017.
 */

public interface ReplaceFragment {

    /**
     * @param fragment : passed fragment will be replaced with current fragment
     */
    void onFragmentReplaced(Fragment fragment);
}
