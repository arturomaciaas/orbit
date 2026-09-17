package com.orbit.blocker.work;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ExpiredRuleCleanupWorker_AssistedFactory_Impl implements ExpiredRuleCleanupWorker_AssistedFactory {
  private final ExpiredRuleCleanupWorker_Factory delegateFactory;

  ExpiredRuleCleanupWorker_AssistedFactory_Impl(ExpiredRuleCleanupWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public ExpiredRuleCleanupWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<ExpiredRuleCleanupWorker_AssistedFactory> create(
      ExpiredRuleCleanupWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ExpiredRuleCleanupWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<ExpiredRuleCleanupWorker_AssistedFactory> createFactoryProvider(
      ExpiredRuleCleanupWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ExpiredRuleCleanupWorker_AssistedFactory_Impl(delegateFactory));
  }
}
