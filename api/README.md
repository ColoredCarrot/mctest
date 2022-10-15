# mctest-api

The API for your projects using MCTest.
Should be depended on via `testCompileOnly`.

## Parameter Binding

MCTest can autowire a variety of parameters for test methods.
The most prominent example is `TestPlayer`,
which represents a bot that connects to the server.
To the server, it looks like any other player.

`TestPlayer` requires that you mark your test method as `suspend`.
If you do so, you may also add `TickFunctionScope` as a receiver (preferred) or parameter.
This class allows you wait for the next server tick without breaking out of your test method.

Test methods are always executed on the primary server thread.
