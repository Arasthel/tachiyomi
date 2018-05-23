package tachiyomi.ui.categories

import tachiyomi.domain.category.Category

data class CategoriesViewState(
  val categoriesList: List<Category> = emptyList(),
  val error: Throwable? = null
)