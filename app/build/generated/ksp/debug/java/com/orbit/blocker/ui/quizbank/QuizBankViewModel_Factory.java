package com.orbit.blocker.ui.quizbank;

import com.orbit.blocker.data.repository.QuestionRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class QuizBankViewModel_Factory implements Factory<QuizBankViewModel> {
  private final Provider<QuestionRepository> repositoryProvider;

  public QuizBankViewModel_Factory(Provider<QuestionRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public QuizBankViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static QuizBankViewModel_Factory create(Provider<QuestionRepository> repositoryProvider) {
    return new QuizBankViewModel_Factory(repositoryProvider);
  }

  public static QuizBankViewModel newInstance(QuestionRepository repository) {
    return new QuizBankViewModel(repository);
  }
}
