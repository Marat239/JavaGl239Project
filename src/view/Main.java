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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import model.Bounds;
import model.Vector;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

public class Main extends Application
{
    Label clickInfo;
    Surface surface;
    Canvas canvas;
    TextField numberOfTriangles;
    TextField numberOfWideRays;
    TextField filterOverlap;
    TextField[] customTriangleCoordX;
    TextField[] customTriangleCoordY;
    Label addTriangleErrorMessage;
    Label addWideRayErrorMessage;
    Stage dialogStage;
    Stage mainStage;

    @Override
    public void start(Stage stage) throws Exception
    {
        mainStage = stage;
        surface = new Surface();
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

        Button addCustom = new Button("Ввести вручную");
        addCustom.setOnAction(this::enterCustomTriangle);

        clear.prefWidthProperty().bind(execute.widthProperty());

        grid.add(firstLine, 0, 0);
        grid.add(secondLine, 0, 1);
        grid.add(execute, 1, 0);
        grid.add(clear, 1, 1);
        grid.add(addCustom, 0, 2, 2, 1);


        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().add(col1);

        TitledPane pane = new TitledPane("Треугольники", grid);
        pane.setMinWidth(350);
        return pane;
    }

    private void enterCustomTriangle(ActionEvent actionEvent)
    {
        showCustomTriangleDialog(mainStage);
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

        Button addCustom = new Button("Ввести вручную");
        addCustom.setOnAction(this::enterCustomWideRay);

        clear.prefWidthProperty().bind(execute.widthProperty());

        grid.add(firstLine, 0, 0, 1, 2);
        grid.add(execute, 1, 0);
        grid.add(clear, 1, 1);
        grid.add(addCustom, 0, 2, 2, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().add(col1);

        TitledPane pane = new TitledPane("Широкий луч", grid);
        pane.setMinWidth(350);
        return pane;
    }

    private void enterCustomWideRay(ActionEvent actionEvent)
    {
        showCustomWideRayDialog(mainStage);
    }

    private TitledPane createImportExportPane()
    {
        Button saveToFile = new Button("Сохранить в файл");
        Button loadFromFile = new Button("Загрузить из файла");
        VBox buttons = new VBox(loadFromFile, saveToFile);
        
        saveToFile.setOnAction(this::saveToFileAction);
        loadFromFile.setOnAction(this::loadFromFileAction);

        buttons.setSpacing(10);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(20, 10, 20, 10));

        return new TitledPane("Импорт и экспорт", buttons);
    }

    private void saveToFileAction(ActionEvent actionEvent)
    {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("json", "*.json");
        fc.getExtensionFilters().add(extFilter);

        File file = fc.showSaveDialog(mainStage);
        if(file != null)
        {
            surface.saveToFile(file);
        }
    }

    private void loadFromFileAction(ActionEvent actionEvent)
    {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("json", "*.json");
        fc.getExtensionFilters().add(extFilter);

        File file = fc.showOpenDialog(mainStage);
        if(file != null)
        {
            surface.loadFromFile(file);
            draw();
        }
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
        surface.computeIntersections();
        draw();
    }

    private void clearButtonAction(ActionEvent actionEvent)
    {
        surface.clear();
        draw();
    }

    private void clearTrianglesButtonAction(ActionEvent actionEvent)
    {
        surface.getTriangles().clear();
        draw();
    }

    private void generateTrianglesButtonAction(ActionEvent actionEvent)
    {
        String text = numberOfTriangles.getText();
        int n = Integer.parseInt(text);
        surface.generateRandomTriangles(n, 300, 500);
        draw();
    }

    private void clearWideRaysButtonAction(ActionEvent actionEvent)
    {
        surface.getWideRays().clear();
        draw();
    }

    private void generateWideRaysButtonAction(ActionEvent actionEvent)
    {
        String text = numberOfWideRays.getText();
        int n = Integer.parseInt(text);
        surface.generateRandomWideRays(n, 50, 250);
        draw();
    }

