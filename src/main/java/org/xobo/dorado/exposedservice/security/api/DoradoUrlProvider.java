package org.xobo.dorado.exposedservice.security.api;

import java.util.Collection;

/**
 * 返回所有的URL，用来解析URL与dorado service关系。
 * 
 * @author xobo
 */
public interface DoradoUrlProvider {
  Collection<String> getUrls();
}
