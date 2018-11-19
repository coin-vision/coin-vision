package net.coinshome.coinvision.web;

public class PredictionInfo {

    private String coinId;
    private String imageId;
    private float probability;

    public PredictionInfo(String coinId, String imageId, float probability) {
        super();
        this.coinId = coinId;
        this.imageId = imageId;
        this.probability = probability;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((coinId == null) ? 0 : coinId.hashCode());
        result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
        result = prime * result + Float.floatToIntBits(probability);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PredictionInfo other = (PredictionInfo) obj;
        if (coinId == null) {
            if (other.coinId != null)
                return false;
        } else if (!coinId.equals(other.coinId))
            return false;
        if (imageId == null) {
            if (other.imageId != null)
                return false;
        } else if (!imageId.equals(other.imageId))
            return false;
        if (Float.floatToIntBits(probability) != Float.floatToIntBits(other.probability))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PredictionInfo [coinId=" + coinId + ", imageId=" + imageId + ", probability=" + probability + "]";
    }


}
