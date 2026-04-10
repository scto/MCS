MCS: Coding Standards
Dependency Injection (Hilt)
Alle Klassen und Module MÜSSEN Dagger-Hilt für Dependency Injection verwenden. Manuelle Instanziierung von Managern oder Repositories ist strikt untersagt.
Regeln für Aider:
* Application: Die Klasse MCSApplication im :app Modul muss mit @HiltAndroidApp annotiert sein.
* Klassen: Alle Manager, Repositories und UseCases müssen @Inject constructor() verwenden.
* Module: Jedes spezifische Submodul (z.B. :core:data, :core:terminal, :feature:editor) muss ein di-Package mit einem @Module besitzen, das in der Regel in der SingletonComponent::class installiert wird.
* Interfaces: Nutze @Binds in abstrakten Modulen, um Interfaces (aus :core:domain) an ihre jeweiligen Implementierungen (in :core:data) zu binden.
* UI: Alle Activities (hauptsächlich in :app) und ViewModels (in den jeweiligen :feature-Modulen) müssen mit @AndroidEntryPoint bzw. @HiltViewModel annotiert sein.
* Keine ServiceLocator: Nutze niemals manuelle Singletons via object oder companion object, wenn eine Injection via Hilt möglich ist.
Sprache & Frameworks
* Sprache: 100% Kotlin.
* UI: Jetpack Compose (Material 3).
* Asynchronität: Kotlin Coroutines & Flow.