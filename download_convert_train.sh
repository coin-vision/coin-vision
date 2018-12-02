#!/bin/sh


DATA_DIR=./coins-5-classes

DATASET_URL=https://s3.amazonaws.com/coin-vision/coins-5-classes.csv

DATASET_DIR=${DATA_DIR}
TRAIN_DIR=${DATASET_DIR}/train_logs


dataset-extractor/gradlew -b dataset-extractor/build.gradle clean shadowJar && java -jar dataset-extractor/build/libs/dataset-extractor-all.jar ${DATASET_URL} ${DATA_DIR}

source ~/tensorflow/bin/activate


# converting image to *.tfrecord format

# before the next steps you can set training set amount and number of shards -> check/fix convert_coins.py

python dnn-trainer-tf/download_and_convert_data.py \
    --dataset_name=coins \
    --dataset_dir="${DATA_DIR}" 



# training model

# starting TensorBoard to monitor training

tensorboard --logdir=${TRAIN_DIR} &

# if run on CPU add --clone_on_cpu=True

python dnn-trainer-tf/train_image_classifier.py \
    --train_dir=${TRAIN_DIR} \
    --dataset_dir=${DATASET_DIR} \
    --dataset_name=coins \
    --batch_size=64 \
    --learning_rate=0.01 \
    --dataset_split_name=train \
    --model_name=inception_v3