package com.orbit.blocker.ui.quizgate;

import com.orbit.blocker.data.repository.QuestionRepository;
import com.orbit.blocker.data.settings.OrbitSettings;
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
public final class QuizGateViewModel_Factory implements Factory<QuizGateViewModel> {
  private final Provider<QuestionRepository> questionRepositoryProvider;

  private final Provider<OrbitSettings> settingsProvider;

  public QuizGateViewModel_Factory(Provider<QuestionRepository> questionRepositoryProvider,
      Provider<OrbitSettings> settingsProvider) {
    this.questionRepositoryProvider = questionRepositoryProvider;
    this.settingsProvider = settingsProvider;
  }

  @Override
  public QuizGateViewModel get() {
    return newInstance(questionRepositoryProvider.get(), settingsProvider.get());
  }

  public static QuizGateViewModel_Factory create(
      Provider<QuestionRepository> questionRepositoryProvider,
      Provider<OrbitSettings> settingsProvider) {
    return new QuizGateViewModel_Factory(questionRepositoryProvider, settingsProvider);
  }

  public static QuizGateViewModel newInstance(QuestionRepository questionRepository,
      OrbitSettings settings) {
    return new QuizGateViewModel(questionRepository, settings);
  }
}
