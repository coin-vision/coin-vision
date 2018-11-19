import {createStore, combineReducers, applyMiddleware} from "redux";
import {AppNavigator, ReactNavigationReduxMiddleware} from "../AppNavigator";
import searchInput from './searchInput'
import searchResult from './searchResult'

const firstAction = AppNavigator.router.getActionForPathAndParams('Main'); // CoinSearchScreen
const tempNavState = AppNavigator.router.getStateForAction(firstAction);

const initialNavState = AppNavigator.router.getStateForAction(
  tempNavState
);

function nav(state = initialNavState, action) {
  let nextState = AppNavigator.router.getStateForAction(action, state);

  // Simply return the original `state` if `nextState` is null or undefined.
  return nextState || state;
}

const rootReducer = combineReducers({
  nav,
  searchInput,
  searchResult
});

let store = createStore(
  rootReducer,
  applyMiddleware(ReactNavigationReduxMiddleware),
);

export default store;
