package com.orbit.blocker.data.apps;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class InstalledAppsProvider_Factory implements Factory<InstalledAppsProvider> {
  private final Provider<Context> contextProvider;

  public InstalledAppsProvider_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public InstalledAppsProvider get() {
    return newInstance(contextProvider.get());
  }

  public static InstalledAppsProvider_Factory create(Provider<Context> contextProvider) {
    return new InstalledAppsProvider_Factory(contextProvider);
  }

  public static InstalledAppsProvider newInstance(Context context) {
    return new InstalledAppsProvider(context);
  }
}
