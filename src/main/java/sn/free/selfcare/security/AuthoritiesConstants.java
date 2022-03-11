package sn.free.selfcare.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

	public static final String ADMIN = "ROLE_ADMIN";

	public static final String USER = "ROLE_USER";

	public static final String ADMIN_FREE = "ROLE_ADMIN_FREE";

	public static final String ADMIN_CLIENT = "ROLE_ADMIN_CLIENT";

	public static final String BACK_OFFICE = "ROLE_BACK_OFFICE";

	public static final String SUPPORT = "ROLE_SUPPORT";

	public static final String CALL_CENTER = "ROLE_CALL_CENTER";

	public static final String TEAM_LEAD = "ROLE_TEAM_LEAD";

	public static final String KAM = "ROLE_KAM";

	public static final String FINANCE_CLIENT = "ROLE_FINANCE_CLIENT";

	public static final String ANONYMOUS = "ROLE_ANONYMOUS";

	private AuthoritiesConstants() {
	}
}
