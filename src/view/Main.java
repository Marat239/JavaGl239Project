package view;

import javafx.application.Application;
import javafx.event.ActionEvent;
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
    Canvas canvas;
    TextField numberOfTriangles;
    TextField numberOfWideRays;
    TextField filterOverlap;

    @Override
    public void start(Stage stage) throws Exception
    {
        model = new Surface();
//        model.generateRandomTriangles(15, 100, 250);
//        model.generateRandomWideRays(10, 50, 100);

        Scene scene = createScene();
        stage.setScene(scene);
        stage.show();
    }

    private Scene createScene()
    {
        GridPane grid = new GridPane();

        ScrollPane canvas = getCanvasPane();
        Pane control = getControlPanel(grid);
        grid.add(canvas, 0, 0);
        grid.add(control, 1, 0);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.SOMETIMES);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(control.getMinWidth());
        grid.getColumnConstraints().addAll(col1, col2);

        return new Scene(grid, 1200, 800);
    }

    private ScrollPane getCanvasPane()
    {
        canvas = new Canvas();
        ScrollPane pane = new ScrollPane(canvas);

        pane.setContent(canvas);
        canvas.setOnMouseClicked(this::mouseClicked);

        draw();

        pane.heightProperty().addListener(evt ->
        {
            canvas.setHeight(pane.getHeight() * 2);
            draw();
        });

        pane.widthProperty().addListener(evt ->
        {
            canvas.setWidth(pane.getWidth() * 2);
            draw();
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

        numberOfTriangles = new TextField();
        numberOfTriangles.setMaxWidth(60);

        HBox firstLine = new HBox(
                new Label("Добавить"),
                numberOfTriangles,
                new Label("треугольников"));

        firstLine.setSpacing(10);
        firstLine.setAlignment(Pos.BASELINE_LEFT);

        HBox secondLine = new HBox(new CheckBox("Без пересечений"));

        Button execute = new Button("Выполнить");
        Button clear = new Button("Очистить");
        execute.setOnAction(this::generateTrianglesButtonAction);
        clear.setOnAction(this::clearTrianglesButtonAction);

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

        numberOfWideRays = new TextField();
        numberOfWideRays.setMaxWidth(60);

        HBox firstLine = new HBox(
                new Label("Добавить"),
                numberOfWideRays,
                new Label("широких лучей"));

        firstLine.setSpacing(10);
        firstLine.setAlignment(Pos.BASELINE_LEFT);
        Button execute = new Button("Выполнить");
        Button clear = new Button("Очистить");
        execute.setOnAction(this::generateWideRaysButtonAction);
        clear.setOnAction(this::clearWideRaysButtonAction);

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
        filterOverlap = new TextField();
        filterOverlap.setMaxWidth(30);
        Button solveButton = new Button("Решить задачу");
        Button clearButton = new Button("Очистить всё");
        solveButton.setOnAction(this::solveButtonAction);
        clearButton.setOnAction(this::clearButtonAction);

        HBox solveButtons = new HBox(filterOverlap, solveButton, clearButton);

        solveButtons.setSpacing(10);
        solveButtons.setAlignment(Pos.CENTER);

        HBox zoomButtons = new HBox(new Button("+"),
                new Button("-"),
                new Button("100%"));

        zoomButtons.setSpacing(10);
        zoomButtons.setAlignment(Pos.CENTER);

        clickInfo = new Label();
        VBox lines = new VBox(
                new HBox(new Label("Координаты:"), clickInfo),
                new Label("Всего тругольников:"),
                new Label("Всего лучей:"),
                zoomButtons,
                solveButtons);

        lines.setSpacing(10);
        lines.setAlignment(Pos.BASELINE_LEFT);
        lines.setPadding(new Insets(20, 10, 20, 10));

        return new TitledPane("Основное", lines);
    }

    private void solveButtonAction(ActionEvent actionEvent)
    {
        model.computeIntersections();
        draw();
    }

    private void clearButtonAction(ActionEvent actionEvent)
    {
        model.clear();
        draw();
    }

    private void clearTrianglesButtonAction(ActionEvent actionEvent)
    {
        model.getTriangles().clear();
        draw();
    }

    private void generateTrianglesButtonAction(ActionEvent actionEvent)
    {
        String text = numberOfTriangles.getText();
        int n = Integer.parseInt(text);
        model.generateRandomTriangles(n, 300, 500);
        draw();
    }

    private void clearWideRaysButtonAction(ActionEvent actionEvent)
    {
        model.getWideRays().clear();
        draw();
    }

    private void generateWideRaysButtonAction(ActionEvent actionEvent)
    {
        String text = numberOfWideRays.getText();
        int n = Integer.parseInt(text);
        model.generateRandomWideRays(n, 50, 250);
        draw();
    }


    private void draw()
    {
        GraphicsContext gr = canvas.getGraphicsContext2D();
        Bounds bounds = model.getBounds();

        gr.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gr.setLineWidth(1);
        gr.setStroke(Color.valueOf("#FF00FF"));
        for (Triangle t : model.getTriangles())
        {
            for (Vector v : t.getVectors())
            {
                gr.strokeLine(v.From.X, v.From.Y, v.To.X, v.To.Y);
            }
        }

        gr.setStroke(Color.valueOf("#0000ff"));
        for (WideRay w : model.getWideRays())
        {
            for (Vector v : w.getVectors())
            {
                gr.strokeLine(v.From.X, v.From.Y, v.To.X, v.To.Y);
            }
        }

        gr.setLineWidth(2);
        gr.setStroke(Color.valueOf("#FF0000"));

        int filter = -1;
        if (filterOverlap != null && filterOverlap.getText().length() > 0)
        {
            filter = Integer.parseInt(filterOverlap.getText());
        }

        gr.setFill(Color.valueOf("#FE6F7E20"));
        for (int j = 0; j < model.getOverlaps().size(); j++)
        {
            Overlap o = model.getOverlaps().get(j);
            if (o == model.getLargestOverlap())
            {
                continue;
            }

            if (filter > -1 && filter != j)
            {
                continue;
            }

            List<Vector> vectors = o.Intersection.getVectors();
            double[] xx = new double[vectors.size()];
            double[] yy = new double[vectors.size()];
            for (int i = 0; i < vectors.size(); i++)
            {
                var v = vectors.get(i);

                xx[i] = v.From.X;
                yy[i] = v.From.Y;
            }

            gr.fillPolygon(xx, yy, vectors.size());

            for (int i = 0; i < vectors.size(); i++)
            {
                var v = vectors.get(i);
                gr.strokeLine(v.From.X, v.From.Y, v.To.X, v.To.Y);
            }
        }

        gr.setFill(Color.valueOf("#FE6A76"));
        Overlap o = model.getLargestOverlap();
        if(o != null)
        {
            List<Vector> vectors = o.Intersection.getVectors();
            double[] xx = new double[vectors.size()];
            double[] yy = new double[vectors.size()];
            for (int i = 0; i < vectors.size(); i++)
            {
                var v = vectors.get(i);

                xx[i] = v.From.X;
                yy[i] = v.From.Y;
            }

            gr.fillPolygon(xx, yy, vectors.size());

            for (int i = 0; i < vectors.size(); i++)
            {
                var v = vectors.get(i);
                gr.strokeLine(v.From.X, v.From.Y, v.To.X, v.To.Y);
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
