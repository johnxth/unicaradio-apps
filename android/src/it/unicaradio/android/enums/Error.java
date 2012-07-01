package it.unicaradio.android.enums;

public enum Error {
	OK, GENERIC_ERROR, DOWNLOAD_ERROR;

	public static Error fromInteger(int errorCode)
	{
		switch(errorCode) {
			case 0:
				return OK;
			case 1:
				return GENERIC_ERROR;
			case 2:
				return DOWNLOAD_ERROR;
			default:
				return GENERIC_ERROR;
		}
	}

	public static int toInteger(Error error)
	{
		switch(error) {
			case OK:
				return 0;
			case GENERIC_ERROR:
				return 1;
			case DOWNLOAD_ERROR:
				return 2;
			default:
				return 1;
		}
	}
}
