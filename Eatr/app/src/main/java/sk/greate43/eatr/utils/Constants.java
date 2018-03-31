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
    String INGREDIENTS_TAGS = "ingredientsTags";
    String IMAGE_URI = "imageUri";
    String PICK_UP_LOCATION = "pickUpLocation";
    String CHECK_IF_ORDER_IS_ACTIVE = "checkIfOrderIsActive";
    String CHECK_IF_FOOD_IS_IN_DRAFT_MODE = "checkIfFoodIsInDraftMode";
    String PRICE = "price";
    String NO_OF_SERVINGS = "numberOfServings";
    String LONGITUDE = "longitude";
    String LATITUDE = "latitude";
    String CHECK_IF_ORDER_IS_PURCHASED = "checkIfOrderIsPurchased";
    String PURCHASED_BY = "purchasedBy";
    String POSTED_BY = "postedBy";
    String PURCHASED_DATE = "purchasedDate";
    String CHECK_IF_ORDER_IS_IN_PROGRESS = "checkIfOrderIsInProgress";
    String CHECK_IF_ORDER_IS_ACCEPTED = "checkIfOrderIsAccepted";
    String CHECK_IF_ORDERED_IS_BOOKED = "checkIfOrderIsBooked";
    String CHECK_IF_MAP_SHOULD_BE_CLOSED = "checkIfMapShouldBeClosed";
    String CHECK_IF_ORDER_IS_COMPLETED = "checkIfOrderIsCompleted";

    //Food Constants


    /**
     * {@link sk.greate43.eatr.entities.Notification }
     */
    String ARGS_NOTIFICATION = "ARGS_NOTIFICATION";
    String NOTIFICATION = "Notification";
    String CHECK_IF_BUTTON_SHOULD_BE_ENABLED = "checkIfButtonShouldBeEnabled";
    String MESSAGE = "message";
    String TITLE = "title";
    String SENDER_ID = "senderId";
    String RECEIVER_ID = "receiverId";
    String ORDER_ID = "orderId";
    String NOTIFICATION_ID = "notificationId";
    String NOTIFICATION_IMAGE = "notificationImage";
    String CHECK_IF_NOTIFICATION_ALERT_SHOULD_BE_SENT = "checkIfNotificationAlertShouldBeSent";
    String CHECK_IF_NOTIFICATION_ALERT_SHOULD_BE_SHOWN = "checkIfNotificationAlertShouldBeShown";
    String NOTIFICATION_TYPE = "notificationType";
    String TYPE_NOTIFICATION_ORDER_REQUEST = "ORDER_REQUEST";
    String TYEPE_NOTIFICATION_ORDER_COMPLETED = "ORDER_COMPLETED";
    /**
     * {@link sk.greate43.eatr.fragments.AddFoodItemFragment}
     */
    String ARGS_FOOD = "ARGS_FOOD";

    // Add Food Fragment

    String ALL_ORDERS = "ALL_ORDERS";
    String ORDER_STATE = "ORDER_STATE";
    String ORDER_ACTIVE = "ORDER_ACTIVE";
    String ORDER_DRAFT = "ORDER_DRAFT";
    String ORDER_PURCHASED = "ORDER_PURCHASED";
    String ORDERED_BOOKED = "ORDERED_BOOKED";
    //FoodItemExpiryTimeAndPriceFragment

    /**
     * {@link sk.greate43.eatr.entities.Profile}
     */
    String PROFILE = "Profile";
    String ARGS_PROFILE = "ARGS_PROFILE";
    String TYPE_SELLER = "SELLER";
    String TYPE_BUYER = "BUYER";
    String USER_TYPE = "userType";
    String PROFILE_PHOTO_URI = "profilePhotoUri";
    String FIRST_NAME = "firstName";
    String LAST_NAME = "lastName";
    String USER_ID = "userId";
    String EMAIL = "email";

    // profile

    /**
     * {@link sk.greate43.eatr.entities.LiveLocationUpdate}
     */
    String LIVE_LOCATION_UPDATE = "LiveLocationUpdate";
    String SELLER_ID = "sellerId";
    String BUYER_ID = "buyerId";

    //Live Location Update end

    /**
     * {@link sk.greate43.eatr.entities.Account}
     */
    String BALANCE = "balance";
    String PAYMENT_DATE = "paymentDate";
    String ACCOUNT = "Account";

    // Permission and Requests Constants
    int CAMERA_RESULT = 111;
    int GALLERY_RESULT = 222;

    int REQUEST_CAMERA_AND_WRITE_PERMISSION = 1111;
    int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 2222;

    int REQUEST_FINE_LOCATION_PERMISSION = 4444;
    int PLACE_PICKER_REQUEST = 1;
    // Permission and Requests Constants


    String MAX_UNI_CODE_LIMIT = "\uf8ff";

}
