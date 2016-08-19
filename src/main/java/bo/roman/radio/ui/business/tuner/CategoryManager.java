package bo.roman.radio.ui.business.tuner;

import java.util.List;
import java.util.Optional;

import bo.radio.tuner.CategoryDaoController;
import bo.radio.tuner.entities.Category;
import bo.radio.tuner.exceptions.TunerPersistenceException;

public class CategoryManager {
	private CategoryDaoController daoController;

	private static CategoryManager instance;

	private CategoryManager() {
		daoController = TunerManager.getInstance().getCategoryDaoInstance();
	}

	public static CategoryManager getInstance() {
		if (instance == null) {
			instance = new CategoryManager();
		}
		return instance;
	}

	public List<Category> getAllCategories() throws TunerPersistenceException {
		return daoController.getAllCategories();
	}

	public Optional<Category> findCategoryByName(String name) throws TunerPersistenceException {
		return daoController.findCategoryByName(name);
	}

	public Category createCategory(String name) throws TunerPersistenceException {
		return daoController.createCategory(new Category(name));
	}

}
