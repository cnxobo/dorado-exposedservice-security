package org.xobo.dorado.exposedservice.security.util;

import java.util.Collection;
import java.util.List;

import org.xobo.dorado.exposedservice.security.service.TravelComponentListener;

import com.bstek.dorado.view.widget.Component;
import com.bstek.dorado.view.widget.Container;
import com.bstek.dorado.view.widget.Control;
import com.bstek.dorado.view.widget.base.SplitPanel;
import com.bstek.dorado.view.widget.base.tab.ControlTab;
import com.bstek.dorado.view.widget.base.tab.Tab;
import com.bstek.dorado.view.widget.base.tab.TabControl;

public class DoradoViewUtil {
  public static void travelComponent(Component component, TravelComponentListener listener) {
    if (component == null) {
      return;
    }
    if (listener != null) {
      listener.travel(component);
    }
    if (component instanceof Container) {
      travelContainerComponent((Container) component, listener);
    } else if (component instanceof SplitPanel) {
      SplitPanel splitPanel = (SplitPanel) component;
      Control sideControl = splitPanel.getSideControl();
      Control mainControl = splitPanel.getMainControl();
      travelComponent(sideControl, listener);
      travelComponent(mainControl, listener);
    } else if (component instanceof TabControl) {
      List<Tab> tabList = ((TabControl) component).getTabs();
      if (isNotEmpty(tabList)) {
        for (Tab tab : tabList) {
          if (tab instanceof ControlTab) {
            travelComponent(((ControlTab) tab).getControl(), listener);
          }
        }
      }
    }
  }

  public static void travelContainerComponent(Container container,
      TravelComponentListener listener) {
    List<Component> compoments = container.getChildren();
    for (Component component : compoments) {
      travelComponent(component, listener);
    }

  }

  public static <T> boolean isNotEmpty(Collection<T> collection) {
    return collection != null && !collection.isEmpty();
  }

  public static <T> boolean isEmpty(Collection<T> collection) {
    return !isNotEmpty(collection);
  }


}
