//
//  SongRequest.m
//  Streaming
//
//  Created by Paolo on 20/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SongRequest.h"
#import "../libs/JSONKit/JSONKit.h"

@implementation SongRequest

@synthesize author;
@synthesize title;
@synthesize email;

- (id) init
{
	self = [super init];
    if(self) {
        // Initialization code
    }
    return self;
}

- (NSDictionary *) toJSON
{
	NSMutableDictionary *resultDict = [NSMutableDictionary dictionary];
	[resultDict setObject:author forKey:@"art"];
	[resultDict setObject:email forKey:@"mail"];
	[resultDict setObject:title forKey:@"tit"];

	NSMutableDictionary *appDictionary = [NSMutableDictionary dictionary];
	NSString *appCode = [NSString stringWithFormat:@"%d", IOS_APP_CODE];
	[appDictionary setObject:appCode forKey:@"code"];

	NSString *appVersion = [[[NSBundle mainBundle] infoDictionary] objectForKey:(NSString*)kCFBundleVersionKey];
	[appDictionary setObject:appVersion forKey:@"versionCode"];

	NSString *deviceModel = [[UIDevice currentDevice] model];
	[appDictionary setObject:deviceModel forKey:@"deviceModel"];
	
	NSString *systemVersion = [[UIDevice currentDevice] systemVersion];
	[appDictionary setObject:systemVersion forKey:@"systemVersion"];
	
	[resultDict setObject:appDictionary forKey:@"app"];

	return resultDict;
}

- (NSString *) toJSONString
{
	return [[self toJSON] JSONString];
}

@end
