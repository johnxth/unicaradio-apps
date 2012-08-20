//
//  CaptchaParser.m
//  Streaming
//
//  Created by Paolo on 19/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "CaptchaParser.h"

@implementation CaptchaParser

+ (NSString *) parse:(NSString *)captcha
{
	if(captcha == nil) {
		return @"";
	}

	NSString *trimmedCaptcha = [captcha stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];

	NSInteger catpchaLength = [trimmedCaptcha length];
	if(catpchaLength == 0) {
		return @"";
	}
	
	if(catpchaLength != 8) {
		return @"";
	}
	
	NSString *op1 = [trimmedCaptcha substringWithRange:NSMakeRange(0, 2)];
	NSString *op2 = [trimmedCaptcha substringWithRange:NSMakeRange(catpchaLength - 2, 2)];
	NSString *operation = [trimmedCaptcha substringWithRange:NSMakeRange(2, 4)];
	if([operation isEqualToString:PLUS]) {
		operation = @"+";
	} else if([operation isEqualToString:MINUS]) {
		operation = @"-";
	} else if([operation isEqualToString:MULT]) {
		operation = @"*";
	} else {
		return @"";
	}
	
	return [NSString stringWithFormat:@"%@ %@ %@ = ...", op1, operation, op2];
}

@end
