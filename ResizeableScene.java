import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.HashMap;

public class ResizeableScene {
    private final HashMap<Cursor, EventHandler<MouseEvent>> resizeHandlers = new HashMap<>();
    private final Stage stage;
    private final Scene scene;
    private int resizableArea;
    private final double SCREEN_WIDTH, SCREEN_HEIGHT;

    // Mouse position attributes
    private double mouseStartX, mouseStartY;
    private double mouseStartScreenX, mouseStartScreenY;
    private double initialStageWidth, initialStageHeight;
    private double xOffset, yOffset;

    // State flags
    private boolean resizeable = true;
    private boolean verticalResizeable = true;
    private boolean draggable;

    private static class ResizeableTriggers {
        private final boolean left;
        private final boolean right;
        private final boolean upper;
        private final boolean down;

        public ResizeableTriggers(boolean left, boolean right, boolean upper, boolean down) {
            this.left = left;
            this.right = right;
            this.upper = upper;
            this.down = down;
        }

        public boolean isLeft() {
            return left;
        }

        public boolean isRight() {
            return right;
        }

        public boolean isUpper() {
            return upper;
        }

        public boolean isDown() {
            return down;
        }
    }

    public ResizeableScene(Stage stage, boolean draggable, int resizableArea) {
        this.stage = stage;
        this.scene = stage.getScene();
        this.draggable = draggable;
        this.resizableArea = resizableArea;

        this.SCREEN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
        this.SCREEN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();

        initializeResizeHandlers();
        setupEventHandlers();
    }

    private void initializeResizeHandlers() {
        resizeHandlers.put(Cursor.NW_RESIZE, event -> {
            if (!verticalResizeable) return;
            double newWidth = initialStageWidth - (event.getScreenX() - mouseStartScreenX);
            double newHeight = initialStageHeight - (event.getScreenY() - mouseStartScreenY);
            resizeNorthwest(newWidth, newHeight, event);
        });

        resizeHandlers.put(Cursor.NE_RESIZE, event -> {
            if (!verticalResizeable) return;
            double newWidth = initialStageWidth - (event.getScreenX() - mouseStartScreenX);
            double newHeight = initialStageHeight + (event.getScreenY() - mouseStartScreenY);
            resizeNortheast(newWidth, newHeight, event);
        });

        resizeHandlers.put(Cursor.SW_RESIZE, event -> {
            if (!verticalResizeable) return;
            double newWidth = initialStageWidth + (event.getScreenX() - mouseStartScreenX);
            double newHeight = initialStageHeight - (event.getScreenY() - mouseStartScreenY);
            resizeSouthwest(newWidth, newHeight, event);
        });

        resizeHandlers.put(Cursor.SE_RESIZE, event -> {
            if (!verticalResizeable) return;
            double newWidth = initialStageWidth + (event.getScreenX() - mouseStartScreenX);
            double newHeight = initialStageHeight + (event.getScreenY() - mouseStartScreenY);
            resizeSoutheast(newWidth, newHeight);
        });

        resizeHandlers.put(Cursor.E_RESIZE, event -> {
            double newWidth = initialStageWidth - (event.getScreenX() - mouseStartScreenX);
            if (newWidth > stage.getMinWidth()) {
                stage.setX(event.getScreenX() - mouseStartX);
                stage.setWidth(newWidth);
            }
        });

        resizeHandlers.put(Cursor.W_RESIZE, event -> {
            double newWidth = initialStageWidth + (event.getScreenX() - mouseStartScreenX);
            if (newWidth > stage.getMinWidth()) {
                stage.setWidth(newWidth);
            }
        });

        resizeHandlers.put(Cursor.N_RESIZE, event -> {
            if (!verticalResizeable) return;
            double newHeight = initialStageHeight - (event.getScreenY() - mouseStartScreenY);
            if (newHeight > stage.getMinHeight()) {
                stage.setY(event.getScreenY() - mouseStartY);
                stage.setHeight(newHeight);
            }
        });

        resizeHandlers.put(Cursor.S_RESIZE, event -> {
            if (!verticalResizeable) return;
            double newHeight = initialStageHeight + (event.getScreenY() - mouseStartScreenY);
            if (newHeight > stage.getMinHeight()) {
                stage.setHeight(newHeight);
            }
        });

        resizeHandlers.put(Cursor.DEFAULT, event -> {
            stage.setX(event.getScreenX() - mouseStartX);
            stage.setY(event.getScreenY() - mouseStartY);
        });
    }

