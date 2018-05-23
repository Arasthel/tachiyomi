package tachiyomi.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.categories_edit.*
import tachiyomi.app.R
import tachiyomi.ui.base.MvpScopedController
import tachiyomi.ui.base.withFadeTransaction
import timber.log.Timber

class CategoryController : MvpScopedController<CategoryPresenter>(),
  CategoryCreateDialog.Listener {

  override fun getPresenterClass() = CategoryPresenter::class.java

  override fun getTitle() = "Edit categories"

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup,
    savedViewState: Bundle?
  ): View {
    return inflater.inflate(R.layout.categories_edit, container, false)
  }

  override fun onViewCreated(view: View) {
    super.onViewCreated(view)

    add_category_fab.setOnClickListener {
      openAddCategoryDialog()
    }

    presenter.stateRelay
      .subscribe(::render)

  }

  private fun render(state: CategoriesViewState) {
    if (state.error != null) {
      Toast.makeText(activity!!, state.error.message, Toast.LENGTH_SHORT).show()
      presenter.errorHandled()
    }

    Timber.d("Categories: ${state.categoriesList.count()}")
  }

  private fun openAddCategoryDialog() {
    router.pushController(CategoryCreateDialog(this).withFadeTransaction())
  }

  override fun createCategory(name: String) {
    presenter.createCategory(name)
  }
}