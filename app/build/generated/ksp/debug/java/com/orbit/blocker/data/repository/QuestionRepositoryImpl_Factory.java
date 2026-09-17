package com.orbit.blocker.data.repository;

import com.orbit.blocker.data.db.QuestionDao;
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
public final class QuestionRepositoryImpl_Factory implements Factory<QuestionRepositoryImpl> {
  private final Provider<QuestionDao> questionDaoProvider;

  public QuestionRepositoryImpl_Factory(Provider<QuestionDao> questionDaoProvider) {
    this.questionDaoProvider = questionDaoProvider;
  }

  @Override
  public QuestionRepositoryImpl get() {
    return newInstance(questionDaoProvider.get());
  }

  public static QuestionRepositoryImpl_Factory create(Provider<QuestionDao> questionDaoProvider) {
    return new QuestionRepositoryImpl_Factory(questionDaoProvider);
  }

  public static QuestionRepositoryImpl newInstance(QuestionDao questionDao) {
    return new QuestionRepositoryImpl(questionDao);
  }
}
