import React, { Component } from 'react';
import { GoTrashcan, GoListUnordered } from 'react-icons/go';
import { FaLink } from 'react-icons/fa';
import { navigate } from '@reach/router';

class CoinsList extends Component{
    constructor(props){
        super(props)
        this.state ={
            importUrl: ''
        }

    }


    deleteEntry = (e, imageId) => {
        e.preventDefault();

    };


    render() {
        const { predictions } = this.props;
        console.log([...predictions].length)
        const coins = Array.from(predictions).map(item => {
            return (
                <div className="list-group-item d-flex" key={item.imageId}>
                    <section
                        className="btn-group align-self-center"
                        role="group"
                        aria-label="Meeting Options"
                    >
                        <button
                            className="btn btn-sm btn-outline-secondary"
                            title="Delete Meeting"
                            onClick={e => this.deleteEntry(e, item.imageId)}
                        >
                            <GoTrashcan />
                        </button>
                        <button
                            className="btn btn-sm btn-outline-secondary"
                            title="Check In"
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
                        <a href={`https://www.coinshome.net/coin_details.htm?id=${item.coinId}`} target={"_blank"} > <img alt="${item.coinId}" src={`https://d3k6u6bv48g1ck.cloudfront.net/fs/400_200/${item.imageId}.jpg`} /> </a>
                    </section>
                </div>
            );
        });

        return <div>{coins}</div>;
    }


}

export default CoinsList;