import React from "react";
import { connect } from 'react-redux';
import {createStackNavigator} from "react-navigation";
import { createReactNavigationReduxMiddleware, reduxifyNavigator } from 'react-navigation-redux-helpers';

import CoinSearchScreen from './screens/CoinSearchScreen';
import CoinCameraScreen from './screens/CoinCameraScreen';

export const ReactNavigationReduxMiddleware = createReactNavigationReduxMiddleware(
  "root",
  state => state.nav,
);

export const AppNavigator = createStackNavigator({
    Main: {screen: CoinSearchScreen},
    CoinCamera: {screen: CoinCameraScreen}
  },
  {
    // swipeEnabled: false,
    // lazyLoad: true,
    animationEnabled: true,
  }
);


const ReduxNavigator = reduxifyNavigator(AppNavigator, "root");
const mapStateToProps = (state) => ({
  state: state.nav,
});

export default connect(mapStateToProps)(ReduxNavigator);