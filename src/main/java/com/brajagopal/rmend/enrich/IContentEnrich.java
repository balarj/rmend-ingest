package com.brajagopal.rmend.enrich;

import java.io.File;
import java.util.Collection;

/**
 * @author <bxr4261>
 */
public interface IContentEnrich {

    // Base Directory for the content
    public File getBaseDirectory();

    public Collection<? extends Object> getArticles();

}
