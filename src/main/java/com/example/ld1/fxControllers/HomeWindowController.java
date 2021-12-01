package com.example.ld1.fxControllers;

import com.example.ld1.data.*;
import com.example.ld1.dbManagers.DbManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.ResourceBundle;

public class HomeWindowController implements Initializable
{
    @FXML
    private ListView<Course> viewableCourseList;
    @FXML
    private ListView<Course> moderatedCourseList;
    @FXML
    private TreeView<FileSystemItem> treeView;

    @FXML
    private MenuItem createCourseMenuItem;

    private Set<Course> viewableCourses;
    private Set<Course> moderatedCourses;

    private final ContextMenu treeContextMenu = new ContextMenu();
    private final ContextMenu listContextMenu = new ContextMenu();

    private Course selectedCourse = null;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        createCourseMenuItem.setDisable(DbManager.getInstance().getCurrentUser().isPerson());
        init();
    }

    private void init()
    {
        initData();

        viewableCourseList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Course>() {
            @Override
            public void changed(ObservableValue<? extends Course> observable, Course oldValue, Course newValue) {
                selectedCourse = newValue;
                initTree(newValue);
                expandAll();
            }
        });

        moderatedCourseList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Course>() {
            @Override
            public void changed(ObservableValue<? extends Course> observable, Course oldValue, Course newValue) {
                selectedCourse = newValue;
                initTree(newValue);
                expandAll();
            }
        });
    }

    public void initData()
    {
        fetchAllCourses();

        ObservableList<Course> viewableListContent = FXCollections.observableArrayList();
        ObservableList<Course> moderatedListContent = FXCollections.observableArrayList();

        if(viewableCourses != null)
        {
            for(var course : viewableCourses)
                if(moderatedCourses != null && !moderatedCourses.contains(course))
                    viewableListContent.add(course);
        }
        if (moderatedCourses != null)
        {
            moderatedListContent.addAll(moderatedCourses);
        }

        viewableCourseList.setItems(viewableListContent);
        moderatedCourseList.setItems(moderatedListContent);

        Callback<ListView<Course>, ListCell<Course>> callback = param -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getName() == null)
                {
                    setText(null);
                    return;
                }

                setText(item.getName());
            }
        };

        viewableCourseList.setCellFactory(callback);

        moderatedCourseList.setCellFactory(callback);
    }

    private void fetchAllCourses()
    {
        DbManager.getInstance().UpdateCurrentUserFromDB();

        viewableCourses = DbManager.getInstance().getViewableCourses();
        moderatedCourses = DbManager.getInstance().getModeratedCourses();
    }

    private void initTree(Course course)
    {
        if(course == null)
            return;

        TreeItem<FileSystemItem> root = new TreeItem<>(course.getRootFolder());

        for(var folder : course.getRootFolder().getSubFolders())
        {
            AddSubFolder(folder, root);
        }

        AddFiles(course.getRootFolder(), root);

        treeView.setRoot(root);

        treeView.setCellFactory(new Callback<TreeView<FileSystemItem>,TreeCell<FileSystemItem>>(){
            @Override
            public TreeCell<FileSystemItem> call(TreeView<FileSystemItem> p) {
                TreeCell<FileSystemItem> cell = new TreeCell<FileSystemItem>() {
                    @Override
                    protected void updateItem(FileSystemItem file, boolean empty) {
                        super.updateItem(file, empty);
                        if (empty)
                        {
                            setText(null);
                        }
                        else
                        {
                            setText(file.getName());

                            if(file.isFile())
                                setTextFill(GetColor(file.as(File.class).getFileExtension()));
                        }
                    }
                };
                return cell;
            }
        });

        treeView.addEventHandler(MouseEvent.MOUSE_RELEASED, e->{
            if (e.getButton() == MouseButton.SECONDARY) {
                TreeItem<FileSystemItem> selected = treeView.getSelectionModel().getSelectedItem();

                if (selected!=null)
                {
                    openTreeContextMenu(selected, e.getScreenX(), e.getScreenY());
                }
            }
            else
            {
                treeContextMenu.hide();
            }
        });

        moderatedCourseList.addEventHandler(MouseEvent.MOUSE_RELEASED, e->{
            if (e.getButton() == MouseButton.SECONDARY) {
                var selected = moderatedCourseList.getSelectionModel().getSelectedItem();

                if (selected!=null)
                {
                    openListContextMenu(selected, e.getScreenX(), e.getScreenY());
                }
            }
            else
            {
                treeContextMenu.hide();
            }
        });
    }

    private void openTreeContextMenu(TreeItem<FileSystemItem> item, double x, double y) {
        createTreeContextMenu(item);
        treeContextMenu.show(treeView, x, y);
    }

    private void openListContextMenu(Course course, double x, double y) {
        createListContextMenu(course);
        listContextMenu.show(moderatedCourseList, x, y);
    }

    private void createTreeContextMenu(TreeItem<FileSystemItem> cell) {
        FileSystemItem item = cell.getValue();

        if(item == null)
            return;

        treeContextMenu.getItems().clear();

        MenuItem createFolder = new MenuItem("Create Folder");
        MenuItem createFile = new MenuItem("Create File");
        MenuItem delete = new MenuItem("Delete");

        createFolder.setOnAction(event -> {
                CreateFolder(item.as(Folder.class));
        });

        createFile.setOnAction(event -> {
                CreateFile(item.as(Folder.class));
        });

        delete.setOnAction(event -> {
            DbManager.getInstance().DeleteFileSystemItem(item);
        });

        if(item.isFolder() && DbManager.getInstance().getCurrentUser().DoesModerateCourse(selectedCourse))
        {
            treeContextMenu.getItems().add(createFolder);
            treeContextMenu.getItems().add(createFile);
        }

        Company currentCompany = DbManager.getInstance().getCurrentCompany();

        if(currentCompany != null && currentCompany.getId() == item.getOwner().getId())
            treeContextMenu.getItems().add(delete);

    }

    private void createListContextMenu(Course course)
    {
        if(course == null)
            return;

        listContextMenu.getItems().clear();

        MenuItem giveViewingAccess = new MenuItem("Give viewing access");
        MenuItem giveModeratorAccess = new MenuItem("Give moderator access");
        MenuItem deleteCourse = new MenuItem("Delete");

        giveViewingAccess.setOnAction(event -> {
            GiveViewingAccess(course);
        });

        giveModeratorAccess.setOnAction(event -> {
            GiveModeratorAccess(course);
        });

        deleteCourse.setOnAction(event -> {
            DbManager.getInstance().DeleteCourse(course);
        });

        listContextMenu.getItems().add(giveViewingAccess);
        listContextMenu.getItems().add(giveModeratorAccess);

        Company currentCompany = DbManager.getInstance().getCurrentCompany();

        if(currentCompany != null && currentCompany.IsCreatorOfTheCourse(course))
            listContextMenu.getItems().add(deleteCourse);
    }

    private void CreateFolder(Folder parent)
    {
        String input = GetNameInput(true);

        if(input.isEmpty())
        {
            SceneManager.ShowError("Folder name cannot be empty!");
            return;
        }

        Folder newFolder = new Folder(parent, input, null);

        DbManager.getInstance().CreateT(newFolder);

        refreshTree();
    }

    private void CreateFile(Folder parent)
    {
        String input = GetNameInput(false);

        if(input.isEmpty())
        {
            SceneManager.ShowError("File name cannot be empty!");
            return;
        }

        File newFile = new File(parent, input, "");

        DbManager.getInstance().CreateT(newFile);

        refreshTree();
    }

    private void GiveViewingAccess(Course course)
    {
        String input = GetUsernameInput();
        BaseUser person = DbManager.getInstance().GetByUsername(input);

        if(person == null || person.isCompany())
        {
            SceneManager.ShowError("Username wasn't found!");
            return;
        }

        course.AppendViewer(person.as(Person.class));
    }

    private void GiveModeratorAccess(Course course)
    {
        String input = GetUsernameInput();
        BaseUser user = DbManager.getInstance().GetByUsername(input);

        if(user == null)
        {
            SceneManager.ShowError("Username wasn't found!");
            return;
        }

        course.AppendModerator(user);
    }

    private String GetNameInput(boolean isFolder)
    {
        TextInputDialog td = new TextInputDialog((isFolder ? "Folder" : "File") + " name...");

        td.setHeaderText("Enter " + (isFolder ? "Folder" : "File") + " name");

        td.showAndWait();

        return td.getEditor().getText();

    }

    private String GetUsernameInput()
    {
        TextInputDialog td = new TextInputDialog("username...");

        td.setHeaderText("Enter username");

        td.showAndWait();

        return td.getEditor().getText();

    }

    private static Color GetColor(String fileName)
    {
        int hash = fileName.hashCode();
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = hash & 0x0000FF;

        return new Color(
                Remap(r, 0, 255, 0d, 1d),
                Remap(g, 0, 255, 0d, 1d),
                Remap(b, 0, 255, 0d, 1d),
                1);
    }

    private static double Remap(int value, int low1, int high1, double low2, double high2)
    {
        return low2 + ((double)value - (double)low1) * (high2 - low2) / ((double)high1 - (double)low1);
    }


    private void AddSubFolder(Folder folder, TreeItem<FileSystemItem> parentNode)
    {
        var children = parentNode.getChildren();

        children.add(new TreeItem<>(folder));

        if(folder.IsLeaf())
            return;

        var index = children.size() - 1;

        for(var child : folder.getSubFolders())
            AddSubFolder(child, children.get(index));

        AddFiles(folder, parentNode);
    }

    private void AddFiles(Folder folder, TreeItem<FileSystemItem> parentNode)
    {
        if(folder.getFolderFiles() == null)
            return;

        for(var file : folder.getFolderFiles())
            parentNode.getChildren().add(new TreeItem<>(file));
    }

    public void onCreateCourseClick(ActionEvent actionEvent) throws IOException
    {
        CreateCourseWindowController.CreateCourseDialog(this);
    }

    public void onClickRefresh(ActionEvent actionEvent)
    {
        init();
    }

    private void expandTreeView(TreeItem<?> item){
        if(item != null && !item.isLeaf()){
            item.setExpanded(true);
            for(TreeItem<?> child:item.getChildren()){
                expandTreeView(child);
            }
        }
    }

    private void expandAll()
    {
        expandTreeView(treeView.getRoot());
    }

    private void refreshTree()
    {
        int viewableListIndex = viewableCourseList.getSelectionModel().getSelectedIndex();
        int moderatedListIndex = moderatedCourseList.getSelectionModel().getSelectedIndex();
        var selectedFileItem = treeView.getSelectionModel().getSelectedItem();

        init();

        viewableCourseList.getSelectionModel().select(viewableListIndex);
        moderatedCourseList.getSelectionModel().select(moderatedListIndex);
        treeView.getSelectionModel().select(selectedFileItem);
    }

    public void onClickLogOut(ActionEvent actionEvent) throws IOException
    {
        DbManager.getInstance().setCurrentUser(null);
        SceneManager.LoadScene(WindowResource.login);
    }

    public void onClickDeleteUser(ActionEvent actionEvent)
    {
        DbManager.getInstance().DeleteCurrentUser();
    }
}
