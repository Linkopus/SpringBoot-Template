package com.linkopus.ms.utils.ressources.enums;

import java.util.ArrayList;
import java.util.List;

public enum UserRole {
	NO_ROLE("NO_ROLE"), TALENT("TALENT"), MANAGER("MANAGER"), USER("USER"), SUPER_ADMIN("SUPER_ADMIN"), ADMIN("ADMIN");

	private final String roleName;

	UserRole(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}

	public enum CompanyRole {
		MANAGER(UserRole.MANAGER.getRoleName()), USER(UserRole.USER.getRoleName());

		private final String roleName;

		CompanyRole(String roleName) {
			this.roleName = roleName;
		}

		public String getRoleName() {
			return roleName;
		}
	}

	public enum SystemAdminRole {
		SUPER_ADMIN(UserRole.SUPER_ADMIN.getRoleName()), ADMIN(UserRole.ADMIN.getRoleName());

		private final String roleName;

		SystemAdminRole(String roleName) {
			this.roleName = roleName;
		}

		public String getRoleName() {
			return roleName;
		}
	}

	public static List<String> getUserRoleList() {
		List<String> userRoleList = new ArrayList<>();

		for (UserRole role : UserRole.values()) {
			userRoleList.add(role.getRoleName());
		}

		for (CompanyRole role : CompanyRole.values()) {
			userRoleList.add(role.getRoleName());
		}

		for (SystemAdminRole role : SystemAdminRole.values()) {
			userRoleList.add(role.getRoleName());
		}

		return userRoleList;
	}
}