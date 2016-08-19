package bo.roman.radio.ui.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.entities.Category;
import bo.radio.tuner.entities.Station;
import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.business.AddEditButtonManager;
import bo.roman.radio.ui.business.RadioDisplayerManager;
import bo.roman.radio.ui.business.RadioPlayerManager;
import bo.roman.radio.ui.business.StationPlayingManager;
import bo.roman.radio.ui.business.tuner.CategoryManager;
import bo.roman.radio.ui.controller.util.CodecFormatter;
import bo.roman.radio.ui.view.initializers.TunerLayoutInitializer;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;

public class StationsOverviewController implements Initializable {
	private final Logger logger = LoggerFactory.getLogger(StationsOverviewController.class);
	
	@FXML
	private Accordion accordion;
	
	@FXML
	private MenuItem addCategory;
	
	private final CategoryManager categoryManager;

	private TunerLayoutInitializer rootInitializer;
	
	public StationsOverviewController() {
		categoryManager = CategoryManager.getInstance();
	}
	
	@FXML
	public void initialize() {
		// Load categories and station
		loadStations();
	}

	public void loadStations() {
		try {
			List<Category> allCategories = categoryManager.getAllCategories();
			LoggerUtils.logDebug(logger, () -> "Loading Categories=" + allCategories);
			
			List<TitledPane> allPanes = accordion.getPanes(); 
			List<String> paneNames = allPanes.stream()
											.map(TitledPane::getText)
											.collect(Collectors.toList());
			
			// Add new stations in the existent TitledPanes
			for(TitledPane tp : allPanes) {
				Category c = categoryManager.findCategoryByName(tp.getText()).get();
				@SuppressWarnings("unchecked")
				TableView<Station> stationsTable = (TableView<Station>) tp.getContent();
				if(stationsTable == null) {
					stationsTable = createStationTable(c.getStations());
					tp.setContent(stationsTable);
				} else {
					// Set the missing stations
					ObservableList<Station> observableStations = stationsTable.getItems();
					List<Station> existentStations = observableStations.subList(0, observableStations.size());
					List<Station> newStations = c.getStations().stream()
																.filter(s -> !existentStations.contains(s))
																.collect(Collectors.toList());
					
					LoggerUtils.logDebug(logger, () -> "Adding new stations: " + newStations);
					for(Station s : newStations) {
						stationsTable.getItems().add(s);
					}
				}
				
			}
			
			// Add new Categories
			List<Category> newCategories = allCategories.stream()
														.filter(c -> !paneNames.contains(c.getName()))
														.collect(Collectors.toList());
			
			LoggerUtils.logDebug(logger, () -> "Adding new Categories: " + newCategories);
			for(Category c : newCategories) {
				// Create a new TitledPane
				TitledPane tp = new TitledPane();
				tp.setText(c.getName());
				// Create a new Table
				TableView<Station> stationsTable = createStationTable(c.getStations());
				tp.setContent(stationsTable);
				accordion.getPanes().add(tp);
			}
			
		} catch (TunerPersistenceException e) {
			// TODO display a warning error message on UI
			logger.error("There was an error connecting to the database requesting Categories.", e);
			accordion.setDisable(true);
		}
	}

	private TableView<Station> createStationTable(List<Station> stations) {
		TableView<Station> stationsTable = new TableView<>();
		stationsTable.setItems(FXCollections.observableList(stations));
		
		TableColumn<Station, String> stationName = new TableColumn<>("Radio Name");
		stationName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		
		TableColumn<Station, String> stationBitrate = new TableColumn<>("bitrate");
		stationBitrate.setCellValueFactory(cd -> new SimpleStringProperty(CodecFormatter.formatBitRate(cd.getValue().getBitRate())));
		
		TableColumn<Station, String> stationCodec = new TableColumn<>("codec");
		stationCodec.setCellValueFactory(cd -> new SimpleStringProperty(CodecFormatter.formatCodec(cd.getValue().getCodec())));
		
		stationsTable.getColumns().add(stationName);
		stationsTable.getColumns().add(stationBitrate);
		stationsTable.getColumns().add(stationCodec);
		
		stationsTable.setOnMouseClicked((mouseEvent) -> {
			
			if(mouseEvent.getClickCount() == 2) {
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
		
		return stationsTable;
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
