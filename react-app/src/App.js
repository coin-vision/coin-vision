import React, { Component } from 'react'
import Navigation from './Navigation'
import Welcome from './Welcome'

class App extends Component {

    componentDidMount(){

    }

  render() {
    return (
        <div>
            <Navigation/>
             <Welcome />
      </div>
    );
  }
}

export default App;
