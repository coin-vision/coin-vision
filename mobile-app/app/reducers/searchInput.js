const searchResult = (state = {
  showTargetMask: true,
  imageRotation: null,
  image1: null,
  image2: null,
  imageSource: null
}, action) => {

  switch (action.type) {
    case 'SET_TARGET_MASK':
      return {...state, showTargetMask: action.showTargetMask};

    case 'SET_IMAGE_ROTATION':
      return {...state, imageRotation: action.imageRotation};

    case 'SET_IMAGE_1':
      return {...state, image1: action.image1, imageSource: action.imageSource};

    case 'SET_IMAGE_2':
      return {...state, image2: action.image2, imageSource: action.imageSource};

    case 'RESET_SEARCH':
      return {...state, image1: null, image2: null, showTargetMask: true, imageSource: null, imageRotation: null};

    default:
      return state
  }
};

export default searchResult;
