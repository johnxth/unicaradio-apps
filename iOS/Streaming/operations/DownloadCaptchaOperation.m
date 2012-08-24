//
//  DownloadCaptchaOperation.m
//  Streaming
//
//  Created by Paolo on 18/08/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "DownloadCaptchaOperation.h"
#import "../JSONKit/JSONKit.h"
#import "../utils/NetworkUtils.h"
#import "../enums/Error.h"

@implementation DownloadCaptchaOperation

- (id) init
{
	if(self = [super init]) {
	}

	return self;
}

- (void) main
{
	NSDictionary *requestDictionary = [NSDictionary dictionaryWithObjectsAndKeys: @"getCaptcha", @"method", nil];

    NSString *jsonRequest = [requestDictionary JSONString];
	NSURL *url = [NSURL URLWithString:WEB_SERVICE];
	NSData *result = [NetworkUtils httpPost:url postData:jsonRequest contentType:@"application/json"];
	if(result == nil) {
		NSMutableDictionary *errorDict = [NSMutableDictionary dictionary];
		[errorDict setObject:[NSNumber numberWithInt:INTERNAL_DOWNLOAD_ERROR] forKey:@"errorCode"];
		[[NSNotificationCenter defaultCenter] postNotificationName:@"GetCaptcha" object:errorDict];
		return;
	}

	NSDictionary *resultsDictionary = [result objectFromJSONData];

	[[NSNotificationCenter defaultCenter] postNotificationName:@"GetCaptcha" object:resultsDictionary];
}

@end
