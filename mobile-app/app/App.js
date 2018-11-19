import React from "react";
import {StyleSheet, Text, View} from "react-native";
import {Provider} from "react-redux";
import store from './reducers/index.js';
import AppWithNavigationState from "./AppNavigator";
import RNLanguages from 'react-native-languages';
import i18n from "./i18n";

export default class App extends React.Component {

  componentWillMount() {
    RNLanguages.addEventListener('change', this._onLanguagesChange);
  }

  componentWillUnmount() {
    RNLanguages.removeEventListener('change', this._onLanguagesChange);
  }

  _onLanguagesChange = ({language}) => {
    i18n.locale = language;
  };

  render() {
    return (
      <Provider store={store}>
        <View style={styles.container}>
          <AppWithNavigationState/>
        </View>
      </Provider>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
});
