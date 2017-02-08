package bo.roman.radio.ui.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.entities.Category;
import bo.radio.tuner.entities.Station;
import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.ui.business.AddEditButtonManager;
import bo.roman.radio.ui.business.RadioDisplayerManager;
import bo.roman.radio.ui.business.RadioPlayerManager;
import bo.roman.radio.ui.business.StationPlayingManager;
import bo.roman.radio.ui.business.tuner.CategoryManager;
import bo.roman.radio.ui.business.tuner.StationManager;
import bo.roman.radio.ui.business.tuner.TunerManager;
import bo.roman.radio.ui.view.initializers.TunerLayoutInitializer;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;

public class StationsOverviewController {
	private final Logger logger = LoggerFactory.getLogger(StationsOverviewController.class);

	@FXML
	private Accordion accordion;

	@FXML
	private MenuItem addCategory;

	private final StationManager stationManager;
	private final CategoryManager categoryManager;
	private final TunerManager tunerManager;

	private TunerLayoutInitializer rootInitializer;

	public StationsOverviewController() {
		stationManager = StationManager.getInstance();
		tunerManager = TunerManager.getInstance();
		categoryManager = CategoryManager.getInstance();
	}

	@FXML
	private void initialize() {
		// Load categories and station
		loadStations();
	}

	@SuppressWarnings("unchecked")
	public void loadStations() {
		accordion.getPanes().clear();
		tunerManager.sortCategories();
		
		tunerManager.getTunerTable().forEach(tp -> {
			addStationEvents((ListView<Station>) tp.getContent());
			addTitledPaneEvents(tp);
			accordion.getPanes().add(tp);
		});
	}

	private void addTitledPaneEvents(TitledPane tp) {
		LoggerUtils.logDebug(logger, () -> String.format("Adding MouseEvents to the TitledPane [%s]", tp));
		tp.setOnMouseClicked(mouseEvent -> {
			if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
				ContextMenu menu = new ContextMenu();
				MenuItem delete = new MenuItem("Delete Category");
				delete.setOnAction(ae -> {
					deleteCategory(tp.getText());
				});
				
				MenuItem edit = new MenuItem("Edit Category");
				edit.setOnAction(ae -> {
					editCategory(tp.getText());
				});

				menu.getItems().addAll(edit, delete);
				tp.setContextMenu(menu);
			}
		});
	}

	private void editCategory(String name) {
		LoggerUtils.logDebug(logger, () -> "Triggering: Edit Category");
		try {
			Optional<Category> oc = categoryManager.findCategoryByName(name);
			if (oc.isPresent()) {
				rootInitializer.showEditCategory(oc.get());
				loadStations();
			} else {
				logger.error("The Category {} could not be found in the DB", name);
			}
		} catch (TunerPersistenceException e) {
			logger.error("There was an error when updating the Category [{}].", name, e);
		}
	}

	private void deleteCategory(String name) {
		try {
			categoryManager.deleteCategory(name);
			loadStations();
		} catch (TunerPersistenceException e) {
			logger.error("There was an error when deleting the Category [{}].", name, e);
		}
	}

	private void addStationEvents(ListView<Station> stationsTable) {
		LoggerUtils.logDebug(logger, () -> String.format("Adding MouseEvents to the StationTable [%s]", stationsTable));
		stationsTable.setOnMouseClicked((mouseEvent) -> {
			LoggerUtils.logDebug(logger, () -> "Station selected=" + stationsTable.getSelectionModel().getSelectedItem());
			
			// Trigger an option Menu to Edit | Delete the Station selected
			if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
				ContextMenu menu = new ContextMenu();
				MenuItem edit = new MenuItem("Edit Station");
				edit.setOnAction((actionEvent) -> {
					editStationAction(stationsTable.getSelectionModel().getSelectedItem());
				});

				MenuItem delete = new MenuItem("Delete Station");
				delete.setOnAction(ae -> {
					deleteStationAction(stationsTable.getSelectionModel().getSelectedItem());
				});

				menu.getItems().addAll(edit, delete);
				stationsTable.setContextMenu(menu);
			}
			// Play the Station selected on Double click
			if (mouseEvent.getClickCount() == 2) {
				Station s = stationsTable.getSelectionModel().getSelectedItem();
				StationPlayingManager.setCurrentStationPlaying(s);
				RadioPlayerManager rpm = RadioPlayerManager.getInstance();
				AddEditButtonManager aebm = AddEditButtonManager.getInstance();
				rpm.stop();
				RadioDisplayerManager.getInstance().reloadUI();

				// Play Radio
				rpm.play(s);
				// Enable the Edit Station button
				aebm.enableEdit();
			}

		});
	}

	private void deleteStationAction(Station s) {
		try {
			stationManager.deleteStation(s);
		} catch (TunerPersistenceException e) {
			logger.error("There was an error when deleting the station [{}].", s, e);
		}
	}

	private void editStationAction(Station s) {
		LoggerUtils.logDebug(logger, () -> "Triggering: Edit Station");
		rootInitializer.showEditStation(s);
	}

	@FXML
	public void addCategoryAction() {
		LoggerUtils.logDebug(logger, () -> "Triggering: Add New Category");
		rootInitializer.showAddCategory();
	}

	public void setRootInitializer(TunerLayoutInitializer rootInitializer) {
		this.rootInitializer = rootInitializer;
	}

}
