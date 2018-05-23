package tachiyomi.ui.categories

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import tachiyomi.core.rx.addTo
import tachiyomi.domain.category.Category
import tachiyomi.domain.category.interactor.CreateCategoryWithName
import tachiyomi.domain.category.interactor.SubscribeCategories
import tachiyomi.ui.base.BasePresenter
import javax.inject.Inject

class CategoryPresenter @Inject constructor(
  private val subscribeCategories: SubscribeCategories,
  private val createCategoryWithName: CreateCategoryWithName
) : BasePresenter() {

  val stateRelay = BehaviorProcessor.create<CategoriesViewState>().toSerialized()
  private val actionsRelay = PublishProcessor.create<Action>()
  private val actionsObserver = actionsRelay.onBackpressureBuffer()

  private var currentState: CategoriesViewState =
    CategoriesViewState()

  init {
    val actions = listOf(bindCreateCategory(), bindGetCategories(), bindErrorDelivered())

    Flowable.merge(actions)
      .scan(currentState, ::reduce)
      .logOnNext()
      .observeOn(AndroidSchedulers.mainThread())
      .doOnNext { currentState = it }
      .subscribe(stateRelay::onNext)
      .addTo(disposables)

    getCategories()
  }

  private fun bindGetCategories(): Flowable<Change> {
    return actionsObserver.ofType(Action.SubscribeCategories::class.java)
      .flatMap {
        subscribeCategories.interact()
      }
      .observeOn(Schedulers.io())
      .map(Change::CategoriesUpdate)
  }

  private fun bindCreateCategory(): Flowable<Change> {
    return actionsObserver.ofType(Action.CreateCategory::class.java)
      .flatMap {
        createCategoryWithName.interact(it.name).toFlowable<Change>().onErrorReturn(
          Change::Error)
      }
  }

  private fun bindErrorDelivered(): Flowable<Change> {
    return actionsObserver.ofType(Action.ErrorDelivered::class.java)
      .map { Change.Error(null) }
  }

  fun getCategories() {
    actionsRelay.offer(Action.SubscribeCategories)
  }

  fun createCategory(name: String) {
    actionsRelay.offer(Action.CreateCategory(name))
  }

  fun errorHandled() {
    actionsRelay.offer(Action.ErrorDelivered)
  }

  private fun reduce(state: CategoriesViewState, change: Change): CategoriesViewState {
    return when (change) {
      is Change.CategoriesUpdate -> state.copy(categoriesList = change.categories)
      is Change.Error -> state.copy(error = change.error)
    }
  }

}

private sealed class Action {
  object SubscribeCategories : Action()
  data class CreateCategory(val name: String) : Action()
  object ErrorDelivered : Action()
}

private sealed class Change {
  data class CategoriesUpdate(val categories: List<Category>) : Change()
  data class Error(val error: Throwable?) : Change()
}