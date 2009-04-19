/*
    MySQL 5.x requires a fake sequence which Hibernate can provide with a custom table.
 */
@GenericGenerator(
    name = "wikiSequenceGenerator",
    strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
    parameters = {
        @Parameter(name = "sequence_name", value = "WIKI_SEQUENCE"),
        @Parameter(name = "initial_value", value = "1000"),
        @Parameter(name = "increment_size", value = "1")
    }
)

/*
    PostgreSQL 8.3 supports real sequences

@GenericGenerator(
    name = "wikiSequenceGenerator",
    strategy = "sequence",
    parameters = {
        @Parameter(name = "sequence", value = "WIKI_SEQUENCE"),
        @Parameter(name = "parameters", value = "increment by 1 start with 1000")
    }
)
*/

package org.jboss.seam.wiki.core.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

