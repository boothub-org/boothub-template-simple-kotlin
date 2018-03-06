package org.boothub.simplekotlin.context

import groovy.transform.SelfType
import org.boothub.context.ConfiguredBy
import org.boothub.context.ProjectContext
import org.boothub.context.TextIOConfigurator
import org.beryx.textio.TextIO

@SelfType(ProjectContext)
@ConfiguredBy(LicenseCheckSupport.Configurator)
trait LicenseCheckSupport {
    boolean checkLicenseHeader = true

    static class Configurator extends TextIOConfigurator  {
        @Override
        void configureWithTextIO(ProjectContext context, TextIO textIO) {
            def ctx = context as LicenseCheckSupport
            ctx.checkLicenseHeader = textIO.newBooleanInputReader()
                    .withDefaultValue(ctx.checkLicenseHeader)
                    .read("Check license header?")
        }
    }

}
