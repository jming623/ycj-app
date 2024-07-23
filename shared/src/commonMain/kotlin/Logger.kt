import io.github.aakira.napier.Napier
import io.github.aakira.napier.DebugAntilog
fun initLogger() {
    Napier.base(DebugAntilog())
}