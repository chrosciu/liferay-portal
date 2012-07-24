/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.template;

import com.liferay.portal.deploy.sandbox.SandboxHandler;
import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManager;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.TemplateResourceLoader;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tina Tian
 */
public abstract class AbstractTemplate implements Template {

	public AbstractTemplate(
		TemplateResource templateResource,
		TemplateResource errorTemplateResource,
		TemplateContextHelper templateContextHelper, String templateManagerName,
		long interval) {

		if (templateResource == null) {
			throw new IllegalArgumentException("Template resource is null");
		}

		if (templateContextHelper == null) {
			throw new IllegalArgumentException(
				"Template context helper is null");
		}

		if (templateManagerName == null) {
			throw new IllegalArgumentException("Template manager name is null");
		}

		this.templateResource = templateResource;
		this.errorTemplateResource = errorTemplateResource;

		_templateContextHelper = templateContextHelper;

		if (interval != 0) {
			_cacheTemplateResource(templateManagerName);
		}
	}

	public void prepare(HttpServletRequest request) {
		_templateContextHelper.prepare(this, request);
	}

	public boolean processTemplate(Writer writer) throws TemplateException {
		if (errorTemplateResource == null) {
			try {
				processTemplate(templateResource, writer);

				return true;
			}
			catch (Exception e) {
				throw new TemplateException(
					"Unable to process template " +
						templateResource.getTemplateId(),
					e);
			}
		}

		try {
			UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

			processTemplate(templateResource, unsyncStringWriter);

			StringBundler sb = unsyncStringWriter.getStringBundler();

			sb.writeTo(writer);

			return true;
		}
		catch (Exception e) {
			handleException(e, writer);

			return false;
		}
	}

	protected String getTemplateResourceUUID(
		TemplateResource templateResource) {

		StringBundler sb = new StringBundler(5);

		sb.append(TemplateResource.TEMPLATE_RESOURCE_UUID_PREFIX);
		sb.append(StringPool.POUND);
		sb.append(templateResource.getTemplateId());
		sb.append(StringPool.POUND);

		if (templateResource instanceof StringTemplateResource) {
			StringTemplateResource stringTemplateResource =
				(StringTemplateResource)templateResource;

			sb.append(stringTemplateResource.getContent());
		}
		else {
			sb.append(templateResource.getLastModified());
		}

		return sb.toString();
	}

	protected abstract void handleException(Exception exception, Writer writer)
		throws TemplateException;

	protected abstract void processTemplate(
			TemplateResource templateResource, Writer writer)
		throws Exception;

	protected TemplateResource errorTemplateResource;
	protected TemplateResource templateResource;

	private void _cacheTemplateResource(String templateManagerName) {
		String templateId = templateResource.getTemplateId();

		if (templateManagerName.equals(TemplateManager.VELOCITY) &&
			templateId.contains(SandboxHandler.SANDBOX_MARKER)) {

			return;
		}

		if (!(templateResource instanceof CacheTemplateResource) &&
			!(templateResource instanceof StringTemplateResource)) {

			templateResource = new CacheTemplateResource(templateResource);
		}

		String cacheName = TemplateResourceLoader.class.getName();

		cacheName = cacheName.concat(StringPool.POUND).concat(
			templateManagerName);

		PortalCache portalCache = MultiVMPoolUtil.getCache(cacheName);

		Object object = portalCache.get(templateResource.getTemplateId());

		if ((object == null) || !templateResource.equals(object)) {
			portalCache.put(templateResource.getTemplateId(), templateResource);
		}

		if (errorTemplateResource == null) {
			return;
		}

		String errorTemplateId = errorTemplateResource.getTemplateId();

		if (templateManagerName.equals(TemplateManager.VELOCITY) &&
			errorTemplateId.contains(SandboxHandler.SANDBOX_MARKER)) {

			return;
		}

		if (!(errorTemplateResource instanceof CacheTemplateResource) &&
			!(errorTemplateResource instanceof StringTemplateResource)) {

			errorTemplateResource = new CacheTemplateResource(
				errorTemplateResource);
		}

		object = portalCache.get(errorTemplateResource.getTemplateId());

		if ((object == null) || !errorTemplateResource.equals(object)) {
			portalCache.put(
				errorTemplateResource.getTemplateId(), errorTemplateResource);
		}
	}

	private TemplateContextHelper _templateContextHelper;

}