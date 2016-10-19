package bo.roman.radio.ui.business.tuner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.CategoryDaoApi;
import bo.radio.tuner.StationDaoApi;
import bo.radio.tuner.TunerDaoApi;
import bo.radio.tuner.business.TunerBusiness;
import bo.radio.tuner.entities.Category;
import bo.radio.tuner.entities.Station;
import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;

public class TunerManager {
	private final Logger logger = LoggerFactory.getLogger(TunerManager.class);

	private static final String DBPROPS_PATH = "src/main/resources/database.properties";
	private static TunerDaoApi tunerDao;

	private static TunerManager instance;

	private static List<TitledPane> tunerTable = new ArrayList<>();

	@SuppressWarnings("unchecked")
	private TunerManager() {
		Properties databaseProperties = new Properties();
		try (FileInputStream fis = new FileInputStream(DBPROPS_PATH)) {
			databaseProperties.load(fis);
			tunerDao = new TunerBusiness(databaseProperties);
			// Load Stations Table
			getCategoryDaoInstance().getAllCategories().forEach(c -> addCategory(c));
			// Sort
			Collections.sort(tunerTable, (e1, e2) -> {
				int s1 = ((TableView<Station>) e1.getContent()).getItems().size();
				int s2 = ((TableView<Station>) e2.getContent()).getItems().size();
				return Integer.compare(s2, s1);
			});
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format(
					"There was an error loading the DB properties. The file %s does not exist.", DBPROPS_PATH), e);
		} catch (IOException e) {
			throw new RuntimeException(String.format("There was an error reading the DB properties.", DBPROPS_PATH), e);
		} catch (TunerPersistenceException e) {
			e.printStackTrace();
		}
	}

	public static TunerManager getInstance() {
		if (instance == null) {
			instance = new TunerManager();
		}
		return instance;
	}

	public StationDaoApi getStationDaoInstance() {
		return tunerDao.getStationControllerInstance();
	}

	public CategoryDaoApi getCategoryDaoInstance() {
		return tunerDao.getCategoryControllerInstance();
	}

	public void initializeTunerDatabase() throws TunerPersistenceException {
		LoggerUtils.logDebug(logger, () -> "Initializing Tuner Database.");
		tunerDao.init();
	}

	public void addStation(Station station) {
		LoggerUtils.logDebug(logger, () -> String.format("Adding Station [%s] to Tuner Table", station));
		tableModifier(station, t -> t.getItems().add(station));
	}

	public void removeStation(Station s) {
		LoggerUtils.logDebug(logger, () -> String.format("Removing Station [%s] from Tuner Table", s));
		tableModifier(s, t -> t.getItems().remove(s));
	}

	public void updateStation(Station oldStation, Station newStation) {
		// Remove all references of the old station
		removeStation(oldStation);

		// Add the new station
		addStation(newStation);
	}

	public void addCategory(Category c) {
		LoggerUtils.logDebug(logger, () -> String.format("Loading Tuner Table with Category[%s] with %d Stations",
				c.getName(), c.getStations().size()));
		TitledPane tp = new TitledPane();
		tp.setText(c.getName());
		tp.setContent(createStationTable(c.getStations()));
		tunerTable.add(tp);
	}
	
	public void deleteCategory(Category c) {
		LoggerUtils.logDebug(logger, () -> "Deleting Category: " + c);
		tunerTable.stream()
					.filter(tp -> tp.getText().equals(c.getName()))
					.findFirst()
					.ifPresent(tp -> tunerTable.remove(tp));
	}

	private TableView<Station> createStationTable(List<Station> stations) {
		TableView<Station> stationsTable = new TableView<>();
		stationsTable.setItems(FXCollections.observableList(stations));

		TableColumn<Station, String> stationName = new TableColumn<>("Radio Name");
		stationName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		stationsTable.getColumns().add(stationName);

		return stationsTable;
	}

	@SuppressWarnings("unchecked")
	private void tableModifier(Station s, Consumer<TableView<Station>> modifier) {
		for (Category c : s.getCategories()) {
			for (TitledPane tp : tunerTable) {
				if (tp.getText().equals(c.getName())) {
					TableView<Station> stationsTable = (TableView<Station>) tp.getContent();
					modifier.accept(stationsTable);
					stationsTable.refresh();
					break;
				}
			}
		}
	}

	public List<TitledPane> getTunerTable() {
		return tunerTable;
	}

}
