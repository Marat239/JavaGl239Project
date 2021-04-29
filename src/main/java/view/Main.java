package view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.*;
import javafx.scene.Node;
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

public class Main extends Application implements ISurfaceEventHandler
{
    Label clickInfo;
    Surface surface;
    Canvas canvas;
    TextField numberOfTriangles;
    TextField numberOfWideRays;
    int filterOverlap;
    TextField[] customTriangleCoordX;
    TextField[] customTriangleCoordY;
    Label addTriangleErrorMessage;
    Label addWideRayErrorMessage;
    Stage dialogStage;
    Stage mainStage;

    Label trianglesCount;
    Label wideRaysCount;
    Label overlapsCount;
    Label largestOverlapSquare;

    @Override
    public void start(Stage stage) throws Exception
    {
        mainStage = stage;
        surface = new Surface();
        surface.setOnChange(this);
        Scene scene = createScene();
        stage.setScene(scene);
        stage.show();
    }

    private Scene createScene()
    {
        GridPane grid = new GridPane();

        Node canvas = getCanvasPane();
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


    private Node getCanvasPane()
    {
        canvas = new Canvas();
        //ScrollPane pane = new ScrollPane(canvas);

        var pane = new Pane(canvas);
        pane.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        canvas.setOnMouseClicked(this::mouseClicked);

        pane.heightProperty().addListener(evt -> {
            canvas.setHeight(pane.getHeight());
            draw();
        });

        pane.widthProperty().addListener(evt -> {
            canvas.setWidth(pane.getWidth());
            draw();
        });
        return pane;
    }


    private Pane getControlPanel(Pane parent)
    {
        TitledPane[] panes = new TitledPane[]{createInformationPane(), createTrianglesPane(), createWideRaysPane(), createImportExportPane()};

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

        HBox firstLine = new HBox(new Label("Добавить"), numberOfTriangles, new Label("треугольников"));

        firstLine.setSpacing(10);
        firstLine.setAlignment(Pos.BASELINE_LEFT);

        Button execute = new Button("Выполнить");
        Button clear = new Button("Очистить");

        execute.setOnAction(this::generateTrianglesButtonAction);
        clear.setOnAction(this::clearTrianglesButtonAction);

        Button addCustom = new Button("Ввести вручную");
        addCustom.setOnAction(this::enterCustomTriangle);

        clear.prefWidthProperty().bind(execute.widthProperty());

        grid.add(firstLine, 0, 0);
        grid.add(addCustom, 0, 1);
        grid.add(execute, 1, 0);
        grid.add(clear, 1, 1);

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

        HBox firstLine = new HBox(new Label("Добавить"), numberOfWideRays, new Label("широких лучей"));

        firstLine.setSpacing(10);
        firstLine.setAlignment(Pos.BASELINE_LEFT);
        Button execute = new Button("Выполнить");
        Button clear = new Button("Очистить");
        execute.setOnAction(this::generateWideRaysButtonAction);
        clear.setOnAction(this::clearWideRaysButtonAction);

        Button addCustom = new Button("Ввести вручную");
        addCustom.setOnAction(this::enterCustomWideRay);

        clear.prefWidthProperty().bind(execute.widthProperty());

        grid.add(firstLine, 0, 0);
        grid.add(addCustom, 0, 1);
        grid.add(execute, 1, 0);
        grid.add(clear, 1, 1);

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
        if (file != null)
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
        if (file != null)
        {
            surface.loadFromFile(file);
        }
    }

    private TitledPane createInformationPane()
    {
        Button prevIntersection = new Button("<-");
        Button nextIntersection = new Button("->");
        Button solveButton = new Button("Решить задачу");
        Button clearButton = new Button("Очистить всё");
        solveButton.setPrefWidth(110);
        clearButton.setPrefWidth(110);

        prevIntersection.setOnAction(this::prevIntersectionAction);
        nextIntersection.setOnAction(this::nextIntersectionAction);

        solveButton.setOnAction(this::solveButtonAction);
        clearButton.setOnAction(this::clearButtonAction);

        HBox solveButtons = new HBox(solveButton, clearButton);
        HBox choiceButtons = new HBox(prevIntersection, nextIntersection);

        solveButtons.setSpacing(10);
        solveButtons.setAlignment(Pos.CENTER);
        choiceButtons.setSpacing(10);
        choiceButtons.setAlignment(Pos.CENTER);

        clickInfo = new Label();
        trianglesCount = new Label();
        wideRaysCount = new Label();
        overlapsCount = new Label();
        largestOverlapSquare = new Label();

        VBox lines = new VBox(
                new HBox(new Label("Координаты: "), clickInfo),
                new HBox(new Label("Всего треугольников: "), trianglesCount),
                new HBox(new Label("Всего лучей: "), wideRaysCount),
                new HBox(new Label("Всего пересечений: "), overlapsCount),
                new HBox(new Label("Максимальная площадь: "), largestOverlapSquare),
                solveButtons,
                choiceButtons);

        lines.setSpacing(10);
        lines.setAlignment(Pos.BASELINE_LEFT);
        lines.setPadding(new Insets(10, 10, 10, 10));

        return new TitledPane("Основное", lines);
    }

    private void nextIntersectionAction(ActionEvent actionEvent)
    {
        if(filterOverlap < surface.getOverlaps().size() - 1)
        {
            filterOverlap++;
        }
        else
        {
            filterOverlap = 0;
        }

        onChange();
    }

    private void prevIntersectionAction(ActionEvent actionEvent)
    {
        if(filterOverlap > 0)
        {
            filterOverlap--;
        }
        else
        {
            filterOverlap = surface.getOverlaps().size() - 1;
        }

        onChange();
    }

    private void solveButtonAction(ActionEvent actionEvent)
    {
        filterOverlap = -1;
        surface.computeIntersections();
    }

    private void clearButtonAction(ActionEvent actionEvent)
    {
        surface.clear();
    }

    private void clearTrianglesButtonAction(ActionEvent actionEvent)
    {
        surface.clearTriangles();
    }

    private void generateTrianglesButtonAction(ActionEvent actionEvent)
    {
        String text = numberOfTriangles.getText();
        int n = Integer.parseInt(text);
        surface.generateRandomTriangles(n, 300, 500);
    }

    private void clearWideRaysButtonAction(ActionEvent actionEvent)
    {
        surface.clearWideRays();
    }

    private void generateWideRaysButtonAction(ActionEvent actionEvent)
    {
        String text = numberOfWideRays.getText();
        int n = Integer.parseInt(text);
        surface.generateRandomWideRays(n, 50, 250);
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
            } catch (NumberFormatException e)
            {
                addTriangleErrorMessage.setText(MessageFormat.format("Ошибка: {0}", e.getMessage()));
                return;
            }
        }

