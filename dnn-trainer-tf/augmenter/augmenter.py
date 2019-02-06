#
#
#

import collections
import os.path
import random
import imgaug as ia
import numpy as np
import tensorflow as tf
from imgaug import augmenters as iaa
from scipy import ndimage, misc
from tensorflow.python.platform import gfile
from PIL import Image
import shutil
import concurrent.futures


generated_prefix = "_gen_"

FLAGS = tf.app.flags.FLAGS

tf.app.flags.DEFINE_string(
    'dataset_train_dir',
    None,
    'Folder with train data')

tf.app.flags.DEFINE_string(
    'dataset_test_dir',
    None,
    '')


def black_rec(root_dir, dir_name, base_name, image):
    img = Image.fromarray(np.uint8(image))
    result = Image.new("RGB", (mywidth, mywidth))
    wpercent = (float(mywidth) / float(img.size[0]))
    hsize = int((float(img.size[1]) * float(wpercent)))
    img = img.resize((mywidth, hsize), Image.ANTIALIAS)
    
    x = 0
    y = int((mywidth - img.size[1]) / 2)
    result.paste(img, (x, y))
    file_path = root_dir + "/" + dir_name + "/bl_rec_" + base_name
    misc.imsave(file_path, result)


def apply_gaussianBlur(dir_name, base_name, img):
    file_path = dir_name + "/gaussianBlur" + base_name
    tf.logging.info("Processing  Gaussian Blur ... target ${0}".format(file_path))
    blurer = iaa.GaussianBlur(0.70)
    updated_image = blurer.augment_image(img)
    misc.imsave(file_path, updated_image)


def apply_inver(root_dir, dir_name, base_name, img):
    # if base_name.startswith("avers_"):
    file_path = root_dir + "/" + dir_name + "/invert" + base_name
    tf.logging.info("Processing  inver ... target ${0}".format(file_path))
    invert = iaa.Invert(1, )
    updated_image = invert.augment_image(img)
    misc.imsave(file_path, updated_image)


def apply_affine(dir_name, base_name, img, zoom, save_to_file=True):

    file_path = dir_name + "/affine_zoom" + str(zoom) + base_name
    tf.logging.info("Processing  Affine ... target ${0}".format(file_path))
    blurer = iaa.Affine(scale=(zoom))
    updated_image = blurer.augment_image(img)
    if save_to_file:
        misc.imsave(file_path, updated_image)
    return updated_image


def apply_affine_rotate(root_dir, dir_name, base_name, img, rotate, prefix="", save_to_file=True):
    # if base_name.startswith("avers_"):
    file_name_prefix = prefix + "_affine_rotate_" + str(rotate) + "_"
    file_path = root_dir + "/" + dir_name + "/" + file_name_prefix + base_name
    tf.logging.info("Processing  affine ... target ${0}".format(file_path))
    blurer = iaa.Affine(rotate=(rotate))
    updated_image = blurer.augment_image(img)
    if save_to_file:
        misc.imsave(file_path, updated_image)
    return updated_image, file_name_prefix


def apply_edge_detect(dir_name, base_name, img):
    file_path = dir_name + "/edgeDetect" + base_name
    tf.logging.info("Processing  Edge Detect ... target ${0}".format(file_path))
    au = iaa.EdgeDetect(0.5)
    updated_image = au.augment_image(img)
    misc.imsave(file_path, updated_image)


def apply_dropout(dir_name, base_name, img):
    file_path = dir_name + "/dropout_" + base_name
    tf.logging.info("Processing  dropout ... target ${0}".format(file_path))
    blurer = iaa.Dropout(0.05)
    updated_image = blurer.augment_image(img)
    misc.imsave(file_path, updated_image)


def crop_and_save(dir_name, base_name, img):
    if "avers_" not in base_name:
        file_path = dir_name + "/avers_" + base_name
        crop_image_a, crop_image_r = crop_center(img)
        if not use_combined_images:
            misc.imsave(file_path, crop_image_a)
            misc.imsave(root_dir + "/" + dir_name + "/revers_" + base_name, crop_image_r)
        return "avers_" + base_name, "revers_" + base_name, crop_image_a, crop_image_r


def identity(root_dir, dir_name, base_name, img):
    file_path = root_dir + "/" + dir_name + "/" + base_name
    misc.imsave(file_path, img)
    return base_name, img;


def crop_center(img):
    (y, x, z) = img.shape

    return img[0: y, 0:int(x / 2), :], img[0:y, int(x / 2):x, :]


def join_image(img1, img2):
    ha, wa = img1.shape[:2]
    hb, wb = img2.shape[:2]
    max_height = np.max([ha, hb])
    total_width = wa + wb
    new_img = np.zeros(shape=(max_height, total_width, 3))
    new_img[:ha, :wa] = img1
    new_img[:hb, wa:wa + wb] = img2
    return new_img

def processImage(file_name):
    base_name = os.path.basename(file_name)
    sub_dir = os.path.dirname(file_name)
    image = ndimage.imread(sub_dir + "/" + base_name)
    if len(image.shape) != 3:
        return

    if generated_prefix not in base_name:
        apply_gaussianBlur(sub_dir, generated_prefix + base_name, image)
        apply_affine(sub_dir, generated_prefix + base_name, image, 0.9, True)
    
    
def walk_dir(image_dir):
    if not gfile.Exists(image_dir):
        tf.logging.error("Image directory '" + image_dir + "' not found.")
        return

    with concurrent.futures.ProcessPoolExecutor() as executor:

        result = collections.OrderedDict()
        sub_dirs = [
            os.path.join(image_dir, item)
            for item in gfile.ListDirectory(image_dir)]
        sub_dirs = sorted(item for item in sub_dirs if gfile.IsDirectory(item))
        extensions = ['jpg', 'jpeg', 'JPG', 'JPEG']
        for sub_dir in sub_dirs:
            file_list = []
            dir_name = os.path.basename(sub_dir)
            tf.logging.info('processing {}  {}'.format(sub_dir, dir_name))
            if dir_name == image_dir:
                continue
            tf.logging.info("Looking for images in '" + dir_name + "'")
    
            for extension in extensions:
                file_glob = os.path.join(image_dir, dir_name, '*.' + extension)
                file_list.extend(gfile.Glob(file_glob))
            if not file_list:
                tf.logging.warning('No files found')
                continue
            executor.map(processImage, file_list)

    return


def copy_base_images(target_dir, dir_name, file_list):
    for file_name in file_list:
        base_name = os.path.basename(file_name)
        shutil.copyfile(file_name, os.path.join(target_dir, dir_name, base_name))

def main():
    tf.logging.set_verbosity(tf.logging.WARN)
    
    walk_dir(FLAGS.dataset_train_dir)
    
    walk_dir(FLAGS.dataset_test_dir)


if __name__ == "__main__": main()
