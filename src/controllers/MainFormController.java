package controllers;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import hash.DuplicateValues;
import hash.HashAlgorithms;
import hash.HashFolder;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class MainFormController implements Initializable {

	private File folderPath;
	private Map<String, String> fileHashes;

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Button ButtonOpenFolder;

	@FXML
	private Button ButtonHashFolder;

	@FXML
	private TextField TextFieldFolder;

	@FXML
	private Label LabelFolder;

	@FXML
	private ListView<String> ListViewFiles;

	@FXML
	private ComboBox<String> ComboBoxAlgorithm;

	@FXML
	private Button ButtonRemoveDuplicateFiles;

	@FXML
	private CheckBox CheckBoxIncludeSubfolders;

	@FXML
	private Label LabelAlgorithm;

	@FXML
	private Label LabelStatus;

	@FXML
	void onDragOverEvent(DragEvent event) {
		if (event.getGestureSource() != this.anchorPane && event.getDragboard().hasFiles()) {
			event.acceptTransferModes(TransferMode.COPY);
		}
		event.consume();
	}

	@FXML
	void onDragDroppedEvent(DragEvent event) {
		Dragboard dragboard = event.getDragboard();
		if (dragboard.hasFiles()) {
			String folderName = dragboard.getFiles().get(0).getAbsolutePath();
			this.folderPath = new File(folderName);
			if (this.folderPath.isDirectory()) {
				this.TextFieldFolder.setText(this.folderPath.getAbsolutePath());
				this.enableControls();
			} else {
				this.displayAlert(AlertType.ERROR, "You Selected A File!", ButtonType.OK);
			}
		}
		event.setDropCompleted(true);
		event.consume();
	}

	@FXML
	void openFolder(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select A Folder");
		this.folderPath = directoryChooser.showDialog(null);
		if (this.folderPath != null) {
			this.enableControls();
		} else {
			this.disableControls();
		}
	}

	@FXML
	void hashFolderFiles(ActionEvent event) {
		HashFolder hashFolder = new HashFolder(this.folderPath.toPath(), this.ComboBoxAlgorithm.getValue(),
				this.CheckBoxIncludeSubfolders.isSelected());
		hashFolder.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				taskFinishedHashFolder(hashFolder.getValue());
			}
		});
		hashFolder.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				taskFailedHashFolder(event);
			}
		});
		hashFolder.setOnRunning(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				taskRunningHashFolder();
			}
		});
		Thread thread = new Thread(hashFolder);
		thread.start();
	}

	@FXML
	void removeDuplicateFiles(ActionEvent event) {
		DuplicateValues duplicateValues = new DuplicateValues(this.fileHashes);
		duplicateValues.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				taskFinishedDuplicateValues(duplicateValues.getValue());
			}
		});
		duplicateValues.setOnRunning(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				taskRunningDuplicateValues();
			}
		});
		duplicateValues.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				taskFailedDuplicateValues(event);
			}
		});
		Thread thread = new Thread(duplicateValues);
		thread.start();
	}

	@FXML
	void comboBoxClicked(MouseEvent event) {
		this.fileHashes.clear();
		this.ListViewFiles.getItems().clear();
		this.ButtonHashFolder.setDisable(false);
		this.CheckBoxIncludeSubfolders.setDisable(false);
	}

	@FXML
	void checkBoxClicked(MouseEvent event) {
		this.fileHashes.clear();
		this.ButtonHashFolder.setDisable(false);
		this.ButtonRemoveDuplicateFiles.setDisable(true);
		this.ListViewFiles.getItems().clear();
	}

	@FXML
	void initialize() {
		assert ButtonOpenFolder != null : "fx:id=\"ButtonOpenFolder\" was not injected: check your FXML file 'MainForm.fxml'.";
		assert TextFieldFolder != null : "fx:id=\"TextFieldFolder\" was not injected: check your FXML file 'MainForm.fxml'.";
		assert LabelFolder != null : "fx:id=\"LabelFolder\" was not injected: check your FXML file 'MainForm.fxml'.";
		assert ListViewFiles != null : "fx:id=\"ListViewFiles\" was not injected: check your FXML file 'MainForm.fxml'.";
		assert ComboBoxAlgorithm != null : "fx:id=\"ComboBoxAlgorithm\" was not injected: check your FXML file 'MainForm.fxml'.";
		assert ButtonRemoveDuplicateFiles != null : "fx:id=\"ButtonRemoveDuplicateFiles\" was not injected: check your FXML file 'MainForm.fxml'.";
		assert ButtonHashFolder != null : "fx:id=\"ButtonHashFolder\" was not injected: check your FXML file 'MainForm.fxml'.";
		assert CheckBoxIncludeSubfolders != null : "fx:id=\"CheckBoxIncludeSubfolders\" was not injected: check your FXML file 'MainForm.fxml'.";
		assert LabelAlgorithm != null : "fx:id=\"LabelAlgorithm\" was not injected: check your FXML file 'MainForm.fxml'.";
		assert LabelStatus != null : "fx:id=\"LabelStatus\" was not injected: check your FXML file 'MainForm.fxml'.";
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.CheckBoxIncludeSubfolders.setDisable(true);
		this.ComboBoxAlgorithm.setDisable(true);
		this.ButtonHashFolder.setDisable(true);
		this.ButtonRemoveDuplicateFiles.setDisable(true);
		this.LabelStatus.setText("Ready");
		this.fillComboBox();
		this.fileHashes = new HashMap<String, String>();
	}

	private void fillComboBox() {
		this.ComboBoxAlgorithm.getItems().add(HashAlgorithms.getMD5());
		this.ComboBoxAlgorithm.getItems().add(HashAlgorithms.getSHA1());
		this.ComboBoxAlgorithm.getItems().add(HashAlgorithms.getSHA2_224());
		this.ComboBoxAlgorithm.getItems().add(HashAlgorithms.getSHA2_256());
		this.ComboBoxAlgorithm.getItems().add(HashAlgorithms.getSHA2_384());
		this.ComboBoxAlgorithm.getItems().add(HashAlgorithms.getSHA2_512());
		this.ComboBoxAlgorithm.getItems().add(HashAlgorithms.getSHA3_224());
		this.ComboBoxAlgorithm.getItems().add(HashAlgorithms.getSHA3_256());
		this.ComboBoxAlgorithm.getItems().add(HashAlgorithms.getSHA3_384());
		this.ComboBoxAlgorithm.getItems().add(HashAlgorithms.getSHA3_512());
		this.ComboBoxAlgorithm.setValue(this.ComboBoxAlgorithm.getItems().get(9));
		this.TextFieldFolder.setEditable(false);
		this.ButtonHashFolder.setDisable(true);
		this.ButtonRemoveDuplicateFiles.setDisable(true);
	}

	private void taskFinishedHashFolder(Map<String, String> fileHashes) {
		this.ButtonOpenFolder.setDisable(false);
		this.ButtonRemoveDuplicateFiles.setDisable(false);
		this.ComboBoxAlgorithm.setDisable(false);
		this.fileHashes = fileHashes;
		this.fillListViewWithFileHashes(this.fileHashes);
		this.LabelStatus.setText("Ready");
	}

	private void taskRunningHashFolder() {
		this.ButtonOpenFolder.setDisable(true);
		this.ButtonRemoveDuplicateFiles.setDisable(true);
		this.ComboBoxAlgorithm.setDisable(true);
		this.ButtonHashFolder.setDisable(true);
		this.CheckBoxIncludeSubfolders.setDisable(true);
		this.LabelStatus.setText("Working");
	}

	private void taskFailedHashFolder(WorkerStateEvent e) {
		this.ButtonOpenFolder.setDisable(false);
		this.ComboBoxAlgorithm.setDisable(true);
		this.ButtonHashFolder.setDisable(true);
		this.ButtonRemoveDuplicateFiles.setDisable(true);
		this.CheckBoxIncludeSubfolders.setDisable(true);
		this.ListViewFiles.getItems().clear();
		this.fileHashes.clear();
		this.displayAlert(AlertType.ERROR, e.getSource().getException().getMessage(), ButtonType.OK);
		this.LabelStatus.setText("Ready");
	}

	private void taskRunningDuplicateValues() {
		this.ButtonOpenFolder.setDisable(true);
		this.ButtonHashFolder.setDisable(true);
		this.ButtonRemoveDuplicateFiles.setDisable(true);
		this.CheckBoxIncludeSubfolders.setDisable(true);
		this.ComboBoxAlgorithm.setDisable(true);
		this.LabelStatus.setText("Working");
	}

	private void taskFinishedDuplicateValues(Map<String, List<String>> map) {
		this.ButtonHashFolder.setDisable(true);
		this.ButtonRemoveDuplicateFiles.setDisable(true);
		this.ButtonOpenFolder.setDisable(false);
		this.fillListViewAfterRemovingDuplicates(map);
		this.LabelStatus.setText("Ready");
	}

	private void taskFailedDuplicateValues(WorkerStateEvent e) {
		this.ButtonHashFolder.setDisable(true);
		this.ButtonRemoveDuplicateFiles.setDisable(true);
		this.ButtonOpenFolder.setDisable(false);
		this.ComboBoxAlgorithm.setDisable(true);
		this.CheckBoxIncludeSubfolders.setDisable(true);
		this.displayAlert(AlertType.ERROR, e.getSource().getException().getMessage(), ButtonType.OK);
		this.LabelStatus.setText("Ready");
	}

	private void fillListViewAfterRemovingDuplicates(Map<String, List<String>> map) {
		this.ListViewFiles.getItems().clear();
		for (Entry<String, List<String>> entry : map.entrySet()) {
			String lineToAdd = entry.getValue().get(0) + " " + entry.getKey();
			this.ListViewFiles.getItems().add(lineToAdd);
		}
	}

	private void fillListViewWithFileHashes(Map<String, String> map) {
		this.ListViewFiles.getItems().clear();
		for (Entry<String, String> entry : map.entrySet()) {
			String lineToAdd = entry.getKey() + " " + entry.getValue();
			this.ListViewFiles.getItems().add(lineToAdd);
		}
	}

	private void disableControls() {
		this.TextFieldFolder.setText(null);
		this.ButtonHashFolder.setDisable(true);
		this.ButtonRemoveDuplicateFiles.setDisable(true);
		this.CheckBoxIncludeSubfolders.setDisable(true);
		this.ComboBoxAlgorithm.setDisable(true);
		this.ListViewFiles.getItems().clear();
	}

	private void enableControls() {
		this.ButtonHashFolder.setDisable(false);
		this.CheckBoxIncludeSubfolders.setDisable(false);
		this.ComboBoxAlgorithm.setDisable(false);
		this.TextFieldFolder.setText(this.folderPath.getAbsolutePath());
		this.ListViewFiles.getItems().clear();
	}

	private void displayAlert(AlertType alertType, String alertMessage, ButtonType buttonType) {
		Alert alert = new Alert(alertType, alertMessage, buttonType);
		alert.show();
	}
}