package view;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.*;
import model.Bounds;
import model.Vector;

import java.text.MessageFormat;
import java.util.*;

public class Main extends Application
{
    Label clickInfo;
    Surface model;

    @Override
    public void start(Stage stage) throws Exception
    {
        model = new Surface();
        model.generateRandomTriangles(15, 100, 250);
        model.generateRandomWideRays(10, 50, 100);

        Scene scene = createScene();
        stage.setScene(scene);
        stage.show();
    }

    private Scene createScene()
    {
        GridPane grid = new GridPane();

        ScrollPane canvas = getCanvasPane(grid);
        Pane control = getControlPanel(grid);
        grid.add(canvas, 0, 0);
        grid.add(control, 1, 0);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.SOMETIMES);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(control.getMinWidth());
//        col2.setMinWidth(200);


        grid.getColumnConstraints().addAll(col1, col2);

        return new Scene(grid, 1200, 800);
    }

    private ScrollPane getCanvasPane(Pane parent)
    {
        Canvas canvas = new Canvas();
        ScrollPane pane = new ScrollPane(canvas);

        pane.setContent(canvas);

        draw(pane, canvas);

        EventHandler<? super MouseEvent> canvasClicked;
        canvas.setOnMouseClicked(this::mouseClicked);

        pane.heightProperty().addListener(evt ->
        {
            canvas.setHeight(pane.getHeight() * 2);
            draw(pane, canvas);
        });

        pane.widthProperty().addListener(evt ->
        {
            canvas.setWidth(pane.getWidth() * 2);
            draw(pane, canvas);
        });

        return pane;
    }


    private Pane getControlPanel(Pane parent)
    {
        TitledPane[] panes = new TitledPane[]
                {
                        createInformationPane(),
                        createTrianglesPane(),
                        createWideRaysPane(),
                        createImportExportPane()
                };

        var pane = new Pane();
        Accordion accordion = new Accordion();
        double width = 0;
        for (var p : panes)
        {
            width = Math.max(p.getMinWidth(), width);
            accordion.getPanes().add(p);
        }
        accordion.setExpandedPane(panes[0]);

        pane.setMinWidth(width);
        pane.getChildren().add(new VBox(accordion));
        pane.prefHeightProperty().bind(parent.heightProperty());
        accordion.prefWidthProperty().bind(pane.widthProperty());
        return pane;
    }


    private TitledPane createTrianglesPane()
    {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 20, 10));

        TextField number = new TextField();
        number.setMaxWidth(60);

        HBox firstLine = new HBox(
                new Label("Добавить"),
                number,
                new Label("треугольников"));

        firstLine.setSpacing(10);
        firstLine.setAlignment(Pos.BASELINE_LEFT);

        HBox secondLine = new HBox(new CheckBox("Без пересечений"));

        Button execute = new Button("Выполнить");
        Button clear = new Button("Очистить");

        clear.prefWidthProperty().bind(execute.widthProperty());

        grid.add(firstLine, 0, 0);
        grid.add(secondLine, 0, 1);
        grid.add(execute, 1, 0);
        grid.add(clear, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().add(col1);

        TitledPane pane = new TitledPane("Треугольники", grid);
        pane.setMinWidth(350);
        return pane;
    }

    private TitledPane createWideRaysPane()
    {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 20, 10));

        TextField number = new TextField();
        number.setMaxWidth(60);

        HBox firstLine = new HBox(
                new Label("Добавить"),
                number,
                new Label("широких лучей"));

        firstLine.setSpacing(10);
        firstLine.setAlignment(Pos.BASELINE_LEFT);
        Button execute = new Button("Выполнить");
        Button clear = new Button("Очистить");

        clear.prefWidthProperty().bind(execute.widthProperty());

        grid.add(firstLine, 0, 0, 1, 2);
        grid.add(execute, 1, 0);
        grid.add(clear, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().add(col1);

        TitledPane pane = new TitledPane("Широкий луч", grid);
        pane.setMinWidth(350);
        return pane;
    }

    private TitledPane createImportExportPane()
    {
        VBox buttons = new VBox(
                new Button("Загрузить из файла"),
                new Button("Сохранить в файл"));

        buttons.setSpacing(10);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(20, 10, 20, 10));

        return new TitledPane("Импорт и экспорт", buttons);
    }

    private TitledPane createInformationPane()
    {
        HBox solveButtons = new HBox(
                new Button("Решить задачу"),
                new Button("Очистить всё"));

        solveButtons.setSpacing(10);
        solveButtons.setAlignment(Pos.CENTER);

        HBox zoomButtons = new HBox(new Button("+"),
                new Button("-"),
                new Button("100%"));

        zoomButtons.setSpacing(10);
        zoomButtons.setAlignment(Pos.CENTER);

        VBox lines = new VBox(
                new Label("Всего тругольников:"),
                new Label("Всего лучей:"),
                zoomButtons,
                solveButtons);

        lines.setSpacing(10);
        lines.setAlignment(Pos.BASELINE_LEFT);
        lines.setPadding(new Insets(20, 10, 20, 10));

        return new TitledPane("Основное", lines);
    }

    ArrayList<Triangle> tt = null;

    private void draw(ScrollPane pane, Canvas canvas)
    {
        GraphicsContext gr = canvas.getGraphicsContext2D();
        Bounds bounds = model.getBounds();

        gr.setStroke(Color.valueOf("#FF00FF"));
        for (Triangle t : model.getTriangles())
        {
            for (Vector v : t.getVectors())
            {
                gr.strokeLine(
                        v.From.X - bounds.MinX,
                        v.From.Y - bounds.MinY,
                        v.To.X - bounds.MinX,
                        v.To.Y - bounds.MinY);
            }
        }

        gr.setStroke(Color.valueOf("#0000ff"));
        for (WideRay w : model.getWideRays())
        {
            for (Vector v : w.getVectors())
            {
                gr.strokeLine(
                        v.From.X - bounds.MinX,
                        v.From.Y - bounds.MinY,
                        v.To.X - bounds.MinX,
                        v.To.Y - bounds.MinY);
            }
        }

//        canvas.setWidth(bounds.MaxX - bounds.MinX);
//        canvas.setHeight(bounds.MaxY - bounds.MinX);
    }

    private void mouseClicked(MouseEvent x)
    {
        clickInfo.setText(MessageFormat.format("{0} {1}", x.getSceneX(), x.getSceneY()));
    }


    public static void main(String[] args)
    {
        launch(args);
    }


//        grid.setBackground(new Background(new BackgroundFill(
//                Color.rgb(69, 99, 75),
//                CornerRadii.EMPTY,
//                Insets.EMPTY)));

}
