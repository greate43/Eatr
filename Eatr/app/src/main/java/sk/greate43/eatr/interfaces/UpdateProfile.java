package sk.greate43.eatr.interfaces;

import sk.greate43.eatr.entities.Profile;

/**
 * Created by great on 3/4/2018.
 */

public interface UpdateProfile {
    void onNavDrawerDataUpdated(Profile profile);
    void myOverAllRating(float myRating);
}