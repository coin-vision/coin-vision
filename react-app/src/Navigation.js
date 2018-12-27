import React, { Component } from 'react';
import { FaUsers } from 'react-icons/fa';
import { Link } from '@reach/router';

class Navigation extends Component {

  render() {
    return (
      <nav className="site-nav family-sans navbar navbar-expand bg-primary navbar-dark higher">
        <div className="container-fluid">
          <Link to="/" className="navbar-brand">
            <FaUsers className="mr-1" /> CoinVision Monitor
          </Link>
          <div className="navbar-nav ml-auto">
          </div>
        </div>
      </nav>
    );
  }
}

export default Navigation;
