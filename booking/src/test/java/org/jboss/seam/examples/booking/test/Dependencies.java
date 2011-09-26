package org.jboss.seam.examples.booking.test;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

public interface Dependencies {

    static final Archive<?>[] SOLDER = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("pom.xml")
            .artifact("org.jboss.seam.solder:seam-solder").exclusion("*").resolveAs(GenericArchive.class)
            .toArray(new Archive<?>[0]);
    static final Archive<?>[] JODA_TIME = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("pom.xml")
            .artifact("joda-time:joda-time").exclusion("*").resolveAs(GenericArchive.class).toArray(new Archive<?>[0]);
    static final Archive<?>[] INTERNATIONAL = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadMetadataFromPom("pom.xml").artifact("org.jboss.seam.international:seam-international").exclusion("*")
            .resolveAs(GenericArchive.class).toArray(new Archive<?>[0]);
}
