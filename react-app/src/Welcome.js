import React, { Component } from 'react';
import Coins from './Coins'

class Welcome extends Component {
    render() {
        return (
            <div className="text-center mt-4">
               <Coins coinsList={{}}/>
            </div>
        );
    }
}

export default Welcome;
