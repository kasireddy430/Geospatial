package com.cs503.assignment;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class SmearDetection {

    public static void main(String[] args) {


        /**
         *  Assigned path for the folder to be accessed
         */
        String smearDirectory = "C:/Users/Nikhil/Downloads/sample_drive/sample_drive/cam_4/";
        //String smearDirectory = System.getProperty("user.dir").replace("\\", "/")+"/src/main/java/com/cs503/assignment/InputImages/";
        System.out.println(smearDirectory);
        File directory = new File(smearDirectory);

        // Used to list all the files in File[]
        File[] files_array = directory.listFiles();
        ArrayList filelist = new ArrayList();
        int length = files_array.length;

        // using the jpg images shared by professor
        for (int j = 0; j < length; j++) {
            if (files_array[j].isFile()) {
                if (files_array[j].getName().endsWith(".jpg")) {
                    filelist.add(files_array[j].getName());
                }
            } else if (files_array[j].isDirectory()) {
            }
        }
        System.out.println("file list size : "+filelist.size());
        SmearDetection.imageAveraging(filelist.size(), filelist, smearDirectory);
    }

    public static void imageAveraging(int count, ArrayList fileList, String smearDetectionDir) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(smearDetectionDir + fileList.get(0)));
            int height = bufferedImage.getHeight();
            int width = bufferedImage.getWidth();
            int imagePixel = height * width;

            /***
             * Creating an RGB group array to store pixels of images
             */

            int blue[] = new int[imagePixel];
            int green[] = new int[imagePixel];
            int red[] = new int[imagePixel];

            BufferedImage image;
            for (int i = 0; i < count; i++) {
                image = ImageIO.read(new File(smearDetectionDir + fileList.get(i)));

                int pxl_number = 0;
                for (int j = 0; j < height; j++) {
                    for (int k = 0; k < width; k++) {
                        Color c = new Color(image.getRGB(k, j));
                        red[pxl_number] = red[pxl_number] + c.getRed();
                        blue[pxl_number] = blue[pxl_number] + c.getBlue();
                        green[pxl_number] = green[pxl_number] + c.getGreen();
                        if (i == count - 1) {
                            red[pxl_number] /= count;
                            green[pxl_number] /= count;
                            blue[pxl_number] /= count;
                            Color newColor = new Color(red[pxl_number], green[pxl_number], blue[pxl_number]);
                            bufferedImage.setRGB(k, j, newColor.getRGB());
                        }
                        pxl_number++;
                    }
                }
            }

            /**
             * Image path for averaging image picture
             *
             */
            File averageImageOutput = new File(smearDetectionDir + "averageimage.jpg");
            ImageIO.write(bufferedImage, "jpg", averageImageOutput);
            SmearDetection.SmearDetection(smearDetectionDir);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public static void SmearDetection(String smearDirectory) {
        try {

            /***
             * Initially we load the OpenCV library for SmearDetection
             */
//            System.out.println("library path"+System.getProperty("java.library.path"));
//            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            OpenCV.loadLocally();
            String image_temproray = smearDirectory + "averageimage.jpg";

            /***
             * Create a grayscale and read color of the image from directory with the use of
             * core OpenCV library
             */
            Mat image_Org_Color = Imgcodecs.imread(image_temproray, Imgcodecs.IMREAD_COLOR);
            Mat image_GreyScale = Imgcodecs.imread(image_temproray, Imgcodecs.IMREAD_GRAYSCALE);
            Mat final_Image = new Mat(image_GreyScale.rows(), image_GreyScale.cols(), image_GreyScale.type());

            Imgproc.adaptiveThreshold(image_GreyScale, final_Image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                    Imgproc.THRESH_BINARY, 275, 10);
            Imgproc.blur(final_Image, final_Image, new Size(3, 3));
            Imgproc.Canny(final_Image, image_GreyScale, 155.0, 200.0);
            String image_Temprory_new = smearDirectory + "Intermediate.jpg";
            /**
             *
             * Rewriting the image with adaptiveThreshold and saving it as intermediate.jpg
             */

            Imgcodecs.imwrite(image_Temprory_new, final_Image);

            BufferedImage bufferedImage = ImageIO.read(new File(image_Temprory_new));
            int bufferedImage_height = bufferedImage.getHeight();
            int buffered_width = bufferedImage.getWidth();

            int horizontal_minimum = buffered_width + 1;
            int horizontal_maximum = 0;
            int vertical_minimun = bufferedImage_height + 1;
            int vertical_maximum = 0;

            /***
             * horizontal_minimum is the X-minimum horizontal_maximum is the X-maximum
             * vertical_minimun is the Y-minimum vertical_minimum is the Y-maximum
             */
            for (int j = 0; j < bufferedImage_height; j++) { // Getting the coordinates of smear need to create
                // rectangle
                for (int k = 0; k < buffered_width; k++) {
                    Color c = new Color(bufferedImage.getRGB(k, j));
                    if (c.getRed() + c.getBlue() + c.getGreen() == 0) {
                        if (k > horizontal_maximum) {
                            horizontal_maximum = k;
                        }
                        if (j > vertical_maximum) {
                            vertical_maximum = j;

                        }
                        if (k < horizontal_minimum) {
                            horizontal_minimum = k;
                        }
                        if (j < vertical_minimun) {
                            vertical_minimun = j;
                        }
                    }
                }
            }
            Imgproc.rectangle(image_Org_Color, new Point(horizontal_minimum, vertical_minimun),
                    new Point(horizontal_maximum, vertical_maximum), new Scalar(0, 0, 255), 3);

            /**
             * The finalImage is generated by this and saved to the same directory with
             * FinalImage.jpg
             */
            smearDirectory = smearDirectory + "FinalImage.jpg";
            Imgcodecs.imwrite(smearDirectory, image_Org_Color);
            /**
             * Successfully execution of the file
             *
             */
            print("Average Image , Intermediate image and Final Images files have been created successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void print(String string) {
        System.out.println(string);

    }
}
