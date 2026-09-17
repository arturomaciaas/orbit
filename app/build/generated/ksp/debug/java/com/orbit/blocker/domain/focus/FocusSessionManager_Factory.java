package com.orbit.blocker.domain.focus;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class FocusSessionManager_Factory implements Factory<FocusSessionManager> {
  @Override
  public FocusSessionManager get() {
    return newInstance();
  }

  public static FocusSessionManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FocusSessionManager newInstance() {
    return new FocusSessionManager();
  }

  private static final class InstanceHolder {
    private static final FocusSessionManager_Factory INSTANCE = new FocusSessionManager_Factory();
  }
}
