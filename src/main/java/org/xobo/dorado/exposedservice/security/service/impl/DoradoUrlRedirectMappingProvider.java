package org.xobo.dorado.exposedservice.security.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.xobo.dorado.exposedservice.security.service.UrlRedirectMappingProvider;

import com.bstek.dorado.view.config.XmlViewConfigDefinitionFactory;
import com.bstek.dorado.view.manager.ViewConfigFactoryRegister;

public class DoradoUrlRedirectMappingProvider implements UrlRedirectMappingProvider {
  private static String DEFAULT_VIEW_ROOT = "classpath:";

  private Map<String, String> urlMapping = new HashMap<String, String>();

  public DoradoUrlRedirectMappingProvider(
      Collection<ViewConfigFactoryRegister> viewConfigFactoryRegisterList) {
    for (ViewConfigFactoryRegister viewConfigFactoryRegister : viewConfigFactoryRegisterList) {

      try {
        String viewNamePattern =
            (String) getFieldValue(viewConfigFactoryRegister, "viewNamePattern");

        // exposedservice.** -> exposedservice
        if (viewNamePattern.equals("**")) {
          continue;
        }
        String viewPath = viewNamePattern.replace(".**", "");
        Object viewConfigFactory = getFieldValue(viewConfigFactoryRegister, "viewConfigFactory");

        if (viewConfigFactory instanceof XmlViewConfigDefinitionFactory) {
          String pathPrefixs = ((XmlViewConfigDefinitionFactory) viewConfigFactory).getPathPrefix();
          for (String pathPrefix : pathPrefixs.split(";")) {
            if (pathPrefix.startsWith(DEFAULT_VIEW_ROOT)
                && pathPrefix.length() > DEFAULT_VIEW_ROOT.length()) {
              // classpath:org/xobo -> org.xobo
              String path = pathPrefix.substring(DEFAULT_VIEW_ROOT.length()).replaceAll("/", ".");

              // org.xobo.exposedservice -> exposedservice
              urlMapping.put(path + "." + viewPath, viewPath);
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public Object getFieldValue(Object target, String property) throws IllegalArgumentException,
      IllegalAccessException, NoSuchFieldException, SecurityException {
    Field field = target.getClass().getDeclaredField(property);
    if (Modifier.isPrivate(field.getModifiers())) {
      field.setAccessible(true);
      return field.get(target);
    }
    return null;
  }

  public Map<String, String> getMapping() {
    return urlMapping;
  }

}
