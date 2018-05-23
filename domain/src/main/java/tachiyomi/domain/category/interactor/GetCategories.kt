package tachiyomi.domain.category.interactor

import io.reactivex.Flowable
import io.reactivex.Single
import tachiyomi.domain.category.Category
import tachiyomi.domain.category.repository.CategoryRepository
import javax.inject.Inject

class GetCategories @Inject constructor(
  private val categoryRepository: CategoryRepository
) {

  fun interact(): Single<List<Category>> {
    return categoryRepository.getCategories()
  }
}
