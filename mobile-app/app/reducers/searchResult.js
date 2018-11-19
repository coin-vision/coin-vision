const searchResult = (state = {
  coinGroups: [{
    coinId: null,
    imageId: null,
    title: ''
  }],
  ciid: null,
  predictionStatus: null
}, action) => {

  switch (action.type) {
    case 'START_SEARCH':
      return {...state, predictionStatus: "processing"};

    case 'SET_CIID':
      return {...state, ciid: action.ciid, predictionStatus: "processing"};

    case 'SET_COIN_GROUPS':
      return {...state, coinGroups: action.coinGroups, predictionStatus: action.predictionStatus};

    case 'RESET_SEARCH':
      return {...state, coinGroups: null, predictionStatus: null, ciid: null};

    case 'IMAGE_UPLOAD_FAILED': // same as reset
      return {...state, coinGroups: null, predictionStatus: null, ciid: null};

    case 'CHECK_PREDICTION_FAILED': // same as reset
      return {...state, coinGroups: null, predictionStatus: null, ciid: null};

    default:
      return state
  }
};

export default searchResult;
