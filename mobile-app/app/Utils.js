import {AsyncStorage, Alert, NativeModules, Platform} from "react-native";
import i18n from "./i18n";
import * as Config from "./Config";


export async function uploadImageAsync(store: any, uri1: String, uri2: String) {

  const state = store.getState();

  store.dispatch({type: 'START_SEARCH'});

  let activeCatalogs = '';

  let uriParts1 = uri1.split('.');
  let fileType1 = uriParts1[uriParts1.length - 1]; // 'image/jpeg';

  let formData = new FormData();
  formData.append('image', {
    uri: uri1,
    name: `my_picture1.${fileType1}`,
    type: `image/${fileType1}`,
  });

  if (null != uri2) {
    let uriParts2 = uri2.split('.');
    let fileType2 = uriParts2[uriParts2.length - 1]; // 'image/jpeg';

    formData.append('image2', {
      uri: uri2,
      name: `my_picture2.${fileType2}`,
      type: `image/${fileType2}`,
    });
  }
  // params to help cut off round area in backend
  formData.append('screenWidth', Config.SCREEN_WIDTH);
  formData.append('screenHeight', Config.SCREEN_HEIGHT);
  formData.append('targetDiameter', Config.INNER_DIAMETER);
  formData.append('targetCenterX', 0.5 * Config.SCREEN_WIDTH);
  formData.append('targetCenterY', 0.3 * Config.SCREEN_HEIGHT);
  formData.append('imageSource', state.searchInput.imageSource); // camera or camera roll
  formData.append('imageRotation', state.searchInput.imageRotation); // landscape of portrait

  let options = {
    method: 'POST',
    body: formData,
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'multipart/form-data',
    },
  };

  // response sample: {"ciid":"vEDAqAH1LbUAAAFkuC9f3q56"}
  return fetch(Config.BE_URL_UPLOAD_PICTURE, options)
    .then((response) => response.json())
    .then((responseJson) => {

      console.log('Utils.checkPredictionAsync(): responseJson: ' + JSON.stringify(responseJson));

      if (null != responseJson && null != responseJson.coinGroups && responseJson.coinGroups.length > 0) {
        store.dispatch({
          type: 'SET_COIN_GROUPS',
          coinGroups: responseJson.coinGroups,
          predictionStatus: responseJson.predictionStatus
        });
      }

    })
    .catch((error) => {
      console.log('Utils.uploadImageAsync(): ERROR uploading image : ' + error);

      // release featching flag, so it able reqauest next page later
      store.dispatch({type: 'IMAGE_UPLOAD_FAILED'});

      Alert.alert('Server connection error');
    });

};

export async function handleImagePicked(store, imageURI, imageSource) {

  const state = store.getState();
  try {
    if (state.searchInput.image1) {
      store.dispatch({type: 'SET_IMAGE_2', image2: imageURI, imageSource: imageSource});
      search(store);

    } else {
      store.dispatch({type: 'SET_IMAGE_1', image1: imageURI, imageSource: imageSource});
    }

  } catch (e) {
    console.log({e});
    alert('Upload failed, sorry :(');
  }
};


export async function search(store: Object) {
  const state = store.getState();
  uploadImageAsync(store, state.searchInput.image1, state.searchInput.image2);
};

export function geUserLanguage() {
  let language = i18n.currentLocale();
  // de_DE -> de ?
  language = language.length > 2 ? language.substr(0, 2) : language;

  if (Config.SUPPORTED_LANGUAGES.indexOf(language) < 0)
    language = Config.DEFAULT_LANGUAGE;

  return language;
}

export function geCoinsHomeLanguage() {
  let language = geUserLanguage();
  language = ('pt' == language) ? 'es' : language; // for pt show es at coinshome.net links
  return language;
}
