import Coins from '../components/Coins'
import { connect } from "react-redux";
import {displayPredictions} from '../actions/actions'

 const   downloadImage = (dispatch, imageToImport) => {
        const options = {
            method: 'POST',
            body: JSON.stringify({importUrl: imageToImport}),
            headers: new Headers({
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            })
        };

        fetch('http://localhost:8888/search-by-url', options)
            .then(function(response) {
                return response.json()
            }).then(text => {
                dispatch(displayPredictions(text, imageToImport))
        }).catch(e => console.log(e))
    };


  const mapDispatchToProps = dispatch => ({
    downloadImage: importUrl => downloadImage(dispatch, importUrl)
  })

  const mapStateToProps = state => ({
    downloadedImage: state.downloadedImage,
    importUrl : state.importUrl
  })

export default connect(mapStateToProps, mapDispatchToProps)(Coins);
