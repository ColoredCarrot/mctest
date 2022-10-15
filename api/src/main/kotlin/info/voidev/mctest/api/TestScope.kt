package info.voidev.mctest.api

/**
 * Use this as the receiver of your test methods
 * (although any parameter works).
 */
interface TestScope : TickFunctionScope {

    /**
     * Yields ticks until all outstanding packets,
     * whether sent by the server or client,
     * have been received by the opposing side.
     *
     * For example, if following the AAA (Arrange, Act, Assert) pattern,
     * this method should be called in between the Act and Assert stage
     * (as well as, potentially, between Arrange and Act).
     */
    suspend fun syncPackets()

}
