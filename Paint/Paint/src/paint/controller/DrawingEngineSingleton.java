package paint.controller;

import paint.model.Shape;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DrawingEngineSingleton implements DrawingEngine {
    private static DrawingEngineSingleton instance;
    private ArrayList<Shape> shapeList = new ArrayList<>();
    private Stack<ArrayList<Shape>> primary = new Stack<>();
    private Stack<ArrayList<Shape>> secondary = new Stack<>();

    private DrawingEngineSingleton() {}

    public static DrawingEngineSingleton getInstance() {
        if (instance == null) {
            instance = new DrawingEngineSingleton();
        }
        return instance;
    }

    @Override
    public void refresh(Object canvas) {
        try {
            primary.push(new ArrayList<>(cloneList(shapeList)));
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        redraw((javafx.scene.canvas.Canvas) canvas);
    }

    @Override
    public void addShape(Shape shape) {
        shapeList.add(shape);
    }

    @Override
    public void removeShape(Shape shape) {
        shapeList.remove(shape);
    }

    @Override
    public void updateShape(Shape oldShape, Shape newShape) {
        shapeList.remove(oldShape);
        shapeList.add(newShape);
    }

    @Override
    public Shape[] getShapes() {
        return shapeList.toArray(new Shape[0]);
    }

    @Override
    public void undo() {
        if (secondary.size() < 21) {
            ArrayList<Shape> temp = primary.pop();
            secondary.push(temp);
            if (primary.empty()) {
                shapeList = new ArrayList<>();
            } else {
                temp = primary.peek();
                shapeList = temp;
            }
        }
    }

    @Override
    public void redo() {
        ArrayList<Shape> temp = secondary.pop();
        primary.push(temp);
        temp = primary.peek();
        shapeList = temp;
    }

    @Override
    public void save(String path) {
        // Implement save logic here or delegate to SaveToXML
    }

    @Override
    public void load(String path) {
        // Implement load logic here or delegate to LoadFromXML
    }

    @Override
    public List<Class<? extends Shape>> getSupportedShapes() {
        return null;
    }

    @Override
    public void installPluginShape(String jarPath) {
        // Implement plugin shape logic here
    }

    // Helper methods
    public ArrayList<Shape> cloneList(ArrayList<Shape> l) throws CloneNotSupportedException {
        ArrayList<Shape> temp = new ArrayList<>();
        for (int i = 0; i < l.size(); i++) {
            temp.add(l.get(i).cloneShape());
        }
        return temp;
    }

    public void redraw(javafx.scene.canvas.Canvas canvas) {
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, 850, 370);
        try {
            for (int i = 0; i < shapeList.size(); i++) {
                shapeList.get(i).draw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
