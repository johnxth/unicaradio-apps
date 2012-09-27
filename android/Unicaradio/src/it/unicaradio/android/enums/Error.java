package it.unicaradio.android.enums;

public enum Error {
	NO_ERROR,

	INTERNAL_GENERIC_ERROR, INTERNAL_DOWNLOAD_ERROR,

	DB_GENERIC_ERROR, DB_CANNOT_OPEN, DB_CANNOT_CREATE, DB_CANNOT_READ, DB_CANNOT_WRITE,

	CAPTCHA_NOT_FOUND,

	MAIL_ERROR_SENDING,

	OPERATION_FORBIDDEN,

	GENERIC_ERROR;

	public static Error fromInteger(int errorCode)
	{
		switch(errorCode) {
			case 0x000:
				return NO_ERROR;
			case 1:
				return INTERNAL_GENERIC_ERROR;
			case 2:
				return INTERNAL_DOWNLOAD_ERROR;
			case 0x010:
				return DB_GENERIC_ERROR;
			case 0x011:
				return DB_CANNOT_OPEN;
			case 0x012:
				return DB_CANNOT_CREATE;
			case 0x013:
				return DB_CANNOT_READ;
			case 0x014:
				return DB_CANNOT_WRITE;
			case 0x100:
				return CAPTCHA_NOT_FOUND;
			case 0x200:
				return MAIL_ERROR_SENDING;
			case 0x300:
				return OPERATION_FORBIDDEN;
			case 0x900:
				return GENERIC_ERROR;

			default:
				return INTERNAL_GENERIC_ERROR;
		}
	}

	public static int toInteger(Error error)
	{
		switch(error) {
			case NO_ERROR:
				return 0x000;
			case INTERNAL_GENERIC_ERROR:
				return 1;
			case INTERNAL_DOWNLOAD_ERROR:
				return 2;
			case DB_GENERIC_ERROR:
				return 0x010;
			case DB_CANNOT_OPEN:
				return 0x011;
			case DB_CANNOT_CREATE:
				return 0x012;
			case DB_CANNOT_READ:
				return 0x013;
			case DB_CANNOT_WRITE:
				return 0x014;
			case CAPTCHA_NOT_FOUND:
				return 0x100;
			case MAIL_ERROR_SENDING:
				return 0x200;
			case OPERATION_FORBIDDEN:
				return 0x300;
			case GENERIC_ERROR:
				return 0x900;
			default:
				return 1;
		}
	}
}
