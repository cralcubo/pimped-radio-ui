package bo.roman.radio.ui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.entities.Category;
import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.ui.business.tuner.CategoryManager;
import bo.roman.radio.ui.view.initializers.TunerLayoutInitializer;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class CategoryCreatorEditorController {
	private final Logger logger = LoggerFactory.getLogger(CategoryCreatorEditorController.class);
	
	private static final String CREATE_TITLE = "Create Category";
	private static final String CREATEBUTTON_TEXT = "Create";
	private static final String EDITBUTTON_TEXT = "Save";
	private static final String EDIT_TITLE = "Edit Category";

	private TunerLayoutInitializer rootInitializer;
	
	private CategoryManager categoryManager;
	private boolean isCreator;
	private Stage stage;
	
	@FXML
	private TextArea textArea;

	@FXML
	Button persistButton;

	private Category category;
	
	@FXML
	private void initialize() {
		categoryManager = CategoryManager.getInstance();
	}

	@FXML
	public void persistAction() {
		if (isCreator) {
			LoggerUtils.logDebug(logger, () -> "Pop create category up");
			createAction();
		}
		else {
			LoggerUtils.logDebug(logger, () -> "Pop edit category up");
			saveAction();
		}
			
	}
	
	private void saveAction() {
		// Check if the name of the category has changed
		if(!category.getName().equals(textArea.getText()))
		{
			category.setName(textArea.getText());
			try {
				categoryManager.updateCategory(category);
			} catch (TunerPersistenceException e) {
				logger.error("There was an error updating {}", category, e);
			}
		}
		stage.close();
	}

	private void createAction() {
		try {
			// Do the creation of a new Category
			Category category = categoryManager.createCategory(new Category(textArea.getText()));

			// Clear textArea
			clearTextArea();

			// Update StationOverview with new category
			LoggerUtils.logDebug(logger, () -> "Creating new category in UI: " + category.getName());
			rootInitializer.loadStationsOverview();
		} catch (TunerPersistenceException e) {
			// Display an error message here
			logger.error("There was an error creating a new Category.", e);
		}
	}

	public void clearTextArea() {
		textArea.clear();
	}
	
	public void setRootInitializer(TunerLayoutInitializer rootInitializer) {
		this.rootInitializer = rootInitializer;
	}

	public void setMode(boolean isCreator) {
		this.isCreator = isCreator;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void initializeLabels() {
		persistButton.setText(isCreator ? CREATEBUTTON_TEXT : EDITBUTTON_TEXT);
		stage.setTitle(isCreator ? CREATE_TITLE : EDIT_TITLE);
	}

	public void setCategory(Category c) {
		this.category = c;
		textArea.setText(c.getName());
	}

}
