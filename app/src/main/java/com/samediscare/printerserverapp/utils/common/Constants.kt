package com.bong.brothersetup.utils.common


object Constants {

    const val STRING_BLANK = ""
    const val STRING_COMMA = ","
    const val STRING_COLON = ":"
    const val STRING_COLON_DASH = ":-"
    const val STRING_SPACE = " "
    const val STRING_NEW_LINE = "\n"
    const val STRING_NULL = "null"
    const val STRING_DOT = "."
    const val STRING_SLASH = "/"
    const val STRING_DOT_BIG = "•"
    const val STRING_DASH = "-"
    const val PDF_EXTENSION = ".pdf"
    const val DEFAULT_DEVICE_IMAGE = "img/placeholders/"
    const val PREFS_NAME = "samedis-care-prefs"
    const val PUBLIC_TENANT_NAME = "'samedis.care'"
    const val PUBLIC_TENANT_VALUE = "'true'"
    const val STRING_TRUE = "true"
    const val STRING_FALSE = "false"
    const val PUBLIC_CONTACT_VALUE = "'public'"
    const val STRING_PUBLIC = "public"
    const val STRING_BLANK_JSON = "{}"

    const val NULL_INDEX = -1L

    const val SPLASH_DISPLAY_LENGTH: Long = 2000


    const val TENANT_BACK = "tenant_back"
    const val SCAN_QR_CODE_SPLIT = ".html"

    //Image
    const val IMAGE_FORMAT_JPEG: String = "data:image/jpeg;base64,"
    const val IMAGE_FORMAT_PNG = "data:image/png;base64,"

    //Global and tenant specific
    const val SCOPE_PUBLIC_TENANT = "scope_public_tenant"

    //Timer
    interface TimerDelay{
        companion object{
            const val SCREEN_OPEN: Long = 10000
            const val TYPING_SEARCH: Long = 900
            const val SYNC_DATA: Long = 1000
        }
    }


    //Pagination
    const val PAGE_INITIAL_LOAD_SIZE_HINT: Int = 1000
    const val PAGE_PREFETCH_DISTANCE: Int = 2
    const val PAGE_SIZE: Int = 1000
    const val PAGE: Int = 1
    interface Pagination{
        companion object{
            const val LIMIT_PER_PAGE: Int = 230
            const val LIMIT_PER_PAGE_MIN: Int = 30
        }
    }

    /*interface SamedisCareLink{
        companion object{
            const val WEB_URL_QR_CODE = "/qr_code"   //Current Url = https://dev.samedis.care/api/v2/qr_code_resources/btNZyoUWgmbs6y4Y3zS9J3uV.html
        }
    }*/

    interface Hockey {
        companion object {
            const val HOCKEY_APP_SECRET_ID = "98f1e39e-e03b-4788-834e-44c311f642e4"
            const val HOCKEY_APP_BUILD_TYPE = "BuildType"
            const val HOCKEY_APP_BRANCH = "Branch"
            const val HOCKEY_APP_APP_VERSION = "AppVersion"
        }
    }

    interface DialogAction{
        companion object{
            const val ALERT_ACTION_POSITIVE = "positive"
            const val ALERT_ACTION_NEGATIVE = "negative"
        }
    }

    //Broadcast receiver
    interface ReceiverIntent{
        companion object{
            const val CHANGE_LANGUAGE = "changeLanguage"
        }
    }

    interface IntentExtra{
        companion object{
            const val CAMERA_SCAN_FROM = "camera_scan_from"
            const val CAMERA_SCAN_FROM_URI = "camera_scan_from_URI"
            const val CAMERA_SCAN_FROM_SCAN = "camera_scan_from_SCAN"
            const val TITLE = "title"
            const val SEARCH_KEY = "search_key"
            const val SEARCH_KEY_DEVICE_MODEL = "search_key_device_model"
            const val SEARCH_KEY_LOCATION = "search_key_location"
            const val SEARCH_KEY_DEPARTMENT = "search_key_department"
            const val SEARCH_KEY_SERVICE_PARTNER = "search_key_service_partner"
            const val EDIT_INVENTORY = "edit_inventory"
            const val INVENTORY_ID = "inventory_id"
            const val CATALOG_ID = "catalog_id"
            const val QR_PDF_PATH = "qr_pdf_path"
            const val COME_FROM_TENANT_SCREEN = "come_from_tenant_screen"
        }
    }

    interface BundleExtra{
        companion object{
            //More
            const val WEB_PAGE_URL = "web_page_url"
            const val MENU_OPEN_IN_ACTIVITY = "menu_open_in_activity"
            const val SCAN_QR_URL = "scan_qr_url"
        }
    }

