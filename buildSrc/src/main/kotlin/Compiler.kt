object CompilerArguments {
    const val coroutines = "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    const val experimental = "-Xopt-in=kotlin.Experimental"
    const val experimentalStdlib = "-Xuse-experimental=kotlin.ExperimentalStdlibApi"
}

object Jvm {
    const val target = "1.8"
}
