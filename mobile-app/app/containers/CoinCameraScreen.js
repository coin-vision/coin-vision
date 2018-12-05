import React, {Component} from "react";
import {
  Image,
  View,
  StyleSheet,
  Text,
  TouchableOpacity,
  Button
} from "react-native";
import PropTypes from "prop-types";
import Ionicons from "react-native-vector-icons/Ionicons";
import {RNCamera} from "react-native-camera";
import ImageResizer from "react-native-image-resizer";
import globalStyles from "../GlobalStyleSheet";
import * as Config from "../Config";
import {handleImagePicked} from "../Utils";
import i18n from "../i18n";

export default class CoinCameraScreen extends Component {

  static navigationOptions = ({navigation}) => ( {
      title: i18n.t('Make-a-picture'),
      headerLeft: (
        <Text/>
      ),
      headerRight: (
        <View style={{padding: 5}}>
          <Button
            style={{backgroundColor: 'transparent', color: '3478f6'}}

            onPress={() => {
                        const store = navigation.state.params.store;
                        store.dispatch({ type: 'SET_TARGET_MASK', showTargetMask: false});
                        navigation.goBack();
                    }}
            title={i18n.t('Cancel')}
          />
        </View>
      ),

    }
  );

  state = {
    flash: 'off',
    zoom: 0,
    autoFocus: 'on',
    depth: 0,
    type: 'back',
    whiteBalance: 'auto',
    ratio: '16:9',
    ratios: [],
    photoId: 1,
    showGallery: false,
    photos: [],
    faces: [],
  };

  constructor(props: Object) {
    super(props);
  };

  componentDidMount() {
    const {store} = this.context;
    this.unsubscribe = store.subscribe(() =>
      this.forceUpdate()
    );
  };

  componentWillUnmount() {
    this.unsubscribe();
  };

  render() {
    const {store} = this.context;
    const state = store.getState();

    // moved take picture button outside of RNCamera
    // inside RNCamera doesn't work on Android
    return (
      <View style={styles.screenContainer}>
        <RNCamera
          ref={ref => {
                      this.camera = ref;
                    }}

          style={styles.cameraContainer}
          type={this.state.type}
          flashMode={this.state.flash}
          autoFocus={this.state.autoFocus}
          zoom={this.state.zoom}
          whiteBalance={this.state.whiteBalance}
          ratio={this.state.ratio}
          focusDepth={this.state.depth}
          permissionDialogTitle={'Permission to use camera'}
          permissionDialogMessage={'We need your permission to use your camera phone'}
        >

        </RNCamera>
        <View style={(state.searchInput.showTargetMask) ? styles.roundTarget: {}}>
          {
            (state.searchInput.showTargetMask) ? ( <Text style={styles.targetCross}> + </Text> ) : (<View/>)
          }
        </View>

        <TouchableOpacity
          style={styles.roundButton}
          onPress={ this.takePicture.bind(this) }
        >
          <Ionicons
            name={'ios-camera-outline'}
            size={50}
            style={{ color: '#3478f6' }}
          />
          <Text style={globalStyles.buttonText}>
            {i18n.t('Photo')}
          </Text>
        </TouchableOpacity>

      </View>
    );
  };

  takePicture = async function () {
    if (this.camera) {
      this.camera.takePictureAsync().then(data => {
        // console.log('data: ', data.uri);
        const {store} = this.context;
        store.dispatch({type: 'SET_TARGET_MASK', showTargetMask: false});

        Image.getSize(data.uri, (width, height) => {
            let expectedWidth = 500; // scale down to this width
            store.dispatch({type: 'SET_IMAGE_ROTATION', imageRotation: (width > height) ? 'LANDSCAPE' : 'PORTRAIT'});

            let scaleFactor = Math.min(width / expectedWidth, height / expectedWidth);
            ImageResizer.createResizedImage(data.uri, width / scaleFactor, height / scaleFactor, 'JPEG', 80)
              .then((response) => {

                handleImagePicked(store, response.uri, Config.IMAGE_SOURCE_PHOTO); // handle / upload resized image

              }).catch(() => {
              // Oops, something went wrong. Handle/upload original size
              handleImagePicked(store, data.uri, Config.IMAGE_SOURCE_PHOTO);
            });
          }
        );

        const navigation = this.props.navigation;
        navigation.goBack();

      });
    }
  };

}

CoinCameraScreen.contextTypes = {
  store: PropTypes.object
};


const styles = StyleSheet.create({
  screenContainer: {
    flex: 1,
  },

  cameraContainer: {
    flex: 1,
  },

  roundTarget: {
    position: 'absolute',
    borderColor: 'rgba(0,0,0,0.5)',
    borderWidth: Config.BORDER_WIDTH,
    alignItems: 'center',
    justifyContent: 'center',
    width: Config.CIRCLE_DIAMETER,
    height: Config.CIRCLE_DIAMETER,
    backgroundColor: 'transparent',
    borderRadius: Config.CIRCLE_DIAMETER,
    left: 0.5 * Config.SCREEN_WIDTH - 0.5 * Config.CIRCLE_DIAMETER,
    top: 0.3 * Config.SCREEN_HEIGHT - 0.5 * Config.CIRCLE_DIAMETER
  },

  targetCross: {
    color: '#FF0000',
    backgroundColor: 'transparent',
    fontSize: 25,
  },

  roundButton: {
    position: 'absolute',
    borderWidth: 1,
    borderColor: Config.DARK_GRAY_COLOR,
    alignItems: 'center',
    justifyContent: 'center',
    width: Config.CAMERA_BUTTON_DIAMETER,
    height: Config.CAMERA_BUTTON_DIAMETER,
    backgroundColor: 'rgb(235,235,235)',
    borderRadius: Config.CAMERA_BUTTON_DIAMETER,
    left: 0.5 * Config.SCREEN_WIDTH - 0.5 * Config.CAMERA_BUTTON_DIAMETER,
    top: Config.SCREEN_HEIGHT - 2 * Config.CAMERA_BUTTON_DIAMETER
  },


});
