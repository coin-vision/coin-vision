import CoinsList from "../components/CoinsList"
import { connect } from 'react-redux'

const mapStateToProps = state => ({
  predictions: state.predictions
})

export default connect(
  mapStateToProps
)(CoinsList)
