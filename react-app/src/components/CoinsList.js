import React from 'react';
import { GoTrashcan, GoListUnordered } from 'react-icons/go';
import { FaLink } from 'react-icons/fa';
import PropTypes from 'prop-types'
import { navigate } from '@reach/router';

const  CoinsList = (props) => {
        const { predictions } = props;
        const predictionList = predictions.map(item => {
            return (
                <div className="list-group-item d-flex" key={item.imageId}>
                    <section
                        className="btn-group align-self-center"
                        role="group"
                        aria-label="Prediction Options"
                    >
                        <button
                            className="btn btn-sm btn-outline-secondary"
                            title="Delete Prediction"
                            onClick={e => deleteEntry(e, item.imageId)}
                        >
                            <GoTrashcan />
                        </button>
                        <button
                            className="btn btn-sm btn-outline-secondary"
                            title="Save"
                            onClick={() =>
                                navigate(
                                    `/checkin/${this.props.userID}/${item.imageId}`
                                )
                            }
                        >
                            <FaLink />
                        </button>
                        <button
                            className="btn btn-sm btn-outline-secondary"
                            title="Attendees List"
                            onClick={() =>
                                navigate(
                                    `/coin/${this.props.imageId}/${item.imageId}`
                                )
                            }
                        >
                            <GoListUnordered />
                        </button>
                    </section>

                    <section className="pl-3 text-left align-self-center">
                        <a href={`https://www.coinshome.net/coin_details.htm?id=${item.coinId}`} target={"_blank"} >
                          <img alt={`${item.coinId}`} src={`https://d3k6u6bv48g1ck.cloudfront.net/fs/400_200/${item.imageId}.jpg`} />
                        </a>
                    </section>
                </div>
            );
        });

        return <div>{predictionList}</div>;
    }


    const deleteEntry = (e, imageId) => {
      e.preventDefault();
      console.log("Remove" + imageId);
    };

CoinsList.propTypes = {
  predictions: PropTypes.arrayOf(PropTypes.shape({
    coinId: PropTypes.string.isRequired,
    imageId: PropTypes.string.isRequired,
    probability: PropTypes.number.isRequired
  }).isRequired).isRequired
}

export default CoinsList;
