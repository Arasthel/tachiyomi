package tachiyomi.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import tachiyomi.app.R
import tachiyomi.ui.base.MvpScopedController
import tachiyomi.ui.base.withFadeTransaction
import tachiyomi.ui.categories.CategoryController

class LibraryController : MvpScopedController<LibraryPresenter>() {

  override fun getPresenterClass() = LibraryPresenter::class.java

  override fun getModule() = LibraryModule(this)

  override fun getTitle() = resources?.getString(R.string.label_library)

  init {
    setHasOptionsMenu(true)
  }

  //===========================================================================
  // ~ Lifecycle
  //===========================================================================

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup,
    savedViewState: Bundle?
  ): View {
    presenter // TODO forcing presenter creation
    return View(container.context) // TODO
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    return inflater.inflate(R.menu.library, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {

    when (item.itemId) {
      R.id.action_edit_categories -> router.pushController(
        CategoryController().withFadeTransaction())
    }

    return super.onOptionsItemSelected(item)
  }

}
