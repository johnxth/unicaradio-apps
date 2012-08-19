//
//  CaptchaParser.h
//  Streaming
//
//  Created by Paolo on 19/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

#define PLUS  @"0001"
#define MINUS @"0010"
#define MULT  @"0100"

@interface CaptchaParser : NSObject
{
	
}

+ (NSString *) parse: (NSString *)captcha;

@end
