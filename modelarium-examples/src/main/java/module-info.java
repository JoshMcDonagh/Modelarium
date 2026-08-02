/**
 * Module for containing worked example models built against the Modelarium framework.
 *
 * <p>This module deliberately depends only on {@code modelarium}: compiling it verifies that the library's exported
 * packages are sufficient for client code.
 */
module modelarium.examples {
    requires modelarium;
    requires com.fasterxml.jackson.databind;
}
