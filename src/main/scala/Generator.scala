package testchipip

object Generator extends util.GeneratorApp {
  val longName = names.topModuleClass + "." + names.configs
  generateFirrtl
  generateTestSuiteMakefrags // TODO: Needed only for legacy make targets
  generateParameterDump // TODO: Needed only for legacy make targets
}
