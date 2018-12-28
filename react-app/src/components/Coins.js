import React from 'react'
import CoinsListContainer from '../containers/CoinsListContainer'

const Coins = ({downloadedImage, importUrl, downloadImage }) => {

  let input

  return (
    <div className="container mt-4">
      <div className="row justify-content-center">
        <div className="col-md-8 text-center">

          <div className="card bg-light">
            <div className="card-body text-center">
              <form
                className="formgroup"
                onSubmit={e => {
                  e.preventDefault();
                  downloadImage(input.value)
                }
                }
              >
                <h4 className="font-weight-light">Import Coin</h4>
                <div className="input-group input-group-lg">
                  <input
                    type="text"
                    className="form-control"
                    name="importUrl"
                    placeholder="Image Url"
                    ref={node => (input = node)}
                    aria-describedby="buttonAdd"
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
              <div className="list-group-flush">
                <h4 className="card-title font-weight-light m-0">Original Image</h4>
                <img alt={""} src={downloadedImage}/>
              </div>
            )}

            {/*{this.props.coins && this.props.coins.length ? (*/}
              {/*<div className="card-body py-2">*/}
                {/*<h4 className="card-title font-weight-light m-0">*/}
                  {/*Your Coins*/}
                {/*</h4>*/}

              {/*</div>*/}
            {/*) : null}*/}
            <div className="list-group list-group-flush">
              <CoinsListContainer/>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Coins;
