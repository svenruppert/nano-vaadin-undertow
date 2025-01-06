/*
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
