package com.svenruppert.vaadin.nano;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultipleResourceManager implements ResourceManager {

  private List<ResourceManager> resourceManagerList = new ArrayList<>();

  public MultipleResourceManager(ResourceManager... resourceManagers) {
    Collections.addAll(this.resourceManagerList, resourceManagers);
  }

  @Override
  public Resource getResource(String path) throws IOException {
    for (ResourceManager resourceManager : resourceManagerList) {
      Resource resource = resourceManager.getResource(path);
      if (resource != null) {
        return resource;
      }
    }
    throw new IOException("Resource not found: " + path);
  }

  @Override
  public boolean isResourceChangeListenerSupported() {
    return false;
  }

  @Override
  public void registerResourceChangeListener(ResourceChangeListener listener) {
    throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
  }

  @Override
  public void removeResourceChangeListener(ResourceChangeListener listener) {
    throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
  }


  @Override
  public void close() throws IOException {
    for (ResourceManager resourceManager : resourceManagerList) {
      resourceManager.close();
    }
  }
}
