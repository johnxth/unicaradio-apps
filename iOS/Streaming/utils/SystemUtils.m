//
//  SystemUtils.m
//  Streaming
//
//  Created by Paolo Cortis on 03/11/13.
//
//

#import "SystemUtils.h"

@implementation SystemUtils

+ (BOOL) isIos7
{
	return SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0") && SYSTEM_VERSION_LESS_THAN(@"8.0");
}

+ (BOOL) isIos6
{
	return SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"6.0") && SYSTEM_VERSION_LESS_THAN(@"7.0");
}

@end
