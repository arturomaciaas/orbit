package com.orbit.blocker.work;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = ExpiredRuleCleanupWorker.class
)
public interface ExpiredRuleCleanupWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.orbit.blocker.work.ExpiredRuleCleanupWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      ExpiredRuleCleanupWorker_AssistedFactory factory);
}
