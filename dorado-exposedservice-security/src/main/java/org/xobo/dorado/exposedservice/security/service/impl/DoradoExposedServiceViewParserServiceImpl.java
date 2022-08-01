package org.xobo.dorado.exposedservice.security.service.impl;

import java.util.*;
import java.util.Map.Entry;
import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.xobo.dorado.exposedservice.security.api.DoradoUrlProvider;
import org.xobo.dorado.exposedservice.security.api.ServiceUrlMappingProvider;
import org.xobo.dorado.exposedservice.security.service.TravelComponentListener;
import org.xobo.dorado.exposedservice.security.util.DoradoViewUtil;
import com.bstek.dorado.config.ExpressionMethodInterceptor;
import com.bstek.dorado.core.el.Expression;
import com.bstek.dorado.data.provider.DataProvider;
import com.bstek.dorado.data.provider.DirectDataProvider;
import com.bstek.dorado.data.resolver.DataResolver;
import com.bstek.dorado.data.resolver.DirectDataResolver;
import com.bstek.dorado.data.type.DataType;
import com.bstek.dorado.data.type.EntityDataType;
import com.bstek.dorado.data.type.property.Mapping;
import com.bstek.dorado.data.type.property.PropertyDef;
import com.bstek.dorado.data.type.property.Reference;
import com.bstek.dorado.util.proxy.MethodInterceptorDispatcher;
import com.bstek.dorado.view.View;
import com.bstek.dorado.view.ViewState;
import com.bstek.dorado.view.config.ViewNotFoundException;
import com.bstek.dorado.view.manager.ViewConfig;
import com.bstek.dorado.view.manager.ViewConfigManager;
import com.bstek.dorado.view.widget.Component;
import com.bstek.dorado.view.widget.SubViewHolder;
import com.bstek.dorado.view.widget.action.AjaxAction;
import com.bstek.dorado.view.widget.action.UpdateAction;
import com.bstek.dorado.view.widget.data.DataSet;
import com.bstek.dorado.web.DoradoContext;
import javassist.util.proxy.MethodHandler;

public class DoradoExposedServiceViewParserServiceImpl implements ServiceUrlMappingProvider {

  private static Logger logger =
      LoggerFactory.getLogger(DoradoExposedServiceViewParserServiceImpl.class);

  private ViewConfigManager viewConfigManager;

  private Collection<DoradoUrlProvider> doradoUrlProviders;

  private static String EL_GET_DATAPROVIDER = "${dorado.getDataProvider(";

  public DoradoExposedServiceViewParserServiceImpl(ViewConfigManager viewConfigManager,
      Collection<DoradoUrlProvider> doradoUrlProviders) {
    this.viewConfigManager = viewConfigManager;
    this.doradoUrlProviders = doradoUrlProviders;
  }

  public void setDoradoUrlProviders(Collection<DoradoUrlProvider> doradoUrlProviders) {
    this.doradoUrlProviders = doradoUrlProviders;
  }

  public Collection<String> loadDoradoUrl() {
    Set<String> doradoUrlSet = new HashSet<String>();
    for (DoradoUrlProvider doradoUrlProvider : doradoUrlProviders) {
      doradoUrlSet.addAll(doradoUrlProvider.getUrls());
    }
    return doradoUrlSet;
  }

  /**
   * 返回dorado servive 对应URL映射关系。 key是dorado service 形如 demoController#hello； value是 dorado URL集合，形如
   * ['aa.aaa.d', 'bb.bbb.d']。
   * 
   * @return dorado service map
   */
  public Map<String, Collection<String>> loadServiceUrlMapping() {
    Map<String, Collection<String>> serviceUrlMapping = new HashMap<String, Collection<String>>();
    Collection<String> doradoUrls = loadDoradoUrl();

    for (String url : doradoUrls) {
      Collection<String> services = null;
      try {
        services = loadDoradoService(url);
      } catch (Exception e) {
        logger.error("load service error", e);
        continue;
      }
      for (String service : services) {
        Collection<String> urlColl = serviceUrlMapping.get(service);
        if (DoradoViewUtil.isEmpty(urlColl)) {
          urlColl = new HashSet<String>();
          serviceUrlMapping.put(service, urlColl);
        }
        urlColl.add(url);
      }
    }
    return serviceUrlMapping;
  }