    private void showCustomTriangleDialog(Stage parentStage)
    {
        dialogStage = new Stage();
        GridPane grid = new GridPane();
        int n = 3;
        TextField[] coordsX = new TextField[n];
        TextField[] coordsY = new TextField[n];

        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 20, 10));

        for (int i = 0; i < n; i++)
        {
            grid.add(new Label(MessageFormat.format("Добавьте {0}-ю координату:", i + 1)), 0, i);

            coordsX[i] = new TextField();
            coordsX[i].setMaxWidth(60);
            HBox firstCoord = new HBox(new Label("X:"), coordsX[i]);
            firstCoord.setSpacing(5);
            firstCoord.setAlignment(Pos.BASELINE_LEFT);
            grid.add(firstCoord, 1, i);

            coordsY[i] = new TextField();
            coordsY[i].setMaxWidth(60);
            HBox secondCoord = new HBox(new Label("Y:"), coordsY[i]);
            secondCoord.setSpacing(5);
            secondCoord.setAlignment(Pos.BASELINE_LEFT);
            grid.add(secondCoord, 2, i);
        }

        addTriangleErrorMessage = new Label("");
        addTriangleErrorMessage.setTextFill(Color.RED);
        grid.add(addTriangleErrorMessage, 0, n, 2, 1);

        Button addTriangle = new Button("Добавить");
        addTriangle.setOnAction(this::addCustomTriangle);
        grid.add(addTriangle, 2, n);

        customTriangleCoordX = coordsX;
        customTriangleCoordY = coordsY;


        dialogStage.setScene(new Scene(grid));
        dialogStage.setTitle("Добавление треугольника");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setResizable(false);
        dialogStage.show();
    }

    private void addCustomTriangle(ActionEvent actionEvent)
    {
        addTriangleErrorMessage.setText(null);
        Point[] p = new Point[3];

        for (int i = 0; i < 3; i++)
        {
            try
            {
                double x = Double.parseDouble(customTriangleCoordX[i].getText());
                double y = Double.parseDouble(customTriangleCoordY[i].getText());
                p[i] = new Point(x, y);
            }
            catch (NumberFormatException e)
            {
                addTriangleErrorMessage.setText(
                        MessageFormat.format("Ошибка: {0}", e.getMessage()));
                return;
            }
        }

        Triangle triangle = Triangle.create(p[0], p[1], p[2]);
        if(triangle == null)
        {
            addTriangleErrorMessage.setText("Ошибка: невозможный треугольник");
            return;
        }
        surface.add(triangle);
        draw();
        dialogStage.close();
    }

    private void showCustomWideRayDialog(Stage parentStage)
    {
        dialogStage = new Stage();
        GridPane grid = new GridPane();
        int n = 2;
        TextField[] coordsX = new TextField[n];
        TextField[] coordsY = new TextField[n];

        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 20, 10));

        for (int i = 0; i < n; i++)
        {
            grid.add(new Label(MessageFormat.format("Добавьте {0}-ю координату:", i + 1)), 0, i);

            coordsX[i] = new TextField();
            coordsX[i].setMaxWidth(60);
            HBox firstCoord = new HBox(new Label("X:"), coordsX[i]);
            firstCoord.setSpacing(5);
            firstCoord.setAlignment(Pos.BASELINE_LEFT);
            grid.add(firstCoord, 1, i);

            coordsY[i] = new TextField();
            coordsY[i].setMaxWidth(60);
            HBox secondCoord = new HBox(new Label("Y:"), coordsY[i]);
            secondCoord.setSpacing(5);
            secondCoord.setAlignment(Pos.BASELINE_LEFT);
            grid.add(secondCoord, 2, i);
        }

        addWideRayErrorMessage = new Label("");
        addWideRayErrorMessage.setTextFill(Color.RED);
        grid.add(addTriangleErrorMessage, 0, n, 2, 1);

        Button addWideRay = new Button("Добавить");
        addWideRay.setOnAction(this::addCustomWideRay);
        grid.add(addWideRay, 2, n);

        customTriangleCoordX = coordsX;
        customTriangleCoordY = coordsY;

        dialogStage.setScene(new Scene(grid));
        dialogStage.setTitle("Добавление широкого луча");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setResizable(false);
        dialogStage.show();
    }

    private void addCustomWideRay(ActionEvent actionEvent)
    {
        addWideRayErrorMessage.setText(null);
        Point[] p = new Point[2];

        for (int i = 0; i < 2; i++)
        {
            try
            {
                double x = Double.parseDouble(customTriangleCoordX[i].getText());
                double y = Double.parseDouble(customTriangleCoordY[i].getText());
                p[i] = new Point(x, y);
            }
            catch (NumberFormatException e)
            {
                addWideRayErrorMessage.setText(
                        MessageFormat.format("Ошибка: {0}", e.getMessage()));
                return;
            }
        }

        WideRay wideRay = new WideRay(p[0], p[1]);
        surface.add(wideRay);
        draw();
        dialogStage.close();
    }


    private void draw()
    {
        GraphicsContext gr = canvas.getGraphicsContext2D();
        Bounds bounds = surface.getBounds();

        gr.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gr.setLineWidth(1);
        gr.setStroke(Color.valueOf("#FF00FF"));
        for (Triangle t : surface.getTriangles())
        {
            for (Vector v : t.getVectors())
            {
                gr.strokeLine(v.From.X, v.From.Y, v.To.X, v.To.Y);
            }
        }

        gr.setStroke(Color.valueOf("#0000ff"));
        for (WideRay w : surface.getWideRays())
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
        for (int j = 0; j < surface.getOverlaps().size(); j++)
        {
            Overlap o = surface.getOverlaps().get(j);
            if (o == surface.getLargestOverlap())
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
        Overlap o = surface.getLargestOverlap();
        if (o != null)
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
