package sk.greate43.eatr.utils;

/**
 * Created by great on 12/10/2017.
 * Constants class for holding constant
 */


public interface Constants {
    String FOOD = "Food";
    String PHOTOS = "Photos";
    String TIME_STAMP = "timeStamp";


    /**
     * {@link sk.greate43.eatr.entities.Food}
     */
    String PUSH_ID = "pushId";
    String DISH_NAME = "dishName";
    String CUISINE = "cuisine";
    String EXPIRY_TIME = "expiryTime";
    String INCIDENT_TAGS = "ingredientsTags";
    String IMAGE_URI = "imageUri";
    String PICK_UP_LOCATION = "pickUpLocation";
    String CHECK_IF_ORDER_IS_ACTIVE = "checkIfOrderIsActive";
    String CHECK_IF_FOOD_IS_IN_DRAFT_MODE = "checkIfFoodIsInDraftMode";
    String PRICE = "price";
    String NO_OF_SERVINGS = "numberOfServings";
    String LONGITUDE = "longitude";
    String LATITUDE = "latitude";
    String CHECK_IF_ORDER_Is_PURCHASED = "checkIfOrderPurchased";
    //Food Constants


    /**
     * {@link sk.greate43.eatr.fragments.AddFoodItemFragment}
     */
    String ARGS_FOOD = "ARGS_FOOD";

    // Add Food Fragment


    /**
     * {@link sk.greate43.eatr.fragments.FoodItemExpiryTimeAndPriceFragment}
     *
     */

    //FoodItemExpiryTimeAndPriceFragment

    /**
     * {@link sk.greate43.eatr.entities.Profile}
     */
    String PROFILE = "Profile";
    String ARGS_PROFILE = "ARGS_PROFILE";
    String TYPE_SELLER = "SELLER";
    String TYPE_BUYER = "BUYER";
    // profile


    // Permission and Requests Constants
    int CAMERA_RESULT = 111;
    int GALLERY_RESULT = 222;

    int REQUEST_CAMERA_AND_WRITE_PERMISSION = 1111;
    int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 2222;

    int REQUEST_FINE_LOCATION_PERMISSION = 4444;
    int PLACE_PICKER_REQUEST = 1;
    // Permission and Requests Constants


}
