/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.util;

import org.hibernate.dialect.MySQL5InnoDBDialect;

import java.sql.Types;

/**
 * Fix the broken Hibernate defaults for MySQL databases with UTF-8 encoding.
 *
 * @author Christian Bauer
 */
public class WikiMySQL5HibernateDialect extends MySQL5InnoDBDialect {

    @Override
    protected void registerVarcharTypes() {

        // TODO: The MySQL default makes it difficult to migrate the data because mysqldump is braindead...
        // registerColumnType(Types.BIT, "tinyint(1)");

        // It's pretty safe to assume that anything with more than 1024 characters (minus length byte) should probably be
        // a TEXT, not a VARCHAR which would have the "maximum row size" limit of 65KB.
        // I mean, where is the limit? If you have 20 of these VARCHAR fields on a table, and your character set is
        // UTF8, you are over the limit. Less than 20 or so should be OK. Just another fine example of how MySQL
        // protects its users from seeing its ugly internal implementation details.
        registerColumnType(Types.VARCHAR, "longtext");
        registerColumnType(Types.VARCHAR, 16777215, "mediumtext");
        registerColumnType(Types.VARCHAR, 1023, "varchar($l)");
    }

    // Create all tables as default UTF8!
    @Override
    public String getTableTypeString() {
        return " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_general_ci";
    }
}
