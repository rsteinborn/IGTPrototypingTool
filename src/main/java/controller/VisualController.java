package controller;

import algorithm.Measurement;
import algorithm.ToolMeasure;
import algorithm.TrackingService;
import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import shapes.simple.ConeMesh;
import userinterface.TrackingDataDisplay;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.scene.input.KeyCode.W;

public class VisualController implements Controller {

    private static final double MODEL_SCALE_FACTOR = 10;
    private static final double MODEL_X_OFFSET = 0; // standard
    private static final double MODEL_Y_OFFSET = 0; // standard
    private static final int VIEWPORT_SIZE = 800;
    private static final int VIEWPORT_CENTER = VIEWPORT_SIZE / 2;
    private double mouseOldX, mouseOldY = 0;
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);

    @FXML
    Group meshGroup;
    @FXML
    Button loadStlFile;
    @FXML
    Button loadCsvFile;
    @FXML
    Button loadOpenIgtLink;
    @FXML
    Button start;
    @FXML
    Button freeze;
    @FXML
    CheckBox cullBack;
    @FXML
    CheckBox wireframe;
    @FXML
    HBox posBox;
    @FXML
    HBox rotBox;
    @FXML
    ColorPicker colorPicker;
    @FXML
    Slider trackerSlider;

    private PointLight pointLight;
    private MeshView[] meshView;

    private MeshView[] trackingDevices;

    private final TrackingService trackingService = TrackingService.getInstance();
    List<TrackingDataDisplay> toolDisplayList;
    HashMap<String, Label> position;
    HashMap<String, Label> rotation;
    Label statusLabel;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final BooleanProperty sourceConnected = new SimpleBooleanProperty(false);
    private final BooleanProperty meshViewEmpty = new SimpleBooleanProperty(true);

    private PerspectiveCamera perspectiveCamera = new PerspectiveCamera(false);

    private Sphere sphere = new Sphere(2);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        toolDisplayList = new ArrayList<>();
        start.disableProperty().bind(meshViewEmpty);
    }

    /**
     * Set a color for the tracker in the visualisation view
     */
    @FXML
    public void setTrackerColor() {
        sphere.setMaterial(new PhongMaterial(colorPicker.getValue()));
    }

    /**
     * Set a size for the tracker in the visualisation view
     */
    @FXML
    public void setTrackerSize() {
        sphere.setRadius(trackerSlider.getValue());
    }

    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
        this.statusLabel.setText("");
    }

    /**
     * This method opens a FileChooser to select multiple STL files
     * which are then visualized.
     *
     * @return -- MeshView Array with STL Files
     */
    private MeshView[] loadMeshViews() {

        StlMeshImporter importer = new StlMeshImporter();
        FileChooser fc = new FileChooser();
        fc.setTitle("Load STL File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("STL Files", "*.stl"));
        List<File> fileList = fc.showOpenMultipleDialog(new Stage());

        if (fileList != null) {
            meshView = new MeshView[fileList.size()];
            int x = 0;

            for (File file : fileList) {
                try {
                    importer.read(file);
                    Mesh mesh = importer.getImport();
                    meshView[x] = new MeshView(mesh);

                    x++;

                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error reading STL file");
                }
            }
        }

        meshViewEmpty.set(false);

        return meshView;
    }

    @FXML
    private void showTrackingData() {
        if (trackingService.getTrackingDataSource() == null) {
            statusLabel.setText("No Tracking Data Source");
            return;
        }
        // timeline has not been started in trackingdata view
        if (trackingService.getTimeline() == null) {
            statusLabel.setText("Start Tracking in Tracking Data View first");
            return;
        }
        statusLabel.setText("");
        trackingService.getTimeline().getKeyFrames().add(
                new KeyFrame(Duration.millis(100),
                        event2 -> updateTrackingDevice())
        );
    }

    /**
     * Visualize tracking data from source. Checks if there are tools
     * available and creates data series for each tool. Visualization
     * uses last 5 measurements for blending.
     */
    private void updateTrackingDevice() {

        if (trackingService.getTrackingDataSource() == null) {
            return;
        }
        // loads the next set of tracking data
        trackingService.getTrackingDataSource().update();

        List<ToolMeasure> tools = trackingService.getDataService().loadNextData(1);
        if (tools.isEmpty()) return;

        for (ToolMeasure tool : tools) {

//            TrackingDataDisplay display = checkToolDisplayList(tool.getName());
            // clear old data
//            display.clearData();

            List<Measurement> measurements = tool.getMeasurement();
            //use the last 5 measurements, otherwise blending will be a problem during motion
            for (int i = 1; i < 5; i++) {
                if (measurements.size() - i < 0) {
                    break;
                }
                double x = measurements.get(measurements.size() - i).getPoint().getX();
                // invert Y axis for correct display!
                double y = measurements.get(measurements.size() - i).getPoint().getY() * -1;
                // invert tracking data, so fits the experiment's setup
                double z = measurements.get(measurements.size() - i).getPoint().getZ() * -1;

                sphere.setTranslateX(x);
                sphere.setTranslateY(y);
                sphere.setTranslateZ(z);

//                if (i == 1) {
//                    position.get(tool.getName()).setText(": ["
//                            + cone.getTranslateX() + ";"
//                            + cone.getTranslateY() + ";"
//                            + cone.getTranslateZ() + "]");
//                }

                //TODO: add rotation
            }
        }
    }


    private Group buildScene() {
        Group root = new Group();

        MeshView[] meshViews = loadMeshViews();
        for (MeshView view : meshViews) {
            // centre every mesh
//            view.setTranslateX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
//            view.setTranslateY(VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
//            view.setTranslateZ(VIEWPORT_SIZE / 2);
//            view.setScaleX(MODEL_SCALE_FACTOR);
//            view.setScaleY(MODEL_SCALE_FACTOR);
//            view.setScaleZ(MODEL_SCALE_FACTOR);

//            sample.setSpecularColor(lightColor);
//            sample.setSpecularPower(16);
//            meshViews[i].setMaterial(sample);
//            meshViews[i].getTransforms().setAll(new Rotate(38, Rotate.Z_AXIS), new Rotate(20, Rotate.X_AXIS));
//            view.getTransforms().add(transform);
        }

//        Color ambientColor = Color.rgb(80, 80, 80, 0);
//        AmbientLight ambient = new AmbientLight(ambientColor);

        root.setRotationAxis(Rotate.Y_AXIS);
        root.setRotate(0);

        rotateX.setPivotX(VIEWPORT_CENTER + MODEL_X_OFFSET);
        rotateX.setPivotY(VIEWPORT_CENTER + MODEL_Y_OFFSET);
        rotateX.setPivotZ(VIEWPORT_CENTER);

        rotateY.setPivotX(VIEWPORT_CENTER + MODEL_X_OFFSET);
        rotateY.setPivotY(VIEWPORT_CENTER + MODEL_Y_OFFSET);
        rotateY.setPivotZ(VIEWPORT_CENTER);

        rotateZ.setPivotX(VIEWPORT_CENTER + MODEL_X_OFFSET);
        rotateZ.setPivotY(VIEWPORT_CENTER + MODEL_Y_OFFSET);
        rotateZ.setPivotZ(VIEWPORT_CENTER);

        root.getChildren().addAll(meshViews);
        root.getChildren().addAll(sphere);

        root.setTranslateX(VIEWPORT_CENTER + MODEL_X_OFFSET);
        root.setTranslateY(VIEWPORT_CENTER + MODEL_Y_OFFSET);
        root.setTranslateZ(VIEWPORT_CENTER);
        root.setScaleX(MODEL_SCALE_FACTOR);
        root.setScaleY(MODEL_SCALE_FACTOR);
        root.setScaleZ(MODEL_SCALE_FACTOR);

        return root;
    }

    private PerspectiveCamera initCamera() {
//        PerspectiveCamera perspectiveCamera = new PerspectiveCamera(false);
        perspectiveCamera.setTranslateX(0);
        perspectiveCamera.setTranslateY(0);
        perspectiveCamera.setTranslateZ(0);
        perspectiveCamera.setNearClip(0.1);
        perspectiveCamera.setFarClip(1000.0);

        perspectiveCamera.getTransforms().addAll(rotateX, rotateY, new Translate(0, 0, -1000));
        return perspectiveCamera;
    }

    private SubScene createScene3D(Group meshGroup) {
        SubScene scene3d = new SubScene(meshGroup, 850, 800, true, SceneAntialiasing.BALANCED);
        scene3d.widthProperty().bind(((AnchorPane) this.meshGroup.getParent()).widthProperty());
        scene3d.heightProperty().bind(((AnchorPane) this.meshGroup.getParent()).heightProperty());
//        PerspectiveCamera perspectiveCamera = initCamera();
        initCamera();

        scene3d.setFill(Color.DARKGREY);
        scene3d.setCamera(perspectiveCamera);
        scene3d.setPickOnBounds(true);
        return scene3d;
    }


    @FXML
    private void showFigure() {
        // Add MeshView to Group
        Group root = buildScene();
//        // Create Camera
//        PerspectiveCamera perspectiveCamera = initCamera();
        // Create subScene
        SubScene subScene = createScene3D(root);

//        subScene.requestFocus();

        subScene.requestFocus();
//        // Add Camera to Scene
//        subScene.setCamera(perspectiveCamera);
        // Add subScene to meshGroup
        this.meshGroup.getChildren().add(subScene);
        // size will not impact the size of parent and resizing will also work when reducing the AnchorPane object's size
        subScene.setManaged(false);

        handleKeyboard(subScene);
        handleMouse(subScene, root);
    }

    /**
     * Add Mouse functionalities to SubScene
     * allow user to zoom in and out
     * allow user to drag
     * @param subScene
     * @param root
     */
    private void handleMouse(SubScene subScene, Group root) {
        subScene.setOnScroll(event -> {
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();
            if (deltaY < 0) {
                zoomFactor = 2.0 - zoomFactor;
            }
            root.setScaleX(root.getScaleX() * zoomFactor);
            root.setScaleY(root.getScaleY() * zoomFactor);
            root.setScaleZ(root.getScaleZ() * zoomFactor);
            event.consume();
        });
        subScene.setOnMousePressed(event -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        subScene.setOnMouseDragged(event -> {
            rotateX.setAngle(rotateX.getAngle() - (event.getSceneY() - mouseOldY));
            rotateY.setAngle(rotateY.getAngle() + (event.getSceneX() - mouseOldX));
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();

        });
    }

    //TODO: request focus for subScene - input not working
    /**
     * Add ArrowKey functionalities to SubScene
     * allow user to navigate camera via KeyPress
     * @param subScene
     */
    private void handleKeyboard(SubScene subScene) {
        subScene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case W:
                    perspectiveCamera.translateYProperty().set(perspectiveCamera.getTranslateY() - 100);
                    keyEvent.consume();
                    break;
                case S:
                    perspectiveCamera.translateYProperty().set(perspectiveCamera.getTranslateY() + 100);
                    keyEvent.consume();
                    break;
                case A:
                    perspectiveCamera.translateXProperty().set(perspectiveCamera.getTranslateX() + 100);
                    keyEvent.consume();
                    break;
                case D:
                    perspectiveCamera.translateXProperty().set(perspectiveCamera.getTranslateX() - 100);
                    keyEvent.consume();
                    break;
            }
        });
    }

    /**
     * Set DrawMode to Line or CullFace to none for every MeshView in the scene
     */
    @FXML
    private void changeView(Event e) {
        if (e.getSource().equals(cullBack)) {
            for (MeshView mesh : meshView) {
                mesh.cullFaceProperty().bind(
                        Bindings.when(
                                        cullBack.selectedProperty())
                                .then(CullFace.NONE)
                                .otherwise(CullFace.BACK)
                );
            }
        } else if (e.getSource().equals(wireframe)) {
            for (MeshView mesh : meshView) {
                mesh.drawModeProperty().bind(
                        Bindings.when(
                                        wireframe.selectedProperty())
                                .then(DrawMode.LINE)
                                .otherwise(DrawMode.FILL)
                );
            }
        }
    }

    private RotateTransition rotate3dGroup(Group group) {
        RotateTransition rotate = new RotateTransition(Duration.seconds(10), group);
        rotate.setAxis(Rotate.X_AXIS);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.setCycleCount(RotateTransition.INDEFINITE);

        return rotate;
    }

    @Override
    public void close() {
        statusLabel.setText("");
        unregisterController();
    }
}
