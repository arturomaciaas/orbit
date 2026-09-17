package com.orbit.blocker.data.seed;

import com.orbit.blocker.data.repository.QuestionRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class QuestionSeeder_Factory implements Factory<QuestionSeeder> {
  private final Provider<QuestionRepository> questionRepositoryProvider;

  public QuestionSeeder_Factory(Provider<QuestionRepository> questionRepositoryProvider) {
    this.questionRepositoryProvider = questionRepositoryProvider;
  }

  @Override
  public QuestionSeeder get() {
    return newInstance(questionRepositoryProvider.get());
  }

  public static QuestionSeeder_Factory create(
      Provider<QuestionRepository> questionRepositoryProvider) {
    return new QuestionSeeder_Factory(questionRepositoryProvider);
  }

  public static QuestionSeeder newInstance(QuestionRepository questionRepository) {
    return new QuestionSeeder(questionRepository);
  }
}