    private void setupEventHandlers() {
        scene.setOnMousePressed(this::handleMousePressed);
        scene.setOnMouseMoved(this::handleMouseMoved);

        scene.getRoot().addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            if (!isInResizeZone(event.getSceneX(), event.getSceneY())) {
                scene.setCursor(Cursor.DEFAULT);
                scene.setOnMouseDragged(null);
                setDraggable(true);
            }
        });
    }

    private void handleMousePressed(MouseEvent event) {
        mouseStartX = event.getSceneX();
        mouseStartY = event.getSceneY();
        mouseStartScreenX = event.getScreenX();
        mouseStartScreenY = event.getScreenY();
        initialStageWidth = stage.getWidth();
        initialStageHeight = stage.getHeight();
    }

    private void handleMouseMoved(MouseEvent event) {
        if (!resizeable) return;

        ResizeableTriggers triggers = calculateTriggers(event.getSceneX(), event.getSceneY());
        updateCursor(triggers.isLeft(), triggers.isRight(), triggers.isUpper(), triggers.isDown());
    }

    private ResizeableTriggers calculateTriggers(double sx, double sy) {
        boolean leftTrigger = sx > 0 && sx < resizableArea;
        boolean rightTrigger = sx < scene.getWidth() && sx > scene.getWidth() - resizableArea;
        boolean upperTrigger = sy < scene.getHeight() && sy > scene.getHeight() - resizableArea;
        boolean downTrigger = sy > 0 && sy < resizableArea;

        return new ResizeableTriggers(leftTrigger, rightTrigger, upperTrigger, downTrigger);
    }

    private boolean isInResizeZone(double sx, double sy) {
        if (!resizeable) return false;
        if (sx < 0 || sx > scene.getWidth() || sy < 0 || sy > scene.getHeight()) return false;

        ResizeableTriggers triggers = calculateTriggers(sx, sy);
        return (triggers.isLeft() || triggers.isRight() || (triggers.isUpper() || triggers.isDown()) && verticalResizeable);
    }

    private void updateCursor(boolean left, boolean right, boolean upper, boolean down) {
        if (left && down && verticalResizeable) fireAction(Cursor.NW_RESIZE);
        else if (left && upper && verticalResizeable) fireAction(Cursor.NE_RESIZE);
        else if (right && down && verticalResizeable) fireAction(Cursor.SW_RESIZE);
        else if (right && upper && verticalResizeable) fireAction(Cursor.SE_RESIZE);
        else if (left) fireAction(Cursor.E_RESIZE);
        else if (right) fireAction(Cursor.W_RESIZE);
        else if (down && verticalResizeable) fireAction(Cursor.N_RESIZE);
        else if (upper && verticalResizeable) fireAction(Cursor.S_RESIZE);
        else fireAction(Cursor.DEFAULT);
    }

    private void fireAction(Cursor cursor) {
        scene.setCursor(cursor);

        if (cursor != Cursor.DEFAULT) {
            setDraggable(false);
            scene.setOnMouseDragged(resizeHandlers.get(cursor));
        } else {
            scene.setOnMouseDragged(null);
            setDraggable(true);
        }
    }

    private void resizeNorthwest(double newWidth, double newHeight, MouseEvent event) {
        if (newHeight > stage.getMinHeight()) {
            stage.setY(event.getScreenY() - mouseStartY);
            stage.setHeight(newHeight);
        }
        if (newWidth > stage.getMinWidth()) {
            stage.setX(event.getScreenX() - mouseStartX);
            stage.setWidth(newWidth);
        }
    }

    private void resizeNortheast(double newWidth, double newHeight, MouseEvent event) {
        if (newHeight > stage.getMinHeight()) stage.setHeight(newHeight);
        if (newWidth > stage.getMinWidth()) {
            stage.setX(event.getScreenX() - mouseStartX);
            stage.setWidth(newWidth);
        }
    }

    private void resizeSouthwest(double newWidth, double newHeight, MouseEvent event) {
        if (newHeight > stage.getMinHeight()) {
            stage.setHeight(newHeight);
            stage.setY(event.getScreenY() - mouseStartY);
        }
        if (newWidth > stage.getMinWidth()) stage.setWidth(newWidth);
    }

    private void resizeSoutheast(double newWidth, double newHeight) {
        if (newHeight > stage.getMinHeight()) stage.setHeight(newHeight);
        if (newWidth > stage.getMinWidth()) stage.setWidth(newWidth);
    }

    public void hookDragBar(Node node) {
        node.setOnMousePressed(event -> {
            if (!this.draggable) return;
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        node.setOnMouseDragged(event -> {
            if (!this.draggable) return;
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    public void setResizableArea(int resizableArea) {
        this.resizableArea = resizableArea;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public void setResizeable(boolean resizeable) {
        this.resizeable = resizeable;
    }

    public void setVerticalResizeable(boolean state) {
        this.verticalResizeable = state;
    }

    public double getHeight() {
        return SCREEN_HEIGHT;
    }

    public double getWidth() {
        return SCREEN_WIDTH;
    }
}
