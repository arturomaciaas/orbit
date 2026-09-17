package com.orbit.blocker;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.orbit.blocker.data.apps.InstalledAppsProvider;
import com.orbit.blocker.data.backup.BackupManager;
import com.orbit.blocker.data.db.AccessGrantDao;
import com.orbit.blocker.data.db.BlockRuleDao;
import com.orbit.blocker.data.db.BlockedAppDao;
import com.orbit.blocker.data.db.FocusSessionDao;
import com.orbit.blocker.data.db.GalaxyProgressDao;
import com.orbit.blocker.data.db.NotificationRecordDao;
import com.orbit.blocker.data.db.OrbitDatabase;
import com.orbit.blocker.data.db.QuestionDao;
import com.orbit.blocker.data.repository.AccessGrantRepositoryImpl;
import com.orbit.blocker.data.repository.BlockRepositoryImpl;
import com.orbit.blocker.data.repository.FocusSessionRepositoryImpl;
import com.orbit.blocker.data.repository.GalaxyRepositoryImpl;
import com.orbit.blocker.data.repository.NotificationRepository;
import com.orbit.blocker.data.repository.NotificationRepositoryImpl;
import com.orbit.blocker.data.repository.QuestionRepositoryImpl;
import com.orbit.blocker.data.seed.QuestionSeeder;
import com.orbit.blocker.data.settings.OrbitSettings;
import com.orbit.blocker.di.DatabaseModule_ProvideAccessGrantDaoFactory;
import com.orbit.blocker.di.DatabaseModule_ProvideBlockRuleDaoFactory;
import com.orbit.blocker.di.DatabaseModule_ProvideBlockedAppDaoFactory;
import com.orbit.blocker.di.DatabaseModule_ProvideDatabaseFactory;
import com.orbit.blocker.di.DatabaseModule_ProvideFocusSessionDaoFactory;
import com.orbit.blocker.di.DatabaseModule_ProvideGalaxyDaoFactory;
import com.orbit.blocker.di.DatabaseModule_ProvideNotificationDaoFactory;
import com.orbit.blocker.di.DatabaseModule_ProvideQuestionDaoFactory;
import com.orbit.blocker.di.RepositoryModule_Companion_ProvideSettingsFactory;
import com.orbit.blocker.domain.block.BlockEnforcer;
import com.orbit.blocker.domain.focus.FocusController;
import com.orbit.blocker.domain.focus.FocusSessionManager;
import com.orbit.blocker.domain.gamification.GamificationEvents;
import com.orbit.blocker.domain.notification.NotificationTierCache;
import com.orbit.blocker.gate.QuizGateActivity;
import com.orbit.blocker.gate.QuizGateActivity_MembersInjector;
import com.orbit.blocker.service.FocusSessionService;
import com.orbit.blocker.service.FocusSessionService_MembersInjector;
import com.orbit.blocker.ui.MainActivity;
import com.orbit.blocker.ui.RootViewModel;
import com.orbit.blocker.ui.RootViewModel_HiltModules;
import com.orbit.blocker.ui.blocks.BlocksViewModel;
import com.orbit.blocker.ui.blocks.BlocksViewModel_HiltModules;
import com.orbit.blocker.ui.digest.DigestViewModel;
import com.orbit.blocker.ui.digest.DigestViewModel_HiltModules;
import com.orbit.blocker.ui.focus.FocusViewModel;
import com.orbit.blocker.ui.focus.FocusViewModel_HiltModules;
import com.orbit.blocker.ui.home.HomeViewModel;
import com.orbit.blocker.ui.home.HomeViewModel_HiltModules;
import com.orbit.blocker.ui.onboarding.OnboardingViewModel;
import com.orbit.blocker.ui.onboarding.OnboardingViewModel_HiltModules;
import com.orbit.blocker.ui.quizbank.QuizBankViewModel;
import com.orbit.blocker.ui.quizbank.QuizBankViewModel_HiltModules;
import com.orbit.blocker.ui.quizgate.QuizGateViewModel;
import com.orbit.blocker.ui.quizgate.QuizGateViewModel_HiltModules;
import com.orbit.blocker.ui.settings.SettingsViewModel;
import com.orbit.blocker.ui.settings.SettingsViewModel_HiltModules;
import com.orbit.blocker.work.ExpiredRuleCleanupWorker;
import com.orbit.blocker.work.ExpiredRuleCleanupWorker_AssistedFactory;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerOrbitApplication_HiltComponents_SingletonC {
  private DaggerOrbitApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public OrbitApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements OrbitApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public OrbitApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements OrbitApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public OrbitApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements OrbitApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public OrbitApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements OrbitApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public OrbitApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements OrbitApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public OrbitApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements OrbitApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public OrbitApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements OrbitApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public OrbitApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends OrbitApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends OrbitApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends OrbitApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends OrbitApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectQuizGateActivity(QuizGateActivity quizGateActivity) {
      injectQuizGateActivity2(quizGateActivity);
    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(9).put(LazyClassKeyProvider.com_orbit_blocker_ui_blocks_BlocksViewModel, BlocksViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_orbit_blocker_ui_digest_DigestViewModel, DigestViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_orbit_blocker_ui_focus_FocusViewModel, FocusViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_orbit_blocker_ui_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_orbit_blocker_ui_onboarding_OnboardingViewModel, OnboardingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_orbit_blocker_ui_quizbank_QuizBankViewModel, QuizBankViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_orbit_blocker_ui_quizgate_QuizGateViewModel, QuizGateViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_orbit_blocker_ui_RootViewModel, RootViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_orbit_blocker_ui_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private QuizGateActivity injectQuizGateActivity2(QuizGateActivity instance) {
      QuizGateActivity_MembersInjector.injectAccessGrantRepository(instance, singletonCImpl.accessGrantRepositoryImplProvider.get());
      QuizGateActivity_MembersInjector.injectSettings(instance, singletonCImpl.provideSettingsProvider.get());
      QuizGateActivity_MembersInjector.injectGamificationEvents(instance, singletonCImpl.gamificationEventsProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_orbit_blocker_ui_quizgate_QuizGateViewModel = "com.orbit.blocker.ui.quizgate.QuizGateViewModel";

      static String com_orbit_blocker_ui_RootViewModel = "com.orbit.blocker.ui.RootViewModel";

      static String com_orbit_blocker_ui_settings_SettingsViewModel = "com.orbit.blocker.ui.settings.SettingsViewModel";

      static String com_orbit_blocker_ui_home_HomeViewModel = "com.orbit.blocker.ui.home.HomeViewModel";

      static String com_orbit_blocker_ui_onboarding_OnboardingViewModel = "com.orbit.blocker.ui.onboarding.OnboardingViewModel";

      static String com_orbit_blocker_ui_quizbank_QuizBankViewModel = "com.orbit.blocker.ui.quizbank.QuizBankViewModel";

      static String com_orbit_blocker_ui_focus_FocusViewModel = "com.orbit.blocker.ui.focus.FocusViewModel";

      static String com_orbit_blocker_ui_blocks_BlocksViewModel = "com.orbit.blocker.ui.blocks.BlocksViewModel";

      static String com_orbit_blocker_ui_digest_DigestViewModel = "com.orbit.blocker.ui.digest.DigestViewModel";

      @KeepFieldType
      QuizGateViewModel com_orbit_blocker_ui_quizgate_QuizGateViewModel2;

      @KeepFieldType
      RootViewModel com_orbit_blocker_ui_RootViewModel2;

      @KeepFieldType
      SettingsViewModel com_orbit_blocker_ui_settings_SettingsViewModel2;

      @KeepFieldType
      HomeViewModel com_orbit_blocker_ui_home_HomeViewModel2;

      @KeepFieldType
      OnboardingViewModel com_orbit_blocker_ui_onboarding_OnboardingViewModel2;

      @KeepFieldType
      QuizBankViewModel com_orbit_blocker_ui_quizbank_QuizBankViewModel2;

      @KeepFieldType
      FocusViewModel com_orbit_blocker_ui_focus_FocusViewModel2;

      @KeepFieldType
      BlocksViewModel com_orbit_blocker_ui_blocks_BlocksViewModel2;

      @KeepFieldType
      DigestViewModel com_orbit_blocker_ui_digest_DigestViewModel2;
    }
  }

  private static final class ViewModelCImpl extends OrbitApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<BlocksViewModel> blocksViewModelProvider;

    private Provider<DigestViewModel> digestViewModelProvider;

    private Provider<FocusViewModel> focusViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<QuizBankViewModel> quizBankViewModelProvider;

    private Provider<QuizGateViewModel> quizGateViewModelProvider;

    private Provider<RootViewModel> rootViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.blocksViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.digestViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.focusViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.quizBankViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.quizGateViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.rootViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(9).put(LazyClassKeyProvider.com_orbit_blocker_ui_blocks_BlocksViewModel, ((Provider) blocksViewModelProvider)).put(LazyClassKeyProvider.com_orbit_blocker_ui_digest_DigestViewModel, ((Provider) digestViewModelProvider)).put(LazyClassKeyProvider.com_orbit_blocker_ui_focus_FocusViewModel, ((Provider) focusViewModelProvider)).put(LazyClassKeyProvider.com_orbit_blocker_ui_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_orbit_blocker_ui_onboarding_OnboardingViewModel, ((Provider) onboardingViewModelProvider)).put(LazyClassKeyProvider.com_orbit_blocker_ui_quizbank_QuizBankViewModel, ((Provider) quizBankViewModelProvider)).put(LazyClassKeyProvider.com_orbit_blocker_ui_quizgate_QuizGateViewModel, ((Provider) quizGateViewModelProvider)).put(LazyClassKeyProvider.com_orbit_blocker_ui_RootViewModel, ((Provider) rootViewModelProvider)).put(LazyClassKeyProvider.com_orbit_blocker_ui_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_orbit_blocker_ui_quizgate_QuizGateViewModel = "com.orbit.blocker.ui.quizgate.QuizGateViewModel";

      static String com_orbit_blocker_ui_RootViewModel = "com.orbit.blocker.ui.RootViewModel";

      static String com_orbit_blocker_ui_onboarding_OnboardingViewModel = "com.orbit.blocker.ui.onboarding.OnboardingViewModel";

      static String com_orbit_blocker_ui_settings_SettingsViewModel = "com.orbit.blocker.ui.settings.SettingsViewModel";

      static String com_orbit_blocker_ui_digest_DigestViewModel = "com.orbit.blocker.ui.digest.DigestViewModel";

      static String com_orbit_blocker_ui_focus_FocusViewModel = "com.orbit.blocker.ui.focus.FocusViewModel";

      static String com_orbit_blocker_ui_blocks_BlocksViewModel = "com.orbit.blocker.ui.blocks.BlocksViewModel";

      static String com_orbit_blocker_ui_quizbank_QuizBankViewModel = "com.orbit.blocker.ui.quizbank.QuizBankViewModel";

      static String com_orbit_blocker_ui_home_HomeViewModel = "com.orbit.blocker.ui.home.HomeViewModel";

      @KeepFieldType
      QuizGateViewModel com_orbit_blocker_ui_quizgate_QuizGateViewModel2;

      @KeepFieldType
      RootViewModel com_orbit_blocker_ui_RootViewModel2;

      @KeepFieldType
      OnboardingViewModel com_orbit_blocker_ui_onboarding_OnboardingViewModel2;

      @KeepFieldType
      SettingsViewModel com_orbit_blocker_ui_settings_SettingsViewModel2;

      @KeepFieldType
      DigestViewModel com_orbit_blocker_ui_digest_DigestViewModel2;

      @KeepFieldType
      FocusViewModel com_orbit_blocker_ui_focus_FocusViewModel2;

      @KeepFieldType
      BlocksViewModel com_orbit_blocker_ui_blocks_BlocksViewModel2;

      @KeepFieldType
      QuizBankViewModel com_orbit_blocker_ui_quizbank_QuizBankViewModel2;

      @KeepFieldType
      HomeViewModel com_orbit_blocker_ui_home_HomeViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.orbit.blocker.ui.blocks.BlocksViewModel 
          return (T) new BlocksViewModel(singletonCImpl.blockRepositoryImplProvider.get(), singletonCImpl.installedAppsProvider.get());

          case 1: // com.orbit.blocker.ui.digest.DigestViewModel 
          return (T) new DigestViewModel(singletonCImpl.notificationRepositoryImplProvider.get());

          case 2: // com.orbit.blocker.ui.focus.FocusViewModel 
          return (T) new FocusViewModel(singletonCImpl.blockRepositoryImplProvider.get(), singletonCImpl.focusSessionManagerProvider.get());

          case 3: // com.orbit.blocker.ui.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.galaxyRepositoryImplProvider.get());

          case 4: // com.orbit.blocker.ui.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.provideSettingsProvider.get());

          case 5: // com.orbit.blocker.ui.quizbank.QuizBankViewModel 
          return (T) new QuizBankViewModel(singletonCImpl.questionRepositoryImplProvider.get());

          case 6: // com.orbit.blocker.ui.quizgate.QuizGateViewModel 
          return (T) new QuizGateViewModel(singletonCImpl.questionRepositoryImplProvider.get(), singletonCImpl.provideSettingsProvider.get());

          case 7: // com.orbit.blocker.ui.RootViewModel 
          return (T) new RootViewModel(singletonCImpl.provideSettingsProvider.get());

          case 8: // com.orbit.blocker.ui.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.provideSettingsProvider.get(), singletonCImpl.backupManagerProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends OrbitApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends OrbitApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectFocusSessionService(FocusSessionService focusSessionService) {
      injectFocusSessionService2(focusSessionService);
    }

    private FocusSessionService injectFocusSessionService2(FocusSessionService instance) {
      FocusSessionService_MembersInjector.injectFocusController(instance, singletonCImpl.focusControllerProvider.get());
      FocusSessionService_MembersInjector.injectFocusSessionManager(instance, singletonCImpl.focusSessionManagerProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends OrbitApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<OrbitDatabase> provideDatabaseProvider;

    private Provider<BlockRepositoryImpl> blockRepositoryImplProvider;

    private Provider<AccessGrantRepositoryImpl> accessGrantRepositoryImplProvider;

    private Provider<ExpiredRuleCleanupWorker_AssistedFactory> expiredRuleCleanupWorker_AssistedFactoryProvider;

    private Provider<QuestionRepositoryImpl> questionRepositoryImplProvider;

    private Provider<QuestionSeeder> questionSeederProvider;

    private Provider<GalaxyRepositoryImpl> galaxyRepositoryImplProvider;

    private Provider<FocusSessionManager> focusSessionManagerProvider;

    private Provider<BlockEnforcer> blockEnforcerProvider;

    private Provider<NotificationTierCache> notificationTierCacheProvider;

    private Provider<NotificationRepositoryImpl> notificationRepositoryImplProvider;

    private Provider<OrbitSettings> provideSettingsProvider;

    private Provider<GamificationEvents> gamificationEventsProvider;

    private Provider<InstalledAppsProvider> installedAppsProvider;

    private Provider<BackupManager> backupManagerProvider;

    private Provider<FocusSessionRepositoryImpl> focusSessionRepositoryImplProvider;

    private Provider<FocusController> focusControllerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private BlockedAppDao blockedAppDao() {
      return DatabaseModule_ProvideBlockedAppDaoFactory.provideBlockedAppDao(provideDatabaseProvider.get());
    }

    private BlockRuleDao blockRuleDao() {
      return DatabaseModule_ProvideBlockRuleDaoFactory.provideBlockRuleDao(provideDatabaseProvider.get());
    }

    private AccessGrantDao accessGrantDao() {
      return DatabaseModule_ProvideAccessGrantDaoFactory.provideAccessGrantDao(provideDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return Collections.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>singletonMap("com.orbit.blocker.work.ExpiredRuleCleanupWorker", ((Provider) expiredRuleCleanupWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    private QuestionDao questionDao() {
      return DatabaseModule_ProvideQuestionDaoFactory.provideQuestionDao(provideDatabaseProvider.get());
    }

    private GalaxyProgressDao galaxyProgressDao() {
      return DatabaseModule_ProvideGalaxyDaoFactory.provideGalaxyDao(provideDatabaseProvider.get());
    }

    private NotificationRecordDao notificationRecordDao() {
      return DatabaseModule_ProvideNotificationDaoFactory.provideNotificationDao(provideDatabaseProvider.get());
    }

    private FocusSessionDao focusSessionDao() {
      return DatabaseModule_ProvideFocusSessionDaoFactory.provideFocusSessionDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<OrbitDatabase>(singletonCImpl, 2));
      this.blockRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<BlockRepositoryImpl>(singletonCImpl, 1));
      this.accessGrantRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<AccessGrantRepositoryImpl>(singletonCImpl, 3));
      this.expiredRuleCleanupWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<ExpiredRuleCleanupWorker_AssistedFactory>(singletonCImpl, 0));
      this.questionRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<QuestionRepositoryImpl>(singletonCImpl, 5));
      this.questionSeederProvider = DoubleCheck.provider(new SwitchingProvider<QuestionSeeder>(singletonCImpl, 4));
      this.galaxyRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<GalaxyRepositoryImpl>(singletonCImpl, 6));
      this.focusSessionManagerProvider = DoubleCheck.provider(new SwitchingProvider<FocusSessionManager>(singletonCImpl, 8));
      this.blockEnforcerProvider = DoubleCheck.provider(new SwitchingProvider<BlockEnforcer>(singletonCImpl, 7));
      this.notificationTierCacheProvider = DoubleCheck.provider(new SwitchingProvider<NotificationTierCache>(singletonCImpl, 9));
      this.notificationRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<NotificationRepositoryImpl>(singletonCImpl, 10));
      this.provideSettingsProvider = DoubleCheck.provider(new SwitchingProvider<OrbitSettings>(singletonCImpl, 11));
      this.gamificationEventsProvider = DoubleCheck.provider(new SwitchingProvider<GamificationEvents>(singletonCImpl, 12));
      this.installedAppsProvider = DoubleCheck.provider(new SwitchingProvider<InstalledAppsProvider>(singletonCImpl, 13));
      this.backupManagerProvider = DoubleCheck.provider(new SwitchingProvider<BackupManager>(singletonCImpl, 14));
      this.focusSessionRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<FocusSessionRepositoryImpl>(singletonCImpl, 16));
      this.focusControllerProvider = DoubleCheck.provider(new SwitchingProvider<FocusController>(singletonCImpl, 15));
    }

    @Override
    public void injectOrbitApplication(OrbitApplication orbitApplication) {
      injectOrbitApplication2(orbitApplication);
    }

    @Override
    public BlockEnforcer blockEnforcer() {
      return blockEnforcerProvider.get();
    }

    @Override
    public NotificationTierCache tierCache() {
      return notificationTierCacheProvider.get();
    }

    @Override
    public NotificationRepository notificationRepository() {
      return notificationRepositoryImplProvider.get();
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private OrbitApplication injectOrbitApplication2(OrbitApplication instance) {
      OrbitApplication_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      OrbitApplication_MembersInjector.injectQuestionSeeder(instance, questionSeederProvider.get());
      OrbitApplication_MembersInjector.injectGalaxyRepository(instance, galaxyRepositoryImplProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.orbit.blocker.work.ExpiredRuleCleanupWorker_AssistedFactory 
          return (T) new ExpiredRuleCleanupWorker_AssistedFactory() {
            @Override
            public ExpiredRuleCleanupWorker create(Context appContext, WorkerParameters params) {
              return new ExpiredRuleCleanupWorker(appContext, params, singletonCImpl.blockRepositoryImplProvider.get(), singletonCImpl.accessGrantRepositoryImplProvider.get());
            }
          };

          case 1: // com.orbit.blocker.data.repository.BlockRepositoryImpl 
          return (T) new BlockRepositoryImpl(singletonCImpl.blockedAppDao(), singletonCImpl.blockRuleDao());

          case 2: // com.orbit.blocker.data.db.OrbitDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.orbit.blocker.data.repository.AccessGrantRepositoryImpl 
          return (T) new AccessGrantRepositoryImpl(singletonCImpl.accessGrantDao());

          case 4: // com.orbit.blocker.data.seed.QuestionSeeder 
          return (T) new QuestionSeeder(singletonCImpl.questionRepositoryImplProvider.get());

          case 5: // com.orbit.blocker.data.repository.QuestionRepositoryImpl 
          return (T) new QuestionRepositoryImpl(singletonCImpl.questionDao());

          case 6: // com.orbit.blocker.data.repository.GalaxyRepositoryImpl 
          return (T) new GalaxyRepositoryImpl(singletonCImpl.galaxyProgressDao());

          case 7: // com.orbit.blocker.domain.block.BlockEnforcer 
          return (T) new BlockEnforcer(singletonCImpl.blockRepositoryImplProvider.get(), singletonCImpl.accessGrantRepositoryImplProvider.get(), singletonCImpl.focusSessionManagerProvider.get());

          case 8: // com.orbit.blocker.domain.focus.FocusSessionManager 
          return (T) new FocusSessionManager();

          case 9: // com.orbit.blocker.domain.notification.NotificationTierCache 
          return (T) new NotificationTierCache(singletonCImpl.blockRepositoryImplProvider.get());

          case 10: // com.orbit.blocker.data.repository.NotificationRepositoryImpl 
          return (T) new NotificationRepositoryImpl(singletonCImpl.notificationRecordDao());

          case 11: // com.orbit.blocker.data.settings.OrbitSettings 
          return (T) RepositoryModule_Companion_ProvideSettingsFactory.provideSettings(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 12: // com.orbit.blocker.domain.gamification.GamificationEvents 
          return (T) new GamificationEvents(singletonCImpl.galaxyRepositoryImplProvider.get());

          case 13: // com.orbit.blocker.data.apps.InstalledAppsProvider 
          return (T) new InstalledAppsProvider(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 14: // com.orbit.blocker.data.backup.BackupManager 
          return (T) new BackupManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideDatabaseProvider.get());

          case 15: // com.orbit.blocker.domain.focus.FocusController 
          return (T) new FocusController(singletonCImpl.focusSessionManagerProvider.get(), singletonCImpl.focusSessionRepositoryImplProvider.get(), singletonCImpl.gamificationEventsProvider.get());

          case 16: // com.orbit.blocker.data.repository.FocusSessionRepositoryImpl 
          return (T) new FocusSessionRepositoryImpl(singletonCImpl.focusSessionDao());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
