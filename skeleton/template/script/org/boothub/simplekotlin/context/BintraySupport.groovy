package org.boothub.simplekotlin.context

import groovy.transform.SelfType
import org.boothub.context.ConfiguredBy
import org.boothub.context.ProjectContext
import org.boothub.context.TextIOConfigurator
import org.beryx.textio.TextIO

@SelfType(ProjectContext)
@ConfiguredBy(BintraySupport.Configurator)
trait BintraySupport {
    boolean supportBintray = true

    static class Configurator extends TextIOConfigurator  {
        @Override
        void configureWithTextIO(ProjectContext context, TextIO textIO) {
            def ctx = context as BintraySupport
            ctx.supportBintray = textIO.newBooleanInputReader()
                    .withDefaultValue(ctx.supportBintray)
                    .read("Add support for uploading to Bintray?")
        }
    }

}
