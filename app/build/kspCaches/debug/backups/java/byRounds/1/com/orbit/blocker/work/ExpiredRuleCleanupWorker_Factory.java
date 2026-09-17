package com.orbit.blocker.work;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.orbit.blocker.data.repository.AccessGrantRepository;
import com.orbit.blocker.data.repository.BlockRepository;
import dagger.internal.DaggerGenerated;
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
public final class ExpiredRuleCleanupWorker_Factory {
  private final Provider<BlockRepository> blockRepositoryProvider;

  private final Provider<AccessGrantRepository> accessGrantRepositoryProvider;

  public ExpiredRuleCleanupWorker_Factory(Provider<BlockRepository> blockRepositoryProvider,
      Provider<AccessGrantRepository> accessGrantRepositoryProvider) {
    this.blockRepositoryProvider = blockRepositoryProvider;
    this.accessGrantRepositoryProvider = accessGrantRepositoryProvider;
  }

  public ExpiredRuleCleanupWorker get(Context appContext, WorkerParameters params) {
    return newInstance(appContext, params, blockRepositoryProvider.get(), accessGrantRepositoryProvider.get());
  }

  public static ExpiredRuleCleanupWorker_Factory create(
      Provider<BlockRepository> blockRepositoryProvider,
      Provider<AccessGrantRepository> accessGrantRepositoryProvider) {
    return new ExpiredRuleCleanupWorker_Factory(blockRepositoryProvider, accessGrantRepositoryProvider);
  }

  public static ExpiredRuleCleanupWorker newInstance(Context appContext, WorkerParameters params,
      BlockRepository blockRepository, AccessGrantRepository accessGrantRepository) {
    return new ExpiredRuleCleanupWorker(appContext, params, blockRepository, accessGrantRepository);
  }
}
