import React, {Component} from "react";
import {
  ActivityIndicator,
  Image,
  Linking,
  StyleSheet,
  Text,
  TouchableOpacity,
  TouchableHighlight,
  View,
  ScrollView
} from "react-native";
import PropTypes from "prop-types";
import Ionicons from "react-native-vector-icons/Ionicons";
import ImagePicker from "react-native-image-picker";
import globalStyles from "../GlobalStyleSheet";
import * as Config from "../Config";
import {handleImagePicked, search, geCoinsHomeLanguage} from "../Utils";
import i18n from "../i18n";

let options = {
  storageOptions: {
    skipBackup: true,
    path: 'images'
  }
};

export default class CoinSearchScreen extends Component<{}, {}> {

  static navigationOptions = () => {

    let titleStr = 'Coin Vision';

    let headerLeftComponent = <View/>;
    let headerRightComponent = <View/>;

    return ( {
        header: (
          <View style={globalStyles.navigationOptionsContainer}>
            <View style={globalStyles.navigationOptionsHeaderContainer}>
              <View style={globalStyles.navigationOptionsHeaderLeftComponent}>
                {headerLeftComponent}
              </View>
              <View style={globalStyles.navigationOptionsHeaderCenterComponent}>
                <Text style={{ color: '#222222', fontSize: 15, fontWeight: 'bold', }}>{titleStr}</Text>
              </View>
              <View style={globalStyles.navigationOptionsHeaderRightComponent}>
                {headerRightComponent}
              </View>
            </View>
          </View>
        )
      }
    );
  };


  constructor(props: Object) {
    super(props);
  }

  componentDidMount() {
    const {store} = this.context;
    this.unsubscribe = store.subscribe(() =>
      this.forceUpdate()
    );
  }

  componentWillUnmount() {
    this.unsubscribe();
  }

  render() {

    const {store} = this.context;
    const state = store.getState();

    let showCameraRoll, showPhoto, showSearch, showReset, showSubtitle = false;
    if ("done" !== store.getState().searchResult.predictionStatus) {
      showCameraRoll = (null == state.searchInput.image1 || null == state.searchInput.image2);
      showPhoto = (null == state.searchInput.image1 || null == state.searchInput.image2);
      showSearch = (null != state.searchInput.image1 || null != state.searchInput.image2);
      showReset = (null != state.searchInput.image1 || null != state.searchInput.image2);
      showSubtitle = (null == state.searchInput.image1 && null == state.searchInput.image2);
    } else {
      showCameraRoll = (null == state.searchInput.image1 || null == state.searchInput.image2);
      showPhoto = (null == state.searchInput.image1 || null == state.searchInput.image2);
      showSearch = (null != state.searchInput.image1 && null != state.searchInput.image2);
      // showCameraRoll = false;
      // showPhoto = false;
      // showSearch = false;
      showReset = true;
      showSubtitle = false;
    }

    let searchInStr = 'coinshome.net public catalog';

    return (
      <View style={globalStyles.container}>
        <ScrollView style={globalStyles.scrollContainer}
                    contentContainerStyle={{justifyContent: 'center', alignItems: 'center'}}
        >
          {
            (showSubtitle)
              ? <Text style={styles.subtitleText}>{i18n.t('please-upload-coin-pictures')}</Text>
              : <View/>
          }

          <View style={styles.imageContainer}>
            {
              (state.searchInput.image1)
                ? <Image style={[styles.uploadingImage,
                                        ('LANDSCAPE' === state.searchInput.imageRotation)
                                            ? {resizeMode: 'contain', transform: [{ rotate: '90deg'}]}
                                            : {resizeMode: 'contain'}
                                        ]} source={{ uri: state.searchInput.image1 }}/>
                : <Image style={styles.uploadingImage} source={require('./../../assets/upload_obverse.png')}/>
            }
            {
              (state.searchInput.image2)
                ? <Image style={[styles.uploadingImage,
                                        ('LANDSCAPE' === state.searchInput.imageRotation)
                                            ? {resizeMode: 'contain', transform: [{ rotate: '90deg'}]}
                                            : {resizeMode: 'contain'}
                                    ]} source={{ uri: state.searchInput.image2 }}/>
                : <Image style={styles.uploadingImage} source={require('./../../assets/upload_reverse.png')}/>
            }
          </View>

          {
            (null != searchInStr) ? <Text
                style={{ flex: 1, color: Config.DARK_GRAY_COLOR, fontSize: 13, fontWeight: 'bold'}}> {i18n.t('Search-in')}: {searchInStr} </Text> :
              <Text/>
          }

          <View style={styles.controlPanelContainer}>

            {
              (showCameraRoll)
                ? <View style={globalStyles.controlButtonContainer}>
                  <TouchableOpacity style={globalStyles.roundButton} onPress={this._pickImage}>
                    <Ionicons
                      name='ios-images-outline'
                      size={50}
                      style={{ color: '#3478f6' }}
                    />
                    <Text style={globalStyles.roundButtonText}>
                      {i18n.t('Camera-Roll')}
                    </Text>
                  </TouchableOpacity>

                </View>
                : <View/>
            }
            {
              (showPhoto)
                ? <View style={globalStyles.controlButtonContainer}>
                  <TouchableOpacity
                    style={globalStyles.roundButton}
                    onPress={this._takePicture}
                  >
                    <Ionicons
                      name={'ios-camera-outline'}
                      size={50}
                      style={{ color: '#3478f6' }}
                    />
                    <Text style={globalStyles.roundButtonText}>
                      {i18n.t('Photo')}
                    </Text>
                  </TouchableOpacity>
                </View>
                : <View/>
            }
          </View>

          <View style={styles.controlPanelContainer}>

            {
              (showSearch)
                ? <View style={globalStyles.controlButtonContainer}>
                  <TouchableOpacity style={globalStyles.roundButton} onPress={() => search(store)}>
                    <Ionicons
                      name={'ios-search-outline'}
                      size={50}
                      style={{ color: '#3478f6' }}
                    />
                    <Text style={globalStyles.roundButtonText}>
                      {i18n.t('Search')}
                    </Text>
                  </TouchableOpacity>

                </View>
                : <View/>
            }

            {
              (showReset)
                ? <View style={globalStyles.controlButtonContainer}>
                  <TouchableOpacity style={globalStyles.roundButton} onPress={this._reset}>
                    <Ionicons
                      name={'ios-refresh-outline'}
                      size={50}
                      style={{ color: '#3478f6' }}
                    />
                    <Text style={globalStyles.roundButtonText}>
                      {i18n.t('Reset')}
                    </Text>
                  </TouchableOpacity>
                </View>
                : <View/>
            }
          </View>

          { this._showSearchResult() }


        </ScrollView>
        { this._maybeRenderUploadingOverlay() }
      </View>
    );
  }

