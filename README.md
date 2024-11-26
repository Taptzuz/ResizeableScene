# JavaFX Resizable Transparent Scene

A robust and flexible JavaFX implementation for creating resizable scenes with a transparent style. This solution supports resizing from all corners of the scene and offers enhanced customization options for defining the resizable areas. Additionally, it includes functionality to set draggable zones, enabling smooth scene movement.

## Features
- **Resizable Transparent Scenes**: Easily resize scenes with a transparent style, ensuring a seamless user experience.
- **Full Corner Support**: Resize the scene from all corners for maximum flexibility.
- **Custom Resizable Areas**: Define specific areas at the corners or edges where resizing is allowed.
- **Draggable Zones**: Attach draggable areas to allow the entire scene to be moved effortlessly.
- **Enhanced Resize Behavior**: 
  - Automatically resets the mouse cursor when a node obstructs the resizable area.
  - Provides a smoother experience compared to standard "resizable scenes" implementations.

## Benefits
- Provides precise control over resizable and draggable areas.
- Ensures a polished user experience by addressing common issues with resizing behavior.
- Easily integrated into JavaFX applications with transparent window styles.

## Usage
1. **Define Resizable Areas**:
   - Configure the dimensions or nodes at the edges and corners of the scene where resizing is allowed.
2. **Set Draggable Zones**:
   - Attach draggable nodes to enable smooth movement of the entire scene.
3. **Enhanced Cursor Behavior**:
   - Benefit from the logic that resets the mouse cursor when overlapping nodes block resizable zones.

## Example
```java
public class ExampleStage extends BorderPane {

    private final Stage stage;
    private final Scene scene;

    private final ResizeableScene resizeableScene;

   public ExampleStage() {
        this.stage = new Stage();
        this.scene = new Scene(this);

        this.stage.setTitle("Example");

        this.scene.setFill(Color.TRANSPARENT);
        this.stage.setResizable(false);
        this.stage.setScene(scene);
        this.stage.initStyle(StageStyle.TRANSPARENT);

        this.resizeableScene = new ResizeableScene(this.stage, true, 6);
    }

    /*Example Methods*/
    public final void disableResize() {
        this.resizeableScene.setResizeable(false);
    }

    public final void onlyHorizontalResizeable() {
        this.resizeableScene.setVerticalResizeable(false);
    }

    public void setResizeable(boolean state) {
        this.resizeableScene.setResizeable(state);
    }

    public void setDraggable(boolean state) {
        this.resizeableScene.setDraggable(state);
    }

     /*
      If your draggable area contains interactive content,
      you should call this method with the relevant node as a parameter to temporarily disable the draggable listener.
      This ensures that actions such as button clicks are not dragging the window for example.
     */
    public final void hookResizeableScene(Node node, final ResizeableScene resizeableScene) {
        node.setOnMousePressed(event -> resizeableScene.setDraggable(false));
        node.setOnMouseReleased(event -> resizeableScene.setDraggable(true));
    }
}