    interface ActionCode{
        companion object{
            const val TAKE_PHOTO = 400
            const val TAKE_DOCUMENT = 401

            const val PIC_PHOTO_CAMERA = 500
            const val PIC_PHOTO_GALLERY = 501
            const val PIC_DOCUMENT = 500

            const val IMAGE_DELETE = 1001
            const val IMAGE_STAR = 1002
            const val DOC_EYE = 1003
            const val SERVICE_PARTNER_ROW = 1004
            const val SERVICE_PARTNER_DELETE = 1005
            const val MAINTENANCE_EVENT_DELETE = 1006

            const val OPEN_INVENTORY = 1007
            const val LOCAL_TENANT_ROW = 1008
        }
    }

    /*
    Logic - about offline

        Record status
           - Online
           - Offline
           - OfflineEdit

        Data status
          - Local
          - Upload
          - Delete

        Record status - online -> data status -> upload
        Record status - offline -> data status -> local

        Record status - offlineEdit -> for relational table
           - data status -> Upload, delete, local

        Delete data status do not show on screen

     */
    interface DataStatus{
        companion object{
            const val STATUS = "status"
            const val LOCAL = "local"
            const val UPLOAD = "upload"
            const val DELETE = "delete"
            const val ONLINE = "online"
            const val OFFLINE = "offline"
            const val OFFLINE_EDIT = "offlineEdit"

            //API status
            const val CREATED = "created"
            const val DRAFT = "draft"
            const val FINALIZE_CREATION = "finalize_creation"

            //Sync
            const val SYNC_START = "start"
            const val SYNC_STOP = "stop"
            const val SYNC_COMPLETE = "complete"
            const val SYNC_DONE = "done"
        }
    }

    interface InventoryStep{
        companion object{
            const val ONE = 1
            const val TWO = 2
            const val THREE = 3
            const val FOUR = 4
            const val EDIT = 5
        }
    }

    interface InventorySave{
        companion object{
            const val SERVICE_PARTNER_ADD = 11
            const val SERVICE_PARTNER_DELETE = 12
            const val MAINTENANCE_EVENT_ADD = 13
            const val MAINTENANCE_EVENT_DELETE = 14
            const val IMAGE_ADD = 15
            const val IMAGE_DELETE = 16
            const val DOCUMENT_ADD = 17
            const val DOCUMENT_DELETE = 18
        }
    }

    interface InventoryRelational{
        companion object{
            const val CONTACTS = "contacts"
        }
    }

    interface InventoryOffline{
        companion object{
            const val inventoryIdFormat = "offline-"
        }
    }

    interface scanQrCodeType{
        companion object {
            const val QR_CODE_RESOURCE = "qr_code_resource"
            const val INVENTORY_VIEW_USER= "inventory_view_user"
            const val INVENTORY = "inventory"
        }

    }

    interface PrintStatus {
        companion object {
            const val DOWNLOAD = "download"
            const val FAIL_DOWNLOAD = "fail_download"
            const val NO_INTERNET = "no_internet"
            const val PRINTING = "printing"
            const val BLUETOOTH_OFF = "bluetooth_off"
            const val SAVE_PAIR_DEVICE  = "save_pair_device"
            const val DEVICE_BLUETOOTH_OFF = "device_bluetooth_off"
            const val COMMUNICATION_ERROR = "communication_error"
            const val UNKNOWN_ERROR = "unknown_error"
            const val SUCCESSFUL = "successful"
            const val STATUS1 = "STATUS1"//Please connect bluetooth from setting menu
            const val STATUS2 = "STATUS2"//One of the following may have occurred:\n -Bluetooth is off\n -Printer is not paired\n -Bluetooth is out of range
            const val STATUS3 = "STATUS3"
            const val SAVEPAIRDEVICE = "save_pair_device"
        }
    }




    //Enum Ref: https://www.geeksforgeeks.org/enum-classes-in-kotlin/
    enum class CE{
        NO,
        YES
    }

    enum class RISK {
        SELF_EXPLANATORY,
        USER_INSTRUCTION,
        MANUFACTURER;
    }

    enum class FIELDS{
        CATALOG_ID,
        INVENTORY_NUMBER
    }

    enum class OPERATOR_ORDINANCE(val key:String) {
        ANNEX_1("annex_1"),
        ANNEX_1_2("annex_1_2"),
        ANNEX_2("annex_2"),
        NONE("none")
    }

}