  _takePicture = async() => {
    const navigation = this.props.navigation;
    const {store} = this.context;
    store.dispatch({type: 'SET_TARGET_MASK', showTargetMask: true});
    navigation.navigate('CoinCamera', {store: store});
  };

  _cancelSearch = async() => {
    const {store} = this.context;
    store.dispatch({type: 'CANCEL_SEARCH'});
  };

  _maybeRenderUploadingOverlay = () => {
    const {store} = this.context;
    const state = store.getState();

    if ("processing" === state.searchResult.predictionStatus) {
      return (
        <View
          style={[StyleSheet.absoluteFill,
            {backgroundColor: 'rgba(0,0,0,0.8)', alignItems: 'center', justifyContent: 'center', flex: 1, flexDirection: 'column'}]}>
          <ActivityIndicator
            style={{flex: 3}}
            color="#fff"
            animating
            size="large"
          />
          <View
            style={{flex: 1}}
          >
            <TouchableOpacity
              style={globalStyles.roundButton}
              onPress={this._cancelSearch}
            >
              <Ionicons
                name={'ios-close'}
                size={50}
                style={{ color: '#3478f6' }}
              />
              <Text style={globalStyles.roundButtonText}>
                {i18n.t('Cancel')}
              </Text>
            </TouchableOpacity>
          </View>
        </View>
      );
     }
  };

  _showSearchResult = () => {

    const {store} = this.context;
    const state = store.getState();
    let searchResult = state.searchResult;

    if ("done" !== searchResult.predictionStatus) {
      return;
    }

    let language = geCoinsHomeLanguage();

    return (
      <View style={styles.searchResultContainer}>
        <Text
          style={{ flex: 1, paddingVertical: 10, paddingHorizontal: 10, color: '#222222', fontSize: 17, fontWeight: 'bold', }}>
          {i18n.t('Search-result')}
        </Text>

        {searchResult.coinGroups.map((coinGroup) => {
          return (
            <TouchableHighlight style={{flex: 1}}
                                key={'' + coinGroup.coinId}
                                onPress={
                                                () => {
                                                    Linking.openURL("https://www.coinshome.net/" + language + "/coin_definition-a-b-c-" + coinGroup.coinId + ".htm");
                                                }
                                            }
                                underlayColor='#99d9f4'>

              <View style={styles.searchResultContainer}>
                <Image key={'' + coinGroup.imageId}
                       source={{ uri: Config.BE_URL_IMAGE + coinGroup.imageId +'.jpg' }}
                       style={{flex: 1, width: 300, height: 150, resizeMode: 'contain'}}
                />
                <Text
                  style={styles.coinGroupTitleText}>
                  probability: {coinGroup.probability}
                </Text>

              </View>
            </TouchableHighlight>
          )
        })}


      </View>
    )
  };

  _reset = async() => {
    const {store} = this.context;
    store.dispatch({type: 'RESET_SEARCH'});
  };


  _pickImage = async() => {

    ImagePicker.launchImageLibrary(options, (response) => {

      console.log('Response = ', response);

      if (response.didCancel) {
        console.log('User cancelled image picker');
      }
      else if (response.error) {
        console.log('ImagePicker Error: ', response.error);
      }
      else if (response.customButton) {
        console.log('User tapped custom button: ', response.customButton);
      }
      else {
        const {store} = this.context;
        handleImagePicked(store, response.uri, Config.IMAGE_SOURCE_CAMERA_ROLL);
      }
    });

  };


} // export default class CoinSearchScreen extends Component


CoinSearchScreen.contextTypes = {
  store: PropTypes.object
};


const styles = StyleSheet.create({


  searchResultContainer: {
    flex: 1,
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#FAFAFA'
  },

  controlPanelContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#FAFAFA',
  },

  imageContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#FAFAFA',
    paddingTop: 10,
    paddingBottom: 10,
    paddingLeft: 3,
    paddingRight: 3
  },

  uploadingImage: {
    flex: 1,
    width: 200,
    height: 200,
    margin: 0,
  },

  subtitleText: {
    flex: 1,
    paddingVertical: 5,
    paddingHorizontal: 10,
    color: Config.DARK1_COLOR,
    fontSize: 15,
    fontWeight: 'bold',
    paddingTop: 20
  },

  coinGroupTitleText: {
    flex: 1,
    paddingVertical: 10,
    paddingHorizontal: 10,
    paddingBottom: 20,
    paddingTop: 10,
    color: '#808080',
    fontSize: 14,
    alignItems: 'center',
    justifyContent: 'center',
    fontWeight: 'bold',
  }

});