        Triangle triangle = Triangle.create(p[0], p[1], p[2]);
        if (triangle == null)
        {
            addTriangleErrorMessage.setText("Ошибка: невозможный треугольник");
            return;
        }
        surface.add(triangle);
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
        grid.add(addWideRayErrorMessage, 0, n, 2, 1);

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
            } catch (NumberFormatException e)
            {
                addWideRayErrorMessage.setText(MessageFormat.format("Ошибка: {0}", e.getMessage()));
                return;
            }
        }

        WideRay wideRay = WideRay.create(new Vector(p[0], p[1]));
        if(wideRay == null)
        {
            addWideRayErrorMessage.setText("Ошибка: невозможный широкий луч");
            return;
        }
        surface.add(wideRay);
        dialogStage.close();
    }

    private void drawPolygon(GraphicsContext gr, Polygon polygon, boolean fill, double k, double dx, double dy)
    {
        List<Vector> vectors = polygon.getVectors();
        int size = vectors.size();

        for (Vector v : vectors)
        {
            gr.strokeLine((v.From.X + dx) * k, (v.From.Y + dy) * k, (v.To.X + dx) * k, (v.To.Y + dy) * k);
        }

        if (fill)
        {
            double[] xx = new double[size];
            double[] yy = new double[size];

            for (int i = 0; i < size; i++)
            {
                var v = vectors.get(i);

                xx[i] = (v.From.X + dx) * k;
                yy[i] = (v.From.Y + dy) * k;
            }

            gr.fillPolygon(xx, yy, vectors.size());
        }
    }

    private void draw()
    {
        GraphicsContext gr = canvas.getGraphicsContext2D();
        gr.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (surface.isEmpty())
        {
            return;
        }

        Bounds bounds = surface.getBounds();
        int padding = 10;

        var kx = (canvas.getWidth() - 2 * padding) / (bounds.MaxX - bounds.MinX);
        var ky = (canvas.getHeight() - 2 * padding) / (bounds.MaxY - bounds.MinY);
        var k = Math.min(kx, ky);
        var dx = -bounds.MinX + padding / k;
        var dy = -bounds.MinY + padding / k;

        gr.setLineWidth(1);
        gr.setStroke(Color.valueOf("#FF00FF"));
        for (Triangle t : surface.getTriangles())
        {
            drawPolygon(gr, t, false, k, dx, dy);
        }

        gr.setStroke(Color.valueOf("#0000ff"));
        for (WideRay w : surface.getWideRays())
        {
            drawPolygon(gr, w, false, k, dx, dy);
        }

        gr.setLineWidth(2);
        gr.setStroke(Color.valueOf("#FF0000"));

        if (surface.getOverlaps().size() == 0)
        {
            return;
        }

        // continue if the task is solved
        if (filterOverlap == -1)
        {
            // draw all
            // last one is the largest
            gr.setFill(Color.valueOf("#FE6F7E20"));
            for (int i = 0; i < surface.getOverlaps().size() - 1; i++)
            {
                drawPolygon(gr, surface.getOverlaps().get(i).Intersection, true, k, dx, dy);
            }

            gr.setFill(Color.valueOf("#FE6A76"));
            drawPolygon(gr, surface.getLargestOverlap().Intersection, true, k, dx, dy);
        }
        else
        {
            if(filterOverlap == surface.getOverlaps().size() - 1)
            {
                gr.setFill(Color.valueOf("#FE6F7E"));
            }
            else
            {
                gr.setFill(Color.valueOf("#FE6F7E20"));
            }
            drawPolygon(gr, surface.getOverlaps().get(filterOverlap).Intersection, true, k, dx, dy);
        }
    }

    private void mouseClicked(MouseEvent x)
    {
        clickInfo.setText(MessageFormat.format("{0} {1}", x.getSceneX(), x.getSceneY()));
    }


    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void onChange()
    {
        draw();
        trianglesCount.setText(String.valueOf(surface.getTriangles().size()));
        wideRaysCount.setText(String.valueOf(surface.getWideRays().size()));
        overlapsCount.setText(String.valueOf(surface.getOverlaps().size()));

        if (surface.getOverlaps().size() > 0)
        {
            largestOverlapSquare.setText(String.valueOf(surface.getLargestOverlap().Square));
        }

    }
}
