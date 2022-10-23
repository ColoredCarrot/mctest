package info.voidev.mctest.engine.server

class RuntimeDidExitException(val code: Int) : Exception("The MCTest runtime exited unexpectedly with code $code")
