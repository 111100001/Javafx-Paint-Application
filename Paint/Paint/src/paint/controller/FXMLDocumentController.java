
package paint.controller;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import paint.model.*;


public class FXMLDocumentController implements Initializable {
  
    /***FXML VARIABLES***/
    @FXML
    private Button DeleteBtn;

    @FXML
    private ComboBox<String> ShapeBox;

    @FXML
    private Button UndoBtn;

    @FXML
    private Button RedoBtn;

    @FXML
    private ColorPicker ColorBox;

    @FXML
    private Button SaveBtn;
    
    @FXML
    private Button MoveBtn;
    
    @FXML
    private Button RecolorBtn;
    
    @FXML
    private Button LoadBtn;
    
    @FXML
    private GridPane After;
    
    @FXML
    private Pane Before;
    
    @FXML
    private Pane PathPane;
    
    @FXML
    private TextField PathText;

    @FXML
    private Button StartBtn;
    
    @FXML
    private Button ResizeBtn;
    
    @FXML
    private Button ImportBtn;
    
    @FXML
    private Button PathBtn;
    
    @FXML
    private Canvas CanvasBox;
    
    @FXML
    private Button CopyBtn;
    
    @FXML
    private Label Message;
    
    @FXML
    private ListView ShapeList;
    
    
    
    /***CLASS VARIABLES***/
    private Point2D start;
    private Point2D end;
    
    private boolean move=false;
    private boolean copy=false;
    private boolean resize=false;
    private boolean save=false;
    private boolean load=false;
    private boolean importt =false;


    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        if(event.getSource() == StartBtn){
            Before.setVisible(false);
            After.setVisible(true);
        }
        
        Message.setText("");
        if(event.getSource()==DeleteBtn){
            if(!ShapeList.getSelectionModel().isEmpty()){
                int index = ShapeList.getSelectionModel().getSelectedIndex();
                Shape[] shapes = DrawingEngineSingleton.getInstance().getShapes();
                if (index >= 0 && index < shapes.length) {
                    DrawingEngineSingleton.getInstance().removeShape(shapes[index]);
                }
            }else{
                Message.setText("You need to pick a shape first to delete it.");
            }
        }
        
        if(event.getSource()==RecolorBtn){
            if(!ShapeList.getSelectionModel().isEmpty()){
                int index = ShapeList.getSelectionModel().getSelectedIndex();
                Shape[] shapes = DrawingEngineSingleton.getInstance().getShapes();
                if (index >= 0 && index < shapes.length) {
                    shapes[index].setFillColor(ColorBox.getValue());
                    DrawingEngineSingleton.getInstance().refresh(CanvasBox);
                }
            }else{
                Message.setText("You need to pick a shape first to recolor it.");
            }
        }
        
        if(event.getSource()==MoveBtn){
            if(!ShapeList.getSelectionModel().isEmpty()){
                move=true;
                Message.setText("Click on the new top-left position below to move the selected shape.");
            }else{
                Message.setText("You need to pick a shape first to move it.");
            }
        }
        
        if(event.getSource()==CopyBtn){
            if(!ShapeList.getSelectionModel().isEmpty()){
                copy=true;
                Message.setText("Click on the new top-left position below to copy the selected shape.");
            }else{
                Message.setText("You need to pick a shape first to copy it.");
            }
        }
        
        if(event.getSource()==ResizeBtn){
            if(!ShapeList.getSelectionModel().isEmpty()){
                resize=true;
                Message.setText("Click on the new right-button position below to resize the selected shape.");
            }else{
                Message.setText("You need to pick a shape first to copy it.");
            }
        }
        
        if(event.getSource()==UndoBtn){
            DrawingEngineSingleton.getInstance().undo();
            DrawingEngineSingleton.getInstance().refresh(CanvasBox);
        }
        if(event.getSource()==RedoBtn){
            DrawingEngineSingleton.getInstance().redo();
            DrawingEngineSingleton.getInstance().refresh(CanvasBox);
        }
        
        if(event.getSource()==SaveBtn){
            showPathPane();
            save=true;
        }
        
        if(event.getSource()==LoadBtn){
            showPathPane();
            load=true;
        }
        
        if(event.getSource()==ImportBtn){
            showPathPane();
            importt=true;
        }
        
