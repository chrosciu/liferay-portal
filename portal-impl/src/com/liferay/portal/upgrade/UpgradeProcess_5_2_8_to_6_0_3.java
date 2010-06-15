/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.upgrade;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.upgrade.v5_2_8_to_6_0_3.UpgradeSchema;
import com.liferay.portal.upgrade.v6_0_0.UpgradeAsset;
import com.liferay.portal.upgrade.v6_0_0.UpgradeAssetPublisher;
import com.liferay.portal.upgrade.v6_0_0.UpgradeBlogs;
import com.liferay.portal.upgrade.v6_0_0.UpgradeDocumentLibrary;
import com.liferay.portal.upgrade.v6_0_0.UpgradeExpando;
import com.liferay.portal.upgrade.v6_0_0.UpgradePolls;
import com.liferay.portal.upgrade.v6_0_0.UpgradePortletId;
import com.liferay.portal.upgrade.v6_0_0.UpgradeShopping;
import com.liferay.portal.upgrade.v6_0_2.UpgradeNestedPortlets;

/**
 * <a href="UpgradeProcess_5_2_8_to_6_0_3.java.html"><b><i>View Source</i></b>
 * </a>
 *
 * @author Douglas Wong
 */
public class UpgradeProcess_5_2_8_to_6_0_3 extends UpgradeProcess {

	public int getThreshold() {
		return ReleaseInfo.RELEASE_6_0_3_BUILD_NUMBER;
	}

	protected void doUpgrade() throws Exception {
		upgrade(UpgradeSchema.class);
		upgrade(UpgradeAsset.class);
		upgrade(UpgradeAssetPublisher.class);
		upgrade(UpgradeBlogs.class);
		upgrade(UpgradeDocumentLibrary.class);
		upgrade(UpgradeExpando.class);
		upgrade(UpgradePolls.class);
		upgrade(UpgradePortletId.class);
		upgrade(UpgradeShopping.class);

		upgrade(com.liferay.portal.upgrade.v6_0_1.UpgradeDocumentLibrary.class);

		upgrade(com.liferay.portal.upgrade.v6_0_2.UpgradeExpando.class);
		upgrade(UpgradeNestedPortlets.class);
	}

}