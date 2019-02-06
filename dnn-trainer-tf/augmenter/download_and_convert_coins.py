"""
Downloads and converts coins dataset from coinshome.net.
"""
from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import urllib
import math
import os
import random
import sys
import csv
import tensorflow as tf

_IMAGE_BASE_URL = "https://st.coinshome.net/fs/600_300/{}.jpg"

_DATASET_CSV_FILE = "dataset.csv"


class CoinsHomeImageDownloader(object):
    
    _ORIGINAL_DIR = "original-images"

    def __init__(self, dataset_output_dir, csv_dataset_file):
        self._csv_dataset_file = os.path.join(dataset_output_dir, csv_dataset_file)
        self._dataset_output_dir = dataset_output_dir
        self._original_images_dir  = os.path.join(dataset_output_dir, CoinsHomeImageDownloader._ORIGINAL_DIR)


    def _dataset_exists(self):
        if not tf.gfile.Exists(self._dataset_output_dir):
            print('Dataset directory {} does not exists'.format(self._dataset_output_dir))
            return False
        if not tf.gfile.Exists(self._csv_dataset_file):
            print('Dataset file {} does not exists'.format(self._csv_dataset_file))
            return False
        if not tf.gfile.Exists(self._original_images_dir):
            print('Creating dir {} .....'.format(self._original_images_dir))
            tf.gfile.MakeDirs(self._original_images_dir)
        return True
    
    def download(self):
        if not self._dataset_exists():
            return
        with open(self._csv_dataset_file) as csv_file:
            csv_reader = csv.reader(csv_file, delimiter=',')
            line_count = 0
            #imageNum, coinGroupNum, imageId, ciid, coinGroupId
            for row in csv_reader:
                if line_count == 0:
                    line_count += 1
                else:
                    image_id = row[2]
                    coin_detail_id = row[4]
                    self._download_and_save_image(image_id, coin_detail_id)
                    line_count += 1
            print(f'Processed {line_count} images.')
    
    def _download_and_save_image(self, image_id, coin_detail_id):
        output_dir = os.path.join(self._original_images_dir, coin_detail_id)
        image_url = _IMAGE_BASE_URL.format(image_id)
        filename = image_url.split('/')[-1]
        output_file = os.path.join(output_dir, filename)
        if  tf.gfile.Exists(output_file):
            return
        elif not tf.gfile.Exists(output_dir):
            tf.gfile.MakeDirs(output_dir)
        filepath, _ = urllib.request.urlretrieve(image_url, output_file)
        statinfo = os.stat(filepath)
        if statinfo.st_size <= 10000:
            print(f'Warning:  small file {output_file} - {statinfo.st_size} bytes')
        else:
            print(f'..... downloaded {output_file} - info {statinfo.st_size} bytes')
        
    

def main():
    tf.logging.set_verbosity(tf.logging.WARN)
    
    downloader = CoinsHomeImageDownloader("usa-coins-398", _DATASET_CSV_FILE)
    
    downloader.download()

if __name__ == "__main__": main()
