#!/bin/sh

### Configs

#DATA_DIR=./coins-5-classes
#DATASET_URL=https://s3.amazonaws.com/coin-vision/coins-5-classes.csv

DATASET_DIR=./usa-coins-398
DATASET_URL=https://s3.amazonaws.com/coin-vision/usa-coins-398-classes.csv

TRAIN_DIR=${DATASET_DIR}/train_logs



# DOWNLOADING IMAGES

dataset-extractor/gradlew -b dataset-extractor/build.gradle clean shadowJar && java -jar dataset-extractor/build/libs/dataset-extractor-all.jar ${DATASET_URL} ${DATASET_DIR}


echo "Removing augmented files..."
find . -name '*_gen_*' -exec rm -rf {} \;

echo "Running augmentation..."
python dnn-trainer-tf/augmenter/augmenter.py  --dataset_train_dir="${DATASET_DIR}/labeled-images-tr" --dataset_test_dir="${DATASET_DIR}/labeled-images-tst"

# CONVERTING IMAGES TO *.tfrecord format

# before the next steps you can set training set amount and number of shards -> check/fix convert_coins.py

python dnn-trainer-tf/download_and_convert_data.py \
    --dataset_name=coins \
    --dataset_dir="${DATASET_DIR}" 



# TRAINING MODEL

# starting TensorBoard to monitor training
# tensorboard --logdir=${TRAIN_DIR} &

# if run on CPU add --clone_on_cpu=True

python dnn-trainer-tf/train_image_classifier.py \
    --train_dir=${TRAIN_DIR} \
    --dataset_dir=${DATASET_DIR} \
    --dataset_name=coins \
    --batch_size=64 \
    --learning_rate=0.01 \
    --dataset_split_name=train \
    --model_name=inception_v3