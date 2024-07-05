package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import org.example.CameraService;
import org.example.Handler;
/**
 * Controller class for handling interactions with the application UI defined in the FXML file.
 */
public class appController {
    private CameraService cameraService;

    @FXML private Button loadImgBtn;

    @FXML private Button takeImgBtn;

    @FXML private AnchorPane mainPane;

    @FXML private AnchorPane imagePane;

    @FXML private AnchorPane btnVbox;

    @FXML private ImageView imageView;

    @FXML private Button takeFrameBtn;

    @FXML private CheckBox redChannel;

    @FXML private Button rotateImageBtn;

    @FXML private Button applyChannelBtn;

    @FXML private Button cropImageBtn;

    @FXML private Button drawTheQuadrangleBtn;

    @FXML private CheckBox greenChannel;

    @FXML private CheckBox blueChannel;

    @FXML private Button saveImageBtn;
    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up event handlers and initial UI configurations.
     */
    @FXML
    void initialize() {
        Handler.setImageView(imageView);
        cameraService = new CameraService();
        mainPane.setBackground(Background.fill(Color.rgb(134, 136, 138)));
        imagePane.setBackground(Background.fill(Color.rgb(201, 218, 238)));
        btnVbox.setBackground(Background.fill(Color.rgb(49, 51, 53)));
        loadImgBtn.setOnAction(_ -> Handler.loadImg());
        takeImgBtn.setOnAction(_ -> {
            cameraService.startCamera();
            takeFrameBtn.setVisible(true);
            takeFrameBtn.setOnAction(_ -> {
                cameraService.captureAndProcessFrame();
                takeFrameBtn.setVisible(false);
                cameraService.stopCamera();
            });
        });
        applyChannelBtn.setOnAction(_ -> {
            System.out.println("Кнопка применения канала нажата.");
            Handler.applyChannelFilter(redChannel, greenChannel, blueChannel);
        });
        cropImageBtn.setOnAction(_ -> {
            System.out.println("Кнопка обрезания изображения нажата.");
            Handler.cropImage();
        });
        rotateImageBtn.setOnAction(_ -> {
            System.out.println("Кнопка вращения изображения нажата.");
            Handler.rotateImage();
        });
        drawTheQuadrangleBtn.setOnAction(_ -> {
            System.out.println(
                    "Кнопка накладывания четырехугольника на изображение нажата.");
            Handler.drawRectangle();
        });
        saveImageBtn.setOnAction(_ -> {
            System.out.println("Кнопка сохранения изображения в ImageView нажата.");
            Handler.saveImage();
        });
    }
}
