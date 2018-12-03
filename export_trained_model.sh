#!/bin/sh

#DATASET_DIR=./coins-5-classes
DATASET_DIR=./usa-coins-398

TRAIN_DIR=${DATASET_DIR}/train_logs

CHECKPOINT_FILE=${TRAIN_DIR}/model.ckpt-0

python dnn-trainer-tf/export_inference_graph.py \
  --alsologtostderr \
  --dataset_name=coins \
  --model_name=inception_v3 \
  --output_file=${DATASET_DIR}/inception_v3_inf_graph.pb


freeze_graph \
  --input_graph=${DATASET_DIR}/inception_v3_inf_graph.pb \
  --input_checkpoint=${CHECKPOINT_FILE} \
  --input_binary=true \
  --output_graph=${DATASET_DIR}/frozen_inception_v3_graph.pb \
  --output_node_names=InceptionV3/Predictions/Reshape_1


# export to web-app
mkdir web-app/trained-model

cp ${DATASET_DIR}/dataset.csv web-app/trained-model/dataset.csv
cp ${DATASET_DIR}/labels.txt web-app/trained-model/labels.txt
cp ${DATASET_DIR}/frozen_inception_v3_graph.pb web-app/trained-model/graph.pb