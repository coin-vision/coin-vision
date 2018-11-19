import {Dimensions} from "react-native";

export const SUPPORTED_LANGUAGES = ['en', 'de', 'fr', 'ru', 'es', 'it', 'uk', 'zh', 'pt'];
export const DEFAULT_LANGUAGE = 'en';


const BE_HOST = 'http://192.168.1.245:8888';

export const BE_URL_UPLOAD_PICTURE = BE_HOST + '/search-from-mobile';

// storage with coin images
export const BE_URL_IMAGE = 'https://d3k6u6bv48g1ck.cloudfront.net/fs/';


export const STORAGE_CATALOGS_KEY = 'STORAGE_CATALOGS_KEY';
export const STORAGE_USERNAME_KEY = 'STORAGE_USERNAME_KEY';
export const STORAGE_PASSWORD_KEY = 'STORAGE_PASSWORD_KEY';

export const LIST_ROW_BORDER_LEFT_COLOR = '#FFFFFF'; // rgb(225,225,225)
export const LIST_ROW_BORDER_RIGHT_COLOR = '#FFFFFF'; // rgb(225,225,225)
export const LIST_ROW_BORDER_TOP_COLOR = '#FFFFFF'; // rgb(225,225,225)
export const LIST_ROW_BORDER_BOTTOM_COLOR = '#E0E0E0'; // rgb(224,224,224)

export const LIST_ROW_BACKGROUND_COLOR = '#FAFAFA'; //rgb 250 250 250

export const WHITE_COLOR = '#FFFFFF'; // rgb(225,225,225)
export const WHITE_GRAY_COLOR = '#FAFAFA'; //rgb 250 250 250
export const MIDDLE_GRAY_COLOR1 = '#EBEBEB'; //rgb 235 235 235
export const MIDDLE_GRAY_COLOR2 = '#E0E0E0'; // rgb(224,224,224)
export const MIDDLE_GRAY_COLOR3 = '#BEBEBE'; // rgb(190,190,190)
export const MIDDLE_GRAY_COLOR4 = '#B6B6B6'; // rgb(182,182,182)
export const DARK_GRAY_COLOR = '#808080'; //rgb 128 128 128

export const DARK1_COLOR = '#222222'; // rgb 34 34 34

export const BLUE1_COLOR = '#009FDB'; // rgb 0 159 219
export const BLUE2_COLOR = '#169EDA'; // rgb(22, 158, 218)
export const BLUE3_COLOR = '#99d9f4'; // for buttons underlayColor

export const GREEN1_COLOR = '#10d03a'; // rgb 16 208 58

export const DEFAULT_ACTIVE_TINT_COLOR = '#3478f6'; // rgb(52, 120, 246) // in IOS

export const SCREEN_WIDTH = Dimensions.get('window').width;
export const SCREEN_HEIGHT = Dimensions.get('window').height;
export const INNER_DIAMETER = 0.8 * SCREEN_WIDTH;
export const BORDER_WIDTH = SCREEN_HEIGHT;
export const CIRCLE_DIAMETER = INNER_DIAMETER + 2 * BORDER_WIDTH;
export const CAMERA_BUTTON_DIAMETER = 105;

export const IMAGE_SOURCE_PHOTO = 'photo'; // images from camera done with target mask and can be cropped in backend
export const IMAGE_SOURCE_CAMERA_ROLL = 'camera-roll';