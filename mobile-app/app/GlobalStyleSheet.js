import {StyleSheet, Platform} from "react-native";
import * as Config from "./Config";

export default StyleSheet.create({

  container: {
    flex: 1,
    flexDirection: 'row',
    backgroundColor: '#FAFAFA',
  },

  scrollContainer: {
    flex: 1,
    backgroundColor: '#FAFAFA',
    flexDirection: 'column',
  },

  navigationOptionsContainer: {
    height: Platform.OS === 'ios' ? 60 : 36,
  },

  navigationOptionsHeaderContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'flex-end',
    justifyContent: 'center',
    backgroundColor: 'rgb(235,235,235)',
    marginTop: 0,
    paddingBottom: 7,
  },

  navigationOptionsHeaderLeftComponent: {
    flex: 2,
    justifyContent: 'center',
    alignItems: 'flex-start',
  },

  navigationOptionsHeaderCenterComponent: {
    flex: 3,
    justifyContent: 'center',
    alignItems: 'center',
  },

  navigationOptionsHeaderRightComponent: {
    flex: 2,
    justifyContent: 'center',
    alignItems: 'flex-end',
  },

  controlButtonContainer: {
    flex: 1,
    paddingTop: 10,
    paddingBottom: 10,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 7
  },

  roundButton: {
    borderWidth: 1,
    borderColor: Config.DARK_GRAY_COLOR,
    alignItems: 'center',
    justifyContent: 'center',
    width: 105,
    height: 105,
    backgroundColor: 'rgb(235,235,235)',
    borderRadius: 105,
  },

  roundButtonText: {
    color: '#222222',
    fontSize: 13,
    fontWeight: 'bold'
  },

  buttonText: {
    fontSize: 18,
    color: Config.DARK_GRAY_COLOR,
    alignSelf: 'center'
  },

  button: {
    height: 36,
    backgroundColor: Config.MIDDLE_GRAY_COLOR2,
    borderColor: Config.MIDDLE_GRAY_COLOR2,
    borderWidth: 1,
    borderRadius: 8,
    marginBottom: 10,
    alignSelf: 'stretch',
    justifyContent: 'center'
  },

});

