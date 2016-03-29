package com.citifleet.util;

public class Constants {
    public static final String PREFS_NAME = "UserPrefs";
    public static final String PREFS_TOKEN = "token";
    public static final float ZOOM_LEVEL = 12.0f;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public static final String REPORT_DIALOG_TAG = "report_dialog";
    public static final String BENEFITS_BARCODE_TAG = "barcode_dialog";
    public static final int REPORT_TARGET_FRAGMENT = 100;
    public static final String ACTION_LOGOUT = "ACTION_LOGOUT";
    public static final String ACTION_PICK_IMAGE_CAMERA = "android.media.action.IMAGE_CAPTURE";
    public static final String BARCODE_STRING = "barcode_string";
    public static final int MAX_BRIGHTNESS = 255;
    public static final int MIN_LENGTH_HACK_LICENSE = 6;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final String IMAGES_LIST_TAG = "images_list";
    public static final String IMAGES_SELECTED_TAG = "images_selected_tag";
    public static final int DEFAULT_UNSELECTED_POSITION = -1;
    public static final String LEGAL_AID_TYPE_TAG = "legal_aid_type_tag";
    public static final String SELECTED_PERSON_TAG = "selected_person_tag";
    public static final String SELECTED_NOTIFICATION_TAG = "selected_notification_tag";
    public static final float DISABLED_LAYOUT_ALPHA = 0.5f;
    public static final float ENABLED_LAYOUT_ALPHA = 1f;
    public static final String HELP_URL_PATH = "help/";
    public static final int DOCUMENTS_TYPES_COUNT = 8;
    public static final int DOCUMENT_PLATE_NUMBER_POSITION = 6;
    public static final String POSTING_TYPE_TAG = "posting_type";
    public static final String DOC_IMAGE_PATH = "doc_image_path";
    public static final String DOC_IMAGE_FRAGMENT_TAG = "doc_image_fragment_tag";
    public static final String HTTP_SCHEME = "http";
    public static final int BUY_RENT_COLUMNS_COUNT = 2;
    public static final int MIN_POSTING_YEAR = 2009;
    public static final int POSTING_IMAGES_NUMBER = 5;
    public static final int IMAGE_WIDTH = 1440;
    public static final int MAX_PRICE=4;
    public static final int MAX_PRICE_DEC_PLACES = 2;
    public static final String INPUT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String OUTPUT_TIME_FORMAT = "hh:mm aa";
    public static final String OUTPUT_DATE_FORMAT = "mm/dd/yyyy";
}
