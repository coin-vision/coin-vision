package net.coinshome.coinvision.utils;

import org.tensorflow.*;
import org.tensorflow.types.UInt8;


public class TensorflowUtils {


    public static Tensor<Float> preprocessImage(byte[][] imageBytes) {
        float[][][][] bResult = new float[imageBytes.length][][][];

        for (int i = 0; i < imageBytes.length; i++) {
            // long start = System.currentTimeMillis();
            try (Graph g = new Graph()) {
                GraphBuilder b = new GraphBuilder(g);
                final float mean = 0f;
                final float scale = 255f;

                // Since the graph is being constructed once per execution here, we can use a constant for the input image.
                // If the graph were to be re-used for multiple input images, a placeholder would have been more appropriate.
                final Output<String> input = b.constant("input", imageBytes[i]);
                final Output<Float> output = b.div(
                        b.sub(
                                b.resizeBilinear(
                                        b.expandDims(
                                                b.cast(
                                                        b.decodeJpeg(
                                                                input
                                                                , 3),
                                                        Float.class),
                                                b.constant("make_batch", 0)
                                        ),
                                        b.constant("size", new int[]{ImageUtils.INCEPTION_HEIGHT, ImageUtils.INCEPTION_WIDTH}))
                                , b.constant("mean", mean)
                        ),
                        b.constant("scale", scale)
                );

                try (Session s = new Session(g)) {
                    Tensor<Float> tensor = s.runner().fetch(output.op().name()).run().get(0).expect(Float.class);
                    //create prediction buffer

                    float[][][][] prediction = new float[1][299][299][3];
                    tensor.copyTo(prediction);
                    bResult[i] = prediction[0];
                }
            }

            //System.out.println("Resize" + i + " -> "  + (System.currentTimeMillis() - start));
        }
        return Tensor.create(bResult, Float.class);
    }

    public static float[][] executeDNNGraph(byte[] graphDef, Tensor<Float> image) {
        try (Graph g = new Graph()) {
            g.importGraphDef(graphDef);
            long start = System.currentTimeMillis();
            String outputNode = "InceptionV3/Predictions/Reshape_1";
            try (Session s = new Session(g); Tensor<Float> result = s.runner().feed("input", image).fetch(outputNode).run().get(0).expect(Float.class)) {
                final long[] rshape = result.shape();
                int batchSize = (int) rshape[0];
                int nlabels = (int) rshape[1];
                float[][] prediction = new float[batchSize][nlabels];
                System.out.println("graph run time (" + outputNode + ") with batchSize " + batchSize + " time: " + (System.currentTimeMillis() - start) + " ms");

                result.copyTo(prediction);
                return prediction;
            }
        }
    }

    // In the fullness of time, equivalents of the methods of this class should be auto-generated from
    // the OpDefs linked into libtensorflow_jni.so. That would match what is done in other languages like Python, C++ and Go.
    static class GraphBuilder {
        private Graph g;

        GraphBuilder(Graph g) {
            this.g = g;
        }

        Output<Float> div(Output<Float> x, Output<Float> y) {
            return binaryOp("Div", x, y);
        }

        <T> Output<T> sub(Output<T> x, Output<T> y) {
            return binaryOp("Sub", x, y);
        }

        <T> Output<Float> resizeBilinear(Output<T> images, Output<Integer> size) {
            return binaryOp3("ResizeBilinear", images, size);
        }

        <T> Output<T> expandDims(Output<T> input, Output<Integer> dim) {
            return binaryOp3("ExpandDims", input, dim);
        }

        <T, U> Output<U> cast(Output<T> value, Class<U> type) {
            DataType dtype = DataType.fromClass(type);
            return g.opBuilder("Cast", "Cast").addInput(value).setAttr("DstT", dtype).build().<U>output(0);
        }

        Output<UInt8> decodeJpeg(Output<String> contents, long channels) {
            return g.opBuilder("DecodeJpeg", "DecodeJpeg").addInput(contents).setAttr("channels", channels).build().<UInt8>output(0);
        }

        <T> Output<T> constant(String name, Object value, Class<T> type) {
            try (Tensor<T> t = Tensor.<T>create(value, type)) {
                return g.opBuilder("Const", name).setAttr("dtype", DataType.fromClass(type)).setAttr("value", t).build().<T>output(0);
            }
        }

        Output<String> constant(String name, byte[] value) {
            return this.constant(name, value, String.class);
        }

        Output<Integer> constant(String name, int value) {
            return this.constant(name, value, Integer.class);
        }

        Output<Integer> constant(String name, int[] value) {
            return this.constant(name, value, Integer.class);
        }

        Output<Float> constant(String name, float value) {
            return this.constant(name, value, Float.class);
        }

        private <T> Output<T> binaryOp(String type, Output<T> in1, Output<T> in2) {
            return g.opBuilder(type, type).addInput(in1).addInput(in2).build().<T>output(0);
        }

        private <T, U, V> Output<T> binaryOp3(String type, Output<U> in1, Output<V> in2) {
            return g.opBuilder(type, type).addInput(in1).addInput(in2).build().<T>output(0);
        }
    }
}