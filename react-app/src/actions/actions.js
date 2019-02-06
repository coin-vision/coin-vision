
export const IMPORT_IMAGE = "IMPORT_IMAGE"
export const DISPLAY_PREDICTION = "DISPLAY_PREDICTION"

export const importImage = (imageUrl) => {
    return {
        type: IMPORT_IMAGE,
        imageUrl
    }
}

export const displayPredictions = (predictions, imageToImport) => {
    return {
        type: DISPLAY_PREDICTION,
        predictions,
        imageToImport
    }
}
