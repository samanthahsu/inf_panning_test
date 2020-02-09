import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    Delta boardDrag;
    Delta childDrag;
    Node draggedChild;

    @Override
    public void start(Stage primaryStage) throws Exception {
        AnchorPane anchorPane = new AnchorPane();
//        anchorPane.setManaged(false);
        anchorPane.setStyle("-fx-background-color: #FF0000;");
        anchorPane.getChildren().add(new Text("hello world"));

        StackPane pane = new StackPane();
        pane.getChildren().add(anchorPane);
        pane.setPrefSize(1024,900);
        pane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double scrollY = event.getDeltaY();
                anchorPane.setScaleX(anchorPane.getScaleX() + scrollY / 300);
                anchorPane.setScaleY(anchorPane.getScaleY() + scrollY / 300);
            }
        });
        pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                boardDrag = new Delta(event.getX(), event.getY());
            }
        });
        pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                anchorPane.setTranslateX(anchorPane.getTranslateX() + event.getX() - boardDrag.x);
                anchorPane.setTranslateY(anchorPane.getTranslateY() + event.getY() - boardDrag.y);
                boardDrag.x = event.getX();
                boardDrag.y = event.getY();
            }
        });


        anchorPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                for (Node child : anchorPane.getChildren()) {
                    if(child.localToScene(child.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY())) {
                        Point2D childPos = child.localToScene(0,0);
                        childDrag = new Delta(childPos.getX() - event.getSceneX(), childPos.getY() - event.getSceneY());
                        draggedChild = child;
                        event.consume();
                        break;
                    }
                }

            }
        });
        anchorPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (draggedChild != null) {
                    Point2D newPos = anchorPane.sceneToLocal(event.getSceneX() + childDrag.x, event.getSceneY() + childDrag.y);
                    draggedChild.setLayoutX(newPos.getX());
                    draggedChild.setLayoutY(newPos.getY());
                    event.consume();
                }

            }

        });
        pane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                draggedChild = null;
            }
        });


        BorderPane root = new BorderPane();
        root.setCenter(pane);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("infinity");
        primaryStage.sizeToScene();
        primaryStage.show();
    }
}