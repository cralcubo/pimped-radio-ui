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

public class CategoryCreatorController {
	private final Logger logger = LoggerFactory.getLogger(CategoryCreatorController.class);

	private TunerLayoutInitializer rootInitializer;
	
	
	private CategoryManager categoryManager;

	@FXML
	private TextArea textArea;

	@FXML
	Button createButton;
	
	@FXML
	private void initialize() {
		categoryManager = CategoryManager.getInstance();
	}

	@FXML
	public void createAction() {
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
}
