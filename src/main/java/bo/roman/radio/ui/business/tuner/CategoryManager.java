package bo.roman.radio.ui.business.tuner;

import java.util.List;
import java.util.Optional;

import bo.radio.tuner.CategoryDaoApi;
import bo.radio.tuner.entities.Category;
import bo.radio.tuner.exceptions.TunerPersistenceException;

public class CategoryManager {
	private CategoryDaoApi daoController;

	private static CategoryManager instance;
	private final TunerManager tunerManager;

	private CategoryManager() {
		tunerManager = TunerManager.getInstance();
		daoController = tunerManager.getCategoryDaoInstance();
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

	public Category createCategory(Category c) throws TunerPersistenceException {
		Optional<Category> oc = findCategoryByName(c.getName());
		if (!oc.isPresent()) {
			Category toCreate = daoController.createCategory(c);
			tunerManager.addCategory(toCreate);
			return toCreate;
		}

		return oc.get();
	}

	public void deleteCategory(String name) throws TunerPersistenceException {
		Optional<Category> oc = findCategoryByName(name);
		if(oc.isPresent()) {
			daoController.deleteCategory(oc.get());
			tunerManager.deleteCategory(oc.get());
		}
	}

	public void updateCategory(Category category) throws TunerPersistenceException {
		Optional<Category> oldCategory = daoController.getCategory(category.getId());
		if(oldCategory.isPresent())
		{
			daoController.updateCategory(category);
			tunerManager.updateCategory(oldCategory.get(), category);
		}
	}

}
