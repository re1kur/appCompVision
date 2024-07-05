package org.example;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.FileUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
/**
 * A handler class for converting PNG images to JPG format and changing file extensions.
 * This class uses JavaFX to create a file chooser dialog for selecting PNG files and
 * Java's ImageIO library to perform the image conversion.
 */
public class Handler {
    private static Mat currentImageMat;
    private static ImageView imageView;
    private static Stage primaryStage;
    private static File loadedImage;
    /**
     * Setter of the prim. stage.
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }
    /**
     * Getter of the current image.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    /**
     * Setter for current image.
     * @param img current image.
     */
    private static void setImage(File img) {
        loadedImage = img;
    }
    /**
     * Methode for loading the needed image.
     */
    public static void loadImg() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select the needed image");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.jpg"));
        File selectedFile = fc.showOpenDialog(getPrimaryStage());
        if (selectedFile != null) {
            String src = selectedFile.getAbsolutePath(); // Получение абсолютного пути
            // без двойных слэшей
            String path =
                    "src\\main\\resources\\chosen_images\\" + selectedFile.getName();
            if (!new File(src).exists()) {
                System.err.println("Файл не существует: " + src);
                return;
            } else {
                System.out.println("Файл существует: " + src);
            }
            try {
                System.out.println(isPngFile(selectedFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                path = moveImage(selectedFile, path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(path + "                jopa");
            resizeImage(path);
            if (!new File(path).exists()) {
                System.err.println(
                        "Изображение(640x480) не сохранилось (Файл не существует): "
                                + path);
                return;
            } else {
                System.out.println("Изображение(640x480) сохранилось: " + path);
            }

            Image img = new Image("file:" + path);
            imageView.setImage(img);
            setImage(new File(path));
            setCurrentImageMat(Imgcodecs.imread(loadedImage.getAbsolutePath()));
        }
    }
    /**
     * Moves the needed image to dir in the project.
     * @param src the needed file
     * @param path is the path where is will be this file
     */
    private static String moveImage(File src, String path) throws IOException {
        if (isPngFile(src)) {
            try {
                path = changePngToJpg(path);
                convertPngToJpg(src);
            } catch (ImageReadException | ImageWriteException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        File image = new File(path);
        try {
            FileUtils.copyFile(src, image);
            System.out.println(path);
            System.out.println(
                    "Выбранное изображение перемещено в папку проекта: " + path);
        } catch (IOException e) {
            System.err.println(
                    "Ошибка при перемещении изображения: " + e.getMessage());
        } catch (IllegalArgumentException _) {
        }
        return path;
    }
    /**
     * Methode for resize of images for imageview.
     * @param path path of the file to resize
     */
    private static void resizeImage(String path) {
        Size size = new Size(640, 480);
        Mat src = Imgcodecs.imread(path);
        if (src.empty()) {
            System.err.println("Не удалось прочитать изображение: " + path);
            return;
        } else {
            System.out.println("Изображение удалось прочитать: " + path);
        }
        Mat out = new Mat(size, src.type());
        Imgproc.resize(src, out, size);
        Imgcodecs.imwrite(path, out);
    }
    /**
     * Setter of the imageView
     * @param imageView imageview in the primaryStage
     */
    public static void setImageView(ImageView imageView) {
        Handler.imageView = imageView;
    }
    /**
     * Getter of imageView
     */
    public static ImageView getImageView() {
        return imageView;
    }
    /**
     * Setter of CurrentImageMat
     * @param currentImageMat current mat
     */
    public static void setCurrentImageMat(Mat currentImageMat) {
        Handler.currentImageMat = currentImageMat;
    }
    /**
     * Methode for button "APPLY CHANNEL".
     */
    public static void applyChannelFilter(
            CheckBox redChannel, CheckBox greenChannel, CheckBox blueChannel) {
        if (currentImageMat == null) {
            showAlert("ERROR!", "First load the image or take a picture.");
            return;
        }
        Mat channelImage = new Mat(currentImageMat.size(), currentImageMat.type());
        List<Mat> channels = new ArrayList<>(3);
        Core.split(currentImageMat, channels);
        Mat zeros = Mat.zeros(currentImageMat.size(), CvType.CV_8UC1);

        if (redChannel.isSelected()) {
            channels.set(2, zeros);
        }
        if (greenChannel.isSelected()) {
            channels.set(1, zeros);
        }
        if (blueChannel.isSelected()) {
            channels.set(0, zeros);
        }

        Core.merge(channels, channelImage);
        updateImageView(channelImage);
    }
    private static void updateImageView(Mat mat) {
        currentImageMat = mat.clone();
        Image image = mat2Image(mat);
        imageView.setImage(image);
    }
    /**
     * Converts the mat in the image.
     */
    private static Image mat2Image(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
    /**
     * Crops the image. User enter the coords and the size
     */
    public static void cropImage() {
        if (currentImageMat == null) {
            showAlert("ERROR!", "First load the image or take a picture.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog("x,y,width,height");
        dialog.setTitle("Crop Image");
        dialog.setHeaderText("Enter crop coordinates and size:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String[] values = result.get().split(",");
            if (values.length == 4) {
                try {
                    int x = Integer.parseInt(values[0]);
                    int y = Integer.parseInt(values[1]);
                    int width = Integer.parseInt(values[2]);
                    int height = Integer.parseInt(values[3]);
                    Rect roi = new Rect(x, y, width, height);
                    Mat cropped = new Mat(currentImageMat, roi);
                    updateImageView(cropped);
                } catch (NumberFormatException e) {
                    System.out.println("Неправильный ввод.");
                }
            }
        }
    }
    /**
     * Rotate the image. User enters the angle.
     */
    public static void rotateImage() {
        if (currentImageMat == null) {
            showAlert("ERROR!", "First load the image or take a picture.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog("angle");
        dialog.setTitle("Rotate Image");
        dialog.setHeaderText("Enter rotation angle:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                double angle = Double.parseDouble(result.get());
                Point center = new Point((double) currentImageMat.width() / 2,
                        (double) currentImageMat.height() / 2);
                Mat rotationMatrix = Imgproc.getRotationMatrix2D(center, angle, 1);
                Mat rotated = new Mat();
                Imgproc.warpAffine(
                        currentImageMat, rotated, rotationMatrix, currentImageMat.size());
                updateImageView(rotated);
            } catch (NumberFormatException e) {
                System.out.println("Неправильный ввод.");
            }
        }
    }
    /**
     * Draws the rectangle. User enter the coords and the size
     */
    public static void drawRectangle() {
        if (currentImageMat == null) {
            showAlert("ERROR!", "First load the image or take a picture.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog("x,y,width,height");
        dialog.setTitle("Draw Rectangle");
        dialog.setHeaderText("Enter rectangle coordinates and size:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String[] values = result.get().split(",");
            if (values.length == 4) {
                try {
                    int x = Integer.parseInt(values[0]);
                    int y = Integer.parseInt(values[1]);
                    int width = Integer.parseInt(values[2]);
                    int height = Integer.parseInt(values[3]);
                    Mat imageWithRectangle = currentImageMat.clone();
                    Imgproc.rectangle(imageWithRectangle, new Point(x, y),
                            new Point(x + width, y + height), new Scalar(255, 0, 0), 3);
                    updateImageView(imageWithRectangle);
                } catch (NumberFormatException e) {
                    System.out.println("Неправильный ввод.");
                }
            }
        }
    }
    /**
     * Saves the image.
     *
     */
    public static void saveImage() {
        // Получаем текущее изображение из ImageView
        Image image = imageView.getImage();
        if (image == null) {
            showAlert("ERROR!", "First load the image or take a picture.");
            return;
        }
        // Создаем и настраиваем FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить изображение");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images JPG", "*.jpg"));

        // Показываем диалог для сохранения файла
        File file = fileChooser.showSaveDialog(getPrimaryStage());
        if (file != null) {
            try {
                // Сохраняем изображение в выбранный файл
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                showAlert("SUCCESS!", "The image is  saved: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println(e.getMessage());
                showAlert("ERROR!", "The image could not be saved!");
            }
        }
    }/**
     * Shows alert.
     *
     * @param head the text in the head
     * @param text text in the bottom of the alert
     */
    public static void showAlert(String head, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(head);
        alert.setContentText(text);
        alert.showAndWait();
    }
    /**
     * Converts a PNG file to a JPG file.
     *
     * @param pngFile the PNG file to be converted
     * @throws IOException if an error occurs during reading or writing the file
     */
    private static void convertPngToJpg(File pngFile)
            throws IOException, ImageReadException, ImageWriteException {
        BufferedImage image = ImageIO.read(pngFile);
        String jpgPath = changePngToJpg(pngFile.getAbsolutePath());
        System.out.println(pngFile.getAbsolutePath());
        System.out.println(jpgPath + "      asdasd");
        // Create the output JPG file
        File jpgFile = new File(pngFile.getParent(), jpgPath);
        // Write the image as a JPG file
        ImageIO.write(image, "jpg", jpgFile);

        System.out.println(
                "File converted successfully to " + jpgFile.getAbsolutePath());
    }
    private static boolean isPngFile(File file) throws IOException {
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        var imageFormat = Imaging.guessFormat(file);
        return imageFormat == ImageFormats.PNG;
    }
    public static String changePngToJpg(String filePath) {
        return filePath.replaceFirst("[.][^.]+$", "") + ".jpg";
    }
}
