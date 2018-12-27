import React, {Component} from 'react'
import CoinsList from './CoinsList'
class Coins extends Component{

    constructor(props){
        super(props)
        this.state ={
            importUrl: ''
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }


    downloadImage = (imageToImport) =>{
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
                this.setState({predictions: text, downloadedImage: imageToImport})
        }).catch(e => console.log(e))
    };


    handleChange(e) {
        const itemName = e.target.name;
        const itemValue = e.target.value;
        this.setState({ [itemName]: itemValue });
    }

    handleSubmit(e) {
        e.preventDefault();
        const imageToImport = this.state.importUrl
        this.downloadImage(imageToImport)
        this.setState({ importUrl: '' })
    }



    arrayBufferToBase64 = (buffer) => {
        var binary = '';
        var bytes = [].slice.call(new Uint8Array(buffer));
        bytes.forEach((b) => binary += String.fromCharCode(b));
        return window.btoa(binary);
    };

    render() {
        const  predictions  = this.state.predictions
        const  downloadedImage = this.state.downloadedImage
        return (
            <div className="container mt-4">
                <div className="row justify-content-center">
                    <div className="col-md-8 text-center">

                        <div className="card bg-light">
                            <div className="card-body text-center">
                                <form
                                    className="formgroup"
                                    onSubmit={this.handleSubmit}
                                >
                                    <h4 className="font-weight-light">Import Coin</h4>
                                    <div className="input-group input-group-lg">
                                         <input
                                            type="text"
                                            className="form-control"
                                            name="importUrl"
                                            placeholder="Image Url"
                                            value={this.state.importUrl}
                                            aria-describedby="buttonAdd"
                                            onChange={this.handleChange}
                                        />
                                        <div className="input-group-append">
                                            <button
                                                type="submit"
                                                className="btn btn-sm btn-info"
                                                id="buttonAdd"
                                            >
                                                +
                                            </button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <div className="col-11 col-md-6 text-center">
                        <div className="card border-top-0 rounded-0">

                            {downloadedImage && (
                                <div className="list-group list-group-flush">
                                    <h4 className="card-title font-weight-light m-0">Original Image</h4>
                                    <img src={downloadedImage} />
                                </div>
                            )}

                            {this.props.coins && this.props.coins.length ? (
                                <div className="card-body py-2">
                                    <h4 className="card-title font-weight-light m-0">
                                        Your Coins
                                    </h4>

                                </div>
                            ) : null}
                            {predictions && (
                                <div className="list-group list-group-flush">
                                    <CoinsList predictions={[...predictions]}/>
                                </div>
                            )}

                        </div>
                    </div>
                </div>
            </div>
        );
    }


}

export default Coins;