package com.orbit.blocker.data.settings;

import android.content.Context;
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
public final class OrbitSettings_Factory implements Factory<OrbitSettings> {
  private final Provider<Context> contextProvider;

  public OrbitSettings_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public OrbitSettings get() {
    return newInstance(contextProvider.get());
  }

  public static OrbitSettings_Factory create(Provider<Context> contextProvider) {
    return new OrbitSettings_Factory(contextProvider);
  }

  public static OrbitSettings newInstance(Context context) {
    return new OrbitSettings(context);
  }
}
