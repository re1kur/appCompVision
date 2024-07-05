package org.example;

import java.io.ByteArrayInputStream;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
/**
 * A service class for capturing frames from a webcam using OpenCV and displaying them in JavaFX.
 */
public class CameraService {
    private final VideoCapture capture;
    private final Mat frame;
    private ImageView imageView;
    private Timeline timeline;
    private boolean isCapturing;
    /**
     * Constructs a new CameraService.
     */
    public CameraService() {
        capture = new VideoCapture();
        frame = new Mat();
    }
    /**
     * Starts capturing frames from the webcam and displaying them in the specified ImageView.
     * Initializes a timeline to continuously read frames at a fixed interval.
     */
    public void startCamera() {
        this.imageView = Handler.getImageView();

        capture.open(0);
        if (!capture.isOpened()) {
            System.err.println("Не удалось открыть веб-камеру.");
            return;
        }

        isCapturing = true;

        timeline = new Timeline(new KeyFrame(Duration.millis(100), _ -> {
            if (isCapturing) {
                capture.read(frame);
                if (!frame.empty()) {
                    Image imageToShow = mat2Image(frame);
                    updateImageView(imageView, imageToShow);
                }
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    /**
     * Captures the current frame, saves it as a JPEG file, resizes it, and displays the processed image.
     */
    public void captureAndProcessFrame() {
        if (frame.empty()) {
            System.err.println("Не удалось захватить кадр с веб-камеры.");
            return;
        }

        String path = "src/main/resources/chosen_images/frame.jpg";

        // Save the captured frame as a JPEG file
        if (!Imgcodecs.imwrite(path, frame)) {
            System.err.println("Не удалось сохранить изображение: " + path);
            return;
        }

        // Resize the image and save the processed image
        resizeImage(path, path);

        // Load and display the processed image
        Image image = new Image("file:" + path);
        imageView.setImage(image);
        Handler.setCurrentImageMat(Imgcodecs.imread(path));
    }
    /**
     * Resizes an image located at 'path' to the specified 'outpath'.
     *
     * @param path    Path to the original image.
     * @param outpath Path to save the resized image.
     */
    private void resizeImage(String path, String outpath) {
        Size size = new Size(640, 480);
        Mat src = Imgcodecs.imread(path);
        if (src.empty()) {
            System.err.println("Не удалось загрузить изображение: " + path);
            return;
        }
        Mat out = new Mat();
        Imgproc.resize(src, out, size);
        if (!Imgcodecs.imwrite(outpath, out)) {
            System.err.println(
                    "Не удалось сохранить обработанное изображение: " + outpath);
        }
    }
    /**
     * Updates the specified ImageView with the given Image.
     *
     * @param view  ImageView to update.
     * @param image Image to display in the ImageView.
     */
    private void updateImageView(ImageView view, Image image) {
        view.setImage(image);
    }
    /**
     * Converts a OpenCV Mat object to a JavaFX Image object.
     *
     * @param frame OpenCV Mat object representing an image frame.
     * @return JavaFX Image object converted from the Mat frame.
     */
    private Image mat2Image(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", frame, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
    /**
     * Stops capturing frames from the webcam and releases associated resources.
     */
    public void stopCamera() {
        isCapturing = false;
        if (timeline != null) {
            timeline.stop();
        }
        if (capture.isOpened()) {
            capture.release();
        }
        frame.release();
    }
}
