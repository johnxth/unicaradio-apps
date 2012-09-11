//
//  DownloadScheduleOperation.m
//  Streaming
//
//  Created by Paolo on 21/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "DownloadScheduleOperation.h"
#import "../utils/NetworkUtils.h"
#import "../enums/Error.h"
#import "../libs/JSONKit/JSONKit.h"

@implementation DownloadScheduleOperation

- (id) init
{
	if(self = [super init]) {
	}
	
	return self;
}

- (void) main
{
	NSData *result = [NetworkUtils httpGet:[NSURL URLWithString:SCHEDULE_URL]];

	if(result == nil) {
		NSMutableDictionary *errorDict = [NSMutableDictionary dictionary];
		[errorDict setObject:[NSNumber numberWithInt:INTERNAL_DOWNLOAD_ERROR] forKey:@"errorCode"];
		[[NSNotificationCenter defaultCenter] postNotificationName:@"GetSchedule" object:errorDict];
		return;
	}

	[[NSNotificationCenter defaultCenter] postNotificationName:@"GetSchedule" object:result];
}

@end
