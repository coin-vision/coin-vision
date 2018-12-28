import React from 'react';
import { FaUsers } from 'react-icons/fa';
import { Link } from '@reach/router';

const Navigation = () => (
      <nav className="site-nav family-sans navbar navbar-expand bg-primary navbar-dark higher">
        <div className="container-fluid">
          <Link to="/" className="navbar-brand">
            <FaUsers className="mr-1" /> CoinVision Monitor
          </Link>
          <div className="navbar-nav ml-auto">
          </div>
        </div>
      </nav>
)

export default Navigation;
