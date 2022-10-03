package controller;

import algorithm.TrackingService;
import algorithm.VisualizationManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import shapes.ConeMesh;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class VisualizationController implements Controller {

    @FXML
    Group meshGroup;
    @FXML
    Button loadStlFile;
    @FXML
    Button start;
    @FXML
    ToggleButton cullBack;
    @FXML
    ToggleButton wireframe;
    @FXML
    ColorPicker colorPicker;
    @FXML
    Slider trackerSlider;
    @FXML
    ScrollPane scrollPane;

    TrackingDataController trackingDataController;
    VisualizationManager visualizationManager;

    TrackingService trackingService = TrackingService.getInstance();

    private Label statusLabel;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final BooleanProperty sourceConnected = new SimpleBooleanProperty(false);


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
    }

    public void injectStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
        this.statusLabel.setText("");
    }

    public void injectTrackingDataController(TrackingDataController trackingDataController) {
        this.trackingDataController = trackingDataController;
    }

    public void injectSceneBuilder(VisualizationManager visualizationManager) {
        this.visualizationManager = visualizationManager;
    }

    @FXML
    private void loadSTLFile() {
        if (trackingService.getTrackingDataSource() == null) {
            statusLabel.setText("Select Tracking Data Source first");
            return;
        }
        visualizationManager.setPane(this.scrollPane);
        visualizationManager.setMeshGroup(this.meshGroup);
        visualizationManager.loadStlModel();
        visualizationManager.showFigure();
    }

    @FXML
    private void startTracking() {
        trackingDataController.visualizeTracking();
    }

    /**
     * Set a color for the tracker in the visualisation view
     */
    @FXML
    public void setTrackerColor() {
        if (visualizationManager.getTrackingCones() == null) {
            statusLabel.setText("No Tracking Data Source");
            return;
        }
        statusLabel.setText("");

        ConeMesh[] trackingCones = visualizationManager.getTrackingCones();
        for (ConeMesh trackingCone : trackingCones) {
            trackingCone.setMaterial(new PhongMaterial(colorPicker.getValue()));
        }
    }

    /**
     * Set a size for the tracker in the visualisation view
     */
    @FXML
    public void setTrackerSize() {
        if (visualizationManager.getTrackingCones() == null) {
            statusLabel.setText("No Tracking Data Source");
            return;
        }
        statusLabel.setText("");

        ConeMesh[] trackingCones = visualizationManager.getTrackingCones();
        for (ConeMesh trackingCone : trackingCones
        )
            trackingCone.setHeight(trackerSlider.getValue());
    }

    /**
     * Pauses the visualization
     */
    @FXML
    private void pauseVisualization() {
        trackingDataController.freezeVisualization();
    }

    /**
     * Set CullFace to none for every MeshView in the scene
     */
    @FXML
    private void toggleCullFace() {
        if (visualizationManager.getMeshView() == null) {
            statusLabel.setText("No 3D-Model selected");
            return;
        }
        statusLabel.setText("");

        MeshView[] meshView = visualizationManager.getMeshView();

        if (cullBack.isSelected()) {
            for (MeshView mesh : meshView) {
                mesh.setCullFace(CullFace.NONE);
            }
        } else {
            for (MeshView mesh : meshView) {
                mesh.setCullFace(CullFace.BACK);
            }
        }
    }

    /**
     * Set DrawMode to Line for every MeshView in the scene
     */
    @FXML
    private void toggleWireframe() {
        if (visualizationManager.getMeshView() == null) {
            statusLabel.setText("No 3D-Model selected");
            return;
        }
        statusLabel.setText("");

        MeshView[] meshView = visualizationManager.getMeshView();

        if (wireframe.isSelected()) {
            for (MeshView mesh : meshView) {
                mesh.setDrawMode(DrawMode.LINE);
            }
        } else {
            for (MeshView mesh : meshView) {
                mesh.setDrawMode(DrawMode.FILL);
            }
        }
    }

    @Override
    public void close() {
        statusLabel.setText("");
        unregisterController();
    }
}
