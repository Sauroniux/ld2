package com.example.ld1.fxControllers;

import com.example.ld1.data.*;
import com.example.ld1.dbManagers.*;
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
import java.util.List;
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

    private final ContextMenu treeContextMenu = new ContextMenu();
    private final ContextMenu listContextMenu = new ContextMenu();

    private Course selectedCourse = null;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        init();
    }

    public void init()
    {
        User currentUser = UserDbManager.getInstance().getCurrentUser();
        createCourseMenuItem.setDisable(currentUser.getAccountType() == AccountType.Person);

        InitCourses();
        InitTree();
    }

    private void InitCourses()
    {
        ObservableList<Course> viewableListContent = FXCollections.observableArrayList();
        ObservableList<Course> moderatedListContent = FXCollections.observableArrayList();

        User currentUser = UserDbManager.getInstance().getCurrentUser();

        if(currentUser == null)
        {
            SceneManager.ShowError("Current user is uninitialized");
            return;
        }

        boolean isAdmin = currentUser.isAdmin();

        List<Course> viewedCourses;
        List<Course> moderatedCourses;

        if(isAdmin)
        {
            viewedCourses = null;
            moderatedCourses = CourseDbManager.getInstance().GetAllCourses();
        }
        else
        {
            viewedCourses = RelationshipDbManager.getInstance().GetViewedCoursesObjects(currentUser);
            moderatedCourses = RelationshipDbManager.getInstance().GetModeratedCoursesObjects(currentUser);
        }

        viewableListContent.addAll(viewedCourses);
        moderatedListContent.addAll(moderatedCourses);

        viewableCourseList.setItems(viewableListContent);
        moderatedCourseList.setItems(moderatedListContent);

        viewableCourseList.setCellFactory(renderCourseItem());
        moderatedCourseList.setCellFactory(renderCourseItem());

        viewableCourseList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Course>() {
            @Override
            public void changed(ObservableValue<? extends Course> observable, Course oldValue, Course newValue) {
                selectedCourse = newValue;
                InitTree();
                expandAll();
            }
        });

        moderatedCourseList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Course>() {
            @Override
            public void changed(ObservableValue<? extends Course> observable, Course oldValue, Course newValue) {
                selectedCourse = newValue;
                InitTree();
                expandAll();
            }
        });

        moderatedCourseList.addEventHandler(MouseEvent.MOUSE_RELEASED, e->{
            if (e.getButton() == MouseButton.SECONDARY)
            {
                var selected = moderatedCourseList.getSelectionModel().getSelectedItem();

                if (selected!=null)
                    openListContextMenu(selected, e.getScreenX(), e.getScreenY());
            }
            else
            {
                treeContextMenu.hide();
            }
        });
    }

    private void InitTree()
    {
        Course course = selectedCourse;

        if(course == null)
            return;

        int rootFolderId = course.getRootFolderId();
        Folder rootFolder = FolderDbManager.getInstance().GetFolder(rootFolderId);
        var subFolders = rootFolder.getSubFolders();

        TreeItem<FileSystemItem> rootItem = new TreeItem<FileSystemItem>(rootFolder);

        for(var folder : subFolders)
            AddSubFolder(folder, rootItem);

        AddFiles(rootFolder, rootItem);

        treeView.setRoot(rootItem);

        treeView.setCellFactory(getRenderTreeItem());

        treeView.addEventHandler(MouseEvent.MOUSE_RELEASED, e->{
            if (e.getButton() == MouseButton.SECONDARY) {
                TreeItem<FileSystemItem> selected = treeView.getSelectionModel().getSelectedItem();

                if (selected != null)
                    openTreeContextMenu(selected, e.getScreenX(), e.getScreenY());
            }
            else
            {
                treeContextMenu.hide();
            }
        });
    }

    private void RefreshTree()
    {
        int viewableListIndex = viewableCourseList.getSelectionModel().getSelectedIndex();
        int moderatedListIndex = moderatedCourseList.getSelectionModel().getSelectedIndex();
        var selectedFileItem = treeView.getSelectionModel().getSelectedItem();

        init();

        viewableCourseList.getSelectionModel().select(viewableListIndex);
        moderatedCourseList.getSelectionModel().select(moderatedListIndex);
        treeView.getSelectionModel().select(selectedFileItem);
    }

    private void CreateFolder(Folder parent)
    {
        String input = GetNameInput(true);

        if(input.isEmpty())
        {
            SceneManager.ShowError("Folder name cannot be empty!");
            return;
        }

        Folder newFolder = new Folder(input, parent.getId());

        FolderDbManager.getInstance().CreateFolder(newFolder);

        RefreshTree();
    }

    private void CreateFile(Folder parent)
    {
        String input = GetNameInput(false);

        if(input.isEmpty())
        {
            SceneManager.ShowError("File name cannot be empty!");
            return;
        }

        File newFile = new File(parent.getId(), input, "");

        FileDbManager.getInstance().CreateFile(newFile);

        RefreshTree();
    }

    private void GiveViewingAccess(Course course)
    {
        String input = GetUsernameInput();
        User user = UserDbManager.getInstance().GetByUsername(input);

        if(user == null)
        {
            SceneManager.ShowError("Username wasn't found!");
            return;
        }

        RelationshipDbManager.getInstance().AddCourseViewer(user, course);
    }

    private void GiveModeratorAccess(Course course)
    {
        String input = GetUsernameInput();
        User user = UserDbManager.getInstance().GetByUsername(input);

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

    private void AddSubFolder(Folder folder, TreeItem<FileSystemItem> parentNode)
    {
        var children = parentNode.getChildren();

        children.add(new TreeItem<>(folder));

        var index = children.size() - 1;
        AddFiles(folder, children.get(index));

        var subFolders = RelationshipDbManager.getInstance().GetChildFolders(folder);

        for(var child : subFolders)
            AddSubFolder(child, children.get(index));
    }

    private void AddFiles(Folder folder, TreeItem<FileSystemItem> parentNode)
    {
        var files = folder.getFolderFiles();

        if(files == null || files.size() == 0)
            return;

        for(var file : files)
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

    public void onClickLogOut(ActionEvent actionEvent) throws IOException
    {
        UserDbManager.getInstance().setCurrentUser(null);
        SceneManager.LoadScene(WindowResource.login);
    }

    public void onClickDeleteUser(ActionEvent actionEvent)
    {
        User user = UserDbManager.getInstance().getCurrentUser();

        UserDbManager.getInstance().DeleteUser(user.getId());
    }

    //region Rendering, context menus, misc...

    Callback<ListView<Course>, ListCell<Course>> renderCourseItem()
    {
        return param -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getTitle() == null)
                {
                    setText(null);
                    return;
                }

                setText(item.getTitle());
            }
        };
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
            if(item.isFile())
            {
                File file = item.as(File.class);
                FileDbManager.getInstance().DeleteFile(file.getId());
                InitTree();
            }
            else if(item.isFolder())
            {
                Folder folder = item.as(Folder.class);
                FolderDbManager.getInstance().DeleteFolder(folder.getId());
                InitTree();
            }
        });

        var currentUser = UserDbManager.getInstance().getCurrentUser();
        var moderatedCourses = RelationshipDbManager.getInstance().GetModeratedCoursesIds(currentUser);
        var doesModerate = moderatedCourses.contains(selectedCourse.getId());

        if(item.isFolder() && doesModerate)
        {
            treeContextMenu.getItems().add(createFolder);
            treeContextMenu.getItems().add(createFile);
        }

        if(doesModerate)
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
            CourseDbManager.getInstance().DeleteCourse(course.getId());
            init();
        });

        listContextMenu.getItems().add(giveViewingAccess);
        listContextMenu.getItems().add(giveModeratorAccess);

        User currentUser = UserDbManager.getInstance().getCurrentUser();
        var ownedCourses = RelationshipDbManager.getInstance().GetOwnedCoursesIds(currentUser);
        var doesOwn = ownedCourses.contains(course.getId());

        if(doesOwn)
            listContextMenu.getItems().add(deleteCourse);
    }

    private Callback<TreeView<FileSystemItem>,TreeCell<FileSystemItem>> getRenderTreeItem()
    {
        return new Callback<TreeView<FileSystemItem>,TreeCell<FileSystemItem>>(){
            @Override
            public TreeCell<FileSystemItem> call(TreeView<FileSystemItem> p) {
                TreeCell<FileSystemItem> cell = new TreeCell<FileSystemItem>() {
                    @Override
                    protected void updateItem(FileSystemItem file, boolean empty) {
                        super.updateItem(file, empty);

                        if (!empty)
                        {
                            setText(file.getDisplayName());

                            if(file.isFile())
                                setTextFill(GetColor(File.class.cast(file).getFileExtension()));
                        }
                        else
                            setText(null);
                    }
                };
                return cell;
            }
        };
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

    //endregion
}
