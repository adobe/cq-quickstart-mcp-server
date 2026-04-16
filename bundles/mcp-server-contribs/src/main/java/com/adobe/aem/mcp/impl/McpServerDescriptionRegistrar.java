/*
 * Copyright 2026 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.aem.mcp.impl;

import org.apache.sling.mcp.server.spi.McpServerDescription;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.license.ProductInfo;
import com.adobe.granite.license.ProductInfoService;

@Component
public class McpServerDescriptionRegistrar{
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Reference
    private ProductInfoService productInfoService;

    private ServiceRegistration<McpServerDescription> registration;

    @Activate
    public void activate(ComponentContext ctx) {
        
        ProductInfo[] productInfos = productInfoService.getInfos();
        if ( productInfos == null || productInfos.length == 0 ) {
            logger.info("No product info found, skipping MCP server description registration");
            return;
        }
        
        if ( productInfos.length > 1 ) {
            logger.warn("Multiple product infos found, using the first one for MCP server description registration");
        }
        
        ProductInfo productInfo = productInfos[0];
        
        registration = ctx.getBundleContext().registerService(McpServerDescription.class, 
            new McpServerDescriptionImpl(productInfo.getName(), productInfo.getVersion().toString()), null);
    }
    
    @Deactivate
    public void deactivate() {
        if ( registration != null ) {
            registration.unregister();
        }
    }
    
    record McpServerDescriptionImpl(String name, String version) implements McpServerDescription {
        
    }

}