        if(event.getSource()==PathBtn){
            if(PathText.getText().isEmpty()){PathText.setText("You need to set the path of the file.");return;}
            if(save){
                save=false;
                DrawingEngineSingleton.getInstance().save(PathText.getText());
            }
            else if(load){
                load=false;
                DrawingEngineSingleton.getInstance().load(PathText.getText());
            }
            else if(importt){
                importt=false;
                DrawingEngineSingleton.getInstance().installPluginShape(PathText.getText());
                Message.setText("Not supported yet.");
            }
            hidePathPane();
        }
    }
    
    public void showPathPane(){
        Message.setVisible(false);
        PathPane.setVisible(true);
    }
    
    public void hidePathPane(){
        PathPane.setVisible(false);
        Message.setVisible(true);
    }
    
    public void startDrag(MouseEvent event){
        start = new Point2D(event.getX(),event.getY());
        Message.setText("");
    }
    public void endDrag(MouseEvent event) throws CloneNotSupportedException{
        end = new Point2D(event.getX(), event.getY());
        if(end.equals(start)){clickFunction();}else{dragFunction();}
    }
    
    public void clickFunction() throws CloneNotSupportedException{
        if(move){move=false;moveFunction();}
        else if(copy){copy=false;copyFunction();}
        else if(resize){resize=false;resizeFunction();}
    }
    
    public void moveFunction(){
        int index = ShapeList.getSelectionModel().getSelectedIndex();
        Shape[] shapes = DrawingEngineSingleton.getInstance().getShapes();
        if (index >= 0 && index < shapes.length) {
            shapes[index].setTopLeft(start);
            DrawingEngineSingleton.getInstance().refresh(CanvasBox);
        }
    }
    
    public void copyFunction() throws CloneNotSupportedException{
        int index = ShapeList.getSelectionModel().getSelectedIndex();
        Shape[] shapes = DrawingEngineSingleton.getInstance().getShapes();
        if (index >= 0 && index < shapes.length) {
            Shape temp = shapes[index].cloneShape();
            if(temp == null){System.out.println("Error cloning failed!");}
            else{
                DrawingEngineSingleton.getInstance().addShape(temp);
                Shape[] newShapes = DrawingEngineSingleton.getInstance().getShapes();
                newShapes[newShapes.length-1].setTopLeft(start);
                DrawingEngineSingleton.getInstance().refresh(CanvasBox);
            }
        }
    }
    
    public void resizeFunction(){
        int index = ShapeList.getSelectionModel().getSelectedIndex();
        Shape[] shapes = DrawingEngineSingleton.getInstance().getShapes();
        if (index >= 0 && index < shapes.length) {
            Color c = shapes[index].getFillColor();
            start = shapes[index].getTopLeft();
            Shape temp = new ShapeFactory().createShape(shapes[index].getClass().getSimpleName(),start,end,ColorBox.getValue());
            if(temp.getClass().getSimpleName().equals("Line")){
                Message.setText("Line doesn't support this command. Sorry :(");return;
            }
            DrawingEngineSingleton.getInstance().removeShape(shapes[index]);
            temp.setFillColor(c);
            DrawingEngineSingleton.getInstance().addShape(temp);
            DrawingEngineSingleton.getInstance().refresh(CanvasBox);
        }
    }
    
    public void dragFunction(){
        String type = ShapeBox.getValue();
        Shape sh;
        try{sh = new ShapeFactory().createShape(type,start,end,ColorBox.getValue());}catch(Exception e)
        {Message.setText("Don't be in a hurry! Choose a shape first :'D");return;}
        DrawingEngineSingleton.getInstance().addShape(sh);
        sh.draw(CanvasBox);
    }
    
    
    //Observer DP
    public ObservableList getStringList(){
        ObservableList<String> l = FXCollections.observableArrayList();
        Shape[] shapes = DrawingEngineSingleton.getInstance().getShapes();
        try{
            for(int i=0;i<shapes.length;i++){
                String temp = shapes[i].getClass().getSimpleName() + "  (" + (int) shapes[i].getTopLeft().getX() + "," + (int) shapes[i].getTopLeft().getY() + ")";
                l.add(temp);
            }
        }catch(Exception e){}
        return l;
    }
    
    public ArrayList<Shape> cloneList(ArrayList<Shape> l) throws CloneNotSupportedException{
        ArrayList<Shape> temp = new ArrayList<Shape>();
        for(int i=0;i<l.size();i++){
            temp.add(l.get(i).cloneShape());
        }
        return temp;
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    ObservableList<String> shapeList = FXCollections.observableArrayList();
    shapeList.add("Circle");
    shapeList.add("Ellipse");
    shapeList.add("Rectangle");
    shapeList.add("Square");
    shapeList.add("Triangle");
    shapeList.add("Line");
    ShapeBox.setItems(shapeList);
    ColorBox.setValue(Color.BLACK);
    }

    // Drawing engine methods removed. Use DrawingEngineSingleton.getInstance() instead.

    
    
}