  public Collection<String> loadDoradoService(String url) {
    if (StringUtils.isEmpty(url)) {
      return Collections.emptyList();
    }

    if (!url.endsWith(".d")) {
      return Collections.emptyList();
    }

    int beginIndex = 0;
    if (url.startsWith("/")) {
      beginIndex = 1;
    }
    String VIEWSTATE_KEY = ViewState.class.getName();
    DoradoContext context = DoradoContext.getCurrent();
    context.setAttribute(VIEWSTATE_KEY, ViewState.rendering);

    String viewName = url.substring(beginIndex, url.length() - 2);

    ViewConfig viewConfig = null;
    try {
      viewConfig = viewConfigManager.getViewConfig(viewName);
    } catch (ViewNotFoundException e) {
      return Collections.emptyList();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    View view = viewConfig.getView();
    if (view == null) {
      return Collections.emptyList();
    }

    final Set<String> doradoServiceSet = new HashSet<String>();

    DoradoViewUtil.travelComponent(view, new TravelComponentListener() {

      public void travel(Component component) {
        Collection<String> serviceList = loadComponentService(component);
        doradoServiceSet.addAll(serviceList);
      }

    });

    doradoServiceSet.addAll(loadDataTypeService(viewConfig));

    return doradoServiceSet;
  }

  public Collection<String> loadDataTypeService(ViewConfig viewConfig) {
    final Set<String> doradoServiceSet = new HashSet<String>();

    Set<String> dataTypeNames = viewConfig.getPrivateDataTypeNames();

    for (String dataTypeName : dataTypeNames) {
      try {
        DataType dataType = viewConfig.getDataType(dataTypeName);
        if (dataType instanceof EntityDataType) {
          Map<String, PropertyDef> propertyDefMap = ((EntityDataType) dataType).getPropertyDefs();

          for (Entry<String, PropertyDef> entry : propertyDefMap.entrySet()) {
            PropertyDef propertyDef = entry.getValue();

            if (propertyDef instanceof Reference) {
              Reference reference = (Reference) propertyDef;
              DataProvider dataProvider = reference.getDataProvider();
              if (dataProvider == null) {
                continue;
              }
              String doradoService = dataProvider.getId();
              if (!StringUtils.isEmpty(doradoService) || doradoService.indexOf('#') >= 0) {
                doradoServiceSet.add(doradoService);
              }
            }

            Mapping mapping = propertyDef.getMapping();
            if (mapping instanceof javassist.util.proxy.ProxyObject) {
              MethodHandler methodHandler =
                  ((javassist.util.proxy.ProxyObject) mapping).getHandler();
              if (methodHandler instanceof MethodInterceptorDispatcher) {
                MethodInterceptor[] methodInterceptors =
                    ((MethodInterceptorDispatcher) methodHandler).getSubMethodInterceptors();
                for (MethodInterceptor methodInterceptor : methodInterceptors) {
                  if (methodInterceptor instanceof ExpressionMethodInterceptor) {
                    Expression mappingExpression = ((ExpressionMethodInterceptor) methodInterceptor)
                        .getExpressionProperties().get("mapValues");
                    String expression = mappingExpression.toString().trim();
                    try {
                      if (expression.startsWith(EL_GET_DATAPROVIDER)) {
                        int fromIndex = EL_GET_DATAPROVIDER.length();
                        char splitChar = expression.charAt(fromIndex);
                        int endIndex = expression.indexOf(splitChar, fromIndex + 1);

                        String doradoSevice = expression.substring(fromIndex + 1, endIndex);
                        doradoServiceSet.add(doradoSevice);
                      }
                    } catch (Exception e) {
                      logger.error("处理expression {} 异常，忽略该记录。", expression, e);
                    }
                  }
                }
              }
            }
          }

        }
      } catch (Exception e) {
        logger.error("处理dataType {} 异常，忽略该记录。", dataTypeName, e);
      }

    }
    return doradoServiceSet;
  }

  public Collection<String> loadComponentService(Component component) {
    String service = null;
    if (component instanceof DataSet) {
      DataProvider dataProvider = ((DataSet) component).getDataProvider();
      if (dataProvider instanceof DirectDataProvider) {
        service = dataProvider.getId();
      }
    } else if (component instanceof UpdateAction) {
      DataResolver dataResolver = ((UpdateAction) component).getDataResolver();
      if (dataResolver instanceof DirectDataResolver) {
        service = dataResolver.getId();
      }
    } else if (component instanceof AjaxAction) {
      service = ((AjaxAction) component).getService();
    } else if (component instanceof SubViewHolder) {
      String subView = ((SubViewHolder) component).getSubView();
      if (!StringUtils.isEmpty(subView)) {
        return loadDoradoService(subView + ".d");
      }
    }

    if (service != null) {
      return Arrays.asList(service);
    }
    return Collections.emptyList();
  }

}
