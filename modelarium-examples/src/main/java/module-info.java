/**
 * Module containing worked example models built against the Modelarium framework.
 *
 * <p>Compiling this module also provides a useful client-side check that Modelarium exports the packages required
 * to build real models. Jackson is used only for loading the examples' JSON configuration resources.
 */
module modelarium.examples {
    requires modelarium;
    requires com.fasterxml.jackson.databind;
}
