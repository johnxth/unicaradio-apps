//
//  Error.h
//  Streaming
//
//  Created by Paolo on 21/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef Streaming_Error_h
#define Streaming_Error_h

typedef enum {
	NO_ERROR = 0x000,
	
	INTERNAL_GENERIC_ERROR = 1, INTERNAL_DOWNLOAD_ERROR = 2,
	
	DB_GENERIC_ERROR = 0x010, DB_CANNOT_OPEN = 0x011, DB_CANNOT_CREATE = 0x012, DB_CANNOT_READ = 0x013, DB_CANNOT_WRITE = 0x014,
	
	CAPTCHA_NOT_FOUND = 0x100,
	
	MAIL_ERROR_SENDING = 0x200,
	
	GENERIC_ERROR = 0x900
} Error;

#endif
