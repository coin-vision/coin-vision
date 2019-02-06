import { createStore, applyMiddleware } from 'redux';
import {IMPORT_IMAGE, DISPLAY_PREDICTION} from './actions';
import { composeWithDevTools } from 'redux-devtools-extension';
import thunkMiddleware from 'redux-thunk'

// initial state
const initialState = {
    imageUrl:"",
    downloadedImage: "",
    predictions: []
}


// reducers
export const reducer = (state = initialState, action) => {
    switch (action.type) {
        case IMPORT_IMAGE:
            return Object.assign({}, state, {
                importUrl: action.imageUrl
            })
        case DISPLAY_PREDICTION:
            return Object.assign({}, state, {
                predictions: action.predictions,
                downloadedImage: action.imageToImport,
                importUrl: ''
            })
        default: return state
    }
}

let store = createStore(reducer, initialState, composeWithDevTools(applyMiddleware(thunkMiddleware)));

export default store;
